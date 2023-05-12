package com.example.assignment_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import androidx.annotation.Nullable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class DotsOverlayView extends RelativeLayout {
    private Context context;

    // The list of dot views
    private ArrayList<View> dots = new ArrayList<>();

    public DotsOverlayView(Context context) {
        super(context);
        this.context = context;
    }

    public DotsOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DotsOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void addDot(float x, float y) {
        View dot = new View(context);

        // Create a circular shape drawable and set it as the background of the view
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.RED);
        dot.setBackground(drawable);

        int size = getResources().getDimensionPixelSize(R.dimen.dot_size);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.leftMargin = (int) x - size / 2;
        params.topMargin = (int) y - size / 2;

        dot.setLayoutParams(params);
        this.addView(dot);
        dots.add(dot);
    }

    public void removeDot(int index) {
        if (index >= 0 && index < dots.size()) {
            View dot = dots.get(index);
            removeView(dot);
            dots.remove(index);
        }
    }

    public int getDotAtPosition(float x, float y) {
        for (int i = 0; i < dots.size(); i++) {
            View dot = dots.get(i);
            int dotX = (int) dot.getX() + dot.getWidth() / 2;
            int dotY = (int) dot.getY() + dot.getHeight() / 2;
            if (Math.sqrt(Math.pow(x - dotX, 2) + Math.pow(y - dotY, 2)) <= dot.getWidth() / 2) {
                return i;
            }
        }
        return -1;
    }
}
