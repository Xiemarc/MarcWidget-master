package com.xie.marcwidget.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xie.marcwidget.R;
import com.xie.marcwidget.entity.PieDataEntity;
import com.xie.marcwidget.widget.PieChart;

import java.util.ArrayList;
import java.util.List;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class PieChartActivity extends AppCompatActivity {
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        List<PieDataEntity> dataEntities = new ArrayList<>();
        for(int i = 0;i<9;i++){
            PieDataEntity entity = new PieDataEntity("name"+i,i+1,mColors[i]);
            dataEntities.add(entity);
        }
        pieChart.setDataList(dataEntities);

        pieChart.setOnItemPieClickListener(new PieChart.OnItemPieClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(PieChartActivity.this,"点击了"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
