package com.zhangstar.app.common.manyprocess.router;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.ILocalRouterAIDL;
import com.zhangstar.app.common.RouterRequest;
import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.tools.ProcessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   :
 * </pre>
 */

public class WideRouter {
    private static final String TAG = "WideRouter";
    public static final String PROCESS_NAME = "com.zhangstar.wide";//外部路由启动所在的进程名
    private static WideRouter instance;
    private BaseApplication mApplication;
    private static HashMap<String, ConnectServiceWrapper> localRouterServiceMap;//保存的其他本地路由的守护Service
    private HashMap<String, ServiceConnection> mLocalRouterConnectionMap;
    private HashMap<String, ILocalRouterAIDL> mLocalRouterAIDLHashMap;
    private boolean isStop = false;

    public static synchronized WideRouter getInstance(@NonNull BaseApplication context) {
        if (instance == null) {
            instance = new WideRouter(context);
        }
        return instance;
    }


    private WideRouter(BaseApplication context) {
        mApplication = context;
        String checkProcessName = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        if (!PROCESS_NAME.equals(checkProcessName)) {
            throw new RuntimeException("你不应该初始化WideRoter在这个进程:" + checkProcessName);
        }
        localRouterServiceMap = new HashMap<>();
        mLocalRouterConnectionMap = new HashMap<>();
        mLocalRouterAIDLHashMap = new HashMap<>();
    }

    public static void registerLocalRouter(String processName, Class<? extends LocalRouterConnectService> targetClass) {

        if (null == localRouterServiceMap) {
            localRouterServiceMap = new HashMap<>();
        }
        ConnectServiceWrapper connectServiceWrapper = new ConnectServiceWrapper(targetClass);
        localRouterServiceMap.put(processName, connectServiceWrapper);
    }

    /**
     * WideRouter与多进程的LocalRouter的守护Service进行AIDL通信连接
     *
     * @param domain localRouter所在进程的进程名
     * @return
     */
    boolean connectLocalRouter(final String domain) {
        ConnectServiceWrapper connectServiceWrapper = localRouterServiceMap.get(domain);
        if (null == connectServiceWrapper) {
            return false;
        }
        Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
        if (null == clazz) {
            return false;
        }
        Intent binderIntent = new Intent(mApplication, clazz);
        Bundle bundle = new Bundle();
        binderIntent.putExtras(bundle);
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ILocalRouterAIDL mLocalRouterAIDL = ILocalRouterAIDL.Stub.asInterface(service);
                ILocalRouterAIDL temp = mLocalRouterAIDLHashMap.get(domain);
                if (null == temp) {
                    mLocalRouterAIDLHashMap.put(domain, mLocalRouterAIDL);
                    mLocalRouterConnectionMap.put(domain, this);
//                    try {
//                        mLocalRouterAIDL.connectWideRouter();
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLocalRouterAIDLHashMap.remove(domain);
                mLocalRouterConnectionMap.remove(domain);
            }
        };
        mApplication.bindService(binderIntent, serviceConnection, BIND_AUTO_CREATE);
        return true;
    }

    /**
     * 断开WideRouter与某进程的守护Service的连接，并停止此Service
     *
     * @param processName 进程名
     * @return
     */
    public boolean disconnectLocalRouterServiceConnection(String processName) {
        if (TextUtils.isEmpty(processName)) {
            return false;
        } else if (PROCESS_NAME.equals(processName)) {
            //停止WideRouter
            stopSelf();
            return true;
        } else if (null == mLocalRouterConnectionMap.get(processName)) {
            return false;
        } else {
            ILocalRouterAIDL aidl = mLocalRouterAIDLHashMap.get(processName);
            if (aidl != null) {
                try {
                    aidl.stopWideRouter();//停止本地LocalRouter与外部WideService的通信 并且一一取消绑定
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mApplication.unbindService(mLocalRouterConnectionMap.get(processName));//停止某一个进程的守护Service
            mLocalRouterAIDLHashMap.remove(processName);
            mLocalRouterConnectionMap.remove(processName);
            return true;
        }
    }


    /**
     *
     */
    public void stopSelf() {
        isStop = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> localKeys = new ArrayList<String>();
                localKeys.addAll(mLocalRouterAIDLHashMap.keySet());
                for (String localKey : localKeys) {
                    ILocalRouterAIDL localAIDL = mLocalRouterAIDLHashMap.get(localKey);
                    if (localAIDL != null) {
                        try {
                            localAIDL.stopWideRouter();//停止本地LocalRouter与外部WideService的通信 并且一一取消绑定
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    mApplication.unbindService(mLocalRouterConnectionMap.get(localKey));//停止某一个进程的守护Service
                    mLocalRouterAIDLHashMap.remove(localKey);
                    mLocalRouterConnectionMap.remove(localKey);
                }
                try {
                    Thread.sleep(2000);
                    mApplication.stopService(new Intent(mApplication, WideRouterConnectService.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }).start();
    }

    /**
     * 检查LocalRouter收到的请求是否异步
     * 多进程启动时，都会在本进程的LocalRouter中与WideSerivce绑定起来
     * 存在情况：
     * 1、当向router发送请求时，需要的多进程并没有启动或存在。此时mLocalRouterAIDLHashMap、mLocalRouterConnectionMap中也就没有存在此进程名字的AIDL、ServiceConnetion。
     *
     * @param request
     *
     */
    public boolean answerLocalAsync(RouterRequest request) {
        ILocalRouterAIDL localRouterAIDL = mLocalRouterAIDLHashMap.get(request.getProcessName());
        if (localRouterAIDL == null) {
            //多进程还没有启动的情况下
            ConnectServiceWrapper wrapperService = localRouterServiceMap.get(request.getProcessName());
            if (wrapperService == null)
                //且没有目标进程的守护Service存在的时候（就判断为同一进程）
                return false;
            Class<? extends LocalRouterConnectService> target = wrapperService.targetClass;
            if (target == null)
                return false;
            else
                //目标守护Service存在，则此操作则需要开启进程，可以断定为异步操作
                return true;
        } else {
            //多进程已经启动、存在的情况下。
            try {
                return localRouterAIDL.checkResponseAsync(request);
            } catch (RemoteException e) {
                e.printStackTrace();
                return true;
            }
        }
    }

    boolean checkLocalRouterHasRegistered(final String domain) {
        ConnectServiceWrapper connectServiceWrapper = localRouterServiceMap.get(domain);
        if (null == connectServiceWrapper) {
            return false;
        }
        Class<? extends LocalRouterConnectService> clazz = connectServiceWrapper.targetClass;
        if (null == clazz) {
            return false;
        } else {
            return true;
        }
    }

    public ActionResult route(RouterRequest request) {
        ActionResult result;
        if (isStop) {
            result = new ActionResult(mApplication, ActionResult.WIDEROUTER_NOT_WORKING, "WideRouter已经停止工作了");
            return result;
        }
        if (PROCESS_NAME.equals(request.getProcessName())) {
            result = new ActionResult(mApplication, ActionResult.CODE_ERROR, "你不能在外部路由发起请求！");
            return result;
        }
        ILocalRouterAIDL targetLocalRouterAIDL = mLocalRouterAIDLHashMap.get(request.getProcessName());
        if (targetLocalRouterAIDL == null) {
            if (!connectLocalRouter(request.getProcessName())) {
                return new ActionResult(mApplication, ActionResult.CODE_ROUTER_NOT_REGISTER, "目标进程的Service没有注册");
            } else {
                int time = 0;
                while (true) {
                    targetLocalRouterAIDL = mLocalRouterAIDLHashMap.get(request.getProcessName());
                    if (targetLocalRouterAIDL == null) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        time++;
                    } else {
                        break;
                    }
                    if (time >= 100) {
                        return new ActionResult(mApplication, ActionResult.CODE_CANNOT_BIND_LOCAL, "不能绑定目标进程：" + request.getProcessName() + "的Service");
                    }
                }
            }
        }
        try {
            result = targetLocalRouterAIDL.route(request);
        } catch (RemoteException e) {
            e.printStackTrace();
            result = new ActionResult(mApplication, ActionResult.REMOTE_EXCEPTION, e.getMessage());
        }
        return result;
    }

}
