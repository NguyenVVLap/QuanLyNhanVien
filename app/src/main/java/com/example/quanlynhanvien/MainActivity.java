package com.example.quanlynhanvien;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    Button btn_Chup, btn_ChonAnh, btn_Them, btn_Xoa, btn_Sua;
    EditText et_Ma, et_Ten;
    Spinner sp_DonVi;
    ListView lv_NhanVien;
    RadioButton rb_Nam, rb_Nu;

    String fileName = "NhanVien.txt";
    int positionItemClicked = -1;

    ArrayList<NhanVien> listNV = new ArrayList<NhanVien>();
    ArrayAdapter<String> adapterDV;
    MyArrayAdapter adapterNV;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addWidgets();
        String[] strDonVi = getResources().getStringArray(R.array.arrayDonvI);
        adapterDV = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strDonVi);
        sp_DonVi.setAdapter(adapterDV);

        pref = getApplicationContext().getSharedPreferences("NhanVien.txt", 0);
        editor = pref.edit();

//        readFromInternalStorage();
        readFromSharedPreference();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this
                    , new String[] {Manifest.permission.CAMERA}, 100);
        }
        btn_Chup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        btn_ChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
        });

        btn_Them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = et_Ma.getText().toString();
                String name = et_Ten.getText().toString();
                boolean gender = rb_Nam.isChecked();
                byte[] bytes = imgToBytes(img);
                String donVi = (String) sp_DonVi.getSelectedItem();

                NhanVien nv = new NhanVien(id, name, donVi, gender, bytes);
                listNV.add(nv);
//                writeToInternalStorage();
//                readFromInternalStorage();
                writeWithSharedPreference();
                readFromSharedPreference();
                Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
            }
        });

        lv_NhanVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NhanVien nv = listNV.get(i);
                et_Ma.setText(nv.getId());
                et_Ten.setText(nv.getName());
                for (int j = 0; j < strDonVi.length; j++) {
                    if (strDonVi[j].equals(nv.getDonVi())) {
                        sp_DonVi.setSelection(j);
                    }
                }
                if (nv.isGender()) {
                    rb_Nam.setChecked(true);
                } else {
                    rb_Nu.setChecked(true);
                }
                byte[] bytes = nv.getImgByte();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img.setImageBitmap(bitmap);
                positionItemClicked = i;
                adapterNV.setPositionItemSelected(i);
                adapterNV.notifyDataSetChanged();
            }
        });
        btn_Xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Chú ý");
                builder.setMessage("Bạn có chắn chắc xóa nhân viên này");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        listNV.remove(positionItemClicked);
//                        writeToInternalStorage();
//                        readFromInternalStorage();
                        removeFromSharedPreference(positionItemClicked);
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btn_Sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ma = et_Ma.getText().toString();
                String ten = et_Ten.getText().toString();
                boolean gender = rb_Nam.isChecked();
                String donVi = (String) sp_DonVi.getSelectedItem();
                byte[] bytes = imgToBytes(img);

                NhanVien nv = new NhanVien(ma, ten, donVi, gender, bytes);
                listNV.set(positionItemClicked, nv);
//                writeToInternalStorage();
//                readFromInternalStorage();

                writeWithSharedPreference();
                readFromSharedPreference();
            }
        });
    }



    public byte[] imgToBytes(ImageView img) {
        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap capture = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(capture);
        }

        if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri image = data.getData();
            img.setImageURI(image);
        }
    }

    public void addWidgets() {
        img = findViewById(R.id.image);
        btn_Chup = findViewById(R.id.button_ChupAnh);
        btn_ChonAnh = findViewById(R.id.button_ChonAnh);
        btn_Them = findViewById(R.id.button_Them);
        btn_Xoa = findViewById(R.id.button_Xoa);
        btn_Sua = findViewById(R.id.button_Sua);
        et_Ma = findViewById(R.id.editText_Ma);
        et_Ten = findViewById(R.id.editText_Ten);
        sp_DonVi = findViewById(R.id.spinner);
        lv_NhanVien = findViewById(R.id.listView);
        rb_Nam = findViewById(R.id.radioButton_Nam);
        rb_Nu = findViewById(R.id.radioButton_Nu);
    }

    public void writeToInternalStorage() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE));
            for (NhanVien nv : listNV) {
                out.writeObject(nv);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromInternalStorage() {
        try {
            ObjectInputStream in = new ObjectInputStream(openFileInput(fileName));
            listNV = new ArrayList<NhanVien>();
            while (true) {
                NhanVien nv = (NhanVien) in.readObject();
                listNV.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapterNV = new MyArrayAdapter(MainActivity.this, R.layout.my_item_layout, listNV, -1);
        lv_NhanVien.setAdapter(adapterNV);
    }

    public void writeWithSharedPreference() {
        Gson gson = new Gson();
        String json;
        int i = 0;
        for (NhanVien nv: listNV) {
            json = gson.toJson(nv);
            editor.putString("NhanVien" + i++, json);
        }
        editor.commit();
    }

    public void readFromSharedPreference() {
        HashMap<String, String> map = (HashMap<String, String>) pref.getAll();
        String json;
        Gson gson = new Gson();
        NhanVien nv;
        listNV = new ArrayList<NhanVien>();
        for (int i = 0; i < map.size(); i++) {
            json = map.get("NhanVien" + i);
            nv = gson.fromJson(json, NhanVien.class);
            listNV.add(nv);
        }
        adapterNV = new MyArrayAdapter(MainActivity.this, R.layout.my_item_layout, listNV, -1);
        lv_NhanVien.setAdapter(adapterNV);
    }

    private void removeFromSharedPreference(int positionItemClicked) {
        editor.remove("NhanVien"+positionItemClicked);
        editor.commit();
        readFromSharedPreference();
    }
}