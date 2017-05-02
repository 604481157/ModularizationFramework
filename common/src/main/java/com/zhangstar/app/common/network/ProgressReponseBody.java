package com.zhangstar.app.common.network;


import com.zhangstar.app.common.rx.RxBus;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by zhangstar on 2016/12/9.
 */

public class ProgressReponseBody extends ResponseBody {
    private ResponseBody mResponseBody;
    private BufferedSource bufferedSource;

    public ProgressReponseBody(ResponseBody mResponseBody) {
        this.mResponseBody = mResponseBody;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(mResponseBody.source()));
        }

        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long bytesReaded = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                bytesReaded += bytesRead == -1 ? 0 : bytesRead;
                RxBus.getInstance().post(new FileDownload(contentLength(), bytesReaded));
                return bytesRead;
            }
        };
    }


}
