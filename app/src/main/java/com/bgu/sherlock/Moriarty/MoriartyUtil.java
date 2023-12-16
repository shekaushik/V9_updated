package com.bgu.sherlock.Moriarty;

import javax.crypto.*;
import java.io.*;

/**
 * Created by simondzn on 15/02/2016.
 */
public class MoriartyUtil {


    public static File Encrypter(File input) throws Exception {
        long timestamp = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream(input);
        File outfile;
        String type;
        Clues clue = new Clues();
        if (input.getName().endsWith(".mp4")) {
            outfile = new File(Clues.getDir() + "/encrypt.mp4");
            clue.SendMal("Encrypting Data", "Begin: Encrypting a video", "malicious", "malicious");
            type = "video";
        } else {
            clue.SendMal("Encrypting Data", "Begin: Encrypting a photo", "malicious", "malicious");
            outfile = new File(Clues.getDir() + "/encrypt.jpg");
            type = "photo";
        }

        int read;
        FileOutputStream fos = new FileOutputStream(outfile);
        Cipher encipher = Cipher.getInstance("AES");
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        SecretKey skey = kgen.generateKey();
        //Lgo
        encipher.init(Cipher.ENCRYPT_MODE, skey);
        CipherInputStream cis = new CipherInputStream(fis, encipher);
        while ((read = cis.read()) != -1) {
            fos.write((char) read);
            fos.flush();
        }
        fos.close();
        clue.SendMal("Encrypting Data", "End: Encrypted a " + type + " (duration [ms]);"+(System.currentTimeMillis()-timestamp), "malicious", "malicious");
        return outfile;
    }
}
