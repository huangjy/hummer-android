package com.hummer.core.layout;

import android.support.annotation.Nullable;
import android.view.View;

import com.hummer.core.component.HMBase;
import com.hummer.core.jni.JSValue;
import com.facebook.yoga.YogaNode;
import com.facebook.yoga.android.YogaLayout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HMDomNode<T extends View> {
    public String nodeID;
    private HashMap domStyle;
    private HashMap domAttr;
    private HMBase<T> linkView;
    private YogaNode yogaNode;

    /**
     * 构造函数
     *
     * @param linkView node关联的view
     */
    public HMDomNode(HMBase<T> linkView, @Nullable String nodeID) {
        this.linkView = linkView;
        getYogaNode();
        this.domAttr = new HashMap<>();
        this.domStyle = new HashMap<>();
        this.nodeID = nodeID == null ? createViewID() : nodeID;
    }

    /**
     * 复位属性为null
     */
    public void reset() {
        domAttr.clear();
        domStyle.clear();
        if (yogaNode != null) {
            yogaNode.setData(null);
            yogaNode = null;
        }
        nodeID = null;
        domStyle = null;
        domAttr = null;
        linkView = null;
    }

    /**
     * 创建 Node 节点
     *
     * @param view : 视图对象
     * @return Node 节点
     */
    public static <T extends View> HMDomNode nodeForView(HMBase<T> view, JSValue[] args) {
        String nodeID = args.length > 0 ? args[0].toCharString() : null;
        return new HMDomNode(view, nodeID);
    }

    public static <T extends View> HMDomNode nodeForView(View  view, JSValue[] args) {
        String nodeID = args.length > 0 ? args[0].toCharString() : null;
        return new HMDomNode(null, nodeID);
    }

    public YogaNode getYogaNode() {
        if (yogaNode == null) {
            if (linkView.getView() instanceof YogaLayout) {
                yogaNode = ((YogaLayout) this.linkView.getView()).getYogaNode();
            } else {
                yogaNode = new YogaNode();
                yogaNode.setData(linkView.getView());
                yogaNode.setMeasureFunction(new YogaLayout.ViewMeasureFunction());
            }
        }
        return yogaNode;
    }

    public void setYogaNode(YogaNode yogaNode) {
        this.yogaNode = yogaNode;
    }

    public void setDomStyle(HashMap style) {
        if (style == null) {
            return;
        }
        Iterator iterator = style.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
//            System.out.println(entry.getKey() + " : " + entry.getValue());
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            int attr = HMYogaConfig.defaultConfig().ygPropertyWithCSSStyle(key);
            if (attr == Integer.MAX_VALUE) {
                domAttr.put(key, value);
            } else {
                domStyle.put(key, value);
            }
        }

        configureLayout(domStyle);
        configureAttribute(domAttr);

        HashMap tempMap = new HashMap();
        tempMap.putAll(domAttr);
        tempMap.putAll(domStyle);
        // 判定style是否有变更，若发生变更需要重新渲染view
        if (!style.equals(tempMap)) {
            linkView.getView().requestLayout();
        }
    }

    /**
     * 添加子视图
     *
     * @param subview 子视图
     */
    public void addSubview(HMBase subview) {
        if (subview == null) {
            return;
        }
        HMDomNode subDomNode = subview.getDomNode();
        if (subDomNode == null) {
            return;
        }
        YogaLayout superview = superview();
        if (superview == null) {
            return;
        }
        int index = yogaNode.getChildCount();
        YogaNode subYogaNode = subDomNode.getYogaNode();
        superview.addView(subview.getView(), subYogaNode);
        yogaNode.addChildAt(subYogaNode, index);
    }

    /**
     * 移除子视图
     *
     * @param subview 子视图
     */
    public void removeSubview(HMBase subview) {
        if (subview == null) {
            return;
        }
        HMDomNode subDomNode = subview.getDomNode();
        if (subDomNode == null) {
            return;
        }
        YogaLayout superview = superview();
        if (superview == null) {
            return;
        }
        // 移除自视图，YogaLayout内部会移除node
        superview.removeView(subview.getView());
    }

    /**
     * 移除所有子视图
     */
    public void removeAllSubviews() {
        YogaLayout superview = superview();
        if (superview == null) {
            return;
        }
        superview.removeAllViews();
    }

    /**
     * 插入一个子视图
     *
     * @param subview      要插入的视图
     * @param existingView 在existingView之前插入子视图
     */
    public void insertBefore(HMBase subview, HMBase existingView) {
        if (subview == null) {
            return;
        }
        HMDomNode subDomNode = subview.getDomNode();
        if (subDomNode == null) {
            return;
        }
        YogaLayout superview = superview();
        if (superview == null) {
            return;
        }

        this.addSubview(subview);
        int index = superview.indexOfChild(existingView.getView());
        this.yogaNode.addChildAt(subDomNode.getYogaNode(), index);
    }

    /**
     * 替换子视图
     *
     * @param newSubview 新视图
     * @param oldSubview 旧视图
     */
    public void replaceSubview(HMBase newSubview, HMBase oldSubview) {
        if (newSubview == null || oldSubview == null) {
            return;
        }
        HMDomNode subDomNode = newSubview.getDomNode();
        if (subDomNode == null) {
            return;
        }
        YogaLayout superview = superview();
        if (superview == null) {
            return;
        }
        int index = superview.indexOfChild(oldSubview.getView());
        this.removeSubview(oldSubview);
        this.addSubview(newSubview);
        this.yogaNode.addChildAt(subDomNode.getYogaNode(), index);
    }

    /**
     * 重新layout子视图
     */
    public void layoutSubviews() {
        YogaLayout superview = superview();
        if (superview == null) {
            return;
        }
        this.yogaNode.calculateLayout(0, 0);
    }


    /**
     * 获取父视图
     *
     * @return 如果当前视图不是ViewGroup，则返回null
     */
    private YogaLayout superview() {
        if (!(linkView.getView() instanceof YogaLayout)) {
            return null;
        }
        return (YogaLayout) linkView.getView();
    }

    private void bindLayoutWithStyle(HashMap domStyle) {
        if (domStyle == null) {
            return;
        }
        YogaNode node = getYogaNode();
        HMYogaConfig.applyLayoutBackgroudParams(node, linkView);
        if (node != null) {
            Iterator iterator = domStyle.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                if (!(value instanceof HashMap)) {
                    setLayoutNodeStyle(key, value, node);
                }
            }
        }
    }

    /**
     * 配置元素属性
     *
     * @param attr 元素属性
     */
    private void configureAttribute(HashMap attr) {
        if (attr == null) return;
        bindLinkViewWithCssAttribute(attr);
    }

    /**
     * 配置布局属性d
     *
     * @param style style 属性
     */
    private void configureLayout(HashMap style) {
        if (style == null) {
            return;
        }
        bindLayoutWithStyle(style);
    }

    private void setLayoutNodeStyle(String key, Object style, YogaNode node) {
        if (style == null || key == null || node == null) {
            return;
        }
        HMYogaConfig.applyLayoutParams(node, key, style);
    }

    private void bindLinkViewWithCssAttribute(HashMap domAttr) {
        Iterator iterator = domAttr.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
//            System.out.println(entry.getKey() + " : " + entry.getValue());
            String key = entry.getKey().toString();
            Object attribute = entry.getValue();
            setLinkViewWithCssAttribute(key, attribute);
        }
    }

    private void setLinkViewWithCssAttribute(String key, Object attribute) {
        HMYogaConfig.applyViewAttributeParams(linkView, key, attribute);
    }

    private String createViewID() {
        long currentTime = System.currentTimeMillis();
        String time = String.valueOf(currentTime);
        String ViewID = "_" + time;
        return ViewID;
    }
}
