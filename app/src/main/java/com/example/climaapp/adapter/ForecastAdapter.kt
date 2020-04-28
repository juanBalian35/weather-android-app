package com.example.climaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climaapp.vo.WeatherResponse
import com.example.climaapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// Adapter for the recyclerView that displays upcoming days
class ForecastAdapter(private val mContext: Context,
                      private val mForecastList: List<WeatherResponse.Daily>) : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDay: TextView = itemView.findViewById(R.id.text_day)
        var ivForecast: ImageView = itemView.findViewById(R.id.image_forecast_daily)
        var tvMinTemp: TextView = itemView.findViewById(R.id.text_min_temp)
        var tvMaxTemp: TextView = itemView.findViewById(R.id.text_max_temp)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val params = viewHolder.itemView.layoutParams as RecyclerView.LayoutParams

        when(position){
            0 -> params.leftMargin = 52
            mForecastList.lastIndex -> params.rightMargin = 52
        }
        viewHolder.itemView.layoutParams = params

        val forecast: WeatherResponse.Daily = mForecastList[position]

        val sdf = SimpleDateFormat("E", Locale("es","ES"))
        val date = Date(forecast.dt * 1000L)
        viewHolder.tvDay.text = sdf.format(date).capitalize()

        val resourceName = "drawable/ic_" + forecast.weather!![0].icon
        val imageResource = mContext.resources.getIdentifier(resourceName,
                                                          null, mContext.packageName)
        viewHolder.ivForecast.setImageResource(imageResource)

        val minTemp: String = forecast.temp!!.min.roundToInt().toString() + mContext.getString(R.string.degreeSymbol)
        val maxTemp: String = forecast.temp!!.max.roundToInt().toString() + mContext.getString(R.string.degreeSymbol)
        viewHolder.tvMinTemp.text = minTemp
        viewHolder.tvMaxTemp.text = maxTemp
    }

    override fun getItemCount(): Int {
        return mForecastList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val contactView = inflater.inflate(R.layout.item_forecast, parent, false)

        return ViewHolder(contactView)
    }
}