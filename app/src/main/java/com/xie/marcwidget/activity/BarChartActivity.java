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
import java.util.Random;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class BarChartActivity extends AppCompatActivity {

    private BarChart barChart;
    private List<ChartEntity> data;
    private Random random;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        barChart = (BarChart) findViewById(R.id.chart);

        random = new Random();
        data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ChartEntity chartEntity = new ChartEntity(String.format("%s", i), (float) random.nextInt(100));
            data.add(chartEntity);
        }
        barChart.setData(data);
        MyMarkerView markerView = new MyMarkerView(BarChartActivity.this, R.layout.custom_marker_view);
        barChart.setMarkerView(markerView);
        barChart.setIsDrawMarkerView(true);
        barChart.setmDrawTopValues(true);
        barChart.setOnItemBarClickListener(new BarChart.OnItemBarClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(BarChartActivity.this, "点击了：" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void changeData(View v) {
        data.clear();
        for (int i = 0; i < 10; i++) {
            data.add(new ChartEntity(String.valueOf(i), (float) (random.nextInt(100))));
        }
        barChart.setData(data);
//        barChart.startCustomAnimation();
    }
}
