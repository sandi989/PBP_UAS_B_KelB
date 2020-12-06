package com.laundry.laundry.model;

import java.io.Serializable;

public class Setrika implements Serializable {
    private int id, jumlah_pakaian;
    private String jenis_pakaian;
    private Double berat;

    public Setrika (int id, Double berat, int jumlah_pakaian, String jenis_pakaian) {
        this.id = id;
        this.berat = berat;
        this.jumlah_pakaian = jumlah_pakaian;
        this.jenis_pakaian = jenis_pakaian;
    }

    public Setrika(Double berat, int jumlah_pakaian, String jenis_pakaian) {
        this.berat = berat;
        this.jumlah_pakaian = jumlah_pakaian;
        this.jenis_pakaian = jenis_pakaian;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getStringId() {
        return String.valueOf(this.id);
    }

    public int getJumlah_pakaian() { return jumlah_pakaian; }

    public void setJumlah_pakaian(int jumlah_pakaian) { this.jumlah_pakaian = jumlah_pakaian; }

    public String getJenis_pakaian() { return jenis_pakaian; }

    public void setJenis_pakaian(String jenis_pakaian) { this.jenis_pakaian = jenis_pakaian; }

    public Double getBerat() { return berat; }

    public void setBerat(Double berat) { this.berat = berat; }
}
