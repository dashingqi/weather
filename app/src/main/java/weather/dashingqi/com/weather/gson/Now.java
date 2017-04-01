package weather.dashingqi.com.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangqi on 2017/3/29.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
