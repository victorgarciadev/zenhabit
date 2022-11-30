package com.example.zenhabit.models

class Usuari(nom: String, email: String, llistaReptes: ArrayList<RepteUsuari>?, llistaPlantes: ArrayList<PlantaUsuari>?, llistaTasques: ArrayList<Tasca>?, llistaHabits: ArrayList<Habit>?) {
    val nom = nom
    val email = email
    val llistaReptes = llistaReptes
    val llistaPlantes = llistaPlantes
    val llistaTasques = llistaTasques
    val llistaHabits = llistaHabits
}