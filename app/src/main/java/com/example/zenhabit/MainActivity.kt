package com.example.zenhabit

import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
//            val myFragment = JardiFragment()
//            val fragment : Fragment? =
//                supportFragmentManager.findFragmentByTag(myFragment::class.java.simpleName)

            var jardiFragment = JardiFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragmentJardi, jardiFragment).commit()
//            supportFragmentManager.beginTransaction()
//                .add(R.id.constraintLayout, myFragment, myFragment::class.java.simpleName)
//                .commit()
//            }
        }
    }
}