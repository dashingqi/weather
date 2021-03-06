package weather.dashingqi.com.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import weather.dashingqi.com.weather.gson.Forecast;
import weather.dashingqi.com.weather.gson.Weather;
import weather.dashingqi.com.weather.service.AutoUpdateService;
import weather.dashingqi.com.weather.util.HttpUtil;
import weather.dashingqi.com.weather.util.Utility;

/**
 * Created by zhangqi on 2017/3/30.
 */

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoTxt;
    private LinearLayout forecastLayout;
    private TextView aqiTxt;
    private TextView pm25Txt;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public  SwipeRefreshLayout srlSwipeRefresh;
    private Button navButton;
    public  DrawerLayout drawerLayout;
    private String mWeatherId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather);
        initView();
        //界面一初始化 就去加载图片 再次进入
        loadBingPic();

    }

    /**
     * 初始化各种控件
     */
    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.sv_weather_layout);
        titleCity = (TextView) findViewById(R.id.tv_title_city);
        titleUpdateTime = (TextView) findViewById(R.id.tv_title_update_data);
        degreeText = (TextView) findViewById(R.id.tv_degree_text);
        weatherInfoTxt = (TextView) findViewById(R.id.tv_weather_info_txt);
        forecastLayout = (LinearLayout) findViewById(R.id.ll_forecast_layout);
        aqiTxt = (TextView) findViewById(R.id.tv_aqi_txt);
        pm25Txt = (TextView) findViewById(R.id.tv_pm25_txt);
        comfortText = (TextView) findViewById(R.id.tv_comfort_text);
        carWashText = (TextView) findViewById(R.id.tv_car_wash_text);
        sportText = (TextView) findViewById(R.id.tv_sport_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        srlSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.srl_swipe_refresh);
        //设置向下拉 刷新的进度条颜色
        srlSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        navButton = (Button) findViewById(R.id.btn_nav);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if(weatherString!=null){
            //有缓存时直接解析天气的数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId =weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //没有缓存是 就去服务器去取数据
             mWeatherId = getIntent().getStringExtra("weather_id");
             weatherLayout.setVisibility(View.INVISIBLE);
              requestWeather(mWeatherId);
        }
        //为swiperefersh 设置监听事件 有刷新 就去服务器取数据
        srlSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {

                requestWeather(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bing_pic = prefs.getString("bing_pic", null);
        if(bing_pic!=null){
            Glide.with(this).load(bing_pic).into(bingPicImg);
        }else{
            loadBingPic();
        }
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("bing_pic",bingPic);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    /**
     * 根据天气id请求城市天气信息
     * @param weatherId
     */
    public  void requestWeather(final String weatherId) {
        final String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+
                "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        //隐藏刷新的进度条
                        srlSwipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putString("weather",responseText);
                            //此处获取WeatherId 当再次选择城市 后刷新不会再次重回第一次选择的城市天气界面
                            //此时刷新去服务器 的WeatherId就是现在选择的页面
                            mWeatherId=weather.basic.weatherId;
                            edit.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //隐藏刷新的进度条
                        srlSwipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
    /**
     * 处理并展示Weather类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoTxt.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.tv_date_txt);
                TextView infoText = (TextView) view.findViewById(R.id.tv_info_txt);
                TextView maxText = (TextView) view.findViewById(R.id.tv_max_txt);
                TextView minText = (TextView) view.findViewById(R.id.tv_min_txt);

                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                aqiTxt.setText(weather.aqi.city.aqi);
                pm25Txt.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运动指数：" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else{
            Toast.makeText(getApplicationContext(),"更新失败",Toast.LENGTH_SHORT).show();
        }
    }
}
