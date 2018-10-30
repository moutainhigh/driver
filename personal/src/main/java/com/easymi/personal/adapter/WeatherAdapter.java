package com.easymi.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.easymi.component.utils.TimeUtil;
import com.easymi.personal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/12/9 0009.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.Holder> {

    private Context context;
    private List<LocalDayWeatherForecast> forecasts;

    public WeatherAdapter(Context context) {
        this.context = context;
        forecasts = new ArrayList<>();
    }

    public void setForecasts(List<LocalDayWeatherForecast> forecasts) {
        this.forecasts = forecasts;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        LocalDayWeatherForecast weatherForecast = forecasts.get(position);
        if (weatherForecast.getDayWeather().contains("雷")) {
            holder.weatherImg.setImageResource(R.mipmap.therd_icon);
        } else if (weatherForecast.getDayWeather().contains("雪")) {
            holder.weatherImg.setImageResource(R.mipmap.snow_icon);
        } else if (weatherForecast.getDayWeather().contains("多云")) {
            holder.weatherImg.setImageResource(R.mipmap.cloudy_icon);
        } else if (weatherForecast.getDayWeather().contains("雨")) {
            holder.weatherImg.setImageResource(R.mipmap.rain_icon);
        } else {
            holder.weatherImg.setImageResource(R.mipmap.sun_day_icon);
        }


        String date = weatherForecast.getDate();
        long timestamp = TimeUtil.parseTime("yyyy-MM-dd", date);
        long todayStamp = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis()));

        if (timestamp == todayStamp) {
            holder.weatherTime.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.weatherTmp.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.weatherTime.setTextColor(context.getResources().getColor(R.color.text_default));
            holder.weatherTmp.setTextColor(context.getResources().getColor(R.color.text_default));
        }

        String dayAndWeek = TimeUtil.getTime("MM/dd", timestamp) + "  " + TimeUtil.getTime("EEEE", timestamp);
        holder.weatherTime.setText(dayAndWeek);

        holder.weatherTmp.setText(weatherForecast.getNightTemp() + "°  -  " + weatherForecast.getDayTemp() + "℃");
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        ImageView weatherImg;
        TextView weatherTmp;
        TextView weatherTime;

        public Holder(View itemView) {
            super(itemView);
            weatherImg = itemView.findViewById(R.id.weather_img);
            weatherTmp = itemView.findViewById(R.id.weather_tmp);
            weatherTime = itemView.findViewById(R.id.weather_time);
        }
    }
}
