package com.example.mapreallytest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        return convertView;
    }

    // clear 메서드 추가
    @Override
    public void clear() {
        super.clear();
    }
}
