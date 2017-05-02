package com.zhangstar.app.common.network;

/**
 * Created by zhangstar on 2016/12/9.
 */

public class FileDownload {
    long total;
    long bytesLoaded;

    public FileDownload(long total, long bytesLoaded) {
        this.total = total;
        this.bytesLoaded = bytesLoaded;
    }

    public long getBytesLoaded() {
        return bytesLoaded;
    }

    public long getTotal() {
        return total;
    }
}
