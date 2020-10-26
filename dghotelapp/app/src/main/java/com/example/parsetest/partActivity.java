package com.example.parsetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class partActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part);

        Button suseongButton=findViewById(R.id.suseongButton);
        suseongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "수성구");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button northButton=findViewById(R.id.northButton);
        northButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "북구");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button eastButton=findViewById(R.id.eastButton);
        eastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "동구");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button southButton=findViewById(R.id.southButton);
        southButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "남구");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button middleButton=findViewById(R.id.middleButton);
        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "중구");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button westButton=findViewById(R.id.westButton);
        westButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "서구");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button dalsungButton=findViewById(R.id.dalsungButton);
        dalsungButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "달성군");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button dalseoButton=findViewById(R.id.dalseoButton);
        dalseoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "달서구");
                intent.putExtra("SearchOpt", "part");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button partToSearchButton=findViewById(R.id.partToSearchButton);
        partToSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void runDialog(final int seconds)
    {
        progressDialog= ProgressDialog.show(this,"알림","숙박 업소들을 찾는 중입니다. 잠시만 기다려주세요!");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(seconds*1000);
                    progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}