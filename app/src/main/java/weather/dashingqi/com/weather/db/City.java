package weather.dashingqi.com.weather.db;

import org.litepal.crud.DataSupport;

/**
 * 数据库中 city表
 * Created by zhangqi on 2017/3/27.
 */

public class City extends DataSupport {
    //城市的ID
    private int id;
    //城市名
    private String cityName;
    //城市代号
    private String cityCode;
    //当前市所属省的ID
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
