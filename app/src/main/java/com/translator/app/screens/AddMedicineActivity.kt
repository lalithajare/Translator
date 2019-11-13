package com.translator.app.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.translator.app.R
import com.translator.app.models.Medicine
import com.translator.app.utils.MyApplication
import kotlinx.coroutines.*

/**
 * 1. This Activity opens up when the User comes from 'TranslateActivity' by pressing button 'Add to Device'.
 * 2. The main function of this class is to save the medicine with description into the locally created database.
 * 3. The concept of 'Coroutines' in kotlin is used here to the 'Insertion' operation in Database.
 * 4. The user will save the 'Medicine name'(translated) and 'Medicine description' in Database.
 */
class AddMedicineActivity : AppCompatActivity() {

    private val TAG = AddMedicineActivity::class.java.simpleName

    companion object {

        /**
         * Constant for Key value of 'Medicine' object
         */
        const val MEDICINE_OBJ = "medicine_obj"

        /**
         * This function opens up this Activity.
         *
         * @param activity : The invoking Activity.
         * @param medicine : The value passed in 'Intent', while opening this Activity.
         */
        fun beginActivity(activity: AppCompatActivity, medicine: Medicine) {
            val intent = Intent(activity, AddMedicineActivity::class.java)
            intent.putExtra(MEDICINE_OBJ, medicine)
            activity.startActivity(intent)
        }
    }

    private lateinit var edtMedicineName: EditText
    private lateinit var edtMedicineDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var medicine: Medicine

    /**
     * This function is Entry point to this Activity
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicine)
        medicine = intent.getSerializableExtra(MEDICINE_OBJ)!! as Medicine
        initViews()
        setViews()
    }


    /**
     * Initialize the views in this screen
     */
    private fun initViews() {
        edtMedicineName = findViewById(R.id.edt_medicine_name)
        edtMedicineDescription = findViewById(R.id.edt_medicine_description)
        btnSave = findViewById(R.id.btn_save)
    }

    /**
     * Set listeners and values to the views
     * A listener to button 'Save' is also set over here that has the 'Coroutine' to save the Medicine data in Local Database.
     */
    private fun setViews() {
        if (!medicine.medicineName.isBlank())
            edtMedicineName.setText(medicine.medicineName)

        btnSave.setOnClickListener {
            //Save Medicine to Local Database
            medicine.medicineDate = System.currentTimeMillis()
            medicine.medicineDescription = edtMedicineDescription.text.toString().trim()

            Log.d(
                TAG,
                "Medicine model inserted in DB -- \n Medicine Name : ${medicine.medicineName}" +
                        ", Medicine Date : ${medicine.medicineDate}, Medicine Description : ${medicine.medicineDescription}," +
                        " Medicine Language Code : ${medicine.medicineLangCode}"
            )
            GlobalScope.launch(Dispatchers.Main) {
                val rowId = MyApplication.getPharmacyDB().medicineDao().insertMedicine(medicine)
                if (rowId != 0L) {
                    MedicineListActivity.beginActivity(this@AddMedicineActivity)
                    finish()
                }
            }
        }
    }

}
