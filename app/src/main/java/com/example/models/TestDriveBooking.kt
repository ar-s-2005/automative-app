package com.example.models

data class TestDriveBooking(
    val id: String = "",
    val userId: String = "",
    val carId: String = "",
    val carModel: String = "",
    val date: String = "",
    val time: String = "",
    val dealerName: String = "",
    val status: String = "Pending"
)
