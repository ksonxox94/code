package com.example.parsetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

public class HotelListActivity extends AppCompatActivity {

    private ListView hListView;
    private Cursor cursor;
    private TextView hotelListTitle;
    private SQLiteDatabase sqlDB;
    private myDBHelper mysqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        final MyAdapter hMyAdapter = new MyAdapter();
        hListView=findViewById(R.id.hotelListView);
        hotelListTitle=findViewById(R.id.hotelListTitle);

        Intent intent=getIntent();
        String keyword=intent.getStringExtra("Keyword");
        hotelListTitle.setText("'" +keyword+"'과(와) 관련된 숙소 목록입니다");

        try{
            mysqlHelper = new myDBHelper(this);
            sqlDB = mysqlHelper.getReadableDatabase();
            cursor=sqlDB.rawQuery("SELECT NM, ADR FROM hotelDB WHERE NM LIKE '%" + keyword + "%';",null);

            if(cursor.getCount()<1)
            {
                hotelListTitle.setText("죄송합니다. 관련된 숙소가 없습니다");
            }
            else {
                while (cursor.moveToNext())
                {
                    hMyAdapter.addItem(cursor.getString(0), cursor.getString(1));
                }

                hListView.setAdapter(hMyAdapter);

                hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent data = new Intent();
                        Log.e("제발",hMyAdapter.getItem(i).getName());
                        data.putExtra("result", hMyAdapter.getItem(i).getName());
                        setResult(0,data);
                        finish();
                    }
                });
            }
        }catch(Exception e)
        {
            Log.e("Error","오류");
        }

        Button hCancleButton=findViewById(R.id.cancelButton);
        hCancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}