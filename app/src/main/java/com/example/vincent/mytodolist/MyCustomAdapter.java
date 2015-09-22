package com.example.vincent.mytodolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyCustomAdapter extends ArrayAdapter<String> {

    // A custom adapter...

    public MyCustomAdapter(Context context, ArrayList<String> items) {
        super(context, R.layout.my_row_layout, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View theView = theInflater.inflate(R.layout.my_row_layout, parent, false);
        String toDoItem = getItem(position);
        TextView theTextView = (TextView) theView.findViewById(R.id.row_textView);
        theTextView.setText(toDoItem);
        ImageView theImageView = (ImageView) theView.findViewById(R.id.row_imageView);
        theImageView.setImageResource(R.drawable.black_dot);
        return theView;
    }
}
