package com.r.notification.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.r.notification.API
import com.r.notification.MyApp.Companion.CHANNEL_1_ID
import com.r.notification.R
import com.r.notification.YDelegate
import com.r.notification.apiCallBack.GeoLocationGatheringTimeResponse
import com.r.notification.apiCallBack.NotificationIntervalTimeResponse
import com.r.notification.apiCallBack.StoreGEOLocationResponse
import com.r.notification.database.DatabaseHelper
import com.r.notification.databinding.ActivityAlarmBinding
import com.r.notification.utilities.SessionManager
import com.r.notification.utilities.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.invoke.ConstantCallSite
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.content.res.AssetFileDescriptor
import java.text.ParseException


class AlarmActivity : AppCompatActivity(), View.OnClickListener {

    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION_ID = 123

    var timer: CountDownTimer? = null
    var timerLocation: CountDownTimer? = null

    var notificationInterval: Long = 0
    var storeTimer: Long = 300000

    var userName: String = ""
    var userId: Int = 0
    var userToken: String = ""
    var startDateTime: String = ""

    var binding: ActivityAlarmBinding? = null
    var navigationView: NavigationView? = null
    var clAssignSitesAndDuty: LinearLayout? = null
    var clSignOut: LinearLayout? = null
    var tvUserName: TextView? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private val PERMISSION_ID = 42

    private var dbHelper = DatabaseHelper(this)

    private var notificationManager: NotificationManagerCompat? = null
    var locationManager: LocationManager? = null
    var mediaPlayer: MediaPlayer? = null
    var media = MediaPlayer()
    var timerNotification: CountDownTimer? = null
    var startTime = ""
    var endTime = ""

    companion object {
        var timerStoretap: CountDownTimer? = null
        private var jsonNotificationTapArray: JsonArray = JsonArray()
        private var jsonLoctionArray: JsonArray = JsonArray()
        private var id: Int = 0
    }

    /*private var mainHandler: Handler? = null
    private val updateTextTask = object : Runnable {
        override fun run() {
            Log.d("TimerRun", "run: Timer")
            mainHandler!!.postDelayed(this, 1 * (60*1000))
        }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        /* mainHandler = Handler(Looper.getMainLooper())
         mainHandler!!.post(updateTextTask)*/
        notificationManager = NotificationManagerCompat.from(this);
        userToken = SessionManager().getPrefData(YDelegate.USER_TOKEN, this@AlarmActivity)!!
        val firstName = SessionManager().getPrefData(YDelegate.FIRST_NAME, this@AlarmActivity)!!
        var lastName: String =
            if (SessionManager().getPrefData(YDelegate.LAST_NAME, this@AlarmActivity) != null) {
                " " + SessionManager().getPrefData(YDelegate.LAST_NAME, this@AlarmActivity)!!
            } else {
                ""
            }
        userName = firstName + lastName
        binding!!.includeMain.tvUserName.text = userName
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding!!.drawerLayout,
            binding!!.includeMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        binding!!.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding!!.includeMain.toolbar.setNavigationIcon(R.drawable.ic_menu)

        navigationView = findViewById(R.id.nav_view)
        clAssignSitesAndDuty = binding!!.navView.findViewById(R.id.ll_assign_site_and_duty)
        clSignOut = binding!!.navView.findViewById(R.id.ll_sign_out)
        binding!!.navHeader.tvUserName.text = getString(R.string.hi) +
                SessionManager().getPrefData(YDelegate.FIRST_NAME, this@AlarmActivity)!!

        clAssignSitesAndDuty!!.setOnClickListener(this)
        clSignOut!!.setOnClickListener(this)


        userToken = SessionManager().getPrefData(YDelegate.USER_TOKEN, this@AlarmActivity)!!
        userId = SessionManager().getPrefIntData(YDelegate.USER_ID, this@AlarmActivity)!!
        if (SessionManager().getPrefData(
                YDelegate.STORE_NOTIFICATION_LAST_TAP,
                this@AlarmActivity
            ) != null && SessionManager().getPrefData(
                YDelegate.STORE_NOTIFICATION_LAST_TAP,
                this@AlarmActivity
            ).toString() != ""
        ) {
            binding!!.includeMain.tvNotificationTest.text =
                SessionManager().getPrefData(
                    YDelegate.STORE_NOTIFICATION_LAST_TAP,
                    this@AlarmActivity
                )
        }

        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("time")) {
                val msg = extras.get("time")
                val selectSDate = extras.get("stime")
                Log.d("intentMsg", "onCreate: " + msg)
                Log.d("selectSDate", "onCreate: " + selectSDate)
                val dateFormat: DateFormat = SimpleDateFormat("hh:mm a")
                binding!!.includeMain.tvNotificationTest.text =
                    dateFormat.format(Calendar.getInstance().time)
                SessionManager().setPrefData(
                    YDelegate.STORE_NOTIFICATION_LAST_TAP,
                    dateFormat.format(Calendar.getInstance().time), this@AlarmActivity
                )

                val tapDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh-mm-ss")
                val tapDate = tapDateFormat.format(Calendar.getInstance().time)

                Log.d("dateD", "storeUserNotificationTap: $tapDate")
                Log.d("dateD", "storeUserNotificationTap: " + selectSDate.toString())
                if (mediaPlayer != null) {
                    mediaPlayer!!.release()
                    // mediaPlayer.stop()
                    mediaPlayer = MediaPlayer()
                    Log.d("onCreatestop", "onCreate: stop")
                }
                val json = SessionManager().getPrefData(
                    YDelegate.STORE_NOTIFICATION_JSON,
                    this@AlarmActivity
                )
                if (!json.equals("")) {
                    jsonNotificationTapArray = JsonParser().parse(json).asJsonArray
                } else {
                    jsonNotificationTapArray = JsonArray()
                }

                if (SessionManager().getPrefIntData(
                        YDelegate.NOTIFICATION_ID,
                        this@AlarmActivity
                    ) != null
                ) {
                    id = SessionManager().getPrefIntData(
                        YDelegate.NOTIFICATION_ID,
                        this@AlarmActivity
                    )!! + 1
                } else {
                    id += 1
                }
                Log.d("onCreate:ID", "onCreate: ID = $id")
                SessionManager().setPrefIntData(YDelegate.NOTIFICATION_ID, id, this@AlarmActivity)
                var jsonObject: JsonObject = JsonObject()
                jsonObject.addProperty("notification_id", id)
                jsonObject.addProperty("display_datetime", selectSDate.toString())
                jsonObject.addProperty("tap_datetime", tapDate)
                jsonNotificationTapArray.add(jsonObject)
                SessionManager().setPrefData(
                    YDelegate.STORE_NOTIFICATION_JSON,
                    jsonNotificationTapArray.toString(),
                    this@AlarmActivity
                )
                /* storeUserNotificationTap(
                     userToken,
                     userId,
                     selectSDate.toString()
                 )*/

                media = MediaPlayer()
                val afd: AssetFileDescriptor = this.assets.openFd("thankyou.mp3")
                media.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                media.prepare()
                media.start()
            }
        }

        requestPermissions()

        /* getNotificationIntervalTime(userToken, userId)
         getAssignedSitesDutyTime(userToken, userId)
         getGeoLocationGatheringTime(userToken, userId)
 //        createNotificationChannel()
         getDifference()*/
        getAssignedSitesDutyTime(userToken, userId)

        binding!!.includeMain.tvSignOut.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@AlarmActivity)
            builder.setMessage(getString(R.string.confirmation_msg_sign_out))
            //performing positive action
            builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
                dialogInterface.dismiss()
                SessionManager().setPrefData(YDelegate.USER_TOKEN, "", this@AlarmActivity)
                SessionManager().setPrefData(YDelegate.FIRST_NAME, "", this@AlarmActivity)
                SessionManager().setPrefData(YDelegate.LAST_NAME, "", this@AlarmActivity)
                SessionManager().setPrefIntData(YDelegate.USER_ID, 0, this@AlarmActivity)
                SessionManager().setBooleanPrefData(YDelegate.IS_LOGIN, false, this@AlarmActivity)
                SessionManager().setPrefData(
                    YDelegate.NOTIFICATION_INTERVAL_TIME,
                    "",
                    this@AlarmActivity
                )
                SessionManager().setPrefData(
                    YDelegate.LOCATION_GATHERING_INTERVAL_TIME,
                    "",
                    this@AlarmActivity
                )
                openActivity()
            }
            //performing cancel action
            builder.setNeutralButton(getString(R.string.no)) { dialogInterface, which ->
                dialogInterface.dismiss()
            }

            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.orange))
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setTextColor(resources.getColor(R.color.orange))
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLocationDifference()
    }

    private fun openActivity() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    /* private fun createNotificationChannel() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val channel = NotificationChannel(
                 CHANNEL_ID,
                 CHANNEL_NAME,
                 NotificationManager.IMPORTANCE_DEFAULT
             ).apply {
                 lightColor = Color.GREEN
                 enableLights(true)
             }

             val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
             manager.createNotificationChannel(channel)
         }
     }*/

    private fun sendNotification() {
        try {
            val intent = Intent(this@AlarmActivity, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            Log.d("selectDate", "sendNotification: $startDateTime")
            intent.putExtra("time", Calendar.getInstance().time)
            intent.putExtra("stime", startDateTime)
            intent.action = Calendar.getInstance().time.toString()
            val requestID = System.currentTimeMillis().toInt()

            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val title: String
            val description: String
            if (SessionManager().getPrefData(
                    YDelegate.NOTIFICATION_TITLE,
                    this@AlarmActivity
                )!!.isNotEmpty()
            ) {
                title = SessionManager().getPrefData(
                    YDelegate.NOTIFICATION_TITLE,
                    this@AlarmActivity
                ).toString()
            } else {
                title = "I am here"
            }
            if (SessionManager().getPrefData(
                    YDelegate.NOTIFICATION_DESCRIPTION,
                    this@AlarmActivity
                )!!.isNotEmpty()
            ) {
                description = SessionManager().getPrefData(
                    YDelegate.NOTIFICATION_DESCRIPTION,
                    this@AlarmActivity
                ).toString()
            } else {
                description = sdf.format(Date())
            }
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                //.setSound(uri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
            notificationManager!!.notify(NOTIFICATION_ID, notification)


            binding!!.includeMain.gifBell.visibility = View.VISIBLE

            mediaPlayer = MediaPlayer()
            val afd: AssetFileDescriptor = this.assets.openFd("alarmsound.mp3")
            mediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            timerNotification = object : CountDownTimer(6000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.d("TickNotification", "onTick:Timer " + millisUntilFinished / 1000)
                }

                override fun onFinish() {
                    Log.d("TickNotification", "onFinish: ")
                    timerNotification!!.cancel()
                    binding!!.includeMain.gifBell.visibility = View.GONE
                    mediaPlayer!!.stop()
                }
            }
            (timerNotification as CountDownTimer).start()
        } catch (e: Exception) {
            Log.d("TAG", "sendNotification: " + e.localizedMessage)
        }

    }

    private fun getDifference() {
        val time = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        var minute = time.get(Calendar.MINUTE)

        var intervalMinute = 0

        if (SessionManager().getPrefData(
                YDelegate.NOTIFICATION_INTERVAL_TIME,
                this@AlarmActivity
            )!!.isNotEmpty()
        ) {
            intervalMinute = SessionManager().getPrefData(
                YDelegate.NOTIFICATION_INTERVAL_TIME,
                this@AlarmActivity
            )!!.toInt()
            Log.d("TAG", "getDifference: "+intervalMinute)
            val totalMinute = 60
            var countOfLoop = 0
            val currentMinute = minute
            var startt = 0
            var endt = 0
            var selectDate = 0

            countOfLoop = totalMinute / intervalMinute

            for (i in countOfLoop downTo 1) {
                endt = i * intervalMinute - 1
                startt = i * intervalMinute - intervalMinute
                if (currentMinute in startt..endt) {
                    Log.d("TAG", "getLocationDifference: Sum  = " + i * intervalMinute)
                    Log.d("TAG", "getLocationDifference: End  = $endt")
                    Log.d("TAG", "getLocationDifference: Start  = $startt")
                    Log.d("TAG", "getLocationDifference: Index = $i")
                    Log.d("TAG", "getLocationDifference: Your next time is = :" + (endt + 1))
                    selectDate = startt
                    minute = (endt + 1)
                }
            }

            var hour = time.get(Calendar.HOUR_OF_DAY)

            if (minute == 0) {
                hour += 1
            }

            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            calendar[Calendar.SECOND] = 0

            Log.d("TAG", "onCreate: time:" + time.time.time)
            Log.d("TAG", "onCreate: calendar.time:" + calendar.time.time)
            Log.d("TAG", "onCreate: calendar.time:" + time.get(Calendar.HOUR_OF_DAY))
            Log.d("TAG", "onCreate: calendar.time:" + time.get(Calendar.MINUTE))
            Log.d("TAG", "onCreate: calendar.time:" + time.get(Calendar.SECOND))
            val calculation = calendar.time.time - time.time.time
            Log.d("TAG", "onCreate: calculation:" + calculation / 1000)
            notificationInterval = calculation

            val dateFormat: DateFormat = SimpleDateFormat("hh:mm a")
            binding!!.includeMain.tvNextNotification.text =
                dateFormat.format(calendar.time.time)

            val dateStartFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh-mm-ss")
            calendar[Calendar.MINUTE] = selectDate
            startDateTime = dateStartFormat.format(calendar.time)

            timer = object : CountDownTimer(notificationInterval, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.d("Tick", "onTick:Timer " + millisUntilFinished / 1000)
                }

                override fun onFinish() {
                    Log.d("Tick", "onFinish: ")
                    timer!!.cancel()
                    getDifference()
                    sendNotification()
                }
            }

            timer!!.start()
            if (timerStoretap != null) {
                Log.d("timeronTick", "onTick: Store = ")
                timerStoretap!!.onTick(storeTimer)
            } else {
                timerStoretap = object : CountDownTimer(storeTimer, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        storeTimer = millisUntilFinished
                        Log.d("timeronTick", "onTick: Store = " + millisUntilFinished / 1000)
                    }

                    override fun onFinish() {
                        Log.d("timeronTick", "onTick: ")
                        timerStoretap!!.start()
                        if (jsonNotificationTapArray != null && jsonNotificationTapArray.size() > 0) {
                            storeUserNotificationTap(
                                userToken,
                                userId
                            )
                        }
                    }
                }
                timerStoretap!!.start()
            }
        }
    }

    private fun getLocationDifference() {
        val time = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        var minute = time.get(Calendar.MINUTE)

        //Dynamic minutes Logic
        var intervalMinute = 0

        if (SessionManager().getPrefData(
                YDelegate.LOCATION_GATHERING_INTERVAL_TIME,
                this@AlarmActivity
            )!!.isNotEmpty()
        ) {
            intervalMinute = SessionManager().getPrefData(
                YDelegate.LOCATION_GATHERING_INTERVAL_TIME,
                this@AlarmActivity
            )!!.toInt()

            val totalMinute = 60
            var countOfLoop = 0
            val currentMinute = minute
            var startt = 0
            var endt = 0


            countOfLoop = totalMinute / intervalMinute

            for (i in countOfLoop downTo 1) {
                endt = i * intervalMinute - 1
                startt = i * intervalMinute - intervalMinute
                if (currentMinute in startt..endt) {
                    Log.d("TAG", "getLocationDifference: Sum  = " + i * intervalMinute)
                    Log.d("TAG", "getLocationDifference: End  = $endt")
                    Log.d("TAG", "getLocationDifference: Start  = $startt")
                    Log.d("TAG", "getLocationDifference: Index = $i")
                    Log.d("TAG", "getLocationDifference: Your next time is = :" + (endt + 1))
                    minute = (endt + 1)
                }
            }

            var hour = time.get(Calendar.HOUR_OF_DAY)

            if (minute == 0) {
                hour += 1
            }

            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            calendar[Calendar.SECOND] = 0

            Log.d("TAG", "onCreate: time:" + time.time.time)
            Log.d("TAG", "onCreate: calendar.time:" + calendar.time.time)
            Log.d("TAG", "onCreate: calendar.time:" + time.get(Calendar.HOUR_OF_DAY))
            Log.d("TAG", "onCreate: calendar.time:" + time.get(Calendar.MINUTE))
            Log.d("TAG", "onCreate: calendar.time:" + time.get(Calendar.SECOND))
            Log.d("TAG", "onCreate: calendar.time:" + startDateTime)
            val calculation = calendar.time.time - time.time.time
            Log.d("TAG", "onCreate: calculation:" + calculation / 1000)
            notificationInterval = calculation

            val dateFormat: DateFormat = SimpleDateFormat("hh:mm a")
            /* binding!!.tvNextNotification.text =
                 dateFormat.format(calendar.time.time)*/

            timerLocation = object : CountDownTimer(notificationInterval, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    Log.d("TAG", "onTick: " + millisUntilFinished / 1000)
                }

                override fun onFinish() {
                    Log.d("TAG", "onFinish: ")
                    timerLocation!!.cancel()
                    getLastLocation(false)
                    getLocationDifference()
                }
            }

            timerLocation!!.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer!!.cancel()
        }
        if (timerLocation != null) {
            timerLocation!!.cancel()
        }
        if (timerNotification != null) {
            timerNotification!!.cancel()
        }
        mediaPlayer?.release()
    }

    private fun getNotificationIntervalTime(userToken: String, userId: Int) {
        if (Utils.isNetworkAvailable(this)) {
            YDelegate.getApiClient()
            val api = YDelegate.retrofit?.create(API::class.java)

            val apiCallBack: Call<NotificationIntervalTimeResponse>? =
                api?.getNotificationIntervalTime(userToken, userId)

            apiCallBack?.enqueue(object : Callback<NotificationIntervalTimeResponse> {

                override fun onFailure(call: Call<NotificationIntervalTimeResponse>, t: Throwable) {
                    if (t.localizedMessage != null) {
                        Utils.toast(
                            this@AlarmActivity,
                            t.localizedMessage
                        )
                    }
                }

                override fun onResponse(
                    call: Call<NotificationIntervalTimeResponse>,
                    response: Response<NotificationIntervalTimeResponse>
                ) {
                    try {
                        if (response.code() == 200) {
                            if (response.body()!!.getStatus() == YDelegate.SUCCESS) {


                                SessionManager().setPrefData(
                                    YDelegate.NOTIFICATION_TITLE,
                                    response.body()!!.getData()!![0]!!.getTitle().toString(),
                                    this@AlarmActivity
                                )
                                SessionManager().setPrefData(
                                    YDelegate.NOTIFICATION_DESCRIPTION,
                                    response.body()!!.getData()!![0]!!.getDescription().toString(),
                                    this@AlarmActivity
                                )
                                SessionManager().setPrefData(
                                    YDelegate.NOTIFICATION_INTERVAL_TIME,
                                    response.body()!!.getData()!![0]!!.getIntervalTime(),
                                    this@AlarmActivity
                                )
                                Log.d("onResponse", "onResponse:NOTIFICATION_ "+SessionManager().getPrefData(
                                    YDelegate.NOTIFICATION_INTERVAL_TIME,
                                    this@AlarmActivity
                                ))
                                getDifference()
                                /*if (SessionManager().getPrefData(
                                        YDelegate.NOTIFICATION_INTERVAL_TIME,
                                        this@AlarmActivity
                                    ).isNullOrEmpty()
                                ) {
                                    getDifference()
                                }*/

                            } else {
                                Utils.toast(
                                    this@AlarmActivity,
                                    response.body()!!.getMessage()!!
                                )
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Utils.toast(
                                    this@AlarmActivity,
                                    jObjError.getString("errorMessage")
                                )
                            } catch (e: Exception) {
                                Log.d("TAG", "onResponse: " + e.localizedMessage)
                                Utils.toast(
                                    this@AlarmActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        if (e.localizedMessage != null) {
                            Utils.toast(
                                this@AlarmActivity,
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

    private fun getGeoLocationGatheringTime(userToken: String, userId: Int) {
        if (Utils.isNetworkAvailable(this)) {
            YDelegate.getApiClient()
            val api = YDelegate.retrofit?.create(API::class.java)

            val apiCallBack: Call<GeoLocationGatheringTimeResponse>? =
                api?.getGeoLocationGatheringTime(userToken, userId)

            apiCallBack?.enqueue(object : Callback<GeoLocationGatheringTimeResponse> {

                override fun onFailure(call: Call<GeoLocationGatheringTimeResponse>, t: Throwable) {
                    if (t.localizedMessage != null) {
                        Utils.toast(
                            this@AlarmActivity,
                            t.localizedMessage
                        )
                    }
                }

                override fun onResponse(
                    call: Call<GeoLocationGatheringTimeResponse>,
                    response: Response<GeoLocationGatheringTimeResponse>
                ) {
                    try {
                        if (response.code() == 200) {
                            if (response.body()!!.getStatus() == YDelegate.SUCCESS) {
                                if (SessionManager().getPrefData(
                                        YDelegate.LOCATION_GATHERING_INTERVAL_TIME,
                                        this@AlarmActivity
                                    ).isNullOrEmpty()
                                ) {
                                    getLocationDifference()
                                }

                                SessionManager().setPrefData(
                                    YDelegate.LOCATION_GATHERING_INTERVAL_TIME,
                                    response.body()!!
                                        .getData()!![0]!!.getLocationGatheringIntervalTime(),
                                    this@AlarmActivity
                                )
                                Log.d(
                                    "onResponse", "onResponse: " + response.body()!!
                                        .getData()!![0]!!.getLocationGatheringIntervalTime()
                                )
                            } else {
                                Utils.toast(
                                    this@AlarmActivity,
                                    response.body()!!.getMessage()!!
                                )
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Utils.toast(
                                    this@AlarmActivity,
                                    jObjError.getString("errorMessage")
                                )
                            } catch (e: Exception) {
                                Log.d("TAG", "onResponse: " + e.localizedMessage)
                                Utils.toast(
                                    this@AlarmActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        if (e.localizedMessage != null) {
                            Utils.toast(
                                this@AlarmActivity,
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

    private fun storeUserNotificationTap(userToken: String, userId: Int) {
        if (Utils.isNetworkAvailable(this)) {
            YDelegate.getApiClient()
            val api = YDelegate.retrofit?.create(API::class.java)
            /*val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh-mm-ss")
            val tapDate = dateFormat.format(Calendar.getInstance().time)
            *//*val dcal = Calendar.getInstance()
            dcal.add(
                Calendar.MINUTE,
                -SessionManager().getPrefData(
                    YDelegate.NOTIFICATION_INTERVAL_TIME,
                    this@AlarmActivity
                )!!.toInt()
            )
            val displayDate = dateFormat.format(dcal.time)*//*

            Log.d("dateD", "storeUserNotificationTap: "+tapDate)
            Log.d("dateD", "storeUserNotificationTap: "+selectedSDate)


            var jsonObject: JsonObject = JsonObject()
            jsonObject.addProperty("notification_id", 1)
            jsonObject.addProperty("display_datetime", selectedSDate)
            jsonObject.addProperty("tap_datetime", tapDate)

            jsonNotificationTapArray.add(jsonObject)*/
            Log.d("UserNotificationTapNoti", "storeUserNotificationTap: $userToken");
            Log.d("UserNotificationTapNoti", "storeUserNotificationTap: $userId");
            Log.d("UserNotificationTapNoti", "storeUserNotificationTap: $jsonNotificationTapArray");
            val apiCallBack: Call<StoreGEOLocationResponse>? =
                api?.storeUserNotificationTap(
                    userToken,
                    userId,
                    jsonNotificationTapArray.toString()
                )

            apiCallBack?.enqueue(object : Callback<StoreGEOLocationResponse> {

                override fun onFailure(call: Call<StoreGEOLocationResponse>, t: Throwable) {
                    if (t.localizedMessage != null) {
                        Utils.toast(
                            this@AlarmActivity,
                            t.localizedMessage
                        )
                    }
                }

                override fun onResponse(
                    call: Call<StoreGEOLocationResponse>,
                    response: Response<StoreGEOLocationResponse>
                ) {
                    try {
                        if (response.code() == 200) {
                            if (response.body()!!.getStatus() == YDelegate.SUCCESS) {
                                Log.d("timeronTick", "onResponse: NotificationTap")
                                SessionManager().setPrefData(
                                    YDelegate.STORE_NOTIFICATION_JSON,
                                    "",
                                    this@AlarmActivity
                                )
                            } else {
                                Utils.toast(
                                    this@AlarmActivity,
                                    response.body()!!.getMessage()!!
                                )
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Utils.toast(
                                    this@AlarmActivity,
                                    jObjError.getString("errorMessage")
                                )
                            } catch (e: Exception) {
                                Log.d("TAG", "onResponse: " + e.localizedMessage)
                                Utils.toast(
                                    this@AlarmActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        if (e.localizedMessage != null) {
                            Utils.toast(
                                this@AlarmActivity,
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

    @SuppressLint("MissingPermission")
    private fun getLastLocation(isFirst: Boolean) {
        if (checkPermissions(isFirst)) {
            if (isLocationEnabled()) {

                fusedLocationClient!!.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        /* findViewById<TextView>(R.id.latTextView).text = location.latitude.toString()
                         findViewById<TextView>(R.id.lonTextView).text = location.longitude.toString()*/
                        Log.d("location", "getLastLocation: " + location.latitude.toString())
                        Log.d("location", "getLastLocation: " + location.longitude.toString())

                        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh-mm-ss")

                        /* try {
                             dbHelper.insertData(
                                 location.latitude.toString(), location.longitude.toString(),
                                 dateFormat.format(Calendar.getInstance().time)
                             )
                             Log.d("TAG", "getLastLocation: "+dbHelper.allData.count)
                         } catch (e: Exception) {
                             e.printStackTrace()
                             Toast.makeText(
                                 this@AlarmActivity,
                                 e.localizedMessage,
                                 Toast.LENGTH_SHORT
                             ).show()
                         }*/

                        val json = SessionManager().getPrefData(
                            YDelegate.STORE_LOCATION_JSON,
                            this@AlarmActivity
                        )
                        if (!json.equals("")) {
                            jsonLoctionArray = JsonParser().parse(json).asJsonArray
                        } else {
                            jsonLoctionArray = JsonArray()
                        }
                        var jsonArray: JsonArray = JsonArray()

                        var jsonObject: JsonObject = JsonObject()
                        jsonObject.addProperty("latitude", location.latitude.toString())
                        jsonObject.addProperty("longitude", location.longitude.toString())
                        jsonObject.addProperty(
                            "taken_datetime",
                            dateFormat.format(Calendar.getInstance().time)
                        )

                        jsonLoctionArray.add(jsonObject)
                        SessionManager().setPrefData(
                            YDelegate.STORE_LOCATION_JSON,
                            jsonLoctionArray.toString(),
                            this@AlarmActivity
                        )
                        if (jsonLoctionArray != null && jsonLoctionArray.size() > 0) {
                            storeUserGEOLocation(
                                userToken,
                                userId,
                                location.latitude.toString(),
                                location.longitude.toString(),
                                dateFormat.format(Calendar.getInstance().time)
                            )
                        }
                    }
                }
            } else {

                if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps()
                } else if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation()
                }

                // Toast.makeText(this@AlarmActivity, "Turn on location", Toast.LENGTH_LONG).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//                  startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri: Uri = Uri.fromParts("package", packageName, null)
//                intent.data = uri
//                // This will take the user to a page where they have to click twice to drill down to grant the permission
//                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 2000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            /*findViewById<TextView>(R.id.latTextView).text = mLastLocation.latitude.toString()
            findViewById<TextView>(R.id.lonTextView).text = mLastLocation.longitude.toString()*/

            Log.d(
                "location",
                "getLastLocation: onLocationResult:" + mLastLocation.latitude.toString()
            )
            Log.d(
                "location",
                "getLastLocation: onLocationResult:" + mLastLocation.longitude.toString()
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

        /*locationManager = Objects.requireNonNull(this)
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation()
        }*/
    }

    private fun buildAlertMessageNoGps() {
        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setMessage("Please Turn ON your GPS Connection")
            .setCancelable(false)
            .setPositiveButton(
                "yes"
            ) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }

        val alert = dialog.create()
        alert.show()
    }

    private fun getLocation() {
        if (((ActivityCompat.checkSelfPermission(
                Objects.requireNonNull(this),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED))
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
            )
        } else {
            val location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val location1 =
                locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val location2 =
                locationManager!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            when {
                location != null -> {
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                    BaseConfig.LocationCode.LATITUDE = latitude
//                    BaseConfig.LocationCode.LONGITUDE = longitude
                    // textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                    // + "\n" + "Longitude = " + longitude);
                    // Toast.makeText(this, "lat " + longitude, Toast.LENGTH_SHORT).show();
                }
                location1 != null -> {
//                    val latitude = location1.latitude
//                    val longitude = location1.longitude
//                    BaseConfig.LocationCode.LATITUDE = latitude
//                    BaseConfig.LocationCode.LONGITUDE = longitude
                    // textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                    // + "\n" + "Longitude = " + longitude);
                    // Toast.makeText(this, "lat " + longitude, Toast.LENGTH_SHORT).show();
                }
                location2 != null -> {
//                    val latitude = location2.latitude
//                    val longitude = location2.longitude
//                    BaseConfig.LocationCode.LATITUDE = latitude
//                    BaseConfig.LocationCode.LONGITUDE = longitude
                    // textView.setText("Your current location is"+ "\n" + "Lattitude = " + lattitude
                    // + "\n" + "Longitude = " + longitude);
                    // Toast.makeText(this, "lat " + longitude, Toast.LENGTH_SHORT).show();
                }
                else -> {
                    Handler().postDelayed({
                        // getCurrentLocation()
                    }, 3000)
                }
            }

        }
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
                //getLastLocation(true)
            } else {
                locationManager =
                    getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager != null) {
                    if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps()
                    } else if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        getLocation()
                    }
                }
            }
        }
    }

    private fun storeUserGEOLocation(
        userToken: String,
        userId: Int,
        latitude: String,
        longitude: String,
        date: String
    ) {
        if (Utils.isNetworkAvailable(this)) {
            YDelegate.getApiClient()
            val api = YDelegate.retrofit?.create(API::class.java)

            /*var jsonArray: JsonArray = JsonArray()

            var jsonObject: JsonObject = JsonObject()
            jsonObject.addProperty("latitude", latitude)
            jsonObject.addProperty("longitude", longitude)
            jsonObject.addProperty("taken_datetime", date)

            jsonArray.add(jsonObject)*/
            Log.d("jsonLoctionArray", "getLastLocation: " + jsonLoctionArray.toString())

            // Log.d("storeUserGEOLocationapi", "storeUserGEOLocation: jsonArray:$jsonArray")
            Log.d("storeUserGEOLocationapi", "storeUserGEOLocation: userToken: $userToken")
            Log.d("storeUserGEOLocationapi", "storeUserGEOLocation: userId: $userId")
            Log.d("storeUserGEOLocationapi", "storeUserGEOLocation: userId: $date")

            val apiCallBack: Call<StoreGEOLocationResponse>? =
                api?.storeUserGEOLocation(userToken, userId, jsonLoctionArray.toString())

            apiCallBack?.enqueue(object : Callback<StoreGEOLocationResponse> {

                override fun onFailure(call: Call<StoreGEOLocationResponse>, t: Throwable) {
                    if (t.localizedMessage != null) {
                        Utils.toast(
                            this@AlarmActivity,
                            t.localizedMessage
                        )
                    }
                }

                override fun onResponse(
                    call: Call<StoreGEOLocationResponse>,
                    response: Response<StoreGEOLocationResponse>
                ) {
                    try {
                        if (response.code() == 200) {
                            if (response.body()!!.getStatus() == YDelegate.SUCCESS) {
                                SessionManager().setPrefData(
                                    YDelegate.STORE_LOCATION_JSON,
                                    "",
                                    this@AlarmActivity
                                )
                                Log.d("location", "onResponse: ")
                            } else {
                                Log.d("location", "onResponse: Else: ")
                                Utils.toast(
                                    this@AlarmActivity,
                                    response.body()!!.getMessage()!!
                                )
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Log.d(
                                    "location",
                                    "onResponse: " + jObjError.getString("errorMessage")
                                )
                                Utils.toast(
                                    this@AlarmActivity,
                                    jObjError.getString("errorMessage")
                                )
                            } catch (e: Exception) {
                                Log.d("TAG", "onResponse: " + e.localizedMessage)
                                Utils.toast(
                                    this@AlarmActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("location", "onResponse: " + e.localizedMessage)
                        if (e.localizedMessage != null) {
                            Utils.toast(
                                this@AlarmActivity,
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

    override fun onPause() {
        super.onPause()
        //    mainHandler!!.removeCallbacks(updateTextTask)
    }

    override fun onResume() {
        super.onResume()
        /* if (timerStoretap != null) {
             timerStoretap!!.cancel()
         }
         timerStoretap = object : CountDownTimer(300000, 1000) {
             override fun onTick(millisUntilFinished: Long) {
                 Log.d("timeronTick", "onTick: " + millisUntilFinished / 1000)
             }

             override fun onFinish() {
                 Log.d("timeronTick", "onTick: ")
                 storeUserNotificationTap(
                     userToken,
                     userId
                 )
             }
         }
         timerStoretap!!.start()*/
    }

    override fun onClick(p0: View?) {
        binding!!.drawerLayout.closeDrawers()
        when (p0!!.id) {
            R.id.ll_assign_site_and_duty -> {
                Toast.makeText(
                    this@AlarmActivity,
                    getString(R.string.assign_sites_and_duty_time),
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.ll_sign_out -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@AlarmActivity)
                builder.setMessage(getString(R.string.confirmation_msg_sign_out))
                //performing positive action
                builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
                    dialogInterface.dismiss()
                    SessionManager().setPrefData(YDelegate.USER_TOKEN, "", this@AlarmActivity)
                    SessionManager().setPrefData(YDelegate.FIRST_NAME, "", this@AlarmActivity)
                    SessionManager().setPrefData(YDelegate.LAST_NAME, "", this@AlarmActivity)
                    SessionManager().setPrefIntData(YDelegate.USER_ID, 0, this@AlarmActivity)
                    SessionManager().setBooleanPrefData(
                        YDelegate.IS_LOGIN,
                        false,
                        this@AlarmActivity
                    )
                    SessionManager().setPrefData(
                        YDelegate.NOTIFICATION_INTERVAL_TIME,
                        "",
                        this@AlarmActivity
                    )
                    SessionManager().setPrefData(
                        YDelegate.LOCATION_GATHERING_INTERVAL_TIME,
                        "",
                        this@AlarmActivity
                    )
                    openActivity()
                }
                //performing cancel action
                builder.setNeutralButton(getString(R.string.no)) { dialogInterface, which ->
                    dialogInterface.dismiss()
                }

                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.orange))
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    .setTextColor(resources.getColor(R.color.orange))
            }
        }
    }

    private fun getAssignedSitesDutyTime(userToken: String, userId: Int) {
        if (Utils.isNetworkAvailable(this)) {
            YDelegate.getApiClient()
            val api = YDelegate.retrofit?.create(API::class.java)

            val apiCallBack: Call<NotificationIntervalTimeResponse>? =
                api?.getAssignedSitesDutyTime(userToken, userId)

            apiCallBack?.enqueue(object : Callback<NotificationIntervalTimeResponse> {

                override fun onFailure(call: Call<NotificationIntervalTimeResponse>, t: Throwable) {
                    if (t.localizedMessage != null) {
                        Utils.toast(
                            this@AlarmActivity,
                            t.localizedMessage
                        )
                    }
                }

                override fun onResponse(
                    call: Call<NotificationIntervalTimeResponse>,
                    response: Response<NotificationIntervalTimeResponse>
                ) {
                    try {
                        if (response.code() == 200) {
                            if (response.body()!!.getStatus() == YDelegate.SUCCESS) {
                                if (response.body()!!.getData() != null) {
                                    startTime =
                                        response.body()!!.getData()!![0]!!.getDutyStartTime()
                                            .toString()
                                    endTime = response.body()!!.getData()!![0]!!.getDutyEndTime()
                                        .toString()
                                    var currentTime = ""

                                    try {
                                        val string1 = startTime
                                        //val string1 = "20:11:13"
                                        val time1 = SimpleDateFormat("HH:mm:ss").parse(string1)
                                        val calendar1 = Calendar.getInstance()
                                        calendar1.time = time1
                                        calendar1.add(Calendar.DATE, 1)

                                        //val string2 = "14:49:00"
                                        val string2 = endTime
                                        val time2 = SimpleDateFormat("HH:mm:ss").parse(string2)
                                        val calendar2 = Calendar.getInstance()
                                        calendar2.time = time2
                                        calendar2.add(Calendar.DATE, 1)


                                        val timeInMillis = System.currentTimeMillis()
                                        val cal1 = Calendar.getInstance()
                                        cal1.timeInMillis = timeInMillis
                                        val dateFormat = SimpleDateFormat(
                                            "hh:mm:ss a"
                                        )
                                        currentTime = dateFormat.format(cal1.time)
                                        val displayFormat = SimpleDateFormat("HH:mm:ss a")
                                        val parseFormat = SimpleDateFormat("hh:mm:ss a")
                                        val date = parseFormat.parse(currentTime)

                                        Log.d(
                                            "onResponseTime",
                                            "onResponse: " + parseFormat.format(date)
                                                .toString() + " = " + displayFormat.format(date)
                                        )
                                        Log.d("onResponseTime", "onResponse: " + currentTime)

                                        //val someRandomTime = "01:00:00"
                                        val someRandomTime = displayFormat.format(date)
                                        val d = SimpleDateFormat("HH:mm:ss").parse(someRandomTime)
                                        val calendar3 = Calendar.getInstance()
                                        calendar3.time = d
                                        calendar3.add(Calendar.DATE, 1)

                                        val x = calendar3.time
                                        Log.d("onResponse", "onResponse: " + calendar1.time)
                                        Log.d("onResponse", "onResponse: " + calendar2.time)
                                        Log.d("onResponse", "onResponse: " + calendar3.time)
                                        if (x.after(calendar1.time) && x.before(calendar2.time)) {
                                            getNotificationIntervalTime(userToken, userId)
                                            getGeoLocationGatheringTime(userToken, userId)
                                            //getDifference()
                                        } else {
                                            Utils.toast(
                                                this@AlarmActivity,
                                                "Your duty s Off."
                                            )
                                        }
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                }

                            } else {
                                Utils.toast(
                                    this@AlarmActivity,
                                    response.body()!!.getMessage()!!
                                )
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                Utils.toast(
                                    this@AlarmActivity,
                                    jObjError.getString("errorMessage")
                                )
                            } catch (e: Exception) {
                                Log.d("TAG", "onResponse: " + e.localizedMessage)
                                Utils.toast(
                                    this@AlarmActivity,
                                    e.localizedMessage
                                )
                            }
                        }
                    } catch (e: Exception) {
                        if (e.localizedMessage != null) {
                            Utils.toast(
                                this@AlarmActivity,
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

}