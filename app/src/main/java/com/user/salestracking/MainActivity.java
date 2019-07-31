package com.user.salestracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.user.salestracking.Data.DataDonatur;
import com.user.salestracking.Data.DataListCall;
import com.user.salestracking.Data.DataListClosing;
import com.user.salestracking.Data.DataSales;
import com.user.salestracking.db.DatabaseClosing;
import com.user.salestracking.db.DatabaseHelper;
import com.user.salestracking.db.DatabaseHelperAkun;
import com.user.salestracking.db.DatabaseList;
import com.user.salestracking.db.SessionManager;
import com.user.salestracking.list_adapter.DonaturlistAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    private List<DataListClosing> databaseClosings = new ArrayList<DataListClosing>();
    private List<DataListCall> databaseLists = new ArrayList<DataListCall>();
    private List<DataDonatur> donaturList = new ArrayList<>();


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

    ProgressDialog progressDialog ;
    Button GetImageFromGalleryButton;
    ImageView ShowSelectedImage;
    Bitmap FixBitmap;
    String ImageTag = "image_tag" ;
    String ImageName = "image_data" ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    HttpURLConnection httpURLConnection ;
    URL url_image;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, REQUEST_CAPTURE_IMAGE = 100;
    String datetf;
    String imageFilePath;
    private MediaPlayer mediaPlayer;
    private DatabaseHelper db;
    private DatabaseList db_list;
    private DatabaseClosing db_closing;
    private DonaturlistAdapter adapter;
    private DatabaseHelperAkun db_sales;
    private List<DataSales> salesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sales Tracking");
        mediaPlayer = MediaPlayer.create(this, R.raw.audio1);
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

        db = new DatabaseHelper(this);
        db_sales = new DatabaseHelperAkun(this);
        db_list = new DatabaseList(this);
        db_closing = new DatabaseClosing(this);

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
        Button bty = (Button) findViewById(R.id.btn_export);
        bty.setVisibility(View.GONE);
        txtTitle.setText("LIST DONATUR");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

//        setUpNavigationView();
        listView = (ListView) findViewById(R.id.list_view);

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        txtName.setText(user.get(SessionManager.KEY_NAMA));
//        txtCabang.setText(user.get(SessionManager.KEY_EMAIL));

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            txtType.setText("Sales Marketing");
        }else if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("1")){
            txtType.setText("Admin");
        }else if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("2")){
            txtType.setText("Donatur");
        }else {
            txtType.setText("Owner");
        }
//        request();

        if (db.getAllDonatur() != null){
            donaturList.addAll(db.getAllDonatur());
            donatur_list.addAll(db.getAllDonatur());
        }

        adapter = new DonaturlistAdapter(this, R.layout.row_data_donatur, donaturList);
        listView.setAdapter(adapter);

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
        Menu nav_Menu = navigationView.getMenu();
//        nav_Menu.findItem(R.id.create_akun).setVisible(false);

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            nav_Menu.findItem(R.id.list_sales).setVisible(false);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            startActivity(new Intent(MainActivity.this, Dashboard_activity.class));
            drawer.closeDrawers();
            finish();

        }else if (id == R.id.list_sales) {
            startActivity(new Intent(MainActivity.this, List_Sales.class));
            drawer.closeDrawers();
            finish();

        }else if (id == R.id.list_call) {
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
            dialogAboutUs();

        } else if (id == R.id.create_donatur) {
            dialogCreate_donatur();

        } else if (id == R.id.create_akun) {
            dialogPilih_akun();

        } else if (id == R.id.list_donatur) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
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
                        Intent intent = new Intent(MainActivity.this, Dashboard_activity.class);
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

    /////////////////////// DIALOG ///////////////////
    private void dialogAboutUs() {
        dialogs = new AlertDialog.Builder(MainActivity.this);
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

//        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            btn_edit.setVisibility(View.GONE);
//        }else{
//            btn_edit.setVisibility(View.VISIBLE);
//        }

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

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        final String formattedDate = df.format(c);

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                if (txt_catatan.getText().toString().equals("")){
//                    Toast.makeText(getApplicationContext(), "Catatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
//                }else {
//                    request_post("CALL", String.valueOf(spinner.getSelectedItem()), txt_nama.getText().toString(),donatur_list.get(pos).getEmail(), donatur_list.get(pos).getAlamat(), donatur_list.get(pos).getJenis_kelamin()
//                    , donatur_list.get(pos).getNo_hp(), txt_catatan.getText().toString(), "1", url, user.get(SessionManager.KEY_NAMA), "1");
//                }
                createCall(txt_nama.getText().toString(), donatur_list.get(pos).getEmail(),String.valueOf(spinner.getSelectedItem()),
                        donatur_list.get(pos).getAlamat(),txt_noHp.getText().toString(), "CALL",String.valueOf(spinner.getSelectedItem()),
                        txt_catatan.getText().toString(), "1", formattedDate, "");

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

        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        final String formattedDate = df.format(c);

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                if (txt_catatan.getText().toString().equals("")){
//                    Toast.makeText(getApplicationContext(), "Catatan tidak boleh kosong", Toast.LENGTH_SHORT).show();
//                }else {
//                    request_post("VISIT", String.valueOf(spinner.getSelectedItem()), txt_nama.getText().toString(),donatur_list.get(pos).getEmail(), donatur_list.get(pos).getAlamat(), donatur_list.get(pos).getJenis_kelamin()
//                            , donatur_list.get(pos).getNo_hp(), txt_catatan.getText().toString(), "2", url, user.get(SessionManager.KEY_NAMA), "2");
//                }
//
//            }
//        });

                createCall(txt_nama.getText().toString(), donatur_list.get(pos).getEmail(),String.valueOf(spinner.getSelectedItem()),
                        donatur_list.get(pos).getAlamat(),txt_noHp.getText().toString(), "VISIT",String.valueOf(spinner.getSelectedItem()),
                        txt_catatan.getText().toString(), "2", formattedDate, "");

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

        final String url = Api_url.URL_CHECK_STATUS;
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner1);
        final Spinner spinner1 = (Spinner) dialogView.findViewById(R.id.spinner2);
        final TextView txt_akun_bnk    = (TextView) dialogView.findViewById(R.id.tv_akun_bank);

        ShowSelectedImage = (ImageView)dialogView.findViewById(R.id.imageView);
        ShowSelectedImage.setVisibility(View.GONE);

        byteArrayOutputStream = new ByteArrayOutputStream();

        GetImageFromGalleryButton = (Button)dialogView.findViewById(R.id.buttonSelect);

        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        ShowSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_imageFull();
            }
        });

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

        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        final String formattedDate = df.format(c);

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_nominal.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if (txt_tgl_pembayaran.getText().toString().equals("") || ShowSelectedImage.getVisibility() == View.GONE){
                    Toast.makeText(getApplicationContext(), "Tanggal pembayaran tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else if (ShowSelectedImage.getVisibility() == View.GONE){
                    Toast.makeText(getApplicationContext(), "Foto Bukti transfer/Cash tidak boleh kosong ", Toast.LENGTH_SHORT).show();
                }else {
                    if (String.valueOf(spinner.getSelectedItem()).equals("Cash")){
                        createClosing(donatur_list.get(pos).getName(), donatur_list.get(pos).getEmail(),"",
                                donatur_list.get(pos).getAlamat(),donatur_list.get(pos).getNo_hp(), "CLOSING","",
                                "", "3", formattedDate, "", String.valueOf(spinner.getSelectedItem()),"-",txt_nominal.getText().toString(),
                                txt_tgl_pembayaran.getText().toString(), imageFilePath);
                    }else {
                        createClosing(donatur_list.get(pos).getName(), donatur_list.get(pos).getEmail(),"",
                                donatur_list.get(pos).getAlamat(),donatur_list.get(pos).getNo_hp(), "CLOSING","",
                                "", "3", formattedDate, "", String.valueOf(spinner.getSelectedItem()),String.valueOf(spinner1.getSelectedItem()),txt_nominal.getText().toString(),
                                txt_tgl_pembayaran.getText().toString(), imageFilePath);

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

    private void dialog_imageFull() {
        dialogs = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.full_display, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        ImageView img_full = (ImageView) dialogView.findViewById(R.id.img_full);
        img_full.setImageBitmap(FixBitmap);

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
//                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") || tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
//                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
//                }else {
//                    request_Edit_donatur(txt_nama.getText().toString(),txt_email.getText().toString(), txt_alamat.getText().toString(),
//                            spinner.getSelectedItem().toString(),tgl_lahir.getText().toString(),txt_noHp.getText().toString(),url, donatur_list.get(pos).getId());
//                }

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
//                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") || tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
//                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
//                }else {
//                    request_Edit_donatur(txt_nama.getText().toString(),txt_email.getText().toString(), txt_alamat.getText().toString(),
//                            spinner.getSelectedItem().toString(),tgl_lahir.getText().toString(),txt_noHp.getText().toString(),url, "");
//                }
                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") ||
                        tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    createNote(txt_nama.getText().toString(),txt_email.getText().toString(), String.valueOf(spinner.getSelectedItem()), txt_alamat.getText().toString(), txt_noHp.getText().toString(), tgl_lahir.getText().toString());
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
                dialogCreate_akun("3");
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
//        ln_cabang.setVisibility(View.VISIBLE);
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

        final String finalType1 = type;
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_nama.getText().toString().equals("") || txt_alamat.getText().toString().equals("") || txt_email.getText().toString().equals("") ||
                        tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    createSales(txt_nama.getText().toString(), finalType1,txt_email.getText().toString(), txt_password.getText().toString(),String.valueOf(spinner.getSelectedItem()), txt_alamat.getText().toString(), txt_noHp.getText().toString(), tgl_lahir.getText().toString());
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

    ////////////////////// DIALOG ////////////////////////////


    /////////////////// CAMERA //////////////////////

    private void showPictureDialog(){
        android.support.v7.app.AlertDialog.Builder pictureDialog = new android.support.v7.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                                }else {
                                    openCameraIntent();
                                }
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCameraIntent();
            }
        }
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.user.salestracking.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_CAPTURE_IMAGE &&
                resultCode == RESULT_OK) {
            File imgFile = new  File(imageFilePath);
            if(imgFile.exists()){
                FixBitmap = BitmapFactory.decodeFile(imageFilePath);
                ShowSelectedImage.setVisibility(View.VISIBLE);
                ShowSelectedImage.setImageBitmap(FixBitmap);
            }

        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                if (contentURI != null) {
                    imageFilePath = getRealPathFromURI(contentURI);
                }
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    ShowSelectedImage.setVisibility(View.VISIBLE);
                    ShowSelectedImage.setImageBitmap(FixBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    //////////////////// CAMERA ////////////////////

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

    private void createNote(String name, String email, String jk, String alamat, String no_hp, String tgl_lahir) {
        long id = db.insertDonatur(name, email, jk, alamat, no_hp, tgl_lahir);

        // get the newly inserted note from db
        DataDonatur n = db.getDonatur(id);

        if (n != null) {
            // adding new note to array list at 0 position
            donaturList.add(0, n);

            // refreshing the list
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage("success")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), Dashboard_activity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private void createSales(String name, String type, String email,String Password, String jk, String alamat, String no_hp, String tgl_lahir) {
        long id = db_sales.insertDonatur(name, type, email,Password, jk, alamat, no_hp, tgl_lahir);

        // get the newly inserted note from db
        DataSales n = db_sales.getDonatur(id);

        if (n != null) {
            // adding new note to array list at 0 position
            salesList.add(0, n);

            // refreshing the list
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage("success")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), Dashboard_activity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private void createCall(String name, String email, String jenis_kelamin, String alamat, String no_hp, String aktivitas, String hasil_aktivitas, String catatan
            , String type_aktivitas, String date_record, String assign_by) {
        long id = db_list.insertDonatur(name, email, jenis_kelamin, alamat, no_hp, aktivitas, hasil_aktivitas, catatan, type_aktivitas, date_record, assign_by);

        // get the newly inserted note from db
        DataListCall n = db_list.getDonatur(id);

        if (n != null) {
            // adding new note to array list at 0 position
            databaseLists.add(0, n);

            // refreshing the list
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage("success")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), Dashboard_activity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private void createClosing(String name, String email, String jenis_kelamin, String alamat, String no_hp, String aktivitas, String hasil_aktivitas, String catatan
            , String type_aktivitas, String date_record, String assign_by, String type_transfer, String akun_bank, String nominal, String tanggal_transfer, String url_image) {
        long id = db_closing.insertDonatur(name, email, jenis_kelamin, alamat, no_hp, aktivitas, hasil_aktivitas, catatan, type_aktivitas, date_record, assign_by, type_transfer,
                akun_bank, nominal, tanggal_transfer, url_image);

        // get the newly inserted note from db
        DataListClosing n = db_closing.getDonatur(id);

        if (n != null) {
            // adding new note to array list at 0 position
            databaseClosings.add(0, n);

            // refreshing the list
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage("success")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), Dashboard_activity.class);
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


    @Override
    public void onBackPressed() {
        mediaPlayer.start();
        popup_close();
    }
}
