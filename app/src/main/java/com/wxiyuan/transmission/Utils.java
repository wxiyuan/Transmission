package com.wxiyuan.transmission;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

public class Utils {

    public static String getStringRes(@NonNull Context context, int resId) {
        return context.getString(resId);
    }

    public static String getMacAddress() {
        String macSerial = "";
        String line = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(inputStreamReader);
            for (; null != line;) {
                line = input.readLine();
                if (line != null) {
                    macSerial = line.trim();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/sit0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return macSerial;
    }

    private static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String result = loadReaderAsString(reader);
        reader.close();
        return result;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

}
