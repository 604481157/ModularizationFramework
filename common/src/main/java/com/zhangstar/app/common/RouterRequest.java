package com.zhangstar.app.common;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.zhangstar.app.common.manyprocess.tools.ProcessUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/03/29
 *     desc   : 向路由请求转发的请求类
 * </pre>
 */
public class RouterRequest implements Parcelable {
    private static final String TAG = "RouterRequest";
    private static String DEFAULT_PROCESS = "";
    private String from;
    private String processName;//进程名
    private String provider;
    private String action;
    private HashMap<String, String> data;
    private Object object;
    public AtomicBoolean isIdle = new AtomicBoolean(true);
    private static final int length = 64;
    private static AtomicInteger sIndex = new AtomicInteger(0);
    private static final int RESET_NUM = 1000;
    private static volatile RouterRequest[] table = new RouterRequest[length];//高并发的常量池

    static {
        for (int i = 0; i < length; i++) {
            table[i] = new RouterRequest();
        }
    }

    protected RouterRequest(Parcel in) {
        DEFAULT_PROCESS = in.readString();
        processName = in.readString();
        provider = in.readString();
        action = in.readString();
        data = in.readHashMap(HashMap.class.getClassLoader());

    }

    private RouterRequest() {
        this.from = DEFAULT_PROCESS;
        this.processName = DEFAULT_PROCESS;
        this.provider = "";
        this.action = "";
        this.data = new HashMap<>();
    }


    private RouterRequest(Context context) {
        this.from = getProcess(context);
        this.processName = getProcess(context);
        this.provider = "";
        this.action = "";
        this.data = new HashMap<>();
    }


    public String getFrom() {
        return from;
    }

    public String getProcessName() {
        return processName;
    }

    public String getProvider() {
        return provider;
    }

    public String getAction() {
        return action;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public Object getAndClearObject() {
        Object temp = object;
        object = null;
        return temp;
    }

    private static String getProcess(Context context) {
        if (TextUtils.isEmpty(DEFAULT_PROCESS) || ProcessUtil.UNKNOWN_PROCESS_NAME.equals(DEFAULT_PROCESS)) {
            DEFAULT_PROCESS = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        }
        return DEFAULT_PROCESS;
    }

    @Override
    public String toString() {
        //Here remove Gson to save about 10ms.
        //String result = new Gson().toJson(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from", from);
            jsonObject.put("processName", processName);
            jsonObject.put("provider", provider);
            jsonObject.put("action", action);

            try {
                JSONObject jsonData = new JSONObject();
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    jsonData.put(entry.getKey(), entry.getValue());
                }
                jsonObject.put("data", jsonData);
            } catch (Exception e) {
                e.printStackTrace();
                jsonObject.put("data", "{}");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }


    public RouterRequest processName(String processName) {
        this.processName = processName;
        return this;
    }


    public RouterRequest provider(String provider) {
        this.provider = provider;
        return this;
    }


    public RouterRequest action(String action) {
        this.action = action;
        return this;
    }


    public RouterRequest data(String key, String data) {
        this.data.put(key, data);
        return this;
    }

    public RouterRequest object(Object object) {
        this.object = object;
        return this;
    }

    public static RouterRequest obtain(Context context) {
        return obtain(context, 0);
    }

    private static RouterRequest obtain(Context context, int retryTime) {
        int index = sIndex.getAndIncrement();
        if (index > RESET_NUM) {
            sIndex.compareAndSet(index, 0);
            if (index > RESET_NUM * 2) {
                sIndex.set(0);
            }
        }

        int num = index & (length - 1);

        RouterRequest target = table[num];

        if (target.isIdle.compareAndSet(true, false)) {
            target.from = getProcess(context);
            target.processName = getProcess(context);
            target.provider = "";
            target.action = "";
            target.data.clear();
            return target;
        } else {
            if (retryTime < 5) {
                return obtain(context, retryTime++);
            } else {
                return new RouterRequest(context);
            }

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DEFAULT_PROCESS);
        dest.writeString(processName);
        dest.writeString(provider);
        dest.writeString(action);
        dest.writeMap(data);
    }

    public static final Creator<RouterRequest> CREATOR = new Creator<RouterRequest>() {
        @Override
        public RouterRequest createFromParcel(Parcel in) {
            return new RouterRequest(in);
        }

        @Override
        public RouterRequest[] newArray(int size) {
            return new RouterRequest[size];
        }
    };

}
