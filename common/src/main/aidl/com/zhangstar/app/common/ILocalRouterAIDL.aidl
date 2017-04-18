// ILocalRouterAIDL.aidl
package com.zhangstar.app.common;

// Declare any non-default types here with import statements
import com.zhangstar.app.common.RouterRequest;
import com.zhangstar.app.common.ActionResult;
interface ILocalRouterAIDL {
    boolean checkResponseAsync(in RouterRequest request);
    ActionResult route(in RouterRequest request);
    void connectWideRouter();
    void stopWideRouter();
}
