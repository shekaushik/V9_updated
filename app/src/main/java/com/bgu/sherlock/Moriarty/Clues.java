package com.bgu.sherlock.Moriarty;

//import android.content.Context;
import android.os.Environment;
//import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;


/**
 * Created by simondzn on 26/11/2015.
 */
public class Clues {
//    String extend;
    FileWriter logger;
    JsonObject data;
//    JsonArray array;
    File clueFile;

//    public Clues(String TAG, String extend) {

    public void SendMal(String action, String details, String behavior,String actionType) {
        File StorageDir = getDir();
        clueFile = new File(StorageDir, "MoriartyClues.json");
        data = new JsonObject();
        data.addProperty("Action", action);
        data.addProperty("ActionType", actionType);
        data.addProperty("Behavior", behavior);
        data.addProperty("Details", details);
        data.addProperty("UUID", System.currentTimeMillis());
        data.addProperty("SessionType", "malicious");
//        Context context = MyApp.getContext();
//        MyApp myApp = (MyApp) context.getApplicationContext();
        data.addProperty("Version", "9.1");
        data.addProperty("SessionID", MyApp.sessionId);
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        try {
            logger = new FileWriter(clueFile, true);
            if (clueFile.length() > 0) {
                logger.write("," + jsonData);
            } else {
                logger.write(data.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public void SendBenign(String action, String details) {
//        File StorageDir = getDir();
//        clueFile = new File(StorageDir, "MoriartyClues.json");
//        data = new JsonObject();
//        data.addProperty("Action", action);
//        data.addProperty("ActionType", "benign");
//        data.addProperty("Details", details);
//        data.addProperty("Behavior", "benign");
//        data.addProperty("UUID", System.currentTimeMillis());
//        data.addProperty("SessionType", MyApp.sessionType);
//        data.addProperty("Version", "8.0");
//        data.addProperty("SessionID", MyApp.sessionId);
//        try {
//            logger = new FileWriter(clueFile, true);
//            if (clueFile.length() > 0) {
//                logger.write("," + data.toString());
//                logger.close();
//            } else {
//                logger.write(data.toString());
//                logger.close();
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

//    public void SendMal(String action, String details, String behavior, int sessionId) {
//        File StorageDir = getDir();
//        clueFile = new File(StorageDir, "MoriartyClues.json");
//        data = new JsonObject();
//        data.addProperty("Action", action);
//        data.addProperty("ActionType", "malicious");
//        data.addProperty("Behavior", behavior);
//        data.addProperty("Details", details);
//        data.addProperty("UUID", System.currentTimeMillis());
//        data.addProperty("SessionType", "malicious");
//        Context context = MyApp.getContext();
//        data.addProperty("Version", "8.0");
//        data.addProperty("SessionID", sessionId);
//        try (FileWriter logger = new FileWriter(clueFile, true)){
//            if (clueFile.length() > 0) {
//                logger.write("," + data.toString());
//                logger.close();
//            } else {
//                logger.write(data.toString());
//                logger.close();
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
    public void updateLastEntrance() {
        File storageDir = getDir();
        File clueFile = new File(storageDir, "lastUp.txt");
        try {
            logger = new FileWriter(clueFile, true);
            long timestamp = System.currentTimeMillis();
            logger.write(timestamp +" \"");
            logger.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getDir() {
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File(sdCard.getAbsolutePath() + "/" + "Moriarty");
        file.mkdirs();
        return file;
    }
}
