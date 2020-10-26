package com.example.parsetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class myDBHelper extends SQLiteOpenHelper
{
    public myDBHelper(@Nullable Context context) {
        super(context, "hotelDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB알림","myDBHelper onCreate");
        db.execSQL("CREATE TABLE hotelDB(THEME TEXT, ID INTEGER PRIMARY KEY, PART TEXT, NM TEXT, MMO TEXT, PCL TEXT, ADR TEXT, TLNO TEXT,CKI TEXT, CKO TEXT, PRK TEXT, PSB TEXT, SBW TEXT, BUS TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS hotelDB");
        onCreate(db);
    }
}