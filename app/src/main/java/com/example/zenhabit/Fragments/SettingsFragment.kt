package com.example.zenhabit.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.zenhabit.Activities.LoginActivity
import com.example.zenhabit.MainActivity
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


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
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle("Configuració")
        binding.btnSave.setOnClickListener {
            Log.d("0","entra button")
            val actualUser = FirebaseAuth.getInstance().currentUser
            val email = actualUser!!.email
            val actualPsw = binding.inputActualPsw.text
            if (!actualPsw.isEmpty()) {
                val credential = EmailAuthProvider.getCredential(email!!, actualPsw.toString())
                Log.d("1","Entra if")
                //actualUser.reauthenticate(credential)
                actualUser.reauthenticate(credential).addOnCompleteListener {
                    actualUser.updatePassword(binding.inputChangePsw.text.toString()).addOnCompleteListener { task ->
                        Log.d("2","Entra update")
                        if (task.isSuccessful) {
                            Toast(activity).showCustomToast("Contrasenya canviada correctament.")
                        } else {
                            Toast(activity).showCustomToast("Error al canviar contrasenya.")
                        }
                    }
                }

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