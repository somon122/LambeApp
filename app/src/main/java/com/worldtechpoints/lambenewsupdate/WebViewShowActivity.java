package com.worldtechpoints.lambenewsupdate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewShowActivity extends AppCompatActivity {

    private WebView webView;
    private String targetUrl;
    private TextView networkAlert;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_show);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        networkAlert = findViewById(R.id.networkAlert_id);
        networkAlert.setVisibility(View.GONE);
        webView = findViewById(R.id.webView_id);


        if (HaveNetwork()){

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                targetUrl = bundle.getString("webUrl");
            } else {
                Toast.makeText(this, "Data is Empty", Toast.LENGTH_SHORT).show();
            }

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(targetUrl);
        }else {
            webView.setVisibility(View.GONE);
            networkAlert.setVisibility(View.VISIBLE);
            rulesAlert();


        }

        networkAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rulesAlert();
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private boolean HaveNetwork() {

        boolean have_WiFi = false;
        boolean have_Mobile = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfo) {

            if (info.getTypeName().equalsIgnoreCase("WIFI")) {
                if (!info.isConnectedOrConnecting()) {
                    have_WiFi = true;
                }
            }
            if (info.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (!info.isConnectedOrConnecting()) {
                    have_Mobile = true;
                }
            }

        }
        return have_WiFi || have_Mobile;


    }

    private void rulesAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewShowActivity.this);

        builder.setMessage("Please Connect your Internet first..!\n Then Try again!)")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(WebViewShowActivity.this,WebViewShowActivity.class));

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();


    }


}