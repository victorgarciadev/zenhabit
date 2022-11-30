package com.example.zenhabit.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zenhabit.MainActivity
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentHomeBinding
import com.example.zenhabit.models.Tasca
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [home.newInstance] factory method to
 * create an instance of this fragment.
 */
class home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        val csActivity = activity as MainActivity
        val view = binding.root
        binding.btnVeureJardi.setOnClickListener{
            findNavController().navigate(R.id.action_home2_to_jardiFragment)  // posar aquest codi als btn
        }
        val tasquesPendents = FirebaseFirestore.getInstance().collection("Usuaris")
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                    val patata = result.get("llistaTasques") as ArrayList<Tasca>
                    val numeroPatatas = patata.count()
                    if (numeroPatatas == 1) {
                        binding.tasquesPendents.text = "Tens 1 tasca pendent"
                    } else {
                        binding.tasquesPendents.text = "Tens $numeroPatatas tasques pendents"
                    }
            }
        binding.btnVeureHabitTasca.setOnClickListener{
            findNavController().navigate(R.id.action_home2_to_tasksFragment2)
        }
        return view
    }



}
