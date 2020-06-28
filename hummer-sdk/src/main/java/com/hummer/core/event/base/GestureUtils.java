package com.hummer.core.event.base;

import android.view.MotionEvent;

import java.util.HashMap;

public class GestureUtils {
    public static int findStateInMotionEvent(MotionEvent e) {
        int ret = HMBaseEvent.HM_GESTURE_STATE_NORMAL;

        if (e == null) return ret;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                return HMBaseEvent.HM_GESTURE_STATE_BEGAN;
            }
            case MotionEvent.ACTION_MOVE: {
                return HMBaseEvent.HM_GESTURE_STATE_CHANGED;
            }
            case MotionEvent.ACTION_UP: {
                return HMBaseEvent.HM_GESTURE_STATE_ENDED;
            }
            case MotionEvent.ACTION_CANCEL: {
                return HMBaseEvent.HM_GESTURE_STATE_CANCELLED;
            }
            default:
                return ret;
        }
    }
}
