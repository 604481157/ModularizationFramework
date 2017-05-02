package com.zhangstar.app.common.manyprocess.tools;

import android.util.Log;

/**
 * Created by zhangstar on 2017/1/10.
 */
public class LogUtil {

    public static boolean isLoging = false;

    public static void e(String tag, String msg) {
        if (isLoging)
            Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isLoging)
            Log.w(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isLoging)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isLoging)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isLoging)
            Log.v(tag, msg);
    }

    public static void setIsLoging(boolean isLoging) {
        LogUtil.isLoging = isLoging;
    }
}
