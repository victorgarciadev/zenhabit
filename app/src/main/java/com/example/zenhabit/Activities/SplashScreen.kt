package com.example.zenhabit.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.ActionBar
import com.example.zenhabit.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        Handler().postDelayed(Runnable {
            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            finish()
        }, 2600)
    }
}