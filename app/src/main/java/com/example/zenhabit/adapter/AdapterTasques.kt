package com.example.zenhabit.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.models.Tasca


class AdapterTasques(val listaTasques: ArrayList<Tasca>) :
    RecyclerView.Adapter<AdapterTasques.ViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTasca: TextView = itemView.findViewById(R.id.txtNomTasca)
        val tvHora: TextView = itemView.findViewById(R.id.txtHora)
        val tvEsTascaHabit: TextView = itemView.findViewById(R.id.esTascaHabit)


        fun bind(tasca: String?, hora: String?, tascahabit: String?) {
            tvTasca.text = tasca
            tvHora.text = hora
            tvEsTascaHabit.text = tascahabit
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
        return listaTasques.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            listaTasques[position].nom,
            listaTasques[position].hora,
            listaTasques[position].tascaHabit
        )
    }
}
