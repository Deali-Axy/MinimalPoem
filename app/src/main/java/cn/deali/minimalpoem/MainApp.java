package cn.deali.minimalpoem;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.AppCompatDialog;

import cn.deali.minimalpoem.db.DaoMaster;
import cn.deali.minimalpoem.db.DaoSession;

// 管理全局 Application 对象
public class MainApp extends Application {
    private static MainApp mApp;

    // 配置管理器
    public ConfigManager config;
    // 数据库
    public DaoSession daoSession;

    public static MainApp getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 自动开启夜间模式
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        mApp = this;
        config = new ConfigManager(this);
        daoSession = initGreenDao();
    }


    public void setNightMode(boolean isEnabled) {
        if (isEnabled)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public boolean getNightMode() {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        switch (nightMode) {
            case AppCompatDelegate.MODE_NIGHT_YES:
                return true;
            case AppCompatDelegate.MODE_NIGHT_NO:
                return false;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                return false;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                return false;
            default:
                return false;
        }
    }


    /**
     * 数据库初始化
     *
     * @return
     */
    private DaoSession initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, config.getDBName());
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        return daoMaster.newSession();
    }
}
