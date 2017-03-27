package weather.dashingqi.com.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Weather数据库 中 省 的表
 * Created by zhangqi on 2017/3/27.
 */

public class Province extends DataSupport{
    //id
    private int id;
    //省名
    private String provinceName;
    //省的代号
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
