package com.example.zenhabit.Fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zenhabit.R
import com.example.zenhabit.adapter.AdapterTasques
import com.example.zenhabit.databinding.FragmentTasksBinding
import com.example.zenhabit.models.Habit
import com.example.zenhabit.models.Objectiu
import com.example.zenhabit.models.Repte
import com.example.zenhabit.models.Tasca
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Objects

class TasksFragment : Fragment() {

    private lateinit var _binding: FragmentTasksBinding
    private val binding get() = _binding

    private lateinit var data: MutableList<Tasca>
    private lateinit var mAdapter: AdapterTasques
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




//      RecyclerView shimmer
        val mRecyclerView = binding.rvTasques
        val mLayoutManager = LinearLayoutManager(this.getActivity())

        shimmerFrameLayout = binding.shimmer
        shimmerFrameLayout.startShimmer()

//        cargar
        Handler(Looper.getMainLooper()).postDelayed({
            binding.rvTasques.visibility = View.VISIBLE
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.INVISIBLE
        }, 1000)


//        cargar recyclerview
        mRecyclerView.setLayoutManager(mLayoutManager);
        data = dataInicialize() as MutableList<Tasca>
        mAdapter = AdapterTasques(
            data,
            { index -> deleteItem(index) },
            { nom, hora -> sendItem(nom, hora) });
        mRecyclerView.setAdapter(mAdapter)

        // chart
        pieChart = binding.pieChart
        preparePieData()


        return view
    }

    private fun sendItem(nom: String, hora: String) {
        val action =
            TasksFragmentDirections.actionTasksFragment2ToCreateEditTaskFragment(nom, hora)
        findNavController().navigate(action)
    }

    private fun deleteItem(index: Int) {
        if (::data.isInitialized) {
            data.removeAt(index)
            mAdapter.setItems(data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun dataInicialize(): ArrayList<Tasca> {

        val tasquesList: ArrayList<Tasca> = ArrayList()


        tasquesList.add(Tasca("Llegir", "10:00", "Tasca"))
        tasquesList.add(Tasca("Caminar", "12:00", "Habit"))
        tasquesList.add(Tasca("Comprar", "22:00", "Tasca"))
        tasquesList.add(Tasca("Llegir", "10:00", "Tasca"))
        tasquesList.add(Tasca("Caminar", "12:00", "Habit"))
        tasquesList.add(Tasca("Comprar", "22:00", "Tasca"))
        tasquesList.add(Tasca("Llegir", "10:00", "Tasca"))
        tasquesList.add(Tasca("Caminar", "12:00", "Habit"))
        tasquesList.add(Tasca("Comprar", "22:00", "Tasca"))
        tasquesList.add(Tasca("Llegir", "10:00", "Tasca"))
        tasquesList.add(Tasca("Caminar", "12:00", "Habit"))
        tasquesList.add(Tasca("Comprar", "22:00", "Tasca"))
        tasquesList.add(Tasca("Llegir", "10:00", "Tasca"))
        tasquesList.add(Tasca("Caminar", "12:00", "Habit"))
        tasquesList.add(Tasca("Comprar", "22:00", "Tasca"))

        return tasquesList

    }

    private fun preparePieData() {
// on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.TRANSPARENT)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(30f)
        pieChart.setTransparentCircleRadius(34f)

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

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        //val perFet: Float = getDataFromDatabase()
        //val perNoFet: Float = 100 - perFet
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(80f))
        entries.add(PieEntry(20f))
        //entries.add(PieEntry(80f))
        //entries.add(PieEntry(20f))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 7f

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

        // loading chart
        pieChart.invalidate()
    }

    private fun getDataFromDatabase(): Float {
        var fetes = 0
        var noFetes = 0
        FirebaseFirestore.getInstance().collection("Usuaris")
            .document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener { result ->
                val objectius = result.get("llistaObjectius") as ArrayList<Objectiu>
                objectius.forEach {
                    if (it.complert) {
                        fetes++
                    } else {
                        noFetes++
                    }
                }
            }
        val total = fetes + noFetes
        val perFet = fetes / total as Float
        return perFet
    }

}
