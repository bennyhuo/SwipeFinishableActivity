package com.bennyhuo.swipefinishable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.bennyhuo.swipefinishable.SwipeFinishable.State;


/**
 * Created by benny on 9/17/16.
 */
public class ActivityRootLayout extends FrameLayout {
    public static final String TAG = "ActivityRootLayout";

    private Drawable shadow;

    public ActivityRootLayout(Context context) {
        super(context);
        init();
    }

    public ActivityRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActivityRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        shadow = getResources().getDrawable(R.drawable.shadow_left);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        SwipeFinishable.INSTANCE.dispatchTouchEvent(ev);
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return SwipeFinishable.INSTANCE.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return SwipeFinishable.INSTANCE.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isAtTop && SwipeFinishable.INSTANCE.getState() != State.IDLE) {
            drawShadow(canvas);
            drawCover(canvas);
        }
    }

    private void drawCover(Canvas canvas) {
        final float offset = getOffset();
        final int alpha = (int) (200 * (1 - offset / getWidth()));
        canvas.clipRect(0, 0, offset, getHeight());
        canvas.drawColor(Color.argb(alpha, 0, 0, 0));
    }

    private void drawShadow(Canvas canvas) {
        final float offset = getOffset();
        canvas.save();
        canvas.translate(offset, 0);
        shadow.setBounds( - shadow.getIntrinsicWidth(), getTop(), 0, getBottom());
        shadow.setAlpha((int) (255 * (1 - offset / getWidth())));
        shadow.draw(canvas);
        canvas.restore();
    }

    public void setOffset(float offset) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setTranslationX(offset);
        }
        invalidate();
    }

    public float getOffset() {
        return getChildAt(0).getTranslationX();
    }

    private boolean isAtTop = false;

    public void setAtTop(boolean isAtTop) {
        this.isAtTop = isAtTop;
    }

}
