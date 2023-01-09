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

/**
 * @author Victor García, Izan Jimenez, Txell Llanas, Pablo Morante
 */
class LoginActivity : AppCompatActivity() {

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

    /**
     * Es crida quan l'activitat es fa visible per a l'usuari.
     *
     * Aquest mètode verifica si l'usuari té connexió a Internet i redirigeix a NoInternetActivity si no en té.
     * Si l'usuari té connexió a Internet, comprova si l'usuari ha iniciat sessió i redirigeix a MainActivity en conseqüència.
     *
     * @throws IllegalStateException si l'activitat s'està destruint.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public override fun onStart() {
        super.onStart()
        if (!isOnline(this)) {
            startActivity(Intent(this, NoInternetActivity::class.java))
        } else {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    /**
     * Intents d'iniciar sessió a l'usuari amb el correu electrònic i la contrasenya especificats.
     * Si l'inici de sessió té èxit, l'usuari passa a la pantalla d'inici i es mostra un missatge de toast.
     * Si l'inici de sessió falla, es mostra un missatge de toast i es mostra el botó de restabliment de la contrasenya si el nombre d'intents fallits és superior o igual a 3.
     *
     * @param email de l'usuari
     * @param password de l'usuari
     * @author Izan Jimenez, Pablo Morante
     */
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
                                    Toast(this).showCustomToast(
                                        getString(R.string.send_email),
                                        this
                                    )
                                } else {
                                    Toast(this).showCustomToast(
                                        getString(R.string.error_send_email),
                                        this
                                    )
                                }
                            }
                    }
                }
            }
        }
    }

    /**
     * Valida el formulari comprovant que els camps de correu electrònic i contrasenya no estiguin buits.
     * Si algun dels camps està buit, es mostra un missatge d'error.
     *
     * @return true si el formulari és vàlid, false en cas contrari
     * @author Izan Jimenez, Txell Llanas, Pablo Morante
     */
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
        return valid
    }

    /**
     * Mostra un toast (missatge) personalitzat amb el text especificat.
     *
     * @param missatge que es mostrarà al toast
     * @param activity des de la qual es mostra el toast
     * @author Pablo Morante
     */
    private fun Toast.showCustomToast(message: String, activity: LoginActivity) {
        val layout = activity.layoutInflater.inflate(
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

    /**
     * Comprova si el dispositiu té una connexió a Internet activa.
     *
     * @param context des del qual es crida la funció
     * @return true si el dispositiu té una connexió a Internet activa, false en cas contrari
     * @author Pablo Morante
     */
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