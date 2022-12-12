package com.example.zenhabit

import android.app.*
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
import com.example.zenhabit.databinding.ActivityMainBinding
import com.example.zenhabit.models.Habit
import com.example.zenhabit.models.Repte
import com.example.zenhabit.models.Tasca
import com.example.zenhabit.models.Verificacio
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


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
        resetNotification() // funcio que checkea si ha passat un dia des de la última notificació o no
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

    }

    override fun onStop() {
        // l'aplicació entra aqui quan l'usuari ja no la veu
        super.onStop()
            // checkeja a la bbdd si ja ha vist la notifiació avui o o no
            val verificacio = FirebaseFirestore.getInstance().collection("Verificacions")
                .document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener { result ->
                    val vist = result.get("vist") as Boolean
                    if (!vist) {
                        val update = FirebaseFirestore.getInstance().collection("Verificacions")
                            .document(Firebase.auth.currentUser!!.uid).update( "vist",true)
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            launchNotification() // funció que llença la notificació, es farà als 3 segons de tancar l'aplicació
                        }, 3000)
                        }
                    }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

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
        //val reto = Repte(3,"Escriure't una nota positiva","Nota")
        val verifica = Verificacio(false, Calendar.getInstance().getTime())
        //val planta = Planta("exemple", "exemple descripcio2", "@drawable/ic_tree01_200", "pinacio")
        val db = FirebaseFirestore.getInstance().collection("Verificacions")
            .document(Firebase.auth.currentUser!!.uid).set(verifica)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    private fun launchNotification() {
        val tasquesPendents = FirebaseFirestore.getInstance().collection("Usuaris")
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val tasca = result.get("llistaTasques") as ArrayList<Tasca>
                val habit = result.get("llistaHabits") as ArrayList<Habit>
                val numeroPendents = tasca.count() + habit.count()
                var text = ""
                if (numeroPendents > 0) {
                    createNotificationChannel()
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    if (numeroPendents == 1) {
                        text = getString(R.string.label_notificacio_getNewItemPrimer) + " $numeroPendents " + getString(R.string.label_notificacio_getNewItemPrimer_singular)
                    } else {
                        text = getString(R.string.label_notificacio_getNewItemPrimer) + " $numeroPendents " + getString(R.string.label_notificacio_getNewItemPrimer_plural)
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
        //*** FI NOTIFICACIONS ***
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

    private fun resetNotification() {
        val actualDay = Calendar.getInstance().getTime() // dia i hora actual
        val verificacio = FirebaseFirestore.getInstance().collection("Verificacions")
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val lastDay = result.get("lastDate") as Date // dia i hora que es va llençar l'última notificació
                val difference: Long = lastDay.time - actualDay.time
                val seconds = difference / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                if (days >= 1) { // si ja ha passat un dia canvia la bbdd per saber que ha de llençar la notifiació una altre vegada
                    val update = FirebaseFirestore.getInstance().collection("Verificacions")
                        .document(Firebase.auth.currentUser!!.uid).update( "vist",false, "lastDate",actualDay)
                }
            }
    }
    }