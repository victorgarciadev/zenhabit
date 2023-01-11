package com.projectegrupd.zenhabit.Fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.projectegrupd.zenhabit.R
import com.projectegrupd.zenhabit.databinding.FragmentCreateEditTaskBinding
import com.projectegrupd.zenhabit.models.Objectius
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Victor García, Izan Jimenez, Txell Llanas, Pablo Morante
 */
class CreateEditTaskFragment : Fragment() {

    // View Binding (Fragment)
    private var _binding: FragmentCreateEditTaskBinding? = null
    private val binding get() = _binding!!
    private var editantTasca: Boolean = false
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment (View Binding)
        _binding = FragmentCreateEditTaskBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.create_task))
        val view = binding.root

        val name = arguments?.get("Name").toString()
        if (name != "null") {
            editantTasca = true
            binding.nomTascaEdit.setText(name)
            (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.edit_task))
            binding.btnCrearEditarHabit.isVisible = false
        }
        val categoria = arguments?.get("categoria").toString()
        if (categoria != "null") {
            checkLanguageCategoria(categoria)
        }
        val descripcio = arguments?.get("descripcio").toString()
        if (descripcio != "null") {
            binding.txtInputDescripcioTasca.editText?.setText(descripcio)
        } else {
            binding.txtInputDescripcioTasca.editText?.setText("")
        }
        // binding pels botons 'btn_crearEditarHabit' i 'btn_guardarCrearEditarHabit'
        binding.btnCrearEditarHabit.setOnClickListener {
            findNavController().navigate(R.id.action_createEditTaskFragment_to_createEditHabitFragment)
        }
        binding.btnGuardarCrearEditarTasca.setOnClickListener {
            if (!validateForm()) {
                Log.d("Error", "Error format tasca.")
            } else {
                val nom = binding.nomTascaEdit.editableText.toString()
                val descripcio = binding.txtInputDescripcioTasca.editText?.text.toString()
                val categoria = binding.dropDwnMenuCategoriesTasca.editText?.text.toString()
                val dataLimit = binding.etPlannedDate.hint.toString()
                val tipus = false

                val tasca =
                    Objectius(nom, descripcio, categoria, dataLimit, null, null, false, null, tipus)

                db.collection("Usuaris")
                    .document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener { result ->
                        val valors = Objectius.dataFirebaseToObjectius(result)
                        if (!editantTasca) {
                            valors.add(tasca)
                        } else {
                            var index = 0
                            for (valor in valors) {
                                if (!valor.tipus && valor.nom == arguments?.get("Name")
                                        .toString() && valor.dataLimit == arguments?.get("time")
                                        .toString()
                                ) {
                                    valors.set(index, tasca)
                                }
                                index++
                            }
                        }
                        db.collection("Usuaris")
                            .document(Firebase.auth.currentUser!!.uid)
                            .update("llistaObjectius", valors)
                            .addOnSuccessListener {
                                if (!editantTasca) {
                                    Toast(activity).showCustomToast(getString(R.string.toast_tasca_creada))
                                } else {
                                    Toast(activity).showCustomToast(getString(R.string.toast_tasca_update))
                                }
                                findNavController().navigate(R.id.action_createEditTaskFragment_to_tasksFragment2)
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

        var initialYear = -1
        if (initialYear == -1) {
            initialYear = year
        }
        var initialMonth = -1
        if (initialMonth == -1) {
            initialMonth = c.get(Calendar.MONTH) +1
        }
        var initialDay = -1
        if (initialDay == -1) {
            initialDay = c.get(Calendar.DAY_OF_MONTH)
        }
        val hora = arguments?.get("time").toString()
        if (hora != "null") {
            binding.etPlannedDate.hint = "$hora"
        } else {
            binding.etPlannedDate.hint = "$initialDay-$initialMonth-$initialYear"
        }
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

    /**
     *  Mostra un dialog de Calendari i agafa la data seleccionada
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
     * Comprova l'idioma del dispositiu i estableix el TextView per a la categoria de la tasca en l'idioma corresponent.
     *
     * @param categoria de la tasca
     * @author Pablo Morante
     */
    private fun checkLanguageCategoria(categoria: String) {
        val language = Locale.getDefault().language
        if (language == "es") {
            if (categoria == "Aprendizaje" || categoria == "Producticidad" || categoria == "Salud") {
                binding.dropDwnMenuCategoriesTasca.editText?.setText(categoria)
            } else {
                if (categoria == "Learning" || categoria == "Aprenentatge") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Aprendizaje")
                } else if (categoria == "Productivity" || categoria == "Productivitat") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Productividad")
                } else if (categoria == "Health" || categoria == "Salut") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Salud")
                }
            }
        } else if (language == "en") {
            if (categoria == "Learning" || categoria == "Productivity" || categoria == "Health") {
                binding.dropDwnMenuCategoriesTasca.editText?.setText(categoria)
            } else {
                if (categoria == "Aprendizaje" || categoria == "Aprenentatge") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Learning")
                } else if (categoria == "Productividad" || categoria == "Productivitat") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Productivity")
                } else if (categoria == "Salud" || categoria == "Salut") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Health")
                }
            }
        } else {
            if (categoria == "Aprenentatge" || categoria == "Productivitat" || categoria == "Salut") {
                binding.dropDwnMenuCategoriesTasca.editText?.setText(categoria)
            } else {
                if (categoria == "Aprendizaje" || categoria == "Learning") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Aprenentatge")
                } else if (categoria == "Productividad" || categoria == "Productivity") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Productivitat")
                } else if (categoria == "Salud" || categoria == "Health") {
                    binding.dropDwnMenuCategoriesTasca.editText?.setText("Salut")
                }
            }
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
        if (!binding.nomTascaEdit.text.isNullOrBlank()) {
            if (binding.nomTascaEdit.text?.length!! > 25) {
                binding.nomTascaEdit.error = getString(R.string.formulari_nom_objectiu)
                valid = false
            } else {
                binding.nomTascaEdit.error = null
            }
        } else {
            binding.nomTascaEdit.error = getString(R.string.formulari_nom_objectiu2)
            valid = false
        }
        if (binding.dropDwnMenuCategoriesTasca.editText?.text.isNullOrBlank()) {
            binding.dropDwnMenuCategoriesTasca.error = getString(R.string.formulari_categoria)
            valid = false
        } else {
            binding.dropDwnMenuCategoriesTasca.error = null
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN)
        val plannedDateString = binding.etPlannedDate.hint.toString()
        val plannedDate = sdf.parse(plannedDateString)
        val currentDate = Calendar.getInstance()
        val plannedDateCalendar = Calendar.getInstance()
        plannedDateCalendar.setTime(plannedDate)
        if (plannedDateCalendar.before(currentDate)) {
            binding.etPlannedDate.error = getString(R.string.formulari_date)
            valid = false
        } else {
            binding.etPlannedDate.error = null
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