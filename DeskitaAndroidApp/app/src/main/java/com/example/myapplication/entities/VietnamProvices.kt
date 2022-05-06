package com.example.myapplication.entities

class Wards(
    val name: String,
    val code: Int
)

class Districts(
    val name: String,
    val code: Int,
    val wards: ArrayList<Wards>
)

class City(
    val name: String,
    val code: Int,
    val districts: ArrayList<Districts>
)