package com.hummer.core.component;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_METHOD;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.Hummer;
import com.hummer.core.common.ILifeCycle;
import com.hummer.core.component.anim.HMBasicAnimation;
import com.hummer.core.component.view.ViewBackgroundManager;
import com.hummer.core.event.HMEventCollection;
import com.hummer.core.event.HMEventManager;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.base.GestureUtils;
import com.hummer.core.event.base.IBaseEvent;
import com.hummer.core.jni.JSValue;
import com.hummer.core.layout.HMDomNode;
import com.hummer.core.utility.HMConverter;
import com.hummer.core.utility.HMSpacing;
import com.facebook.yoga.android.YogaLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author: linjizong
 * @date: 2019/4/23
 * @desc:
 */
public abstract class HMBase<T extends View> implements ILifeCycle {
    private T mTargetView;
    private HMDomNode mNode;
    private Map<String, HMBasicAnimation> animMap = new HashMap<>();
    private ViewBackgroundManager viewBackgroundManager;
    protected HMEventManager mEventManager;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private MotionEvent mLatestMotionEvent;
    protected JSValue mAssociatedJSValue;


    public HMBase(Context context, @Nullable JSValue[] args) {
        mTargetView = createView(context);
        mNode = HMDomNode.nodeForView(this, args);
        viewBackgroundManager = new ViewBackgroundManager(getView());
    }

    @CallSuper
    @Override
    public void onCreate() {
        mEventManager = new HMEventManager();
        mEventManager.onCreate();

        initViewGestureEvent();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        mEventManager.onDestroy();

        if (mTargetView != null) {
            mTargetView.setOnTouchListener(null);
            mTargetView.setOnClickListener(null);
        }
        mGestureDetector = null;
    }

    public final void destroy() {
        onDestroy();

        mTargetView = null;

        if (mNode != null) {
            mNode.reset();
            mNode = null;
        }
        animMap.clear();
        viewBackgroundManager.destroy();
        mAssociatedJSValue = null;
    }

    private final T createView(Context context) {
        T view = createViewInstance(context);
        if (view == null) {
            throw new RuntimeException("createViewInstance must return a view");
        }
        return view;
    }

    protected abstract T createViewInstance(Context context);

    public T getView() {
        return mTargetView;
    }

    public HMDomNode getDomNode() {
        return mNode;
    }

    public void setAssociatedJSValue(JSValue associatedJSValue) {
        mAssociatedJSValue = associatedJSValue;
    }

    @HM_EXPORT_PROPERTY("style")
    public HashMap style;

    public void setStyle(JSValue value) {
        HashMap style = (HashMap) value.toObject();
        this.style = style;
        getDomNode().setDomStyle(style);
        onStyleUpdated(style);
    }

    public void onStyleUpdated(HashMap newStyle) {}

    @HM_EXPORT_PROPERTY("viewID")
    public String viewID;

    public void setViewID(String viewID) {
        this.viewID = viewID;
        getDomNode().nodeID = viewID;
    }

    public String getViewID() {
        return getDomNode().nodeID;
    }

    /**
     * 是否不响应交互事件
     */
    @HM_EXPORT_PROPERTY("enabled")
    public boolean enabled;

    public void setEnabled(JSValue enabled) {
        this.enabled = enabled.toBoolean();
        getView().setEnabled(this.enabled);
    }

    public boolean getEnabled() {
        return enabled;
    }

    @HM_EXPORT_METHOD("addEventListener")
    public void addEventListener(JSValue eventName, JSValue callback) {
        mEventManager.addEventListener(
                eventName.toCharString(), callback);
    }

    @HM_EXPORT_METHOD("removeEventListener")
    public void removeEventListener(JSValue eventName, JSValue callback) {
        mEventManager.removeEventListener(
                eventName.toCharString(), callback);
    }

    private void initViewGestureEvent() {
        if (!mTargetView.getClass().getName().equals(YogaLayout.class.getName())) {
            return;
        }
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {}

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mEventManager.dispatchEvent(HMBaseEvent.HM_CLICK_EVENT_NAME, jsContext -> {

                    HMEventCollection collection = HMEventCollection.getInstance();
                    Class clazz = collection.classWithEventName(HMBaseEvent.HM_CLICK_EVENT_NAME);
                    JSValue tapEventJS = Hummer.getInstance().valueWithClass(clazz, jsContext);

                    IBaseEvent event = (IBaseEvent) tapEventJS.toObject();
                    event.setType(HMBaseEvent.HM_CLICK_EVENT_NAME);
                    event.setState(GestureUtils.findStateInMotionEvent(e));
                    event.setTarget(mAssociatedJSValue);

                    event.configWithData(new HashMap<String, Object>(){
                        {
                            put("x", e.getX());
                            put("y", e.getY());
                        }
                    });
                    return tapEventJS;
                });
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mEventManager.dispatchEvent(HMBaseEvent.HM_PAN_EVENT_NAME, jsContext -> {

                    HMEventCollection collection = HMEventCollection.getInstance();
                    Class clazz = collection.classWithEventName(HMBaseEvent.HM_PAN_EVENT_NAME);
                    JSValue panEventJS = Hummer.getInstance().valueWithClass(clazz, jsContext);

                    IBaseEvent event = (IBaseEvent) panEventJS.toObject();
                    event.setType(HMBaseEvent.HM_PAN_EVENT_NAME);
                    if (e1.getAction() == MotionEvent.ACTION_DOWN) {
                        event.setState(HMBaseEvent.HM_GESTURE_STATE_BEGAN);
                    }

                    if (e2.getAction() == MotionEvent.ACTION_UP) {
                        event.setState(HMBaseEvent.HM_GESTURE_STATE_ENDED);
                    } else if (e2.getAction() == MotionEvent.ACTION_CANCEL) {
                        event.setState(HMBaseEvent.HM_GESTURE_STATE_CANCELLED);
                    }
                    event.setTarget(mAssociatedJSValue);
                    event.configWithData(new HashMap<String, Object>(){
                        {
                            put("deltaX", distanceX);
                            put("deltaY", distanceY);
                        }
                    });

                    return panEventJS;
                });
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                mEventManager.dispatchEvent(HMBaseEvent.HM_LONG_PRESS_EVENT_NAME, jsContext -> {
                    HMEventCollection collection = HMEventCollection.getInstance();
                    Class clazz = collection.classWithEventName(HMBaseEvent.HM_LONG_PRESS_EVENT_NAME);
                    JSValue longPressEventJS = Hummer.getInstance().valueWithClass(clazz, jsContext);

                    IBaseEvent event = (IBaseEvent) longPressEventJS.toObject();
                    event.setType(HMBaseEvent.HM_LONG_PRESS_EVENT_NAME);
                    event.setState(GestureUtils.findStateInMotionEvent(e));
                    event.setTarget(mAssociatedJSValue);

                    event.configWithData(new HashMap<String, Object>(){
                        {
                            put("x", e.getX());
                            put("y", e.getY());
                        }
                    });

                    return longPressEventJS;
                });
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                mEventManager.dispatchEvent(HMBaseEvent.HM_SWIPE_EVENT_NAME, jsContext -> {

                    HMEventCollection collection = HMEventCollection.getInstance();
                    Class clazz = collection.classWithEventName(HMBaseEvent.HM_SWIPE_EVENT_NAME);
                    JSValue swipeEventJS = Hummer.getInstance().valueWithClass(clazz, jsContext);

                    IBaseEvent event = (IBaseEvent) swipeEventJS.toObject();
                    event.setType(HMBaseEvent.HM_SWIPE_EVENT_NAME);

                    event.setState(HMBaseEvent.HM_GESTURE_STATE_CHANGED);
                    if (e1.getAction() == MotionEvent.ACTION_DOWN) {
                        event.setState(HMBaseEvent.HM_GESTURE_STATE_BEGAN);
                    }
                    if (e2.getAction() == MotionEvent.ACTION_UP) {
                        event.setState(HMBaseEvent.HM_GESTURE_STATE_ENDED);
                    } else if (e2.getAction() == MotionEvent.ACTION_CANCEL) {
                        event.setState(HMBaseEvent.HM_GESTURE_STATE_CANCELLED);
                    }

                    event.setTarget(mAssociatedJSValue);

                    event.configWithData(new HashMap<String, Object>(){
                        {
                            put("beginX", e1.getX());
                            put("beginY", e1.getY());
                            put("endX", e2.getX());
                            put("endY", e2.getY());
                            put("velocityX", velocityX);
                            put("velocityY", velocityY);
                        }
                    });

                    return swipeEventJS;
                });
                return false;
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                // 控制缩放的最大值
                final float scale = Math.max(0.1f, Math.min(detector.getScaleFactor(), 5.0f));;

                mEventManager.dispatchEvent(HMBaseEvent.HM_PINCH_EVENT_NAME, jsContext -> {

                    HMEventCollection collection = HMEventCollection.getInstance();
                    Class clazz = collection.classWithEventName(HMBaseEvent.HM_PINCH_EVENT_NAME);
                    JSValue pinchEventJS = Hummer.getInstance().valueWithClass(clazz, jsContext);

                    IBaseEvent event = (IBaseEvent) pinchEventJS.toObject();
                    event.setType(HMBaseEvent.HM_PINCH_EVENT_NAME);
                    event.setState(GestureUtils.findStateInMotionEvent(mLatestMotionEvent));

                    event.configWithData(new HashMap<String, Object>(){
                        {
                            put("scale", scale);
                        }
                    });

                    return pinchEventJS;
                });
                return true;
            }
        });

        mTargetView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLatestMotionEvent = event;
                mScaleGestureDetector.onTouchEvent(event);
                mGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @HM_EXPORT_METHOD("addAnimation")
    public void addAnimation(JSValue anim, JSValue id) {
        HMBasicAnimation animation = (HMBasicAnimation) anim.toObject();
        animMap.put(id.toCharString(), animation);
        animation.start(getView());
    }

    @HM_EXPORT_METHOD("removeAnimationForKey")
    public void removeAnimationForKey(JSValue id) {
        if (animMap.containsKey(id.toCharString())) {
            HMBasicAnimation animation = animMap.get(id.toCharString());
            animation.stop();
            animMap.remove(id.toCharString());
        }
    }

    @HM_EXPORT_METHOD("removeAllAnimation")
    public void removeAllAnimation() {
        Iterator<Map.Entry<String, HMBasicAnimation>> iterator = animMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, HMBasicAnimation> entry = iterator.next();
            HMBasicAnimation animation = entry.getValue();
            animation.stop();
            iterator.remove();
        }
    }

    public static final String VISIBILITY_VISIBLE = "visible";
    public static final String VISIBILITY_HIDDEN = "hidden";
    @HM_EXPORT_ATTR("visibility")
    public void setVisibility(String hidden) {
        getView().setVisibility(
                VISIBILITY_HIDDEN.equals(hidden) ? View.INVISIBLE : View.VISIBLE);
    }

    public static final String DISPLAY_INLINE = "inline";
    public static final String DISPLAY_NONE = "none";
    @HM_EXPORT_ATTR("display")
    public void setDisplay(String hidden) {
        getView().setVisibility(
                DISPLAY_NONE.equals(hidden) ? View.GONE : View.VISIBLE);
    }


    @HM_EXPORT_ATTR("backgroundColor")
    public void setBackgroundColor(Object color) {
        if (color instanceof String) {
            viewBackgroundManager.setBackgroundColor(Integer.parseInt((String) color));
        } else if (color instanceof int[]) {
            int[] array = (int[]) color;
            int deg = array[0];
            int[] colors = Arrays.copyOfRange(array, 1, array.length);
            viewBackgroundManager.setBackgroundGradientColor(deg, colors);
        }
    }

    @HM_EXPORT_ATTR("backgroundImage")
    public void setBackgroundImage(String image) {
        viewBackgroundManager.setBackgroundImage(image);
    }

    @HM_EXPORT_ATTR("borderWidth")
    public void setBorderWidth(float width) {
        viewBackgroundManager.setBorderWidth(HMSpacing.ALL, width);
    }

    @HM_EXPORT_ATTR("borderColor")
    public void setBorderColor(int color) {
        viewBackgroundManager.setBorderColor(HMSpacing.ALL, color, color >>> 24);
    }

    @HM_EXPORT_ATTR("borderRadius")
    public void setBorderRadius(float borderRadius) {
        viewBackgroundManager.setBorderRadius(borderRadius);
    }

    @HM_EXPORT_ATTR("borderStyle")
    public void setBorderStyle(String style) {
        if (style == null) return;
        viewBackgroundManager.setBorderStyle(style);
    }

    @HM_EXPORT_ATTR("shadow")
    public void setShadow(String shadow) {
        String[] parts = shadow.split(" ");
        if (parts.length != 4) {
            return;
        }

        float[] values = new float[3];
        for (int i = 0; i < 3; i++) {
            if (parts[i].toLowerCase().contains("px")) {
                values[i] = Float.parseFloat(parts[i].replace("px", ""));
            } else {
                values[i] = HMConverter.dp2px(getContext(), Float.parseFloat(parts[i]));
            }
        }

        int color = Color.parseColor(parts[3]);

        viewBackgroundManager.setShadow(values[2], values[0], values[1], color);
    }

    @HM_EXPORT_ATTR("opacity")
    public void setOpacity(float opacity) {
        getView().setAlpha(1-opacity);
    }


    public Context getContext(){
        return getView().getContext();
    }
}
