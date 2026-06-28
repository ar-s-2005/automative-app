package com.example.models

data class Booking(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val carId: String = "",
    val carName: String = "",
    val date: String = "",
    val time: String = "",
    val dealershipName: String = "",
    val status: String = "Pending"
)
