package com.xie.marcwidget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xie.marcwidget.R;
import com.xie.marcwidget.entity.ChartEntity;
import com.xie.marcwidget.widget.LineChart;
import com.xie.marcwidget.widget.MyMarkerView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class LineChartActivity extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        lineChart = (LineChart) findViewById(R.id.chart);
        List<ChartEntity> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(new ChartEntity(String.valueOf(i), (float) (Math.random() * 1000)));
        }
        lineChart.setData(data);
        MyMarkerView markerView = new MyMarkerView(LineChartActivity.this, R.layout.custom_marker_view);
        lineChart.setMarkerView(markerView);
        lineChart.setIsDrawMarkerView(true);
        lineChart.setOnItemLineClickListener(new LineChart.OnItemLineClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(LineChartActivity.this, "点击了：" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void change(View v) {
        boolean isSmooth = lineChart.getIsSmooth();
        if (isSmooth) {
            lineChart.setIsSmooth(false);
        }else {
            lineChart.setIsSmooth(true);
        }
    }
}
