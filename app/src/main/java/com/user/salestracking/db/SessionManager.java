package com.user.salestracking.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID = "id";
    public static final String KEY_CABANG = "cabang";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_JENIS_KELAMIN = "jenis_kelamin";
    public static final String KEY_TYPE_ACCOUNT = "type_account";
    public static final String KEY_NAMA = "nama";
    public static final String KEY_ALAMAT = "alamat";
    public static final String KEY_TGL_LAHIR = "tgl_lahir";
    public static final String KEY_NO_HANDPHONE = "nomor_handphone";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String email, String nama, String type_account){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_ID, id);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_TYPE_ACCOUNT, type_account);
        editor.putString(KEY_NAMA, nama);

        // commit changes
        editor.commit();
    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_TGL_LAHIR, pref.getString(KEY_TGL_LAHIR, null));
        user.put(KEY_NO_HANDPHONE, pref.getString(KEY_NO_HANDPHONE, null));
        user.put(KEY_TYPE_ACCOUNT, pref.getString(KEY_TYPE_ACCOUNT, null));
        user.put(KEY_NAMA, pref.getString(KEY_NAMA, null));
        user.put(KEY_JENIS_KELAMIN, pref.getString(KEY_JENIS_KELAMIN, null));
        user.put(KEY_ALAMAT, pref.getString(KEY_ALAMAT, null));
        user.put(KEY_CABANG, pref.getString(KEY_CABANG, null));

        // return user
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
