package com.example.zenhabit.Fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentCreateEditHabitBinding
import com.example.zenhabit.databinding.FragmentCreateEditTaskBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [CreateEditTaskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateEditTaskFragment : Fragment() {

    // View Binding (Fragment)
    private var _binding: FragmentCreateEditTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (View Binding)
        _binding = FragmentCreateEditTaskBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle("Crear tasca")
        val view = binding.root

        val name = arguments?.get("Name").toString()
        if (name != "null") {
            binding.nomTascaEdit.setText(name)
            (activity as AppCompatActivity?)!!.supportActionBar?.setTitle("Editar tasca")
            binding.btnCrearEditarHabit.isVisible = false
        } else {
            binding.nomTascaEdit.setText("Nom de la tasca")
            binding.btnCrearEditarHabit.isVisible = true
        }
        // binding pels botons 'btn_crearEditarHabit' i 'btn_guardarCrearEditarHabit'
        binding.btnCrearEditarHabit.setOnClickListener {
            findNavController().navigate(R.id.action_createEditTaskFragment_to_createEditHabitFragment)
        }
        binding.btnGuardarCrearEditarTasca.setOnClickListener {
            findNavController().navigate(R.id.action_createEditTaskFragment_to_tasksFragment2)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Carregar dades XML + crear adaptador pel Dropdown Menu (Categories)
        val categories = resources.getStringArray(R.array.categories)
        val adapter = activity?.let {
            ArrayAdapter(
                it,
                R.layout.list_item, // Carrego layout per mostrar els ítems
                categories
            )
        }

        with(binding.autoCompleteTextView){
            setAdapter(adapter)
        }

//--------------------------------------------CALENDARI--------------------------------------------
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)

        var initialYear = -1
        if (initialYear == -1)
            initialYear = year
        var initialMonth = -1
        if (initialMonth == -1)
            initialMonth = c.get(Calendar.MONTH)
        var initialDay = -1
        if (initialDay == -1)
            initialDay = c.get(Calendar.DAY_OF_MONTH)
        binding.etPlannedDate.hint = "$initialDay-$initialMonth-$initialYear"
        binding.apply {
            etPlannedDate.hint
            etPlannedDate.setOnClickListener {
                // create new instance of DatePickerFragment
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                // we have to implement setFragmentResultListener
                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        etPlannedDate.hint = date
                    }
                }

                // show
                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }
        }
//--------------------------------------------CALENDARI--------------------------------------------

    }


    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
        private val calendar = Calendar.getInstance()

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // default date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // return new DatePickerDialog instance
            return DatePickerDialog(requireActivity(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val selectedDate = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(calendar.time)

            val selectedDateBundle = Bundle()
            selectedDateBundle.putString("SELECTED_DATE", selectedDate)

            setFragmentResult("REQUEST_KEY", selectedDateBundle)
        }
    }

}
