package cn.deali.minimalpoem;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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
        mApp = this;
        config = new ConfigManager(this);
        daoSession = initGreenDao();
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
