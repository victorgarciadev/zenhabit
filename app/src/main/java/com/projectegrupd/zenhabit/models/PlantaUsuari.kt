package com.projectegrupd.zenhabit.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.ArrayList

/**
 * @author Pablo Morante
 */
class PlantaUsuari(val planta: String, var quantitat: Int) {

    companion object {
        fun dataFirebaseToPlanta(document: DocumentSnapshot): ArrayList<PlantaUsuari> {
            var ret: ArrayList<PlantaUsuari> = ArrayList()

            val d: Map<String, String> = document.data as Map<String, String>
            val a = d.get("llistaPlantes") as List<*>
            for (i in a) {
                i as HashMap<String, String?>
                val planta = i["planta"]
                val quantitat = i["quantitat"].toString()

                val p = PlantaUsuari(planta.toString(), Integer.parseInt(quantitat))

                ret.add(p)
            }

            return ret
        }
    }
}