package com.worldtechpoints.lambenewsupdate;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.worldtechpoints.lambenewsupdate.AdminPanel.AdminPanelActivity;
import com.worldtechpoints.lambenewsupdate.AdminPanel.WithdrawActivity;
import com.worldtechpoints.lambenewsupdate.LogIn.LoginActivity;
import com.worldtechpoints.lambenewsupdate.LogIn.SaveDailyCheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {


    DrawerLayout drawer;
    NavigationView navigationView;


    private TextView goalButton, lindaikejisblogButton, nairaIlandButton,
            lagosCityButton,naijaLoadedButton,bellaNaijaButton,youtubeButton,netadvocButton,instagramButton,live_scoreButton;

    private String goal = "https://www.goal.com";
    private String lindaikejisblog = "https://www.lindaikejisblog.com";
    private String nairaIland = "https://www.nairaland.com";
    private String lagosCity = "https://lagoscityreporters.com";
    private String naijaLoaded  = "https://www.naijaloaded.com.ng";
    private String bellaNaija = "https://www.bellanaija.com";

    private String netadvoc = "https://netadvoc.com.ng";
    private String youtube = "https://www.youtube.com";
    private String instagram = "https://www.instagram.com";
    private String live_score = "http://www.livescores.com";

    private ViewFlipper viewFlipper;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private String mainInterstitialAd = "ca-app-pub-3940256099942544/1033173712";
    //private String mainInterstitialAd = "ca-app-pub-7888587621635916/1826625368";

    private String url;


    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private int lastPoint;

    String date;
    String oldDate;
    SaveDailyCheck saveDailyCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


         drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        int images [] = {R.drawable.naigiria,R.drawable.messi1,R.drawable.nigaria,R.drawable.massi2};
        viewFlipper = findViewById(R.id.viewFlipper_id);
        goalButton = findViewById(R.id.goal_id);
        lindaikejisblogButton = findViewById(R.id.lindaikejisblog_id);
        nairaIlandButton = findViewById(R.id.nairaIland_id);
        lagosCityButton = findViewById(R.id.lagosCity_id);
        naijaLoadedButton = findViewById(R.id.naijaloaded_id);
        bellaNaijaButton = findViewById(R.id.bellanaija_id);

        youtubeButton = findViewById(R.id.youtube_id);
         netadvocButton = findViewById(R.id.netadvoc_id);
        live_scoreButton = findViewById(R.id.live_score_id);
        instagramButton = findViewById(R.id.instagram_id);

        MobileAds.initialize(this,getString(R.string.test_AppUnitId));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(mainInterstitialAd);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null){
            retrivePoint( user.getDisplayName());
        }

        saveDailyCheck = new SaveDailyCheck(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        date = sdf.format(new Date());
        oldDate = saveDailyCheck.getdate();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){

            dailyPointAdd();
        }





        for (int image : images){

            flipperImages(image);
        }



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();


                if (id == R.id.nav_home){

                    startActivity(new Intent(HomeActivity.this,HomeActivity.class));
                    finish();

                }else if (id == R.id.nav_adminPanel){

                   adminPanel("123456");


                }else if (id == R.id.nav_wallet){

                    startActivity(new Intent(HomeActivity.this, WithdrawActivity.class));


                }else if (id == R.id.nav_privacy){
                    Toast.makeText(HomeActivity.this, "nav_privacy", Toast.LENGTH_SHORT).show();

                }else if (id == R.id.nav_share){

                    shareApp();

                }else if (id == R.id.nav_login){

                    if (FirebaseAuth.getInstance().getCurrentUser() == null){
                        loginAlert();
                    }else {
                        Toast.makeText(HomeActivity.this, "You have already login.", Toast.LENGTH_SHORT).show();
                    }


                }else if (id == R.id.nav_send){

                    Toast.makeText(HomeActivity.this, "nav_send", Toast.LENGTH_SHORT).show();

                }else if (id == R.id.nav_exits){

                    exitsAlert();

                }else {
                    Toast.makeText(HomeActivity.this, "No match", Toast.LENGTH_SHORT).show();
                }



                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);


                return false;

            }
        });

        goalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mInterstitialAd.isLoaded()){
                    url = goal;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(goal);
                }

            }
        });

        lindaikejisblogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mInterstitialAd.isLoaded()){
                    url = lindaikejisblog;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(lindaikejisblog);
                }



            }
        });

        nairaIlandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mInterstitialAd.isLoaded()){
                    url = nairaIland;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(nairaIland);
                }

            }
        });


        lagosCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    url = lagosCity;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(lagosCity);
                }

            }
        });

        naijaLoadedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mInterstitialAd.isLoaded()){
                    url = naijaLoaded;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(naijaLoaded);
                }


            }
        });

        bellaNaijaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    url = bellaNaija;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(bellaNaija);
                }

            }
        });


        netadvocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    url = netadvoc;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(netadvoc);
                }

            }
        });
        youtubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    url = youtube;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(youtube);
                }

            }
        });
        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    url = instagram;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(instagram);
                }

            }
        });
        live_scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()){
                    url = live_score;
                    mInterstitialAd.show();
                }else {
                    sentWebsiteUrl(live_score);
                }

            }
        });



        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {


            }

            @Override
            public void onAdClosed() {

                userPointAdd();


            }
        });

    }

    private void dailyPointAdd() {

        if (!date.equals(oldDate)){

            int newPoint = lastPoint+100;
            myRef.child("MainPoint").child(user.getDisplayName()).setValue(String.valueOf(newPoint))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                saveDailyCheck.dataStore(date);
                                Toast.makeText(HomeActivity.this, "Point added Success", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(HomeActivity.this, "point Added field", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, "point Added field", Toast.LENGTH_SHORT).show();

                }
            });

        }else {

            Toast.makeText(this, "Date match", Toast.LENGTH_SHORT).show();
        }



    }


    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){

            if (!date.equals(oldDate)){
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                finish();

            }

        }else {
            if (!date.equals(oldDate)){
                loginAlert();
            }

        }




    }

    private void loginAlert() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Login alert!")
                .setMessage("Are you want to Login for earn money?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                        finish();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                saveDailyCheck.dataStore(date);
                dialog.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void logoutAlert() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Logout alert!")
                .setMessage("Are you sure to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(HomeActivity.this, "Log out success", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


 private void exitAlert() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Exits alert!")
                .setMessage("Are you sure to Exits?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       finishAffinity();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }




    private void shareApp() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBody = "App link : " + "https://play.google.com/store/apps/details?id=" + getPackageName();
        String shareSub = "Android App";
        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Lambe News Update"));

    }

    private void userPointAdd() {

        int newPoint = lastPoint +5;


        myRef.child("MainPoint").child(user.getDisplayName()).setValue(String.valueOf(newPoint)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Intent intent = new Intent(HomeActivity.this,WebViewShowActivity.class);
                    intent.putExtra("webUrl",url);
                    startActivity(intent);

                }else {

                    Intent intent = new Intent(HomeActivity.this,WebViewShowActivity.class);
                    intent.putExtra("webUrl",url);
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Intent intent = new Intent(HomeActivity.this,WebViewShowActivity.class);
                intent.putExtra("webUrl",url);
                startActivity(intent);

            }
        });

    }

    private void sentWebsiteUrl(String url){

        if (HaveNetwork()){

            Intent intent = new Intent(HomeActivity.this,WebViewShowActivity.class);
            intent.putExtra("webUrl",url);
            startActivity(intent);

        }else {
            rulesAlert();
        }


    }

    private void flipperImages(int image){

        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);

        viewFlipper.setInAnimation(this,android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(this,android.R.anim.slide_out_right);


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

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

        builder.setMessage("Please Connect your Internet first..!\n Then Try again!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(HomeActivity.this,HomeActivity.class));

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout){

            if (FirebaseAuth.getInstance().getCurrentUser() != null){

                logoutAlert();
            }else {
               loginAlert();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            exitsAlert();
        } else {
            exitsAlert();
        }

    }

    private void exitsAlert() {

       exitAlert();
    }

    private void retrivePoint(String refer){

        myRef.child("MainPoint").child(refer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    String userPoint = dataSnapshot.getValue(String.class);

                    lastPoint = Integer.parseInt(userPoint);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void adminPanel(final String password) {


        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view1 = getLayoutInflater().inflate(R.layout.admin_control, null);


        final EditText passwordET = view1.findViewById(R.id.adminCheckPassword_id);
        Button submit = view1.findViewById(R.id.adminSubmit_id);


        builder.setTitle("Admin Panel");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mPassword = passwordET.getText().toString();

                if (mPassword.isEmpty()) {

                    passwordET.setError("Please enter password");

                } else {

                    if (mPassword.equals(password)) {

                        Toast.makeText(HomeActivity.this, "Password is matches", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomeActivity.this, AdminPanelActivity.class));


                    } else {

                        Toast.makeText(HomeActivity.this, "Password is not matches", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();


    }



}
