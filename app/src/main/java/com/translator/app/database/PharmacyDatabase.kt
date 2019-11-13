package com.translator.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.translator.app.models.Medicine

/**
 * 1. This class is 'Room Library' related class.
 * 2. The purpose of this class is to create a singleton object of 'PharmacyDatabase'.
 * 3. Also generate a reference to 'MedicineDAO' for Database operations.
 * 4. This class works as a initializer for 'Room' Database Library.
 */
@Database(entities = [Medicine::class], version = 1, exportSchema = false)
abstract class PharmacyDatabase : RoomDatabase() {

    /**
     * Reference to MedicineDAO for Database operations.
     * It is initialzed by 'Room Database Library'
     * @return
     */
    abstract fun medicineDao(): MedicineDAO

    companion object {

        /**
         * Instance of  'PharmacyDatabase' that managed as singleton throughout the App.
         */
        private var INSTANCE: PharmacyDatabase? = null

        /**
         * TODOThe function to get 'Singleton' instance of 'PharmacyDatabase'
         *
         * @param context
         * @return
         */
        fun getDatabase(context: Context): PharmacyDatabase? {
            if (INSTANCE == null) {
                synchronized(PharmacyDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        PharmacyDatabase::class.java, "pharma.db"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}