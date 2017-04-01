package weather.dashingqi.com.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangqi on 2017/3/29.
 */

public class Forecast {
    public String data;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class More{
    @SerializedName("txt_d")
    public String info;
   }
   public class Temperature{
       public String max;
       public String min;
   }
}