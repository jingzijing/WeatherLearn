package com.jzj.weatherlearn.global;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

//sp实例提供类
public class SharedPreferencesManager {

    private static SharedPreferences sharedPreferences;
    private static List<String> tags = new ArrayList<>();

    public static synchronized SharedPreferences getSharedPreferences(String tag) {
        if (sharedPreferences == null) {
            sharedPreferences = App.context.getSharedPreferences(App.SP_CACHE, 0);
        }
        tags.add(tag);
        return sharedPreferences;
    }

    private static boolean hasContext() {
        if (tags.size() == 0) {
            return false;
        }
        return true;
    }

    public static void itemDestroy(String tag) {
        tags.remove(tag);
        if (!hasContext()) {
            sharedPreferences = null;
        }
    }

}
