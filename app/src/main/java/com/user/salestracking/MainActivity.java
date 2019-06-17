package com.user.salestracking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SessionManager session;
    private HashMap<String, String> user;
    private ListView listView;
    List<DataDonatur> donatur_list;
    AlertDialog.Builder dialogs;
    private ProgressDialog dialog;
    LayoutInflater inflater;
    TextView txt_nama, txt_email, txt_alamat, txt_jk, txt_noHp;
    EditText txt_catatan, txt_nominal;
    TextView txt_tgl_pembayaran, tgl_lahir;
    Button btn_call, btn_visit, btn_closing, btn_save, btn_edit;
    View dialogView;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView txtName, txtCabang, txtType;
    private Toolbar toolbar;


    private String[] activityTitles;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private String[] pay = {
            "Transfer",
            "Cash"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sales Tracking");
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
        dialog = new ProgressDialog(MainActivity.this);
        donatur_list = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtCabang = (TextView) navHeader.findViewById(R.id.website);
        txtType = (TextView) navHeader.findViewById(R.id.type);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        txtTitle.setText("LIST DONATUR");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

//        setUpNavigationView();
        listView = (ListView) findViewById(R.id.list_view);

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
        request();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                dialogFormDetail(position);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        drawer.addDrawerListener(toggle);

        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.create_akun).setVisible(false);
            nav_Menu.findItem(R.id.create_donatur).setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(this);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_call:
                        Intent intent = new Intent(MainActivity.this,List_Call.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.list_visit:
                        Intent intent1 = new Intent(MainActivity.this,List_visit.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.list_closing:
                        Intent intent2 = new Intent(MainActivity.this,List_closing.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    private void popup(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda Yakin ingin keluar dari aplikasi?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
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

    private void logout(){
        session.logoutUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void request() {
        class RegisterUser extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();

                return requestHandler.getRequest(Api_url.GET_DATA_DONATUR);
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
                result(result);

            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private void result(String result){
        Log.d("result", result);
        JSONObject jsonObject;
        JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);

                    donatur_list.add(new DataDonatur(jsonObject.getString("id"),
                            jsonObject.getString("nama"),
                            jsonObject.getString("email"),
                            jsonObject.getString("jenis_kelamin"),
                            jsonObject.getString("alamat"),
                            jsonObject.getString("nomor_hanphone"),
                            jsonObject.getString("tgl_lahir")));
                    DonaturlistAdapter adapter = new DonaturlistAdapter(this, R.layout.row_data_donatur, donatur_list);
                    listView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void dialogFormDetail(final int pos) {
        dialogs = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_donatur, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);
        dialogs.setTitle("Data Donatur");

        txt_nama    = (TextView) dialogView.findViewById(R.id.txt_nama);
        txt_email  = (TextView) dialogView.findViewById(R.id.txt_email);
        txt_noHp  = (TextView) dialogView.findViewById(R.id.txt_noHp);
        txt_jk    = (TextView) dialogView.findViewById(R.id.txt_JK);
        txt_alamat  = (TextView) dialogView.findViewById(R.id.txt_alamat);

        txt_nama.setText("Nama : "+donatur_list.get(pos).getName());
        txt_email.setText("Email : "+donatur_list.get(pos).getEmail());
        txt_noHp.setText("No Handphone : "+donatur_list.get(pos).getNo_hp());
        txt_jk.setText("Jenis Kelamin : "+donatur_list.get(pos).getJenis_kelamin());
        txt_alamat.setText("Alamat : "+donatur_list.get(pos).getAlamat());

        btn_call = (Button) dialogView.findViewById(R.id.btn_call);
        btn_visit = (Button) dialogView.findViewById(R.id.btn_visit);
        btn_closing = (Button) dialogView.findViewById(R.id.btn_closing);
        btn_edit = (Button) dialogView.findViewById(R.id.btn_edit);

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            btn_edit.setVisibility(View.GONE);
        }else{
            btn_edit.setVisibility(View.VISIBLE);
        }

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogCall(pos);
            }
        });

        btn_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogVisit(pos);
            }
        });

        btn_closing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogClosing(pos);
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogEdit_donatur(pos);
            }
        });

        dialogs.show();
    }

    private void dialogCall(final int pos) {
        dialogs = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_call, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        final String url = Api_url.URL_INSERT_CALL;

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
        txt_catatan  = (EditText) dialogView.findViewById(R.id.txt_catatan);
        txt_noHp  = (EditText) dialogView.findViewById(R.id.txt_noHp);

        txt_nama.setText(donatur_list.get(pos).getName());
        txt_noHp.setText(donatur_list.get(pos).getNo_hp());

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_catatan.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Catatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    request_post("CALL", String.valueOf(spinner.getSelectedItem()), txt_nama.getText().toString(),donatur_list.get(pos).getEmail(), donatur_list.get(pos).getAlamat(), donatur_list.get(pos).getJenis_kelamin()
                    , donatur_list.get(pos).getNo_hp(), txt_catatan.getText().toString(), "1", url, user.get(SessionManager.KEY_NAMA), "1");
                }

            }
        });

        dialogs.show();
    }

    private void dialogVisit(final int pos) {
        dialogs = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_visit, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        final String url = Api_url.URL_INSERT_VISIT;

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
        txt_catatan  = (EditText) dialogView.findViewById(R.id.txt_catatan);
        txt_noHp  = (EditText) dialogView.findViewById(R.id.txt_noHp);

        txt_nama.setText(donatur_list.get(pos).getName());
        txt_noHp.setText(donatur_list.get(pos).getNo_hp());

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_catatan.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Catatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    request_post("VISIT", String.valueOf(spinner.getSelectedItem()), txt_nama.getText().toString(),donatur_list.get(pos).getEmail(), donatur_list.get(pos).getAlamat(), donatur_list.get(pos).getJenis_kelamin()
                            , donatur_list.get(pos).getNo_hp(), txt_catatan.getText().toString(), "2", url, user.get(SessionManager.KEY_NAMA), "2");
                }

            }
        });

        dialogs.show();
    }

    private void dialogClosing(final int pos) {
        dialogs = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_closing, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        final String url = Api_url.URL_INSERT_CLOSING;
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner2);
        final TextView txt_akun_bnk    = (TextView) dialogView.findViewById(R.id.tv_akun_bank);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Cash")){
                    spinner1.setVisibility(View.GONE);
                    txt_akun_bnk.setVisibility(View.GONE);
                }else {
                    spinner1.setVisibility(View.VISIBLE);
                    txt_akun_bnk.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (String.valueOf(spinner.getSelectedItem()).equals("Cash")){
            spinner1.setVisibility(View.GONE);
            txt_akun_bnk.setVisibility(View.GONE);
        }else {
            spinner1.setVisibility(View.VISIBLE);
            txt_akun_bnk.setVisibility(View.VISIBLE);
        }

        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
        txt_tgl_pembayaran = (TextView) dialogView.findViewById(R.id.txt_tgl_pembayaran);

        txt_nama.setText(donatur_list.get(pos).getName());

        txt_nominal    = (EditText) dialogView.findViewById(R.id.txt_nominal);
        txt_nominal.addTextChangedListener(onTextChangedListener());

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_tgl_pembayaran.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Tanggal pembayaran tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    if (String.valueOf(spinner.getSelectedItem()).equals("Cash")){
                        request_postClosing("CLOSING", txt_nama.getText().toString(),donatur_list.get(pos).getEmail(), donatur_list.get(pos).getAlamat(), donatur_list.get(pos).getJenis_kelamin()
                                , donatur_list.get(pos).getNo_hp(), "3", url, user.get(SessionManager.KEY_NAMA), "3", String.valueOf(spinner.getSelectedItem()), String.valueOf(spinner.getSelectedItem()),
                                txt_nominal.getText().toString(), txt_tgl_pembayaran.getText().toString());
                    }else {
                        request_postClosing("CLOSING", txt_nama.getText().toString(),donatur_list.get(pos).getEmail(), donatur_list.get(pos).getAlamat(), donatur_list.get(pos).getJenis_kelamin()
                                , donatur_list.get(pos).getNo_hp(), "3", url, user.get(SessionManager.KEY_NAMA), "3", String.valueOf(spinner.getSelectedItem()), String.valueOf(spinner1.getSelectedItem()),
                                txt_nominal.getText().toString(), txt_tgl_pembayaran.getText().toString());
                    }

                }

            }
        });

        txt_tgl_pembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showCalendar();
            }
        });

        dialogs.show();
    }

    private void dialogEdit_donatur(final int pos) {
        dialogs = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_edit_donatur, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        final String url = Api_url.URL_EDIT_DONATUR;

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
        final EditText txt_alamat = (EditText) dialogView.findViewById(R.id.txt_alamat);
        final EditText txt_email = (EditText) dialogView.findViewById(R.id.txt_email);
        tgl_lahir = (TextView) dialogView.findViewById(R.id.txt_tgl_lahir);
        txt_noHp  = (EditText) dialogView.findViewById(R.id.txt_noHp);

        txt_nama.setText(donatur_list.get(pos).getName());
        txt_alamat.setText(donatur_list.get(pos).getAlamat());
        txt_email.setText(donatur_list.get(pos).getEmail());
        tgl_lahir.setText(donatur_list.get(pos).getTanggal_lahir());

        tgl_lahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showCalendar_edit();
            }
        });

        txt_noHp.setText(donatur_list.get(pos).getNo_hp());

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") || tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    request_Edit_donatur(txt_nama.getText().toString(),txt_email.getText().toString(), txt_alamat.getText().toString(),
                            spinner.getSelectedItem().toString(),tgl_lahir.getText().toString(),txt_noHp.getText().toString(),url, donatur_list.get(pos).getId());
                }

            }
        });

        dialogs.show();
    }

    private void dialogCreate_donatur() {
        dialogs = new AlertDialog.Builder(MainActivity.this);
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
                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") || tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    request_Edit_donatur(txt_nama.getText().toString(),txt_email.getText().toString(), txt_alamat.getText().toString(),
                            spinner.getSelectedItem().toString(),tgl_lahir.getText().toString(),txt_noHp.getText().toString(),url, "");
                }

            }
        });

        dialogs.show();
    }

    private void dialogPilih_akun() {
        dialogs = new AlertDialog.Builder(MainActivity.this);
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

    private void dialogCreate_akun(String type_account) {
        dialogs = new AlertDialog.Builder(MainActivity.this);
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

    public void showCalendar() {
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
                        txt_tgl_pembayaran.setText(date);

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


    private void request_post(final String aktivitas, final String hasil_aktivitas, final String nama, final String email, final String alamat, final String jenis_kelamin,
                              final String noHp, final String catatan, final String type, final String url, final String assign, final String status) {
        class insert_call extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                String formattedDate = df.format(c);

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("aktivitas", aktivitas);
                params.put("hasil_aktivitas", hasil_aktivitas);
                params.put("nama", nama);
                params.put("email", email);
                params.put("alamat", alamat);
                params.put("jenis_kelamin", jenis_kelamin);
                params.put("no_hp", noHp);
                params.put("catatan", catatan);
                params.put("type_aktivitas", type);
                params.put("date_record", formattedDate);
                params.put("assign_by", assign);
                params.put("status", status);
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

    private void request_postClosing(final String aktivitas, final String nama, final String email, final String alamat, final String jenis_kelamin,
                                     final String noHp, final String type, final String url, final String assign, final String status, final String typeTransfer,
                                     final String akunBank, final String nominal, final String dateTransfer) {
        class insert_call extends AsyncTask<Void, Void, String> {


            @SuppressLint("SimpleDateFormat")
            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm");
                String formattedDate = df.format(c);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date newDate = null;
                try {
                    newDate = format.parse(dateTransfer);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                format = new SimpleDateFormat("dd MMMM yyyy");
                String datetf = format.format(newDate);

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("aktivitas", aktivitas);
                params.put("nama", nama);
                params.put("email", email);
                params.put("alamat", alamat);
                params.put("jenis_kelamin", jenis_kelamin);
                params.put("no_hp", noHp);
                params.put("type_aktivitas", type);
                params.put("date_record", formattedDate);
                params.put("assign_by", assign);
                params.put("status", status);
                params.put("type_transfer", typeTransfer);
                params.put("akun_bank", akunBank);
                params.put("nominal", nominal);
                params.put("tanggal_transfer", datetf);
                //returing the response
                Log.d("jsonClose", params.toString());
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.list_call) {
            startActivity(new Intent(MainActivity.this, List_Call.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.list_visit) {
            startActivity(new Intent(MainActivity.this, List_visit.class));
            drawer.closeDrawers();
            finish();
        } else if (id == R.id.list_closing) {
            startActivity(new Intent(MainActivity.this, List_closing.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.nav_about_us) {
            Toast.makeText(getApplicationContext(), "about_us", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.create_donatur) {
            dialogCreate_donatur();

        } else if (id == R.id.create_akun) {
            dialogPilih_akun();

        } else if (id == R.id.list_donatur) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.logout) {
            popup();
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txt_nominal.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    txt_nominal.setText(formattedString);
                    txt_nominal.setSelection(txt_nominal.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                txt_nominal.addTextChangedListener(this);
            }
        };
    }

    @Override
    public void onBackPressed() {
        popup_close();
    }
}
