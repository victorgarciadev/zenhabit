package com.example.zenhabit.Activities

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.annotation.RequiresApi
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
    private var passwordTry: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
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
            if (!isOnline(this)) {
                startActivity(Intent(this, NoInternetActivity::class.java))
            } else {
                signIn(binding.inputEmail.text.toString(), binding.inputPsw.text.toString())
            }
        }

        binding.textViewRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Comprovar si usuari ja està loguejat
    @RequiresApi(Build.VERSION_CODES.M)
    public override fun onStart() {
        super.onStart()
        // check if user has internet connection
        if (!isOnline(this)) {
            startActivity(Intent(this, NoInternetActivity::class.java))
        } else {
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = auth.currentUser
            if (currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            }
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
                Toast(this).showCustomToast("Hola, ${user?.displayName}!", this)
                //canviar pantalla
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                binding.inputPsw.setText("")
                passwordTry++
                // Si falla, mostrem l’error o errors
                Log.w(TAG, "signInWithEmail:fail", task.exception)
                Toast(this).showCustomToast(getString(R.string.signIn_fail), this)
                if (passwordTry >= 3) {
                    binding.forgotPassword.visibility = View.VISIBLE
                    binding.forgotPassword.setOnClickListener {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast(this).showCustomToast(getString(R.string.send_email), this)
                                } else {
                                    Toast(this).showCustomToast(getString(R.string.error_send_email), this)
                                }
                            }
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.inputEmail.text.toString().trim()
        if (TextUtils.isEmpty(email)) {
            binding.inputEmail.error = getString(R.string.mandatory_camp)
            valid = false
        } else {
            binding.inputEmail.error = null
        }

        val password = binding.inputPsw.text.toString().trim()
        if (TextUtils.isEmpty(password)) {
            binding.inputPsw.error = getString(R.string.mandatory_camp)
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}
