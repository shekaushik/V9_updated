package com.bgu.sherlock.Moriarty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiCheck extends BroadcastReceiver {
    public WifiCheck() {
    }
Clues clue = new Clues();
    @Override
    public void onReceive(Context context, Intent intent) {
        //MyApp myApp = (MyApp)context.getApplicationContext();

            if (isConnectedToWifi(context)) {
                //clue.SendMal("Probing for WiFi access", "Response: Wifi available", "malicious", "malicious");

            }

            Intent i = new Intent(context.getApplicationContext(), this.getClass());
            AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getBroadcast(context.getApplicationContext(), 205, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR, pi);

    }

    public boolean isConnectedToWifi(Context context) {
        //clue.SendMal("Probing for WiFi access", "Wifi connection is pending", "malicious", "malicious");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null){
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected()) {
            return true;
        }
        }else {
            //clue.SendMal("Probing for WiFi access", "Response: No wifi connection", "malicious", "malicious");
            return false;
        }
        return false;
    }
}