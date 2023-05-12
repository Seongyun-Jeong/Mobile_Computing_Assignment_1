package com.example.assignment_1;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import android.graphics.drawable.GradientDrawable;

public class DotsOverlayView extends RelativeLayout {

    private class Dot {
        private View view;
        private float x;
        private float y;

        public Dot(View view, float x, float y) {
            this.view = view;
            this.x = x;
            this.y = y;
        }

        public View getView() {
            return view;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }

    private class BlueDot {
        private View view;
        private float x;
        float y;

        public BlueDot(View view, float x, float y) {
            this.view = view;
            this.x = x;
            this.y = y;
        }

        public View getView() {
            return view;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    private View blueDot = null;
    private Context context;

    // The list of dot views
    private ArrayList<Dot> dots = new ArrayList<>();

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
        View dotView = new View(context);

        // Create a circular shape drawable and set it as the background of the view
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.RED);
        dotView.setBackground(drawable);

        int size = getResources().getDimensionPixelSize(R.dimen.dot_size);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.leftMargin = (int) x - size / 2;
        params.topMargin = (int) y - size / 2;

        dotView.setLayoutParams(params);
        this.addView(dotView);

        Dot dot = new Dot(dotView, x, y);
        dots.add(dot);
        if (context instanceof MainActivity) {
            ((MainActivity) context).setLastDotIndex(dots.size() - 1);
        }
    }

    public void removeDot(int index) {
        if (index >= 0 && index < dots.size()) {
            Dot dot = dots.get(index);
            removeView(dot.getView());
            dots.remove(index);
        }
    }

    public int getDotAtPosition(float x, float y) {
        for (int i = 0; i < dots.size(); i++) {
            Dot dot = dots.get(i);
            int dotX = (int) dot.getX() + dot.getView().getWidth() / 2;
            int dotY = (int) dot.getY() + dot.getView().getHeight() / 2;
            if (Math.sqrt(Math.pow(x - dotX, 2) + Math.pow(y - dotY, 2)) <= dot.getView().getWidth() / 2) {
                return i;
            }
        }
        return -1;
    }

    public int getDotCount() {
        return dots.size();
    }

    public float getDotX(int index) {
        if (index >= 0 && index < dots.size()) {
            return dots.get(index).getX();
        }
        return -1;
    }

    public float getDotY(int index) {
        if (index >= 0 && index < dots.size()) {
            return dots.get(index).getY();
        }
        return -1;
    }

    public void addOrUpdateBlueDot(float x, float y) {
        int size = getResources().getDimensionPixelSize(R.dimen.dot_size);

        if (blueDot == null) {
            blueDot = new View(context);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(Color.BLUE);
            blueDot.setBackground(drawable);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            blueDot.setLayoutParams(params);
            this.addView(blueDot);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) blueDot.getLayoutParams();
        params.leftMargin = (int) x - size / 2;
        params.topMargin = (int) y - size / 2;
        blueDot.setLayoutParams(params);
    }

    public void removeBlueDot() {
        if (blueDot != null) {
            this.removeView(blueDot);
            blueDot = null;
        }
    }

}