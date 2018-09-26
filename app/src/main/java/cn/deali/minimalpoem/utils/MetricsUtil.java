package cn.deali.minimalpoem.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class MetricsUtil {
	private final static String TAG = "MetricsUtil";

	public static Point getSize(Context ctx) {
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		Point size = new Point();
		size.x = dm.widthPixels;
		size.y = dm.heightPixels;
		return size;
	}

	public static int getPopWidth(Context ctx) {
		Point size = getSize(ctx);
		return size.x * 2/5;
	}
	
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)  。加0.5的目的是四舍五入
     */  
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 。加0.5的目的是四舍五入
     */  
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}

