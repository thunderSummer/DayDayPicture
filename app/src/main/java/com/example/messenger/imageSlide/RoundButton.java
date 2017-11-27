package com.oureda.thunder.daydaypicture.imageSlide;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.oureda.thunder.daydaypicture.R;


/**
 * Created by thunder on 17-4-15.
 */

public class RoundButton extends View {
    private int radius;
    private String content;
    private int lineColor;
    private int textSize;
    private Paint mPaint;
    private Rect bound;
    private int mWidth;
    private int mHeight;
    private int lineWidth;




    public RoundButton(Context context) {
        super(context);
    }

    public RoundButton(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public RoundButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundButton,defStyleAttr,0);
        int n = typedArray.getIndexCount();
        for(int i= 0;i<n;i++){
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.RoundButton_lineColor:
                    lineColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RoundButton_textSize:
                    textSize = typedArray.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RoundButton_text:
                    content = typedArray.getString(attr);
                    break;
                case R.styleable.RoundButton_radius:
                    radius=typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RoundButton_lineWidth:
                    lineWidth=typedArray.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics()));

            }
        }
        mPaint = new Paint();
        mPaint.setTextSize(textSize);
        mPaint.setColor(lineColor);

        bound = new Rect();
        //获取文字高度大小
        mPaint.getTextBounds(content,0,content.length(),bound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if(specMode==MeasureSpec.EXACTLY){
            mWidth=specSize;

        }else{
            if(specMode==MeasureSpec.AT_MOST){
                mPaint.getTextBounds(content,0,content.length(),bound);
                mWidth=+bound.width()+radius*2+lineWidth;
                Log.d("s", "onMeasure: "+mWidth);
            }
        }
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            mHeight = specSize;
        }else{
            if(specMode==MeasureSpec.AT_MOST){
                mHeight =+radius*2+bound.height();
            }
        }
        setMeasuredDimension((int) (mWidth*1.2f), (int) (mHeight*1.6f));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(" ddd ", "onDraw: "+content);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(3);
        Log.d("ss", "onDraw: "+mWidth+mHeight+radius);
        RectF rectf = new RectF(mWidth*0.1f,mHeight*0.3f,mWidth*1.1f,mHeight*1.3f);

     //   canvas.drawRect(rectf,mPaint);
        canvas.drawText(content,mWidth*0.6f  - bound.width() * 1.0f / 2, mHeight*0.8f+bound.height()*0.3f,mPaint);
        mPaint.setStrokeWidth(10);
        canvas.drawRoundRect(rectf,radius,radius,mPaint);
    }
    public void setRadius(int radius) {
        this.radius = radius;
        requestLayout();
    }

    public void setContent(String content) {
        this.content = content;
        requestLayout();

    }
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        requestLayout();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        requestLayout();
    }
}
