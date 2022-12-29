package com.example.zenhabit.Fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentJardiBinding
import com.example.zenhabit.models.PlantaUsuari
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Victor García, Txell Llanas, Pablo Morante
 */
class JardiFragment : Fragment() {
    private var _binding: FragmentJardiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJardiBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.garden_title))
        val view = binding.root
        christmasSpecial()

        // Mostra la descripció pels ítems del Jardí (CardViews)
        openItemDescription()

        // Mostra la quantitat de plantes aconseguides
        getPlantesfromFirebase()

        // Inflate the layout for this fragment
        return view
    }

    /**
     * Habilita una visualització especial de Nadal si la data actual és entre el 15 de desembre i el 6 de gener.
     *
     * @author Pablo Morante
     */
    private fun christmasSpecial() {
        val actualDay = Calendar.getInstance().getTime()
        val d1 = "12/15/2022"
        val d2 = "01/06/2023"

        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val firstDate: Date = sdf.parse(d1)
        val secondDate: Date = sdf.parse(d2)

        if (actualDay.compareTo(firstDate) > 0) {
            if (actualDay.compareTo(secondDate) < 0) {
                binding.snowfall.isVisible = true
            }
        }
    }

    /**
     * Mètode per mostrar en un 'Dialog' les decripcions de cada ítem del Jardí quan es clica
     * el cardView corresponent.
     *
     * @author Txell Llanas
     */
    private fun openItemDescription() {

        val dialog = activity?.let {
            Dialog(it, R.style.CustomDialog)
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Item 1: Avet
        binding.cardViewAvet.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_avet_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 2: Palmera
        binding.cardViewPalmera.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_palmera_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 3: Olivera
        binding.cardViewOlivera.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_olivera_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 4: Girasol
        binding.cardViewGirasol.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_girasol_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 5: Rosa
        binding.cardViewRosa.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_rosa_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 6: Lavanda
        binding.cardViewLavanda.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_lavanda_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 7: Aloe-vera
        binding.cardViewAloe.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_aloe_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 8: Bambú
        binding.cardViewBambu.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_bambu_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
        // Item 9: Cactus
        binding.cardViewCactus.setOnClickListener {
            dialog?.setContentView(R.layout.dialog_cactus_description)
            dialog?.show()

            val closeBtn = dialog?.findViewById<Button>(R.id.btn_tancar_dialeg)
            closeBtn?.setOnClickListener { dialog.dismiss() }
        }
    }

    /**
     * Mètode per mostrar la quantitat de plantes aconseguides dintre del fragment Jardí.
     *
     * @author Víctor García
     */
    private fun getPlantesfromFirebase() {
        FirebaseFirestore.getInstance().collection("Usuaris")
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val plantes = PlantaUsuari.dataFirebaseToPlanta(result)
                var i = 0
                for (planta in plantes) {
                    when (i) {
                        0 -> binding.quantitat0.text = planta.quantitat.toString()
                        1 -> binding.quantitat1.text = planta.quantitat.toString()
                        2 -> binding.quantitat2.text = planta.quantitat.toString()
                        3 -> binding.quantitat3.text = planta.quantitat.toString()
                        4 -> binding.quantitat4.text = planta.quantitat.toString()
                        5 -> binding.quantitat5.text = planta.quantitat.toString()
                        6 -> binding.quantitat6.text = planta.quantitat.toString()
                        7 -> binding.quantitat7.text = planta.quantitat.toString()
                        8 -> binding.quantitat8.text = planta.quantitat.toString()
                    }
                    i++
                }
            }

    }
}

