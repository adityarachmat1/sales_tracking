package com.user.salestracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.user.salestracking.Api_Service.Api_url;
import com.user.salestracking.Api_Service.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText et_email, et_password;
    Button btn_login;
    private ProgressDialog dialog;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        session = new SessionManager(getApplicationContext());
        dialog = new ProgressDialog(LoginActivity.this);
        et_email = (EditText) findViewById(R.id.userid);
        et_password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btnLogin);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (et_email.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Email Kosong", Toast.LENGTH_SHORT).show();
                }else if (et_password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Password Kosong", Toast.LENGTH_SHORT).show();
                }else {
                    request();
                }

            }
        });
    }

    private void request() {
        class RegisterUser extends AsyncTask<Void, Void, String> {


            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("email", et_email.getText().toString());
                params.put("password", et_password.getText().toString());

                //returing the response
                return requestHandler.sendPostRequest(Api_url.URL_LOGIN, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                dialog.setMessage("please wait...");
                dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                dialog.dismiss();

                try {
                    JSONObject obj = new JSONObject(s);
                    if (obj.getInt("status") == 0) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject userJson = obj.getJSONObject("user");
                        int id = userJson.getInt("id");
                        String cabang = userJson.getString("cabang");
                        String email = userJson.getString("email");
                        String jenis_kelamin = userJson.getString("jenis_kelamin");
                        String type_account = userJson.getString("type_account");
                        String nama = userJson.getString("nama");
                        String alamat = userJson.getString("alamat");
                        String tgl_lahir = userJson.getString("tgl_lahir");
                        String nomor_hanphone = userJson.getString("nomor_hanphone");

                        session.createLoginSession(String.valueOf(id), email,cabang, jenis_kelamin, nama, type_account, alamat, tgl_lahir, nomor_hanphone);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute();

    }

}
