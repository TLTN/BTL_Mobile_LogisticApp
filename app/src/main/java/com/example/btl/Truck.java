package com.example.btl;

public class Truck {
    private int id;
    private String name;
    private String plate;
    private String status;

    public Truck(int id, String name, String plate, String status) {
        this.id = id;
        this.name = name;
        this.plate = plate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlate() {
        return plate;
    }

    public String getStatus() {
        return status;
    }
}
