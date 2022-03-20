package com.example.quanlynhanvien;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MyArrayAdapter extends ArrayAdapter<NhanVien> {
    private Activity context = null;
    private int layoutId;
    private ArrayList<NhanVien> arrayList;
    private int positionItemSelected;

    public MyArrayAdapter(Activity context, int resource, ArrayList<NhanVien> objects, int positionItemSelected) {
        super(context, resource, objects);
        this.context = context;
        this.arrayList = objects;
        this.layoutId = resource;
        this.positionItemSelected = positionItemSelected;
    }

    public void setPositionItemSelected(int positionItemSelected) {
        this.positionItemSelected = positionItemSelected;
    }

    @NonNull
    @Override
    @SuppressLint("ResourceAsColor")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        if (position >= 0 && arrayList.size() > 0) {
            final TextView txtDisplay = convertView.findViewById(R.id.textView_Item);
            final ImageView img = convertView.findViewById(R.id.imageView_item);
            NhanVien nv = arrayList.get(position);
            txtDisplay.setText(nv.toString());
            byte[] bytes = nv.getImgByte();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            img.setImageBitmap(bitmap);
            if (this.positionItemSelected == position) {
                final LinearLayout linearLayout = convertView.findViewById(R.id.myLinearinear);
                linearLayout.setBackgroundColor(androidx.cardview.R.color.cardview_dark_background);
            }
        }
        return convertView;
    }
}
