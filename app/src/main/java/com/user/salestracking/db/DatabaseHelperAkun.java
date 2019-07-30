package com.user.salestracking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.user.salestracking.DataDonatur;
import com.user.salestracking.DataSales;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperAkun extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_sales_db";


    public DatabaseHelperAkun(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(DataSales.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DataSales.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertDonatur(String name, String type_akun, String email, String password, String jk, String alamat, String no_hp, String tgl_lahir) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DataSales.COLUMN_NAME, name);
        values.put(DataSales.COLUMN_TYPE_AKUN, type_akun);
        values.put(DataSales.COLUMN_EMAIL, email);
        values.put(DataSales.COLUMN_PASSWORD, password);
        values.put(DataSales.COLUMN_JENIS_KELAMIN, jk);
        values.put(DataSales.COLUMN_ALAMAT, alamat);
        values.put(DataSales.COLUMN_NO_HP, no_hp);
        values.put(DataSales.COLUMN_TGL_LAHIR, tgl_lahir);

        // insert row
        long id = db.insert(DataSales.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public DataSales getDonatur(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DataSales.TABLE_NAME,
                new String[]{DataSales.COLUMN_ID, DataSales.COLUMN_NAME, DataSales.COLUMN_TYPE_AKUN,DataSales.COLUMN_EMAIL, DataSales.COLUMN_PASSWORD, DataSales.COLUMN_JENIS_KELAMIN
                        ,DataSales.COLUMN_ALAMAT, DataSales.COLUMN_NO_HP,DataSales.COLUMN_TGL_LAHIR,DataSales.COLUMN_TIMESTAMP},
                DataSales.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DataSales note = new DataSales(
                cursor.getInt(cursor.getColumnIndex(DataSales.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_TYPE_AKUN)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_JENIS_KELAMIN)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_ALAMAT)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_NO_HP)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_TGL_LAHIR)),
                cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<DataSales> getAllDonatur() {
        List<DataSales> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataSales.TABLE_NAME + " ORDER BY " +
                DataSales.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataSales note = new DataSales();
                note.setId(cursor.getInt(cursor.getColumnIndex(DataSales.COLUMN_ID)));
                note.setName(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_NAME)));
                note.setType_akun(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_TYPE_AKUN)));
                note.setAlamat(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_ALAMAT)));
                note.setEmail(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_EMAIL)));
                note.setPassword(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_PASSWORD)));
                note.setJenis_kelamin(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_JENIS_KELAMIN)));
                note.setNo_hp(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_NO_HP)));
                note.setTanggal_lahir(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_TGL_LAHIR)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DataSales.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getDonatursCount() {
        String countQuery = "SELECT  * FROM " + DataSales.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateDonatur(DataSales note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataSales.COLUMN_NAME, note.getName());
        values.put(DataSales.COLUMN_NAME, note.getType_akun());
        values.put(DataSales.COLUMN_EMAIL, note.getEmail());
        values.put(DataSales.COLUMN_NAME, note.getPassword());
        values.put(DataSales.COLUMN_JENIS_KELAMIN, note.getJenis_kelamin());
        values.put(DataSales.COLUMN_ALAMAT, note.getAlamat());
        values.put(DataSales.COLUMN_NO_HP, note.getNo_hp());
        values.put(DataSales.COLUMN_TGL_LAHIR, note.getTanggal_lahir());

        // updating row
        return db.update(DataSales.TABLE_NAME, values, DataSales.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteDonatur(DataSales note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataSales.TABLE_NAME, DataSales.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
