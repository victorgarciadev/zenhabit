package com.example.zenhabit.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

/**
 * @author Pablo Morante
 */
class RepteUsuari(titol: String, descripcio: String, aconseguit: Boolean, mostrant: Boolean) {
    var titol = titol
    var descripcio = descripcio
    var aconseguit = aconseguit
    var mostrant = mostrant

    companion object {

        fun dataFirebasetoReptes(document: DocumentSnapshot): ArrayList<RepteUsuari> {

            var ret: ArrayList<RepteUsuari> = ArrayList()

            val d: Map<String, String> = document.data as Map<String, String>
            val a = d.get("llistaReptes") as List<*>
            for (i in a) {
                i as HashMap<String, String?>
                val aconseguit = i["aconseguit"] as Boolean
                var titol = i["titol"] as String
                var descripcio = i["descripcio"] as String
                val mostrant = i["mostrant"] as Boolean
                val r = RepteUsuari(
                    titol,
                    descripcio,
                    aconseguit,
                    mostrant
                )
                ret.add(r)
            }
            return ret
        }
    }
}
