package com.zhangstar.app.common.manyprocess;

import android.content.Context;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.BaseAction;
import com.zhangstar.app.common.RouterRequest;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/11
 *     desc   : 当调用者找不到Action时或查找出来的Action为空时，则返回给调用者这个异常Action类
 * </pre>
 */

public class ErrorAction extends BaseAction {
    private static final String DEFALULT_MESSAGE = "可能某些地方出错了，但是不影响程序运行";
    private boolean isAsyns;
    private String errorMessage;
    private int code;

    public ErrorAction() {
        isAsyns = false;
        code = ActionResult.CODE_ERROR;
        errorMessage = DEFALULT_MESSAGE;
    }

    public ErrorAction(boolean isAsyns, int errorCode, String errorMessage) {
        this.code = errorCode;
        this.isAsyns = isAsyns;
        this.errorMessage = errorMessage;
    }

    @Override
    public ActionResult invoke(Context context, RouterRequest requestData) {
        return new ActionResult(context, code, errorMessage);
    }

    @Override
    public boolean isAsync() {
        return isAsyns;
    }
}
