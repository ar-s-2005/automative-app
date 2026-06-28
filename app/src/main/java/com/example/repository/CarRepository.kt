package com.example.repository

import com.example.firebase.FirebaseManager
import com.example.models.Car

object CarRepository {
    private val staticCars = listOf(
        // BMW Collection
        Car(
            id = "bmw_m4",
            brand = "BMW",
            model = "M4 Competition",
            price = "₹1.55Cr",
            year = 2024,
            fuelType = "Petrol",
            engineType = "3.0L Twin-Turbo L6",
            horsepower = 503,
            mileage = 3000,
            imageUrl = "https://images.unsplash.com/photo-1617531653332-bd46c24f2068?auto=format&fit=crop&q=80&w=800",
            topSpeed = 290,
            torque = 650,
            capacity = "2993 cc",
            condition = "Excellent",
            location = "Delhi, DL"
        ),
        Car(
            id = "bmw_x7",
            brand = "BMW",
            model = "X7 xDrive40d",
            price = "₹1.25Cr",
            year = 2023,
            fuelType = "Diesel",
            engineType = "3.0L Turbocharged I6",
            horsepower = 340,
            mileage = 12000,
            imageUrl = "https://images.unsplash.com/photo-1556189250-72ba954cfc2b?auto=format&fit=crop&q=80&w=800",
            topSpeed = 245,
            torque = 700,
            capacity = "2993 cc",
            condition = "Like New",
            location = "Mumbai, MH"
        ),
        Car(
            id = "bmw_i7",
            brand = "BMW",
            model = "i7 xDrive60",
            price = "₹2.00Cr",
            year = 2024,
            fuelType = "Electric",
            engineType = "Dual Electric Motors",
            horsepower = 544,
            mileage = 1500,
            imageUrl = "https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 745,
            capacity = "N/A (EV)",
            condition = "Mint",
            location = "Bangalore, KA"
        ),
        Car(
            id = "bmw_m5",
            brand = "BMW",
            model = "M5 CS",
            price = "₹2.10Cr",
            year = 2022,
            fuelType = "Petrol",
            engineType = "4.4L Twin-Turbo V8",
            horsepower = 627,
            mileage = 8000,
            imageUrl = "https://images.unsplash.com/photo-1607853202273-797f1c22a38e?auto=format&fit=crop&q=80&w=800",
            topSpeed = 305,
            torque = 750,
            capacity = "4395 cc",
            condition = "Excellent",
            location = "Hyderabad, TS"
        ),
        Car(
            id = "bmw_z4",
            brand = "BMW",
            model = "Z4 M40i Roadster",
            price = "₹90L",
            year = 2023,
            fuelType = "Petrol",
            engineType = "3.0L Turbocharged I6",
            horsepower = 340,
            mileage = 4000,
            imageUrl = "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 500,
            capacity = "2998 cc",
            condition = "Excellent",
            location = "Pune, MH"
        ),

        // Audi Collection
        Car(
            id = "audi_r8",
            brand = "Audi",
            model = "R8 V10 Performance",
            price = "₹2.70Cr",
            year = 2021,
            fuelType = "Petrol",
            engineType = "5.2L Naturally Aspirated V10",
            horsepower = 602,
            mileage = 15000,
            imageUrl = "https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?auto=format&fit=crop&q=80&w=800",
            topSpeed = 330,
            torque = 560,
            capacity = "5204 cc",
            condition = "Excellent",
            location = "Delhi, DL"
        ),
        Car(
            id = "audi_rs7",
            brand = "Audi",
            model = "RS7 Sportback",
            price = "₹2.00Cr",
            year = 2023,
            fuelType = "Petrol",
            engineType = "4.0L Twin-Turbo V8",
            horsepower = 591,
            mileage = 5000,
            imageUrl = "https://images.unsplash.com/photo-1606016159991-dfe4f2746ad5?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 800,
            capacity = "3996 cc",
            condition = "Excellent",
            location = "Chennai, TN"
        ),
        Car(
            id = "audi_q8",
            brand = "Audi",
            model = "Q8 55 TFSI Celebration",
            price = "₹1.15Cr",
            year = 2023,
            fuelType = "Petrol",
            engineType = "3.0L Turbocharged V6",
            horsepower = 335,
            mileage = 14000,
            imageUrl = "https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 500,
            capacity = "2995 cc",
            condition = "Good",
            location = "Mumbai, MH"
        ),
        Car(
            id = "audi_etron_gt",
            brand = "Audi",
            model = "e-tron GT RS",
            price = "₹1.80Cr",
            year = 2024,
            fuelType = "Electric",
            engineType = "Dual Electric Motors",
            horsepower = 637,
            mileage = 2000,
            imageUrl = "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 830,
            capacity = "N/A (EV)",
            condition = "Mint",
            location = "Bangalore, KA"
        ),
        Car(
            id = "audi_a8",
            brand = "Audi",
            model = "A8 L 55 TFSI Quattro",
            price = "₹1.60Cr",
            year = 2023,
            fuelType = "Petrol",
            engineType = "3.0L Turbocharged V6",
            horsepower = 335,
            mileage = 9000,
            imageUrl = "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 500,
            capacity = "2995 cc",
            condition = "Like New",
            location = "Kolkata, WB"
        ),

        // Mercedes-Benz Collection
        Car(
            id = "merc_amg_gt",
            brand = "Mercedes-Benz",
            model = "AMG GT R Coupe",
            price = "₹3.00Cr",
            year = 2022,
            fuelType = "Petrol",
            engineType = "4.0L Twin-Turbo V8",
            horsepower = 577,
            mileage = 6000,
            imageUrl = "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&q=80&w=800",
            topSpeed = 318,
            torque = 700,
            capacity = "3982 cc",
            condition = "Excellent",
            location = "Delhi, DL"
        ),
        Car(
            id = "merc_g_wagon",
            brand = "Mercedes-Benz",
            model = "G-Wagon G 400d",
            price = "₹2.50Cr",
            year = 2023,
            fuelType = "Diesel",
            engineType = "3.0L Inline-6 Turbocharged",
            horsepower = 325,
            mileage = 11000,
            imageUrl = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&q=80&w=800",
            topSpeed = 210,
            torque = 700,
            capacity = "2925 cc",
            condition = "Like New",
            location = "Mumbai, MH"
        ),
        Car(
            id = "merc_maybach",
            brand = "Mercedes-Benz",
            model = "Maybach GLS 600",
            price = "₹3.20Cr",
            year = 2024,
            fuelType = "Petrol",
            engineType = "4.0L Twin-Turbo V8",
            horsepower = 550,
            mileage = 3000,
            imageUrl = "https://images.unsplash.com/photo-1600706432502-75a0e2b4b4d1?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 730,
            capacity = "3982 cc",
            condition = "Mint",
            location = "Chandigarh, CH"
        ),
        Car(
            id = "merc_s_class",
            brand = "Mercedes-Benz",
            model = "S-Class S 450",
            price = "₹1.80Cr",
            year = 2024,
            fuelType = "Petrol",
            engineType = "3.0L Turbocharged I6",
            horsepower = 362,
            mileage = 5000,
            imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&q=80&w=800",
            topSpeed = 250,
            torque = 500,
            capacity = "2999 cc",
            condition = "Excellent",
            location = "Ahmedabad, GJ"
        ),
        Car(
            id = "merc_c63",
            brand = "Mercedes-Benz",
            model = "C63 AMG S E-Performance",
            price = "₹1.90Cr",
            year = 2023,
            fuelType = "Hybrid",
            engineType = "2.0L Turbo Hybrid I4",
            horsepower = 671,
            mileage = 4500,
            imageUrl = "https://images.unsplash.com/photo-1622194991756-3364f9addf47?auto=format&fit=crop&q=80&w=800",
            topSpeed = 280,
            torque = 1020,
            capacity = "1991 cc",
            condition = "Excellent",
            location = "Mumbai, MH"
        ),

        // Porsche Collection
        Car(
            id = "porsche_911",
            brand = "Porsche",
            model = "911 Turbo S Coupe",
            price = "₹3.30Cr",
            year = 2024,
            fuelType = "Petrol",
            engineType = "3.8L Twin-Turbo Flat-6",
            horsepower = 640,
            mileage = 1200,
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&q=80&w=800",
            topSpeed = 330,
            torque = 800,
            capacity = "3745 cc",
            condition = "Mint",
            location = "Bangalore, KA"
        ),
        Car(
            id = "porsche_taycan",
            brand = "Porsche",
            model = "Taycan Turbo S",
            price = "₹2.20Cr",
            year = 2024,
            fuelType = "Electric",
            engineType = "Dual Electric Motors",
            horsepower = 670,
            mileage = 800,
            imageUrl = "https://images.unsplash.com/photo-1554744512-d6c603f27c54?auto=format&fit=crop&q=80&w=800",
            topSpeed = 260,
            torque = 850,
            capacity = "N/A (EV)",
            condition = "Mint",
            location = "Pune, MH"
        ),
        Car(
            id = "porsche_panamera",
            brand = "Porsche",
            model = "Panamera GTS",
            price = "₹1.80Cr",
            year = 2023,
            fuelType = "Petrol",
            engineType = "2.9L Twin-Turbo V6",
            horsepower = 325,
            mileage = 9500,
            imageUrl = "https://images.unsplash.com/photo-1562920549-6b17a2747d74?auto=format&fit=crop&q=80&w=800",
            topSpeed = 270,
            torque = 450,
            capacity = "2894 cc",
            condition = "Excellent",
            location = "Coimbatore, TN"
        ),
        Car(
            id = "porsche_cayenne",
            brand = "Porsche",
            model = "Cayenne Turbo E-Hybrid",
            price = "₹1.50Cr",
            year = 2024,
            fuelType = "Petrol",
            engineType = "4.0L Twin-Turbo V8",
            horsepower = 541,
            mileage = 6000,
            imageUrl = "https://images.unsplash.com/photo-1619767886558-efdc259cde1a?auto=format&fit=crop&q=80&w=800",
            topSpeed = 286,
            torque = 770,
            capacity = "3996 cc",
            condition = "Excellent",
            location = "Mumbai, MH"
        ),
        Car(
            id = "porsche_cayman",
            brand = "Porsche",
            model = "718 Cayman Style Edition",
            price = "₹1.40Cr",
            year = 2023,
            fuelType = "Petrol",
            engineType = "2.0L Turbocharged Flat-4",
            horsepower = 300,
            mileage = 8000,
            imageUrl = "https://images.unsplash.com/photo-1611245801314-e0e5bef229b9?auto=format&fit=crop&q=80&w=800",
            topSpeed = 275,
            torque = 380,
            capacity = "1988 cc",
            condition = "Excellent",
            location = "Goa, GA"
        )
    )

    fun getAllCars(onComplete: (List<Car>) -> Unit) {
        FirebaseManager.getCustomListings(
            onSuccess = { customListings ->
                FirebaseManager.getWishlistIds { wishlistIds ->
                    val combinedList = (customListings + staticCars).map { car ->
                        car.copy(isWishlisted = wishlistIds.contains(car.id))
                    }
                    onComplete(combinedList)
                }
            },
            onFailure = {
                FirebaseManager.getWishlistIds { wishlistIds ->
                    val combinedList = staticCars.map { car ->
                        car.copy(isWishlisted = wishlistIds.contains(car.id))
                    }
                    onComplete(combinedList)
                }
            }
        )
    }

    fun getCarById(id: String, onComplete: (Car?) -> Unit) {
        getAllCars { list ->
            val car = list.find { it.id == id }
            onComplete(car)
        }
    }
}
