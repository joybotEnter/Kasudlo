package com.example.kasudlo.models

data class Immunization(

    var childName: String = "",
    var bcg: Boolean = false,
    var dpt1: Boolean = false,
    var dpt2: Boolean = false,
    var dpt3: Boolean = false,
    var opv1: Boolean = false,
    var measles: Boolean = false

)