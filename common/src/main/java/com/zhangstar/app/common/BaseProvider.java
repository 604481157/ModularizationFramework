package com.zhangstar.app.common;

import java.util.HashMap;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/10
 *     desc   : 每个module中的事件提供者
 * </pre>
 */

public abstract class BaseProvider {
    private HashMap<String, BaseAction> mActions;

    public BaseProvider() {
        mActions = new HashMap<>();
        registerAllActions();
    }

    protected void regsiterAction(String actionName, BaseAction action) {
        mActions.put(actionName, action);
    }

    protected abstract void registerAllActions();

    public BaseAction findAction(String actionName) {
        return mActions.get(actionName);
    }
}
