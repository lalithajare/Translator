package com.translator.app.screens

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateModelManager
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.translator.app.R
import com.translator.app.adapters.LanguageAdapter
import com.translator.app.models.Language
import com.translator.app.models.Medicine

/**
 * 1. This Activity is opened when the TextView -> Medicine Name in 'EditMedicineActivity' is clicked.
 * 2. After doing translation the result is sent back to 'EditMedicineActivity'.
 * 3. The working of this Acitivity is similar to 'TranslateActivity'.
 * 4. The only difference is that this Activity is used for editing purpose.
 * 5. It only edits the medicine name.
 * 6. 'Medicine' object is passed from 'EditMedicineActivity' to this Activity
 */
class EditTranslateActivity : AppCompatActivity() {

    companion object {

        /**
         * value for request while opening 'EditTranslateActivity'
         */
        const val REQ_TRANSLATE = 78

        /**
         * value for key of 'Medicine' object
         */
        const val MEDICINE_OBJ = "medicine_obj"

        /**
         * This function opens up this Activity.
         *
         * @param activity : The invoking Activity.
         */
        fun beginActivityForResult(activity: AppCompatActivity, medicine: Medicine) {
            val intent = Intent(activity, EditTranslateActivity::class.java)
            intent.putExtra(MEDICINE_OBJ, medicine)
            activity.startActivityForResult(intent, REQ_TRANSLATE)
        }
    }

    /**
     * This enum consists of Language codes
     * @param code : The ISO code of Language
     */
    enum class LangCode(var code: String) {
        /**
         * Enum instance for Korean code
         */
        KOREAN("ko"),
        /**
         * Enum instance for Vietnamese code
         */
        VIETNAMESE("vi"),
        /**
         * Enum instance for English code
         */
        ENGLISH("en")
    }


    /**
     * This List consists of 'Predetermined' set of Languages
     */
    private val languageList = arrayListOf<Language>(
        Language(
            LangCode.KOREAN.code,
            "Korean"
        )
        ,
        Language(
            LangCode.VIETNAMESE.code,
            "Vietnamese"
        )
        ,
        Language(
            LangCode.ENGLISH.code,
            "English"
        )
    )

    private lateinit var edtInputText: EditText
    private lateinit var txtOutputText: TextView
    private lateinit var spnLanguage: AppCompatSpinner
    private lateinit var btnTranslate: Button
    private lateinit var btnUpdate: Button


    private lateinit var mAdapter: LanguageAdapter
    private var isVietnameseAvailable = false
    private var isKoreanAvailable = false

    /**
     * This variable keeps the of exactly which language the text is translated to.
     */
    private var languageTranslated = ""

    private var mMedicine: Medicine? = null

    /**
     * This is the entry point to Activity
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_translate)
        mMedicine = intent.getSerializableExtra(MEDICINE_OBJ) as Medicine
        initViews()
        setViews()
        setData()
        setAdapter()
        checkFirebaseLangModels()
    }

    /**
     * Initialize the views
     */
    private fun initViews() {
        spnLanguage = findViewById(R.id.spn_language)
        edtInputText = findViewById(R.id.edt_input_text)
        txtOutputText = findViewById(R.id.txt_output_text)
        btnTranslate = findViewById(R.id.btn_translate)
        btnUpdate = findViewById(R.id.btn_update)
    }

    /**
     * Set listeners to the views
     */
    private fun setViews() {
        btnTranslate.setOnClickListener {
            if (isKoreanAvailable && isVietnameseAvailable) {
                translateText()
            } else {
                Toast.makeText(this, getString(R.string.plz_wt_let_models_dwnld), Toast.LENGTH_LONG)
                    .show()
            }
        }

        btnUpdate.setOnClickListener {
            // Go to Add Medicine Activity
            mMedicine?.medicineName = txtOutputText.text.toString().trim()
            mMedicine?.medicineLangCode = languageTranslated
            val intent = intent
            intent.putExtra(MEDICINE_OBJ, mMedicine)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    /**
     * Sets the medicine name as an Input Text for translation
     *
     */
    private fun setData() {
        if (!TextUtils.isEmpty(mMedicine?.medicineName))
            edtInputText.setText(mMedicine?.medicineName)
    }


    /**
     * Language adapter for Spinner
     */
    private fun setAdapter() {

        if (mMedicine?.medicineLangCode!!.equals(LangCode.KOREAN.code)) {
            //Remove Vietnamese, as we don't have Korean - Vietnamese translator
            languageList.removeAt(1)
        } else if (mMedicine?.medicineLangCode!!.equals(LangCode.VIETNAMESE.code)) {
            //Remove Vietnamese, as we don't have Vietnamese - Korean translator
            languageList.removeAt(0)
        }

        mAdapter = LanguageAdapter(this, languageList)
        spnLanguage.adapter = mAdapter
    }

    /**
     * Start to the process of translation
     * And then does changes to the field 'languageTranslated' to show which language the text was translated.
     * After translation, the output text is set the the TextView.
     */
    private fun translateText() {
        var options: FirebaseTranslatorOptions? = null
        when {
            (spnLanguage.selectedItem as Language).langCode == LangCode.VIETNAMESE.code -> {
                options =
                    FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.VI)
                        .build()
                languageTranslated = LangCode.VIETNAMESE.code
            }
            (spnLanguage.selectedItem as Language).langCode == LangCode.KOREAN.code -> {
                options =
                    FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.KO)
                        .build()
                languageTranslated = LangCode.KOREAN.code
            }
            (spnLanguage.selectedItem as Language).langCode == LangCode.ENGLISH.code -> {
                detectAndTranslateLanguage()
                languageTranslated = LangCode.ENGLISH.code
                return
            }
        }
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options!!)
        translator.translate(edtInputText.text.toString().trim()).addOnSuccessListener { text ->
            txtOutputText.text = text
        }
    }

    /**
     * Automatically detects the entered text's language and translates it to English
     */
    private fun detectAndTranslateLanguage() {
        var options: FirebaseTranslatorOptions? = null

        val languageIdentifier = FirebaseNaturalLanguage.getInstance().languageIdentification
        languageIdentifier.identifyLanguage(edtInputText.text.toString().trim())
            .addOnSuccessListener { languageCode ->
                if (languageCode == LangCode.KOREAN.code) {
                    options = FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.KO)
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
                        .build()

                } else if (languageCode == LangCode.VIETNAMESE.code) {
                    options = FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.VI)
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
                        .build()

                }
                val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options!!)
                translator.translate(edtInputText.text.toString().trim())
                    .addOnSuccessListener { text ->
                        txtOutputText.text = text

                    }
            }
            .addOnFailureListener {
                // Model couldn’t be loaded or other internal error.
                // ...
            }
    }


    /**
     * Check if device consists of language models
     *  if not then download them.
     */
    private fun checkFirebaseLangModels() {
        val modelManager = FirebaseTranslateModelManager.getInstance()
        // Get translation models stored on the device.
        modelManager.getAvailableModels(FirebaseApp.getInstance())
            .addOnSuccessListener { models ->


                //Check for Vietnamese
                for (model in models) {
                    if (model.languageCode.toLowerCase() == LangCode.VIETNAMESE.code) {
                        isVietnameseAvailable = true
                        break
                    }
                }

                //Check for Korean
                for (model in models) {
                    if (model.languageCode.toLowerCase() == LangCode.KOREAN.code) {
                        isKoreanAvailable = true
                        break
                    }
                }

                if (!isVietnameseAvailable) {
                    downloadVietNameseModel(modelManager)
                }

                if (!isKoreanAvailable) {
                    downloadKoreanModel(modelManager)
                }

            }
            .addOnFailureListener {
                // Error.
                Log.d("MainAcitivity", it.localizedMessage)
            }
    }

    /**
     * Download the Korean language model.
     */
    private fun downloadKoreanModel(modelManager: FirebaseTranslateModelManager) {
        val model = FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.KO)
            .build()
        modelManager.downloadRemoteModelIfNeeded(model)
            .addOnSuccessListener {
                // Model downloaded.
                isKoreanAvailable = true
                Toast.makeText(this, "Korean language model downloaded", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                // Error.
                Toast.makeText(this, "Cannot download Korean language model", Toast.LENGTH_LONG)
                    .show()
                Log.d("MainAcitivity", it.localizedMessage)
            }
    }

    /**
     * Download the Vietnamese language model.
     */
    private fun downloadVietNameseModel(modelManager: FirebaseTranslateModelManager) {
        val model = FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.VI)
            .build()
        modelManager.downloadRemoteModelIfNeeded(model)
            .addOnSuccessListener {
                // Model downloaded.
                isVietnameseAvailable = true
                Toast.makeText(this, "Vietnamese language model downloaded", Toast.LENGTH_LONG)
                    .show()
            }
            .addOnFailureListener {
                // Error.
                Toast.makeText(this, "Cannot download Vietnamese language model", Toast.LENGTH_LONG)
                    .show()
                Log.d("MainAcitivity", it.localizedMessage)
            }
    }

}
