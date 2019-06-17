package com.user.salestracking;

public class DataDonatur {
    private String id;
    private String name;
    private String email;
    private String jenis_kelamin;
    private String alamat;
    private String no_hp;
    private String tanggal_lahir;

    public DataDonatur(String id, String name, String email, String jenis_kelamin, String alamat, String no_hp, String tgl_lahir) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.no_hp = no_hp;
        this.tanggal_lahir = tgl_lahir;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }
}
