package com.example.zenhabit.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.zenhabit.R
import com.example.zenhabit.databinding.ActivityRegisterBinding
import com.example.zenhabit.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
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

        val checkBoxTermsAndConditions: TextView = bin.textViewTermsAndConditions
        checkBoxTermsAndConditions.setOnClickListener(){
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://docs.google.com/document/d/1wtYD8rVOgt0BvMbN0Tl1huyKJ5ZbATtrKiwV_hx2gLw/edit?usp=sharing")
            startActivity(intent)
        }
    }



private fun crearUsuari(email: String, password: String, nom: String) {
    if (!validateForm()) {
        return
    } else {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val usuari = Usuari(nom, email, ArrayList<RepteUsuari>(), ArrayList<PlantaUsuari>(), ArrayList<Tasca>(), ArrayList<Habit>())
                    val db = FirebaseFirestore.getInstance().collection("Usuaris")
                        .document(Firebase.auth.currentUser!!.uid).set(usuari)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
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

private fun validateForm(): Boolean {
    var valid = true
    val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}\$".toRegex()

    val email = bin.inputCreateEmail.text.toString().trim()
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        bin.inputCreateEmail.error = "El format d'email és incorrecte."
        valid = false
    } else {
        bin.inputCreateEmail.error = null
    }

    val password = bin.inputCreatePsw.text.toString().trim()
    if (!password.matches(PASSWORD_REGEX)) {
        bin.inputCreatePsw.error = "La contrasenya com a mínim ha de tenir 8 caràcters i un número."
        valid = false
    } else {
        bin.inputCreatePsw.error = null
    }

    val nom = bin.inputCreateUserName.text.toString().trim()
    if (nom.length >= 10) {
        bin.inputCreateUserName.error = "El nom ha de tenir un máxim de 10 caràcters."
    } else {
        bin.inputCreateUserName.error = null
    }
    //if (!valid) {
    //Toast(this).showCustomToast("Camp buit", this)
    //}
    return valid
}

private fun Toast.showCustomToast(message: String, activity: RegisterActivity) {
    val layout = activity.layoutInflater.inflate(
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