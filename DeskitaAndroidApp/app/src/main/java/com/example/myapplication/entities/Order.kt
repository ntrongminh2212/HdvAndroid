package com.example.myapplication.entities

import java.util.*
import kotlin.collections.ArrayList

enum class OrderStatus() {
    Processing, Confirmed, Delivered, Complete
}

class Order(
    var _id: String,
    var shippingInfo: UserAddress,
    var paymentMethod: String,
    var itemsPrice: Int,
    var taxPrice: Int,
    var shippingPrice: Int,
    var totalPrice: Int,
    var orderStatus: OrderStatus,
    var orderItems : ArrayList<CartItem>,
    var user : String,
    var createAt: Date,
    var deliveredAt: Date,
    var paidAt: Date
)

class MyOrder(val orders:ArrayList<Order>)