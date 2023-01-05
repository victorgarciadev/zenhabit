package com.example.zenhabit.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.HashMap

/**
 * @author Pablo Morante, Izan Jimenez
 */
class RepteUsuari(repte: Repte, aconseguit: Boolean) {
    var repte = repte
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
                var repte = i["repte"] as HashMap<String, String>

                val r = RepteUsuari(
                    Repte(
                        repte["idRepte"].toString().toInt(),
                        repte["descripcio"].toString(),
                        repte["titol"].toString()
                    ),
                    aconseguit
                )
                ret.add(r)
            }
            return ret
        }

        fun dataFirebaseReptestoReptesUsuaris(document: DocumentSnapshot): RepteUsuari {
            return RepteUsuari(
                Repte(
                    document.get("idRepte").toString().toInt(),
                    document.get("descripcio").toString(),
                    document.get("titol").toString()
                ), false
            )


        }


    }
}
