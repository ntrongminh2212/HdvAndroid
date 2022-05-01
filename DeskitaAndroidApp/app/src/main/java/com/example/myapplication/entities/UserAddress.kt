package com.example.myapplication.entities

import java.util.*

class UserAddress(
    var address:String,
    var city : String,
    var country: String,
    var phoneNo : String,
    var postalCode: String
)

class Avatar(
    var public_id:String,
    var url : String
)

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
)