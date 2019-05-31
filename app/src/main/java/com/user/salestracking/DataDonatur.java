package com.user.salestracking;

public class DataDonatur {
    private String name;
    private String email;
    private String jenis_kelamin;
    private String alamat;
    private String no_hp;

    public DataDonatur(String name, String email, String jenis_kelamin, String alamat, String no_hp) {
        this.name = name;
        this.email = email;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.no_hp = no_hp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }
}
