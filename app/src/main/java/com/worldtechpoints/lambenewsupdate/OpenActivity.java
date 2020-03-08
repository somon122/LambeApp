package com.worldtechpoints.lambenewsupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

public class OpenActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        progressBar = findViewById(R.id.progressBar);



        if (HaveNetwork()){

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    doTheWork();
                    startApp();
                }
            });
            thread.start();

        }else {

            Toast.makeText(this, "Please connect your Internet first", Toast.LENGTH_SHORT).show();

        }

    }

    private void startApp() {
        startActivity(new Intent(OpenActivity.this,HomeActivity.class));
        finish();
    }

    private void doTheWork() {

        for (progress = 20; progress <= 100; progress = progress+20){
            try {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    private boolean HaveNetwork() {
        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfo){

            if (info.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (info.isConnected())
                {
                    have_WiFi = true;
                }
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))

            {
                if (info.isConnected())
                {
                    have_Mobile = true;
                }
            }

        }
        return have_WiFi || have_Mobile;

    }

}
