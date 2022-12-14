package com.example.zenhabit.Fragments

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
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.adapter.AdapterObjectius
import com.example.zenhabit.databinding.FragmentTasksBinding
import com.example.zenhabit.models.Habit
import com.example.zenhabit.models.Objectius
import com.example.zenhabit.models.Repte
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Objects

class TasksFragment : Fragment() {

    private lateinit var _binding: FragmentTasksBinding
    private val binding get() = _binding

    val db = FirebaseFirestore.getInstance()
    private lateinit var data: MutableList<Objectius>
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AdapterObjectius
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var shimmerFrameLayoutObjDiari: ShimmerFrameLayout

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

        return view
    }

    private fun loadData() {

        var ret: ArrayList<Objectius> = ArrayList()

        val docref = db.collection("Usuaris").document(Firebase.auth.currentUser!!.uid)
        docref.get().addOnSuccessListener { document ->
            if (document != null) {

                ret = Objectius.dataFirebaseToObjectius(document)

//shimmer desaparece
                binding.rvTasques.visibility = View.VISIBLE
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.INVISIBLE


                mAdapter = AdapterObjectius(
                    ret,
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
        if (::data.isInitialized) {
            data.removeAt(index)
            mAdapter.setItems(data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



}
