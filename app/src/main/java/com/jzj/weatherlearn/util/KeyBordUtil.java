package com.jzj.weatherlearn.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 按键类工具
 */
public class KeyBordUtil {

    /**
     * 关闭软键盘
     * @param view
     */
    public static void closeKeyBordUtil(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
