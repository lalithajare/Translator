package com.translator.app.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 1. This class is base class for all ViewHolders in App.
 * 2. This class is used to implement 'ViewHolder' pattern recommended by Google.
 * 3. It is useful for smooth scrolling of screen
 *
 * @param itemView :
 *                The initialized view, that is passed to ViewHolder, from which the 'ViewHolder' retrieves TextViews,EditTextViews,Buttons,ImageViews,etc.
 */
abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * This function is called when the View is Bound to list.
     * @param model
     */
    abstract fun onBindView(model: Any)
}