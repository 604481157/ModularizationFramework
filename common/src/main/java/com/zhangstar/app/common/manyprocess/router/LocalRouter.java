package com.zhangstar.app.common.manyprocess.router;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.BaseAction;
import com.zhangstar.app.common.BaseProvider;
import com.zhangstar.app.common.IWideRouterAIDL;
import com.zhangstar.app.common.RouterRequest;
import com.zhangstar.app.common.manyprocess.ErrorAction;
import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.tools.ProcessUtil;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   : LocalRouter是基于进程（JVM）的单例，当启动多进程时，Application重新多次启动，LocalRouter也会多次实例，而对应的进程名也会变动。
 * </pre>
 */

public class LocalRouter {
    private static final String TAG = "LocalRouter";
    private static LocalRouter sInstance = null;
    private String mProcessName = ProcessUtil.UNKNOWN_PROCESS_NAME;//默认进程名
    private BaseApplication mApplication;
    private IWideRouterAIDL mIWideRouterAIDL = null;
    private HashMap<String, BaseProvider> mProviders;
    private ServiceConnection mServiceConnection = new ServiceConnection() {//与外部进程的WideService进行通信。 WideService为服务端，LocalRoter为其他进程的客户端，由客户端单向调用请求
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIWideRouterAIDL = IWideRouterAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIWideRouterAIDL = null;
        }
    };
    private static ExecutorService threadPool;

    public LocalRouter(BaseApplication mBaseApplication) {
        mApplication = mBaseApplication;
        mProcessName = ProcessUtil.getProcessName(mBaseApplication, ProcessUtil.getMyProcessId());//一般获取的都是应用的主进程名，也就是应用启动的那个进程。
        mProviders = new HashMap<>();
        if (mBaseApplication.needMultipleProcess() && !WideRouter.PROCESS_NAME.equals(mProcessName)) {
            connectWideService();
        }
    }

    public static synchronized LocalRouter getInstance(@NonNull BaseApplication context) {
        if (sInstance == null) {
            sInstance = new LocalRouter(context);
        }
        return sInstance;
    }

    private static ExecutorService getThreadPool() {
        if (threadPool == null) {
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }

    /**
     * 连接WideService，在多进程情况LocalRouter可能会多次用binder方式启动WideService，并通过WideAIDL与WideService建立联系
     * 把每个进程的进程名传递过去，并让WideRouter去启动事先在application中注册的多进程守护Service
     */
    public void connectWideService() {
        Intent binderIntent = new Intent(mApplication, WideRouterConnectService.class);
        binderIntent.putExtra("domain", mProcessName);
        mApplication.bindService(binderIntent, mServiceConnection, BIND_AUTO_CREATE);
    }


    /**
     * 断开此进程下的LocalRouter与WideSerivce的通信
     */
    public void disconnectWideRouterServiceConnetion() {
        if (mServiceConnection == null)
            return;
        mApplication.unbindService(mServiceConnection);
        mIWideRouterAIDL = null;
    }

    /**
     * LocalRouter先停止自己与外部路由的守护Service(WiderRouter)的通信连接
     * 再停止自己进程的守护Service
     */
    public void stopMyself(Class<? extends LocalRouterConnectService> clazz) {
        if (checkWideRouterConnection()) {
            try {
                mIWideRouterAIDL.stopLocalRouter(mProcessName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            mApplication.stopService(new Intent(mApplication, clazz));
        }
    }

    public boolean checkWideRouterConnection() {
        boolean result = false;
        if (mIWideRouterAIDL != null) {
            result = true;
        }
        return result;
    }

    public boolean answerLocalAsync(@NonNull RouterRequest request) {
        if (mProcessName.equals(request.getProcessName()) && checkWideRouterConnection()) {
            return findRequestAction(request).isAsync();
        } else {
            return false;
        }
    }

    /**
     * 在每个module中的LocalRouter都去注册每个module的提供类
     *
     * @param providerName module的提供类的名字
     * @param provider     module的提供类
     */
    public void registerProvider(String providerName, BaseProvider provider) {
        mProviders.put(providerName, provider);
    }

    public ActionResult route(Context context, @NonNull RouterRequest request) throws Exception {
        ActionResult result = new ActionResult();
        if (mProcessName.equals(request.getProcessName())) {
            //本地主进程内请求
            BaseAction action = findRequestAction(request);
            if (action.isAsync()) {
                //异步请求操作
                LocalTask localTask = new LocalTask(request, action, context);
                result.futureResult = getThreadPool().submit(localTask);
            } else {
                //同步请求操作
                result = action.invoke(context, request);
            }
            result.isActionAsync = action.isAsync();
            request.isIdle.set(true);
        } else if (!mApplication.needMultipleProcess()) {
            throw new RuntimeException("请确认您应用是否需要多进程！如果是，请在application中修改isNeedMultipleProcess值");
        } else {
            //IPC 跨进程请求
            request.isIdle.set(true);
            if (checkWideRouterConnection()) {
                result.isActionAsync = mIWideRouterAIDL.checkResponseAsync(request);
            } else {
                result.isActionAsync = true;
                ConnectionTask connectionTask = new ConnectionTask(request);
                result.futureResult = getThreadPool().submit(connectionTask);
                return result;
            }
            if (result.isActionAsync) {
                //Async异步操作
                result.isActionAsync = true;
                WideTask task = new WideTask(request);
                result.futureResult = getThreadPool().submit(task);
            } else {
                //sync同步操作
                result = mIWideRouterAIDL.route(request);
                result.isActionAsync = false;
            }
        }
        return result;
    }

    public Observable<ActionResult> rxRoute(Context context, @NonNull RouterRequest request) throws Exception {
        Observable<ActionResult> result;
        if (mProcessName.equals(request.getProcessName())) {
            //本地主进程内请求
            BaseAction action = findRequestAction(request);
            if (action.isAsync()) {
                //异步请求操作
                LocalTask localTask = new LocalTask(request, action, context);
                result = Observable.fromFuture(getThreadPool().submit(localTask));
            } else {
                //同步请求操作
                result = Observable.just(action.invoke(context, request));
            }
            request.isIdle.set(true);
        } else if (!mApplication.needMultipleProcess()) {
            throw new RuntimeException("请确认您应用是否需要多进程！如果是，请在application中修改isNeedMultipleProcess值");
        } else {
            //IPC 跨进程请求
            boolean isActionAsync;
            request.isIdle.set(true);
            if (checkWideRouterConnection()) {
                isActionAsync = mIWideRouterAIDL.checkResponseAsync(request);
            } else {
                ConnectionTask connectionTask = new ConnectionTask(request);
                result = Observable.fromFuture(getThreadPool().submit(connectionTask));
                return result;
            }
            if (isActionAsync) {
                //Async异步操作
                WideTask task = new WideTask(request);
                result = Observable.fromFuture(getThreadPool().submit(task));
            } else {
                //sync同步操作
                result = Observable.just(mIWideRouterAIDL.route(request));
            }
        }
        return result;
    }

    private BaseAction findRequestAction(RouterRequest request) {
        BaseProvider provider = mProviders.get(request.getProvider());
        if (provider == null) {
            return new ErrorAction(false, ActionResult.ACTION_NOT_FOUND, "Provider is not found");
        } else {
            BaseAction action = provider.findAction(request.getAction());
            if (action == null) {
                return new ErrorAction(false, ActionResult.ACTION_NOT_FOUND, "Action is not found");
            } else {
                return action;
            }
        }
    }

    private class LocalTask implements Callable<ActionResult> {
        private BaseAction action;
        private RouterRequest request;
        private Context mContext;

        public LocalTask(RouterRequest request, BaseAction action, Context context) {
            this.request = request;
            this.action = action;
            this.mContext = context;
        }

        @Override
        public ActionResult call() throws Exception {
            return action.invoke(mContext, request);
        }
    }

    /**
     * 该task作用是连接widerouter并且执行寻址工作
     * 因为已经另起线程了，所以我们这里也不需要再关心action执行是否为异步了
     */
    private class ConnectionTask implements Callable<ActionResult> {
        private int time = 0;
        private RouterRequest mRouterRequest;

        public ConnectionTask(RouterRequest mRouterRequest) {
            this.mRouterRequest = mRouterRequest;
        }

        @Override
        public ActionResult call() throws Exception {
            connectWideService();
            while (true) {
                if (mIWideRouterAIDL == null) {
                    Thread.sleep(30);
                    time++;
                } else if (time >= 600) {
                    ErrorAction action = new ErrorAction(true, ActionResult.CODE_CANNOT_BIND_WIDE, "不能绑定上WideRouterService");
                    return action.invoke(mApplication, mRouterRequest);
                } else {
                    break;
                }
            }
            return mIWideRouterAIDL.route(mRouterRequest);
        }
    }

    private class WideTask implements Callable<ActionResult> {
        private RouterRequest request;

        public WideTask(RouterRequest request) {
            this.request = request;
        }

        @Override
        public ActionResult call() throws Exception {
            return mIWideRouterAIDL.route(request);
        }
    }
}
