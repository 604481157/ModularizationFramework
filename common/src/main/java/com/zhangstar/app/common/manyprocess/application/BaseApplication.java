package com.zhangstar.app.common.manyprocess.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import com.zhangstar.app.common.manyprocess.router.LocalRouter;
import com.zhangstar.app.common.manyprocess.router.WideRouter;
import com.zhangstar.app.common.manyprocess.router.WideRouterConnectService;
import com.zhangstar.app.common.manyprocess.tools.LogUtil;
import com.zhangstar.app.common.manyprocess.tools.ProcessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/28
 *     desc   : BaseApplication类
 * </pre>
 */

public abstract class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    private static BaseApplication instance;
    private HashMap<String, List<PriorityLogicWrapper>> mLogicClassMap;
    private List<PriorityLogicWrapper> logicList;
    private List<Activity> activitys = new ArrayList<Activity>();

    @Override

    public void onCreate() {
        super.onCreate();
        instance = this;
        LogUtil.d(TAG, "启动BaseApplication---onCreate()" + System.currentTimeMillis());
        initLocalRouter();
        registerApplicationLogics();
        initLogic();
    }

    //实例化本地路由LocalRoter
    private void initLocalRouter() {
        mLogicClassMap = new HashMap<>();
        LocalRouter.getInstance(this);
        if (needMultipleProcess()) {
            //启动多进程的外部路由的守护Service
            registerApplicationLogic(WideRouter.PROCESS_NAME, 1000, WideApplicationLogic.class);
            Intent intent = new Intent(this, WideRouterConnectService.class);
            startService(intent);
        }
    }

    /**
     * 多进程情况下，在外部路由（WideRouter）注册多进程的守护Service(前提你是知道多进程的存在)
     */
    protected abstract void registerAllProcessService();

    protected abstract void registerApplicationLogics();

    public abstract boolean needMultipleProcess();


    public void registerApplicationLogic(String processName, int priority, Class<? extends BaseApplicationLogic> logic) {
        if (mLogicClassMap != null) {
            List<PriorityLogicWrapper> tempList = mLogicClassMap.get(processName);
            if (tempList == null) {
                tempList = new ArrayList<>();
                mLogicClassMap.put(processName, tempList);
            }
            if (tempList.size() > 0) {
                for (PriorityLogicWrapper priorityLogicWrapper : tempList) {
                    if (logic.getName().equals(priorityLogicWrapper.logicClass.getName())) {
                        throw new RuntimeException(logic.getName() + " 已经注册过了.");
                    }
                }
            }
            PriorityLogicWrapper warpper = new PriorityLogicWrapper(priority, logic);
            tempList.add(warpper);
        }
    }

    /**
     * 手动让logic执行oonCrate方法，让Loigc和生命周期同步
     */
    public void initLogic() {
        logicList = mLogicClassMap.get(ProcessUtil.getProcessName(this, ProcessUtil.getMyProcessId()));
        if (logicList != null && logicList.size() > 0) {
            for (PriorityLogicWrapper priorityLogicWrapper : logicList) {
                if (priorityLogicWrapper != null) {
                    try {
                        priorityLogicWrapper.instance = priorityLogicWrapper.logicClass.newInstance();
                        priorityLogicWrapper.instance.setApplication(this);
                        priorityLogicWrapper.instance.onCreate();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 当终止应用程序对象时调用，不保证一定被调用，当程序是被内核终止以便为其他应用程序释放资源，那
     * 么将不会提醒，并且不调用应用程序的对象的onTerminate方法而直接终止进程
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.i(TAG, "BaseApplication---onTerminate()");
    }

    /**
     * 当后台程序已经终止,资源还匮乏时会调用这个方法。
     * 好的应用程序一般会在这个方法里面释放一些不必要的资源来应付当后台程序已经终止，前台应用程序内存还不够时的情况。
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtil.i(TAG, "BaseApplication---onLowMemory()");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        LogUtil.i(TAG, "BaseApplication---onLowMemory()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null) {
            if (!activitys.contains(activity)) {
                activitys.add(activity);
            }
        }
    }

    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
    }
}
