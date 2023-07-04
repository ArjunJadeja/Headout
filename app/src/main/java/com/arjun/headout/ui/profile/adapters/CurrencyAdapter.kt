package com.arjun.headout.ui.profile.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.arjun.headout.R
import com.arjun.headout.data.model.Currency

class CurrencyAdapter(context: Context, options: List<Currency>) :
    ArrayAdapter<Currency>(context, R.layout.drop_down, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val currency = getItem(position)
        val text = "${currency?.name} (${currency?.symbol})"
        view.findViewById<TextView>(android.R.id.text1).text = text
        return view
    }
}