package com.example.btl;

public class PhanCong {
    private String maDon;
    private String maXe;
    private String timestamp;

    public PhanCong(String maDon, String maXe, String timestamp) {
        this.maDon = maDon;
        this.maXe = maXe;
        this.timestamp = timestamp;
    }

    public String getMaDon() {
        return maDon;
    }

    public String getMaXe() {
        return maXe;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getNgayGiao() {
        if (timestamp == null || !timestamp.contains(" ")) {
            return "";
        }
        String[] parts = timestamp.split(" ");
        return parts[0];
    }
}
