package com.translator.app.utils

import com.google.gson.Gson
import com.translator.app.models.User

/**
 * 1. This class manages the handling of SharedPreferences.
 * 2. It's primary purpose is to save and retreive 'User' details from SharedPreferences.
 * 3. Reference to 'SharedPreferences' is aquired from 'MyApplication' class.
 */
object Prefs {

    private val USER_OBJ = "user_obj"
    /**
     * Instance of 'SharedPreferences' aquired from 'MyApplication'
     */
    private val prefs = MyApplication.getApplicationInstance().mSharedPreferences
    private val gson = Gson()

    /**
     * Instance of User to saved or retrieved
     */
    var user: User?
        get() {
            return gson.fromJson(prefs.getString(USER_OBJ, ""), User::class.java)
        }
        set(value) {
            prefs.edit()?.putString(USER_OBJ, gson.toJson(value))?.apply()
        }

}

