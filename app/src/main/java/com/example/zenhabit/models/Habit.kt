package com.example.zenhabit.models

//import com.google.type.Date
import java.util.Date

class Habit(nom: String, descripcio: String?, categoria: String, repetible: List<DiesHabit>?, dataLimit: String, horari: String, complert: Boolean, dataAconseguit: Date?) {
    val nom = nom
    val descripcio = descripcio
    val categoria = categoria
    val dataLimit = dataLimit
    val repetible = repetible
    val horari = horari
    val complert = complert
    val dataAconseguit = dataAconseguit
}