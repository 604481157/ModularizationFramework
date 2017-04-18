package com.zhangstar.app.common.manyprocess.router;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.ILocalRouterAIDL;
import com.zhangstar.app.common.RouterRequest;
import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.tools.LogUtil;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   :
 * </pre>
 */

public class LocalRouterConnectService extends Service {
    private static final String TAG = "LocalRouterConnectService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind");
        return stub;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    ILocalRouterAIDL.Stub stub = new ILocalRouterAIDL.Stub() {

        @Override
        public boolean checkResponseAsync(RouterRequest request) throws RemoteException {
            return LocalRouter.getInstance(BaseApplication.getInstance()).answerLocalAsync(request);
        }

        @Override
        public ActionResult route(RouterRequest request) {
            try {
                return LocalRouter.getInstance(BaseApplication.getInstance()).route(LocalRouterConnectService.this, request);
            } catch (Exception e) {
                return new ActionResult(LocalRouterConnectService.this, ActionResult.CODE_ERROR, e.getMessage());
            }
        }

        @Override
        public void connectWideRouter() throws RemoteException {
            LocalRouter.getInstance(BaseApplication.getInstance()).connectWideService();
        }

        @Override
        public void stopWideRouter() throws RemoteException {
            LocalRouter.getInstance(BaseApplication.getInstance()).disconnectWideRouterServiceConnetion();
        }
    };
}
