package com.mylovin.music.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate() {
        return dateFormater.format(new Date());
    }
}
