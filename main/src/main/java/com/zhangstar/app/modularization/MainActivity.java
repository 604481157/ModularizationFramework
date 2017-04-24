package com.zhangstar.app.modularization;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.zhangstar.app.common.ActionResult;
import com.zhangstar.app.common.RouterRequest;
import com.zhangstar.app.common.manyprocess.application.BaseApplication;
import com.zhangstar.app.common.manyprocess.router.LocalRouter;
import com.zhangstar.app.common.manyprocess.tools.LogUtil;
import com.zhixin.com.process.ProcessLocalRouterService;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.zhangstar.app.modularization.R.layout.activity_main);
        findViewById(com.zhangstar.app.modularization.R.id.begin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LocalRouter.getInstance(BaseApplication.getInstance()).rxRoute(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                            .provider("main")
                            .action("main"))
                            .subscribe(new Consumer<ActionResult>() {
                                @Override
                                public void accept(ActionResult actionResult) throws Exception {
                                    Toast.makeText(MainActivity.this, actionResult.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(com.zhangstar.app.modularization.R.id.begin_process).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LocalRouter.getInstance(BaseApplication.getInstance()).rxRoute(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                            .processName(ProcessLocalRouterService.PROCESS_NAME)
                            .provider("process")
                            .action("process")).subscribe(new Consumer<ActionResult>() {
                        @Override
                        public void accept(ActionResult actionResult) throws Exception {
                            Toast.makeText(MainActivity.this, actionResult.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        findViewById(com.zhangstar.app.modularization.R.id.end_process).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LocalRouter.getInstance(BaseApplication.getInstance()).rxRoute(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                            .processName(ProcessLocalRouterService.PROCESS_NAME)
                            .provider("process")
                            .action("shutdown")).subscribe(new Consumer<ActionResult>() {
                        @Override
                        public void accept(ActionResult actionResult) throws Exception {
                            Toast.makeText(MainActivity.this, actionResult.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("MainActivity", "onDestroy");
    }


}
