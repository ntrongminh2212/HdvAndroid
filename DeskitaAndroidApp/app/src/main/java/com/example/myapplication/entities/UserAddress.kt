package com.example.myapplication.entities

import java.io.Serializable
import java.util.*

class UserAddress(
    var address:String,
    var city : String,
    var country: String,
    var phoneNo : String,
    var postalCode: String
):Serializable

class Avatar(
    var public_id:String,
    var url : String
):Serializable

class User(
    var avatar : Avatar,
    var _id : String ,
    var name : String,
    var placeOfBirth : String,
    var dateOfBirth : String,
    var phoneNumber : String,
    var emailUser : String,
    var createAt : Date,
    var role : String
):Serializable

class UserContainer(var user:User)