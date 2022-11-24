package com.example.zenhabit.models

class Usuari(nom: String, email: String, llistaReptes: List<RepteUsuari>, llistaPlantes: List<PlantaUsuari>) {
    val nom = nom
    val email = email
    val llistaReptes = llistaReptes
    val llistaPlantes = llistaPlantes
}