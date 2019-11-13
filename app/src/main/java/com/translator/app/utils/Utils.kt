package com.translator.app.utils

import android.widget.Toast

/**
 * 1. This class consists of all the utility functions
 * 2. For now it only consists of function to 'show toast messages'.
 * 3. but, in future it can also be used to deal with some validations, small calculations, small operations on data, etc.
 */
object Utils {

    /**
     * A function to show 'toast' messages to make it short hand, just by calling showToast(msg: String)',
     * Instead of calling long function  Toast.makeText(context : Context, msg, Toast.LENGTH_SHORT).show()'.
     * @param msg
     */
    fun showToast(msg: String) {
        Toast.makeText(MyApplication.getApplicationInstance(), msg, Toast.LENGTH_SHORT).show()
    }
}