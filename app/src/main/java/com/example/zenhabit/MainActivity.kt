package com.example.zenhabit

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
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

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val btnJardi: LinearLayout = bin.btnJardi
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