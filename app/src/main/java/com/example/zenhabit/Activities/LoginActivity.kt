package com.example.zenhabit.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.zenhabit.MainActivity
import com.example.zenhabit.R
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

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        // Intent cap a la pantalla principal 'Home' si l'usuari no està loguejat
        binding.btnEnter.setOnClickListener {
            signIn(binding.inputEmail.text.toString(), binding.inputPsw.text.toString())
        }

        binding.textViewRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Comprovar si usuari ja està loguejat
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            //startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun signIn(email: String, password: String) {
        //verificació d’errors de camps de text. Mostra errors en Toast
        if (!validateForm()) {
            return
        }
        //si tot és correcte es comprova al firebase
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            //si troba usuari canvia la pantalla i mostra el home
            if (task.isSuccessful) {
                // Login correcte, mostrem que tot ha anat correcte
                Log.d(TAG, "signInWithEmail:success")
                val user = auth.currentUser
                Toast(this).showCustomToast("Hola, ${user?.displayName} !", this)
                //canviar pantalla
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Si falla, mostrem l’error o errors
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast(this).showCustomToast("Usuari o contrasenya incorrectes", this)
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.inputEmail.text.toString().trim()
        if (TextUtils.isEmpty(email)) {
            binding.inputEmail.error = "Camp obligatori"
            valid = false
        } else {
            binding.inputEmail.error = null
        }

        val password = binding.inputPsw.text.toString().trim()
        if (TextUtils.isEmpty(password)) {
            binding.inputPsw.error = "Camp obligatori"
            valid = false
        } else {
            binding.inputPsw.error = null
        }
        //if (!valid) {
            //Toast(this).showCustomToast("Camp buit", this)
        //}
        return valid
    }

    private fun Toast.showCustomToast(message: String, activity: LoginActivity)
    {
        val layout = activity.layoutInflater.inflate (
            R.layout.toast_layout,
            activity.findViewById(R.id.toast_container)
        )

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        this.apply {
            setGravity(Gravity.CENTER, 0, 700)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

}