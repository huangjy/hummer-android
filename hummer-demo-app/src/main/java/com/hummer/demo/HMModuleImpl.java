package com.hummer.demo;

import com.bumptech.glide.Glide;
import com.hummer.core.module.HMBuilder;
import com.hummer.core.module.HMModule;

/**
 * @author: linjizong
 * @date: 2019/4/11
 * @desc:
 */
public class HMModuleImpl implements HMModule {
     @Override
     public void applyOptions(HMBuilder builder) {
          builder.setWebImageHandler((url, imageView) -> Glide.with(imageView).load(url).into(imageView));
     }
}
