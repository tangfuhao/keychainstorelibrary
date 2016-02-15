package com.example.tang.keychainstorelibrary;

import android.content.Context;
import android.os.Environment;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

/**
 * Created by tang on 16/2/15.
 */
public class KeyChainStore {
    private static final String TAG = "KeyChainStore";
    private KeyStore keyStore;
    private HashMap<String, String> tempData;
    private static KeyChainStore instance;
    private String saveFile;
    private Context mContext;

    private String ContextName;

    public static KeyChainStore getInstance(Context context) {
        if (instance == null) {
            instance = new KeyChainStore(context);
            instance.init();
        }

        return instance;
    }

    private KeyChainStore(Context context) {
        mContext = context.getApplicationContext();
        ContextName = mContext.getApplicationInfo().packageName.replace(".","");
    }

    private void init() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

        } catch (Exception e) {
        }

        ///////////////////////////////////
        boolean containsAlias = true;
        try {
            containsAlias = keyStore.containsAlias(ContextName);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        File keyChainFolder = new File(Environment.getExternalStorageDirectory(),TAG);
        File objectFile = new File(keyChainFolder,ContextName+".data");
        saveFile = objectFile.toString();

        if (!containsAlias) {
            //TODO 1,删除已有内容 2,创建别名
            if(!keyChainFolder.exists()) keyChainFolder.mkdir();
            if (objectFile.exists()) objectFile.delete();
            tempData = new HashMap<String, String>();
        }else{
            tempData = ObjectToFile.readMapFromFile(saveFile);
        }

        if(!containsAlias) CreateAlias(ContextName);
    }

    private void CreateAlias(String alias){
        try {
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(mContext)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    private String encrypt(String alias,String value) {
        ByteArrayOutputStream outputStream = null;
        CipherOutputStream cipherOutputStream = null;

        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            outputStream = new ByteArrayOutputStream();
            cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(value.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }finally {

            if(outputStream!=null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return null;
    }

    private String decrypt(String alias,String value) {
        CipherInputStream cipherInputStream = null;

        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(value, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            String finalText = new String(bytes, 0, bytes.length, "UTF-8");
            return finalText;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }finally {
            if(cipherInputStream!=null)
                try {
                    cipherInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return null;
    }

    public synchronized void saveInfoToDevice(String key, String value) {
        if (key != null && value != null) {
            String encryptValue =  encrypt(ContextName,value);
            tempData.put(key, encryptValue);
            ObjectToFile.writeMapToFile(saveFile,tempData);
        }
    }

    public synchronized String getInfoFromDevice(String key) {
        if (key != null && tempData.containsKey(key)) {
            String encryptValue =  tempData.get(key);
            return decrypt(ContextName,encryptValue);
        }
        return null;
    }

    public synchronized void removeInfoFromDevice(String key) {
        if (key != null && tempData.containsKey(key)) {
            tempData.remove(key);
            ObjectToFile.writeMapToFile(saveFile, tempData);
        }
    }

}

