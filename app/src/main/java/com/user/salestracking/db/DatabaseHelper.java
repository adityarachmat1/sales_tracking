package com.user.salestracking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.user.salestracking.Data.DataDonatur;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(DataDonatur.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DataDonatur.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertDonatur(String name, String email, String jk, String alamat, String no_hp, String tgl_lahir) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DataDonatur.COLUMN_NAME, name);
        values.put(DataDonatur.COLUMN_EMAIL, email);
        values.put(DataDonatur.COLUMN_JENIS_KELAMIN, jk);
        values.put(DataDonatur.COLUMN_ALAMAT, alamat);
        values.put(DataDonatur.COLUMN_NO_HP, no_hp);
        values.put(DataDonatur.COLUMN_TGL_LAHIR, tgl_lahir);

        // insert row
        long id = db.insert(DataDonatur.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public DataDonatur getDonatur(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DataDonatur.TABLE_NAME,
                new String[]{DataDonatur.COLUMN_ID, DataDonatur.COLUMN_NAME,DataDonatur.COLUMN_EMAIL, DataDonatur.COLUMN_JENIS_KELAMIN
                        ,DataDonatur.COLUMN_ALAMAT, DataDonatur.COLUMN_NO_HP,DataDonatur.COLUMN_TGL_LAHIR,DataDonatur.COLUMN_TIMESTAMP},
                DataDonatur.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DataDonatur note = new DataDonatur(
                cursor.getInt(cursor.getColumnIndex(DataDonatur.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_JENIS_KELAMIN)),
                cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_ALAMAT)),
                cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_NO_HP)),
                cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_TGL_LAHIR)),
                cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<DataDonatur> getAllDonatur() {
        List<DataDonatur> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataDonatur.TABLE_NAME + " ORDER BY " +
                DataDonatur.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataDonatur note = new DataDonatur();
                note.setId(cursor.getInt(cursor.getColumnIndex(DataDonatur.COLUMN_ID)));
                note.setName(cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_NAME)));
                note.setAlamat(cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_ALAMAT)));
                note.setEmail(cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_EMAIL)));
                note.setJenis_kelamin(cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_JENIS_KELAMIN)));
                note.setNo_hp(cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_NO_HP)));
                note.setTanggal_lahir(cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_TGL_LAHIR)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DataDonatur.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getDonatursCount() {
        String countQuery = "SELECT  * FROM " + DataDonatur.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateDonatur(DataDonatur note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataDonatur.COLUMN_NAME, note.getName());
        values.put(DataDonatur.COLUMN_EMAIL, note.getEmail());
        values.put(DataDonatur.COLUMN_JENIS_KELAMIN, note.getJenis_kelamin());
        values.put(DataDonatur.COLUMN_ALAMAT, note.getAlamat());
        values.put(DataDonatur.COLUMN_NO_HP, note.getNo_hp());
        values.put(DataDonatur.COLUMN_TGL_LAHIR, note.getTanggal_lahir());

        // updating row
        return db.update(DataDonatur.TABLE_NAME, values, DataDonatur.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteDonatur(DataDonatur note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataDonatur.TABLE_NAME, DataDonatur.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}
