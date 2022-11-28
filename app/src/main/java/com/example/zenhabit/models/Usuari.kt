package com.example.zenhabit.models

class Usuari(nom: String, email: String, llistaReptes: List<RepteUsuari>?, llistaPlantes: List<PlantaUsuari>?, llistaTasques: List<Tasca>?, llistaHabits: List<Habit>?) {
    val nom = nom
    val email = email
    val llistaReptes = llistaReptes
    val llistaPlantes = llistaPlantes
    val llistaTasques = llistaTasques
    val llistaHabits = llistaHabits
}