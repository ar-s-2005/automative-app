package com.example.models

import java.io.Serializable

data class Car(
    val id: String = "",
    val brand: String = "",
    val model: String = "",
    val price: String = "",
    val year: Int = 0,
    val fuelType: String = "",
    val engineType: String = "",
    val horsepower: Int = 0,
    val mileage: Int = 0,
    val imageUrl: String = "",
    val topSpeed: Int = 250,
    val torque: Int = 500,
    val capacity: String = "3.0L",
    val ownerName: String = "AutoMarket Elite",
    val contactNumber: String = "+1 555-Luxury",
    val location: String = "Mumbai, MH",
    val condition: String = "Excellent",
    var isWishlisted: Boolean = false,
    val isCustom: Boolean = false
) : Serializable
