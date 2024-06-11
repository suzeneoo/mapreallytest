package com.example.mapreallytest.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mapreallytest.R;

public class JhospitalInfoActivity extends AppCompatActivity {

    private TextView partnershipName;
    private TextView partnershipAddress;
    private TextView partnershipInfo;
    private TextView partnershipDetailsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jhospitals_info);

        TextView partnershipName = findViewById(R.id.partnership_name);
        TextView partnershipAddress = findViewById(R.id.partnership_address);
        TextView partnershipInfo = findViewById(R.id.partnership_info);
        TextView partnershipDetailsInfo = findViewById(R.id.partnership_details_info);

        Intent intent = getIntent();
        String name = intent.getStringExtra("hospital_name");
        String address = intent.getStringExtra("hospital_address");
        String info = intent.getStringExtra("partnership_info");
        String apply = intent.getStringExtra("partnership_apply");

        partnershipName.setText(name);
        partnershipAddress.setText(address);
        partnershipInfo.setText(info);
        partnershipDetailsInfo.setText(apply);
    }

}
