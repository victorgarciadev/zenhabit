package com.example.zenhabit.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zenhabit.MainActivity
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentHomeBinding
import com.example.zenhabit.models.Habit
import com.example.zenhabit.models.Objectiu
import com.example.zenhabit.models.Tasca
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var shake: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle("ZenHabit")
        val view = binding.root
        binding.btnVeureJardi.setOnClickListener{
            findNavController().navigate(R.id.action_home2_to_jardiFragment)  // posar aquest codi als btn
        }
        FirebaseFirestore.getInstance().collection("Usuaris")
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                    shake = AnimationUtils.loadAnimation(activity, R.anim.bell_animation)
                    val objectius = result.get("llistaObjectius") as ArrayList<Objectiu>
                    var numeroTasques = 0
                    var numeroHabits = 0
                    if (objectius != null) { // si hi ha elements a la llista entra
                        for (objectiu in objectius) {
                            if (objectiu.tipus) { // si tipus es true és que es un habit, si no, una tasca
                                numeroHabits++
                            } else {
                                numeroTasques++
                            }
                        }
                    }
                if (numeroTasques == 1 && numeroHabits == 1) {
                    binding.tasquesPendents.text = getString(R.string.pendents_primera) + " 1 " + getString(R.string.pendents_segona_singular) + " 1 " + getString(R.string.pendents_tercera_singular)
                    binding.imgNotification.startAnimation(shake)
                } else if (numeroTasques == 1 && numeroHabits != 1) {
                    binding.tasquesPendents.text = getString(R.string.pendents_primera) + " 1 " + getString(R.string.pendents_segona_singular) + " $numeroHabits " + getString(R.string.pendents_tercera_plural)
                    binding.imgNotification.startAnimation(shake)
                } else if (numeroTasques != 1 && numeroHabits == 1) {
                    binding.tasquesPendents.text = getString(R.string.pendents_primera) + " $numeroTasques " + getString(R.string.pendents_segona_plural) + " 1 " + getString(R.string.pendents_tercera_singular)
                    binding.imgNotification.startAnimation(shake)
                } else {
                    if(numeroTasques != 0 || numeroHabits != 0){
                        binding.imgNotification.startAnimation(shake)
                    }
                    binding.tasquesPendents.text = getString(R.string.pendents_primera) + " $numeroTasques " + getString(R.string.pendents_segona_plural) + " $numeroHabits " + getString(R.string.pendents_tercera_plural)
                }
            }
        binding.btnVeureHabitTasca.setOnClickListener{
            findNavController().navigate(R.id.action_home2_to_tasksFragment2)
        }
        return view
    }



}
