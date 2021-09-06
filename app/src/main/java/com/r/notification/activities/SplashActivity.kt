package com.r.notification.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.r.notification.R
import com.r.notification.utilities.SessionManager
import com.r.notification.YDelegate
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer().schedule(3000) {
            openMainActivity()
        }
    }

    private fun openMainActivity() {
        var intent: Intent =
            if (SessionManager().getBooleanPrefData(YDelegate.IS_LOGIN, this)) {
                Intent(this, AlarmActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}