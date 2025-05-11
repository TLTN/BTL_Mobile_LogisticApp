package com.example.btl;

public class PhanCong {
    private String maDon;
    private String maXe;

    public PhanCong(String maDon, String maXe) {
        this.maDon = maDon;
        this.maXe = maXe;
    }

    public String getMaDon() {
        return maDon;
    }

    public void setMaDon(String maDon) {
        this.maDon = maDon;
    }

    public String getMaXe() {
        return maXe;
    }

    public void setMaXe(String maXe) {
        this.maXe = maXe;
    }
}
