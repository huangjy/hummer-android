package com.hummer.core.manager;

import com.hummer.core.base.NativeModule;
import com.hummer.core.component.HMBase;

/**
 * Tracking Hummer Components, which can be destroyed when SDK releasing.
 */
public interface IHMObjectManager {

    void trackNJBase(HMBase base);

    void trackNativeModule(NativeModule nativeModule);
}
