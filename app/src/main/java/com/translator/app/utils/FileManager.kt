package com.translator.app.utils

import android.os.Environment
import java.io.File

/**
 * 1. This class is used to create the App's directory structure
 * 2. It creates directory structure where the Profile picture is saved.
 * 3. It overwrites the previous file whe n a new Profile picture is set.
 * 4. It also assits in retrieving the File of 'Profile Picture'.
 */
object FileManager {

    /**
     * The path to the image directory in App's directory
     */
    private val IMAGE_PATH = "${Environment.getExternalStorageDirectory()}/Pharma_Translator/Images"

    /**
     * The name of Profile Image File.
     */
    private const val PROFILE_FILE_NAME = "Profile.jpg"

    /**
     * This function initially creates the directory structure.
     *
     */
    fun createDirectory() {
        if (!File(IMAGE_PATH).exists()) {
            File(IMAGE_PATH).mkdirs()
        }
    }

    /**
     * This function returns the 'File' object of Profile picture
     *
     * @return
     */
    fun getProfilePicFile(): File {
        val file = File(File(IMAGE_PATH), PROFILE_FILE_NAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    /**
     * This function deletes the profile picture file from App's directory
     *
     * @return
     */
    fun deleteProfilePicFile(): Boolean {
        val file = File(File(IMAGE_PATH), PROFILE_FILE_NAME)
        if (file.exists()) {
            file.delete()
            return true
        }
        return false
    }

}