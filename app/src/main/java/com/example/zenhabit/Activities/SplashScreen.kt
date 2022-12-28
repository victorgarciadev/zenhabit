package com.example.zenhabit.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.ActionBar
import com.example.zenhabit.R

/**
 * @author Victor García, Pablo Morante
 */
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            finish()
        }, 4000)
    }
}
