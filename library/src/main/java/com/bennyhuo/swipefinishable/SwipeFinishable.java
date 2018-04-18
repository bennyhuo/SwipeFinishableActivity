package com.bennyhuo.swipefinishable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Interpolator;

import com.bennyhuo.swipefinishable.utils.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by benny on 9/17/16.
 */
public final class SwipeFinishable {
    public static final String TAG = "ActivityController";

    private static final int Y_THRESHOLD = 10;
    private static final int X_THRESHOLD = 10;
    private static final int X_EVENT_AREA = 80;
    private static final long MAX_DURATION =500;
    private static final long MIN_DURATION =100;

    public final static SwipeFinishable INSTANCE = new SwipeFinishable();

    private boolean swipeLastActivity = true;
    private VelocityTracker velocityTracker;
    private Stack<Activity> activities = new Stack<>();
    private HashMap<Activity, SwipeFinishablePlugin> pluginMap = new HashMap<>();

    private Application application;

    private SwipeFinishable(){

    }

    public interface SwipeFinishableActivity {
        void finishThisActivity();
    }

    public enum State{
        IDLE,
        SETTLING,
        DRAGGING
    }

    private State state = State.IDLE;

    private void setState(State state){
        if(this.state == state) return ;
        this.state = state;
    }

    public void setSwipeLastActivity(boolean enabled){
        this.swipeLastActivity = enabled;
    }

    public State getState(){
        return state;
    }

    private TouchEventInterceptor interceptor = new TouchEventInterceptor() {

        boolean isAbandon = false;
        float downX, downY;
        float x = 0;

        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent event) {
            if(canSwipe()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        isAbandon = downX > DensityUtil.dip2px(application, X_EVENT_AREA);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float cx = event.getRawX();
                        float cy = event.getRawY();
                        if (isAbandon) {
                            return false;
                        } else if (state == State.DRAGGING) {
                            return true;
                        } else if (Math.abs(cy - downY) > DensityUtil.dip2px(application, Y_THRESHOLD)) {
                            isAbandon = true;
                        } else if (Math.abs(cx - downX) > DensityUtil.dip2px(application, X_THRESHOLD)) {
                            setState(State.DRAGGING);
                            x = cx;
                            return true;
                        }
                        break;
                }
            }
            return false;
        }

        @Override
        public boolean onTouch(MotionEvent event) {
            if(state == State.DRAGGING) {
                recordEvent(event);
                SwipeFinishablePlugin swipeFinishablePlugin = pluginMap.get(getCurrentActivity());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float cx = event.getRawX();
                        float cy = event.getRawY();
                        float newTranslationX = swipeFinishablePlugin.getTranslationX() + cx - x;
                        newTranslationX = newTranslationX < 0 ? 0 : newTranslationX;
                        swipeFinishablePlugin.setTranslationX(newTranslationX);
                        x = cx;
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (swipeFinishablePlugin.getTranslationX() > swipeFinishablePlugin.getWidth() * 0.3f) {
                            finishCurrentActivity();
                        } else {
                            settleCurrentActivity();
                        }
                        return true;
                }
            }else{
                return shouldInterceptTouchEvent(event);
            }
            return false;
        }

        @Override
        public boolean onDispatchTouchEvent(MotionEvent event) {
            return false;
        }
    };

    private boolean canSwipe(){
        if(state != State.IDLE) return false;
        Activity currentActivity = getCurrentActivity();
        if(currentActivity == null) return false;
        if(!(currentActivity instanceof SwipeFinishableActivity)) return false;
        Activity groundActivity = getGroundActivity();
        if(groundActivity == null) return false;
        if(!swipeLastActivity && currentActivity == groundActivity) return false;
        return true;
    }

    private void recordEvent(MotionEvent event){
        if(velocityTracker == null){
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    private float retrieveVelocityX(){
        if(velocityTracker == null) return 0;
        velocityTracker.computeCurrentVelocity(1);
        float velocityX = velocityTracker.getXVelocity();
        velocityTracker.recycle();
        velocityTracker = null;
        return velocityX;
    }

    private ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activities.push(activity);
            Log.d(TAG, "onActivityCreated() called with: activity = [" + activity + "], savedInstanceState = [" + savedInstanceState + "]");

            Activity currentActivity = getCurrentActivity();
            if(currentActivity instanceof SwipeFinishableActivity) {
                final SwipeFinishablePlugin baseActivity = new SwipeFinishablePlugin(currentActivity);
                pluginMap.put(currentActivity, baseActivity);
            }

            if (bindView()) {
                final SwipeFinishablePlugin baseActivity = pluginMap.get(currentActivity);
                baseActivity.onCreate();
                baseActivity.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        baseActivity.setTranslationX(baseActivity.getWidth());
                        final ObjectAnimator animator = ObjectAnimator.ofFloat(baseActivity, "translationX", baseActivity.getWidth(), 0);
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                animator.removeListener(this);
                                setState(State.IDLE);
                            }
                        });
                        animator.start();
                        setState(State.SETTLING);
                        baseActivity.getDecorView().removeOnLayoutChangeListener(this);
                    }
                });
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivityStarted() called with: activity = [" + activity + "]");
            if(activity instanceof SwipeFinishableActivity) {
                final SwipeFinishablePlugin baseActivity = pluginMap.get(activity);
                baseActivity.onStart();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped() called with: activity = [" + activity + "]");
            if(activity instanceof SwipeFinishableActivity) {
                final SwipeFinishablePlugin baseActivity = pluginMap.get(activity);
                baseActivity.onStop();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(TAG, "onActivitySaveInstanceState() called with: activity = [" + activity + "], outState = [" + outState + "]");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activities.remove(activity);
            pluginMap.remove(activity);
            Log.d(TAG, "onActivityDestroyed() called with: activity = [" + activity + "]");
            bindView();
        }
    };

    private boolean bindView(){
        Activity activity = getCurrentActivity();
        final SwipeFinishablePlugin currentPlugin;
        if(activity != null && activity instanceof SwipeFinishableActivity) {
            currentPlugin = pluginMap.get(activity);
            currentPlugin.setOnTranslationUpdateListener(null);
        }else{
            return false;
        }
        activity = getGroundActivity();
        if(activity == null) {
            return false;
        }
        if(activity instanceof SwipeFinishableActivity) {
            SwipeFinishablePlugin groundPlugin = pluginMap.get(activity);
            groundPlugin.setOnTranslationUpdateListener(null);
            if(currentPlugin == groundPlugin) return false;
        }
        final Activity groundActivity = activity;
        currentPlugin.setOnTranslationUpdateListener(new SwipeFinishablePlugin.OnTranslationUpdateListener() {
            @Override
            public void onUpdate(float translationX) {
                if(groundActivity instanceof SwipeFinishableActivity) {
                    pluginMap.get(groundActivity).setTranslationX(0.5f * (translationX - currentPlugin.getWidth()));
                }else{
                    groundActivity.getWindow().getDecorView().setTranslationX(0.5f * (translationX - currentPlugin.getWidth()));
                }
            }
        });
        return true;
    }

    public Activity getCurrentActivity() {
        if (activities.isEmpty()) return null;
        return activities.peek();
    }

    public Activity getGroundActivity() {
        if (activities.size() > 1) {
            return activities.get(activities.size() - 2);
        } else if (activities.size() == 1) {
            return activities.peek();
        } else {
            return null;
        }
    }

    public void init(Application application) {
        this.application = application;
        addTouchEventInterceptor(interceptor);
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    public void startActivity(Intent intent){
        getCurrentActivity().startActivity(intent);
        getCurrentActivity().overridePendingTransition(0, 0);
    }

    public void finishCurrentActivity(){
        float velocityX = retrieveVelocityX();
        final Activity activity = getCurrentActivity();
        if(!(activity instanceof SwipeFinishableActivity)){
            activity.finish();
            return;
        }

        final SwipeFinishablePlugin swipeFinishablePlugin = pluginMap.get(activity);
        if(!swipeLastActivity && activities.size() == 1){
            /* >benny: [16-09-17 23:19] Only one left, finish it without transition. */
            swipeFinishablePlugin.finishThisActivity();
        }else{
            final Animator animator = ObjectAnimator.ofFloat(swipeFinishablePlugin, "translationX", swipeFinishablePlugin.getTranslationX(), swipeFinishablePlugin.getWidth());
            long duration = Math.max(Math.min((long) Math.abs((swipeFinishablePlugin.getWidth() - swipeFinishablePlugin.getTranslationX()) * 2 / velocityX), MAX_DURATION), MIN_DURATION);
            animator.setDuration(duration);
            animator.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    return - input * input + 2 * input;
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    swipeFinishablePlugin.finishThisActivity();
                    activity.overridePendingTransition(0, 0);
                    animator.removeListener(this);
                    setState(State.IDLE);
                }
            });
            animator.start();
            setState(State.SETTLING);
        }
    }

    private void settleCurrentActivity(){
        float velocityX = retrieveVelocityX();
        final Activity activity = getCurrentActivity();
        if(!(activity instanceof SwipeFinishableActivity)){
            return;
        }
        SwipeFinishablePlugin swipeFinishablePlugin = pluginMap.get(getCurrentActivity());
        final ObjectAnimator animator = ObjectAnimator.ofFloat(swipeFinishablePlugin, "translationX", swipeFinishablePlugin.getTranslationX(), 0);
        long duration = Math.max(Math.min((long) Math.abs(swipeFinishablePlugin.getTranslationX() * 2 / velocityX), MAX_DURATION), MIN_DURATION);
        animator.setDuration(duration);
        animator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return - input * input + 2 * input;
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator.removeListener(this);
                setState(State.IDLE);
            }
        });
        animator.start();
        setState(State.SETTLING);
    }


    private ArrayList<TouchEventInterceptor> touchEventInterceptors = new ArrayList<>();

    public void addTouchEventInterceptor(TouchEventInterceptor touchEventInterceptor) {
        if (!this.touchEventInterceptors.contains(touchEventInterceptor))
            this.touchEventInterceptors.add(touchEventInterceptor);
    }

    public void removeTouchEventInterceptor(TouchEventInterceptor touchEventInterceptor) {
        this.touchEventInterceptors.remove(touchEventInterceptor);
    }

    private TouchEventInterceptor intercepted = null;

    boolean dispatchTouchEvent(MotionEvent event) {
        ArrayList<TouchEventInterceptor> touchEventInterceptors = (ArrayList<TouchEventInterceptor>) this.touchEventInterceptors.clone();
        for (TouchEventInterceptor touchEventInterceptor : touchEventInterceptors) {
            if (touchEventInterceptor.onDispatchTouchEvent(event)) {
                return true;
            }
        }
        return false;
    }

    boolean onTouchEvent(MotionEvent event) {
        /* >benny: [16-09-17 20:43] If intercepted, pass the event to the intercepted only. */
        if(intercepted != null) {
            boolean result = intercepted.onTouch(event);
            if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                intercepted = null;
            }
            return result;
        }
        ArrayList<TouchEventInterceptor> touchEventInterceptors = (ArrayList<TouchEventInterceptor>) this.touchEventInterceptors.clone();
        for (TouchEventInterceptor touchEventInterceptor : touchEventInterceptors) {
            if (touchEventInterceptor.onTouch(event)) {
                return true;
            }
        }
        return false;
    }

    boolean shouldInterceptTouchEvent(MotionEvent event) {
        /* >benny: [16-09-17 20:42] If intercepted, won't be called here. */
        if(intercepted != null) return true;
        ArrayList<TouchEventInterceptor> touchEventInterceptors = (ArrayList<TouchEventInterceptor>) this.touchEventInterceptors.clone();
        for (TouchEventInterceptor touchEventInterceptor : touchEventInterceptors) {
            if (touchEventInterceptor.shouldInterceptTouchEvent(event)) {
                intercepted = touchEventInterceptor;
                return true;
            }
        }
        return false;
    }
}
