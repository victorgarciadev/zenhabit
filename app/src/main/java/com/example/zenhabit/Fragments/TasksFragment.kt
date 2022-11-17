package com.example.zenhabit.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.adapter.AdapterTasques
import com.example.zenhabit.databinding.FragmentHomeBinding
import com.example.zenhabit.databinding.FragmentTasksBinding
import com.example.zenhabit.databinding.ShimmerLayoutBinding
import com.example.zenhabit.models.Tasca
import com.facebook.shimmer.ShimmerFrameLayout

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

    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setTitle("Tasques")
        val view = binding.root
        _binding.addTasc.setOnClickListener {
            findNavController().navigate(R.id.action_tasksFragment2_to_createEditTaskFragment)
        }


        // RecyclerView
        var mRecyclerView = binding.rvTasques;
        var mLayoutManager = LinearLayoutManager(this.getActivity());

        shimmerFrameLayout = binding.shimmer
        shimmerFrameLayout.startShimmer()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.rvTasques.visibility = View.VISIBLE
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.INVISIBLE


        }, 5000)

        mRecyclerView.setLayoutManager(mLayoutManager);

        var mAdapter = AdapterTasques(dataInicialize());
        mRecyclerView.setAdapter(mAdapter);
        return view;






        return view
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

}