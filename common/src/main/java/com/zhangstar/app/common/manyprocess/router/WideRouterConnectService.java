package com.zhangstar.app.common.manyprocess.router;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.IWideRouterAIDL;
import com.zhangstar.app.common.RouterRequest;
import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.tools.LogUtil;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   : WideRouterConnectService是用于在多进程情况下与其他进程的LocalRouter类进行通信的，
 *              多进程的LocalRouter统一由WideRouter外部路由进行管理。
 *
 *              WideRouter通过AIDL与每个进程LocalRouter的守护Service绑定到一起，
 *              每个LocalRouter也是通过AIDL与WideRouter的守护Service绑定到一起，
 *              这样，就达到了所有路由都是双向互连的目的。
 * </pre>
 */

public class WideRouterConnectService extends Service {
    private static final String TAG = "WideRouterConnectService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String processName = intent.getStringExtra("domain");
        if (!WideRouter.getInstance(BaseApplication.getInstance()).checkLocalRouterHasRegistered(processName)) {
            LogUtil.e("TAG", processName + "进程下的LocalRouter没有注册！");
            return null;
        }
        WideRouter.getInstance(BaseApplication.getInstance()).connectLocalRouter(processName);
        return stub;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    IWideRouterAIDL.Stub stub = new IWideRouterAIDL.Stub() {
        @Override
        public boolean checkResponseAsync(RouterRequest request) throws RemoteException {
            return WideRouter.getInstance(BaseApplication.getInstance()).answerLocalAsync(request);
        }

        @Override
        public ActionResult route(RouterRequest request) throws RemoteException {
            return WideRouter.getInstance(BaseApplication.getInstance()).route(request);
        }

        @Override
        public void stopLocalRouter(String processName) throws RemoteException {
            WideRouter.getInstance(BaseApplication.getInstance()).disconnectLocalRouterServiceConnection(processName);
        }

    };
}
