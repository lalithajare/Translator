package com.translator.app.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.translator.app.database.PharmacyDatabase

/**
 * 1. This is the 'Application' class, the main entry point to Application.
 * 2. All the objects that are used universally in App are initialized over here.
 * 3. It initializes :- reference to 'SharedPreferences', 'PharmacyDatabase', 'MyApplication'.
 */
class MyApplication : Application() {

    /**
     * The instance of SharedPreferences that is used by 'Prefs' class.
     */
    lateinit var mSharedPreferences: SharedPreferences

    companion object {

        /**
         * The singleton instance of 'MyApplication' used for getting Application context and access universal objects like Database, Preferences, etc.
         */
        private lateinit var instance: MyApplication

        /**
         * The Instance of 'PharmacyDatabase' used for doing Database operations.
         */
        private lateinit var pharmaDB: PharmacyDatabase

        /**
         * Method to acquire 'MyApplication' instance.
         *
         * @return
         */
        fun getApplicationInstance(): MyApplication {
            return instance
        }

        /**
         * Method to acquire 'PharmacyDatabase' instance.
         *
         * @return
         */
        fun getPharmacyDB(): PharmacyDatabase {
            return pharmaDB
        }
    }

    /**
     * Entry point to this Activity
     *
     */
    override fun onCreate() {
        super.onCreate()
        instance = this
        mSharedPreferences = getSharedPreferences("TRANSLATOR", Context.MODE_PRIVATE)
        pharmaDB = PharmacyDatabase.getDatabase(this)!!
    }


}