package com.example.zenhabit.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.zenhabit.R
import com.example.zenhabit.databinding.ActivityLoginBinding
import com.example.zenhabit.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var bin: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bin = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bin.root)

        auth = Firebase.auth

        val buttonRegister: Button = findViewById(R.id.btn_register)
        buttonRegister.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val buttonCancel: Button = findViewById(R.id.btn_cancel)
        buttonCancel.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        buttonRegister.setOnClickListener {
            crearUsuari(
                bin.inputCreateEmail.text.toString(),
                bin.inputCreatePsw.text.toString(),
                bin.inputCreateUserName.text.toString()
            )
        }



    }
}