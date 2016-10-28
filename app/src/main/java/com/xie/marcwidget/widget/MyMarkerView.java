package com.xie.marcwidget.widget;

import android.content.Context;
import android.widget.TextView;

import com.xie.marcwidget.R;
import com.xie.marcwidget.entity.ChartEntity;
import com.xie.marcwidget.utils.DensityUtil;

/**
 * des:
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(ChartEntity e) {
        tvContent.setText("" + DensityUtil.formatNumber(Float.parseFloat(e.getxLabel()), 0, true));
    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }
}
