package com.zhangstar.app.common.network;

import android.util.Log;

import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.rx.RxBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 * Created by zhangstar on 2016/12/9.
 */

public abstract class FileDownloadCallBack {
    int i = 1;

    public FileDownloadCallBack() {
        subscribe();
    }

    public abstract void updateProgress(long total, long progress);

    private void subscribe() {
        Disposable sub = RxBus.getInstance().doSubscribe(FileDownload.class, new Consumer<FileDownload>() {
            @Override
            public void accept(FileDownload fileDownload) throws Exception {
                updateProgress(fileDownload.getTotal(), fileDownload.getBytesLoaded());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

        RxBus.getInstance().addSubscription(BaseApplication.getInstance(), sub);
    }

    private String destFileDir;
    private String destFileName;

    public void setPathAndName(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    public void saveFile(ResponseBody body) {
        Log.e("saveFile", "开始保存");
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            Log.e("saveFile", "保存结束");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("saveFile", e.getMessage());
        } catch (IOException e) {
            Log.e("saveFile", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                RxBus.getInstance().unSubscribe(BaseApplication.getInstance());
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.e("saveFile", e.getMessage());
            }
        }
    }
}
