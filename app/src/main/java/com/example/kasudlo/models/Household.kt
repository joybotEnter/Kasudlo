package com.example.kasudlo.models

data class Household(

    var householdId: String = "",
    var headName: String = "",
    var address: String = "",
    var barangay: String = "",
    var members: Int = 0,
    var createdAt: Long = System.currentTimeMillis()

)