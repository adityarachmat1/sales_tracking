package com.user.salestracking.Data;

public class DataDonatur {
    public static final String TABLE_NAME = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_JENIS_KELAMIN = "jenis_kelamin";
    public static final String COLUMN_ALAMAT = "alamat";
    public static final String COLUMN_NO_HP = "no_hp";
    public static final String COLUMN_TGL_LAHIR = "tanggal_lahir";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String name;
    private String email;
    private String jenis_kelamin;
    private String alamat;
    private String no_hp;
    private String tanggal_lahir;
    private String timestamp;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_EMAIL + " TEXT,"
                    + COLUMN_JENIS_KELAMIN + " TEXT,"
                    + COLUMN_ALAMAT + " TEXT,"
                    + COLUMN_NO_HP + " TEXT,"
                    + COLUMN_TGL_LAHIR + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";
    public DataDonatur() {
    }

    public DataDonatur(int id, String name, String email, String jenis_kelamin, String alamat, String no_hp, String tgl_lahir, String timestamp) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.no_hp = no_hp;
        this.tanggal_lahir = tgl_lahir;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}