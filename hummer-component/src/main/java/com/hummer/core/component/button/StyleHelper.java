package com.hummer.core.component.button;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: linjizong
 * @date: 2019/3/29
 * @desc:
 */
public class StyleHelper {
    private final static String KEY_BACKGROUND_COLOR = "backgroundColor";
    private final static String KEY_TEXT_COLOR = "color";
    private final static String KEY_ONNORMAL = "normal";
    private final static String KEY_ONPRESS = "pressed";
    private final static String KEY_ONDISABLE = "disabled";

    public static ColorDrawable createColorDrawable(HashMap map) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof String) {
                String str = (String) value;
                if (KEY_BACKGROUND_COLOR.equals(key)) {
                    if (str.contains("#")) {
                        return new ColorDrawable(Color.parseColor(str));
                    }
                }
            }
        }
        return null;
    }

    public static ColorStateList getTextColor(HashMap hashMap) {
        Iterator iterator = hashMap.entrySet().iterator();
        List<int[]> states = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (KEY_ONDISABLE.equals(key) && value instanceof HashMap) {
                int color=fetchTextColor((HashMap) value);
                    states.add(new int[]{-android.R.attr.state_enabled});
                    colors.add(color);
            } else if (KEY_ONPRESS.equals(key)) {
                int color=fetchTextColor((HashMap) value);
                    states.add(new int[]{android.R.attr.state_pressed});
                    colors.add(color);
            } else if (KEY_ONNORMAL.equals(key)) {
                int color=fetchTextColor((HashMap) value);
                    states.add(new int[]{android.R.attr.state_enabled});
                    colors.add(color);
            }
        }
        if (states.size()>0){
            int[][] stateArray=new int[states.size()][];
            int[] colorArray= new int[colors.size()];
            for (int i=0;i<colors.size();i++){
                colorArray[i]=colors.get(i);
            }
            return new ColorStateList(states.toArray(stateArray),colorArray);
        }
        return null;
    }
    private static int fetchTextColor(HashMap map){
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof String) {
                String str = (String) value;
                if (KEY_TEXT_COLOR.equals(key)) {
                    if (str.contains("#")) {
                        return Color.parseColor(str);
                    }
                }
            }
        }
        return 0;
    }
}
