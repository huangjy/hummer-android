package com.hummer.core.manager;

import com.hummer.core.base.NativeModule;
import com.hummer.core.common.ILifeCycle;
import com.hummer.core.component.HMBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracking Hummer Components, which can be destroyed when SDK releasing.
 */
public class HMObjectManager implements IHMObjectManager, ILifeCycle {
    List<HMBase> mNJBases;
    List<NativeModule> mNativeModules;

    @Override
    public void onCreate() {
        mNJBases = new ArrayList<>();
        mNativeModules = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        for (HMBase base : mNJBases) {
            base.destroy();
        }

        for (NativeModule nm : mNativeModules) {
            nm.destroy();
        }

        mNJBases.clear();
        mNativeModules.clear();
    }

    @Override
    public void trackNJBase(HMBase base) {
        mNJBases.add(base);
    }

    @Override
    public void trackNativeModule(NativeModule nativeModule) {
        mNativeModules.add(nativeModule);
    }
}
