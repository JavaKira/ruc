package com.github.javakira.ruc;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class FileIO {
    public static String tag = "ruc FileIO";

    public static Properties loadProps(String filename, Context context) {
        Properties properties = new Properties();
        try {
            if (isExist(filename, context))
                properties.load(context.openFileInput(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    public static void writeProps(String filename, Context context, Properties properties) {
        try {
            properties.store(context.openFileOutput(filename, Context.MODE_PRIVATE), "");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
