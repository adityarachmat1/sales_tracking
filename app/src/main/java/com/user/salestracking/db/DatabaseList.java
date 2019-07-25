package com.user.salestracking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.user.salestracking.DataDonatur;
import com.user.salestracking.DataListCall;

import java.util.ArrayList;
import java.util.List;

public class DatabaseList extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_list_db";


    public DatabaseList(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(DataListCall.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DataListCall.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertDonatur(String name, String email, String jenis_kelamin, String alamat, String no_hp, String aktivitas, String hasil_aktivitas, String catatan
            , String type_aktivitas, String date_record, String assign_by) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DataListCall.COLUMN_NAME, name);
        values.put(DataListCall.COLUMN_EMAIL, email);
        values.put(DataListCall.COLUMN_JENIS_KELAMIN, jenis_kelamin);
        values.put(DataListCall.COLUMN_ALAMAT, alamat);
        values.put(DataListCall.COLUMN_NO_HP, no_hp);
        values.put(DataListCall.COLUMN_AKTIVITAS, aktivitas);
        values.put(DataListCall.COLUMN_HASIL_AKTIVITAS, hasil_aktivitas);
        values.put(DataListCall.COLUMN_CATATAN, catatan);
        values.put(DataListCall.COLUMN_TYPE_AKTIVITAS, type_aktivitas);
        values.put(DataListCall.COLUMN_DATE_RECORD, date_record);
        values.put(DataListCall.COLUMN_ASSIGN_BY, assign_by);

        // insert row
        long id = db.insert(DataListCall.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public DataListCall getDonatur(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DataListCall.TABLE_NAME,
                new String[]{DataListCall.COLUMN_ID, DataListCall.COLUMN_NAME,DataListCall.COLUMN_EMAIL, DataListCall.COLUMN_JENIS_KELAMIN
                        ,DataListCall.COLUMN_ALAMAT, DataListCall.COLUMN_NO_HP,DataListCall.COLUMN_AKTIVITAS,DataListCall.COLUMN_HASIL_AKTIVITAS
                        ,DataListCall.COLUMN_CATATAN,DataListCall.COLUMN_TYPE_AKTIVITAS,DataListCall.COLUMN_DATE_RECORD,DataListCall.COLUMN_ASSIGN_BY
                        ,DataListCall.COLUMN_TIMESTAMP},
                DataListCall.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DataListCall note = new DataListCall(
                cursor.getInt(cursor.getColumnIndex(DataListCall.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_JENIS_KELAMIN)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_ALAMAT)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_NO_HP)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_AKTIVITAS)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_HASIL_AKTIVITAS)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_CATATAN)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_TYPE_AKTIVITAS)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_DATE_RECORD)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_ASSIGN_BY)),
                cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<DataListCall> getAllDonatur() {
        List<DataListCall> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataListCall.TABLE_NAME + " WHERE " + DataListCall.COLUMN_TYPE_AKTIVITAS + " = 1 "+ " ORDER BY " +
                DataListCall.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataListCall note = new DataListCall();
                note.setId(cursor.getInt(cursor.getColumnIndex(DataListCall.COLUMN_ID)));
                note.setName(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_NAME)));
                note.setAlamat(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_ALAMAT)));
                note.setEmail(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_EMAIL)));
                note.setJenis_kelamin(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_JENIS_KELAMIN)));
                note.setNo_hp(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_NO_HP)));
                note.setAktivitas(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_AKTIVITAS)));
                note.setHasil_aktivitas(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_HASIL_AKTIVITAS)));
                note.setCatatan(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_CATATAN)));
                note.setType_aktivitas(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_TYPE_AKTIVITAS)));
                note.setDate_record(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_DATE_RECORD)));
                note.setAssign_by(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_ASSIGN_BY)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public List<DataListCall> getAllDonatur_visit() {
        List<DataListCall> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DataListCall.TABLE_NAME+ " WHERE " + DataListCall.COLUMN_TYPE_AKTIVITAS + " = 2 "+ " ORDER BY " +
                DataListCall.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataListCall note = new DataListCall();
                note.setId(cursor.getInt(cursor.getColumnIndex(DataListCall.COLUMN_ID)));
                note.setName(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_NAME)));
                note.setAlamat(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_ALAMAT)));
                note.setEmail(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_EMAIL)));
                note.setJenis_kelamin(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_JENIS_KELAMIN)));
                note.setNo_hp(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_NO_HP)));
                note.setAktivitas(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_AKTIVITAS)));
                note.setHasil_aktivitas(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_HASIL_AKTIVITAS)));
                note.setCatatan(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_CATATAN)));
                note.setType_aktivitas(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_TYPE_AKTIVITAS)));
                note.setDate_record(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_DATE_RECORD)));
                note.setAssign_by(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_ASSIGN_BY)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(DataListCall.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getDonatursCount() {
        String countQuery = "SELECT  * FROM " + DataListCall.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateDonatur(DataListCall note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataListCall.COLUMN_NAME, note.getName());
        values.put(DataListCall.COLUMN_EMAIL, note.getEmail());
        values.put(DataListCall.COLUMN_JENIS_KELAMIN, note.getJenis_kelamin());
        values.put(DataListCall.COLUMN_ALAMAT, note.getAlamat());
        values.put(DataListCall.COLUMN_NO_HP, note.getNo_hp());
        values.put(DataListCall.COLUMN_AKTIVITAS, note.getAktivitas());
        values.put(DataListCall.COLUMN_HASIL_AKTIVITAS, note.getHasil_aktivitas());
        values.put(DataListCall.COLUMN_CATATAN, note.getCatatan());
        values.put(DataListCall.COLUMN_TYPE_AKTIVITAS, note.getType_aktivitas());
        values.put(DataListCall.COLUMN_DATE_RECORD, note.getDate_record());
        values.put(DataListCall.COLUMN_ASSIGN_BY, note.getAssign_by());

        // updating row
        return db.update(DataListCall.TABLE_NAME, values, DataListCall.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteDonatur(DataListCall note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataListCall.TABLE_NAME, DataListCall.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}

