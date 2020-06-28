package com.hummer.core.component.imageview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.hummer.annotation.HM_EXPORT_ATTR;
import com.hummer.annotation.HM_EXPORT_CLASS;
import com.hummer.annotation.HM_EXPORT_PROPERTY;
import com.hummer.core.HMModuleManager;
import com.hummer.core.Hummer;
import com.hummer.core.component.HMBase;
import com.hummer.core.event.base.HMBaseEvent;
import com.hummer.core.event.guesture.HMTapEvent;
import com.hummer.core.event.base.GestureUtils;
import com.hummer.core.jni.JSValue;
import com.hummer.core.module.handler.WebImageHandler;
import com.hummer.core.utility.HMUtility;

import java.util.HashMap;

@HM_EXPORT_CLASS("Image")
public class HMImage extends HMBase<RoundImageView> {

    public HMImage(Context context, JSValue[] args) {
        super(context, args);
    }

    @Override
    protected RoundImageView createViewInstance(Context context) {
        return new RoundImageView(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getView().setOnClickListener((v) -> {
            mEventManager.dispatchEvent(HMBaseEvent.HM_CLICK_EVENT_NAME, jsContext -> {
                JSValue tapEventJS = Hummer.getInstance().valueWithClass(
                        HMTapEvent.class,
                        jsContext);

                HMTapEvent event = (HMTapEvent) tapEventJS.toObject();
                event.setType(HMBaseEvent.HM_CLICK_EVENT_NAME);
                event.setPosition(new HashMap<String, Float>(){{
                    put("x", 0.0f); put("y", 0.0f);
                }});
                event.setState(GestureUtils.findStateInMotionEvent(null));
                event.setTarget(mAssociatedJSValue);

                return tapEventJS;
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 图片资源地址
     */
    @HM_EXPORT_PROPERTY("src")
    public String src;

    public void setSrc(JSValue src) {
        this.src = src.toCharString();
        if (this.src.startsWith("//")) {
            this.src = "https:" + this.src;
        }
        if (this.src.startsWith("http")) {
            WebImageHandler handler = HMModuleManager.getInstance().getHandler(WebImageHandler.class);
            if (handler != null) {
                handler.load(this.src, getView());
            }
        } else if (this.src.startsWith("/")) {
            getView().setImageBitmap(BitmapFactory.decodeFile(this.src));
        } else {
            int imageId = HMUtility.getResourceId(this.src, "drawable", null);
            getView().setImageResource(imageId);
        }
    }


    @HM_EXPORT_ATTR("resize")
    public void setContentMode(String resize) {
        switch (resize) {
            case "origin":
                getView().setScaleType(ImageView.ScaleType.FIT_START);
                break;
            case "contain":
                getView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case "cover":
                getView().setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case "stretch":
                getView().setScaleType(ImageView.ScaleType.FIT_XY);
                break;
        }
    }

    @Override
    @HM_EXPORT_ATTR("borderWidth")
    public void setBorderWidth(float width) {
        getView().setBorderWidth((int)width);
    }

    @Override
    @HM_EXPORT_ATTR("borderColor")
    public void setBorderColor(int color) {
        getView().setBorderColor(color);
    }

    @Override
    @HM_EXPORT_ATTR("borderRadius")
    public void setBorderRadius(float roundRadius) {
        getView().setBorderRadius(roundRadius);
    }


}
