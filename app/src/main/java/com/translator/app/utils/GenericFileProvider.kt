package com.translator.app.utils

import androidx.core.content.FileProvider

/**
 * 1. This class is descendant of 'FileProvider'
 * 2. This class serves no real purpose but is a convention when dealing with Android's security mechanism for accessing Files.
 * 3. It needs to be registered as 'FileProvider' in Manifest.
 */
class GenericFileProvider : FileProvider() {

}