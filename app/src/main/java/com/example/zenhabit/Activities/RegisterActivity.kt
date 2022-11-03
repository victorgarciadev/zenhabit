package com.example.zenhabit.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.zenhabit.R
import com.example.zenhabit.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var bin: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bin = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bin.root)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        auth = Firebase.auth


        val buttonCancel: Button = findViewById(R.id.btn_cancel)
        buttonCancel.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }


        val buttonRegister: Button = findViewById(R.id.btn_register)
        buttonRegister.setOnClickListener {
            crearUsuari(
                bin.inputCreateEmail.text.toString().trim(),
                bin.inputCreatePsw.text.toString().trim(),
                bin.inputCreateUserName.text.toString().trim()
            )
        }

    }

    private fun crearUsuari(email: String, password: String, nom: String) {
        if (email == "" || password == "" || nom == "") {
            Toast(this).showCustomToast("Falten camps per completar. Torna a provar", this)
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast(this).showCustomToast("Usuari creat correctament", this)
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        if (nom.length > 1) posaNomUser(nom)
                    } else {
                        Toast(this).showCustomToast("L'autentificació ha fallat", this)
                    }
                }
        }
    }

    private fun posaNomUser(nom: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = nom
        }

        auth.currentUser!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }
    }

    private fun Toast.showCustomToast(message: String, activity: RegisterActivity)
    {
        val layout = activity.layoutInflater.inflate (
            R.layout.toast_layout,
            activity.findViewById(R.id.toast_container)
        )

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        this.apply {
            setGravity(Gravity.CENTER, 0, 750)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
}