package com.worldtechpoints.lambenewsupdate.LogIn;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveDailyCheck {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SaveDailyCheck(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("SaveDate",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void dataStore (String date){

        editor.putString("date",date);
        editor.commit();

    }

    public String getdate (){

        String date = sharedPreferences.getString("date","");
        return date;
    }

    public void delete (){
        editor.clear();
        editor.commit();

    }


}
