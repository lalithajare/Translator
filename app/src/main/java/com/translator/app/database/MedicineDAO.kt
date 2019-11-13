package com.translator.app.database

import androidx.room.*
import com.translator.app.models.Medicine

/**
 * 1. This interface is 'Room Library' related interface.
 * 2. The methods in this interface are actual Database CRUD operations
 *
 */
@Dao
interface MedicineDAO {

    /**
     * Inserts the 'Medicine' object in DB
     *
     * @param medicine : This 'Medicine' object to be inserted
     * @return
     */
    @Insert
    suspend fun insertMedicine(medicine: Medicine): Long

    /**
     * Deletes the 'Medicine' object in DB
     *
     * @param medicine : This 'Medicine' object to be deleted
     * @return
     */
    @Delete
    suspend fun deleteMedicine(medicine: Medicine): Int

    /**
     * Updates the 'Medicine' object in DB
     *
     * @param medicine : This 'Medicine' object to be updated
     * @return
     */
    @Update
    suspend fun updateMedicine(medicine: Medicine): Int

    /**
     * Retrieves the 'Medicine' List objects from DB*
     * @return
     */
    @Query("SELECT * FROM medicines")
    suspend fun getMedicineList(): List<Medicine>

    /**
     * Retrieves the 'Medicine' object from DB
     *
     * @param id : ID of medicine to be retrieved
     * @return
     */
    @Query("SELECT * FROM medicines WHERE medicine_id = :id")
    suspend fun getMedicine(id: Long): Medicine

}