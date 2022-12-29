package com.example.zenhabit.models

import java.util.Date

/**
 * @author Pablo Morante
 */
class Habit(nom: String, descripcio: String?, categoria: String, repetible: List<DiesHabit>?, dataLimit: String, horari: String, complert: Boolean, ultimaDataFet: Date?) {
    val nom = nom
    val descripcio = descripcio
    val categoria = categoria
    val dataLimit = dataLimit
    val repetible = repetible
    val horari = horari
    val complert = complert
    val ultimaDataFet = ultimaDataFet
}