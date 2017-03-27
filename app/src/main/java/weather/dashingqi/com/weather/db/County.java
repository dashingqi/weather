package weather.dashingqi.com.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangqi on 2017/3/27.
 */

public class County extends DataSupport {
    //ID
    private int id;
    //县名
    private String countyName;
    //当前县所对应的天气Id
    private String weatherId;
    //当前县所对应的城市的Id
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
