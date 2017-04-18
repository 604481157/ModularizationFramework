package com.zhangstar.app.common.manyprocess.application;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/28
 *     desc   : BaseApplication的包装类，根据priority的优先级来排序
 * </pre>
 */

public class PriorityLogicWrapper implements Comparable<PriorityLogicWrapper> {
    public int priority = 0;//优先级
    public Class<? extends BaseApplicationLogic> logicClass;//类型限界指明参数类型
    public BaseApplicationLogic instance;//就是上面的类型，因为类型限界无法调用类的方法，所以创建全局变量来进行一个缓冲对象达到调用方法的目的。

    public PriorityLogicWrapper(int priority, Class<? extends BaseApplicationLogic> logicClass) {
        this.priority = priority;
        this.logicClass = logicClass;
    }

    @Override
    public int compareTo(PriorityLogicWrapper o) {
        return o.priority - this.priority;
    }
}
