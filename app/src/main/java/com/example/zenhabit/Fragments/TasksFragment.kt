package com.example.zenhabit.Fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.adapter.AdapterObjectius
import com.example.zenhabit.databinding.FragmentTasksBinding
import com.example.zenhabit.models.Dies
import com.example.zenhabit.models.Objectius
import com.example.zenhabit.models.PlantaUsuari
import com.example.zenhabit.databinding.ObjDiariAyoutBinding
import com.example.zenhabit.models.*
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List


/**
 * @author Victor García, Izan Jimenez, Txell Llanas, Pablo Morante
 */
class TasksFragment : Fragment() {

    private lateinit var _binding: FragmentTasksBinding
    private val binding get() = _binding

    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AdapterObjectius
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var shimmerFrameLayoutObjDiari: ShimmerFrameLayout
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = getString(R.string.tasks_title)
        val view = binding.root
        _binding.addTasc.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment2_to_createEditTaskFragment)
        }


        //ResptesDiaris shimmer
        shimmerFrameLayoutObjDiari = binding.shimmerObjDiari
        shimmerFrameLayoutObjDiari.startShimmer()

        //obtenir els reptes diaris
        FirebaseFirestore.getInstance().collection("Usuaris")
            .document(auth.currentUser!!.uid).get().addOnSuccessListener { result ->
                val llista = RepteUsuari.dataFirebasetoReptes(result)
                Log.d("PRUEBA", llista[1].aconseguit.toString())
                Log.d("PRUEBA", llista.size.toString())


//nomes agafem els 3 primers
                for (i in 1..3) {
                    //val j = (0..llista.size).random()

                    if (i == 1) {
                        binding.Obj1.textViewDesc.text = llista[0].repte.descripcio
                        binding.Obj1.titolRepte.text = llista[0].repte.titol
                        binding.Obj1.checkboxDone.isChecked = llista[0].aconseguit
                    }
                    if (i == 2) {
                        binding.Obj2.textViewDesc.text = llista[1].repte.descripcio
                        binding.Obj2.titolRepte.text = llista[1].repte.titol
                        binding.Obj2.checkboxDone.isChecked = llista[1].aconseguit
                    }
                    if (i == 3) {
                        binding.Obj3.textViewDesc.text = llista[2].repte.descripcio
                        binding.Obj3.titolRepte.text = llista[2].repte.titol
                        binding.Obj3.checkboxDone.isChecked = llista[2].aconseguit
                    }
                }
            }


        //Mostrar reptes
        Handler(Looper.getMainLooper()).postDelayed({
            binding.listObjDiari.visibility = View.VISIBLE
            shimmerFrameLayoutObjDiari.stopShimmer()
            shimmerFrameLayoutObjDiari.visibility = View.INVISIBLE
            resetReptesDiaris()  // comprova si ja ha pasat un dia des de la actualització de reptes diaris

        }, 1000)

        //canviar de color els checkboxes
        binding.Obj1.checkboxDone.setOnCheckedChangeListener { buttonView, isChecked ->
            checkChecked(isChecked, binding.Obj1, 1)
        }
        binding.Obj2.checkboxDone.setOnCheckedChangeListener { buttonView, isChecked ->
            checkChecked(isChecked, binding.Obj2, 2)
        }
        binding.Obj3.checkboxDone.setOnCheckedChangeListener { buttonView, isChecked ->
            checkChecked(isChecked, binding.Obj3, 3)
        }


        //----------------NEW RECYCLERVIEW-----------------
        //cargar shimmer
        mRecyclerView = binding.rvTasques
        val mLayoutManager = LinearLayoutManager(this.getActivity())
        shimmerFrameLayout = binding.shimmer
        shimmerFrameLayout.startShimmer()
        //cargar recyclerview
        mRecyclerView.layoutManager = mLayoutManager
        loadData()

        // chart
        pieChart = binding.pieChart
        preparePieData()

        return view
    }




    /***
     * Funcio per canviar de color els checkboxes una vegada marcats com complerts i desmarcant-los.
     * Ho canvia també a la BBDD
     * @author Izan Jimenez, Víctor García
     */
    private fun checkChecked(
        isChecked: Boolean,
        objDiariAyoutBinding: ObjDiariAyoutBinding,
        obj: Int
    ) {
        if (isChecked) {
            objDiariAyoutBinding.repteDiariRow1.background.setTint(Color.parseColor("#2E4751"))
            objDiariAyoutBinding.textViewDesc.setTextColor(Color.parseColor("#6B7477"))
            objDiariAyoutBinding.checkboxDone.setButtonTintList(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#6B7477"
                    )
                )
            )
            objDiariAyoutBinding.titolRepte.setTextColor(Color.parseColor("#6B7477"))
            FirebaseFirestore.getInstance().collection("Usuaris")
                .document(auth.currentUser!!.uid).get().addOnSuccessListener { result ->
                    val llista = RepteUsuari.dataFirebasetoReptes(result)
                    for (i in 0..llista.size - 1) {
                        if (llista[i].repte.titol.equals(objDiariAyoutBinding.titolRepte.text)) {
                            llista[i].aconseguit = true
                        }
                    }
                    FirebaseFirestore.getInstance().collection("Usuaris")
                        .document(auth.currentUser!!.uid).update("llistaReptes", llista)
                }
        } else {
            objDiariAyoutBinding.repteDiariRow1.background.setTint(Color.parseColor("#3dd598"))
            objDiariAyoutBinding.textViewDesc.setTextColor(Color.parseColor("#2E2E2E"))
            objDiariAyoutBinding.checkboxDone.setButtonTintList(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#2E2E2E"
                    )
                )
            )
            objDiariAyoutBinding.titolRepte.setTextColor(Color.parseColor("#2E2E2E"))
            FirebaseFirestore.getInstance().collection("Usuaris")
                .document(auth.currentUser!!.uid).get().addOnSuccessListener { result ->
                    val llista = RepteUsuari.dataFirebasetoReptes(result)
                    for (i in 0..llista.size - 1) {
                        if (llista[i].repte.titol.equals(objDiariAyoutBinding.titolRepte.text)) {
                            llista[i].aconseguit = false
                        }
                    }
                    FirebaseFirestore.getInstance().collection("Usuaris")
                        .document(auth.currentUser!!.uid).update("llistaReptes", llista)
                }
        }
    }

    /***
     * Reseteja els reptes diaris cada 24h
     * * @author Izan Jimenez
     */
    private fun resetReptesDiaris() {
        val actualDay = Calendar.getInstance().time// dia i hora actual

        FirebaseFirestore.getInstance().collection("Verificacions")
            .document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val lastDay = result.getTimestamp("lastDate")!!
                    .toDate() // dia i hora que es va llençar l'última notificació
                val difference: Long = actualDay.time - lastDay.time
                val seconds = difference / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                Log.d("DAYS", " $days")
                if (days >= 1) { // si ja ha passat un dia canvia els colors i isChecked dels checkboxes
                    checkChecked(false, binding.Obj1, 1)
                    binding.Obj3.checkboxDone.isChecked = false
                    checkChecked(false, binding.Obj1, 2)
                    binding.Obj3.checkboxDone.isChecked = false
                    checkChecked(false, binding.Obj1, 3)
                    binding.Obj3.checkboxDone.isChecked = false


                }
            }


    }

    /**
     * Omple el recyclerView amb les dades que hi ha a la BBDD si aquesta esta disponible
     * @author Izan Jimenez
     */
    private fun loadData() {

        var ret: ArrayList<Objectius>

        val docref = db.collection("Usuaris").document(Firebase.auth.currentUser!!.uid)
        docref.get().addOnSuccessListener { document ->
            if (document != null) {
                ret = Objectius.dataFirebaseToObjectius(document)
                if (ret.isEmpty()) {
                    mRecyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    mRecyclerView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }

                val filteredList = ret.filter { !it.complert }

                if (filteredList.isEmpty()) {
                    mRecyclerView.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    mRecyclerView.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }

                //shimmer desaparece
                binding.rvTasques.visibility = View.VISIBLE
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.INVISIBLE


                setRecyclerView(filteredList)

            } else {
                //ERROR
                Log.d("TAG", "DocumentSnapshot data: NO SE ENCONTRO EL DOCUMENTO")

            }

        }.addOnFailureListener { exception ->
            Log.d("TAG", "ERROR AL OBTENER ${exception}")

        }
    }

    /**
     * Estableix la RecyclerView amb la llista d'Objectius donada.
     *
     * @param objectiusList llista d'Objectius per mostrar al RecyclerView
     * @author Izan Jimenez, Pablo Morante
     */
    private fun setRecyclerView(objectiusList: List<Objectius>) {
        mAdapter = AdapterObjectius(
            objectiusList,
            { index -> deleteItem(index) },
            { nom, fecha, descripcio, categoria, tipus, hora, repeticion ->
                sendItem(
                    nom,
                    fecha,
                    descripcio,
                    categoria,
                    tipus,
                    hora,
                    repeticion
                )
            })

        mRecyclerView.setAdapter(mAdapter)
    }

    /**
     * @author Pablo Morante
     */
    private fun sendItem(
        nom: String,
        fecha: String,
        descripcio: String,
        categoria: String,
        tipus: Boolean,
        hora: String?,
        repeticion: Dies?
    ) {
        if (tipus) {
            val action =
                TasksFragmentDirections.actionTasksFragment2ToCreateEditHabitFragment(
                    nom,
                    hora,
                    fecha,
                    descripcio,
                    categoria,
                    repeticion?.toBooleanArray()
                )
            findNavController().navigate(action)
        } else {
            val action =
                TasksFragmentDirections.actionTasksFragment2ToCreateEditTaskFragment(
                    nom,
                    fecha,
                    descripcio,
                    categoria
                )
            findNavController().navigate(action)
        }
    }

    /**
     * Elimina la cel·la complerta de la BBDD i del recyclerView
     * @author Izan Jimenez
     */
    private fun deleteItem(index: Int) {
        var objectiuSeleccionat = mAdapter.getItem(index) as Objectius
        db.collection("Usuaris").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val objectius = Objectius.dataFirebaseToObjectius(result)
                var index = 0
                for (objectiu in objectius) {
                    if (objectiu.nom == objectiuSeleccionat.nom && objectiu.categoria == objectiuSeleccionat.categoria && objectiu.dataLimit == objectiuSeleccionat.dataLimit && objectiu.horari == objectiuSeleccionat.horari) {
                        val objectiuLlista = objectiu
                        objectiuLlista.complert = true
                        objectius.set(index, objectiuLlista)
                        val filteredList = objectius.filter { !it.complert }
                        setRecyclerView(filteredList)
                        break
                    }
                    index++
                }
                db.collection("Usuaris")
                    .document(Firebase.auth.currentUser!!.uid).update("llistaObjectius", objectius)

                val numRandom = (1..9).random()

                db.collection("Plantes").document(numRandom.toString()).get()
                    .addOnSuccessListener { secondResult ->
                        val plantesUsuari = PlantaUsuari.dataFirebaseToPlanta(result)
                        val canvisPlanta = plantesUsuari.get(numRandom - 1)
                        var quantitat = canvisPlanta.quantitat
                        quantitat++
                        canvisPlanta.quantitat = quantitat
                        plantesUsuari.set(numRandom - 1, canvisPlanta)
                        db.collection("Usuaris").document(Firebase.auth.currentUser!!.uid)
                            .update("llistaPlantes", plantesUsuari)
                            .addOnSuccessListener {
                                val language = Locale.getDefault().language
                                if (language == "en") {
                                    Toast(activity).showCustomToast(
                                        getString(R.string.toast_objectiu_completat) + " " + secondResult.get(
                                            "name"
                                        ).toString()
                                    )
                                } else if (language == "es") {
                                    Toast(activity).showCustomToast(
                                        getString(R.string.toast_objectiu_completat) + " " + secondResult.get(
                                            "nombre"
                                        ).toString()
                                    )
                                } else {
                                    Toast(activity).showCustomToast(
                                        getString(R.string.toast_objectiu_completat) + " " + secondResult.get(
                                            "nom"
                                        ).toString()
                                    )
                                }
                            }
                    }
            }
    }

    /**
     * Prepara les dades del gràfic i les configura.
     *
     * @author Pablo Morante, Víctor García
     */
    private fun preparePieData() {
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 5f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.35f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.TRANSPARENT)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(75f)
        pieChart.setTransparentCircleRadius(33f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(false)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(800, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        var perCC = 0f
        runBlocking {
            perCC = getDataFromFirestore()
        }
        val perNC: Float = 100 - (perCC * 100)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(perNC))
        entries.add(PieEntry(perCC * 100))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 0f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to listwwssw
        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#FD565E"))
        colors.add(Color.parseColor("#3dd598"))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(0f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.highlightValues(null)

        // loading chart
        pieChart.invalidate()
    }

    /**
     * Recupera dades de Firestore i calcula el percentatge d'objectius completats.
     *
     * @return el percentatge d'objectius completats com a float
     */
    suspend fun getDataFromFirestore(): Float {
        var perT = 0f
        var total = 0f
        var oComplets = 0f
        var objectius: ArrayList<Objectius> = ArrayList()

        val result = db.collection("Usuaris")
            .document(Firebase.auth.currentUser!!.uid).get().await()

        if (result != null) {
            objectius = Objectius.dataFirebaseToObjectius(result)
            total = (objectius.size).toFloat()
            for (objectiu in objectius) {
                if (objectiu.complert) {
                    oComplets++
                }
            }
            perT = (oComplets / total)
        }

        return perT
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
