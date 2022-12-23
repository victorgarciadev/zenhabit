package com.example.zenhabit.models

class Usuari(nom: String, email: String, llistaReptes: ArrayList<RepteUsuari>?, llistaPlantes: ArrayList<PlantaUsuari>?, llistaObjectius: ArrayList<Objectius>?) {
    val nom = nom
    val email = email
    val llistaReptes = llistaReptes
    val llistaPlantes = llistaPlantes
    val llistaObjectius = llistaObjectius
}