package com.zhangstar.app.common.manyprocess.router;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   : LocalRouter的守护Service的包装类
 * </pre>
 */

public class ConnectServiceWrapper {
    public Class<? extends LocalRouterConnectService> targetClass = null;

    public ConnectServiceWrapper(Class<? extends LocalRouterConnectService> logicClass) {
        this.targetClass = logicClass;
    }
}
