package com.example.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebase.FirebaseManager
import com.example.firebase.GeminiService
import com.example.models.Car
import com.example.models.TestDriveBooking
import com.example.repository.CarRepository
import kotlinx.coroutines.launch
import java.util.UUID

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class CarViewModel(application: Application) : AndroidViewModel(application) {

    private val _carsState = MutableLiveData<UiState<List<Car>>>(UiState.Loading)
    val carsState: LiveData<UiState<List<Car>>> get() = _carsState

    private val _filteredCars = MutableLiveData<List<Car>>(emptyList())
    val filteredCars: LiveData<List<Car>> get() = _filteredCars

    private val _comparisonCars = MutableLiveData<List<Car>>(emptyList())
    val comparisonCars: LiveData<List<Car>> get() = _comparisonCars

    private val _bookingsState = MutableLiveData<UiState<List<TestDriveBooking>>>(UiState.Loading)
    val bookingsState: LiveData<UiState<List<TestDriveBooking>>> get() = _bookingsState

    private val _aiRecommendation = MutableLiveData<String>("")
    val aiRecommendation: LiveData<String> get() = _aiRecommendation

    private val _aiLoading = MutableLiveData<Boolean>(false)
    val aiLoading: LiveData<Boolean> get() = _aiLoading

    private var allCarsList: List<Car> = emptyList()
    private var currentSearchQuery = ""
    private var currentSelectedBrand = "All"

    init {
        loadCars()
    }

    fun loadCars() {
        _carsState.value = UiState.Loading
        CarRepository.getAllCars { list ->
            allCarsList = list
            _carsState.value = UiState.Success(list)
            applyFilters()
        }
    }

    fun searchCars(query: String) {
        currentSearchQuery = query
        applyFilters()
    }

    fun selectBrand(brand: String) {
        currentSelectedBrand = brand
        applyFilters()
    }

    private fun applyFilters() {
        var result = allCarsList
        if (currentSelectedBrand != "All") {
            result = result.filter { it.brand.equals(currentSelectedBrand, ignoreCase = true) }
        }
        if (currentSearchQuery.isNotBlank()) {
            result = result.filter {
                it.model.contains(currentSearchQuery, ignoreCase = true) ||
                        it.brand.contains(currentSearchQuery, ignoreCase = true) ||
                        it.engineType.contains(currentSearchQuery, ignoreCase = true)
            }
        }
        _filteredCars.value = result
    }

    fun toggleWishlist(car: Car) {
        FirebaseManager.toggleWishlist(car.id) { added ->
            allCarsList = allCarsList.map {
                if (it.id == car.id) it.copy(isWishlisted = added) else it
            }
            _carsState.value = UiState.Success(allCarsList)
            applyFilters()
        }
    }

    fun sellCar(
        brand: String,
        model: String,
        year: Int,
        price: String,
        mileage: Int,
        fuelType: String,
        engineType: String,
        condition: String,
        ownerName: String,
        contact: String,
        location: String,
        imageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val newCar = Car(
            id = "custom_" + UUID.randomUUID().toString(),
            brand = brand,
            model = model,
            year = year,
            price = price,
            mileage = mileage,
            fuelType = fuelType,
            engineType = engineType,
            condition = condition,
            ownerName = ownerName,
            contactNumber = contact,
            location = location,
            imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1542282088-fe8426682b8f?q=80&w=1000&auto=format&fit=crop" },
            isCustom = true
        )
        FirebaseManager.addListing(newCar,
            onSuccess = {
                loadCars()
                onSuccess()
            },
            onFailure = {
                onFailure(it.localizedMessage ?: "Unknown Firestore Error")
            }
        )
    }

    // --- CAR COMPARISON ---

    fun toggleCompare(car: Car, onMaxReached: () -> Unit = {}) {
        val currentList = _comparisonCars.value ?: emptyList()
        val isAlreadyCompared = currentList.any { it.id == car.id }
        if (isAlreadyCompared) {
            _comparisonCars.value = currentList.filter { it.id != car.id }
        } else {
            if (currentList.size >= 3) {
                onMaxReached()
            } else {
                _comparisonCars.value = currentList + car
            }
        }
    }

    fun clearComparison() {
        _comparisonCars.value = emptyList()
    }

    // --- TEST DRIVES ---

    fun bookTestDrive(car: Car, date: String, time: String, dealer: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = FirebaseManager.getCurrentUser(getApplication()) ?: return onFailure("Must be logged in to book")
        val booking = TestDriveBooking(
            id = "booking_" + UUID.randomUUID().toString(),
            userId = user.uid,
            carId = car.id,
            carModel = "${car.brand} ${car.model}",
            date = date,
            time = time,
            dealerName = dealer,
            status = "Confirmed"
        )
        FirebaseManager.addBooking(booking,
            onSuccess = {
                onSuccess()
                loadBookings()
            },
            onFailure = {
                onFailure(it.localizedMessage ?: "Booking Error")
            }
        )
    }

    fun loadBookings() {
        _bookingsState.value = UiState.Loading
        FirebaseManager.getBookings(
            onSuccess = { list ->
                _bookingsState.value = UiState.Success(list)
            },
            onFailure = { error ->
                _bookingsState.value = UiState.Error(error.localizedMessage ?: "Error fetching bookings")
            }
        )
    }

    // --- AI ADVISOR ---

    fun getAIRecommendations(budget: String, brandPref: String, style: String, otherInfo: String) {
        _aiLoading.value = true
        _aiRecommendation.value = "Consulting the AutoMarket Elite AI Advisor..."
        viewModelScope.launch {
            val response = GeminiService.getAIRecommendation(budget, brandPref, style, otherInfo)
            _aiRecommendation.postValue(response)
            _aiLoading.postValue(false)
        }
    }
}
