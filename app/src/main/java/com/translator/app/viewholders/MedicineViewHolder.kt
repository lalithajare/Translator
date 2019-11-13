package com.translator.app.viewholders

import android.view.View
import android.widget.TextView
import com.translator.app.R
import com.translator.app.models.Medicine
import com.translator.app.screens.TranslateActivity

/**
 * 1. This class is a ViewHolder for Medicine model in 'MedicineListActivity'
 * 2. It sets the data to View passed in constructor.
 * 3. It extends Generic 'ViewHolder' for consistency through out the app.
 * @property mView :
 *              The initialized view, that is passed to ViewHolder, from which the 'ViewHolder' retrieves TextViews,EditTextViews,Buttons,ImageViews,etc.
 * @property callBack
 *              The callback is invoked when the 'ViewHolder'/Item in list is clicked.
 */
class MedicineViewHolder(var mView: View, var callBack: (Medicine) -> Unit) : ViewHolder(mView) {

    private var mMedicine: Medicine? = null

    private val txtMedicineName: TextView = mView.findViewById(R.id.txt_medicine_name)
    private val txtTranslated: TextView = mView.findViewById(R.id.txt_translated)

    /**
     * This fucntion is called when view is bound to list
     * The data to view is set in this screen.
     * @param model :
     *          This model is further typecasted to 'Medicine'
     */
    override fun onBindView(model: Any) {
        mMedicine = model as Medicine?

        if (!mMedicine?.medicineName.isNullOrEmpty())
            txtMedicineName.text = mMedicine?.medicineName

        if (mMedicine?.medicineLangCode != TranslateActivity.LangCode.ENGLISH.code) {
            txtTranslated.text =
                "${mView.context.getString(R.string.translated)} (${mMedicine?.medicineLangCode})"
            txtTranslated.visibility = View.VISIBLE
        } else {
            txtTranslated.visibility = View.GONE
        }

        mView.setOnClickListener {
            callBack(mMedicine!!)
        }
    }
}