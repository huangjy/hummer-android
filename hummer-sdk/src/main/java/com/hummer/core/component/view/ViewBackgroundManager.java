// Copyright (c) Facebook, Inc. and its affiliates.

// This source code is licensed under the MIT license found in the
// LICENSE file in the root directory of this source tree.

package com.hummer.core.component.view;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.view.View;

import com.hummer.annotation.Nullable;
import com.hummer.core.utility.HMUtility;


/** Class that manages the background for views and borders. */
public class ViewBackgroundManager {

    private @Nullable HMViewBackgroundDrawable mReactBackgroundDrawable;
    private View mView;

    public ViewBackgroundManager(View view) {
        this.mView = view;
    }

    public void destroy() {
        this.mView = null;
    }

    private HMViewBackgroundDrawable getOrCreateReactViewBackground() {
        if (mReactBackgroundDrawable == null) {
            mReactBackgroundDrawable = new HMViewBackgroundDrawable(mView.getContext());
            setBackgroundDrawable(mReactBackgroundDrawable);
        }
        return mReactBackgroundDrawable;
    }

    private void setBackgroundDrawable(Drawable drawable) {
        // required so that drawable callback is cleared before we add the drawable back as a part of LayerDrawable
        HMViewHelper.setBackground(mView, null);
        Drawable backgroundDrawable = mView.getBackground();
        if (backgroundDrawable == null) {
            HMViewHelper.setBackground(mView, drawable);
        } else {
            LayerDrawable layerDrawable =
                    new LayerDrawable(new Drawable[] {drawable, backgroundDrawable});
            HMViewHelper.setBackground(mView, layerDrawable);
        }
    }

    private GradientDrawable.Orientation transOrientation(int deg) {
        GradientDrawable.Orientation orientation;
        switch (deg) {
            case 0:
            default:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 45:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 90:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 135:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 180:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 225:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case 270:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 315:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
        }
        return orientation;
    }

    public void setBackgroundColor(int color) {
        if (color == Color.TRANSPARENT && mReactBackgroundDrawable == null) {
            // don't do anything, no need to allocate ReactBackgroundDrawable for transparent background
        } else {
            getOrCreateReactViewBackground().setColor(color);
        }
    }

    public void setBackgroundGradientColor(int deg, int[] colors) {
        GradientDrawable gradientDrawable = new GradientDrawable(transOrientation(deg), colors);
        setBackgroundDrawable(gradientDrawable);
    }

    public void setBackgroundImage(String image) {
        Drawable drawable = null;
        if (!TextUtils.isEmpty(image)) {
            if (image.startsWith("/")) {
                drawable = new BitmapDrawable(BitmapFactory.decodeFile(image));
            } else {
                int imageId = HMUtility.getResourceId(image, "drawable", null);
                drawable = mView.getResources().getDrawable(imageId);
            }
        }
        setBackgroundDrawable(drawable);
    }

    public void setBorderWidth(int position, float width) {
        getOrCreateReactViewBackground().setBorderWidth(position, width);
    }

    public void setBorderColor(int position, float color, float alpha) {
        getOrCreateReactViewBackground().setBorderColor(position, color, alpha);
    }

    public void setBorderRadius(float borderRadius) {
        getOrCreateReactViewBackground().setRadius(borderRadius);
    }

    public void setBorderRadius(float borderRadius, int position) {
        getOrCreateReactViewBackground().setRadius(borderRadius, position);
    }

    public void setBorderStyle(@Nullable String style) {
        getOrCreateReactViewBackground().setBorderStyle(style);
    }

    public void setShadow(float radius, float dx, float dy, int color) {
        float r = 0f;
        int pL = (int) Math.max(0, radius - dx);
        int pT = (int) Math.max(0, radius - dy);
        int pR = (int) Math.max(0, radius + dx);
        int pB = (int) Math.max(0, radius + dy);
        RoundRectShape rrs = new RoundRectShape(new float[]{r, r, r, r, r, r, r, r}, null, null);
        ShapeDrawable shadow = new ShapeDrawable(rrs);
        shadow.getPaint().setColor(Color.WHITE);
        shadow.getPaint().setStyle(Paint.Style.FILL);
        shadow.getPaint().setShadowLayer(radius, dx, dy, color);
        shadow.setPadding(pL, pT, pR, pB);
        mView.setLayerType(View.LAYER_TYPE_SOFTWARE, shadow.getPaint());

        Drawable[] layers = new Drawable[2];
        layers[0] = shadow;
        layers[1] = mView.getBackground();

        LayerDrawable layerList = new LayerDrawable(layers);
        layerList.setLayerInset(0, pL, pT, pR, pB);
        layerList.setLayerInset(1, 0, 0, 0, 0);

        mView.setBackground(layerList);
    }

    public void setOpacity(float opacity) {
        int alpha = (int)(opacity*255);
        getOrCreateReactViewBackground().setAlpha(alpha);
    }
}
