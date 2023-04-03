package com.github.javakira.ruc.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class FileIO {
    public static final String tag = "ruc FileIO";
    public static final String propsFileName = "config.txt";

    public static Properties loadProps(Context context) {
        Properties properties = new Properties();
        try {
            if (isExist(propsFileName, context))
                properties.load(context.openFileInput(propsFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    public static void writeProps(Context context, Properties properties) {
        try {
            properties.store(context.openFileOutput(propsFileName, Context.MODE_PRIVATE), "");
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
