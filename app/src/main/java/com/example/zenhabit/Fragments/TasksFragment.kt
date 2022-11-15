package com.example.zenhabit.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.adapter.AdapterTasques
import com.example.zenhabit.databinding.FragmentHomeBinding
import com.example.zenhabit.databinding.FragmentTasksBinding
import com.example.zenhabit.models.Tasca

/**
 * A simple [Fragment] subclass.
 * Use the [TasksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TasksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var _binding: FragmentTasksBinding
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val view = binding.root
        _binding.addTasc.setOnClickListener{
            findNavController().navigate(R.id.action_tasksFragment2_to_createEditTaskFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //RECICLERVIEW

//        val recyclerView: RecyclerView = bin.rvTasques
//        recyclerView.setHasFixedSize(true)
//        recyclerView?.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = AdapterTasques(dataInicialize())
//        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity)



//        val recyclerView = bin.rvTasques
//        val manager = LinearLayoutManager(this.context)
//        recyclerView.setLayoutManager(manager)
//        recyclerView.setHasFixedSize(true)
//        val adapter = AdapterTasques(dataInicialize())
//        recyclerView.setAdapter(adapter)



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

}