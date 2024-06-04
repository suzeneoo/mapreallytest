package com.example.mapreallytest.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        Intent intent = getIntent();
        String name = intent.getStringExtra("hospital_name");
        String address = intent.getStringExtra("hospital_address");
        String phone = intent.getStringExtra("hospital_phone");
        String category = intent.getStringExtra("hospital_category");

        nameTextView.setText(name);
        addressTextView.setText(address);
        phoneTextView.setText(phone);
        categoryTextView.setText(category);
    }
}
