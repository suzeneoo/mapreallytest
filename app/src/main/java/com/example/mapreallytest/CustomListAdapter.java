package com.example.mapreallytest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomListAdapter extends ArrayAdapter<String> {

    public CustomListAdapter(@NonNull Context context, @NonNull String[] objects) {
        super(context, R.layout.list_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        String item = getItem(position);
        TextView textView = convertView.findViewById(R.id.list_item_text);
        textView.setText(item);

        // 접근성을 위한 콘텐츠 설명 설정
        textView.setContentDescription("목록 항목 " + item);

        return convertView;
    }
}

