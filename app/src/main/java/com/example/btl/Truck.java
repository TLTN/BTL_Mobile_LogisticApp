package com.example.btl;

public class Truck {
    private String maXe;
    private String plate;
    private String status;
    private String maTaiXe;

    public Truck(String maXe, String plate, String status, String maTaiXe) {
        this.maXe = maXe;
        this.plate = plate;
        this.status = status;
        this.maTaiXe = maTaiXe;
    }

    public String getMaXe() {
        return maXe;
    }

    public void setMaXe(String maXe) {
        this.maXe = maXe;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaTaiXe() {
        return maTaiXe;
    }

    public void setMaTaiXe(String maTaiXe) {
        this.maTaiXe = maTaiXe;
    }
}
