package com.translator.app.models

import java.io.Serializable

/**
 * 1. This class encapsulates all the 'User' related data like : name,height,weight,medical conditions,image.
 * 2. This model is used primarily while saving data of User in SharedPreferences.
 */
class User : Serializable {

    /**
     * The Name of User
     */
    var name: String? = ""
    /**
     * The Height of User
     */
    var height: Double = 0.0
    /**
     * The Weight of User
     */
    var weight: Double = 0.0
    /**
     * The Medical Conditions of User
     */
    var medicalConditions: String? = ""
    /**
     * The Image of User
     */
    var image: String = ""
}