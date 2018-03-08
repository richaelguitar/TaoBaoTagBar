package com.richaelguitar.taobaotagbar.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by richaelguitar on 2018/2/25.
 */

public class TagView extends AppCompatTextView {

    private Paint mPaint;

    private float strokeWidth = 15;

    private int defalutPaintColor =Color.TRANSPARENT;

    private int selectedPaintColor ;

    private boolean isSelected;

    public TagView(Context context) {
        this(context,null);
    }

    public TagView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public TagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(strokeWidth);
    }

    public void setTagSelected(boolean isSelected,int color){
        this.selectedPaintColor = color;
        this.isSelected = isSelected;
        setTextColor(color);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if(isSelected){
            mPaint.setColor(selectedPaintColor);
        }else{
            mPaint.setColor(defalutPaintColor);
        }
        canvas.drawLine(0,getHeight(),getWidth(),getHeight(),mPaint);
        canvas.restore();
    }
}
