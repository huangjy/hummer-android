package com.hummer.core.layout;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LayoutDirection;

import com.hummer.core.Hummer;
import com.hummer.core.component.HMBase;
import com.hummer.core.utility.HMConverter;
import com.hummer.core.utility.HMFlexible;
import com.hummer.core.utility.HMUtility;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaNode;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaWrap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hummer.core.layout.HMYogaConfigConstant.ALIGN_CONTENT;
import static com.hummer.core.layout.HMYogaConfigConstant.ALIGN_ITEMS;
import static com.hummer.core.layout.HMYogaConfigConstant.ALIGN_SELF;
import static com.hummer.core.layout.HMYogaConfigConstant.ASPECT_RATIO;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_ALL;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_BOTTOM;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_END;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_HORIZONTAL;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_LEFT;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_RIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_START;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_TOP;
import static com.hummer.core.layout.HMYogaConfigConstant.BORDER_VERTICAL;
import static com.hummer.core.layout.HMYogaConfigConstant.BOTTOM;
import static com.hummer.core.layout.HMYogaConfigConstant.DIRECTION;
import static com.hummer.core.layout.HMYogaConfigConstant.DISPLAY;
import static com.hummer.core.layout.HMYogaConfigConstant.END;
import static com.hummer.core.layout.HMYogaConfigConstant.FLEX;
import static com.hummer.core.layout.HMYogaConfigConstant.FLEX_BASIS;
import static com.hummer.core.layout.HMYogaConfigConstant.FLEX_DIRECTION;
import static com.hummer.core.layout.HMYogaConfigConstant.FLEX_GROW;
import static com.hummer.core.layout.HMYogaConfigConstant.FLEX_SHRINK;
import static com.hummer.core.layout.HMYogaConfigConstant.FLEX_WRAP;
import static com.hummer.core.layout.HMYogaConfigConstant.HEIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.JUSTIFY_CONTENT;
import static com.hummer.core.layout.HMYogaConfigConstant.LEFT;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_ALL;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_BOTTOM;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_END;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_HORIZONTAL;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_LEFT;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_RIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_START;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_TOP;
import static com.hummer.core.layout.HMYogaConfigConstant.MARGIN_VERTICAL;
import static com.hummer.core.layout.HMYogaConfigConstant.MAX_HEIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.MAX_WIDTH;
import static com.hummer.core.layout.HMYogaConfigConstant.MIN_HEIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.MIN_WIDTH;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_ALL;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_BOTTOM;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_END;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_HORIZONTAL;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_LEFT;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_RIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_START;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_TOP;
import static com.hummer.core.layout.HMYogaConfigConstant.PADDING_VERTICAL;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_ALL;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_BOTTOM;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_END;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_HORIZONTAL;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_LEFT;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_RIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_START;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_TOP;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_TYPE;
import static com.hummer.core.layout.HMYogaConfigConstant.POSITION_VERTICAL;
import static com.hummer.core.layout.HMYogaConfigConstant.RIGHT;
import static com.hummer.core.layout.HMYogaConfigConstant.START;
import static com.hummer.core.layout.HMYogaConfigConstant.TOP;
import static com.hummer.core.layout.HMYogaConfigConstant.WIDTH;

public class HMYogaConfig {
    HashMap yogaConfig;
    /**
     * flex-direction property 映射
     */
    private final static Map flexDirectMap = new HashMap() {{
        put("column", YogaFlexDirection.COLUMN.ordinal());
        put("column-reverse", YogaFlexDirection.COLUMN_REVERSE.ordinal());
        put("row", YogaFlexDirection.ROW.ordinal());
        put("row-reverse", YogaFlexDirection.ROW_REVERSE.ordinal());
    }};

    /**
     * Yoga Overflow
     */
    private final static Map overflowMap = new HashMap() {{
        put("hidden", YogaOverflow.HIDDEN.ordinal());
        put("scroll", YogaOverflow.SCROLL.ordinal());
        put("visible", YogaOverflow.VISIBLE.ordinal());
    }};

    /**
     * Yoga Wrap
     */
    private final static Map wrapMap = new HashMap() {{
        put("wrap", YogaWrap.WRAP.ordinal());
        put("wrap-reverse", YogaWrap.WRAP_REVERSE.ordinal());
        put("nowrap", YogaWrap.NO_WRAP.ordinal());
    }};

    /**
     * Yoga Direction
     */
    private final static Map directionMap = new HashMap() {{
        put("left", YogaDirection.LTR.ordinal());
        put("right", YogaDirection.RTL.ordinal());
        put("inherit", YogaDirection.INHERIT.ordinal());
    }};

    /**
     * Yoga Justify
     */
    private final static Map justifyMap = new HashMap() {{
        put("flex-start", YogaJustify.FLEX_START.ordinal());
        put("center", YogaJustify.CENTER.ordinal());
        put("flex-end", YogaJustify.FLEX_END.ordinal());
        put("space-between", YogaJustify.SPACE_BETWEEN.ordinal());
        put("space-around", YogaJustify.SPACE_AROUND.ordinal());
//        put("space-evenly", YogaJustify.SPACE_EVENLY.ordinal());
    }};

    /**
     * Yoga PositionType
     */
    private final static Map positionTypeMap = new HashMap() {{
        put("absolute", YogaPositionType.ABSOLUTE.ordinal());
        put("relative", YogaPositionType.RELATIVE.ordinal());
        put("static", YogaPositionType.RELATIVE.ordinal());
    }};

    /**
     * Yoga Align
     */
    private final static Map alignMap = new HashMap() {{
        put("auto", YogaAlign.AUTO.ordinal());
        put("flex-start", YogaAlign.FLEX_START.ordinal());
        put("center", YogaAlign.CENTER.ordinal());
        put("flex-end", YogaAlign.FLEX_END.ordinal());
        put("stretch", YogaAlign.STRETCH.ordinal());
        put("baseline", YogaAlign.BASELINE.ordinal());
        put("space-between", YogaAlign.SPACE_BETWEEN.ordinal());
        put("space-around", YogaAlign.SPACE_AROUND.ordinal());
    }};

    /**
     * Yoga Display
     */
    private final static Map displayMap = new HashMap() {{
        put("flex", YogaDisplay.FLEX.ordinal());
        put("none", YogaDisplay.NONE.ordinal());
    }};

    /**
     * 样式映射
     */
    private final static Map styleMap = new HashMap() {{
        put("alignContent", ALIGN_CONTENT);
        put("alignItems", ALIGN_ITEMS);
        put("alignSelf", ALIGN_SELF);
        put("aspectRatio", ASPECT_RATIO);
        put("borderAll", BORDER_ALL);
        put("borderBottom", BORDER_BOTTOM);
        put("borderEnd", BORDER_END);
        put("borderHorizontal", BORDER_HORIZONTAL);
        put("borderLeft", BORDER_LEFT);
        put("borderRight", BORDER_RIGHT);
        put("borderStart", BORDER_START);
        put("borderTop", BORDER_TOP);
        put("borderVertical", BORDER_VERTICAL);
        put("direction", DIRECTION);
        put("display", DISPLAY);
        put("flex", FLEX);
        put("flexBasis", FLEX_BASIS);
        put("flexDirection", FLEX_DIRECTION);
        put("flexGrow", FLEX_GROW);
        put("flexShrink", FLEX_SHRINK);
        put("height", HEIGHT);
        put("justifyContent", JUSTIFY_CONTENT);
        put("marginAll", MARGIN_ALL);
        put("margin", MARGIN);
        put("marginBottom", MARGIN_BOTTOM);
        put("marginEnd", MARGIN_END);
        put("marginHorizontal", MARGIN_HORIZONTAL);
        put("marginLeft", MARGIN_LEFT);
        put("marginRight", MARGIN_RIGHT);
        put("marginStart", MARGIN_START);
        put("marginTop", MARGIN_TOP);
        put("marginVertical", MARGIN_VERTICAL);
        put("maxHeight", MAX_HEIGHT);
        put("maxWidth", MAX_WIDTH);
        put("minHeight", MIN_HEIGHT);
        put("minWidth", MIN_WIDTH);
//        put("overflow", 35);  Hummer 重定义了 overflow 的行为
        put("paddingAll", PADDING_ALL);
        put("paddingBottom", PADDING_BOTTOM);
        put("paddingEnd", PADDING_END);
        put("paddingHorizontal", PADDING_HORIZONTAL);
        put("paddingLeft", PADDING_LEFT);
        put("paddingRight", PADDING_RIGHT);
        put("paddingStart", PADDING_START);
        put("paddingTop", PADDING_TOP);
        put("paddingVertical", PADDING_VERTICAL);
        put("positionAll", POSITION_ALL);
        put("positionBottom", POSITION_BOTTOM);
        put("positionEnd", POSITION_END);
        put("positionHorizontal", POSITION_HORIZONTAL);
        put("positionLeft", POSITION_LEFT);
        put("positionRight", POSITION_RIGHT);
        put("positionStart", POSITION_START);
        put("positionTop", POSITION_TOP);
        put("bottom", BOTTOM);
        put("end", END);
        put("left", LEFT);
        put("right", RIGHT);
        put("start", START);
        put("top", TOP);
        put("positionType", POSITION_TYPE);
        put("position", POSITION);
        put("positionVertical", POSITION_VERTICAL);
        put("width", WIDTH);
        put("flexWrap", FLEX_WRAP);
    }};

    /**
     * 数值类型的属性白名单（不需要dp转换）
     */
    private final static List<String> NON_DP_CONVERT_KEYS = new ArrayList<String>() {{
        add("opacity");
        add("maxLength");
        add("textLineClamp");
        add("column");
    }};

    /**
     * 内部类，在装载内部类时会创建单例对象
     */
    private static class HMYogaConfigHolder {
        public static HMYogaConfig instance = new HMYogaConfig();
    }

    private HMYogaConfig() {
        this.yogaConfig = new HashMap();
    }

    /**
     * 获取单例
     */
    public static HMYogaConfig defaultConfig() {
        return HMYogaConfigHolder.instance;
    }

    /**
     * 获取Yoga 属性名
     *
     * @param cssStyle CSS样式属性
     * @return 属性名 attribute值
     */
    public int ygPropertyWithCSSStyle(String cssStyle) {
        if (HMYogaConfig.styleMap.containsKey(cssStyle))
            return (int) HMYogaConfig.styleMap.get(cssStyle);
        else
            return Integer.MAX_VALUE;
    }

    /**
     * 映射 style值字符串
     *
     * @param attrValue  styleable值 参见styleMap
     * @param styleValue style值 String类型
     * @return
     */
    private int ygStyleValueIsString(int attrValue, Object styleValue) {
        int value = Integer.MAX_VALUE;
        switch (attrValue) {
            case ALIGN_CONTENT:
            case ALIGN_ITEMS:
            case ALIGN_SELF: {
                if (HMYogaConfig.alignMap.containsKey(styleValue))
                    value = (int) HMYogaConfig.alignMap.get(styleValue);
            }
            break;
            case DIRECTION: {
                if (HMYogaConfig.directionMap.containsKey(styleValue))
                    value = (int) HMYogaConfig.directionMap.get(styleValue);
            }
            break;
            case DISPLAY: {
                if (HMYogaConfig.displayMap.containsKey(styleValue))
                    value = (int) HMYogaConfig.displayMap.get(styleValue);
            }
            break;
            case FLEX_DIRECTION: {
                if (HMYogaConfig.flexDirectMap.containsKey(styleValue))
                    value = (int) HMYogaConfig.flexDirectMap.get(styleValue);
            }
            break;
            case JUSTIFY_CONTENT: {
                if (HMYogaConfig.justifyMap.containsKey(styleValue))
                    value = (int) HMYogaConfig.justifyMap.get(styleValue);
            }
            break;
//            case 35: {
//                if (HMYogaConfig.overflowMap.containsKey(styleValue))
//                    value = (int) HMYogaConfig.overflowMap.get(styleValue);
//            }
//            break;
            case POSITION: {
                if (HMYogaConfig.positionTypeMap.containsKey(styleValue))
                    value = (int) HMYogaConfig.positionTypeMap.get(styleValue);
            }
            break;
            case FLEX_WRAP: {
                if (HMYogaConfig.wrapMap.containsKey(styleValue))
                    value = (int) HMYogaConfig.wrapMap.get(styleValue);
            }
            break;
        }
        return value;
    }

    /**
     * 获取Yoga Properties
     *
     * @return Yoga 属性列表
     */
    public Array yogaProperties() {
        return null;
    }

    /**
     * 设置背景默认YogaNode样式（暂未使用）
     *
     * @param node YogaNode对象
     * @param view 目标视图对象
     */
    public static void applyLayoutBackgroudParams(YogaNode node, HMBase view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration configuration = view.getView().getResources().getConfiguration();
            if (configuration.getLayoutDirection() == LayoutDirection.RTL) {
                node.setDirection(YogaDirection.RTL);
            }
        }

        Drawable background = view.getView().getBackground();
        if (background != null) {
            final Rect backgroundPadding = new Rect();
            if (background.getPadding(backgroundPadding)) {
                node.setPadding(YogaEdge.LEFT, backgroundPadding.left);
                node.setPadding(YogaEdge.TOP, backgroundPadding.top);
                node.setPadding(YogaEdge.RIGHT, backgroundPadding.right);
                node.setPadding(YogaEdge.BOTTOM, backgroundPadding.bottom);
            }
        }
    }

    /**
     * 设置视图对应YogaNode的样式
     *
     * @param node  YogaNode对象
     * @param key   flexbox布局样式属性关键字
     * @param style 样式数据值
     */
    public static void applyLayoutParams(YogaNode node, String key, Object style) {
        final int attribute = HMYogaConfig.defaultConfig().ygPropertyWithCSSStyle(key);
        if (attribute == Integer.MAX_VALUE) {
            return;
        }
        float value = formatNumber(attribute, style);
        if (value == Integer.MAX_VALUE) {
            return;
        }
        if (attribute == ALIGN_CONTENT) {
            node.setAlignContent(YogaAlign.fromInt(Math.round(value)));
        } else if (attribute == ALIGN_ITEMS) {
            node.setAlignItems(YogaAlign.fromInt(Math.round(value)));
        } else if (attribute == ALIGN_SELF) {
            node.setAlignSelf(YogaAlign.fromInt(Math.round(value)));
        } else if (attribute == ASPECT_RATIO) {
            node.setAspectRatio(value);
        } else if (attribute == BORDER_LEFT) {
            node.setBorder(YogaEdge.LEFT, value);
        } else if (attribute == BORDER_TOP) {
            node.setBorder(YogaEdge.TOP, value);
        } else if (attribute == BORDER_RIGHT) {
            node.setBorder(YogaEdge.RIGHT, value);
        } else if (attribute == BORDER_BOTTOM) {
            node.setBorder(YogaEdge.BOTTOM, value);
        } else if (attribute == BORDER_START) {
            node.setBorder(YogaEdge.START, value);
        } else if (attribute == BORDER_END) {
            node.setBorder(YogaEdge.END, value);
        } else if (attribute == BORDER_HORIZONTAL) {
            node.setBorder(YogaEdge.HORIZONTAL, value);
        } else if (attribute == BORDER_VERTICAL) {
            node.setBorder(YogaEdge.VERTICAL, value);
        } else if (attribute == BORDER_ALL) {
            node.setBorder(YogaEdge.ALL, value);
        } else if (attribute == DIRECTION) {
            node.setDirection(YogaDirection.fromInt(Math.round(value)));
        } else if (attribute == DISPLAY) {
            node.setDisplay(YogaDisplay.fromInt(Math.round(value)));
        } else if (attribute == FLEX) {
            node.setFlex(value);
        } else if (attribute == FLEX_BASIS) {
            node.setFlexBasis(value);
        } else if (attribute == FLEX_DIRECTION) {
            node.setFlexDirection(YogaFlexDirection.fromInt(Math.round(value)));
        } else if (attribute == FLEX_GROW) {
            node.setFlexGrow(value);
        } else if (attribute == FLEX_SHRINK) {
            node.setFlexShrink(value);
        } else if (attribute == HEIGHT) {
            node.setHeight(value);
        } else if (attribute == MARGIN_LEFT) {
            node.setMargin(YogaEdge.LEFT, value);
        } else if (attribute == JUSTIFY_CONTENT) {
            node.setJustifyContent(YogaJustify.fromInt(Math.round(value)));
        } else if (attribute == MARGIN_TOP) {
            node.setMargin(YogaEdge.TOP, value);
        } else if (attribute == MARGIN_RIGHT) {
            node.setMargin(YogaEdge.RIGHT, value);
        } else if (attribute == MARGIN_BOTTOM) {
            node.setMargin(YogaEdge.BOTTOM, value);
        } else if (attribute == MARGIN_START) {
            node.setMargin(YogaEdge.START, value);
        } else if (attribute == MARGIN_END) {
            node.setMargin(YogaEdge.END, value);
        } else if (attribute == MARGIN_HORIZONTAL) {
            node.setMargin(YogaEdge.HORIZONTAL, value);
        } else if (attribute == MARGIN_VERTICAL) {
            node.setMargin(YogaEdge.VERTICAL, value);
        } else if (attribute == MARGIN_ALL) {
            node.setMargin(YogaEdge.ALL, value);
        } else if (attribute == MAX_HEIGHT) {
            node.setMaxHeight(value);
        } else if (attribute == MAX_WIDTH) {
            node.setMaxWidth(value);
        } else if (attribute == MIN_HEIGHT) {
            node.setMinHeight(value);
        } else if (attribute == MIN_WIDTH) {
            node.setMinWidth(value);
        } else if (attribute == PADDING_LEFT) {
            node.setPadding(YogaEdge.LEFT, value);
        } else if (attribute == PADDING_TOP) {
            node.setPadding(YogaEdge.TOP, value);
        } else if (attribute == PADDING_RIGHT) {
            node.setPadding(YogaEdge.RIGHT, value);
        } else if (attribute == PADDING_BOTTOM) {
            node.setPadding(YogaEdge.BOTTOM, value);
        } else if (attribute == PADDING_START) {
            node.setPadding(YogaEdge.START, value);
        } else if (attribute == PADDING_END) {
            node.setPadding(YogaEdge.END, value);
        } else if (attribute == PADDING_HORIZONTAL) {
            node.setPadding(YogaEdge.HORIZONTAL, value);
        } else if (attribute == PADDING_VERTICAL) {
            node.setPadding(YogaEdge.VERTICAL, value);
        } else if (attribute == PADDING_ALL) {
            node.setPadding(YogaEdge.ALL, value);
        } else if (attribute == POSITION_LEFT) {
            node.setPosition(YogaEdge.LEFT, value);
        } else if (attribute == POSITION_TOP) {
            node.setPosition(YogaEdge.TOP, value);
        } else if (attribute == POSITION_RIGHT) {
            node.setPosition(YogaEdge.RIGHT, value);
        } else if (attribute == POSITION_BOTTOM) {
            node.setPosition(YogaEdge.BOTTOM, value);
        } else if (attribute == POSITION_START) {
            node.setPosition(YogaEdge.START, value);
        } else if (attribute == POSITION_END) {
            node.setPosition(YogaEdge.END, value);
        } else if (attribute == POSITION_HORIZONTAL) {
            node.setPosition(YogaEdge.HORIZONTAL, value);
        } else if (attribute == POSITION_VERTICAL) {
            node.setPosition(YogaEdge.VERTICAL, value);
        } else if (attribute == POSITION_ALL) {
            node.setPosition(YogaEdge.ALL, value);
        } else if (attribute == POSITION_TYPE) {
            node.setPositionType(YogaPositionType.fromInt(Math.round(value)));
        } else if (attribute == WIDTH) {
            node.setWidth(value);
        } else if (attribute == FLEX_WRAP) {
            node.setWrap(YogaWrap.fromInt(Math.round(value)));
        }

//        else if (attribute == com.facebook.yoga.android.R.styleable.yoga_yg_overflow) {
//            node.setOverflow(YogaOverflow.fromInt(Math.round(value)));
//        }
    }


    public static void applyViewAttributeParams(HMBase view, String key, Object attr) {
        if (attr instanceof String) {
            String str = (String) attr;
            if (isColor(str)) {
                int color = Color.parseColor(str);
                if (isColor32(str)) {
                    color = rgba2argb(color);
                }
                attr = String.valueOf(color);
            } else if (isLinearGradientColor(str)) {
                attr = parseLinearGradientColor(str);
            } else if (isPxNumeric(str)) {
                str = str.replace("px", "");
                attr = String.valueOf(HMFlexible.rem2px(Float.parseFloat(str)));
            } else if (isNumeric(str)) {
                if (isNeedDPConvent(key)) {
                    attr = String.valueOf(HMConverter.dp2px(Hummer.sApplication, Float.parseFloat(str)));
                }
            }
        } else if (attr instanceof Number) {
            if (isNeedDPConvent(key)) {
                attr = String.valueOf(HMConverter.dp2px(Hummer.sApplication, ((Number) attr).floatValue()));
            }
        }
        HMUtility.callSetter(view, key, attr);
        return;
    }

    private static boolean isNeedDPConvent(String key) {
        return !NON_DP_CONVERT_KEYS.contains(key);
    }

    private static int rgba2argb(int color) {
        color = color << 24 | (color >> 8 & 0x00ffffff);
        return color;
    }

    /**
     * 将js端传过来的数据转为数值类型，目前支持三种格式，例："100"、100、"100px"，其中"100"、100以dp为单位计算,"100px"以rem为单位计算
     *
     * @param attribute 映射到native端的属性id
     * @param number    js端传递过来的值
     * @return 转化后的值，值为Integer.MAX_VALUE时表示无效值
     */
    private static float formatNumber(int attribute, Object number) {
        if (number instanceof Number) {
            return (HMConverter.dp2px(Hummer.sApplication, (((Number) number).floatValue())));
        }
        if (number instanceof String) {
            float value = 0;
            String attr = (String) number;
            if (isPxNumeric(attr)) {
                attr = attr.replace("px", "");
                value = HMFlexible.rem2px(Float.parseFloat(attr));
            } else if (isNumeric(attr)) {
                value = HMConverter.dp2px(Hummer.sApplication, Float.parseFloat(attr));
            } else {
                value = HMYogaConfig.defaultConfig().ygStyleValueIsString(attribute, number);
            }
            return value;
        }
        return 0f;
    }

    /**
     * 判断string是否含有数字
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isNumeric(String str) {
        return str.matches("^-?\\d+(\\.\\d+)?$");
    }

    /**
     * 判断string是否带px单位的数字
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isPxNumeric(String str) {
        return str.matches("^-?\\d+(\\.\\d+)?px$");
    }

    /**
     * 判断string是否是颜色
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isColor(String str) {
        return str.matches("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");
    }

    /**
     * 判断string是否是24位颜色（rgb: #FFFFFF）
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isColor24(String str) {
        return str.matches("^#([0-9a-fA-F]{6})$");
    }

    /**
     * 判断string是否是32位颜色（argb #FFFFFFFF）
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isColor32(String str) {
        return str.matches("^#([0-9a-fA-F]{8})$");
    }

    /**
     * 判断string是否是渐变颜色
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isLinearGradientColor(String str) {
        return str.matches("^linear-gradient\\(\\d+deg(\\s+#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})){2}\\)$");
    }

    /**
     * 解析渐变颜色值
     *
     * @param str
     * @return
     */
    public static int[] parseLinearGradientColor(String str) {
        str = str.replace("linear-gradient(", "");
        str = str.replace("deg", "");
        str = str.replace(")", "").trim();
        String[] array = str.split("\\s+");
        int[] colors = new int[array.length];
        colors[0] = Integer.parseInt(array[0]) % 360;
        for (int i = 1; i < colors.length; i++) {
            colors[i] = rgba2argb(Color.parseColor(array[i]));
        }
        return colors;
    }
}
