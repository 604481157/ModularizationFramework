package com.zhangstar.app.common.manyprocess.tools;

import android.util.Log;

import com.zhangstar.app.common.BuildConfig;

/**
 * Created by zhangstar on 2017/1/10.
 */
public class LogUtil {

    public static void e(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.w(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.v(tag, msg);
    }
}
