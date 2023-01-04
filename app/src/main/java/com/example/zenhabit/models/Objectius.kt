package com.example.zenhabit.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Pablo Morante, Izan Jimenez, Txell Llanas
 */
class Objectius(
    nom: String,
    descripcio: String,
    categoria: String,
    dataLimit: String,
    repetible: Dies?,
    horari: String?,
    complert: Boolean,
    ultimaDataFet: Date?,
    tipus: Boolean
) {
    val nom = nom
    val descripcio = descripcio
    val categoria = categoria
    val dataLimit = dataLimit
    val repetible = repetible
    val horari = horari
    var complert = complert
    val ultimaDataFet = ultimaDataFet
    val tipus = tipus


    /**
     * @author Izan Jimenez
     */
    companion object {

        fun dataFirebaseToObjectius(document: DocumentSnapshot): ArrayList<Objectius> {
            //fun dataFirebaseToObjectius(document: ArrayList<Objectius>): ArrayList<Objectius> {
            var ret: ArrayList<Objectius> = ArrayList()
            try {
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
            } catch (ex: NullPointerException) {
                Log.d("ERROR", "No hi ha base de dades")
            }
            return ret
        }
    }

}
