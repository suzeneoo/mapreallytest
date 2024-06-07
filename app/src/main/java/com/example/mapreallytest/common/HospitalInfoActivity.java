package com.example.mapreallytest.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mapreallytest.R;

public class HospitalInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_info);

        TextView nameTextView = findViewById(R.id.partnership_name);
        TextView addressTextView = findViewById(R.id.partnership_address);
        TextView phoneTextView = findViewById(R.id.hospital_phone);
        TextView categoryTextView = findViewById(R.id.hospital_hours);
        ImageView partnershipImageView = findViewById(R.id.partnership_image);
        Button backButton = findViewById(R.id.back_button);


        Intent intent = getIntent();
        String name = intent.getStringExtra("hospital_name");
        String address = intent.getStringExtra("hospital_address");
        String phone = intent.getStringExtra("hospital_phone");
        String category = intent.getStringExtra("hospital_category");
        String imageUrl = intent.getStringExtra("hospital_image_url");

        nameTextView.setText(name);
        addressTextView.setText(address);
        phoneTextView.setText(phone);
        categoryTextView.setText(category);

        // Glide를 사용하여 이미지 로드
        Glide.with(this)
                .load(imageUrl)
                .into(partnershipImageView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 돌아가기
                finish();
            }
        });
    }
}
