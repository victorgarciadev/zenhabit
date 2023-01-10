package com.projectegrupd.zenhabit.adapter


import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.projectegrupd.zenhabit.R
import com.projectegrupd.zenhabit.models.Dies
import com.projectegrupd.zenhabit.models.Objectius
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptador del RecyclerView per inserir les dades, mostrarles i les accions de cada cel·la
 * @author Izan Jimenez, Pablo Morante, Víctor García
 */
class AdapterObjectius(
    val listaTasques: List<Objectius>,
    val onClickDelete: (Int) -> Unit,
    val clickListener: (String, String, String, String, Boolean, String?, Dies?) -> Unit
) :
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

            // En cas que la tasca o hàbit tingui una data de finalització anterior a la data actual, la data apareixerà en vermell
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN)
            val plannedDateString = Objectius.dataLimit
            val plannedDate = sdf.parse(plannedDateString)
            val currentDate = Calendar.getInstance()
            val plannedDateCalendar = Calendar.getInstance()
            plannedDateCalendar.setTime(plannedDate)

            if ( currentDate.after(plannedDateCalendar) ) {
                tvHora.setTextColor(ColorStateList.valueOf(Color.parseColor("#FD565E")))
            }

            tvTasca.text = Objectius.nom
            tvHora.text = Objectius.dataLimit

            button.setOnClickListener { onClickDelete(index) }
            bigItem.setOnClickListener {
                clickListener(
                    Objectius.nom,
                    Objectius.dataLimit,
                    Objectius.descripcio,
                    Objectius.categoria,
                    Objectius.tipus,
                    Objectius.horari,
                    Objectius.repetible
                )
            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_list_item, parent, false)

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
     * Estableix els elements de la llista i actualitza la vista de la llista (recycler view).
     *
     * @param items per a la llista
     */
    fun setItems(items: List<Objectius>) {
        listData = items
        notifyDataSetChanged()
    }

    /**
     * Obté l'objecte a la posició especificada de la llista.
     *
     * @param position de l'objecte a la llista
     * @return l'objecte a la posició especificada
     */
    fun getItem(position: Int): Any {
        return listaTasques[position]
    }
}