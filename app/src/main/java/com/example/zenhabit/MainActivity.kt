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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.zenhabit.databinding.ActivityMainBinding
import com.example.zenhabit.models.Objectius
import com.example.zenhabit.models.Repte
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * @author Victor García, Izan Jimenez, Txell Llanas, Pablo Morante
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bin: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    lateinit var bottomNavigation: BottomNavigationView

    // *** NOTIFICACIONS *** (Atributs)
    private val canalID = "channelID"
    private val nomCanal = "@strings/nom_canal"
    private val descripcioCanal = "@strings/descripcio_canal"
    private val notificacioJardiID = 0  // ID únic per identificar aquesta notificació

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

        bottomNavigation = bin.bottomNavigationView
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> navController.popBackStack(R.id.homeActivity, false)
                R.id.settings -> navController.navigate(R.id.settingsFragment)
            }
            true
        }
        //De momento la siguiente línea hace que no se quede marcado el último botón tocado en la NavBar
        bottomNavigation.itemIconTintList = null
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
                            .document(auth.currentUser!!.uid).update("vist", true)
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            launchNotification() // funció que llença la notificació, es farà als 3 segons de tancar l'aplicació
                        }, 3000)
                    }
                }
        }
    }

    //*** NAVBAR (INICI) ***
    /**
     * Mètode per carregar ítems dins l'Action Bar, si aquesta existeix.
     *
     * @property menu El menú a crear.
     * @return valor booleà en funció de si el menú es pot crear satisfactòriament o no.
     * @author Txell Llanas
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_action_bar, menu)
        return true
    }

    /**
     * Mètode per controlar la navegació cap amunt.
     *
     * @return Boolean que determina el tipus de navegació cap amunt.
     * @author Txell Llanas, Pablo Morante
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    //*** FI NAVBAR ***

    //*** MENÚ MORE (INICI) ***
    /**
     * Mètode per especificar una acció per cada ítem que contingui el menú 'More' (Action Bar)
     *
     * @property item L'ítem a crear dins el menú.
     * @return Especifica quin ítem dins el menú s'ha seleccionat.
     * @author Txell Llanas, Pablo Morante
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Ítem 'About': Codi per mostrar el pop-up amb els crèdits
        if (item.itemId == R.id.about) {
            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.about_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            return true
        }

        // Ítem 'Log Out': Codi per sortir de l'aplicació (tancar sessió)
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            finishAffinity()
        }

        return super.onOptionsItemSelected(item)

    }
    // *** FI MENÚ MORE ***

    //*** NOTIFICACIONS (INICI) ***
    /**
     * Crea un canal per gestionar totes les notificacions a l'usuari dins de l'App.
     * Permet especifcar-ne una descripció i un nivell de prioritat per a totes les notificacions
     * assignades a aquest canal.
     *
     * @author Txell Llanas
     */
    private fun createNotificationChannel() {

        // Crea el NotificationChannel, però només per API 26+ perquè aquesta és una Classe nova no present a les llibreries de suport estàndards
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Indica un grau d'importància 'alt' per la notificació (emet un so)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            // Nom del canal per definir les notificacions de l'app
            val channel = NotificationChannel(canalID, nomCanal, importance).apply {
                description = descripcioCanal

            }

            // Registra el canal al Sistema (Dispositiu mòbil)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    /**
     * Llança una notificació que mostra el nombre de tasques pendents a la llista de tasques de l'usuari.
     * Si no hi ha tasques pendents, no es mostra cap notificació.
     *
     * @author Pablo Morante
     */
    private fun launchNotification() {
        FirebaseFirestore.getInstance().collection("Usuaris")
            .document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val objectius = Objectius.dataFirebaseToObjectius(result)
                val objectiusFiltered = objectius.filter { !it.complert }
                val numeroPendents = objectiusFiltered.count()
                var text = ""
                if (numeroPendents > 0) {
                    createNotificationChannel()
                    val pendingIntent: PendingIntent =
                        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                    if (numeroPendents == 1) {
                        text =
                            getString(R.string.pendents_primera) + " $numeroPendents " + getString(R.string.label_notificacio_getNewItem_singular)
                    } else {
                        text =
                            getString(R.string.pendents_primera) + " $numeroPendents " + getString(R.string.label_notificacio_getNewItem_plural)
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
                        notify(notificacioJardiID, builder.build())
                    }
                    val flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
    }

    /**
     * Restableix l'estat de notificació a la base de dades perquè es pugui tornar a mostrar una notificació.
     * L'estat de la notificació es restableix si la darrera vegada que es va mostrar una notificació va ser fa almenys un dia.
     *
     * @author Pablo Morante
     */
    private fun resetNotification() {
        val actualDay = Calendar.getInstance().getTime()// dia i hora actual
        FirebaseFirestore.getInstance().collection("Verificacions")
            .document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val lastDay = result.getTimestamp("lastDate")!!
                    .toDate() // dia i hora que es va llençar l'última notificació
                val difference: Long = actualDay.time - lastDay.time
                val seconds = difference / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                if (days >= 1) { // si ja ha passat un dia canvia la bbdd per saber que ha de llençar la notifiació una altre vegada
                    FirebaseFirestore.getInstance().collection("Verificacions")
                        .document(auth.currentUser!!.uid)
                        .update("vist", false, "lastDate", actualDay)
                }
            }
    }
    //*** FI NOTIFICACIONS ***
}