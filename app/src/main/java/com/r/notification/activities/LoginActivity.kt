package com.r.notification.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.r.notification.API
import com.r.notification.R
import com.r.notification.YDelegate
import com.r.notification.apiCallBack.LoginResponse
import com.r.notification.databinding.ActivityLoginBinding
import com.r.notification.utilities.SessionManager
import com.r.notification.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    var binding: ActivityLoginBinding? = null
    private val PERMISSION_ID = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        init()
    }

    private fun init() {

        SessionManager().setPrefData(
            YDelegate.LOCATION_GATHERING_INTERVAL_TIME,
            "5",
            this@LoginActivity
        )

        SessionManager().setPrefData(
            YDelegate.NOTIFICATION_INTERVAL_TIME,
            "5",
            this@LoginActivity
        )


        if(isLocationEnabled()){
            requestPermissions()
        }

        if (SessionManager().getPrefData(
                YDelegate.USER_MOBILE_NUMBER,
                this@LoginActivity
            ) != null
        ) {
            binding!!.etMobile.setText(
                SessionManager().getPrefData(
                    YDelegate.USER_MOBILE_NUMBER,
                    this@LoginActivity
                )
            )
        }

        if (SessionManager().getPrefData(
                YDelegate.USER_PASSWORD,
                this@LoginActivity
            ) != null
        ) {
            binding!!.etPassword.setText(
                SessionManager().getPrefData(
                    YDelegate.USER_PASSWORD,
                    this@LoginActivity
                )
            )
        }


        binding!!.tvSignIn.setOnClickListener {
            val checkValidation = validationCheck(
                binding!!.etMobile.text.toString(),
                binding!!.etPassword.text.toString()
            )

            if (checkValidation.isEmpty()) {
                loginApi(binding!!.etMobile.text.toString(), binding!!.etPassword.text.toString())
            } else {
                Toast.makeText(this, checkValidation, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validationCheck(mobileNumber: String, password: String): String {
        return when {
            mobileNumber.isEmpty() -> {
                getString(R.string.enter_mobile_number)
            }
            password.isEmpty() -> {
                getString(R.string.enter_password)
            }
            else -> {
                ""
            }
        }
    }

    private fun loginApi(email: String, password: String) {
        if (Utils.isNetworkAvailable(this)) {
            binding!!.clProgress.visibility = View.VISIBLE
            YDelegate.getApiClient()
            val api = YDelegate.retrofit?.create(API::class.java)

            val apiCallBack: Call<LoginResponse>? = api?.loginUser(email, password)

            apiCallBack?.enqueue(object : Callback<LoginResponse> {

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    binding!!.clProgress.visibility = View.GONE
                    if (t.localizedMessage != null) {
                        Utils.toast(
                            this@LoginActivity,
                            t.localizedMessage
                        )
                    }
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    binding!!.clProgress.visibility = View.GONE
                    try {
                        if (response.code() == 200) {
                            if (response.body()!!.getStatus() == YDelegate.SUCCESS) {
                                SessionManager().setPrefData(
                                    YDelegate.USER_TOKEN,
                                    response.body()!!.getData()!!.getApiToken(),
                                    this@LoginActivity
                                )
                                SessionManager().setPrefIntData(
                                    YDelegate.USER_ID,
                                    response.body()!!.getData()!!.getId(),
                                    this@LoginActivity
                                )
                                SessionManager().setPrefData(
                                    YDelegate.FIRST_NAME,
                                    response.body()!!.getData()!!.getFirstName(),
                                    this@LoginActivity
                                )
                                if (response.body()!!.getData()!!.getLastName() != null) {
                                    SessionManager().setPrefData(
                                        YDelegate.LAST_NAME,
                                        response.body()!!.getData()!!.getLastName().toString(),
                                        this@LoginActivity
                                    )
                                }

                                SessionManager().setBooleanPrefData(
                                    YDelegate.IS_LOGIN,
                                    true,
                                    this@LoginActivity
                                )

                                if (binding!!.cbRememberMe.isChecked) {
                                    SessionManager().setPrefData(
                                        YDelegate.USER_MOBILE_NUMBER,
                                        email,
                                        this@LoginActivity
                                    )
                                    SessionManager().setPrefData(
                                        YDelegate.USER_PASSWORD,
                                        password,
                                        this@LoginActivity
                                    )
                                }

                                openMainActivity()
                            } else {
                                Utils.toast(
                                    this@LoginActivity,
                                    response.body()!!.getMessage()!!
                                )
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Utils.toast(
                                    this@LoginActivity,
                                    jObjError.getString("errorMessage")
                                )
                            } catch (e: Exception) {
                                Log.d("TAG", "onResponse: " + e.localizedMessage)
                                Utils.toast(
                                    this@LoginActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        if (e.localizedMessage != null) {
                            Utils.toast(
                                this@LoginActivity,
                                e.localizedMessage
                            )
                        }
                    }
                }
            })
        } else {
            Utils.toast(
                this,
                getString(R.string.no_internet_connection)
            )
        }
    }

    private fun openMainActivity() {
        startActivity(
            Intent(
                this,
                AlarmActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(isFirst: Boolean): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                getLastLocation()
            }
        }
    }
}