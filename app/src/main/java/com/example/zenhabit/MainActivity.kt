package com.example.zenhabit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //anar a tasques
        findViewById<LinearLayout>(R.id.btn_tasques).setOnClickListener {
            setContentView(R.layout.fragment_tasks)
        }

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
    }
}