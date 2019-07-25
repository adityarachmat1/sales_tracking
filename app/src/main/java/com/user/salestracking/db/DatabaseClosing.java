package com.user.salestracking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.user.salestracking.DataDonatur;
import com.user.salestracking.DataListCall;
import com.user.salestracking.DataListClosing;

import java.util.ArrayList;
import java.util.List;

public class DatabaseClosing extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_li_db";


    public DatabaseClosing(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(DataListClosing.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DataListClosing.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertDonatur(String name, String email, String jenis_kelamin, String alamat, String no_hp, String aktivitas, String hasil_aktivitas, String catatan
            , String type_aktivitas, String date_record, String assign_by, String type_transfer, String akun_bank, String nominal, String tanggal_transfer, String url_image) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DataListClosing.COLUMN_NAME, name);
        values.put(DataListClosing.COLUMN_EMAIL, email);
        values.put(DataListClosing.COLUMN_JENIS_KELAMIN, jenis_kelamin);
        values.put(DataListClosing.COLUMN_ALAMAT, alamat);
        values.put(DataListClosing.COLUMN_NO_HP, no_hp);
        values.put(DataListClosing.COLUMN_AKTIVITAS, aktivitas);
        values.put(DataListClosing.COLUMN_HASIL_AKTIVITAS, hasil_aktivitas);
        values.put(DataListClosing.COLUMN_CATATAN, catatan);
        values.put(DataListClosing.COLUMN_TYPE_AKTIVITAS, type_aktivitas);
        values.put(DataListClosing.COLUMN_DATE_RECORD, date_record);
        values.put(DataListClosing.COLUMN_ASSIGN_BY, assign_by);
        values.put(DataListClosing.COLUMN_TYPE_TRANSFER, type_transfer);
        values.put(DataListClosing.COLUMN_AKUN_BANK, akun_bank);
        values.put(DataListClosing.COLUMN_NOMINAL, nominal);
        values.put(DataListClosing.COLUMN_TANGGAL_TRANSFER, tanggal_transfer);
        values.put(DataListClosing.COLUMN_URL_IMAGE, url_image);

        // insert row
        long id = db.insert(DataListClosing.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public DataListClosing getDonatur(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DataListClosing.TABLE_NAME,
                new String[]{DataListClosing.COLUMN_ID, DataListClosing.COLUMN_NAME,DataListClosing.COLUMN_EMAIL, DataListClosing.COLUMN_JENIS_KELAMIN
                        ,DataListClosing.COLUMN_ALAMAT, DataListClosing.COLUMN_NO_HP,DataListClosing.COLUMN_AKTIVITAS,DataListClosing.COLUMN_HASIL_AKTIVITAS
                        ,DataListClosing.COLUMN_CATATAN,DataListClosing.COLUMN_TYPE_AKTIVITAS,DataListClosing.COLUMN_DATE_RECORD,DataListClosing.COLUMN_ASSIGN_BY
                        ,DataListClosing.COLUMN_TYPE_TRANSFER,DataListClosing.COLUMN_AKUN_BANK,DataListClosing.COLUMN_NOMINAL,DataListClosing.COLUMN_TANGGAL_TRANSFER,
                        DataListClosing.COLUMN_URL_IMAGE,DataListClosing.COLUMN_TIMESTAMP},
                DataListClosing.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DataListClosing note = new DataListClosing(
                cursor.getInt(cursor.getColumnIndex(DataListClosing.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_JENIS_KELAMIN)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_ALAMAT)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_NO_HP)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_AKTIVITAS)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_HASIL_AKTIVITAS)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_CATATAN)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TYPE_AKTIVITAS)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_DATE_RECORD)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_ASSIGN_BY)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TYPE_TRANSFER)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_AKUN_BANK)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_NOMINAL)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TANGGAL_TRANSFER)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_URL_IMAGE)),
                cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<DataListClosing> getAllDonatur() {
        List<DataListClosing> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataListClosing.TABLE_NAME + " ORDER BY " +
                DataListClosing.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataListClosing note = new DataListClosing();
                note.setId(cursor.getInt(cursor.getColumnIndex(DataListClosing.COLUMN_ID)));
                note.setName(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_NAME)));
                note.setAlamat(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_ALAMAT)));
                note.setEmail(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_EMAIL)));
                note.setJenis_kelamin(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_JENIS_KELAMIN)));
                note.setNo_hp(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_NO_HP)));
                note.setAktivitas(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_AKTIVITAS)));
                note.setHasil_aktivitas(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_HASIL_AKTIVITAS)));
                note.setCatatan(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_CATATAN)));
                note.setType_aktivitas(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TYPE_AKTIVITAS)));
                note.setDate_record(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_DATE_RECORD)));
                note.setAssign_by(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_ASSIGN_BY)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TIMESTAMP)));
                note.setType_transfer(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TYPE_TRANSFER)));
                note.setAkun_bank(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_AKUN_BANK)));
                note.setNominal(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_NOMINAL)));
                note.setTanggal_transfer(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TANGGAL_TRANSFER)));
                note.setUrl_image(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_URL_IMAGE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DataListClosing.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getDonatursCount() {
        String countQuery = "SELECT  * FROM " + DataListClosing.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateDonatur(DataListClosing note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataListClosing.COLUMN_NAME, note.getName());
        values.put(DataListClosing.COLUMN_EMAIL, note.getEmail());
        values.put(DataListClosing.COLUMN_JENIS_KELAMIN, note.getJenis_kelamin());
        values.put(DataListClosing.COLUMN_ALAMAT, note.getAlamat());
        values.put(DataListClosing.COLUMN_NO_HP, note.getNo_hp());
        values.put(DataListClosing.COLUMN_AKTIVITAS, note.getAktivitas());
        values.put(DataListClosing.COLUMN_HASIL_AKTIVITAS, note.getHasil_aktivitas());
        values.put(DataListClosing.COLUMN_CATATAN, note.getCatatan());
        values.put(DataListClosing.COLUMN_TYPE_AKTIVITAS, note.getType_aktivitas());
        values.put(DataListClosing.COLUMN_DATE_RECORD, note.getDate_record());
        values.put(DataListClosing.COLUMN_ASSIGN_BY, note.getAssign_by());

        // updating row
        return db.update(DataListClosing.TABLE_NAME, values, DataListClosing.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteDonatur(DataListClosing note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataListClosing.TABLE_NAME, DataListClosing.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}

