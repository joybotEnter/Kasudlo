package com.example.kasudlo.models

data class CommunityProfile(

    var profileId: String = "",
    var householdId: String = "",

    var name: String = "",
    var age: String = "",
    var gender: String = "",
    var address: String = "",
    var barangay: String = "",

    var bloodPressure: String = "",
    var healthConcern: String = "",
    var vaccinationStatus: String = "",

    var isSmoker: Boolean = false,
    var drinksAlcohol: Boolean = false,

    var hasNCD: Boolean = false,
    var ncdDetails: String = "",

    var createdAt: Long = System.currentTimeMillis()
)
