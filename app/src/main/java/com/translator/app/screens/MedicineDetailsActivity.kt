package com.translator.app.screens

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.translator.app.R
import com.translator.app.models.Medicine
import com.translator.app.utils.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 1. This Activity opens up after the 'item' in 'MedicineListActivity' is clicked
 * 2. The purpose of this class is to show detailed information about the medicine.
 * 3. 'Medicine Id' from 'item' in 'MedicineListActivity' is passed to this Activity.
 * 4. This class then fetches the 'Medicine' data from Local Database, Kotlin-Coroutine is used for this purpose.
 */
class MedicineDetailsActivity : AppCompatActivity() {

    companion object {

        /**
         * The key for 'Medicine' Object
         */
        const val MEDICINE_ID = "medicine_id"

        /**
         * The request value ehile opening 'MedicineDetailsActivity'
         */
        private const val REQ_MED_DETAILS = 4243

        /**
         * This function opens up this Activity.
         *
         * @param activity : The invoking Activity.
         * @param medicineId : The ID of 'Medicine' that is to be retrieved from Local database.
         */
        fun beginActivity(activity: AppCompatActivity, medicineId: String) {
            val intent = Intent(activity, MedicineDetailsActivity::class.java)
            intent.putExtra(MEDICINE_ID, medicineId)
            activity.startActivity(intent)
        }

        /**
         * This function opens up this Activity expecting some result from this Activity.
         *
         * @param activity : The invoking Activity.
         * @param medicineId : The ID of 'Medicine' that is to be retrieved from Local database.
         */
        fun beginActivityForResult(activity: AppCompatActivity, medicineId: Long) {
            val intent = Intent(activity, MedicineDetailsActivity::class.java)
            intent.putExtra(MEDICINE_ID, medicineId)
            activity.startActivityForResult(intent, REQ_MED_DETAILS)
        }
    }

    private var mMedicine: Medicine? = null
    private lateinit var rgLangTabs: RadioGroup
    private lateinit var txtMedicineName: TextView
    private lateinit var txtMedicineDescription: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    /**
     * This is the variable, whose value is passed from 'MedicineListActivity'
     * On the basis of this variable, the Medicine details from Lcoal database is fetched.
     */
    private var medicineId = 0L

    /**
     * This is the entry point to Activity
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_details)
        medicineId = intent.getLongExtra(MEDICINE_ID, 0L)
        initViews()
        setViews()
    }

    /**
     * This function is called when User revisits the Activity without Activity been killed.
     * All details are fetched again whenever this function calls
     */
    override fun onResume() {
        super.onResume()
        getMedicineDetails(medicineId)
    }

    /**
     * This function get the medicine details from Local DB.
     * Coroutine is used while fetching data from local DB.
     * @param medicineId
     *  : The value by which correct medicine details are fetched.
     */
    private fun getMedicineDetails(medicineId: Long) {
        GlobalScope.launch(Dispatchers.Main) {
            mMedicine = MyApplication.getPharmacyDB().medicineDao().getMedicine(medicineId)
            if (mMedicine != null) {
                setData()
            }
        }
    }

    /**
     * This function initializes all the views in screen
     */
    private fun initViews() {
        rgLangTabs = findViewById(R.id.rg_lang_tabs)
        txtMedicineName = findViewById(R.id.txt_medicine_name)
        txtMedicineDescription = findViewById(R.id.txt_medicine_description)
        btnEdit = findViewById(R.id.btn_edit)
        btnDelete = findViewById(R.id.btn_delete)
    }

    /**
     * This function set the listeners to all the views in screen.
     * It determines next steps when a particular view is clicked.
     */
    private fun setViews() {
        btnEdit.setOnClickListener {
            EditMedicineActivity.beginActivityForResult(this, mMedicine!!)
        }
        btnDelete.setOnClickListener {
            showDeleteDialogue()
        }
    }

    /**
     * When user clicks on 'Delete', User is prompted with confirmation dialogue.
     * This function is one that initiates the dialogue.
     */
    private fun showDeleteDialogue() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.are_you_sure_you_want_to_delete)
            .setPositiveButton(R.string.yes) { dialog, id ->
                //Delete
                GlobalScope.launch(Dispatchers.Main) {
                    val rowsDeleted =
                        MyApplication.getPharmacyDB().medicineDao().deleteMedicine(mMedicine!!)
                    if (rowsDeleted != 0) {
                        dialog.dismiss()
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
            .setNegativeButton(R.string.no,
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
        // Create the AlertDialog object and return it
        builder.create().show()
    }

    /**
     * The loaded is set to the views in screen
     */
    private fun setData() {
        when (mMedicine?.medicineLangCode) {
            TranslateActivity.LangCode.ENGLISH.code -> {
                rgLangTabs.check(R.id.rb_english)
            }
            TranslateActivity.LangCode.VIETNAMESE.code -> {
                rgLangTabs.check(R.id.rb_vietnamese)
            }
            TranslateActivity.LangCode.KOREAN.code -> {
                rgLangTabs.check(R.id.rb_korean)
            }
        }
        txtMedicineName.text = mMedicine?.medicineName
        txtMedicineDescription.text = mMedicine?.medicineDescription
    }

    /**
     * This is a callback function to check the result from further opened activity,i.e, Edit Profile Activity.
     * If the result from 'EditMedicineActivity' is 'RESULT_OK' only then result for this Activity is set as 'RESULT_OK'.
     * Which is further used in 'MedicineListActivity'.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
        }
    }


}