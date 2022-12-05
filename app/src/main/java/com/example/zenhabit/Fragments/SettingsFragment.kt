package com.example.zenhabit.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.zenhabit.MainActivity
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentSettingsBinding
import com.example.zenhabit.models.Habit
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
    }

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
                    actualUser.updatePassword(binding.inputChangePsw.text.toString()).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast(activity).showCustomToast(getString(R.string.toast_change_password))
                        } else {
                            Toast(activity).showCustomToast(getString(R.string.error_password_created))
                        }
                    }
                }
            }
        }

        binding.btnSaveEmail.setOnClickListener{
            val actualUser = FirebaseAuth.getInstance().currentUser
            val email = actualUser!!.email
            val actualPsw = binding.inputActualPswEmail.text
            if (!actualPsw.isEmpty()) {
                val credential = EmailAuthProvider.getCredential(email!!, actualPsw.toString())
                actualUser.reauthenticate(credential).addOnCompleteListener {
                    actualUser.updateEmail(binding.inputChangeEmail.text.toString()).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = FirebaseFirestore.getInstance().collection("Usuaris")
                                .document(actualUser.uid).update("email", binding.inputChangeEmail.text.toString())
                            Toast(activity).showCustomToast(getString(R.string.toast_change_email))
                        } else {
                            Toast(activity).showCustomToast(getString(R.string.error_email_created))
                        }
                    }
                }
            }
        }

        binding.btnSaveNom.setOnClickListener {
            val messi = binding.inputChangeUserName.text.toString()
            if (messi.length <= 15 && messi.length >= 3) {
                val actualUser = FirebaseAuth.getInstance().currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.inputChangeUserName.text.toString()
                }
                actualUser!!.updateProfile(profileUpdates)
                val document = FirebaseFirestore.getInstance().collection("Usuaris")
                    .document(actualUser.uid).update("nom", binding.inputChangeUserName.text.toString())
            } else {
                Toast(activity).showCustomToast(getString(R.string.error_username_created))
            }
        }

        activity?.actionBar?.show()

        return binding.root

    }

    private fun Toast.showCustomToast(message: String)
    {
        val layout = requireActivity().layoutInflater.inflate (
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