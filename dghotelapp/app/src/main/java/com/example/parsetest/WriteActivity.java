package com.example.parsetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity{

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private EditText mWriteTitleText;
    private EditText mWriteContentsText;
    private EditText mWriteNameText;
    private EditText mWriteHotelText;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        mWriteTitleText = findViewById(R.id.write_title_edit);
        mWriteContentsText = findViewById(R.id.write_contents_edit);
        mWriteNameText = findViewById(R.id.write_name_edit);
        mWriteHotelText=findViewById(R.id.write_hotel_edit);


        Intent intent=getIntent();
        if(intent.getStringExtra("detail")!=null) {
            String detail = intent.getStringExtra("detail");
            mWriteHotelText.setText(detail);
        }

        Button writeButton=findViewById(R.id.wirte_upload_button);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = mStore.collection("board").document().getId();
                Map<String, Object> post = new HashMap<>();
                post.put("hotel_name", mWriteHotelText.getText().toString());
                post.put("title", mWriteTitleText.getText().toString());
                post.put("contents", mWriteContentsText.getText().toString());
                post.put("name", mWriteNameText.getText().toString());

                mStore.collection("board").document(id).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(WriteActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WriteActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        Button searchListButton=findViewById(R.id.searchListButton);
        searchListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(getApplicationContext(), HotelListActivity.class);
                intent3.putExtra("Keyword", mWriteHotelText.getText().toString());
                startActivityForResult(intent3,0);
            }
        });

        Button cancleButton=findViewById(R.id.cancel_button);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            if(resultCode==0)
            {
                mWriteHotelText.setText(data.getExtras().getString("result"));
            }
        }
    }
}