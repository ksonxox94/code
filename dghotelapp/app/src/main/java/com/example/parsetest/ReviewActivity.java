package com.example.parsetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.parsetest.R.id.write_button;

public class ReviewActivity extends AppCompatActivity{
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mMainRecyclerView;
    private MainAdpater mAdpater;
    private List<Board> mBoardList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mMainRecyclerView = findViewById(R.id.main_recyclerview);
        mBoardList = new ArrayList<>();
        mStore.collection("board").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            mBoardList.clear();
                            for (QueryDocumentSnapshot dc : value) {
                                String hotel_id = (String) dc.getData().get("hotel_name");
                                String title = (String) dc.getData().get("title");
                                String contents = (String) dc.getData().get("contents");
                                String name = (String) dc.getData().get("name");
                                Board data = new Board(hotel_id, title, contents, name);
                                mBoardList.add(data);
                            }
                            mAdpater = new MainAdpater(mBoardList);
                            mMainRecyclerView.setAdapter(mAdpater);
                        }
                    }
                }
                );

        Button writeButton=findViewById(R.id.write_button);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("abc","작성 클릭 눌림");
                Intent intent=new Intent(getApplicationContext(),WriteActivity.class);
                startActivity(intent);
            }
        });

        Button backButton=findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private class MainAdpater extends RecyclerView.Adapter<MainAdpater.MainViewHolder> {

        private List<Board> mBoardList;

        public MainAdpater(List<Board> mBoardList) {
            this.mBoardList = mBoardList;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            Board data = mBoardList.get(position);
            holder.mTitleTextView.setText(data.getTitle());
            holder.mNameTextView.setText(data.getName());
            holder.mHotelTextView.setText(data.getHotel_name());
            holder.mContentsTextView.setText(data.getContents());
        }

        @Override
        public int getItemCount() {
            return mBoardList.size();
        }

        class MainViewHolder extends RecyclerView.ViewHolder {
            private TextView mTitleTextView;
            private TextView mNameTextView;
            private TextView mHotelTextView;
            private TextView mContentsTextView;

            public MainViewHolder(View itemView) {
                super(itemView);
                mTitleTextView = itemView.findViewById(R.id.mTitleTextView);
                mNameTextView = itemView.findViewById(R.id.mNameTextView);
                mHotelTextView= itemView.findViewById(R.id.mHotelTextView);;
                mContentsTextView= itemView.findViewById(R.id.mContentsTextView);;
            }
        }
    }
}