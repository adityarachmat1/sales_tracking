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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView dessTv;
    private SessionManager session;
    private HashMap<String, String> user;
    private ListView listView;
    List<DataDonatur> donatur_list;
    AlertDialog.Builder dialogs;
    private ProgressDialog dialog;
    LayoutInflater inflater;
    EditText txt_nama, txt_email, txt_alamat, txt_jk, txt_noHp, txt_catatan;
    TextView txt_tgl_pembayaran;
    Button btn_call, btn_visit, btn_closing, btn_save;
    View dialogView;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView txtName, txtCabang;
    private Toolbar toolbar;

    public static int navItemIndex = 0;

    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();
        dialog = new ProgressDialog(MainActivity.this);
        donatur_list = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtCabang = (TextView) navHeader.findViewById(R.id.website);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        setUpNavigationView();
        listView = (ListView) findViewById(R.id.list_view);

        dessTv = (TextView) findViewById(R.id.desTv);

        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();

        txtName.setText(user.get(SessionManager.KEY_NAMA));
        txtCabang.setText(user.get(SessionManager.KEY_CABANG));

        if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("0")){
            dessTv.setText("Sales Marketing");
        }else if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("1")){
            dessTv.setText("Admin");
        }else if (user.get(SessionManager.KEY_TYPE_ACCOUNT).equals("2")){
            dessTv.setText("Donatur");
        }else {
            dessTv.setText("Owner");
        }
        request();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                dialogFormDetail(position);
            }
        });

    }


    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.list_call:
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                        break;
                    case R.id.list_visit:

                        break;
                    case R.id.list_closing:

                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment

                    case R.id.create_donatur:

                        break;
                    case R.id.list_donatur:
                        // launch new intent instead of loading fragment
                        break;
                    case R.id.logout:
                        popup("Anda Yakin ingin keluar dari aplikasi?");
                        return true;
                    default:
                        navItemIndex = 0;
                }

                return true;
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.list_call:
                    toolbar.setTitle("LIST CALL");
                    return true;
                case R.id.list_visit:
                    toolbar.setTitle("LIST VISIT");
                    return true;
                case R.id.list_closing:
                    toolbar.setTitle("LIST CLOSING");
                    return true;
            }

            return false;
        }
    };



    private void popup(String message){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
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

                    donatur_list.add(new DataDonatur(jsonObject.getString("nama"),
                            jsonObject.getString("email"),
                            jsonObject.getString("jenis_kelamin"),
                            jsonObject.getString("alamat"),
                            jsonObject.getString("nomor_hanphone")));
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

        txt_nama    = (EditText) dialogView.findViewById(R.id.txt_nama);
        txt_email  = (EditText) dialogView.findViewById(R.id.txt_email);
        txt_noHp  = (EditText) dialogView.findViewById(R.id.txt_noHp);
        txt_jk    = (EditText) dialogView.findViewById(R.id.txt_JK);
        txt_alamat  = (EditText) dialogView.findViewById(R.id.txt_alamat);

        txt_nama.setText("Nama : "+donatur_list.get(pos).getName());
        txt_email.setText("Email : "+donatur_list.get(pos).getEmail());
        txt_noHp.setText("No Handphone : "+donatur_list.get(pos).getNo_hp());
        txt_jk.setText("Jenis Kelamin : "+donatur_list.get(pos).getJenis_kelamin());
        txt_alamat.setText("Alamat : "+donatur_list.get(pos).getAlamat());

        btn_call = (Button) dialogView.findViewById(R.id.btn_call);
        btn_visit = (Button) dialogView.findViewById(R.id.btn_visit);
        btn_closing = (Button) dialogView.findViewById(R.id.btn_closing);

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
                    , donatur_list.get(pos).getNo_hp(), txt_catatan.getText().toString(), "1", url, txt_nama.getText().toString());
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
                            , donatur_list.get(pos).getNo_hp(), txt_catatan.getText().toString(), "2", url, txt_nama.getText().toString());
                }

            }
        });

        dialogs.show();
    }

    private void dialogClosing(int pos) {
        dialogs = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_closing, null);
        dialogs.setView(dialogView);
        dialogs.setCancelable(true);

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

        Log.d("result", String.valueOf(spinner.getSelectedItem()));

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

        btn_save = (Button) dialogView.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (txt_tgl_pembayaran.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Tanggal pembayaran tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "BTN CALL 3", Toast.LENGTH_SHORT).show();
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
                .setCancelText("No");
        cdp.show((getSupportFragmentManager()),"Promise Date");
    }

    private void request_post(final String aktivitas, final String hasil_aktivitas, final String nama, final String email, final String alamat, final String jenis_kelamin,
                              final String noHp, final String catatan, final String type, final String url, final String assign) {
        class insert_call extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy hh:mm");
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
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        insert_call ru = new insert_call();
        ru.execute();
    }
}
