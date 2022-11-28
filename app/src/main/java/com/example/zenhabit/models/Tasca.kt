package com.example.zenhabit.models

import com.google.type.Date

class Tasca(name: String, time: String, descripcio: String?, categoria: CategoriaTipusHabit, complert: Boolean, dataAconseguit: Date?) {
    val nom = name
    val hora = time
    val descripcio = descripcio
    val categoria = categoria
    val complert = complert
    val dataAconseguit = dataAconseguit
}