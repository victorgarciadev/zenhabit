package com.example.zenhabit.Activities

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

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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