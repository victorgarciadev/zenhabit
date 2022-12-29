package com.example.zenhabit.Fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentSettingsBinding
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.config_title))

        binding.btnSaveNewPsw.setOnClickListener {
            val actualUser = FirebaseAuth.getInstance().currentUser
            val email = actualUser!!.email
            val actualPsw = binding.inputActualPsw.text
            if (!actualPsw.isEmpty()) {
                val credential = EmailAuthProvider.getCredential(email!!, actualPsw.toString())
                actualUser.reauthenticate(credential).addOnCompleteListener {
                    actualUser.updatePassword(binding.inputChangePsw.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast(activity).showCustomToast(getString(R.string.toast_change_password))
                            } else {
                                Toast(activity).showCustomToast(getString(R.string.error_password_created))
                            }
                        }
                }
            }
        }

        binding.btnSaveEmail.setOnClickListener {
            val actualUser = FirebaseAuth.getInstance().currentUser
            val email = actualUser!!.email
            val actualPsw = binding.inputActualPswEmail.text
            if (!actualPsw.isEmpty()) {
                val credential = EmailAuthProvider.getCredential(email!!, actualPsw.toString())
                actualUser.reauthenticate(credential).addOnCompleteListener {
                    actualUser.updateEmail(binding.inputChangeEmail.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                FirebaseFirestore.getInstance().collection("Usuaris")
                                    .document(actualUser.uid)
                                    .update("email", binding.inputChangeEmail.text.toString())
                                Toast(activity).showCustomToast(getString(R.string.toast_change_email))
                            } else {
                                Toast(activity).showCustomToast(getString(R.string.error_email_created))
                            }
                        }
                }
            }
        }

        binding.btnSaveNom.setOnClickListener {
            val nouNom = binding.inputChangeUserName.text.toString()
            if (nouNom.length <= 15 && nouNom.length >= 3) {
                val actualUser = FirebaseAuth.getInstance().currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.inputChangeUserName.text.toString()
                }
                actualUser!!.updateProfile(profileUpdates)
                FirebaseFirestore.getInstance().collection("Usuaris")
                    .document(actualUser.uid)
                    .update("nom", binding.inputChangeUserName.text.toString())
            } else {
                Toast(activity).showCustomToast(getString(R.string.error_username_created))
            }
        }

        activity?.actionBar?.show()

        return binding.root

    }

    /**
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