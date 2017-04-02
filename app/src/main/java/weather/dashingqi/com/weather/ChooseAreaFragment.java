package weather.dashingqi.com.weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import weather.dashingqi.com.weather.db.City;
import weather.dashingqi.com.weather.db.County;
import weather.dashingqi.com.weather.db.Province;
import weather.dashingqi.com.weather.gson.Weather;
import weather.dashingqi.com.weather.util.HttpUtil;
import weather.dashingqi.com.weather.util.Utility;

/**
 * Created by zhangqi on 2017/3/28.
 */

public class ChooseAreaFragment extends Fragment {

    //为 省 城市 县 设定 级别
    public final  static int LEVEL_PROVINCE=0;
    public final  static  int LEVEL_CITY=1;
    public final static int LEVEL_COUNTY=2;

    //整体布局对象 view
    private View view;

    private Button btn_back;
    private TextView tv_title;
    private ListView lv_view;

    private ProgressDialog progressDialog;
    //数组适配器 给listview填充数据的
    private ArrayAdapter<String> adapter;
    private List dateList = new ArrayList();
    /**
     * 省的列表
     */
    private List<Province> provinceList;
    /**
     * 城市的列表
     */
    private List<City> cityList;
    /**
     * 县的列表
     */
    private List<County> countyList;
    /**
     * 选中的省
     */
    private Province selectedProince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 选中的县
     */
    private County selectedCounty;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area,container, false);
        btn_back = (Button) view.findViewById(R.id.btn_back);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        lv_view = (ListView) view.findViewById(R.id.lv_view);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, dateList);
        lv_view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    if(getActivity() instanceof  MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity  weatherActivity = (WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.srlSwipeRefresh.setRefreshing(true);
                       // String weatherIds = countyList.get(position).getWeatherId();
                        weatherActivity.requestWeather(weatherId);
                    }
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        //布局已加载 就去查询省级数据
        queryProvinces();
    }

    /**
     * 查询全国所有的省 优先从数据库中查询 没有 就从服务器中查询
     */
    private void queryProvinces() {
        tv_title.setText("中国");
        //设置返回按钮 在省级目录不可见
        btn_back.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
            if(provinceList.size()>0){
            dateList.clear();
            //遍历集合 取数据
            for (Province province :provinceList){
                dateList.add(province.getProvinceName());
            }
            //刷新适配器
            adapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else{
                String address = "http://guolin.tech/api/china";
                queryFromServer(address,"province");
            }
    }

    /**
     * 查询选中的省 的 所有城市的数据 先从数据库中查询 没有再从服务器中查询
     */
    private void queryCities() {
        tv_title.setText(selectedProince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?",String.valueOf(selectedProince.getId())).find(City.class);
        if(cityList.size()>0){
            dateList.clear();
            for (City city:cityList){
                dateList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else{
            int provinceCode = selectedProince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询选中的城市 的 所有县的数据 先从数据库中查询 再从服务器中查询
     */
    private void queryCounties() {
        tv_title.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        //进行数据库的查询
        countyList=DataSupport.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dateList.clear();
            for (County county : countyList){
                dateList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            lv_view.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            int provinceCode = selectedProince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }
    /**
     * 从服务器中查询数据
     * @param address 服务器地址
     * @param type 查询数据的类别
     */
    private void queryFromServer(String address, final String type) {
        showProcessDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProcessDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
                closeProcessDialog();
            }
        });
    }

    /**
     * 显示进度条对话框
     */
    private  void showProcessDialog(){
        if (progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }
    private void closeProcessDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
