package com.example.myapplication.entities

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.example.myapplication.R
import com.example.myapplication.adapter.ProductAdapter
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.frag_home.*
import okhttp3.*
import java.io.IOException
import java.io.Serializable
import java.util.*

class ProductImage(
    val _id: String,
    val url: String,
    val public_id: String
):Serializable

class UpdateStock(
    val updateDate : Date,
    val _id : String,
    val userId : String,
    val quantity : Int
): Serializable

class Review(
    val createAt : Date,
    val _id : String,
    val rating : Double,
    val comment : String,
    val userId : String
):Serializable

class ProductContainer(val product:Product)

class ProductsFeed(val products: List<Product>)

class Product(
    val _id: String,
    val name: String,
    val price: Double,
    val description: String,
    val rating: Int,
    val images: List<ProductImage>,
    val category: String,
    val stock: Int,
    val classify: String,
    val user: String,
    val seller: String,
    val createdAt: Date,
    val updateStock:List<UpdateStock>,
    val reviews: List<Review>

):Serializable