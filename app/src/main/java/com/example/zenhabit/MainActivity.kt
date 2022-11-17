package com.example.zenhabit

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.zenhabit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bin: ActivityMainBinding
    lateinit var bottomNavigation : BottomNavigationView

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
        //actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        //actionBar?.setCustomView(R.menu.menu_action_bar)
        //menuInflater.inflate(R.menu.menu_action_bar, )
        //actionBar?.setCustomView(R.menu.menu_action_bar)
        //actionBar?.setDisplayShowCustomEnabled(true)
        bottomNavigation = bin.bottomNavigationView as BottomNavigationView
        bottomNavigation.setOnItemSelectedListener{ item ->
            when (item.itemId) {
                R.id.home -> navController.navigate(R.id.homeActivity)
                R.id.settings -> navController.navigate(R.id.settingsFragment)
            }
            true
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

    // Mostrar menú more (Action Bar)
 //   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_action_bar, menu)     // Inicialitzar el menú
//        return true
//    }
    // Accions pels ítems del menú more (Action Bar)
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.about) {
//            //val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity, R.style.CustomDialog)
//            //builder.setView(R.layout.about_dialog)
//            //builder.show()
//
//            // Codi OK per tornar transparent el fons de l'XML
//            val dialog = Dialog(this@MainActivity)
//            dialog.setContentView(R.layout.about_dialog)
//            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.show()
//
//            return true
//       }
//        return super.onOptionsItemSelected(item)
//    }

    }