package com.jimmy.jimmy_jbox_demo;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class WorldBox extends FrameLayout {

    private JBoxImpl jBoxImpl;

    public WorldBox(@NonNull Context context) {
        this(context, null);
    }

    public WorldBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorldBox(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        jBoxImpl = new JBoxImpl(context.getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        jBoxImpl.setWorldSize(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        jBoxImpl.createWorld();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (changed || jBoxImpl.isBodyView(child)) {
                jBoxImpl.createBody(child);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        jBoxImpl.startWorld();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (jBoxImpl.isBodyView(child)){
                child.setX(jBoxImpl.getViewX(child));
                child.setY(jBoxImpl.getViewY(child));
                child.setRotation(jBoxImpl.getViewRotation(child));
            }
        }
        invalidate();

    }


    public void onSensorChanged(float x, float y) {
        int childCount = getChildCount();
        for (int i =0; i< childCount; i++) {
            View view = getChildAt(i);
            if (jBoxImpl.isBodyView(view)) {
                jBoxImpl.applyLinearImpulse(x,y,view);
            }
        }
    }
}
