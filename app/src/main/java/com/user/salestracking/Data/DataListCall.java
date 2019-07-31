package com.user.salestracking.Data;

public class DataListCall {
    public static final String TABLE_NAME = "notes_2";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_JENIS_KELAMIN = "jenis_kelamin";
    public static final String COLUMN_ALAMAT = "alamat";
    public static final String COLUMN_NO_HP = "no_hp";
    public static final String COLUMN_AKTIVITAS = "aktivitas";
    public static final String COLUMN_HASIL_AKTIVITAS = "hasil_aktivitas";
    public static final String COLUMN_CATATAN = "catatan";
    public static final String COLUMN_TYPE_AKTIVITAS = "type_aktivitas";
    public static final String COLUMN_DATE_RECORD = "date_record";
    public static final String COLUMN_ASSIGN_BY = "assign_by";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String name;
    private String email;
    private String jenis_kelamin;
    private String alamat;
    private String no_hp;
    private String aktivitas;
    private String hasil_aktivitas;
    private String catatan;
    private String type_aktivitas;
    private String date_record;
    private String assign_by;
    private String timestamp;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_EMAIL + " TEXT,"
                    + COLUMN_JENIS_KELAMIN + " TEXT,"
                    + COLUMN_ALAMAT + " TEXT,"
                    + COLUMN_NO_HP + " TEXT,"
                    + COLUMN_AKTIVITAS + " TEXT,"
                    + COLUMN_HASIL_AKTIVITAS + " TEXT,"
                    + COLUMN_CATATAN + " TEXT,"
                    + COLUMN_TYPE_AKTIVITAS + " TEXT,"
                    + COLUMN_DATE_RECORD + " TEXT,"
                    + COLUMN_ASSIGN_BY + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public DataListCall() {
    }


    public DataListCall(int id,String name, String email, String jenis_kelamin, String alamat, String no_hp, String aktivitas, String hasil_aktivitas, String catatan
    , String type_aktivitas, String date_record, String assign_by, String timestamp) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.no_hp = no_hp;
        this.aktivitas = aktivitas;
        this.hasil_aktivitas = hasil_aktivitas;
        this.catatan = catatan;
        this.type_aktivitas = type_aktivitas;
        this.date_record = date_record;
        this.assign_by = assign_by;
        this.timestamp = timestamp;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas = aktivitas;
    }

    public String getHasil_aktivitas() {
        return hasil_aktivitas;
    }

    public void setHasil_aktivitas(String hasil_aktivitas) {
        this.hasil_aktivitas = hasil_aktivitas;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
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
