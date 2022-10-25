package com.example.zenhabit.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.zenhabit.MainActivity
import com.example.zenhabit.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // Declarar variable pel View Binding
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Inicialitzar View Binding (Activity)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Intent cap a la pantalla principal 'Home' si l'usuari no està loguejat
        binding.btnEnter.setOnClickListener {
            signIn(binding.inputEmail.text.toString(), binding.inputPsw.text.toString())
        }


        binding.textViewRegisterLink.setOnClickListener {
           // val intent = Intent(this, RegisterActivity::class.java)
           // startActivity(intent)
        }
    }

    // Comprovar si usuari ja està loguejat
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //binding.btnEnter.setOnClickListener {
            //   val intent = Intent(this, MainActivity::class.java)
            //   startActivity(intent)
            //}
        }
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        if (email != "" || password != "") {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext, "Authentication complete.",
                            Toast.LENGTH_SHORT
                        ).show()
                        //cambiar pantalla
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            // [END sign_in_with_email]
        } else {
            Toast.makeText(
                baseContext, "Campo vacio",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
