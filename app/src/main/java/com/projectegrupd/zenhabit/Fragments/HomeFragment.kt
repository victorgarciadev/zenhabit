package com.projectegrupd.zenhabit.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.projectegrupd.zenhabit.Activities.LoginActivity
import com.projectegrupd.zenhabit.R
import com.projectegrupd.zenhabit.databinding.FragmentHomeBinding
import com.projectegrupd.zenhabit.models.Objectius
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

/**
 * @author Pablo Morante, Victor García, Izan Jimenez
 */
class home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var shake: Animation
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle("ZenHabit")
        val view = binding.root
        binding.btnVeureJardi.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_jardiFragment)
        }
        try {
            db.collection("Usuaris")
                .document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener { result ->
                    shake = AnimationUtils.loadAnimation(activity, R.anim.bell_animation)
                    val objectius = Objectius.dataFirebaseToObjectius(result)
                    val filteredObjectius = objectius.filter { !it.complert }
                    val numeroAlerta = filteredObjectius.count()
                    var numeroObjectius = objectius.count()
                    var habitosRealizados = 0f
                    var tareasRealizadas = 0f
                    var totalHabitos = 0f
                    var totalTareas = 0f
                    if (numeroAlerta == 1) {
                        binding.tasquesPendents.text =
                            getString(R.string.pendents_primera) + " 1 " + getString(R.string.pendents_segona_singular)
                        binding.imgNotification.startAnimation(shake)
                    } else {
                        if (numeroAlerta != 0) {
                            binding.imgNotification.startAnimation(shake)
                        }
                        binding.tasquesPendents.text =
                            getString(R.string.pendents_primera) + " $numeroAlerta " + getString(R.string.pendents_segona_plural)
                    }
                    if (numeroObjectius > 0) {
                        for (objectiu in objectius) {
                            if (objectiu.tipus) {
                                totalHabitos++
                                if (objectiu.complert) {
                                    habitosRealizados++
                                }
                            } else if (!objectiu.tipus) {
                                totalTareas++
                                if (objectiu.complert) {
                                    tareasRealizadas++
                                }
                            }
                        }
                        var pendientes = numeroObjectius - (habitosRealizados + tareasRealizadas)
                        pendientes = (pendientes / numeroObjectius) * 100
                        if (totalHabitos > 0) {
                            habitosRealizados = (habitosRealizados / totalHabitos) * 100
                            binding.percentHabits.progress = habitosRealizados.toInt()
                        }
                        if (totalTareas > 0) {
                            tareasRealizadas = (tareasRealizadas / totalTareas) * 100
                            binding.percentTasques.progress = tareasRealizadas.toInt()
                        }
                        binding.percentPendents.progress = pendientes.toInt()
                    }
                }
        } catch (e: NullPointerException) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnVeureHabitTasca.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_tasksFragment2)
        }
        return view
    }


}
