package com.hummer.core.event.guesture;

import android.content.Context;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.jni.JSValue;

import java.util.HashMap;

@HM_EXPORT_CLASS("SwipeEvent")
public class HMSwipeEvent extends HMBaseEvent {
    public static final String DIRECTION_RIGHT = "right";
    public static final String DIRECTION_LEFT = "left";
    public static final String DIRECTION_UP = "up";
    public static final String DIRECTION_DOWN = "down";

    @HM_EXPORT_PROPERTY("direction")
    public String direction;

    public HMSwipeEvent(Context context, JSValue[] args) {
        super(context, args);
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public void configWithData(HashMap<String, Object> hashMap) {
        super.configWithData(hashMap);
        float beginX = (Float) hashMap.get("beginX");
        float beginY = (Float) hashMap.get("beginY");
        float endX = (Float) hashMap.get("endX");
        float endY = (Float) hashMap.get("endY");
        float velocityX = (Float) hashMap.get("velocityX");
        float velocityY = (Float) hashMap.get("velocityY");

        if (beginX - endX > 120 && Math.abs(velocityX) > 0) {   //左滑
            direction = HMSwipeEvent.DIRECTION_LEFT;
        } else if (endX - beginX > 120 && Math.abs(velocityX) > 0) {   //右滑
            direction = HMSwipeEvent.DIRECTION_RIGHT;
        } else if (beginY - endY > 120 && Math.abs(velocityY) > 0) {   //上滑
            direction = HMSwipeEvent.DIRECTION_UP;
        } else if (endY - beginY > 120 && Math.abs(velocityY) > 0) {   //下滑
            direction = HMSwipeEvent.DIRECTION_DOWN;
        }
    }
}
