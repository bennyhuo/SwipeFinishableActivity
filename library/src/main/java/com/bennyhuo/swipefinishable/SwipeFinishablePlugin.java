package com.bennyhuo.swipefinishable;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

/**
 * Created by benny on 9/22/16.
 */
class SwipeFinishablePlugin {
    private static final String TAG = "SwipeFinishablePlugin";

    private final Activity swipableActivity;
    private final ActivityEx activityX;
    private ActivityRootLayout activityRootLayout;

    SwipeFinishablePlugin(Activity swipableActivity) {
        if(swipableActivity instanceof SwipeFinishable.SwipeFinishableActivity) {
            this.swipableActivity = swipableActivity;
            activityX = new ActivityEx(swipableActivity);
            activityX.convertToTranslucent();
        }else{
            throw new UnsupportedOperationException("Activity passed in is not a instance of SwipeActivity.");
        }
    }

    ActivityRootLayout getDecorView(){
        return activityRootLayout;
    }

    //region animator
    public interface OnTranslationUpdateListener {
        void onUpdate(float progress);
    }

    private OnTranslationUpdateListener onTranslationUpdateListener;

    void setOnTranslationUpdateListener(OnTranslationUpdateListener onTranslationUpdateListener){
        this.onTranslationUpdateListener = onTranslationUpdateListener;
    }

    void setTranslationX(float translationX){
        if(this.onTranslationUpdateListener != null){
            this.onTranslationUpdateListener.onUpdate(translationX);
        }
//        getDecorView().setTranslationX(translationX);
        //getDecorView().offsetLeftAndRight((int) translationX - getDecorView().getLeft());

      getDecorView().setOffset(translationX);
    }

    /**
     * Translate the window directly.
     * Modify window param may cause the window looks not complete on api 23 or lower.
     * @param translationX
     */
    @Deprecated
    void setTranslationXAlt(float translationX){
        WindowManager.LayoutParams params = swipableActivity.getWindow().getAttributes();
        params.x = (int) translationX;
        swipableActivity.getWindow().setAttributes(params);

        int flags = 0;
        if (params.x == 0) {
            flags = FLAG_FULLSCREEN;
        } else {
            flags = FLAG_LAYOUT_NO_LIMITS;
        }
        swipableActivity.getWindow().setFlags(flags, FLAG_FULLSCREEN | FLAG_LAYOUT_NO_LIMITS);
    }

    float getTranslationX(){
        return getDecorView().getOffset();
    }

    @Deprecated
    float getTranslationXAlt(){
        return swipableActivity.getWindow().getAttributes().x;
    }

    int getWidth(){
        return getDecorView().getWidth();
    }

    void onCreate(){
        activityRootLayout = new ActivityRootLayout(swipableActivity);
    }

    private void addBackgroundView(){
        View backgroundView = new View(swipableActivity);
        TypedValue typedValue = new TypedValue();
        swipableActivity.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int color = typedValue.data;
        backgroundView.setBackgroundColor(color);
        activityRootLayout.addView(backgroundView, -1, -1);
    }

    private void setupLayout(){
        ViewGroup rootView = (ViewGroup) swipableActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        if(activityRootLayout.getParent() == rootView) {
            Log.d(TAG, "setupLayout() called, in layout.");
        }else {
            View[] children = new View[rootView.getChildCount()];
            Log.d(TAG, "setupLayout() called = " + rootView.getChildCount());
            for (int i = 0; i < rootView.getChildCount(); i++) {
                children[i] = rootView.getChildAt(i);
            }
            rootView.removeAllViews();
            rootView.addView(activityRootLayout, -1, -1);

            addBackgroundView();
            for (View child : children) {
                activityRootLayout.addView(child);
            }
        }
    }

    void onStart() {
        setupLayout();
        getDecorView().setAtTop(true);
    }

    void onStop() {
        getDecorView().setAtTop(false);
    }

    void finishThisActivity(){
        activityX.finish();
    }
}
