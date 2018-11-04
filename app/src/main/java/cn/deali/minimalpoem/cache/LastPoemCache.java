package cn.deali.minimalpoem.cache;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.Instant;

import cn.deali.minimalpoem.MainApp;
import cn.deali.minimalpoem.bean.Poem;

public class LastPoemCache {

    private LastPoemCache() {
    }

    /**
     * 获取缓存的上一首古诗，没有的话返回null
     * @return
     */
    public static Poem getLastPoem() {
        SharedPreferences preferences = MainApp.getInstance().getSharedPreferences("lastPoemCache", Context.MODE_PRIVATE);
        if (preferences.contains("data")) {
            return Poem.parseJsonString(preferences.getString("data", ""));
        }else
            return null;
    }

    public static void setLastPoem(Poem poem) {
        SharedPreferences preferences = MainApp.getInstance().getSharedPreferences("lastPoemCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("data", poem.toJsonString());
        editor.apply();
    }
}
