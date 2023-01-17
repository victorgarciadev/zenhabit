package com.projectegrupd.zenhabit.Fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.projectegrupd.zenhabit.Activities.LoginActivity
import com.projectegrupd.zenhabit.R
import com.projectegrupd.zenhabit.databinding.FragmentHomeBinding
import com.projectegrupd.zenhabit.models.Objectius
import java.util.*

/**
 * @author Pablo Morante, Victor García, Izan Jimenez
 */
class home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var shake: Animation
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "ZenHabit"
        val view = binding.root
        binding.btnVeureJardi.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_jardiFragment)
        }

        // Mostrar Dialog per demanar ressenya a l'usuari
        resetDialog()

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

    /**
     * Mètode per mostrar en un 'Dialog' demanant una ressenya sobre l'App a l'usuari.     *
     * @author Txell Llanas
     */
    private fun showReview() {

        val dialog = activity?.let {
            Dialog(it, R.style.CustomDialog)
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog?.setContentView(R.layout.dialog_review)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            val reviewBtn = dialog?.findViewById<Button>(R.id.btn_deixar_ressenya)
            closeBtn?.setOnClickListener { dialog.dismiss() }
            // Anar a Google Play
            reviewBtn?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.projectegrupd.zenhabit"))
                startActivity(intent)
                dialog.dismiss()
            }
        }

    /**
     * Estableix l'interval de temps (1 dia) per tornar a mostrar la petició de ressenya al Google Play.
     * @author Pablo Morante, Txell Llanas
     */
    private fun resetDialog() {
        val actualDay = Calendar.getInstance().time// dia i hora actual
        FirebaseFirestore.getInstance().collection("Verificacions")
            .document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                if (!result.data.isNullOrEmpty()) {
                    val lastDay = result.getTimestamp("lastDateReview")!!
                        .toDate() // dia i hora que es va mostrar l'avís

                    val difference: Long = actualDay.time - lastDay.time
                    val seconds = difference / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24
                    if (days >= 1) { // si ja ha passat un dia mostra l'avís una altra vegada
                        showReview()
                        FirebaseFirestore.getInstance().collection("Verificacions")
                            .document(auth.currentUser!!.uid)
                            .update("lastDateReview", actualDay)
                    }
                } else {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                }
            }
    }

}
