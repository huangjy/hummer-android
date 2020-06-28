package com.hummer.core.component.input;

import android.support.annotation.StringDef;

/**
 * @author: linjizong
 * @date: 2019/3/28
 * @desc:
 */
@StringDef
public @interface InputType {
    String DEFAULT = "default";
    String NUMBER = "number";
    String TEL = "tel";
    String EMAIL = "email";
    String PASSWORD = "password";
}
