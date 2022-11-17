package com.example.zenhabit.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentHomeBinding

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
        activity?.actionBar?.show()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnVeureJardi.setOnClickListener{
            findNavController().navigate(R.id.action_home2_to_jardiFragment)  // posar aquest codi als btn
        }
        binding.btnVeureHabitTasca.setOnClickListener{
            findNavController().navigate(R.id.action_home2_to_tasksFragment2)
        }
        return view
    }



}