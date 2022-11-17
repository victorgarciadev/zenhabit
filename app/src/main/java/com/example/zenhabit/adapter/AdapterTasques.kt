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

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick (position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvTasca: TextView = itemView.findViewById(R.id.txtNomTasca)
        val tvHora: TextView = itemView.findViewById(R.id.txtHora)


        fun bind(tasca: String?, hora: String?) {
            tvTasca.text = tasca
            tvHora.text = hora
        }

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_class, parent, false)

        return ViewHolder(view,mListener)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return listaTasques.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            listaTasques[position].nom,
            listaTasques[position].hora
        )
    }
}
