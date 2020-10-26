package com.example.parsetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements MapView.MapViewEventListener {

    private MapView mapView;
    private MapPOIItem marker;
    private ViewGroup mapViewContainer;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final String LOG_TAG = "DetailActivity";
    private MapPoint MarkerPoint;
    private Cursor cursor;
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private TextView tv_name;
    private TextView tv_add;
    private TextView tv_phone;
    private TextView tv_cki;
    private TextView tv_cko;
    private TextView tv_car;
    private TextView tv_lan;
    private TextView tv_inter;
    private TextView tv_sub;
    private TextView tv_bus;
    private TextView tv_info;
    private TextView tv_theme;
    private TextView detailTitle;
    private Cursor cursor2;
    private SQLiteDatabase locDB;
    private ProductDBHelper mylocHelper;
    private SQLiteDatabase sqlDB;
    private myDBHelper mysqlHelper;
    private String phoneNumber=" ";
    private String instaTagLink=" ";
    private String bookHotelLink=" ";
    private String naverLink=" ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Double lat=null;
        Double logt=null;

        setContentView(R.layout.activity_detail);
        tv_name=findViewById(R.id.rname);
        tv_add=findViewById(R.id.radd);
        tv_phone=findViewById(R.id.rphone);
        tv_cki=findViewById(R.id.rcki);
        tv_cko=findViewById(R.id.rcko);
        tv_car=findViewById(R.id.rcar);
        tv_lan=findViewById(R.id.rlan);
        tv_inter=findViewById(R.id.rinter);
        tv_sub=findViewById(R.id.rsub);
        tv_bus=findViewById(R.id.rbus);
        tv_info=findViewById(R.id.rinfo);
        tv_theme=findViewById(R.id.rtheme);
        detailTitle=findViewById(R.id.detailTitle);

        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        detailTitle.setText(name+" 정보");

        mysqlHelper = new myDBHelper(this);
        sqlDB = mysqlHelper.getReadableDatabase();
        mylocHelper = new ProductDBHelper(this);
        locDB = mylocHelper.getReadableDatabase();

        mapView = new MapView(this);
        marker = new MapPOIItem();
        mapViewContainer = (ViewGroup) findViewById(R.id.detail_map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setZoomLevel(4, true);

        cursor=sqlDB.rawQuery("SELECT ID,NM,ADR,TLNO,CKI,CKO,PRK,PSB,PCL,SBW,BUS,MMO,THEME FROM hotelDB WHERE NM = '"+name+"';",null);

        while(cursor.moveToNext())
        {
            int id=cursor.getInt(0);
            cursor2 = locDB.rawQuery("SELECT LAT,LOGT FROM locationDB WHERE ID = '" + id + "';", null); //쿼리문

            if (cursor2.getCount() > 0) {
                while (cursor2.moveToNext()) {
                    lat = cursor2.getDouble(0);
                    logt = cursor2.getDouble(1);
                }

                MarkerPoint = MapPoint.mapPointWithGeoCoord(lat,logt);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat,logt), true);
                marker.setItemName(cursor.getString(1));
                marker.setTag(0);
                marker.setMapPoint(MarkerPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(marker);

                tv_name.setText(cursor.getString(1));
                bookHotelLink="https://www.goodchoice.kr/product/result?keyword="+cursor.getString(1);
                instaTagLink="https://www.instagram.com/explore/tags/"+cursor.getString(1)+"/";
                naverLink="https://search.naver.com/search.naver?ie=utf8&where=nexearch&query="+cursor.getString(1);
                tv_add.setText(cursor.getString(2));
                tv_phone.setText(cursor.getString(3));
                phoneNumber="tel:"+cursor.getString(3);
                tv_cki.setText(cursor.getString(4));
                tv_cko.setText(cursor.getString(5));
                tv_car.setText(cursor.getString(6));
                tv_lan.setText(cursor.getString(7));
                tv_inter.setText(cursor.getString(8));
                tv_sub.setText(cursor.getString(9));
                tv_bus.setText(cursor.getString(10));
                tv_info.setText(cursor.getString(11));
                tv_theme.setText(cursor.getString(12));
            }
        }

        Button bookHotelButton=findViewById(R.id.bookHotelButton);
        bookHotelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(bookHotelLink));
                startActivity(bookIntent);
            }
        });
        Button instaButton=findViewById(R.id.instaButton);
        instaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent instaIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(instaTagLink));
                startActivity(instaIntent);
            }
        });
        Button naverButton=findViewById(R.id.naverButton);
        naverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent naverIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(naverLink));
                startActivity(naverIntent);
            }
        });

        Button callButton=findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(phoneNumber));
                startActivity(callIntent);
            }
        });
        Button detailToSearch=(Button)findViewById(R.id.detailToSearchButton);
        detailToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapViewContainer.removeView(mapView);
                finish();
            }
        });
        Button reviewButton=findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),WriteActivity.class);
                intent.putExtra("detail",tv_name.getText().toString());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewContainer.removeAllViews();
    }

    // ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음

            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있다
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(DetailActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DetailActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(DetailActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(DetailActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(DetailActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(DetailActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}