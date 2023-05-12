package com.example.assignment_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;



public class TouchImageView extends androidx.appcompat.widget.AppCompatImageView {

    private float xTouch = -1;
    private float yTouch = -1;

    public TouchImageView(Context context) {
        super(context);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (xTouch != -1 && yTouch != -1) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawCircle(xTouch, yTouch, 20, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                xTouch = event.getX();
                yTouch = event.getY();
                invalidate();
                return true;
        }
        return false;
    }

    public float getTouchX() {
        return xTouch;
    }

    public float getTouchY() {
        return yTouch;
    }





}

