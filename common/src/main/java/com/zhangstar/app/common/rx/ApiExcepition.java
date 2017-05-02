package com.zhangstar.app.common.rx;

import android.widget.Toast;

import com.zhangstar.app.common.manyprocess.application.BaseApplication;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/05/02
 *     desc   :
 * </pre>
 */

public class ApiExcepition {

    public static void resolveError(Throwable e) {
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
        } else if (e instanceof SocketTimeoutException) {
            Toast.makeText(BaseApplication.getInstance(), "网络连接超时", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
            Toast.makeText(BaseApplication.getInstance(), "网络连接超时", Toast.LENGTH_SHORT).show();
        } else if (e instanceof UnknownHostException) {
            Toast.makeText(BaseApplication.getInstance(), "网络异常，请检查网络是否开启！", Toast.LENGTH_SHORT).show();
        }
    }
}
