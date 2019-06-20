package com.user.salestracking;

public class DataListClosing {
    private String id;
    private String aktivitas;
    private String nama;
    private String email;
    private String alamat;
    private String jenis_kelamin;
    private String no_hp;
    private String type_aktivitas;
    private String date_record;
    private String assign_by;
    private String type_transfer;
    private String akun_bank;
    private String nominal;
    private String tanggal_transfer;
    private String url_image;

    public DataListClosing(String id, String aktivitas, String nama, String email, String alamat, String jenis_kelamin, String no_hp, String type_aktivitas, String date_record, String assign_by, String type_transfer, String akun_bank, String nominal, String tanggal_transfer, String url_image) {
        this.id = id;
        this.aktivitas = aktivitas;
        this.nama = nama;
        this.email = email;
        this.alamat = alamat;
        this.jenis_kelamin = jenis_kelamin;
        this.no_hp = no_hp;
        this.type_aktivitas = type_aktivitas;
        this.date_record = date_record;
        this.assign_by = assign_by;
        this.type_transfer = type_transfer;
        this.akun_bank = akun_bank;
        this.nominal = nominal;
        this.tanggal_transfer = tanggal_transfer;
        this.url_image = url_image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas = aktivitas;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getType_aktivitas() {
        return type_aktivitas;
    }

    public void setType_aktivitas(String type_aktivitas) {
        this.type_aktivitas = type_aktivitas;
    }

    public String getDate_record() {
        return date_record;
    }

    public void setDate_record(String date_record) {
        this.date_record = date_record;
    }

    public String getAssign_by() {
        return assign_by;
    }

    public void setAssign_by(String assign_by) {
        this.assign_by = assign_by;
    }

    public String getType_transfer() {
        return type_transfer;
    }

    public void setType_transfer(String type_transfer) {
        this.type_transfer = type_transfer;
    }

    public String getAkun_bank() {
        return akun_bank;
    }

    public void setAkun_bank(String akun_bank) {
        this.akun_bank = akun_bank;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getTanggal_transfer() {
        return tanggal_transfer;
    }

    public void setTanggal_transfer(String tanggal_transfer) {
        this.tanggal_transfer = tanggal_transfer;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }
}
