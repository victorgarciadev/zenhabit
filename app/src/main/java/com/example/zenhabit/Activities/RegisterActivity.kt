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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Victor García, Izan Jimenez, Txell Llanas, Pablo Morante
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var bin: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bin = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bin.root)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        auth = Firebase.auth

        val buttonCancel: Button = bin.btnCancel
        buttonCancel.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val buttonRegister: Button = bin.btnRegister
        buttonRegister.setOnClickListener {
            crearUsuari(
                bin.inputCreateEmail.text.toString().trim(),
                bin.inputCreatePsw.text.toString().trim(),
                bin.inputCreateUserName.text.toString().trim()
            )
        }

        val checkBoxTermsAndConditions: TextView = bin.textViewTermsAndConditions
        checkBoxTermsAndConditions.setOnClickListener() {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("https://docs.google.com/document/d/1wtYD8rVOgt0BvMbN0Tl1huyKJ5ZbATtrKiwV_hx2gLw/edit?usp=sharing")
            startActivity(intent)
        }
    }


    /**
     * Crea un usuari nou amb el correu electrònic, la contrasenya i el nom especificats.
     *
     * Aquest mètode comprova primer si el formulari és vàlid. Si no és així, el mètode torna sense fer res.
     * Si el formulari és vàlid, el mètode crea un usuari nou amb el correu electrònic i la contrasenya especificats mitjançant l'autenticació de Firebase.
     * Si l'usuari es crea correctament, també crea un document nou a Firebase Firestore per a l'usuari amb el nom especificat i altres camps predeterminats.
     * A continuació, mostra un missatge de toast que indica que l'usuari s'ha creat correctament i es redirigeix a LoginActivity.
     * Si no s'ha pogut crear l'usuari, mostra un missatge de toast que indica un error.
     *
     * @param email del nou usuari
     * @param password del nou usuari
     * @param nom del nou usuari
     *
     * @throws IllegalStateException si l'activitat s'està destruint.
     * @author Pablo Morante, Izan Jimenez
     */
    private fun crearUsuari(email: String, password: String, nom: String) {
        if (!validateForm()) {
            return
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        var plantes: ArrayList<PlantaUsuari> = ArrayList()
                        var reptes: ArrayList<RepteUsuari> = ArrayList()
                        addPlantesToList(plantes)
                        val usuari = Usuari(
                            nom,
                            email,
                            reptes,
                            plantes,
                            ArrayList<Objectius>()
                        )
                        db.collection("Usuaris")
                            .document(auth.currentUser!!.uid).set(usuari)
                            .addOnSuccessListener {
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot successfully written!"
                                )
                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
                        val actualDay = Calendar.getInstance().getTime()
                        val temporal = VerificacioNotificacio(actualDay, false)
                        db.collection("Verificacions")
                            .document(auth.currentUser!!.uid).set(temporal)
                        Toast(this).showCustomToast(getString(R.string.user_created), this)
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        if (nom.length > 1) posaNomUser(nom)
                        runBlocking {
                            addReptesToList()
                        }
                    } else {
                        Toast(this).showCustomToast(getString(R.string.error_user_created), this)
                    }
                }
        }
    }

    /**
     * Estableix el nom de l'usuari actual.
     *
     * Aquest mètode actualitza el nom de l'usuari actual mitjançant l'autenticació de Firebase.
     * Si l'actualització té èxit, registra un missatge que indica que el perfil d'usuari s'ha actualitzat.
     *
     * @param nom de l'usuari actual
     * @author Pablo Morante
     */
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

    /**
     * Valida el formulari per crear un nou usuari.
     *
     * Aquest mètode comprova si hi ha errors als camps de correu electrònic, contrasenya i nom.
     * Si es troba algun error, estableix un missatge d'error per al camp corresponent.
     *
     * @return true si el formulari és vàlid, false en cas contrari
     *
     * @throws IllegalStateException si l'activitat s'està destruint.
     * @author Pablo Morante, Txell Llanas
     */
    private fun validateForm(): Boolean {
        var valid = true
        val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{4,}\$".toRegex()

        val email = bin.inputCreateEmail.text.toString().trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            bin.inputCreateEmail.error = getString(R.string.error_email_created)
            valid = false
        } else {
            bin.inputCreateEmail.error = null
        }

        val password = bin.inputCreatePsw.text.toString().trim()
        if (!password.matches(PASSWORD_REGEX)) {
            bin.inputCreatePsw.error = getString(R.string.error_password_created)
            valid = false
        } else {
            bin.inputCreatePsw.error = null
        }

        val nom = bin.inputCreateUserName.text.toString().trim()
        if (nom.length > 15) {
            bin.inputCreateUserName.error = getString(R.string.error_username_created)
            valid = false
        } else {
            bin.inputCreateUserName.error = null
        }

        val checkBox = bin.checkBoxTermsAndConditions
        if (!checkBox.isChecked) {
            valid = false
            Toast(this).showCustomToast(getString(R.string.toast_accept_terms), this)
        }
        return valid
    }

    /**
     * @author Pablo Morante
     */
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

    /**
     * Afegeix les planta predeterminades a la llista de creació.
     *
     * @param plantes llista a la qual s'afegiran les plantes
     * @author Pablo Morante
     */
    private fun addPlantesToList(plantes: ArrayList<PlantaUsuari>) {
        plantes.add(PlantaUsuari("Abeto", 0))
        plantes.add(PlantaUsuari("Palmera", 0))
        plantes.add(PlantaUsuari("Olivo", 0))
        plantes.add(PlantaUsuari("Girasol", 0))
        plantes.add(PlantaUsuari("Rosa", 0))
        plantes.add(PlantaUsuari("Lavanda", 0))
        plantes.add(PlantaUsuari("Aloe Vera", 0))
        plantes.add(PlantaUsuari("Bambú", 0))
        plantes.add(PlantaUsuari("Cactus", 0))

    }

    /***
     * Aquest metode s'tulutzara per omplir els reptes de la Colecció reptes dins de l'usuari
     * @author Izan Jimenez
     */
    private fun addReptesToList() {

        val llistaReptes = ArrayList<RepteUsuari>()

        runBlocking {
            FirebaseFirestore.getInstance().collection("Reptes").get()
                .addOnSuccessListener { result ->

//                    val a = result.toObject<RepteUsuari>()
//                    if (a != null) {
//                        Log.d("REPTESObject", a.acosneguit.toString())
//                    }


                    if (result != null) {
                        for (i in 0 until result.size()) {

                            val r =
                                RepteUsuari.dataFirebaseReptestoReptesUsuaris(result.documents[i])

                            llistaReptes.add(r)

                            FirebaseFirestore.getInstance().collection("Usuaris")
                                .document(auth.currentUser!!.uid)
                                .update("llistaReptes", llistaReptes)


                        }
                    }

                }.addOnFailureListener { exception ->
                    Log.d("TAG", "ERROR: $exception")
                }
        }

    }
}