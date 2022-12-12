package com.example.zenhabit.Fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.zenhabit.R
import com.example.zenhabit.databinding.FragmentCreateEditHabitBinding
import com.example.zenhabit.models.Dies
import com.example.zenhabit.models.Habit
import com.example.zenhabit.models.Objectiu
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateEditHabitFragment : Fragment() {

    // View Binding (Fragment)
    private var _binding: FragmentCreateEditHabitBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (View Binding)
        _binding = FragmentCreateEditHabitBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.create_habit))
        val view = binding.root

        // binding pels botons 'btn_crearEditarTasca' i 'btn_guardarCrearEditarHabit'
        binding.btnCrearEditarTasca.setOnClickListener {
            findNavController().navigate(R.id.action_createEditHabitFragment_to_createEditTaskFragment)
        }
        binding.btnGuardarCrearEditarHabit.setOnClickListener {
            val nom = binding.nomHabitEdit.editableText.toString()
            val descripcio = binding.txtInputDescripcioHabit.editText?.text.toString()
            val categoria = binding.dropDwnMenuCategoriesHabit.editText?.text.toString()
            val dataLimit = binding.etPlannedHour.hint.toString()
            val dies: Dies = Dies(binding.checkboxDilluns.isChecked,
                                  binding.checkboxDimarts.isChecked,
                                  binding.checkboxDimecres.isChecked,
                                  binding.checkboxDijous.isChecked,
                                  binding.checkboxDivendres.isChecked,
                                  binding.checkboxDissabte.isChecked,
                                  binding.checkboxDiumenge.isChecked,)
            val horari = binding.etPlannedHour.hint.toString()
            val complert = false
            val tipus = true

            val habit = Objectiu(nom,descripcio,categoria,dataLimit,dies,horari,false,null,tipus)

            FirebaseFirestore.getInstance().collection("Usuaris")
                .document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener { result ->
                        val valors: ArrayList<Objectiu> = result.get("llistaObjectius") as ArrayList<Objectiu>
                        valors.add(habit)
                        FirebaseFirestore.getInstance().collection("Usuaris")
                            .document(Firebase.auth.currentUser!!.uid).update( "llistaObjectius",valors)
                }
            findNavController().navigate(R.id.action_createEditHabitFragment_to_tasksFragment2)
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

        with(binding.autoCompleteTextView) {
            setAdapter(adapter)
        }
//--------------------------------------------CALENDARI--------------------------------------------
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val supportFragmentManager = requireActivity().supportFragmentManager

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
//--------------------------------------------END CALENDARI--------------------------------------------

        binding.etPlannedHour.setOnClickListener {

            // create new instance of DatePickerFragment
            val datePickerFragment = DatePickerFragment()

            // we have to implement setFragmentResultListener
            supportFragmentManager.setFragmentResultListener(
                "REQUEST_KEY",
                viewLifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == "REQUEST_KEY") {
                    val hour = bundle.getString("SELECTED_HOUR")
                    binding.etPlannedHour.hint = hour
                }
            }
            TimePickerFragment().show(supportFragmentManager, "timePicker")
        }
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

    class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current time as the default values for the picker
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // Create a new instance of TimePickerDialog and return it
            return TimePickerDialog(
                activity,
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(activity)
            )
        }


        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            // Do something with the time chosen by the user

            val selectedDateBundle = Bundle()
            selectedDateBundle.putString("SELECTED_HOUR", "$hourOfDay:$minute")

            setFragmentResult("REQUEST_KEY", selectedDateBundle)

        }
    }
}