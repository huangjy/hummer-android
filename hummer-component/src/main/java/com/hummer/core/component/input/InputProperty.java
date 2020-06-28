package com.hummer.core.component.input;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hummer.core.component.text.FontManager;
import com.hummer.core.jni.JSValue;

import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * @desc: Input属性处理类，包含style内的非yoga属性、其他属性、注册方法的处理
 */
public class InputProperty {
    private final EditText mView;
    private static final InputFilter[] EMPTY_FILTERS = new InputFilter[0];

    private boolean isSingleLine;
    private MaxLinesTextWatcher maxLinesTextWatcher = new MaxLinesTextWatcher(3);

    private JSValue mFocusListener;
    private JSValue mUnFocusListener;

    private int mStagedInputTypeFlags;

    public InputProperty(EditText editText, boolean singleLine) {
        mStagedInputTypeFlags = editText.getInputType();
        this.isSingleLine = singleLine;
        mView = editText;
        mView.setPadding(0, 0, 0, 0);
        mView.setSingleLine(singleLine);
        mView.setOnFocusChangeListener(mOnFocusChangeListener);
        if (!isSingleLine) {
            mView.addTextChangedListener(maxLinesTextWatcher);
        }
    }

    public void setText(String text) {
        mView.setText(text);
        if (mView.getText().length() > 0) {
            mView.setSelection(mView.getText().length());
        }
    }

    public String getText() {
        return mView.getText().toString();
    }

    public void setPlaceholder(String placeholder) {
        mView.setHint(placeholder);
    }

    public void setType(String type) {
        setStagedInputTypeFlags(0, getInputType(type));
    }

    /**
     * js端输入类型转原生类型
     *
     * @param type
     * @return
     */
    private int getInputType(String type) {
        int inputType;
        switch (type) {
            case InputType.EMAIL:
                inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                break;
            case InputType.NUMBER:
                inputType = android.text.InputType.TYPE_CLASS_NUMBER;
                break;
            case InputType.TEL:
                inputType = android.text.InputType.TYPE_CLASS_PHONE;
                break;
            case InputType.PASSWORD:
                inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;

            default:
                inputType = android.text.InputType.TYPE_NULL;
                break;
        }
        return inputType;
    }

    public void setTextColor(int color) {
        mView.setTextColor(color);
    }

    public void setPlaceholderColor(int color) {
        mView.setHintTextColor(color);
    }

    public void setCursorColor(int cursorColor) {
        try {
            Field cursorDrawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawableResField.setAccessible(true);
            int drawableResId = cursorDrawableResField.getInt(mView);

            if (drawableResId == 0) {
                return;
            }

            Drawable drawable = ContextCompat.getDrawable(mView.getContext(), drawableResId);
            drawable.setColorFilter(cursorColor, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editor = editorField.get(mView);
            Field cursorDrawableField = editor.getClass().getDeclaredField("mCursorDrawable");
            cursorDrawableField.setAccessible(true);
            cursorDrawableField.set(editor, drawables);
        } catch (NoSuchFieldException ex) {
        } catch (IllegalAccessException ex) {
        }
    }

    public void setTextAlign(String align) {
        mView.setGravity(getGravity(align));
    }

    private int getGravity(String align) {
        switch (align) {
            case TextAlign.CENTER:
                return isSingleLine ? Gravity.CENTER : Gravity.CENTER_HORIZONTAL;
            case TextAlign.RIGHT:
                return isSingleLine ? Gravity.RIGHT | Gravity.CENTER_VERTICAL  : Gravity.RIGHT;
            default:
                return isSingleLine? Gravity.LEFT | Gravity.CENTER_VERTICAL  : Gravity.LEFT;
        }
    }

    public void setFontFamily(String fontFamily) {
        int style = Typeface.NORMAL;
        if (mView.getTypeface() != null) {
            style = mView.getTypeface().getStyle();
        }
        Typeface newTypeface = FontManager.getInstance().getTypeface(
                fontFamily,
                style,
                mView.getContext().getAssets());
        mView.setTypeface(newTypeface);
    }

    public void setFontSize(float fontSize) {
        mView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
    }

    public void setPlaceholderFontSize(float fontSize) {
        mView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
    }

    public void setMaxLength(int maxLength) {
        InputFilter [] currentFilters = mView.getFilters();
        InputFilter[] newFilters = EMPTY_FILTERS;

        if (maxLength == 0) {
            if (currentFilters.length > 0) {
                LinkedList<InputFilter> list = new LinkedList<>();
                for (int i = 0; i < currentFilters.length; i++) {
                    if (!(currentFilters[i] instanceof InputFilter.LengthFilter)) {
                        list.add(currentFilters[i]);
                    }
                }
                if (!list.isEmpty()) {
                    newFilters = (InputFilter[]) list.toArray(new InputFilter[list.size()]);
                }
            }
        } else {
            if (currentFilters.length > 0) {
                newFilters = currentFilters;
                boolean replaced = false;
                for (int i = 0; i < currentFilters.length; i++) {
                    if (currentFilters[i] instanceof InputFilter.LengthFilter) {
                        currentFilters[i] = new InputFilter.LengthFilter(maxLength);
                        replaced = true;
                    }
                }
                if (!replaced) {
                    newFilters = new InputFilter[currentFilters.length + 1];
                    System.arraycopy(currentFilters, 0, newFilters, 0, currentFilters.length);
                    currentFilters[currentFilters.length] = new InputFilter.LengthFilter(maxLength);
                }
            } else {
                newFilters = new InputFilter[1];
                newFilters[0] = new InputFilter.LengthFilter(maxLength);
            }
        }

        mView.setFilters(newFilters);
    }

    public void setMaxLines(int maxLines) {
        maxLinesTextWatcher.setMaxLines(maxLines);
    }


    public void setReturnKeyType(String type) {
        mView.setImeOptions(getImeOption(type));
    }

    private int getImeOption(String type) {
        switch (type) {
            case ReturnKeyType.GO:
                return EditorInfo.IME_ACTION_GO;
            case ReturnKeyType.SEARCH:
                return EditorInfo.IME_ACTION_SEARCH;
            case ReturnKeyType.SEND:
                return EditorInfo.IME_ACTION_SEND;
            case ReturnKeyType.NEXT:
                return EditorInfo.IME_ACTION_NEXT;
            default:
                return EditorInfo.IME_ACTION_DONE;
        }
    }

    public void setSecureTextEntry(boolean password) {
        if (password) {
            setStagedInputTypeFlags(android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD, android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            setStagedInputTypeFlags(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD, android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void setFocused(boolean focused) {
        if (focused) {
            mView.requestFocus();
            InputMethodManager imm = (InputMethodManager) mView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            mView.clearFocus();
        }
    }

    public void setFocusEvent(JSValue click) {
        mFocusListener = click;
    }

    private void setStagedInputTypeFlags(int flagsToUnset, int flagsToSet) {
        mStagedInputTypeFlags = (mStagedInputTypeFlags & ~flagsToUnset) | flagsToSet;
        mView.setInputType(mStagedInputTypeFlags);
    }

    private final View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mFocusListener != null) {
                JSValue[] params = {JSValue.makeString(String.valueOf(hasFocus), mFocusListener.getContext())};
                mFocusListener.call(params);
            }
        }
    };

    private class MaxLinesTextWatcher implements TextWatcher {

        int maxLines;

        public MaxLinesTextWatcher(int maxLines) {
            this.maxLines = maxLines;
        }

        public void setMaxLines(int maxLines) {
            this.maxLines = maxLines;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            int lines = mView.getLineCount();
            // 限制最大输入行数
            if (lines > maxLines) {
                String str = s.toString();
                int cursorStart = mView.getSelectionStart();
                int cursorEnd = mView.getSelectionEnd();
                if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                    str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
                } else {
                    str = str.substring(0, s.length() - 1);
                }
                // setText会触发afterTextChanged的递归
                mView.setText(str);
                // setSelection用的索引不能使用str.length()否则会越界
                mView.setSelection(mView.getText().length());
            }
        }
    }

}
