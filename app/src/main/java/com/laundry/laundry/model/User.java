package com.laundry.laundry.model;

import androidx.appcompat.app.AppCompatActivity;

public class User  {

    private String userId, nama, alamat, email, password;

    public User() {

    }

    public User(String userId, String nama, String alamat, String email, String password) {
        this.userId = userId;
        this.nama = nama;
        this.alamat = alamat;
        this.email = email;
        this.password = password;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
