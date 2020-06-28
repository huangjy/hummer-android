package com.hummer.core.component.input;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_METHOD;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.Hummer;
import com.hummer.core.component.HMBase;
import com.hummer.core.event.HMEventCollection;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.view.HMInputEvent;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("Input")
public class HMInput extends HMBase<EditText> {

    protected final InputProperty mProperty;

    public HMInput(Context context, @Nullable JSValue[] args) {
        super(context,args);
        mProperty = new InputProperty(getView(), isSingleLine());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getView().addTextChangedListener(mTextWatcher);
        getView().setOnKeyListener(mOnKeyListener);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mEventManager.dispatchEvent(HMBaseEvent.HM_INPUT_EVENT_NAME, jsContext -> {

                HMEventCollection collection = HMEventCollection.getInstance();
                Class clazz = collection.classWithEventName(HMBaseEvent.HM_INPUT_EVENT_NAME);
                JSValue inputEventJS = Hummer.getInstance().valueWithClass( clazz, jsContext);

                HMInputEvent event = (HMInputEvent) inputEventJS.toObject();
                event.setType(HMBaseEvent.HM_INPUT_EVENT_NAME);
                event.setText(s.toString());
                event.setState(HMInputEvent.HM_INPUT_STATE_BEGAN);

                return inputEventJS;
            });
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEventManager.dispatchEvent(HMBaseEvent.HM_INPUT_EVENT_NAME, jsContext -> {

                HMEventCollection collection = HMEventCollection.getInstance();
                Class clazz = collection.classWithEventName(HMBaseEvent.HM_INPUT_EVENT_NAME);
                JSValue inputEventJS = Hummer.getInstance().valueWithClass( clazz, jsContext);

                HMInputEvent event = (HMInputEvent) inputEventJS.toObject();
                event.setType(HMBaseEvent.HM_INPUT_EVENT_NAME);
                event.setText(s.toString());
                event.setState(HMInputEvent.HM_INPUT_STATE_CHANGED);

                return inputEventJS;
            });
        }

        @Override
        public void afterTextChanged(Editable s) {
            mEventManager.dispatchEvent(HMBaseEvent.HM_INPUT_EVENT_NAME, jsContext -> {

                HMEventCollection collection = HMEventCollection.getInstance();
                Class clazz = collection.classWithEventName(HMBaseEvent.HM_INPUT_EVENT_NAME);
                JSValue inputEventJS = Hummer.getInstance().valueWithClass( clazz, jsContext);

                HMInputEvent event = (HMInputEvent) inputEventJS.toObject();
                event.setType(HMBaseEvent.HM_INPUT_EVENT_NAME);
                event.setText(s.toString());
                event.setState(HMInputEvent.HM_INPUT_STATE_ENDED);

                return inputEventJS;
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getView().removeTextChangedListener(mTextWatcher);
        getView().setOnKeyListener(null);
    }

    protected boolean isSingleLine() {
        return true;
    }

    @Override
    protected EditText createViewInstance(Context context) {
        EditText editText = new EditText(context);
        return editText;
    }

    /**
     * 设置文本
     */
    @HM_EXPORT_PROPERTY("text")
    public String text;
    public void setText(JSValue text) {
        this.text = text.toCharString();
        mProperty.setText(this.text);
    }

    public String getText() {
        return mProperty.getText();
    }

    /**
     * 设置占位文案
     */
    @HM_EXPORT_PROPERTY("placeholder")
    public String placeholder;

    public void setPlaceholder(JSValue placeholder) {
        this.placeholder = placeholder.toCharString();
        mProperty.setPlaceholder(this.placeholder);
    }

    /**
     * 设置是否取得焦点
     *
     * @param focused
     */
    @HM_EXPORT_PROPERTY("focused")
    public boolean focused;
    public void setFocused(JSValue focused) {
        this.focused = focused.toBoolean();
        mProperty.setFocused(this.focused);
    }
    /**
     * 设置键盘可输入字符类型
     *
     * @param type
     */
    @HM_EXPORT_ATTR("type")
    public void setType(String type) {
        mProperty.setType(type);
    }


    /**
     * 设置字体颜色
     *
     * @param color
     */
    @HM_EXPORT_ATTR("color")
    public void setColor(int color) {
        mProperty.setTextColor(color);
    }

    /**
     * 设置占位字体颜色
     *
     * @param color
     */
    @HM_EXPORT_ATTR("placeholderColor")
    public void setPlaceholderColor(String color) {
        mProperty.setPlaceholderColor(Integer.parseInt(color));
    }

    /**
     * 设置光标颜色，无公开api，使用反射设置
     *
     * @param cursorColor
     */
    @HM_EXPORT_ATTR("cursorColor")
    public void setCursorColor(int cursorColor) {
        mProperty.setCursorColor(cursorColor);
    }

    /**
     * 设置标题文本对齐方式
     *
     * @param align
     */
    @HM_EXPORT_ATTR("textAlign")
    public void setTextAlign(String align) {
        mProperty.setTextAlign(align);
    }
    /**
     * 设置字体
     *
     * @param fontFamily
     */
    @HM_EXPORT_ATTR("fontFamily")
    public void setFontFamily(String fontFamily) {
        mProperty.setFontFamily(fontFamily);
    }

    /**
     * 设置文本字体大小
     *
     * @param fontSize
     */
    @HM_EXPORT_ATTR("fontSize")
    public void setFontSize(float fontSize) {
        mProperty.setFontSize(fontSize);
    }

    /**
     * 设置占位字体大小，当前效果和设置字体大小一致
     *
     * @param fontSize
     */
    @HM_EXPORT_ATTR("placeholderFontSize")
    public void setPlaceholderFontSize(float fontSize) {
        mProperty.setPlaceholderFontSize(fontSize);
    }

    /**
     * 设置最大字数
     *
     * @param length
     */
    @HM_EXPORT_ATTR("maxLength")
    public void setMaxLength(int length) {
        mProperty.setMaxLength(length);
    }

    /**
     * 设置软键盘回车键类型
     *
     * @param type
     */
    @HM_EXPORT_ATTR("returnKeyType")
    public void setReturnKeyType(String type) {
        mProperty.setReturnKeyType(type);
    }

    @HM_EXPORT_METHOD("clear")
    public void clear() {
        mProperty.setText("");
    }

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                //this is for backspace
                if (getView() != null && TextUtils.isEmpty(getView().getText())) {
                    mEventManager.dispatchEvent(HMBaseEvent.HM_INPUT_EVENT_NAME, jsContext -> {

                        HMEventCollection collection = HMEventCollection.getInstance();
                        Class clazz = collection.classWithEventName(HMBaseEvent.HM_INPUT_EVENT_NAME);
                        JSValue inputEventJS = Hummer.getInstance().valueWithClass( clazz, jsContext);

                        HMInputEvent jsEvent = (HMInputEvent) inputEventJS.toObject();
                        jsEvent.setType(HMBaseEvent.HM_INPUT_EVENT_NAME);
                        jsEvent.setText("");
                        jsEvent.setState(HMInputEvent.HM_INPUT_STATE_CHANGED);

                        return inputEventJS;
                    });
                }
            }
            return false;
        }
    };
}
