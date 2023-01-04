package com.example.zenhabit.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

/**
 * @author Pablo Morante, Izan Jimenez
 */
class RepteUsuari(idRepte: Long, aconseguit: Boolean) {
    var repte = idRepte
    var aconseguit = aconseguit

    /***
     * Retorna una ArrayList de RepteUsuari
     * @param documnt resultat de la consulta a la BBDD (DocumentSnapshot)
     * @author Izan Jimenez
     */
    companion object {

        fun dataFirebasetoReptes(document: DocumentSnapshot): ArrayList<RepteUsuari> {

            var ret: ArrayList<RepteUsuari> = ArrayList()

            val d: Map<String, String> = document.data as Map<String, String>
            val a = d.get("llistaReptes") as List<*>
            for (i in a) {
                i as HashMap<String, String?>
                val aconseguit = i["aconseguit"] as Boolean
                var repte = i["repte"] as Long
                val r = RepteUsuari(
                    repte,
                    aconseguit
                )
                ret.add(r)
            }
            return ret
        }
    }
}
