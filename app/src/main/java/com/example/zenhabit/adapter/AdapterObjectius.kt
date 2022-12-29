package com.example.zenhabit.adapter


import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat.setBackgroundTintList
import androidx.recyclerview.widget.RecyclerView
import com.example.zenhabit.R
import com.example.zenhabit.models.Dies
import com.example.zenhabit.models.Objectius

/**
 * @author Izan Jimenez, Pablo Morante
 */
class AdapterObjectius(val listaTasques: List<Objectius>, val onClickDelete: (Int) -> Unit, val clickListener: (String, String, String, String, Boolean, String?, Dies?) -> Unit) :
    RecyclerView.Adapter<AdapterObjectius.ViewHolder>() {

    var listData = listaTasques

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTasca: TextView = itemView.findViewById(R.id.txtNomTasca)
        val tvHora: TextView = itemView.findViewById(R.id.txtHora)
        val button = itemView.findViewById<Button>(R.id.eliminar)
        val bigItem: ConstraintLayout = itemView.findViewById(R.id.itemRecyclerView)
        val tipus: View = itemView.findViewById(R.id.view)

        fun bind(Objectius: Objectius, index: Int) {
            if (Objectius.tipus) {
                tipus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FEC442"))) // habit
            } else {
                tipus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00A2E7"))) // tasca
            }
            tvTasca.text = Objectius.nom
            tvHora.text = Objectius.dataLimit

            button.setOnClickListener { onClickDelete(index) }
            bigItem.setOnClickListener { clickListener(Objectius.nom, Objectius.dataLimit, Objectius.descripcio, Objectius.categoria, Objectius.tipus, Objectius.horari, Objectius.repetible) }
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

    /**
     *
     */
    fun setItems(items: List<Objectius>){
        listData = items
        notifyDataSetChanged()
    }

    /**
     *
     */
    fun getItem(position: Int): Any {
        return listaTasques[position]
    }
}

