package com.example.zenhabit.adapter


import android.content.pm.PackageManager
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.models.Tasca


class AdapterTasques(val listaTasques: List<Tasca>, val onClickDelete: (Int) -> Unit, val clickListener: (String,String) -> Unit) :
    RecyclerView.Adapter<AdapterTasques.ViewHolder>() {

    var listData = listaTasques

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTasca: TextView = itemView.findViewById(R.id.txtNomTasca)
        val tvHora: TextView = itemView.findViewById(R.id.txtHora)
        val button = itemView.findViewById<Button>(R.id.eliminar)

        fun bind(tasca: Tasca, index: Int) {
            tvTasca.text = tasca.nom
            tvHora.text = tasca.hora

            button.setOnClickListener { onClickDelete(index) }
            tvTasca.setOnClickListener { clickListener(tasca.nom, tasca.hora) }
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
    fun setItems(items: List<Tasca>){
        listData = items
        notifyDataSetChanged()
    }
}

