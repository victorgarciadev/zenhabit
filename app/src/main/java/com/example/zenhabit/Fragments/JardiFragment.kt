package com.example.zenhabit.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentJardiBinding
import java.text.SimpleDateFormat
import java.util.*

class JardiFragment : Fragment() {
    private var _binding: FragmentJardiBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJardiBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.garden_title))
        val view = binding.root
        christmasSpecial()

        // Inflate the layout for this fragment
        return view
    }

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
}
