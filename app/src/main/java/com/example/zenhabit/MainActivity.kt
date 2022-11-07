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
import com.example.zenhabit.databinding.ActivityMainBinding
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.zenhabit.Fragments.JardiFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bin: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bin.root)

        //val actionBar: ActionBar? = supportActionBar
        //actionBar?.hide()
    }

    // Mostrar menú more (Action Bar)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_action_bar, menu)     // Inicialitzar el menú
        return true
    }
    // Accions pels ítems del menú more (Action Bar)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about) {
            //val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity, R.style.CustomDialog)
            //builder.setView(R.layout.about_dialog)
            //builder.show()

            // Codi OK per tornar transparent el fons de l'XML
            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.about_dialog)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            return true
        }
        return super.onOptionsItemSelected(item)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val btnJardi: Button = bin.btnVeureJardi
        btnJardi.setOnClickListener{
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentJardi,JardiFragment())
                commit()
            }
//            Log.d(ContentValues.TAG, "button clicked.")
//            val myFragment = JardiFragment()
//            val fragment : Fragment? =
//                supportFragmentManager.findFragmentByTag(JardiFragment::class.java.simpleName)
//            if (fragment !is JardiFragment) {
//                Log.d(ContentValues.TAG, "if entered.")
//                supportFragmentManager.beginTransaction()
//                    .add(R.id.fragmentJardi, myFragment, JardiFragment::class.java.simpleName)
//                    .commit()
//            }
        }
    }
}