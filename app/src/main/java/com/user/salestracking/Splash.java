package com.user.salestracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity{
    private Timer timer;
    private ProgressBar progressBar;
    private int i=0;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new SessionManager(getApplicationContext());
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        final long period = 30;
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                if (i<100){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    progressBar.setProgress(i);
                    i++;
                }else{
                    //closing the timer
                    timer.cancel();
                    if (session.isLoggedIn()){
                        Intent intent =new Intent(Splash.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent =new Intent(Splash.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 0, period);
    }

}
