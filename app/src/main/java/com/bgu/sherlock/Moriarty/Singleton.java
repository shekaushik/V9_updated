package com.bgu.sherlock.Moriarty;

public class Singleton {
    private static Singleton mInstance = null;

    public boolean mBool;
    public boolean sHome;
    public boolean minimize;
    public boolean shouldAccess = false;
    public boolean haveAccess = false;

    public String clickJacking;
    private Singleton(){
        mBool = false;
        sHome = false;
        minimize = false;


    }

    public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();

        }
        return mInstance;



    }

}