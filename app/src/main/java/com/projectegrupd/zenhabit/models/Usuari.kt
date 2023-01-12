package com.projectegrupd.zenhabit.models

/**
 * @author Pablo Morante
 */
class Usuari(
    val nom: String,
    val email: String,
    val llistaReptes: ArrayList<RepteUsuari>?,
    val llistaPlantes: ArrayList<PlantaUsuari>?,
    val llistaObjectius: ArrayList<Objectius>?
) {

}