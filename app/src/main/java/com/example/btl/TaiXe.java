package com.example.btl;

import java.util.List;

public class TaiXe {
    private String maTaiXe;
    private String tenTaiXe;
    private String sdt;

    public TaiXe(String maTaiXe, String tenTaiXe, String sdt) {
        this.maTaiXe = maTaiXe;
        this.tenTaiXe = tenTaiXe;
        this.sdt = sdt;
    }


    public String getMaTaiXe() {
        return maTaiXe;
    }

    public void setMaTaiXe(String maTaiXe) {
        this.maTaiXe = maTaiXe;
    }

    public String getTenTaiXe() {
        return tenTaiXe;
    }

    public void setTenTaiXe(String tenTaiXe) {
        this.tenTaiXe = tenTaiXe;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
}
