package com.example.zenhabit.models

import java.util.Date

class Objectiu(nom: String, descripcio: String, categoria: String, dataLimit: String, repetible:Dies,horari:String, complert: Boolean, ultimaDataFet: Date?, tipus: Boolean) {
    val nom = nom
    val descripcio = descripcio
    val categoria = categoria
    val dataLimit = dataLimit
    val repetible = repetible
    val horari = horari
    val complert = complert
    val ultimaDataFet = ultimaDataFet
    val tipus = tipus
}