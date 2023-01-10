package com.projectegrupd.zenhabit.models

/**
 * @author Pablo Morante
 */
class Usuari(
    nom: String,
    email: String,
    llistaReptes: ArrayList<RepteUsuari>?,
    llistaPlantes: ArrayList<PlantaUsuari>?,
    llistaObjectius: ArrayList<Objectius>?
) {
    val nom = nom
    val email = email
    val llistaReptes = llistaReptes
    val llistaPlantes = llistaPlantes
    val llistaObjectius = llistaObjectius
}