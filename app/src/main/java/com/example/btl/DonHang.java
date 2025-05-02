package com.example.btl;

public class DonHang {
    private String maDon;
    private String trangThai;

    public DonHang(String maDon, String trangThai) {
        this.maDon = maDon;
        this.trangThai = trangThai;
    }

    public String getMaDon() {
        return maDon;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
