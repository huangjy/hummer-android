package com.hummer.core.component.text;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.component.HMBase;
import com.hummer.core.jni.JSValue;

@HM_EXPORT_CLASS("Text")
public class HMText extends HMBase<TextView> {
    public HMText(Context context, JSValue[] values) {
        super(context,values);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected TextView createViewInstance(Context context) {
        return new TextView(context);
    }

    @HM_EXPORT_PROPERTY("text")
    public String text;
    public void setText(JSValue text) {
        this.text = text.toCharString();
        getDomNode().getYogaNode().dirty();
        getView().setText(this.text);
    }

    @HM_EXPORT_PROPERTY("formattedText")
    public String formattedText;
    public void setFormattedText(JSValue formattedText) {
        this.formattedText = formattedText.toCharString();
        getView().setText(fromHtml(this.formattedText));
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    @HM_EXPORT_ATTR("color")
    public void setColor(int color) {
        getView().setTextColor(color);
    }

    @HM_EXPORT_ATTR("textAlign")
    public void setTextAlign(String textAlign) {
        switch (textAlign) {
            case "center":
                getView().setGravity(Gravity.CENTER);
                break;
            case "left":
                getView().setGravity(Gravity.LEFT);
                break;
            case "right":
                getView().setGravity(Gravity.RIGHT);
                break;
        }
    }

    @HM_EXPORT_ATTR("textDecoration")
    public void setTextDecoration(String textDecoration) {
        switch (textDecoration) {
            case "underline":
                getView().getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                break;
            case "line-through":
                getView().getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @HM_EXPORT_ATTR("fontFamily")
    public void setFontFamily(String fontFamily) {
        int style = Typeface.NORMAL;
        if (getView().getTypeface() != null) {
            style = getView().getTypeface().getStyle();
        }
        Typeface newTypeface = FontManager.getInstance().getTypeface(
                fontFamily,
                style,
                getView().getContext().getAssets());
        getView().setTypeface(newTypeface);
    }

    @HM_EXPORT_ATTR("fontSize")
    public void setFontSize(float fontSize) {
        getView().setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
    }

    @HM_EXPORT_ATTR("fontWeight")
    public void setFontWeight(String fontWeight) {
        switch (fontWeight){
            case "bold":
                getView().setTypeface(null, Typeface.BOLD);
                break;
            case "normal":
                getView().setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    @HM_EXPORT_ATTR("textOverflow")
    public void setTextOverflow(String overflow) {
        if ("clip".equalsIgnoreCase(overflow)) {
            getView().setEllipsize(null);
        } else if ("ellipsis".equalsIgnoreCase(overflow)) {
            getView().setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }
    }

    @HM_EXPORT_ATTR("textLineClamp")
    public void setTextLineClamp(int lines) {
        getView().setMaxLines(lines);
    }

}
