package com.wills.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 王帅
 * @date 2021-07-29 13:52:44
 * @description:
 */
public class DateUtil {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(Date target){
        return df.format(target);
    }
}
