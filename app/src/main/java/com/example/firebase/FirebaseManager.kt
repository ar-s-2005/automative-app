package com.example.firebase

import android.content.Context
import android.util.Log
import com.example.models.Car
import com.example.models.TestDriveBooking
import com.example.models.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    
    private var isInitialized = false
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    
    // In-memory simulation fallback
    private var simulatedUser: UserProfile? = null
    private val simulatedListings = mutableListOf<Car>()
    private val simulatedWishlistIds = mutableSetOf<String>()
    private val simulatedBookings = mutableListOf<TestDriveBooking>()

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                auth = FirebaseAuth.getInstance()
                db = FirebaseFirestore.getInstance()
                isInitialized = true
                Log.d(TAG, "Firebase initialized successfully.")
            } else {
                Log.w(TAG, "Firebase not initialized. Operating in Simulation Mode.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase init exception: ${e.message}. Using Simulation Mode.")
        }
    }

    fun isFirebaseActive(): Boolean = isInitialized && auth != null && db != null

    // --- AUTHENTICATION ---
    
    fun login(email: String, password: String, onSuccess: (UserProfile) -> Unit, onFailure: (Exception) -> Unit) {
        if (isFirebaseActive()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        val profile = UserProfile(uid = user.uid, name = user.displayName ?: "Elite Owner", email = email)
                        onSuccess(profile)
                    } else {
                        onFailure(Exception("User was empty"))
                    }
                }
                ?.addOnFailureListener {
                    onFailure(it)
                }
        } else {
            // Simulation Mode
            if (email.isNotBlank() && password.length >= 6) {
                val profile = UserProfile(uid = "sim_user_123", name = email.substringBefore("@").replaceFirstChar { it.uppercase() }, email = email)
                simulatedUser = profile
                onSuccess(profile)
            } else {
                onFailure(Exception("Invalid email or password (min 6 chars)"))
            }
        }
    }

    fun signup(name: String, email: String, password: String, onSuccess: (UserProfile) -> Unit, onFailure: (Exception) -> Unit) {
        if (isFirebaseActive()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnSuccessListener { result ->
                    val firebaseUser = result.user
                    if (firebaseUser != null) {
                        val profile = UserProfile(uid = firebaseUser.uid, name = name, email = email)
                        
                        // Save in firestore
                        db?.collection("users")?.document(firebaseUser.uid)?.set(profile)
                        
                        onSuccess(profile)
                    } else {
                        onFailure(Exception("User was null"))
                    }
                }
                ?.addOnFailureListener {
                    onFailure(it)
                }
        } else {
            // Simulation
            if (name.isNotBlank() && email.isNotBlank() && password.length >= 6) {
                val profile = UserProfile(uid = "sim_user_123", name = name, email = email)
                simulatedUser = profile
                onSuccess(profile)
            } else {
                onFailure(Exception("Please complete all fields (password min 6 chars)"))
            }
        }
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (isFirebaseActive()) {
            auth?.sendPasswordResetEmail(email)
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { onFailure(it) }
        } else {
            if (email.contains("@")) {
                onSuccess()
            } else {
                onFailure(Exception("Invalid email format"))
            }
        }
    }

    fun getCurrentUser(context: Context): UserProfile? {
        if (isFirebaseActive()) {
            val user = auth?.currentUser
            return if (user != null) {
                UserProfile(uid = user.uid, name = user.displayName ?: "Elite Member", email = user.email ?: "")
            } else {
                null
            }
        }
        return simulatedUser
    }

    fun logout() {
        if (isFirebaseActive()) {
            auth?.signOut()
        } else {
            simulatedUser = null
        }
    }

    // --- CAR LISTINGS ---

    fun addListing(car: Car, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (isFirebaseActive()) {
            db?.collection("listings")?.document(car.id)?.set(car)
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { onFailure(it) }
        } else {
            simulatedListings.add(0, car)
            onSuccess()
        }
    }

    fun getCustomListings(onSuccess: (List<Car>) -> Unit, onFailure: (Exception) -> Unit) {
        if (isFirebaseActive()) {
            db?.collection("listings")?.get()
                ?.addOnSuccessListener { snapshot ->
                    val list = snapshot.toObjects(Car::class.java)
                    onSuccess(list)
                }
                ?.addOnFailureListener { onFailure(it) }
        } else {
            onSuccess(simulatedListings)
        }
    }

    // --- WISHLIST ---

    fun toggleWishlist(carId: String, onSuccess: (Boolean) -> Unit) {
        if (isFirebaseActive()) {
            val uid = auth?.currentUser?.uid ?: "guest"
            val docRef = db?.collection("wishlists")?.document(uid)
            docRef?.get()?.addOnSuccessListener { doc ->
                val currentList = doc.get("carIds") as? List<*> ?: emptyList<Any>()
                val newList = currentList.map { it.toString() }.toMutableList()
                val added = if (newList.contains(carId)) {
                    newList.remove(carId)
                    false
                } else {
                    newList.add(carId)
                    true
                }
                docRef.set(mapOf("carIds" to newList)).addOnSuccessListener {
                    onSuccess(added)
                }
            }
        } else {
            val added = if (simulatedWishlistIds.contains(carId)) {
                simulatedWishlistIds.remove(carId)
                false
            } else {
                simulatedWishlistIds.add(carId)
                true
            }
            onSuccess(added)
        }
    }

    fun getWishlistIds(onSuccess: (Set<String>) -> Unit) {
        if (isFirebaseActive()) {
            val uid = auth?.currentUser?.uid ?: "guest"
            db?.collection("wishlists")?.document(uid)?.get()
                ?.addOnSuccessListener { doc ->
                    val list = doc.get("carIds") as? List<*> ?: emptyList<Any>()
                    onSuccess(list.map { it.toString() }.toSet())
                }
                ?.addOnFailureListener {
                    onSuccess(emptySet())
                }
        } else {
            onSuccess(simulatedWishlistIds.toSet())
        }
    }

    // --- BOOKINGS ---

    fun addBooking(booking: TestDriveBooking, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        if (isFirebaseActive()) {
            db?.collection("bookings")?.document(booking.id)?.set(booking)
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { onFailure(it) }
        } else {
            simulatedBookings.add(0, booking)
            onSuccess()
        }
    }

    fun getBookings(onSuccess: (List<TestDriveBooking>) -> Unit, onFailure: (Exception) -> Unit) {
        if (isFirebaseActive()) {
            val uid = auth?.currentUser?.uid ?: "guest"
            db?.collection("bookings")?.whereEqualTo("userId", uid)?.get()
                ?.addOnSuccessListener { snapshot ->
                    val list = snapshot.toObjects(TestDriveBooking::class.java)
                    onSuccess(list)
                }
                ?.addOnFailureListener { onFailure(it) }
        } else {
            onSuccess(simulatedBookings)
        }
    }
}
