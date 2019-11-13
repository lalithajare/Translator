package com.translator.app.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.translator.app.R
import com.translator.app.utils.Prefs

/**
 * This is launcher Activity.
 * It will be displayed for 3 sec.
 * After that the ProfileActivity will be opened, if the User is not enter previously, or MainActivity.
 */
class SplashActivity : AppCompatActivity() {

    /**
     * Entry point to this activity
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (Prefs.user == null) {
                ProfileActivity.beginActivity(this)
            } else {
                MainActivity.beginActivity(this)
            }
            finish()
        }, 3000)

    }
}
