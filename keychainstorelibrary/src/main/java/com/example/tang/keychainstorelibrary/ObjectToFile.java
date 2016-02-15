package com.example.tang.keychainstorelibrary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tang on 16/2/15.
 */
public class ObjectToFile {
    public static void writeMapToFile(String filePath,HashMap<String,String> map) {
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(map);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(outStream!=null)
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static HashMap<String,String> readMapFromFile(String filePath){
        HashMap<String,String> map = new HashMap<String,String>();
        FileInputStream freader = null;
        try {
            freader = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(freader);
            map = (HashMap<String, String>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (freader!=null){
                try {
                    freader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return map;
    }


}
