package com.xie.marcwidget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.chs.easychartwidget.R;
import com.xie.marcwidget.entity.ChartEntity;
import com.xie.marcwidget.widget.LineChart;

import java.util.ArrayList;
import java.util.List;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class LineChartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        LineChart lineChart = (LineChart) findViewById(R.id.chart);
        List<ChartEntity> data = new ArrayList<>();
        for(int i =0;i<20;i++){
            data.add(new ChartEntity(String.valueOf(i), (float) (Math.random()*1000)));
        }
        lineChart.setData(data);
    }
}
