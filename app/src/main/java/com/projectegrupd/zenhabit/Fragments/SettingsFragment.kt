package com.projectegrupd.zenhabit.Fragments

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.projectegrupd.zenhabit.R
import com.projectegrupd.zenhabit.databinding.FragmentSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

/**
 * @author Victor García, Txell Llanas, Pablo Morante
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.title =
            getString(R.string.config_title)

        binding.btnSaveNewPsw.setOnClickListener {
            val actualPsw = binding.inputActualPsw.text.toString()
            if (!actualPsw.isEmpty()) {
                changePassword(actualPsw)
            }
        }

        binding.btnSaveEmail.setOnClickListener {
            val actualPsw = binding.inputActualPswEmail.text.toString()
            if (actualPsw.isNotEmpty()) {
                changeEmail(actualPsw)
            }
        }

        binding.btnSaveNom.setOnClickListener {
            val nouNom = binding.inputChangeUserName.text.toString()
            if (nouNom.length in 3..15) {
                changeNom()
            } else {
                Toast(activity).showCustomToast(getString(R.string.error_username_created))
            }
        }

        activity?.actionBar?.show()

        return binding.root

    }

    /**
     * Canvia la contrasenya de l'usuari connectat actualment.
     *
     * @param actualPsw contrasenya actual de l'usuari
     */
    private fun changePassword(actualPsw: String) {
        val actualUser = FirebaseAuth.getInstance().currentUser
        val email = actualUser!!.email
        val credential = EmailAuthProvider.getCredential(email!!, actualPsw.toString())
        actualUser.reauthenticate(credential).addOnCompleteListener {
            actualUser.updatePassword(binding.inputChangePsw.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast(activity).showCustomToast(getString(R.string.toast_change_password))
                        binding.inputActualPsw.text.clear()
                        binding.inputChangePsw.text.clear()
                    } else {
                        Toast(activity).showCustomToast(getString(R.string.error_password_created))
                        binding.inputActualPsw.text.clear()
                        binding.inputChangePsw.text.clear()
                    }
                }
        }
    }

    /**
     * Canvia el correu electrònic de l'usuari connectat actualment.
     *
     * @param actualPsw contrasenya actual de l'usuari
     */
    private fun changeEmail(actualPsw: String) {
        val actualUser = FirebaseAuth.getInstance().currentUser
        val email = actualUser!!.email
        val credential = EmailAuthProvider.getCredential(email!!, actualPsw.toString())
        actualUser.reauthenticate(credential).addOnCompleteListener {
            actualUser.updateEmail(binding.inputChangeEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        db.collection("Usuaris")
                            .document(actualUser.uid)
                            .update("email", binding.inputChangeEmail.text.toString())
                        Toast(activity).showCustomToast(getString(R.string.toast_change_email))
                        binding.inputActualPswEmail.text.clear()
                        binding.inputChangeEmail.text.clear()
                    } else {
                        Toast(activity).showCustomToast(getString(R.string.error_email_created))
                        binding.inputActualPswEmail.text.clear()
                        binding.inputChangeEmail.text.clear()
                    }
                }
        }
    }

    /**
     * Canvia el nom de l'usuari connectat actualment.
     */
    private fun changeNom() {
        val actualUser = FirebaseAuth.getInstance().currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = binding.inputChangeUserName.text.toString()
        }
        actualUser!!.updateProfile(profileUpdates)
        db.collection("Usuaris")
            .document(actualUser.uid)
            .update("nom", binding.inputChangeUserName.text.toString())
            .addOnSuccessListener {
                Toast(activity).showCustomToast(getString(R.string.toast_change_name))
                binding.inputChangeUserName.text.clear()
            }
    }

    /**
     * Mostra un missatge de toast personalitzat amb el text donat.
     *
     * @param message el text per mostrar al toast
     * @author Pablo Morante
     */
    private fun Toast.showCustomToast(message: String) {
        val layout = requireActivity().layoutInflater.inflate(
            R.layout.toast_layout,
            requireActivity().findViewById(R.id.toast_container)
        )

        val textView = layout.findViewById<TextView>(R.id.toast_text)
        textView.text = message

        this.apply {
            setGravity(Gravity.CENTER, 0, 700)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

}