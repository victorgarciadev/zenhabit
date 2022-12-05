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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.zenhabit.Activities.LoginActivity
import com.example.zenhabit.Fragments.JardiFragment
import com.example.zenhabit.databinding.ActivityMainBinding
import com.example.zenhabit.models.Planta
import com.example.zenhabit.models.Repte
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bin: ActivityMainBinding
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

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("ZenHabit")

        bottomNavigation = bin.bottomNavigationView
        bottomNavigation.setOnItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.home -> navController.navigate(R.id.homeActivity)
                R.id.settings -> navController.navigate(R.id.settingsFragment)
            }
            true
        }
        //De momento la siguiente línea hace que no se quede marcado el último botón tocado en la NavBar
        bottomNavigation.itemIconTintList = null;
        //FirebaseUtils()



        //*** NOTIFICACIONS ***
        // fonts: https://developer.android.com/develop/ui/views/notifications/build-notification
        // youtube: https://www.youtube.com/watch?v=Zt00P509v10
        createNotificationChannel()

        //3. Definir què fer quan s'obre la notificació 'ITEMS ACONSEGUITS (JARDÍ)' -> Crear un intent explícit o navigation a un fragment
        navController.navigate(R.id.jardiFragment)
            // val intent = Intent(this, JardiFragment::class.java).apply {
            //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //}

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // 1. Crear constructor per mostrar la notificació
        val builder = NotificationCompat.Builder(this, canalID)
            .setSmallIcon(R.drawable.ic_notificacio)
            .setContentTitle(getString(R.string.label_notificacio_jardi))
            .setContentText(getString(R.string.label_notificacio_getNewItem))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Especificar l'intent que s'executarà quan l'usuari toqui la notificació
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 2. Fer que la notificació aparegui (especificar trigger...) PENDENT!!!
        with(NotificationManagerCompat.from(this)) {
            notify(notificacio_jardi_ID, builder.build())
        }
        //*** FI NOTIFICACIONS ***


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Mostrar menú more (Action Bar)
//   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_action_bar, menu)     // Inicialitzar el menú
//        return true
//    }

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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    class FirebaseUtils {
        val reto = Repte(3,"Escriure't una nota positiva","Nota")
        //val planta = Planta("exemple", "exemple descripcio2", "@drawable/ic_tree01_200", "pinacio")
        val db = FirebaseFirestore.getInstance().collection("Reptes")
            .document(reto.idRepte.toString()).set(reto)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    // Mètodes *** NOTIFICACIONS ***
    // 1. Crea el canal per gestionar totes les notificacions
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

    }