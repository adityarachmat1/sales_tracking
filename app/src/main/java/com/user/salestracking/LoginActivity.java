package com.user.salestracking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.user.salestracking.Api_Service.Api_url;
import com.user.salestracking.Api_Service.RequestHandler;
import com.user.salestracking.db.DatabaseHelperAkun;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    EditText et_email, et_password;
    Button btn_login, btn_register;
    private ProgressDialog dialog;
    SessionManager session;
    private DatabaseHelperAkun db_sales;
    List<DataSales> sales_list = new ArrayList<>();
    View dialogView;
    AlertDialog.Builder dialogs;
    LayoutInflater inflater;
    TextView txt_nama, txt_email, txt_alamat, txt_jk, txt_noHp;
    EditText txt_catatan, txt_nominal;
    TextView txt_tgl_pembayaran, tgl_lahir;
    Button btn_call, btn_visit, btn_closing, btn_save, btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        db_sales = new DatabaseHelperAkun(this);

        if (db_sales.getAllDonatur() != null){
            sales_list.addAll(db_sales.getAllDonatur());
        }

        session = new SessionManager(getApplicationContext());
        dialog = new ProgressDialog(LoginActivity.this);
        et_email = (EditText) findViewById(R.id.userid);
        et_password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btnLogin);
        btn_register = (Button) findViewById(R.id.btnRegistrasi);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                    if (et_email.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Email Kosong", Toast.LENGTH_SHORT).show();
                    } else if (et_password.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Password Kosong", Toast.LENGTH_SHORT).show();
                    } else {
                        Boolean isAvailable = false;
                            for (int i = 0; i < sales_list.size(); i++){
                                if (sales_list.get(i).getEmail().toUpperCase().equals(et_email.getText().toString().toUpperCase()) && sales_list.get(i).getPassword().equals(et_password.getText().toString().toUpperCase())){
                                    session.createLoginSession(String.valueOf(1),sales_list.get(i).getEmail(), sales_list.get(i).getName(), sales_list.get(i).getType_akun());
                                    isAvailable = true;
                                }
                            }
                            if (isAvailable){
                                Intent intent = new Intent(getApplicationContext(), Dashboard_activity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                    }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogPilih_akun();
            }
        });
    }

    private void dialogPilih_akun() {
        dialogs = new AlertDialog.Builder(LoginActivity.this);
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
        dialogs = new AlertDialog.Builder(LoginActivity.this);
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
                        tgl_lahir.getText().toString().equals("") || txt_noHp.getText().toString().equals("")|| txt_password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "field tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }else {
                    createSales(txt_nama.getText().toString(), finalType1,txt_email.getText().toString(), txt_password.getText().toString(),String.valueOf(spinner.getSelectedItem()), txt_alamat.getText().toString(), txt_noHp.getText().toString(), tgl_lahir.getText().toString());
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

    private void createSales(String name, String type, String email,String Password, String jk, String alamat, String no_hp, String tgl_lahir) {
        long id = db_sales.insertDonatur(name, type, email,Password, jk, alamat, no_hp, tgl_lahir);

        // get the newly inserted note from db
        DataSales n = db_sales.getDonatur(id);

        if (n != null) {
            // adding new note to array list at 0 position
            sales_list.add(0, n);

            // refreshing the list
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setMessage("success")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }
}
