package com.example.zenhabit.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zenhabit.R
import com.example.zenhabit.databinding.ActivityNoInternetBinding
import kotlin.system.exitProcess

class NoInternetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoInternetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancel.setOnClickListener {
            finishAffinity()
        }
        }
    }