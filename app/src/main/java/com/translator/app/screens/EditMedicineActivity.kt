package com.translator.app.screens

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.translator.app.R
import com.translator.app.models.Medicine
import com.translator.app.utils.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 1. This Activity opens up when the User comes from 'MedicineDetailsActivity' by pressing button 'Edit'.
 * 2. The main function of this class is to save the updated medicine with description into the locally created database.
 * 3. The concept of 'Coroutines' in kotlin is used here to the 'Updating' operation in Database.
 * 4. The user will save the 'Medicine name'(translated) and 'Medicine description' in Database.
 * 5. The functionality to edit 'Medicine name' is not implemented in this screen
 */
class EditMedicineActivity : AppCompatActivity() {

    private val TAG = EditMedicineActivity::class.java.simpleName

    companion object {

        /**
         * The key for 'Medicine' object
         */
        const val MEDICINE_OBJ = "medicine_obj"

        /**
         * The request value while opening 'EditMedicineActivity'
         */
        private const val REQ_EDIT_MED = 82

        /**
         * This function opens up this Activity.
         *
         * @param activity : The invoking Activity.
         * @param medicine : The value passed in 'Intent', while opening this Activity.
         */
        fun beginActivity(activity: AppCompatActivity, medicine: Medicine) {
            val intent = Intent(activity, EditMedicineActivity::class.java)
            intent.putExtra(MEDICINE_OBJ, medicine)
            activity.startActivity(intent)
        }

        /**
         * This function opens up this Activity and expects some result_code.
         *
         * @param activity : The invoking Activity.
         * @param medicine : The value passed in 'Intent', while opening this Activity.
         */
        fun beginActivityForResult(activity: AppCompatActivity, medicine: Medicine) {
            val intent = Intent(activity, EditMedicineActivity::class.java)
            intent.putExtra(MEDICINE_OBJ, medicine)
            activity.startActivityForResult(intent, REQ_EDIT_MED)
        }
    }

    private lateinit var txtMedicineName: TextView
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
        setContentView(R.layout.activity_edit_medicine)
        medicine = intent.getSerializableExtra(MEDICINE_OBJ)!! as Medicine
        initViews()
        setViews()
        setData()
    }

    /**
     * Initialize the views in this screen
     */
    private fun initViews() {
        txtMedicineName = findViewById(R.id.txt_medicine_name)
        edtMedicineDescription = findViewById(R.id.edt_medicine_description)
        btnSave = findViewById(R.id.btn_save)
    }

    /**
     * Set listeners and values to the views
     * A listener to button 'Update' is also set over here that has the 'Coroutine' to save the Medicine data in Local Database.
     */
    private fun setViews() {
        txtMedicineName.setOnClickListener {
            EditTranslateActivity.beginActivityForResult(this, medicine)
        }

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
                val rowsUpdated =
                    MyApplication.getPharmacyDB().medicineDao().updateMedicine(medicine)
                if (rowsUpdated != 0) {
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
                    val intent = intent
                    intent.putExtra(MEDICINE_OBJ, medicine)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    /**
     * This function sets the data to Medicine fields acquired from Local database.
     *
     */
    private fun setData() {
        if (!medicine.medicineName.isBlank())
            txtMedicineName.text = medicine.medicineName

        if (!medicine.medicineDescription.isBlank())
            edtMedicineDescription.setText(medicine.medicineDescription)
    }

    /**
     * This is a callback function to check the result from further opened activity,i.e, Edit Profile Activity
     * If the result is 'RESULT_OK' only then the function is called.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EditTranslateActivity.REQ_TRANSLATE) {
                medicine =
                    data!!.getSerializableExtra(EditTranslateActivity.MEDICINE_OBJ) as Medicine
                setData()
            }
        }
    }
}
