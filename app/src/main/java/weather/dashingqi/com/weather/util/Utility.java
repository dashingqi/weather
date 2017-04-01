package weather.dashingqi.com.weather.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weather.dashingqi.com.weather.db.City;
import weather.dashingqi.com.weather.db.County;
import weather.dashingqi.com.weather.db.Province;
import weather.dashingqi.com.weather.gson.Weather;

/**
 * 解析和处理服务器端返回的工具类
 * Created by zhangqi on 2017/3/27.
 */

public class Utility {
    /**
     * 解析和处理服务器返回省级的数据
     * @param response 数据
     * @return 返回true 表示解析成功 false 解析和处理 失败
     */
    public static  boolean handleProvinceResponse(String response){
        //非空判断
        if (!TextUtils.isEmpty(response)) {
            try {
                //将服务器返回的数据 设置到数组中 然后进行遍历
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    //取出数组中的数据 都是JSONObject类型的
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    //根据字段 获取到数据 设置到相应对象中
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //数据库进行数据添加
                    province.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理城市的数据
     * @param response 数据
     * @param provinceId 当前市所在省的Id
     * @return true 成功 false 失败
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)) {
            try {
                //将数据设置到数组中 遍历数组
                JSONArray cityArray = new JSONArray(response);
                for (int i=0;i<cityArray.length();i++){
                    JSONObject cityObject = cityArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return  true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理县城的数据
     * @param response 数据
     * @param cityId 当前县城县城所属的城市ID
     * @return true 成功 ，false 失败
     */
    public static  boolean handleCountyResponse(String response,int cityId){
        try {
            JSONArray countyArray = new JSONArray(response);
            for(int i=1;i<countyArray.length();i++){
                JSONObject countyObject = countyArray.getJSONObject(i);
                County county = new County();
                county.setCountyName(countyObject.getString("name"));
                county.setWeatherId(countyObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将返回的接送数据解析成Weather实体类
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
