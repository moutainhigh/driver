package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;
import com.easymi.personal.adapter.WeatherAdapter;

import java.util.List;

/**
 * Created by developerLzh on 2017/12/7 0007.
 */

public class WeatherActivity extends RxBaseActivity implements WeatherSearch.OnWeatherSearchListener {

    private WeatherSearch weatherSearch;

    private ImageView todayPic;
    private TextView todayTem;
    private TextView weatherCity;
    private TextView todayType;
    private TextView reportTime;

    private LinearLayout wetherBg;

    private RecyclerView weatherRecycler;
    private WeatherAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_weather;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        todayPic = findViewById(R.id.today_pic);
        todayTem = findViewById(R.id.today_temperature);
        weatherCity = findViewById(R.id.weather_city);
        todayType = findViewById(R.id.today_type);
        reportTime = findViewById(R.id.report_time);

        wetherBg = findViewById(R.id.weather_bg);

        weatherRecycler = findViewById(R.id.forecast_recycler);

        adapter = new WeatherAdapter(this);
        weatherRecycler.setAdapter(adapter);
        weatherRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        searchLiveWeather();
        searchForecast();
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(v -> finish());
        cusToolbar.setTitle(R.string.weather_forecast);
    }

    private void searchLiveWeather() {
        if (null == weatherSearch) {
            weatherSearch = new WeatherSearch(this);
            weatherSearch.setOnWeatherSearchListener(this);
        }
        WeatherSearchQuery liveQuery = new WeatherSearchQuery(EmUtil.getLastLoc().adCode, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        weatherSearch.setQuery(liveQuery);
        weatherSearch.searchWeatherAsyn(); //异步搜索

    }

    private void searchForecast() {
        if (null == weatherSearch) {
            weatherSearch = new WeatherSearch(this);
            weatherSearch.setOnWeatherSearchListener(this);
        }
        WeatherSearchQuery forecastQuery = new WeatherSearchQuery(EmUtil.getLastLoc().adCode, WeatherSearchQuery.WEATHER_TYPE_FORECAST);
        weatherSearch.setQuery(forecastQuery);
        weatherSearch.searchWeatherAsyn(); //异步搜索
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive weatherlive = weatherLiveResult.getLiveResult();
                String fabuTime = weatherlive.getReportTime();
                if (weatherlive.getReportTime().contains(" ")) {
                    fabuTime = fabuTime.split(" ")[1];
                    if (fabuTime.contains(":")) {
                        String[] hourMinSec = fabuTime.split(":");
                        if (hourMinSec.length == 3) {
                            fabuTime = hourMinSec[0] + ":" + hourMinSec[1];
                        }
                    }
                }
                reportTime.setText(fabuTime + getString(R.string.fabu));
                todayType.setText(weatherlive.getWeather());
                todayTem.setText(weatherlive.getTemperature() + "°");
                weatherCity.setText(EmUtil.getLastLoc().city);
                if (weatherlive.getWeather() != null && !TextUtils.isEmpty(weatherlive.getWeather())) {
                    if (weatherlive.getWeather().contains("雷")) {
                        todayPic.setImageResource(R.mipmap.therd_icon);
                        wetherBg.setBackgroundResource(R.mipmap.thundery_sky);
                    } else if (weatherlive.getWeather().contains("雪")) {
                        todayPic.setImageResource(R.mipmap.snow_icon);
                        wetherBg.setBackgroundResource(R.mipmap.snow_day);
                    } else if (weatherlive.getWeather().contains("多云")) {
                        todayPic.setImageResource(R.mipmap.cloudy_icon);
                        wetherBg.setBackgroundResource(R.mipmap.cloudy_day);
                    } else if (weatherlive.getWeather().contains("雨")) {
                        todayPic.setImageResource(R.mipmap.rain_icon);
                        wetherBg.setBackgroundResource(R.mipmap.rain_day);
                    } else {
                        todayPic.setImageResource(R.mipmap.sun_day_icon);
                        int hour = Integer.parseInt(TimeUtil.getTime("HH:mm", System.currentTimeMillis()).split(":")[0]);
                        if (hour > 6 && hour < 18) {
                            wetherBg.setBackgroundResource(R.mipmap.sun_day);
                        } else {
                            wetherBg.setBackgroundResource(R.mipmap.night);
                        }
                    }
                }
            } else {
                ToastUtil.showMessage(WeatherActivity.this, getString(R.string.query_live_failed));
            }
        } else {
            ToastUtil.showMessage(WeatherActivity.this, getString(R.string.query_live_failed));
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int rCode) {
        if (rCode == 1000) {
            if (null != localWeatherForecastResult && localWeatherForecastResult.getForecastResult() != null) {
                LocalWeatherForecast localWeatherForecast = localWeatherForecastResult.getForecastResult();
                if (null != localWeatherForecast) {
                    List<LocalDayWeatherForecast> weatherForecasts = localWeatherForecast.getWeatherForecast();
                    if (weatherForecasts != null && weatherForecasts.size() != 0) {
                        adapter.setForecasts(weatherForecasts);
                    } else {
                        ToastUtil.showMessage(WeatherActivity.this, getString(R.string.query_forecast_failed));
                    }
                } else {
                    ToastUtil.showMessage(WeatherActivity.this, getString(R.string.query_forecast_failed));
                }
            } else {
                ToastUtil.showMessage(WeatherActivity.this, getString(R.string.query_forecast_failed));
            }
        } else {
            ToastUtil.showMessage(WeatherActivity.this, getString(R.string.query_forecast_failed));
        }
    }
}
