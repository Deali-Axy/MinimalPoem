package cn.deali.minimalpoem.utils;

import android.telephony.TelephonyManager;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

public class Utils {

	public static String getDotOne(double src) {
		DecimalFormat df = new DecimalFormat(".0");
		String value = df.format(src);
		return value;
	}

	public static String[] mClassNameArray = {"UNKNOWN", "2G", "3G", "4G"};

//	public static int TYPE_UNKNOWN = 0;
//	public static int TYPE_2G = 1;
//	public static int TYPE_3G = 2;
//	public static int TYPE_4G = 3;
//    
//	public static int TYPE_GSM = 1;
//	public static int TYPE_CDMA = 2;
//	public static int TYPE_LTE = 3;
//	public static int TYPE_WCDMA = 4;

	public static String getNetworkTypeName(TelephonyManager tm, int mobile_type) {
		String type_name = "";
		try {
			Method method = tm.getClass().getMethod("getNetworkTypeName", Integer.TYPE);
			type_name = (String) method.invoke(tm, mobile_type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type_name;
	}

	public static int getClassType(TelephonyManager tm, int mobile_type) {
		int class_type = 0;
		try {
			Method method = tm.getClass().getMethod("getNetworkClass", Integer.TYPE);
			class_type = (Integer) method.invoke(tm, mobile_type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return class_type;
	}

	public static String getClassName(TelephonyManager tm, int mobile_type) {
		int class_type = getClassType(tm, mobile_type);
		return mClassNameArray[class_type];
	}
	
	public static String getFileSize(String file_path) {
		File file = new File(file_path);
		long size_length = 0;
		if (file.exists() == true) {
			size_length = file.length();
		}
		String size = size_length+"B";
		if (size_length > 1024*1024) {
			size = getDotOne(size_length/1024.0/1024.0) + "MB";
		} else if (size_length > 1024) {
			size = getDotOne(size_length/1024.0) + "KB";
		}
		return size;
	}

}
