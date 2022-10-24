package com.example.zenhabit.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zenhabit.R
import com.example.zenhabit.databinding.ActivityLoginBinding
import com.example.zenhabit.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var bin: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bin.root)

        auth = Firebase.auth

    }
}