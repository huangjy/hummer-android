package com.hummer.core.component.input;

import android.content.Context;
import android.support.annotation.Nullable;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("TextArea")
public class HMTextArea extends HMInput {
    public HMTextArea(Context context, @Nullable JSValue[] args) {
        super(context, args);
    }

    @Override
    protected boolean isSingleLine() {
        return false;
    }

    /**
     * 设置最大行数
     *
     * @param lines
     */
    @HM_EXPORT_ATTR("textLineClamp")
    public void setTextLineClamp(int lines) {
        mProperty.setMaxLines(lines);
    }
}