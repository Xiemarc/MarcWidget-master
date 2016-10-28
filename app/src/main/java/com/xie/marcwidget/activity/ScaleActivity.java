package com.xie.marcwidget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.chs.easychartwidget.R;
import com.xie.marcwidget.widget.ScaleView;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class ScaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);
        ScaleView scaleView = (ScaleView) findViewById(R.id.scale_view);
        //实际显示是跟传入的数值反序
        scaleView.setScales(new double[]{0.4, 0.3, 0.15, 0.15});
    }
}
