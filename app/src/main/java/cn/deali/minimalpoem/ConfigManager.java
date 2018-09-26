package cn.deali.minimalpoem;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ConfigManager {
    private static final String TAG = "ConfigManager";

    private Context context;

    public ConfigManager(Context context) {
        this.context = context;
    }

    public String getHost() {
        return context.getString(R.string.host);
    }

    public int getVersionCode() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return 0;
        }
    }


    public String getVersionName() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            return "「获取版本号出错」";
        }
    }

    public String getAppId() {
        return context.getString(R.string.app_id);
    }

    public String getDBName() {
        return context.getString(R.string.db_name);
    }
}
