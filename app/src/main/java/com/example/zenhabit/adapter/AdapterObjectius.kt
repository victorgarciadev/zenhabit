package com.example.zenhabit.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.models.Objectius


class AdapterObjectius(val listaTasques: List<Objectius>, val onClickDelete: (Int) -> Unit, val clickListener: (String, String) -> Unit) :
    RecyclerView.Adapter<AdapterObjectius.ViewHolder>() {

    var listData = listaTasques

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTasca: TextView = itemView.findViewById(R.id.txtNomTasca)
        val tvHora: TextView = itemView.findViewById(R.id.txtHora)
        val button = itemView.findViewById<Button>(R.id.eliminar)
        val bigItem: ConstraintLayout = itemView.findViewById(R.id.itemRecyclerView)

        fun bind(Objectius: Objectius, index: Int) {
            tvTasca.text = Objectius.nom
            tvHora.text = Objectius.horari

            button.setOnClickListener { onClickDelete(index) }
            bigItem.setOnClickListener { clickListener(Objectius.nom, Objectius.horari.toString()) }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_class, parent, false)

        return ViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return listData.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            listData[position],
            position
        )
    }
    fun setItems(items: List<Objectius>){
        listData = items
        notifyDataSetChanged()
    }
}

