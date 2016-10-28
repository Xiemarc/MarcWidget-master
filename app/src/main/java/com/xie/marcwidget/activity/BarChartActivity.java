package com.xie.marcwidget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xie.marcwidget.R;
import com.xie.marcwidget.entity.ChartEntity;
import com.xie.marcwidget.widget.BarChart;
import com.xie.marcwidget.widget.MyMarkerView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class BarChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private List<ChartEntity> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        barChart = (BarChart) findViewById(R.id.chart);

        data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(new ChartEntity(String.valueOf(i), (float) (Math.random() * 1000)));
        }
        barChart.setData(data);
        MyMarkerView markerView  = new MyMarkerView(BarChartActivity.this,R.layout.custom_marker_view);
        barChart.setMarkerView(markerView);
//        barChart.startCustomAnimation();
        barChart.setOnItemBarClickListener(new BarChart.OnItemBarClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(BarChartActivity.this, "点击了：" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void changeData(View v) {
        data.clear();
        for (int i = 0; i < 5; i++) {
            data.add(new ChartEntity(String.valueOf(i), (float) (Math.random() * 150)));
        }
        barChart.setData(data);
//        barChart.startCustomAnimation();
    }
}
