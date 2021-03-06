package com.user.salestracking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.user.salestracking.Api_Service.Api_url;
import com.user.salestracking.Data.DataDonatur;
import com.user.salestracking.Data.DataListClosing;
import com.user.salestracking.Data.DataSales;
import com.user.salestracking.db.DatabaseClosing;
import com.user.salestracking.db.DatabaseHelper;
import com.user.salestracking.db.DatabaseHelperAkun;
import com.user.salestracking.db.SessionManager;
import com.user.salestracking.list_adapter.ClosingAdapter;
import com.user.salestracking.list_adapter.DonaturlistAdapter;
import com.user.salestracking.permission.PermissionsActivity;
import com.user.salestracking.permission.PermissionsChecker;
import com.user.salestracking.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.user.salestracking.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.user.salestracking.permission.PermissionsChecker.REQUIRED_PERMISSION;

public class List_closing extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SessionManager session;
    private HashMap<String, String> user;
    private ListView listView;
    List<DataListClosing> dataListClosing;
    AlertDialog.Builder dialogs;
    private ProgressDialog dialog;
    LayoutInflater inflater;
    TextView txt_nama, txt_email, txt_alamat, txt_jk, txt_noHp, txt_catatan;
    TextView txt_tgl_pembayaran, tgl_lahir;
    Button btn_call, btn_visit, btn_closing, btn_save;
    View dialogView;
    Bitmap FixBitmap;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView txtName, txtCabang, txtType;
    private Toolbar toolbar;

    public static int navItemIndex = 0;

    private String[] activityTitles;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private String[] pay = {
            "Transfer",
            "Cash"
    };
    private MediaPlayer mediaPlayer;

    private DatabaseHelper db;
    private DatabaseClosing db_closing;
    private DonaturlistAdapter adapter;
    private List<DataDonatur> donaturList = new ArrayList<>();
    private Button btn_exportPDF;
    PermissionsChecker checker;
    private DatabaseHelperAkun db_sales;
    private List<DataSales> salesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db_sales = new DatabaseHelperAkun(this);
        db = new DatabaseHelper(this);
        db_closing = new DatabaseClosing(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.audio1);
        setTitle("Sales Tracking");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        btn_exportPDF = (Button) findViewById(R.id.btn_export);
        checker = new PermissionsChecker(this);

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
        dialog = new ProgressDialog(List_closing.this);
        dataListClosing = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtCabang = (TextView) navHeader.findViewById(R.id.website);
        txtType = (TextView) navHeader.findViewById(R.id.type);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        txtTitle.setText("LIST CLOSING");

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
//        request();
        if (db_closing.getAllDonatur() != null){
            dataListClosing.addAll(db_closing.getAllDonatur());
        }

        ClosingAdapter adapter = new ClosingAdapter(this, R.layout.row_data_closing, dataListClosing);
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

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("3")){
            nav_Menu.findItem(R.id.list_sales).setVisible(false);
            nav_Menu.findItem(R.id.create_akun).setVisible(false);
            nav_Menu.findItem(R.id.create_donatur).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_call:
                        Intent intent = new Intent(List_closing.this,List_Call.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.list_visit:
                        Intent intent1 = new Intent(List_closing.this,List_visit.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.list_closing:
                        Intent intent2 = new Intent(List_closing.this,List_closing.class);
                        startActivity(intent2);
                        finish();
                        break;
                }
                return false;
            }
        });

        btn_exportPDF.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
                    PermissionsActivity.startActivityForResult(List_closing.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                } else {
                    if (dataListClosing.size() > 0){
                        createPdf(FileUtils.getAppPath(List_closing.this) + "DataListClosing.pdf");
                    }else {
                        Toast.makeText(List_closing.this, "List Visit Kosong", Toast.LENGTH_SHORT).show();
                    }
                }
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

    private void logout(){
        session.logoutUser();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void dialogPilih_akun() {
        dialogs = new AlertDialog.Builder(List_closing.this);
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

    private void dialogAboutUs() {
        dialogs = new AlertDialog.Builder(List_closing.this);
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

    @SuppressLint("SetTextI18n")
    private void dialogFormDetail(final int pos) {
        dialogs = new AlertDialog.Builder(List_closing.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_detail_closing, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        LinearLayout ln_show_image = (LinearLayout) dialogView.findViewById(R.id.ln_show_image);
        txt_nama    = (TextView) dialogView.findViewById(R.id.tv_name);
        txt_email  = (TextView) dialogView.findViewById(R.id.tv_email);
        txt_alamat  = (TextView) dialogView.findViewById(R.id.tv_alamat);
        TextView tv_noHp = (TextView) dialogView.findViewById(R.id.tv_noHp);
        TextView tv_type_transfer = (TextView) dialogView.findViewById(R.id.tv_type_transfer);
        TextView tv_akun_bank = (TextView) dialogView.findViewById(R.id.tv_akun_bank);
        TextView tv_nominal = (TextView) dialogView.findViewById(R.id.tv_nominal);
        TextView tv_aktivitas = (TextView) dialogView.findViewById(R.id.tv_aktivitas);
        TextView tv_date = (TextView) dialogView.findViewById(R.id.tv_date);
        TextView tv_registeredBy = (TextView) dialogView.findViewById(R.id.tv_registeredBy);
        LinearLayout ln_akun_bank = (LinearLayout) dialogView.findViewById(R.id.ln_akun_bank);

        txt_nama.setText(dataListClosing.get(pos).getName());
        txt_email.setText(dataListClosing.get(pos).getEmail());
        txt_alamat.setText(dataListClosing.get(pos).getAlamat());
        tv_noHp.setText(dataListClosing.get(pos).getNo_hp());
        tv_type_transfer.setText(dataListClosing.get(pos).getType_transfer());
        tv_nominal.setText("Rp. "+dataListClosing.get(pos).getNominal());
        tv_aktivitas.setText(dataListClosing.get(pos).getAktivitas());
        tv_date.setText(dataListClosing.get(pos).getDate_record());
        tv_registeredBy.setText(dataListClosing.get(pos).getAssign_by());

        ln_show_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_imageFull(pos);
            }
        });

        if (dataListClosing.get(pos).getAkun_bank().equals("")){
            ln_akun_bank.setVisibility(View.GONE);
        }else {
            ln_akun_bank.setVisibility(View.VISIBLE);
            tv_akun_bank.setText(dataListClosing.get(pos).getAkun_bank());
        }

        dialogs.show();
    }

    private void dialog_imageFull(int pos) {
        dialogs = new AlertDialog.Builder(List_closing.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.full_display, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

        if (dataListClosing.get(pos).getUrl_image() != null){
            File imgFile = new  File(dataListClosing.get(pos).getUrl_image());

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                ImageView img_full = (ImageView) dialogView.findViewById(R.id.img_full);

                img_full.setImageBitmap(myBitmap);

            }
        }


//        Picasso.with(getApplicationContext()).load(dataListClosing.get(pos).getUrl_image()).into(img_full);

        dialogs.show();
    }

    private void dialogCreate_akun(String type_account) {
        dialogs = new AlertDialog.Builder(List_closing.this);
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


    private void popup_success(String message){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(List_closing.this, MainActivity.class);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {
            startActivity(new Intent(List_closing.this, Dashboard_activity.class));
            drawer.closeDrawers();
            finish();

        }else if (id == R.id.list_sales) {
            startActivity(new Intent(List_closing.this, List_Sales.class));
            drawer.closeDrawers();
            finish();

        }else if (id == R.id.list_call) {
            startActivity(new Intent(List_closing.this, List_Call.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.list_visit) {
            startActivity(new Intent(List_closing.this, List_visit.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.list_closing) {
            startActivity(new Intent(List_closing.this, List_closing.class));
            drawer.closeDrawers();
            finish();

        } else if (id == R.id.nav_about_us) {
            dialogAboutUs();

        } else if (id == R.id.create_donatur) {
            dialogCreate_donatur();

        } else if (id == R.id.create_akun) {
            dialogPilih_akun();


        } else if (id == R.id.list_donatur) {
            startActivity(new Intent(List_closing.this, MainActivity.class));
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
        dialogs = new AlertDialog.Builder(List_closing.this);
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

    public void createPdf(String dest) {

        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            /**
             * Creating Document
             */
            Document document = new Document();

            // Location to save
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
            Rectangle rect = new Rectangle(30, 30, 550, 800);
//            writer.setBoxSize("art", rect);
//            HeaderFooterPageEvent event = new HeaderFooterPageEvent(getApplicationContext());
//            writer.setPageEvent(event);

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Android");
            document.addCreator("Sales Tracking");

            /***
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 18.0f;
            float mValueFontSize = 20.0f;

            /**
             * How to USE FONT....
             */
            BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));


            try {
                Paragraph c = new Paragraph();
                InputStream ims = getAssets().open("izi.jpg");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.scaleToFit(950,85);
                image.setAlignment(Element.ALIGN_CENTER);
                c.add(image);
                document.add(c);
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
                return;
            }

            Font mOrderDetailsTitleFont = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk("LAPORAN AKTIVITAS CLOSING DONATUR", mOrderDetailsTitleFont);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);

            Font mOrderDetailsTitleFontz = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunkz = new Chunk("SALES TRACKING", mOrderDetailsTitleFontz);
            Paragraph mOrderDetailsTitleParagraphz = new Paragraph(mOrderDetailsTitleChunkz);
            mOrderDetailsTitleParagraphz.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraphz);

            String alamat = "Jl. Raya Condet No 54 D-E Batu Ampar Jakarta Timur ";

            Font mOrderDetailsTitleFontzZ = new Font(urName, 20.0f, Font.NORMAL, BaseColor.GRAY);
            Chunk mOrderDetailsTitleChunkzZ = new Chunk(alamat, mOrderDetailsTitleFontzZ);
            Paragraph mOrderDetailsTitleParagraphzZ = new Paragraph(mOrderDetailsTitleChunkzZ);
            mOrderDetailsTitleParagraphzZ.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraphzZ);
//            Date DT = Calendar.getInstance().getTime();
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat dfs = new SimpleDateFormat("MMMM yyyy");
//            final String formattedDates = dfs.format(DT);

            String telp = "Telp:(021)87787325 Fax.(021)87787603";
//
            Font mOrderDetailsTitleFonta = new Font(urName, 20.0f, Font.NORMAL, BaseColor.GRAY);
            Chunk mOrderDetailsTitleChunka = new Chunk(telp, mOrderDetailsTitleFonta);
            Paragraph mOrderDetailsTitleParagrapha = new Paragraph(mOrderDetailsTitleChunka);
            mOrderDetailsTitleParagrapha.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagrapha);


            document.add(new Chunk(lineSeparator));

            document.add(new Chunk("\n"));

            for(int i = 0; i < dataListClosing.size(); i++){

                int o = i + 1;
                Font mOrderIdFontc = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
                Chunk mOrderIdChunkc = new Chunk(""+o+".", mOrderIdFontc);
                Paragraph mOrderIdParagraphc = new Paragraph(mOrderIdChunkc);
                document.add(mOrderIdParagraphc);

                Font mOrderIdFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderIdChunk = new Chunk("Nama Donatur: "+ dataListClosing.get(i).getName(), mOrderIdFont);
                Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
                document.add(mOrderIdParagraph);

                Font mOrderAcNameFonta = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderAcNameChunka = new Chunk("Email: "+ dataListClosing.get(i).getEmail(), mOrderAcNameFonta);
                Paragraph mOrderAcNameParagrapha = new Paragraph(mOrderAcNameChunka);
                document.add(mOrderAcNameParagrapha);

                Font mOrderDateFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderDateChunk = new Chunk("Alamat: "+ dataListClosing.get(i).getAlamat(), mOrderDateFont);
                Paragraph mOrderDateParagraph = new Paragraph(mOrderDateChunk);
                document.add(mOrderDateParagraph);

                Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderDateValueChunk = new Chunk("No. Hp : "+ dataListClosing.get(i).getNo_hp(), mOrderDateValueFont);
                Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
                document.add(mOrderDateValueParagraph);

                Font mOrderAcNameValueFontx = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderAcNameValueChunkx = new Chunk("Type Pembayaran :"+dataListClosing.get(i).getType_transfer(), mOrderAcNameValueFontx);
                Paragraph mOrderAcNameValueParagraphx = new Paragraph(mOrderAcNameValueChunkx);
                document.add(mOrderAcNameValueParagraphx);

                if (dataListClosing.get(i).getAkun_bank() != null) {
                    Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                    Chunk mOrderAcNameValueChunk = new Chunk("Akun BANK :" + dataListClosing.get(i).getAkun_bank(), mOrderAcNameValueFont);
                    Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
                    document.add(mOrderAcNameValueParagraph);
                }

                Font mOrderAcNameValueFonts = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderAcNameValueChunks = new Chunk("Nominal : "+"Rp. "+ dataListClosing.get(i).getNominal(), mOrderAcNameValueFonts);
                Paragraph mOrderAcNameValueParagraphs = new Paragraph(mOrderAcNameValueChunks);
                document.add(mOrderAcNameValueParagraphs);

                Font mOrderIdValueFontz = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                Chunk mOrderIdValueChunkz= new Chunk("Tanggal Transfer: "+ dataListClosing.get(i).getTanggal_transfer(), mOrderIdValueFontz);
                Paragraph mOrderIdValueParagraphz = new Paragraph(mOrderIdValueChunkz);
                document.add(mOrderIdValueParagraphz);

                document.add(new Chunk(lineSeparator));
            }

            Date c = Calendar.getInstance().getTime();

            document.add(new Chunk("\n"));

            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
            final String formattedDate = df.format(c);

            Font mOrderDetailsTitleFonts = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunks = new Chunk("Jakarta, "+formattedDate, mOrderDetailsTitleFonts);
            Paragraph mOrderDetailsTitleParagraphs = new Paragraph(mOrderDetailsTitleChunks);
            mOrderDetailsTitleParagraphs.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderDetailsTitleParagraphs);

            document.add(new Chunk(""));

            Font mOrderDetailsTitleFontss = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunkss = new Chunk("( "+user.get(SessionManager.KEY_NAMA)+" )   ", mOrderDetailsTitleFontss);
            Paragraph mOrderDetailsTitleParagraphss = new Paragraph(mOrderDetailsTitleChunkss);
            mOrderDetailsTitleParagraphss.setAlignment(Element.ALIGN_RIGHT);
            document.add(mOrderDetailsTitleParagraphss);

            document.close();

            FileUtils.openFile(List_closing.this, new File(dest));

        } catch (IOException | DocumentException ie) {
            Log.d("createPdf: Error " , ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(List_closing.this, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            if (dataListClosing.size() > 0){
                createPdf(FileUtils.getAppPath(List_closing.this) + "DataListClosing.pdf");
            }else {
                Toast.makeText(List_closing.this, "List Closing Kosong", Toast.LENGTH_SHORT).show();
            }
            createPdf(FileUtils.getAppPath(List_closing.this) + "DataListClosing.pdf");
        } else {
            Toast.makeText(List_closing.this, "Permission not granted, Try again!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        mediaPlayer.start();
        popup_close();
    }
}
