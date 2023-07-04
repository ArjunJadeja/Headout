package com.arjun.headout.ui.profile.adapters

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.arjun.headout.R
import com.arjun.headout.data.model.Language

class LanguageAdapter(context: Context, options: List<Language>) :
    ArrayAdapter<Language>(context, R.layout.drop_down, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val language = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).apply {
            text = language?.nativeName
            gravity = Gravity.LEFT
        }
        return view
    }
}