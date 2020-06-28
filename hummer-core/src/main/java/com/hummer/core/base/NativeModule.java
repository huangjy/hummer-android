package com.hummer.core.base;

public interface NativeModule {
    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    String getName();

    /**
     * releasing resources.
     */
    void destroy();
}
