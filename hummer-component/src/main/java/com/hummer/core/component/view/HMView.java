package com.hummer.core.component.view;

import android.content.Context;
import android.support.annotation.Nullable;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_METHOD;
import com.hummer.core.component.HMBase;
import com.hummer.core.jni.JSValue;
import com.facebook.yoga.android.YogaLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: linjizong
 * @date: 2019/4/23
 * @desc:
 */
@HM_EXPORT_CLASS("View")
public class HMView extends HMBase<YogaLayout> {
    private Map<String, JSValue> children = new HashMap<>();

    public HMView(Context context, @Nullable JSValue[] args) {
        super(context, args);
    }

    @Override
    protected YogaLayout createViewInstance(Context context) {
        return new YogaLayout(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getView() != null) {
            getView().removeAllViews();
        }
    }

    @HM_EXPORT_METHOD("appendChild")
    public void add(JSValue subview) {
        try {
            HMBase base = (HMBase) subview.toObject();
            getDomNode().addSubview(base);
            children.put(base.getViewID(), subview);
            getDomNode().layoutSubviews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @HM_EXPORT_METHOD("removeChild")
    public void remove(JSValue subview) {
        try {
            HMBase base = (HMBase) subview.toObject();
            getDomNode().removeSubview(base);
            getDomNode().setYogaNode(null);
            children.remove(base.getViewID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @HM_EXPORT_METHOD("removeAll")
    public void removeAll() {
        try {
            getDomNode().removeAllSubviews();
            for (JSValue jv : children.values()) {
                HMBase b = (HMBase) jv.toObject();
                b.getDomNode().setYogaNode(null);
            }
            children.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @HM_EXPORT_METHOD("insertBefore")
    public void insertBefore(JSValue subview, JSValue existingView) {
        try {
            HMBase base = (HMBase) subview.toObject();
            HMBase existing = (HMBase) existingView.toObject();
            getDomNode().insertBefore(base, existing);
            children.put(base.getViewID(), subview);
            getDomNode().layoutSubviews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @HM_EXPORT_METHOD("replaceChild")
    public void replace(JSValue newSubview, JSValue oldSubview) {
        try {
            HMBase base = (HMBase) newSubview.toObject();
            HMBase old = (HMBase) oldSubview.toObject();
            getDomNode().replaceSubview(base, old);
            children.remove(old.getViewID());
            children.put(base.getViewID(), newSubview);
            getDomNode().layoutSubviews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @HM_EXPORT_METHOD("layout")
    public void layout() {
//        getDomNode().layoutSubviews();
    }

    @HM_EXPORT_METHOD("getElementById")
    public JSValue getSubview(JSValue viewID) {
        return children.get(viewID.toCharString());
    }

    public static final String OVERFLOW_VISIBLE = "visible";
    public static final String OVERFLOW_HIDDEN = "hidden";
    @HM_EXPORT_ATTR("overflow")
    public void setOverflow(String overflow) {
        boolean clipSubviews = !OVERFLOW_HIDDEN.equals(overflow);
        getView().setClipToPadding(clipSubviews);
        getView().setClipChildren(clipSubviews);
        getDomNode().layoutSubviews();
    }
}
