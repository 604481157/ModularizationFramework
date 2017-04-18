package com.zhangstar.app.common.manyprocess.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * <pre>
 *     author : zhangstar
 *     e-mail : 604481157@qq.com
 *     time   : 2017/04/05
 *     desc   :
 * </pre>
 */

public class LoadingDrawable  extends Drawable implements Animatable {
    private final LoadingBuilder mLoadingBuilder;

    LoadingDrawable(LoadingBuilder builder)
    {
        this.mLoadingBuilder = builder;
        this.mLoadingBuilder.setCallback(new Drawable.Callback()
        {
            @Override
            public void invalidateDrawable(Drawable d)
            {
                invalidateSelf();
            }

            @Override
            public void scheduleDrawable(Drawable d, Runnable what, long when)
            {
                scheduleSelf(what, when);
            }

            @Override
            public void unscheduleDrawable(Drawable d, Runnable what)
            {
                unscheduleSelf(what);
            }
        });
    }

    void initParams(Context context)
    {
        if (mLoadingBuilder != null){
            mLoadingBuilder.init(context);
            mLoadingBuilder.initParams(context);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas)
    {
        if (!getBounds().isEmpty()){
            this.mLoadingBuilder.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha)
    {
        this.mLoadingBuilder.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter)
    {
        this.mLoadingBuilder.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void start()
    {
        this.mLoadingBuilder.start();
    }

    @Override
    public void stop()
    {
        this.mLoadingBuilder.stop();
    }

    @Override
    public boolean isRunning()
    {
        return this.mLoadingBuilder.isRunning();
    }

    @Override
    public int getIntrinsicHeight()
    {
        return (int) this.mLoadingBuilder.getIntrinsicHeight();
    }

    @Override
    public int getIntrinsicWidth()
    {
        return (int) this.mLoadingBuilder.getIntrinsicWidth();
    }
}
