package com.example.zenhabit

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import com.example.zenhabit.databinding.ActivityMainBinding
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.zenhabit.Fragments.JardiFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bin: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<LinearLayout>(R.id.btn_veureHabitTasca).setOnClickListener {
            setContentView(R.layout.fragment_tasks)
        }

        bin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bin.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Mostrar menú more (Action Bar)
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_action_bar, menu)     // Inicialitzar el menú
//        return true
//    }
//    // Accions pels ítems del menú more (Action Bar)
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
//        }
//        return super.onOptionsItemSelected(item)
//    }

    }