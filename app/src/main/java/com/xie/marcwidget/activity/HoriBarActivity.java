package com.xie.marcwidget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xie.marcwidget.R;
import com.xie.marcwidget.widget.PercentageBar;

import java.util.ArrayList;

/**
 * des:
 * author: marc
 * date:  2016/10/28 23:48
 * email：aliali_ha@yeah.net
 */

public class HoriBarActivity extends AppCompatActivity {
    private PercentageBar mBarGraph;
    private ArrayList<Float> respectTarget;
    private ArrayList<String> respName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_hori);

        respectTarget = new ArrayList<Float>();
        respName = new ArrayList<String>();
        respectTarget.add(35.0f);
        respectTarget.add(20.0f);
        respectTarget.add(18.0f);
        respectTarget.add(15.0f);
        respectTarget.add(10.0f);
        respectTarget.add(8.0f);
        respectTarget.add(5.0f);
        respName.add("滴滴");
        respName.add("小米");
        respName.add("京东");
        respName.add("美团");
        respName.add("魅族");
        respName.add("酷派");
        respName.add("携程");
        mBarGraph = (PercentageBar) findViewById(R.id.hori_bar);
        mBarGraph.setRespectTargetNum(respectTarget);
        mBarGraph.setRespectName(respName);
        mBarGraph.setTotalBarNum(7);
        mBarGraph.setMax(40);
        mBarGraph.setBarWidth(50);
        mBarGraph.setVerticalLineNum(4);
        mBarGraph.setUnit("亿元");
    }
}
