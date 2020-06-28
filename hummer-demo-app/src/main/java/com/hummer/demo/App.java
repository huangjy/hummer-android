package com.hummer.demo;

import android.app.Application;

import com.hummer.core.Hummer;
import com.facebook.soloader.SoLoader;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.guesture.HMLongPressEvent;
import com.hummer.core.event.guesture.HMPanEvent;
import com.hummer.core.event.guesture.HMPinchEvent;
import com.hummer.core.event.guesture.HMSwipeEvent;
import com.hummer.core.event.guesture.HMTapEvent;
import com.hummer.core.event.view.HMInputEvent;
import com.hummer.core.event.view.HMScrollEvent;
import com.hummer.core.event.view.HMSwitchEvent;
import com.squareup.leakcanary.LeakCanary;

import java.util.HashMap;

/**
 * Created by XiaoFeng on 2019/3/25.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        SoLoader.init(this,false);

        Hummer.getInstance().init(this);
        Hummer.getInstance().setModule(new HMModuleImpl());
        Hummer.getInstance().start(new HashMap<String, String>(){{
            // @TODO: export customer class
        }}, new HashMap<String, Class>(){{
            put(HMBaseEvent.HM_CLICK_EVENT_NAME, HMTapEvent.class);
            put(HMBaseEvent.HM_LONG_PRESS_EVENT_NAME, HMLongPressEvent.class);
            put(HMBaseEvent.HM_SWIPE_EVENT_NAME, HMSwipeEvent.class);
            put(HMBaseEvent.HM_PINCH_EVENT_NAME, HMPinchEvent.class);
            put(HMBaseEvent.HM_PAN_EVENT_NAME, HMPanEvent.class);
            put(HMBaseEvent.HM_SCROLL_EVENT_NAME, HMScrollEvent.class);
            put(HMBaseEvent.HM_INPUT_EVENT_NAME, HMInputEvent.class);
            put(HMBaseEvent.HM_SWITCH_EVENT_NAME, HMSwitchEvent.class);
        }});
    }
}
