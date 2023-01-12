package com.projectegrupd.zenhabit.models

import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Pablo Morante, Izan Jimenez, Txell Llanas
 */
class Objectius(
    val nom: String,
    val descripcio: String,
    val categoria: String,
    val dataLimit: String,
    val repetible: Dies?,
    val horari: String?,
    var complert: Boolean,
    val ultimaDataFet: Date?,
    val tipus: Boolean
) {

    /**
     * Retorna una ArrayList d'objectius
     * @param document resultat de la petició a la BBDD (DocumentSnapshot)
     * @author Izan Jimenez
     */
    companion object {

        fun dataFirebaseToObjectius(document: DocumentSnapshot): ArrayList<Objectius> {

            var ret: ArrayList<Objectius> = ArrayList()
            val d: Map<String, String> = document.data as Map<String, String>
            val a = d.get("llistaObjectius") as List<*>
            for (i in a) {
                i as HashMap<String, String?>
                val nom = i["nom"]
                val descripcio = i["descripcio"]
                val categoria = i["categoria"]
                val dataLimit = i["dataLimit"]
                val dies = i["repetible"] as HashMap<*, *>?
                var repetible: Dies? = null
                if (dies != null) {
                    repetible = Dies(
                        dies["dilluns"] as Boolean,
                        dies["dimarts"] as Boolean,
                        dies["dimecres"] as Boolean,
                        dies["dijous"] as Boolean,
                        dies["divendres"] as Boolean,
                        dies["dissabte"] as Boolean,
                        dies["diumenge"] as Boolean
                    )
                }
                val horari = i["horari"]
                val complert: Boolean = i["complert"] as Boolean
                val udf = i["ultimaDataFet"]
                var ultimaDataFet: Date? = null
                if (udf != null) {
                    val format: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy'");
                    ultimaDataFet = format.parse(udf);
                }
                val tipus: Boolean = i["tipus"] as Boolean
                val o = Objectius(
                    nom.toString(),
                    descripcio.toString(),
                    categoria.toString(),
                    dataLimit.toString(),
                    repetible,
                    horari.toString(),
                    complert,
                    ultimaDataFet,
                    tipus
                )
                ret.add(o)
            }
            return ret
        }
    }

}
