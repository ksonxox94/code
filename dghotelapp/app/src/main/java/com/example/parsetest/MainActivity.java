package com.example.parsetest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager;


public class MainActivity extends AppCompatActivity{

    private StrictMode StrictMode;
    private myDBHelper mysqlHelper;
    private SQLiteDatabase sqlDB;
    private TextView textView;
    private Cursor cursor2;
    private String searchOpt;
    private ProgressDialog progressDialog;
    private static final String ROOT_DIR = "/data/data/com.example.parsetest/databases/";
    private SQLiteDatabase locDB;
    private ProductDBHelper mylocHelper;
    Adapter adapter;
    ViewPager viewPager;
    private GestureDetector gestureDetector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureDetector = new GestureDetector(this, new SingleTapGestureListener() );

        getHashKey();

        Log.d("왜","이건아님");


        viewPager = (ViewPager) findViewById(R.id.view);
        adapter = new Adapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });

        setDB(this);
        mylocHelper = new ProductDBHelper(this);
        locDB = mylocHelper.getWritableDatabase();
        locDB.close();

        StrictMode.enableDefaults();
        mysqlHelper=new myDBHelper(this);
        searchOpt="지역명";

        //getAppKeyHash();
        getdatabase();

        Spinner mainSpinner=(Spinner)findViewById(R.id.mainSpinner);
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                if(position==0)
                {
                    searchOpt="지역명";
                }
                else if(position==1)
                {
                    searchOpt="업소명";
                }
                else
                {
                    searchOpt="지역명";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                searchOpt="지역명";
            }
        });

        Button mainSearchButton=findViewById(R.id.mainSearchButton);
        final EditText mainSearchEdit=findViewById(R.id.mainSearchEdit);

        mainSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainSearchEdit.getWindowToken(), 0);
                String keyword;
                keyword=mainSearchEdit.getText().toString();
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                if(keyword!=null)
                {
                    intent.putExtra("Keyword", keyword);
                    intent.putExtra("SearchOpt", searchOpt);
                    startActivity(intent);
                    runDialog(1);
                }
            }
        });

        Button partListButton=findViewById(R.id.partListButton);
        partListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),partActivity.class);
                startActivity(intent);
            }
        });

        Button newListButton=findViewById(R.id.newListButton);
        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainSearchEdit.getWindowToken(), 0);
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                intent.putExtra("Keyword", "new");
                intent.putExtra("SearchOpt", "new");
                startActivity(intent);
                runDialog(1);
            }
        });

        Button reviewButton=findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ReviewActivity.class);
                startActivity(intent);
            }
        });
    }
    private final class SingleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if( velocityX < 0 ) {
                    // 뷰페이저 오른쪽으로 이동
                }else {
                }
            }catch ( Exception e ) {}
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            try {
                int pos=viewPager.getCurrentItem();
                String h_name="리버틴호텔";
                Log.d("왜",Integer.toString(pos));
                Intent intent=new Intent(getApplicationContext(),DetailActivity.class);
                switch(pos) {
                    case 0: h_name="리버틴호텔"; break;
                    case 1: h_name="2월호텔 동성로점"; break;
                    case 2: h_name="호텔더디종";break;
                    case 3: h_name="제이비프리미엄호텔";break;
                    case 4: h_name="탑호텔";break;
                    case 5: h_name="피크니크호텔";break;
                    default: h_name="리버틴호텔";break;
                }
                intent.putExtra("name",h_name);
                startActivity(intent);
            }catch ( Exception e ) {}
            return true;
        }
    }

    public static void setDB(Context ctx) {
        File folder = new File(ROOT_DIR);
        if(folder.exists()) {
        } else {
            folder.mkdirs();
        }
        AssetManager assetManager = ctx.getResources().getAssets();
        File outfile = new File(ROOT_DIR+"locationDB.sqlite");
        InputStream is = null;
        FileOutputStream fo = null;
        long filesize = 0;
        try {
            is = assetManager.open("locationDB.sqlite", AssetManager.ACCESS_BUFFER);
            filesize = is.available();
            if (outfile.length() <= 0) {
                byte[] tempdata = new byte[(int) filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            } else {}
        } catch (IOException e) {}
    }

    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    private void runDialog(final int seconds)
    {
        progressDialog= ProgressDialog.show(this,"알림","숙박 업소들을 찾는 중입니다. 잠시만 기다려주세요.");
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


    public void getdatabase()
    {
        try{
            sqlDB=mysqlHelper.getWritableDatabase();
            locDB = mylocHelper.getWritableDatabase();
            mysqlHelper.onUpgrade(sqlDB,1,2);

            boolean intheme_Nm=false; //주변 시설
            boolean inOPENDATA_ID=false; //아이디 ㅇ
            boolean inGNG_CS=false; //지역구 ㅇ
            boolean inBZ_NM=false; //업소명 ㅇ10
            boolean inMMO=false; //업소 설명
            boolean inPCL_FCTS=false; //인터넷9
            boolean inADR=false; //주소 ㅇ8
            boolean inTLNO=false; //전화번호 ㅇ7
            boolean inCKI_HR=false; //체크인 시간6
            boolean inCKO_HR=false; //체크아웃 시간5
            boolean inPRK_FCL=false; //주차가능대수4
            boolean inPSB_FRN=false; //가능한 외국어3
            boolean inSBW_INF=false; //인근 지하철역2
            boolean inBUS_INF=false; //인근 버스노선1

            String theme_Nm=null;
            String OPENDATA_ID=null;
            String GNG_CS=null;
            String BZ_NM=null;
            String MMO=null;
            String PCL_FCTS=null;
            String ADR=null;
            String TLNO=null;
            String CKI_HR=null;
            String CKO_HR=null;
            String PRK_FCL=null;;
            String PSB_FRN=null;
            String SBW_INF=null;
            String BUS_INF=null;

            URL url = new URL("https://greenstel.daegu.go.kr/kor/xml/greenstel.html?cate=1"
            ); //검색 URL부분

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                        if(parser.getName().equals("theme_Nm")){
                            intheme_Nm = true;
                        }
                        if(parser.getName().equals("OPENDATA_ID")){
                            inOPENDATA_ID = true;
                        }
                        if(parser.getName().equals("GNG_CS")){
                            inGNG_CS = true;
                        }
                        if(parser.getName().equals("BZ_NM")){
                            inBZ_NM = true;
                        }
                        if(parser.getName().equals("MMO")){
                            inMMO = true;
                        }
                        if(parser.getName().equals("PCL_FCTS")){
                            inPCL_FCTS = true;
                        }
                        if(parser.getName().equals("ADR")){
                            inADR = true;
                        }
                        if(parser.getName().equals("TLNO")){
                            inTLNO = true;
                        }
                        if(parser.getName().equals("CKI_HR")){
                            inCKI_HR = true;
                        }
                        if(parser.getName().equals("CKO_HR")){
                            inCKO_HR = true;
                        }
                        if(parser.getName().equals("PRK_FCL")){
                            inPRK_FCL = true;
                        }
                        if(parser.getName().equals("PSB_FRN")){
                            inPSB_FRN = true;
                        }
                        if(parser.getName().equals("PRK_FCL")){
                            inPRK_FCL = true;
                        }
                        if(parser.getName().equals("SBW_INF")){
                            inSBW_INF = true;
                        }
                        if(parser.getName().equals("BUS_INF")){
                            inBUS_INF = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if(intheme_Nm){
                            theme_Nm = parser.getText();
                            intheme_Nm = false;
                        }
                        if(inOPENDATA_ID){
                            OPENDATA_ID = parser.getText();
                            inOPENDATA_ID = false;
                        }
                        if(inGNG_CS){
                            GNG_CS= parser.getText();
                            inGNG_CS = false;
                        }
                        if(inBZ_NM){
                            BZ_NM = parser.getText();
                            inBZ_NM = false;
                        }
                        if(inMMO){
                            MMO = parser.getText();
                            inMMO = false;
                        }
                        if(inPCL_FCTS){
                            PCL_FCTS = parser.getText();
                            inPCL_FCTS = false;
                        }
                        if(inADR) {
                            ADR = parser.getText();
                            inADR = false;
                        }
                        if(inTLNO){
                            TLNO = parser.getText();
                            TLNO=TLNO.replace(")","-");
                            inTLNO = false;
                        }
                        if(inCKI_HR){
                            CKI_HR = parser.getText();
                            inCKI_HR = false;
                        }
                        if(inCKO_HR){
                            CKO_HR = parser.getText();
                            inCKO_HR = false;
                        }
                        if(inPRK_FCL){
                            PRK_FCL = parser.getText();
                            inPRK_FCL = false;
                        }
                        if(inPSB_FRN){
                            PSB_FRN = parser.getText();
                            inPSB_FRN = false;
                        }
                        if(inSBW_INF){
                            SBW_INF = parser.getText();
                            inSBW_INF = false;
                        }
                        if(inBUS_INF){
                            BUS_INF = parser.getText();
                            BUS_INF=BUS_INF.replace("'","#");
                            inBUS_INF = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("row")){
                            sqlDB.execSQL("INSERT INTO hotelDB VALUES ( '" + theme_Nm + "','" + OPENDATA_ID+"','"+GNG_CS+"','"+BZ_NM+"','"+MMO+"','"+PCL_FCTS+"','"+ADR+"','"+TLNO+"','"+CKI_HR+"','"+CKO_HR+"','"+PRK_FCL+"','"+PSB_FRN+"','"+SBW_INF+"','"+BUS_INF+"');");
                            cursor2 = locDB.rawQuery("SELECT ID FROM locationDB WHERE ID = '"+OPENDATA_ID+"';", null); //쿼리문
                            if(cursor2.getCount()<1)
                            {
                                Log.d("업데이트","위치 DB 자동 업데이트");
                                Double lat=null;
                                Double logt=null;
                                List<Address> list = null;
                                if (list != null) {
                                    lat=list.get(0).getLatitude();
                                    logt=list.get(0).getLongitude();
                                }
                                locDB.execSQL("INSERT INTO hotelDB VALUES ( '" + OPENDATA_ID+ "','"+BZ_NM+"','"+ADR+"','"+lat+"','"+logt+"');");
                            }
                        }
                        break;
                }
                parserEvent = parser.next();
            }
            sqlDB.close();
            locDB.close();
            cursor2.close();
            Toast.makeText(getApplicationContext(),"데이터베이스 불러오기 완료",Toast.LENGTH_SHORT).show();

        } catch(Exception e){
            Toast.makeText(getApplicationContext(),"오류가 발생했습니다",Toast.LENGTH_SHORT).show();
        }
    }
}