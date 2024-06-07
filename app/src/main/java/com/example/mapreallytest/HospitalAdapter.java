package com.example.mapreallytest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;


public class HospitalAdapter extends ArrayAdapter<Eyes> {

        public HospitalAdapter(Context context, List<Eyes> hospitals) {
            super(context, 0, hospitals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Eyes hospital = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_hospitals, parent, false);
            }

            TextView hospitalName = convertView.findViewById(R.id.hospital_name);
            TextView hospitalCategory = convertView.findViewById(R.id.hospital_category);
            TextView visitorReviewCount = convertView.findViewById(R.id.hospital_review);

            hospitalName.setText(hospital.get이름());
            hospitalCategory.setText(hospital.get카테고리());
            visitorReviewCount.setText(String.valueOf(hospital.get방문자_리뷰수()));

            // jhospitals.json에서 로드된 병원의 이름을 파란색으로 설정
            if (hospital.isFromJhospitals()) {
                hospitalName.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
            } else {
                hospitalName.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }

            Log.d("HospitalAdapter", "Hospital: " + hospital.get이름() + ", Visitor Reviews: " + hospital.get방문자_리뷰수());

            return convertView;
        }

        @Override
        public void clear() {
            super.clear();
        }
    }


