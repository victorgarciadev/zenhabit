package com.example.zenhabit.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentHomeBinding
import com.example.zenhabit.models.Objectius
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

/**
 * @author Pablo Morante, Victor García, Izan Jimenez
 */
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
                    val objectius = Objectius.dataFirebaseToObjectius(result)
                    val filteredObjectius = objectius.filter{ !it.complert}
                    val numeroAlerta = filteredObjectius.count()
                    var numeroObjectius = objectius.count()
                    var habitosRealizados: Float = 0f
                    var tareasRealizadas: Float = 0f
                    var totalHabitos: Float = 0f
                    var totalTareas: Float = 0f
                if (numeroAlerta == 1 ) {
                    binding.tasquesPendents.text = getString(R.string.pendents_primera) + " 1 " + getString(R.string.pendents_segona_singular)
                    binding.imgNotification.startAnimation(shake)
                } else {
                    if(numeroAlerta != 0){
                        binding.imgNotification.startAnimation(shake)
                    }
                    binding.tasquesPendents.text = getString(R.string.pendents_primera) + " $numeroAlerta " + getString(R.string.pendents_segona_plural)
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
        binding.btnVeureHabitTasca.setOnClickListener{
            findNavController().navigate(R.id.action_home2_to_tasksFragment2)
        }
        return view
    }



}
