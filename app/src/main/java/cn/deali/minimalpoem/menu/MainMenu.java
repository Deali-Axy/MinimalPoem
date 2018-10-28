package cn.deali.minimalpoem.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import cn.deali.minimalpoem.MainApp;
import cn.deali.minimalpoem.R;
import cn.deali.minimalpoem.ConfigManager;
import cn.deali.minimalpoem.activity.FavoriteActivity;
import cn.deali.minimalpoem.task.CheckUpdateTask;

public class MainMenu {
    private static final String TAG = "MainMenu";
    private Context mContext;
    private ConfigManager configManager;

    public MainMenu(Context context) {
        this.mContext = context;
        configManager = new ConfigManager(mContext);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 我的收藏
            case R.id.menu_favorite:
                mContext.startActivity(new Intent(mContext, FavoriteActivity.class));
                return true;

            // 夜间模式
            case R.id.menu_night_mode:
                MainApp.getInstance().setNightMode(true);
                ((AppCompatActivity) mContext).recreate();
                return true;

            // 白天模式
            case R.id.menu_daylight_mode:
                MainApp.getInstance().setNightMode(false);
                ((AppCompatActivity) mContext).recreate();
                return true;

            // 「关于」菜单
            case R.id.menu_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(String.format("关于「%s」", mContext.getString(R.string.app_name)));

                String version = configManager.getVersionName();
                builder.setMessage(String.format("版本号：%s\n开发 by 画星星高手\n微信公众号：DealiAxy", version));
                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            // 检查更新菜单
            case R.id.menu_check_updates:
                Toast.makeText(mContext, "正在检查更新..", Toast.LENGTH_SHORT).show();
                new CheckUpdateTask(mContext, true).execute();
                return true;
            default:
                return false;
        }
    }
}