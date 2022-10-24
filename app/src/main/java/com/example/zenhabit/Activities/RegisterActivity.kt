package com.example.zenhabit.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zenhabit.R
import com.example.zenhabit.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_register)
    }


}