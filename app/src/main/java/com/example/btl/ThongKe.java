package com.example.btl;

public class ThongKe {
    private int id;
    private int tongDon;
    private int donDangGiao;
    private int donHuy;
    private int donDaGiao;
    private int tongXe;
    private int xeDangHoatDong;
    private int xeChuaCoDon;
    private int tongTaiXe;
    private int taiXeChuaCoXe;

    public ThongKe(int tongDon, int donDangGiao, int donHuy, int donDaGiao,
                   int tongXe, int xeDangHoatDong, int xeChuaCoDon,
                   int tongTaiXe, int taiXeChuaCoXe) {
        this.tongDon = tongDon;
        this.donDangGiao = donDangGiao;
        this.donHuy = donHuy;
        this.donDaGiao = donDaGiao;
        this.tongXe = tongXe;
        this.xeDangHoatDong = xeDangHoatDong;
        this.xeChuaCoDon = xeChuaCoDon;
        this.tongTaiXe = tongTaiXe;
        this.taiXeChuaCoXe = taiXeChuaCoXe;
    }

    public int getTongDon() {
        return tongDon;
    }

    public int getDonDangGiao() {
        return donDangGiao;
    }

    public int getDonHuy() {
        return donHuy;
    }

    public int getDonDaGiao() {
        return donDaGiao;
    }

    public int getTongXe() {
        return tongXe;
    }

    public int getXeDangHoatDong() {
        return xeDangHoatDong;
    }

    public int getXeChuaCoDon() {
        return xeChuaCoDon;
    }

    public int getTongTaiXe() {
        return tongTaiXe;
    }

    public int getTaiXeChuaCoXe() {
        return taiXeChuaCoXe;
    }

    public void setTongDon(int i) {
        this.tongDon = i;

    }

    public void setDonDangGiao(int i)
    {
        this.donDangGiao = i;
    }
    public void setDonHuy(int i)
    {
        this.donHuy = i;
    }
    public void setDonDaGiao(int i)
    {
        this.donDaGiao = i;
    }

    public void setTongXe(int i)
    {
        this.tongXe = i;
    }
    public void setXeDangHoatDong(int i)
    {
        this.xeDangHoatDong = i;
    }
    public void setXeChuaCoDon(int i)
    {
        this.xeChuaCoDon = i;
    }
    public void setTongTaiXe(int i)
        {
        this.tongTaiXe = i;
    }
    public void setTaiXeChuaCoXe(int i)
    {
        this.taiXeChuaCoXe = i;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

}
