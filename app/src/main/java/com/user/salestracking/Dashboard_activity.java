package com.user.salestracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.user.salestracking.Api_Service.Api_url;
import com.user.salestracking.Api_Service.RequestHandler;
import com.user.salestracking.db.DatabaseClosing;
import com.user.salestracking.db.DatabaseHelper;
import com.user.salestracking.db.DatabaseList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Dashboard_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SessionManager session;
    private HashMap<String, String> user;
    List<DataDonatur> donatur_list;
    AlertDialog.Builder dialogs;
    private ProgressDialog dialog;
    LayoutInflater inflater;
    TextView txt_nama, txt_email, txt_alamat, txt_jk, txt_noHp;
    EditText txt_catatan, txt_nominal;
    TextView txt_tgl_pembayaran, tgl_lahir, tv_total_call, tv_total_visit, tv_total_closing;
    Button btn_call, btn_visit, btn_closing, btn_save, btn_edit;
    View dialogView;
    LinearLayout ln_call, ln_visit, ln_close;

    ArrayList<String>call_aray = new ArrayList<>();
    ArrayList<String>visit_aray= new ArrayList<>();
    ArrayList<String>close_aray = new ArrayList<>();

    List<DataListCall> dataListCall;
    List<DataListClosing> dataListClosing;
    List<DataListVisit> dataListVisits;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView txtName, txtCabang, txtType;
    private Toolbar toolbar;


    private String[] activityTitles;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private MediaPlayer mediaPlayer;
    private DatabaseHelper db;
    private DonaturlistAdapter adapter;
    private List<DataDonatur> donaturList = new ArrayList<>();
    private DatabaseList db_list;
    private DatabaseClosing db_closing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        db = new DatabaseHelper(this);
        db_list = new DatabaseList(this);
        db_closing = new DatabaseClosing(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.audio1);
        call_aray.clear();
        visit_aray.clear();
        close_aray.clear();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        findViewById(R.id.drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                else
                    drawer.openDrawer(GravityCompat.END);
            }
        });

        mHandler = new Handler();
        dialog = new ProgressDialog(Dashboard_activity.this);
        donatur_list = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtCabang = (TextView) navHeader.findViewById(R.id.website);
        txtType = (TextView) navHeader.findViewById(R.id.type);

        tv_total_call = (TextView) findViewById(R.id.tv_total_call);
        tv_total_visit = (TextView) findViewById(R.id.tv_total_visit);
        tv_total_closing = (TextView) findViewById(R.id.tv_total_close);

        ln_call = (LinearLayout) findViewById(R.id.ln_call_dashboard);
        ln_visit = (LinearLayout) findViewById(R.id.ln_visit_dashboard);
        ln_close = (LinearLayout) findViewById(R.id.ln_closing_dashboard);

        ln_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard_activity.this, List_Call.class));
                finish();
            }
        });

        ln_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard_activity.this, List_visit.class));
                finish();
            }
        });

        ln_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard_activity.this, List_closing.class));
                finish();
            }
        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        txtName.setText(user.get(SessionManager.KEY_NAMA));
        txtCabang.setText(user.get(SessionManager.KEY_EMAIL));

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            txtType.setText("Sales Marketing");
        }else if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("1")){
            txtType.setText("Admin");
        }else if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("2")){
            txtType.setText("Donatur");
        }else {
            txtType.setText("Owner");
        }
//        request_call();

        tv_total_call.setText(""+db_list.getAllDonatur().size());
        tv_total_visit.setText(""+db_list.getAllDonatur_visit().size());
        tv_total_closing.setText(""+db_closing.getAllDonatur().size());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);

        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.create_akun).setVisible(false);

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            nav_Menu.findItem(R.id.create_akun).setVisible(false);
            nav_Menu.findItem(R.id.create_donatur).setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(this);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_call:
                        Intent intent = new Intent(Dashboard_activity.this,List_Call.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.list_visit:
                        Intent intent1 = new Intent(Dashboard_activity.this,List_visit.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.list_closing:
                        Intent intent2 = new Intent(Dashboard_activity.this,List_closing.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            startActivity(new Intent(Dashboard_activity.this, Dashboard_activity.class));
            drawer.closeDrawers();
            finish();

        }else if (id == R.id.list_call) {
            startActivity(new Intent(Dashboard_activity.this, List_Call.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.list_visit) {
            startActivity(new Intent(Dashboard_activity.this, List_visit.class));
            drawer.closeDrawers();
            finish();
        } else if (id == R.id.list_closing) {
            startActivity(new Intent(Dashboard_activity.this, List_closing.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.nav_about_us) {
            dialogAboutUs();

        } else if (id == R.id.create_donatur) {
            dialogCreate_donatur();

        } else if (id == R.id.create_akun) {
            dialogPilih_akun();

        } else if (id == R.id.list_donatur) {
            startActivity(new Intent(Dashboard_activity.this, MainActivity.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.logout) {
            mediaPlayer.start();
            popup();
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    ///////////////// ALERT DIALOG ///////////////////

    private void popup(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda Yakin ingin keluar dari aplikasi?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        logout();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Logout?");
        alert.show();
    }

    private void popup_close(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda Yakin ingin keluar dari aplikasi?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void popup_success(String message){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Dashboard_activity.this, Dashboard_activity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void popup_failed(String message){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    ///////////////// ALERT DIALOG /////////////////

    private void logout(){
        session.logoutUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

//    private void dialogCreate_donatur() {
//        dialogs = new AlertDialog.Builder(Dashboard_activity.this);
//        inflater = getLayoutInflater();
//        dialogView = inflater.inflate(R.layout.dialog_edit_donatur, null);
//        dialogs.setView(dialogView);
//        dialogs.setCancelable(true);
//
//        final String url = Api_url.URL_CREATE_DONATUR;
//
//        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
//        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
//        final EditText txt_alamat = (EditText) dialogView.findViewById(R.id.txt_alamat);
//        final EditText txt_email = (EditText) dialogView.findViewById(R.id.txt_email);
//        tgl_lahir = (TextView) dialogView.findViewById(R.id.txt_tgl_lahir);
//        txt_noHp  = (EditText) dialogView.findViewById(R.id.txt_noHp);
//        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
//        txt_title.setText("CREATE DONATUR");
//
//        tgl_lahir.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                showCalendar_edit();
//            }
//        });
//
//        btn_save = (Button) dialogView.findViewById(R.id.btn_save);
//        btn_save.setText("CREATE");
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") || tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
//                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
//                }else {
//                    request_Edit_donatur(txt_nama.getText().toString(),txt_email.getText().toString(), txt_alamat.getText().toString(),
//                            spinner.getSelectedItem().toString(),tgl_lahir.getText().toString(),txt_noHp.getText().toString(),url, "");
//                }
//
//            }
//        });
//
//        dialogs.show();
//    }

    private void dialogPilih_akun() {
        dialogs = new AlertDialog.Builder(Dashboard_activity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_pilih_akun, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);


        Button btn_admin = (Button) dialogView.findViewById(R.id.btn_admin);

        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogCreate_akun("1");
            }
        });

        Button btn_sales = (Button) dialogView.findViewById(R.id.btn_sales);

        btn_sales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogCreate_akun("2");
            }
        });

        dialogs.show();
    }

    private void dialogAboutUs() {
        dialogs = new AlertDialog.Builder(Dashboard_activity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_about_us, null);
        TextView txt_about = (TextView) dialogView.findViewById(R.id.txt_about);
        txt_about.setText("Sales tracking merupakan aplikasi untuk mencatat aktivitas sales, dan mememudahkan dalam pelaporan.\n" +
                "Dalam aplikasi ini terdapat 2 Role yaitu Admin dan Sales, masing - masing punya Aplikasi untuk tiap role.\n" +
                "\n" +
                "Admin berfungsi untuk memantau aktifitas sales dan mendaftarkan sales baru\n" +
                "\n" +
                "Sales berfungsi untuk melakukan penginputan donatur dan aktivitas.\n" +
                "\n" +
                "alur :\n" +
                "1.  Sales melakukan login aplikasi dengan mengisi username dan password.\n" +
                "2. Klik garis di pojok kanan untuk memulai pencatatan.\n" +
                "3. Pilih Donatur baru jika ingin membuat aku donatur.\n" +
                "4. Pilih List Donatur jika ingin memulai input aktivitas.\n" +
                "5. Untuk melihat laporan aktivitas, pilih list call, list visit atau list closing.");
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        dialogs.show();
    }

    private void dialogCreate_akun(String type_account) {
        dialogs = new AlertDialog.Builder(Dashboard_activity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_edit_donatur, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        final String url = Api_url.URL_CREATE_ACCOUNT;

        String type = null;
        String type_desc = null;

        if (type_account.equals("1")){
            type = "1";
            type_desc = "Admin";
        }else {
            type = "3";
            type_desc = "Sales Marketing";
        }

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
        final EditText txt_alamat = (EditText) dialogView.findViewById(R.id.txt_alamat);
        final EditText txt_email = (EditText) dialogView.findViewById(R.id.txt_email);
        tgl_lahir = (TextView) dialogView.findViewById(R.id.txt_tgl_lahir);
        txt_noHp  = (EditText) dialogView.findViewById(R.id.txt_noHp);
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        txt_title.setText("CREATE ACCOUNT");

        LinearLayout ln_password = (LinearLayout) dialogView.findViewById(R.id.ln_password);
        LinearLayout ln_cabang = (LinearLayout) dialogView.findViewById(R.id.ln_cabang);
        ln_password.setVisibility(View.VISIBLE);
        ln_cabang.setVisibility(View.VISIBLE);
        final EditText txt_password = (EditText) dialogView.findViewById(R.id.txt_password);
        final EditText txt_cabang = (EditText) dialogView.findViewById(R.id.txt_cabang);

        tgl_lahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showCalendar_edit();
            }
        });


        btn_save = (Button) dialogView.findViewById(R.id.btn_save);
        btn_save.setText("CREATE");

        final String finalType = type;
        final String finalType_desc = type_desc;

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") ||
                        tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("") ||  txt_password.getText().toString().equals("") || txt_cabang.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    request_create_akun(txt_nama.getText().toString(),txt_email.getText().toString(), txt_alamat.getText().toString(),
                            spinner.getSelectedItem().toString(),tgl_lahir.getText().toString(),txt_noHp.getText().toString(),url, "",
                            finalType, finalType_desc, txt_password.getText().toString(),txt_cabang.getText().toString());
                }

            }
        });

        dialogs.show();
    }

    public void showCalendar_edit() {
        Calendar calendar = Calendar.getInstance();
        MonthAdapter.CalendarDay currDate = new MonthAdapter.CalendarDay();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        currDate.setDay(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                    @Override
                    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear+1;
                        String date = "";
                        if (dayOfMonth < 10){
                            if (month < 10){
                                date = "0"+dayOfMonth+"-"+"0"+month+"-"+year;
                            }else {
                                date = "0"+dayOfMonth+"-"+month+"-"+year;
                            }
                        }else {
                            if (month < 10){
                                date = dayOfMonth+"-"+"0"+month+"-"+year;
                            }else {
                                date = dayOfMonth+"-"+month+"-"+year;
                            }
                        }
                        tgl_lahir.setText(date);

                    }
                })
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(year, month, day)
                .setDateRange(null, null)
                .setDoneText("Yes")
                .setThemeLight()
                .setCancelText("Cancel");
        cdp.show((getSupportFragmentManager()),"Promise Date");
    }

    ////////////////////// DIALOG ////////////////////////////

    /////////////// REQUEST SERVICE ////////////////////////////

    private void request_call() {
        class RegisterUser extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                return requestHandler.getRequest(Api_url.GET_LIST_CALL);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("please wait...");
                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dialog.dismiss();
                result_call(result);

            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void result_call(String result){
        Log.d("result", result);
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("assign_by").equals(user.get(SessionManager.KEY_NAMA))){
                        call_aray.add(jsonObject.getString("nama"));
                }
            }
            request_visit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void request_visit() {
        class RegisterUser extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                return requestHandler.getRequest(Api_url.GET_LIST_VISIT);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("please wait...");
                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dialog.dismiss();
                result_visit(result);

            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void result_visit(String result){
        Log.d("result", result);
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("assign_by").equals(user.get(SessionManager.KEY_NAMA))){
                        visit_aray.add(jsonObject.getString("nama"));
                    }
                }
                request_closing();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void request_closing() {
        class RegisterUser extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                return requestHandler.getRequest(Api_url.GET_LIST_CLOSING);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("please wait...");
                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dialog.dismiss();
                result_closing(result);

            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void result_closing(String result){
        Log.d("result", result);
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++){
                jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("assign_by").equals(user.get(SessionManager.KEY_NAMA))){
                        close_aray.add(jsonObject.getString("nama"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (call_aray.isEmpty()){
            tv_total_call.setText("0");
        }else {
            tv_total_call.setText(call_aray.size()+"");
        }

        if (visit_aray.isEmpty()){
            tv_total_visit.setText("0");
        }else {
            tv_total_visit.setText(visit_aray.size()+"");
        }

        if (close_aray.isEmpty()){
            tv_total_closing.setText("0");
        }else {
            tv_total_closing.setText(close_aray.size()+"");
        }

    }

    private void request_Edit_donatur(final String nama, final String email, final String alamat, final String jenis_kelamin, final String tanggal_lahir,
                                      final String noHp,final String url,final String id) {
        class insert_call extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("email", email);
                params.put("alamat", alamat);
                params.put("jenis_kelamin", jenis_kelamin);
                params.put("nomor_hanphone", noHp);
                params.put("tgl_lahir", tanggal_lahir);
                params.put("id", id);
                //returing the response
                return requestHandler.sendPostRequest(url, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                dialog.setMessage("please wait...");
                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("result", result);
                dialog.dismiss();

                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("status") == 0) {
                        popup_success(obj.getString("message"));
                    } else {
                        popup_failed(obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        insert_call ru = new insert_call();
        ru.execute();
    }

    private void request_create_akun(final String nama, final String email, final String alamat, final String jenis_kelamin, final String tanggal_lahir,
                                     final String noHp, final String url, final String id, final String type, final String type_desc, final String password,
                                     final String cabang) {
        class insert_call extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("email", email);
                params.put("alamat", alamat);
                params.put("jenis_kelamin", jenis_kelamin);
                params.put("nomor_hanphone", noHp);
                params.put("tgl_lahir", tanggal_lahir);
                params.put("id", id);
                params.put("type", type);
                params.put("type_desc", type_desc);
                params.put("password", password);
                params.put("cabang", cabang);
                //returing the response
                return requestHandler.sendPostRequest(url, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                dialog.setMessage("please wait...");
                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("result", result);
                dialog.dismiss();

                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("status") == 0) {
                        popup_success(obj.getString("message"));
                    } else {
                        popup_failed(obj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        insert_call ru = new insert_call();
        ru.execute();
    }

    private void createNote(String name, String email, String jk, String alamat, String no_hp, String tgl_lahir) {
        long id = db.insertDonatur(name, email, jk, alamat, no_hp, tgl_lahir);

        // get the newly inserted note from db
        DataDonatur n = db.getDonatur(id);

        if (n != null) {
            // adding new note to array list at 0 position
            donaturList.add(0, n);

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage("success")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void updateNote(DataDonatur dataDonatur, int position) {
        DataDonatur n = donaturList.get(position);
        // updating note text
        n.setName(dataDonatur.getName());
        n.setEmail(dataDonatur.getEmail());
        n.setJenis_kelamin(dataDonatur.getJenis_kelamin());
        n.setAlamat(dataDonatur.getAlamat());
        n.setNo_hp(dataDonatur.getNo_hp());
        n.setTanggal_lahir(dataDonatur.getTanggal_lahir());

        // updating note in db
        db.updateDonatur(n);

        // refreshing the list
        donaturList.set(position, n);
        adapter.notifyDataSetChanged();
    }

    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteDonatur(donaturList.get(position));

        // removing the note from the list
        donaturList.remove(position);

    }

    private void dialogCreate_donatur() {
        dialogs = new AlertDialog.Builder(Dashboard_activity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_edit_donatur, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        final String url = Api_url.URL_CREATE_DONATUR;

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
        final EditText txt_alamat = (EditText) dialogView.findViewById(R.id.txt_alamat);
        final EditText txt_email = (EditText) dialogView.findViewById(R.id.txt_email);
        tgl_lahir = (TextView) dialogView.findViewById(R.id.txt_tgl_lahir);
        txt_noHp  = (EditText) dialogView.findViewById(R.id.txt_noHp);
        TextView txt_title = (TextView)dialogView.findViewById(R.id.txt_title);
        txt_title.setText("CREATE DONATUR");

        tgl_lahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showCalendar_edit();
            }
        });

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);
        btn_save.setText("CREATE");
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") ||
                        tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    createNote(txt_nama.getText().toString(),txt_email.getText().toString(), String.valueOf(spinner.getSelectedItem()), txt_alamat.getText().toString(), txt_noHp.getText().toString(), tgl_lahir.getText().toString());
                }

            }
        });

        dialogs.show();
    }


    @Override
    public void onBackPressed() {
        mediaPlayer.start();
        popup_close();
    }
}
