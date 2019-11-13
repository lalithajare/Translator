package com.translator.app.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.translator.app.R
import com.translator.app.utils.CircleTransform
import com.translator.app.utils.FileManager
import com.translator.app.utils.Prefs
import com.translator.app.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


/**
 * 1. This Activity opens when user clicks on User Profile part in 'MainActivity'
 * 2. It is used for editing the existing user values.
 * 3. The updated 'User' object will be saved in Preferences.
 * 4. The Image of User will be saved in App's Directory.
 */
class EditProfileActivity : AppCompatActivity() {

    private val TAG = EditProfileActivity::class.java.simpleName

    companion object {

        /**
         * The value for request while opening 'EditProfileActivity'
         */
        const val REQ_EDIT_PROFILE = 342

        /**
         * This function opens up this Activity.
         *
         * @param activity : The invoking Activity.
         */
        fun beginActivityForResult(activity: AppCompatActivity) {
            activity.startActivityForResult(
                Intent(activity, EditProfileActivity::class.java),
                REQ_EDIT_PROFILE
            )
        }
    }

    private val REQ_IMAGE_PERMS = 788

    private lateinit var imgUser: ImageView
    private lateinit var edtName: EditText
    private lateinit var edtHeight: EditText
    private lateinit var edtWeight: EditText
    private lateinit var edtMedConds: EditText
    private lateinit var btnUpdate: Button
    private lateinit var pgBar: ProgressBar


    private var cameraImageUri: Uri? = null
    private var galleryImageUri: Uri? = null

    /**
     * This is instance of 'User' object that is loaded from preferences.
     */
    private val user = Prefs.user

    /**
     * This is the entry point to Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        initViews()
        setViews()
        setData()
    }

    /**
     * This function initializes all the views in screen
     */
    private fun initViews() {
        imgUser = findViewById(R.id.img_user)
        edtName = findViewById(R.id.edt_name)
        edtHeight = findViewById(R.id.edt_height)
        edtWeight = findViewById(R.id.edt_weight)
        edtMedConds = findViewById(R.id.edt_med_conds)
        btnUpdate = findViewById(R.id.btn_update)
        pgBar = findViewById(R.id.pg_bar)
    }

    /**
     * This function saves the changes done to User object and stores it in preferences.
     *
     */
    private fun saveData() {
        setValues()
        Prefs.user = user
        if (cameraImageUri != null || galleryImageUri != null) {
            if (cameraImageUri != null)
                saveImageInDir(cameraImageUri!!, FileManager.getProfilePicFile())
            else
                saveImageInDir(galleryImageUri!!, FileManager.getProfilePicFile())
        }
    }


    /**
     * This function set the listeners to the Views
     */
    @SuppressLint("NewApi")
    private fun setViews() {
        btnUpdate.setOnClickListener {
            if (validInput()) {
                GlobalScope.launch(Dispatchers.Main) {
                    pgBar.visibility = View.VISIBLE
                    withContext(Dispatchers.Default) {
                        saveData()
                    }
                    pgBar.visibility = View.GONE
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
        imgUser.setOnClickListener {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), REQ_IMAGE_PERMS
                    )
                } else {
                    showImageDialogue()
                }
            } else {
                showImageDialogue()
            }
        }
    }

    /**
     * This function shows the "Select image" popup
     *  From where the User can select if to select 'Camera' or 'Gallery'
     */
    private fun showImageDialogue() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose From Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                getCameraImage()
                dialog.dismiss()
            } else if (options[item] == "Choose From Gallery") {
                getGalleryImage()
                dialog.dismiss()
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    /**
     * This function is used to set the values to User object in this class.
     * The values come from 'User..object saved in preferences
     */
    private fun setData() {
        if (user != null) {
            if (FileManager.getProfilePicFile().exists()) {
                Picasso.get().load(FileManager.getProfilePicFile())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .transform(CircleTransform())
                    .placeholder(R.drawable.ic_user)
                    .into(imgUser)
            }
            if (!TextUtils.isEmpty(user.name)) {
                edtName.setText(user.name)
            }
            edtHeight.setText(user.height.toString())
            edtWeight.setText(user.weight.toString())
            if (!TextUtils.isEmpty(user.medicalConditions)) {
                edtMedConds.setText(user.medicalConditions)
            }
        }
    }

    /**
     * This function is used to set the updated values to User object in this class
     */
    private fun setValues() {
        user?.name = edtName.text.toString().trim()
        user?.height = edtHeight.text.toString().trim().toDouble()
        user?.weight = edtWeight.text.toString().trim().toDouble()
        user?.medicalConditions = edtMedConds.text.toString().trim()
    }

    /**
     * This function validates the entered input.
     * If any one of the value is invalid it returns false else true.
     */
    private fun validInput(): Boolean {
        if (!TextUtils.isEmpty(edtName.text.toString().trim()))
            if (!TextUtils.isEmpty(edtHeight.text.toString().trim()))
                if (!TextUtils.isEmpty(edtWeight.text.toString().trim()))
                    if (!TextUtils.isEmpty(edtMedConds.text.toString().trim()))
                        return true
                    else
                        Utils.showToast(getString(R.string.plz_enter_med_conds))
                else
                    Utils.showToast(getString(R.string.plz_enter_weight))
            else
                Utils.showToast(getString(R.string.plz_enter_height))
        else
            Utils.showToast(getString(R.string.plz_enter_name))
        return false
    }

    /**
     * This function creates and returns a reference to the FilesDir in Internal memory of App.
     */
    private fun getOutputUri(): Uri {
        val photoFile = File(filesDir, "ProfilePIC.jpg")
        return FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".fileprovider",
            photoFile
        )
    }

    /**
     * This function creates and start the Camera Intent / MediaStore.ACTION_IMAGE_CAPTURE.
     *  Hence the Camera is opened up the set request code.
     */
    private fun getCameraImage() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, getOutputUri())
        takePicture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(takePicture, 0)
    }

    /**
     * This function creates and start the External Content Intent / MediaStore.EXTERNAL_CONTENT_URI.
     *  Hence the Gallery/File Explorer is opened up the set request code.
     */
    private fun getGalleryImage() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(pickPhoto, 1)
    }


    /**
     * This function takes the callback from Gallery screen or Camera
     *  Then manages the acquired image data chosen by User.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            //CAMERA
            0 -> if (resultCode == Activity.RESULT_OK) {
                Picasso.get().load(getOutputUri())
                    .transform(CircleTransform())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imgUser)
                cameraImageUri = getOutputUri()
                galleryImageUri = null
            }
            //GALLERY
            1 -> if (resultCode == Activity.RESULT_OK) {
                Picasso.get().load(imageReturnedIntent!!.data)
                    .transform(CircleTransform())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imgUser)
                galleryImageUri = imageReturnedIntent.data
                cameraImageUri = null
            }
        }
    }

    private fun saveImageInDir(inputUri: Uri, fileToWrite: File) {
        val bitmap = BitmapFactory.decodeStream(
            contentResolver.openInputStream(inputUri)
        )
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, fileToWrite.outputStream())
    }


    /**
     * This fucntion is a callback for Permissions approved/declined by User for 'Camera' and 'External Content'
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_IMAGE_PERMS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            FileManager.createDirectory()
            showImageDialogue()
        } else {
            Utils.showToast(getString(R.string.cannot_select_image))
        }
    }

}
