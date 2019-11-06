package com.wave.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Wave.Zhang
 * @time 2019/1/22
 * @email 284388431@qq.com
 */
public class RingScaleView extends View {

    //最大360Kg
    public int Max = 360;

    int longWidth = 50, middleWidth = 30, shortWidth = 20;//长刻度，中长刻度,短刻度

    Paint mPaint = new Paint();

    Paint mTextPaint = new Paint();
    Paint mTextPaint2 = new Paint();

    int width,height;

    int viewWidth,viewHeight;

    float rotate;

    double angle;

    int intervalText = 20;

    int interval;

    float maxRotate,minRotate;

    public RingScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_HARDWARE, new Paint());
        init();
    }

    void init() {

        longWidth = dip2px(36);
        middleWidth = dip2px(28);
        shortWidth = dip2px(21);
        intervalText = dip2px(20);
        interval = dip2px(10);

        width = 360 / 2 * 5;
        width = dip2px(width);
        height = width;

        viewWidth = dip2px(300);
        viewHeight = viewWidth;

        int strokeWidth = dip2px(1);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setAntiAlias(true);
        mPaint.setDither(false);

        mTextPaint.setColor(Color.parseColor("#202020"));
        mTextPaint.setTextSize(dip2px(14));

        mTextPaint2.setColor(Color.parseColor("#202020"));
        mTextPaint2.setTextSize(dip2px(8));

        angle = 360.0f / Max;
        // 最小10公斤 // 最大299公斤
        //minRotate = -100;
        //maxRotate = -2990;
        setMinSelectValue(10);
        setMaxSelectValue(200);
        rotate = minRotate;
    }

    public void setMinSelectValue(float value){
        minRotate = 0 - (value * 10);
    }

    public void setMaxSelectValue(float value){
        maxRotate = 0 - (value * 10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = viewWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int rotateX = viewWidth / 2;
        int rotateY = height / 2;

        int translateX = viewWidth / 2;
        int translateY = 0;

        int r = (int)(Math.abs(rotate) / 360);
        //Log.e("onDraw "," 第" + r + "圈 ");
        int startY = 10;
        // 指标图
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ring_scale_point);
        canvas.drawBitmap(bmp,rotateX - bmp.getWidth() / 2,dip2px(71),null);

        for (int i = 0; i < Max; i++) {
            //double angle = 360.0f / Max;
            //mPaint.setColor(Color.parseColor(i == 0 ? "#FF0000" : "#000000"));
            mPaint.setColor(Color.parseColor( "#000000"));
            if (i % 5 == 0) {
                if (i % 10 == 0) {
                    //长刻度
                    canvas.save();
                    canvas.rotate((float) (i * angle) + rotate, rotateX, rotateY);
                    canvas.translate(translateX, translateY);
                    canvas.drawLine(0 , startY, interval, startY, mPaint);
                    canvas.drawLine(0 , startY, 0, startY + longWidth , mPaint);
                    //数字
                    int number = i / 10 ;
                    number = number + (r * 36) ;
                    Rect rect = new Rect();
                    mTextPaint.getTextBounds(number + "", 0, (number + "").length(), rect);
                    int w = rect.width();
                    canvas.drawText( number + "", 0 - w / 2, longWidth + intervalText , mTextPaint);
                    canvas.restore();

                } else {
                    //中长刻度
                    canvas.save();
                    canvas.rotate((float) (i * angle) + rotate, rotateX, rotateY);
                    canvas.translate(translateX, translateY);
                    canvas.drawLine(0, startY, 0, startY + middleWidth, mPaint);
                    canvas.drawLine(0 , startY, interval, startY , mPaint);
                    canvas.restore();
                }
            } else {
                //短刻度
                canvas.save();
                canvas.rotate((float) (i * angle) + rotate, rotateX, rotateY);
                canvas.translate(translateX, translateY);
                canvas.drawLine(0, startY, 0, startY + shortWidth, mPaint);
                canvas.drawLine(0, startY, interval, startY, mPaint);
                canvas.restore();
            }
        }
    }

    public void setValue(float value){
        float rotate = value * 10;
        Log.e("wave"," 体重为: "+rotate);
        setCanvasRotate(0 - rotate);
    }


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue（DisplayMetrics类中属性density）
     * @return
     */
    public int dip2px( float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setCanvasRotate(float rotate){
        if(rotate > minRotate){
            rotate = minRotate;
        }
        if(rotate < maxRotate){
            rotate = maxRotate;
        }
        this.rotate = rotate;
        invalidate();
    }

    float downX;

    float startRotate;

    float startWeight;

    float curWeight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                startRotate = rotate;
                startWeight = Math.abs(startRotate) / 10;
                if(onRingScaleViewChangedListener != null)onRingScaleViewChangedListener.beforeValueChanged(startWeight);

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX() - downX;
                float newRotate = (float)( Math.abs(moveX) / 9f * angle);

//                Log.d("CircleWeightView2","移动像素为:"+moveX);
//                Log.d("CircleWeightView2","移动像素为/180:"+moveX);
//                Log.d("CircleWeightView2","当前角度为:"+rotate);
//                Log.d("CircleWeightView2","滚动角度为:"+newRotate);
                if(moveX > 0){
                    //Log.d("CircleWeightView2","向右滑动了距离为:"+moveX);
                    setCanvasRotate( startRotate + newRotate);
                }else{
                    //Log.d("CircleWeightView2","向左滑动了距离为:"+moveX);
                    setCanvasRotate( startRotate - newRotate);
                }

                curWeight = Math.abs(rotate) / 10;
                if(onRingScaleViewChangedListener != null)onRingScaleViewChangedListener.onValueChanged(startWeight,curWeight);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                downX = 0;
                Log.d("CircleWeightView2","当前体重为:"+Math.abs(this.rotate) / 10);
                curWeight = Math.abs(rotate) / 10;
                if(onRingScaleViewChangedListener != null)onRingScaleViewChangedListener.afterValueChanged(curWeight);

                break;

        }
        return true;
    }

    OnRingScaleViewChangedListener onRingScaleViewChangedListener;

    public void setOnRingScaleViewChangedListener(OnRingScaleViewChangedListener onRingScaleViewChangedListener) {
        this.onRingScaleViewChangedListener = onRingScaleViewChangedListener;
    }

    public interface OnRingScaleViewChangedListener{

        void beforeValueChanged(float value);

        void onValueChanged(float oldValue,float newValue);

        void afterValueChanged(float value);
    }

}
