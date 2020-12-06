package com.laundry.laundry.model;

import java.io.Serializable;

public class Sepatu implements Serializable {
    private int id;
    private String jenis_layanan, kondisi, jenis_sepatu;

    public Sepatu(int id, String jenis_layanan, String kondisi, String jenis_sepatu) {
        this.id = id;
        this.jenis_layanan = jenis_layanan;
        this.kondisi = kondisi;
        this.jenis_sepatu = jenis_sepatu;
    }

    public Sepatu(String jenis_layanan, String kondisi, String jenis_sepatu) {
        this.jenis_layanan = jenis_layanan;
        this.kondisi = kondisi;
        this.jenis_sepatu = jenis_sepatu;
    }

    public int getId() { return id; }

    public String getStringId() { return String.valueOf(this.id); }

    public void setId(int id) { this.id = id; }

    public String getJenis_layanan() { return jenis_layanan; }

    public void setJenis_layanan(String jenis_layanan) { this.jenis_layanan = jenis_layanan; }

    public String getKondisi() { return kondisi; }

    public void setKondisi(String kondisi) { this.kondisi = kondisi; }

    public String getJenis_sepatu() { return jenis_sepatu; }

    public void setJenis_sepatu(String jenis_sepatu) { this.jenis_sepatu = jenis_sepatu; }
}
