package com.example.quanlynhanvien;

import java.io.Serializable;

public class NhanVien implements Serializable {
    private String id;
    private String name;
    private String donVi;
    private boolean gender;
    private byte[] imgByte;

    public NhanVien(String id, String name, String donVi, boolean gender, byte[] imgByte) {
        this.id = id;
        this.name = name;
        this.donVi = donVi;
        this.gender = gender;
        this.imgByte = imgByte;
    }

    public byte[] getImgByte() {
        return imgByte;
    }

    public void setImgByte(byte[] imgByte) {
        this.imgByte = imgByte;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        String kq = "- Mã: " + this.id + "\n" +
                "- Tên: " + this.name + "\n" +
                "- Đơn vị: " + this.donVi + "\n" +
                "- Giới tính: ";
        if (this.gender) {
            kq += "Nam";
        } else {
            kq += "Nữ";
        }
        return kq;
    }
}
