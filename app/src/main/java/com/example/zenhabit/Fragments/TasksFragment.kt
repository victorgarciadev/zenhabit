package com.example.zenhabit.Fragments

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
import com.example.zenhabit.models.Habit
import com.example.zenhabit.models.Objectius
import com.example.zenhabit.models.Repte
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document
import java.util.Objects
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TasksFragment : Fragment() {

    private lateinit var _binding: FragmentTasksBinding
    private val binding get() = _binding

    val db = FirebaseFirestore.getInstance()
    private lateinit var data: MutableList<Objectius>
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AdapterObjectius
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var shimmerFrameLayoutObjDiari: ShimmerFrameLayout
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle(getString(R.string.tasks_title))
        val view = binding.root
        _binding.addTasc.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment2_to_createEditTaskFragment)
        }


        //ResptesDiaris shimmer
        shimmerFrameLayoutObjDiari = binding.shimmerObjDiari
        shimmerFrameLayoutObjDiari.startShimmer()

        //obtenir els reptes diaris
        for (i in 1..3) {
            val document = FirebaseFirestore.getInstance().collection("Reptes")
                .document(i.toString()).get()
                .addOnSuccessListener { result ->
                    val titol = result.get("titol")
                    val desc = result.get("descripcio")
                    if (i == 1) {
                        binding.Obj1.textViewDesc.text = desc.toString()
                        binding.Obj1.titolRepte.text = titol.toString()
                    }
                    if (i == 2) {
                        binding.Obj2.textViewDesc.text = desc.toString()
                        binding.Obj2.titolRepte.text = titol.toString()
                    }
                    if (i == 3) {
                        binding.Obj3.textViewDesc.text = desc.toString()
                        binding.Obj3.titolRepte.text = titol.toString()
                    }
                }
        }

        //Mostrar
        Handler(Looper.getMainLooper()).postDelayed({
            binding.listObjDiari.visibility = View.VISIBLE
            shimmerFrameLayoutObjDiari.stopShimmer()
            shimmerFrameLayoutObjDiari.visibility = View.INVISIBLE
        }, 1000)



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

    private fun loadData() {

        var ret: ArrayList<Objectius> = ArrayList()

        val docref = db.collection("Usuaris").document(Firebase.auth.currentUser!!.uid)
        docref.get().addOnSuccessListener { document ->
            if (document != null) {

                ret = Objectius.dataFirebaseToObjectius(document)
                val filteredList = ret.filter { !it.complert }

                //shimmer desaparece
                binding.rvTasques.visibility = View.VISIBLE
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.INVISIBLE


                mAdapter = AdapterObjectius(
                    filteredList,
                    { index -> deleteItem(index) },
                    { nom, hora -> sendItem(nom, hora) });

                mRecyclerView.setAdapter(mAdapter)

            } else {
                //ERROR
                Log.d("TAG", "DocumentSnapshot data: NO SE ENCONTRO EL DOCUMENTO")

            }

        }.addOnFailureListener { exception ->
            Log.d("TAG", "ERROR AL OBTENER ${exception}")

        }
    }
    private fun sendItem(nom: String, hora: String) {
        val action =
            TasksFragmentDirections.actionTasksFragment2ToCreateEditTaskFragment(nom, hora)
        findNavController().navigate(action)
    }

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
                        mAdapter.setItems(filteredList)
                        break
                    }
                    index++
                }
                FirebaseFirestore.getInstance().collection("Usuaris")
                    .document(Firebase.auth.currentUser!!.uid).update( "llistaObjectius",objectius)
                    .addOnCompleteListener {
                        Toast(activity).showCustomToast(getString(R.string.toast_habit_creat))
                    }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun preparePieData() {
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(1f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.TRANSPARENT)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(80f)
        pieChart.setTransparentCircleRadius(33f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        var perCC = 0f
        runBlocking {
            perCC = getDataFromFirestore()
        }
        val perNC: Float = 100 - (perCC*100)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(perCC*100))
        entries.add(PieEntry(perNC))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to listwwssw
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.red))
        colors.add(resources.getColor(R.color.yellow))

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

    suspend fun getDataFromFirestore(): Float {
        var perT: Float = 0f
        var total: Float = 0f
        var oComplets: Float = 0f
        var objectius: ArrayList<Objectius> = ArrayList()

        val result = FirebaseFirestore.getInstance().collection("Usuaris")
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
