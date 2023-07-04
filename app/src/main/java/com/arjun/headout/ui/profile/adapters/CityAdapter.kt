package com.arjun.headout.ui.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.arjun.headout.R
import com.arjun.headout.data.model.City

class CityAdapter(context: Context, private val cities: List<City>) :
    ArrayAdapter<City>(context, R.layout.drop_down, cities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.drop_down, parent, false)
        }
        val city = getItem(position)
        val cityName = city.name?.get("en") ?: ""
        (view as TextView).text = cityName
        return view
    }

    override fun getItem(position: Int): City {
        return cities[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return cities.size
    }
}
