package com.songbai.futurex.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.songbai.futurex.R;
import com.songbai.futurex.model.KTrend;
import com.songbai.futurex.utils.Display;

import java.util.List;

public class TimeShareSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint mPaint;
    private Paint mShaderPaint;
    private float baseArea;
    private Path mPath;
    private CustomThread customThread;

    private SurfaceHolder surfaceHolder;

    public TimeShareSurfaceView(Context context) {
        this(context, null);

    }

    public TimeShareSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public TimeShareSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//消除锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));

        mShaderPaint = new Paint();
        mShaderPaint.setAntiAlias(true);
        mShaderPaint.setStyle(Paint.Style.FILL);
//        mShaderPaint.setColor(ContextCompat.getColor(getContext(), R.color.green));
//        mShader = new LinearGradient(0, 0, 0, 100, new int[]{ContextCompat.getColor(getContext(), R.color.white), ContextCompat.getColor(getContext(), R.color.alphaGreen)}, null, Shader.TileMode.CLAMP);
//        mShaderPaint.setShader(mShader);
        mShaderPaint.setShader(new LinearGradient(0, 0, 0, Display.dp2Px(52, getResources()), new int[]{ContextCompat.getColor(getContext(), R.color.white_20), ContextCompat.getColor(getContext(), R.color.alphaGreen)}, null, Shader.TileMode.CLAMP));

        baseArea = Display.dp2Px(12, getResources());
        mPath = new Path();
        //获取对象实例
        surfaceHolder = this.getHolder();
        // 给SurfaceView添加回调
        surfaceHolder.addCallback(this);
        //创建工作线程
        customThread = new CustomThread(surfaceHolder);

    }

    //创建的时候调用
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //工作线程开始工作
        if (!customThread.canRun) {
            customThread.start();
        }
        customThread.canRun = true;

        Log.d("-->surfaceCreated", "surfaceCreated");
    }

    //发生改变的时候调用
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.d("-->surfaceChanged", "surfaceChanged");
    }

    //销毁时的时候调用
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        customThread.canRun = false;
        Log.d("-->surfaceDestroyed", "surfaceDestroyed");

    }

    public void updateData(List<KTrend> mKTrends) {

    }

    //工作线程
    class CustomThread extends Thread {
        private SurfaceHolder surfaceHolder;
        public boolean canRun;

        public CustomThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            canRun = true;
        }

        @Override
        public void run() {

            while (canRun) {
                Canvas canvas = null;
                //线程同步
                synchronized (surfaceHolder) {
                    //创建画笔
                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
                    paint.setTextSize(200);
                    // 锁定画布(想一下,类似Bitmap的效果)
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        //画背景
                        canvas.drawColor(Color.WHITE);
                        //画字
                        canvas.drawText("妈蛋", 100, 300, paint);
                    }
                }

                if (canvas != null) {
                    // 结束锁定
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }

            }

        }
    }
}
