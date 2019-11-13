package com.translator.app.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.R
import android.widget.BaseAdapter
import android.widget.TextView
import com.translator.app.models.Language

/**
 * 1. This class acts as the Adapter to Spinner.
 * 2. It does the working of initializing the View for Language List.
 *
 * @property mContext : The context of Activity that uses the spinner with this adapter*
 * @property list : The actual model list of 'Language' that are predetermined 'English,Korean,Vietnamese'.
 */
class LanguageAdapter(var mContext: Context, var list: List<Language>) : BaseAdapter() {

    /**
     * Function to get Item by position
     *
     * @param p0 : Position of Item in list
     * @return
     */
    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    /**
     * Fucntion to get Item's Id.
     *
     * @param p0 : ID of item that is generated internally
     * @return
     */
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    /**
     * The count of items in list.
     *
     * @return
     */
    override fun getCount(): Int {
        return list.size
    }

    /**
     * This function returns the initialized view.
     *
     * @param position : position of item in list
     * @param convertView : The view for recycling purpose
     * @param parent : The parent of View if any.
     * @return
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = View.inflate(mContext, R.layout.simple_list_item_1, null) as TextView
        textView.text = list[position].language
        return textView
    }

    /**
     * The view taht is shown when Spinner is expanded.
     *
     * @param position : position of item in list
     * @param convertView : The view for recycling purpose
     * @param parent : The parent of View if any.
     * @return
     */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = View.inflate(mContext, R.layout.simple_list_item_1, null) as TextView
        textView.text = list[position].language
        return textView
    }

}