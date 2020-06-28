
// NOTE: this file is auto-copied from https://github.com/facebook/css-layout
// @generated SignedSource<<fc074ec7db63f2eebf1e9cbc626f280d>>

package com.hummer.core.utility;

public class HMFloatUtil {

    private static final float EPSILON = .00001f;

    public static boolean floatsEqual(float f1, float f2) {
        if (Float.isNaN(f1) || Float.isNaN(f2)) {
            return Float.isNaN(f1) && Float.isNaN(f2);
        }
        return Math.abs(f2 - f1) < EPSILON;
    }
}

