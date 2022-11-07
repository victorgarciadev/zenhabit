package com.example.zenhabit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.widget.Button
import android.widget.LinearLayout
import com.example.zenhabit.databinding.ActivityMainBinding
import androidx.appcompat.app.ActionBar
import com.example.zenhabit.Fragments.JardiFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bin: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<LinearLayout>(R.id.btn_veureHabitTasca).setOnClickListener {
            setContentView(R.layout.fragment_tasks)
        }

        bin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bin.root)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
    }
}