package com.example.zenhabit

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.zenhabit.Activities.LoginActivity
import com.example.zenhabit.databinding.ActivityMainBinding
import com.example.zenhabit.models.Habit
import com.example.zenhabit.models.Objectius
import com.example.zenhabit.models.Repte
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bin: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    lateinit var bottomNavigation : BottomNavigationView

    // Atributs *** NOTIFICACIONS ***
    private val canalID = "channelID"
    private val nomCanal = "@strings/nom_canal"
    private val descripcioCanal = "@strings/descripcio_canal"
    private val notificacio_jardi_ID = 0  // notificationId is a unique int for each notification that you must define

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bin.root)

        auth = Firebase.auth

        resetNotification()  // comprova si ja ha pasat un dia des de la última notificacio

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("ZenHabit")

        //hideSystemUI() // esconde la bottomnavigation de android

        bottomNavigation = bin.bottomNavigationView
        bottomNavigation.setOnItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.home -> navController.popBackStack(R.id.homeActivity, false)
                R.id.settings -> navController.navigate(R.id.settingsFragment)
            }
            true
        }
        //De momento la siguiente línea hace que no se quede marcado el último botón tocado en la NavBar
        bottomNavigation.itemIconTintList = null;
        //FirebaseUtils()
    }

    override fun onStop() {
        // l'aplicació entra aqui quan l'usuari ja no la veu
        super.onStop()
        // checkeja a la bbdd si ja ha vist la notifiació avui o o no
        if (auth.currentUser != null) {
            FirebaseFirestore.getInstance().collection("Verificacions")
                .document(auth.currentUser!!.uid).get()
                .addOnSuccessListener { result ->
                    val vist = result.get("vist") as Boolean
                    if (!vist) {
                        FirebaseFirestore.getInstance().collection("Verificacions")
                            .document(auth.currentUser!!.uid).update( "vist",true)
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            launchNotification() // funció que llença la notificació, es farà als 3 segons de tancar l'aplicació
                        }, 3000)
                    }
                }
        }
    }

    class FirebaseUtils {
        val reto = Repte(3,"Escriure't una nota positiva","Nota")
        //val planta = Planta("exemple", "exemple descripcio2", "@drawable/ic_tree01_200", "pinacio")
        val db = FirebaseFirestore.getInstance().collection("Reptes")
            .document(reto.idRepte.toString()).set(reto)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    //*** INICI NAVBAR ***
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    //*** FI NAVBAR ***

    // Accions pels ítems del menú more (Action Bar)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about) {

            // Codi per tornar transparent el fons de l'XML
            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.about_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            return true
       }
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
            finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }

    // INICI *** NOTIFICACIONS ***
    // Crea el canal per gestionar totes les notificacions
    private fun createNotificationChannel() {
        // Crea el NotificationChannel, però només per API 26+ perquè aquesta és una Classe nova no present a les llibreries de suport estàndards
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT // indica grau d'importància de la notificació
            // Nom del canal per definir les notificacions de l'app
            val channel = NotificationChannel(canalID, nomCanal, importance).apply {
                // definir color led per l'avís de notificació
                // LightColor = Color.GREEN
                // enableLights(true)
                description = descripcioCanal
            }
            // Registrar el canal al Sistema (telèfon)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun launchNotification() {
        FirebaseFirestore.getInstance().collection("Usuaris")
            .document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val tasca = result.get("llistaObjectius") as ArrayList<Objectius>
                val numeroPendents = tasca.count()
                var text = ""
                if (numeroPendents > 0) {
                    createNotificationChannel()
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    if (numeroPendents == 1) {
                        text = getString(R.string.pendents_primera) + " $numeroPendents " + getString(R.string.label_notificacio_getNewItem_singular)
                    } else {
                        text = getString(R.string.pendents_primera) + " $numeroPendents " + getString(R.string.label_notificacio_getNewItem_plural)
                    }
                    // 1. Crear constructor per mostrar la notificació
                    val builder = NotificationCompat.Builder(this, canalID)
                        .setSmallIcon(R.drawable.ic_notificacio)
                        .setContentTitle(getString(R.string.label_notificacio_jardi))
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Especificar l'intent que s'executarà quan l'usuari toqui la notificació
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                    // 2. Fer que la notificació aparegui (especificar trigger...) PENDENT!!!
                    with(NotificationManagerCompat.from(this)) {
                        notify(notificacio_jardi_ID, builder.build())
                    }
                    val flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
    }
    private fun resetNotification() {
        val actualDay = Calendar.getInstance().getTime()// dia i hora actual
        FirebaseFirestore.getInstance().collection("Verificacions")
            .document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val lastDay = result.getTimestamp("lastDate")!!.toDate() // dia i hora que es va llençar l'última notificació
                    val difference: Long = actualDay.time - lastDay.time
                    val seconds = difference / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24
                    if (days >= 1) { // si ja ha passat un dia canvia la bbdd per saber que ha de llençar la notifiació una altre vegada
                        FirebaseFirestore.getInstance().collection("Verificacions")
                            .document(auth.currentUser!!.uid).update( "vist",false, "lastDate",actualDay)
                    }
            }
    }


    //*** FI NOTIFICACIONS ***

    private fun hideSystemUI() {
        // comprobar si la SDK es superior o inferior a 30
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            if (window.insetsController != null) {
                window.insetsController!!.hide(WindowInsets.Type.navigationBars()) // esconde la navegacion inferior
                window.insetsController!!.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // la muestra si hacer swipe para arriba
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // versiones inferiores a 30
        }
    }


}