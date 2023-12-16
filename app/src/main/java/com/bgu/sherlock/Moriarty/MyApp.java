package com.bgu.sherlock.Moriarty;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by simondzn on 07/03/2016.
 */
public class MyApp extends Application {
    private static Context context;
    public static int sessionId;
    public static String sessionType =  "malicious";

    @Override
    public void onCreate() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        super.onCreate();
        MyApp.context = getApplicationContext();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("crypto", Context.MODE_PRIVATE);
        sessionId = sharedPreferences.getInt("session", 0);
//        sharedPreferences.edit().putInt("session", sessionId+1).commit();
//        if ((sessionId+1)%3 == 0)
//            sessionType = "benign";
//        else sessionType = "malicious";
    }

    public static Context getContext(){
        return MyApp.context;
    }
}
