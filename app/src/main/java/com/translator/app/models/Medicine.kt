package com.translator.app.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

/**
 * 1. This is an Entity class, means it directly maps to a 'Table' in 'Room' Local Database.
 * 2. All the fields in this class are actual columns in Table -> 'medicines'.
 *
 */
@Entity(tableName = "medicines")
class Medicine : Serializable {

    /**
     * This field is auto-generated type column
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "medicine_id")
    var medicineId: Long = 0L

    /**
     * This field stores the medicine name.
     */
    @ColumnInfo(name = "medicine_name")
    var medicineName: String = ""

    /**
     * This field stores the medicine description.
     */
    @ColumnInfo(name = "medicine_description")
    var medicineDescription: String = ""

    /**
     * This field stores the medicine date in 'Long' format,i.e, System.currentTimeMillis.
     */
    @ColumnInfo(name = "medicine_date")
    var medicineDate: Long = 0L

    /**
     * This field stores the 'Language Code' of Language of Medicine name.
     */
    @ColumnInfo(name = "medicine_lang_code")
    var medicineLangCode: String = ""
}