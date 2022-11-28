package com.example.zenhabit.models

import com.google.type.Date

class Habit(nom: String, descripcio: String?, categoria: CategoriaTipusHabit, dataLimit: Date, horari: String, complert: Boolean, dataAconseguit: Date?) {
    val nom = nom
    val descripcio = descripcio
    val categoria = categoria
    val dataLimit = dataLimit
    val horari = horari
    val complert = complert
    val dataAconseguit = dataAconseguit
}