package com.translator.app.screens

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.translator.app.R
import com.translator.app.adapters.MedicineAdapter
import com.translator.app.models.Medicine
import com.translator.app.utils.MyApplication
import kotlinx.coroutines.*

/**
 * 1. This Activity opens up when '3rd section' -> 'My medicines' in Home screen of App (MainActivity) is clicked.
 * 2. The purpose of this screen is to show the updated list of all the medicines in Local Database.
 * 3. On clicking one of the items in list 'MedicineDetailsActivity' is opened up.
 * 4. This Activity listens to the result-code from 'MedicineDetailsActivity'.
 * 5. If the result-code is 'RESULT_OK', the list is reloaded, indicating that there was a change made in some od the item.
 */
class MedicineListActivity : AppCompatActivity() {

    private val TAG = MedicineListActivity::class.java.simpleName


    companion object {

        /**
         * This function opens up this Activity.
         *
         * @param activity : The invoking Activity.
         */
        fun beginActivity(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, MedicineListActivity::class.java))
        }
    }

    private lateinit var rvMedicine: RecyclerView
    private lateinit var txtNoItems: TextView

    private lateinit var mAdapter: MedicineAdapter

    /**
     * Holds the objects fetched from Local Database.
     */
    private val medicineList = mutableListOf<Medicine>()

    /**
     * This is the entry point to Activity
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_list)
        initViews()
        setAdapter()
        loadMedicinesFromDB()
    }

    private fun initViews() {
        rvMedicine = findViewById(R.id.rv_medicine)
        txtNoItems = findViewById(R.id.txt_no_items)
    }

    private fun setAdapter() {
        mAdapter = MedicineAdapter(medicineList) { medicine ->
            Log.d(TAG, "Medicine clicked : ${medicine.medicineName}")
            MedicineDetailsActivity.beginActivityForResult(this, medicine.medicineId)
        }
        rvMedicine.adapter = mAdapter
        rvMedicine.layoutManager = LinearLayoutManager(this)
        rvMedicine.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
    }

    /**
     * This function loads all the medicine from Local Database'
     * Also notifies the adapter that data has been loaded, 'mAdapter.notifyDataSetChanged()'
     */
    private fun loadMedicinesFromDB() {
        //Get all medicines from database
        GlobalScope.launch(Dispatchers.Main) {
            val list = MyApplication.getPharmacyDB().medicineDao().getMedicineList()
            if (!list.isEmpty()) {
                medicineList.addAll(list)
                mAdapter.notifyDataSetChanged()
                txtNoItems.visibility = View.GONE
            } else {
                txtNoItems.visibility = View.VISIBLE
            }
        }
    }

    /**
     * This is a callback function to check the result from further opened activity,i.e, 'MedicineDetailsActivity'
     * If the result is 'RESULT_OK' only then the list is cleared and reloaded.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            medicineList.clear()
            loadMedicinesFromDB()
        }
    }


}
