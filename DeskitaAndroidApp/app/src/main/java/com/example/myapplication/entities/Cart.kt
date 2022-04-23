package com.example.myapplication.entities

import java.io.Serializable

class Cart (val myCart:List<CartItem>)

class CartItem(
    val _id: String,
    val product: String,
    val checked: Boolean,
    val name: String,
    val image: String,
    val price: Double,
    var quantity: Int,
    val category: String,
    var total: Double,
    var isSelect: Boolean
):Serializable{
    init {
        isSelect = false
    }
}