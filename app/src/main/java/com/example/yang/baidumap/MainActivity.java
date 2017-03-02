package com.example.yang.baidumap;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{

    MapView mMapView=null;
    Button btn_satelite,btn_nomal,btn_white;
    TextView tv_location;


    /**
     * 定位相关
     * */
    LocationClient mLocClient;
    MyLocationData  locData=null;

    public MyLocationListener mListeners= new MyLocationListener();

    View viewCache=null;



    public LocationClient mLocationClient=null;
    public BDLocationListener mListener=new MyLocationListener();
    BaiduMap mBaiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mMapView= (MapView) findViewById(R.id.bmapView);
        btn_satelite= (Button) findViewById(R.id.btn_satellite);
        btn_nomal= (Button) findViewById(R.id.btn_nomal);
        btn_white= (Button) findViewById(R.id.btn_white);
        tv_location= (TextView) findViewById(R.id.tv_location);

        btn_satelite.setOnClickListener(this);
        btn_nomal.setOnClickListener(this);
        btn_white.setOnClickListener(this);
        tv_location.setOnClickListener(this);

        mBaiduMap=mMapView.getMap();

        MapStatusUpdate factory=MapStatusUpdateFactory.zoomTo(15.0f);
        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mListener);


        //mBaiduMap.setMyLocationEnabled(true);
        //MyLocationData locData=new MyLocationData.Builder().accuracy()

        mBaiduMap.setTrafficEnabled(true);//实时交通图

        initLocation();


        //mBaiduMap.setPadding(0,-500,0,0);
        //mBaiduMap.setViewPadding(0,10,100,300);
    }
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度

        option.setCoorType("bd0911");

        int span=1000;
        option.setScanSpan(span);

        option.setIsNeedAddress(true);

        option.setOpenGps(true);

        option.setLocationNotify(true);

        option.setIsNeedLocationDescribe(true);

        option.setIsNeedLocationPoiList(true);

        option.setIgnoreKillProcess(false);

        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMapView.onPause();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_satellite:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//卫星地图

                break;
            case R.id.btn_nomal:

                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//普通地图
                break;
            case R.id.btn_white:

                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);//空白地图
                break;
            case R.id.tv_location:

                mLocationClient.start();
            default:
                break;
        }

    }

class MyLocationListener implements BDLocationListener {




        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());

            tv_location.setText("我在"+location.getAddrStr());
        }


        @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
 }
}