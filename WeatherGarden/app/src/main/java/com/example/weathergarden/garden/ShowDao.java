package com.example.weathergarden.garden;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class ShowDao {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Gson gson;

    public ShowDao(Context context){
        preferences = context.getSharedPreferences("show_data", Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
    }

    public ShowInfo getShowInfo(){
        String showString = preferences.getString("show_data","");
        if(showString == "") {
            ShowInfo showInfo = new ShowInfo();
            showInfo.init();
            setShowInfo(showInfo);
            return showInfo;
        }
        return gson.fromJson(showString, ShowInfo.class);
    }

    public void setShowInfo(ShowInfo showInfo){
        String showString = gson.toJson(showInfo);
        editor.putString("show_data", showString);
        editor.apply();
    }
}
