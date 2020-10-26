package com.example.parsetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class SearchActivity extends AppCompatActivity implements MapView.MapViewEventListener {

    private ListView listView;
    private TextView searchTitle;
    private MapView mapView;
    private MapPOIItem marker;
    private ViewGroup mapViewContainer;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private MapPoint MarkerPoint;
    private SQLiteDatabase locDB;
    private SQLiteDatabase sqlDB;
    private ProductDBHelper mylocHelper;
    private myDBHelper mysqlHelper;
    private Cursor cursor;
    private Cursor cursor2;
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        final MyAdapter mMyAdapter = new MyAdapter();
        listView=findViewById(R.id.searchListView);
        searchTitle=findViewById(R.id.searchTitle);

        marker = new MapPOIItem();
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.search_map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.865584, 128.593502), true);
        mapView.setZoomLevel(7, true);

        Button searchToBackButton=(Button)findViewById(R.id.searchToBackButton);
        searchToBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        String keyword=intent.getStringExtra("Keyword");
        String searchOpt=intent.getStringExtra("SearchOpt");


        try{
            mysqlHelper = new myDBHelper(this);
            sqlDB = mysqlHelper.getReadableDatabase();
            mylocHelper = new ProductDBHelper(this);
            locDB = mylocHelper.getReadableDatabase();
            Double lat=null;
            Double logt=null;

            if(searchOpt.equals("지역명"))
            {
                searchTitle.setText(searchOpt+" '" +keyword+"'과(와) 관련된 숙소 목록입니다");
                cursor=sqlDB.rawQuery("SELECT NM, ADR, ID FROM hotelDB WHERE ADR LIKE '%" + keyword + "%';",null);
            }
            else if(searchOpt.equals("업소명"))
            {
                searchTitle.setText(searchOpt+" '" +keyword+"'과(와) 관련된 숙소 목록입니다");
                cursor=sqlDB.rawQuery("SELECT NM, ADR, ID FROM hotelDB WHERE NM LIKE '%" + keyword + "%';",null);
            }
            else if(searchOpt.equals("new"))
            {
                searchTitle.setText("최근에 오픈 또는 리모델링한 숙소 목록입니다");
                cursor=sqlDB.rawQuery("SELECT NM, ADR, ID FROM hotelDB WHERE MMO LIKE '%신축%' OR MMO LIKE '%최근%' OR MMO LIKE '%리모델링%' OR MMO LIKE '%리뉴얼%' OR MMO LIKE '%오픈%';",null);
            }
            else if(searchOpt.equals("part"))
            {
                searchTitle.setText(keyword+"에 위치한 숙소 목록입니다");
                cursor=sqlDB.rawQuery("SELECT NM, ADR, ID FROM hotelDB WHERE PART = '" + keyword + "';",null);
            }

            if(cursor.getCount()<1)
            {
                searchTitle.setText("죄송합니다. 관련된 숙소가 없습니다");
            }
            else {
                while (cursor.moveToNext())
                {
                    int id=cursor.getInt(2);
                    cursor2 = locDB.rawQuery("SELECT LAT,LOGT FROM locationDB WHERE ID = '" + id + "';", null); //쿼리문
                    if(cursor2.getCount()>0) {
                        while (cursor2.moveToNext()) {
                            lat = cursor2.getDouble(0);
                            logt = cursor2.getDouble(1);
                        }
                        MarkerPoint = MapPoint.mapPointWithGeoCoord(lat,logt);
                        marker.setItemName(cursor.getString(0));
                        marker.setTag(0);
                        marker.setMapPoint(MarkerPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                        mapView.addPOIItem(marker);
                    }
                    mMyAdapter.addItem(cursor.getString(0), cursor.getString(1));
                }

                listView.setAdapter(mMyAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent2 = new Intent(getApplicationContext(), DetailActivity.class);
                        intent2.putExtra("name", mMyAdapter.getItem(i).getName());
                        mapViewContainer.removeView(mapView);
                        startActivity(intent2);
                    }
                });
            }
        }catch(Exception e)
        {
            Log.e("Error","오류");
        }
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
                    Toast.makeText(SearchActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(SearchActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(SearchActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(SearchActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(SearchActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(SearchActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(SearchActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
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