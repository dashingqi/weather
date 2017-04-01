package weather.dashingqi.com.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangqi on 2017/3/29.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
