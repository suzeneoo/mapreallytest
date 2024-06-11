package com.example.mapreallytest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class HospitalAdapter extends BaseAdapter {
    private Context context;
    private List<Eyes> hospitals;

    public HospitalAdapter(Context context, List<Eyes> hospitals) {
        this.context = context;
        this.hospitals = hospitals;
    }

    @Override
    public int getCount() {
        return hospitals.size();
    }

    @Override
    public Object getItem(int position) {
        return hospitals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_hospitals, parent, false);
        }

        Eyes hospital = hospitals.get(position);

        TextView hospitalType = convertView.findViewById(R.id.hospital_type);
        TextView hospitalName = convertView.findViewById(R.id.hospital_name);
        TextView hospitalCategory = convertView.findViewById(R.id.hospital_category);
        TextView hospitalReview = convertView.findViewById(R.id.hospital_review);

        if (hospital.isPartnered()) {
            hospitalType.setText("제휴병원");
        } else {
            hospitalType.setText("일반병원");
        }

        hospitalName.setText(hospital.get이름());
        hospitalCategory.setText(hospital.get카테고리());
        hospitalReview.setText("리뷰 수: " + hospital.get방문자_리뷰수());

        return convertView;
    }

    public void updateHospitals(List<Eyes> newHospitals) {
        hospitals.clear();
        hospitals.addAll(newHospitals);
        notifyDataSetChanged();
    }
}
