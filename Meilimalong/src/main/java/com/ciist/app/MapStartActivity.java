package com.ciist.app;


import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.services.core.LatLonPoint;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.ciist.util.ToastUtil;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MapStartActivity extends Activity implements AMap.OnMarkerClickListener,
        AMap.OnMapClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter,
        PoiSearch.OnPoiSearchListener, RouteSearch.OnRouteSearchListener, OnClickListener {

    private MapView mapView;
    private com.amap.api.maps.AMap aMap;
    private RouteSearch routeSearch;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private LatLonPoint startPoint = null;
    private LatLonPoint endPoint = null;
    private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mapstart);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(bundle);// 此方法必须重写
        init();
        Intent i = getIntent();
        double longtidue_e = i.getDoubleExtra("longtidue_e", 0);
        double latidue_e = i.getDoubleExtra("latidue_e", 0);
        double longtidue_b = i.getDoubleExtra("longtidue_b", 0);
        double latidue_b = i.getDoubleExtra("latidue_b", 0);
        startPoint = new LatLonPoint(latidue_b, longtidue_b);
        endPoint = new LatLonPoint(latidue_e, longtidue_e);
        Action();
        Button _start= (Button) findViewById(R.id.btnStartNav);
        _start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AMapNavi _nav=AMapNavi.getInstance(MapStartActivity.this);
                if(_nav==null){
                    Toast.makeText(MapStartActivity.this,"对不起，您的手机暂不支持",Toast.LENGTH_SHORT).show();
                }*/
        //        Toast.makeText(MapStartActivity.this,"对不起，您的手机暂不支持开始",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MapStartActivity.this,GPSNaviActivity.class);
                intent.putExtra("start",startPoint);
                intent.putExtra("end",endPoint);
                startActivity(intent);

            }
        });
    }

    void Action() {
        showProgressDialog();
        //,

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, drivingMode, null, null, "");
        // 第一个参数表示路径规划的起点和终点，
        // 第二个参数表示驾车模式，
        // 第三个参数表示途经点，
        // 第四个参数表示避让区域，
        // 第五个参数表示避让道路
        routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    }

    /**
     * 驾车结果回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                driveRouteResult = result;
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        this, aMap, drivePath, driveRouteResult.getStartPos(),
                        driveRouteResult.getTargetPos());
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            } else {
                ToastUtil.show(MapStartActivity.this, "无规划路线");
            }
        } else if (rCode == 27) {
            ToastUtil.show(MapStartActivity.this, "网络异常");
        } else if (rCode == 32) {
            ToastUtil.show(MapStartActivity.this, "授权失败");
        } else {
            ToastUtil.show(MapStartActivity.this, "信息：" + rCode);
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();

            registerListener();
        }
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(MapStartActivity.this);
        aMap.setOnMarkerClickListener(MapStartActivity.this);
        aMap.setOnInfoWindowClickListener(MapStartActivity.this);
        aMap.setInfoWindowAdapter(MapStartActivity.this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }


    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


}
