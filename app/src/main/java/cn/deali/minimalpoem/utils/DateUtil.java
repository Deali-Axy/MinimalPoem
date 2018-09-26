package cn.deali.minimalpoem.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ouyangshen on 2016/9/24.
 */
public class DateUtil {
    @SuppressLint("SimpleDateFormat")
    public static String getNowDateTime(String formatStr) {
    	String format = formatStr;
    	if (format==null || format.length()<=0) {
    		format = "yyyyMMddHHmmss";
    	}
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowTime() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss");
        return s_format.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getNowTimeDetail() {
        SimpleDateFormat s_format = new SimpleDateFormat("HH:mm:ss.SSS");
        return s_format.format(new Date());
    }

	public static String formatTime(String src) {
		String dest = src;
		SimpleDateFormat old_sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat new_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = old_sdf.parse(src);
			dest = new_sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dest;
	}

}
