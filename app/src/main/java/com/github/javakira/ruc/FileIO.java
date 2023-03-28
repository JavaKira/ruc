package com.github.javakira.ruc;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileIO {
    public static String tag = "ruc FileIO";

    public static void writeToFile(String data, String filename, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(tag, "File write failed: " + e);
        }
    }

    public static boolean isExist(String filename, Context context) {
        for (String str : context.fileList()) {
            if (str.equals(filename)) {
                return true;
            }
        }

        return false;
    }

    public static String readFromFile(String filename, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(tag, "File not found: " + e);
        } catch (IOException e) {
            Log.e(tag, "Can not read file: " + e);
        }

        return ret;
    }
}
