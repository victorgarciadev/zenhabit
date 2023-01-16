package com.projectegrupd.zenhabit.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.HashMap

/**
 * @author Pablo Morante, Izan Jimenez
 */
class RepteUsuari(var repte: Repte, var aconseguit: Boolean, var mostrant: Boolean) {

    /***
     * Retorna una ArrayList de RepteUsuari
     * @param documnt resultat de la consulta a la BBDD (DocumentSnapshot)
     * @author Izan Jimenez
     */
    companion object {

        fun dataFirebasetoReptes(document: DocumentSnapshot): ArrayList<RepteUsuari> {

            var ret: ArrayList<RepteUsuari> = ArrayList()

            val d: Map<String, String> = document.data as Map<String, String>
            val a = d["llistaReptes"] as List<*>
            for (i in a) {
                i as HashMap<String, String?>
                val aconseguit = i["aconseguit"] as Boolean
                val mostrant = i["mostrant"] as Boolean
                val repte = i["repte"] as HashMap<String, String>

                val r = RepteUsuari(
                    Repte(
                        repte["idRepte"].toString().toInt(),
                        repte["descripcio"].toString(),
                        repte["titol"].toString()
                    ),
                    aconseguit,
                    mostrant
                )
                ret.add(r)
            }
            return ret
        }

        /***
         * Retorna un sol RepteUsuari
         * @return RepteUsuari RepteUsuari
         * @param DocumentSnapshot reultat del Firebase
         * @author Izan Jimenez
         */
        fun dataFirebaseReptestoReptesUsuaris(document: DocumentSnapshot): RepteUsuari {
            return RepteUsuari(
                Repte(
                    document.get("idRepte").toString().toInt(),
                    document.get("descripcio").toString(),
                    document.get("titol").toString()
                ), false, false
            )


        }


    }
}
