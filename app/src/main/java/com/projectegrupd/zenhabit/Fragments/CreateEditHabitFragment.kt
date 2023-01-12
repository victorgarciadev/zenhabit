package com.projectegrupd.zenhabit.Fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.projectegrupd.zenhabit.R
import com.projectegrupd.zenhabit.databinding.FragmentCreateEditHabitBinding
import com.projectegrupd.zenhabit.models.Dies
import com.projectegrupd.zenhabit.models.Objectius
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Victor García, Izan Jimenez, Txell Llanas, Pablo Morante
 */
class CreateEditHabitFragment : Fragment() {

    // View Binding (Fragment)
    private var _binding: FragmentCreateEditHabitBinding? = null
    private val binding get() = _binding!!
    private var editantHabit: Boolean = false
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (View Binding)
        _binding = FragmentCreateEditHabitBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.create_habit))
        val view = binding.root

        // editar habit
        val name = arguments?.get("Name").toString()
        if (name != "null") {
            editantHabit = true
            binding.nomHabitEdit.setText(name)
            (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.edit_habit))
            binding.btnCrearEditarTasca.isVisible = false
        }
        val categoria = arguments?.get("Categoria").toString()
        if (categoria != "null") {
            binding.dropDwnMenuCategoriesHabit.editText?.setText(categoria)
        }
        val descripcio = arguments?.get("Descripcion").toString()
        if (descripcio != "null") {
            binding.txtInputDescripcioHabit.editText?.setText(descripcio)
        } else {
            binding.txtInputDescripcioHabit.editText?.setText("")
        }
        val arrayDies = arguments?.get("Repeticion") as BooleanArray?
        if (arrayDies != null) {
            val llistaDies: List<Dies> = Dies.fromBooleanArray(arrayDies)
            for (dia in llistaDies) {
                if (dia.dilluns) binding.checkboxDilluns.isChecked = true
                if (dia.dimarts) binding.checkboxDimarts.isChecked = true
                if (dia.dimecres) binding.checkboxDimecres.isChecked = true
                if (dia.dijous) binding.checkboxDijous.isChecked = true
                if (dia.divendres) binding.checkboxDivendres.isChecked = true
                if (dia.dissabte) binding.checkboxDissabte.isChecked = true
                if (dia.diumenge) binding.checkboxDiumenge.isChecked = true
            }
        }

        // binding pels botons 'btn_crearEditarTasca' i 'btn_guardarCrearEditarHabit'
        binding.btnCrearEditarTasca.setOnClickListener {
            findNavController().navigate(R.id.action_createEditHabitFragment_to_createEditTaskFragment)
        }
        binding.btnGuardarCrearEditarHabit.setOnClickListener {
            if (!validateForm()) {
                Log.d("Error", "Error format habit.")
            } else {
                val nom = binding.nomHabitEdit.editableText.toString()
                val descripcio = binding.txtInputDescripcioHabit.editText?.text.toString()
                val categoria = binding.dropDwnMenuCategoriesHabit.editText?.text.toString()
                val dataLimit = binding.etPlannedDate.hint.toString()
                val dies = Dies(
                    binding.checkboxDilluns.isChecked,
                    binding.checkboxDimarts.isChecked,
                    binding.checkboxDimecres.isChecked,
                    binding.checkboxDijous.isChecked,
                    binding.checkboxDivendres.isChecked,
                    binding.checkboxDissabte.isChecked,
                    binding.checkboxDiumenge.isChecked,
                )
                val horari = binding.etPlannedHour.hint.toString()
                val tipus = true
                val habit =
                    Objectius(
                        nom,
                        descripcio,
                        categoria,
                        dataLimit,
                        dies,
                        horari,
                        false,
                        null,
                        tipus
                    )
                db.collection("Usuaris")
                    .document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener { result ->
                        val valors = Objectius.dataFirebaseToObjectius(result)
                        if (!editantHabit) {
                            valors.add(habit)
                        } else {
                            var index = 0
                            for (valor in valors) {
                                if (valor.tipus && valor.nom == arguments?.get("Name")
                                        .toString() && valor.dataLimit == arguments?.get("Fecha")
                                        .toString() && valor.horari == arguments?.get("Hora")
                                        .toString()
                                ) {
                                    valors.set(index, habit)
                                }
                                index++
                            }
                        }
                        db.collection("Usuaris")
                            .document(Firebase.auth.currentUser!!.uid)
                            .update("llistaObjectius", valors)
                            .addOnCompleteListener {
                                if (!editantHabit) {
                                    Toast(activity).showCustomToast(getString(R.string.toast_habit_creat))
                                } else {
                                    Toast(activity).showCustomToast(getString(R.string.toast_habit_update))
                                }
                                findNavController().navigate(R.id.action_createEditHabitFragment_to_tasksFragment2)
                            }
                    }
            }
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
            initialMonth = c.get(Calendar.MONTH) +1
        var initialDay = -1
        if (initialDay == -1)
            initialDay = c.get(Calendar.DAY_OF_MONTH)
        val fecha = arguments?.get("Fecha").toString()
        if (fecha != "null") {
            binding.etPlannedDate.hint = "$fecha"
        } else {
            binding.etPlannedDate.hint = "$initialDay-$initialMonth-$initialYear"
        }
        val hora = arguments?.get("Hora").toString()
        if (hora != "null") {
            binding.etPlannedHour.hint = "$hora"
        }
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


    /**
     * Mostra un dialog de Calendari i agafa la data seleccionada
     * @author Izan Jimenez
     */
    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
        private val calendar = Calendar.getInstance()

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // default date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

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

    /**
     * Mostra un dialog de temps i agafa la hora seleccionada
     * @author Izan Jimenez
     */
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

    /**
     * Valida el formulari per assegurar-se que s'omplen tots els camps obligatoris i que les dades són vàlides.
     *
     * @return true si el formulari és vàlid, false en cas contrari
     * @author Pablo Morante
     */
    private fun validateForm(): Boolean {
        var valid = true
        if (!binding.nomHabitEdit.text.isNullOrBlank()) {
            if (binding.nomHabitEdit.text?.length!! > 25) {
                binding.nomHabitEdit.error = getString(R.string.formulari_nom_objectiu)
                valid = false
            } else {
                binding.nomHabitEdit.error = null
            }
        } else {
            binding.nomHabitEdit.error = getString(R.string.formulari_nom_objectiu2)
            valid = false
        }
        if (binding.dropDwnMenuCategoriesHabit.editText?.text.isNullOrBlank()) {
            binding.dropDwnMenuCategoriesHabit.error = getString(R.string.formulari_categoria)
            valid = false
        } else {
            binding.dropDwnMenuCategoriesHabit.error = null
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN)
        val plannedDateString = binding.etPlannedDate.hint.toString()
        val plannedDate = sdf.parse(plannedDateString)
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DATE, -1)
        val plannedDateCalendar = Calendar.getInstance()
        plannedDateCalendar.setTime(plannedDate)
        if (plannedDateCalendar.before(currentDate)) {
            binding.etPlannedDate.error = getString(R.string.formulari_date)
            valid = false
        } else {
            binding.etPlannedDate.error = null
        }
        var countTemp = 0
        if (binding.checkboxDilluns.isChecked) countTemp++
        if (binding.checkboxDimarts.isChecked) countTemp++
        if (binding.checkboxDimecres.isChecked) countTemp++
        if (binding.checkboxDijous.isChecked) countTemp++
        if (binding.checkboxDivendres.isChecked) countTemp++
        if (binding.checkboxDissabte.isChecked) countTemp++
        if (binding.checkboxDiumenge.isChecked) countTemp++

        if (countTemp == 0) {
            Toast(activity).showCustomToast(getString(R.string.toast_formulari_habit))
            valid = false
        }

        return valid
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