package com.xie.marcwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.xie.marcwidget.activity.BarChartActivity;
import com.xie.marcwidget.activity.HollowPieChartActivity;
import com.xie.marcwidget.activity.HoriBarActivity;
import com.xie.marcwidget.activity.LineChartActivity;
import com.xie.marcwidget.activity.PieChartActivity;
import com.xie.marcwidget.activity.ScaleActivity;
import com.xie.marcwidget.utils.UIUtils;
import com.xie.marcwidget.widget.DragLayout;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DragLayout mDragLayout;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mDragLayout = (DragLayout) findViewById(R.id.drag);
        mDragLayout.setDragStatusListener(new DragLayout.onDragStatusChangeListener() {
            @Override
            public void onClose() {
                UIUtils.showToast(MainActivity.this, "close");
            }

            @Override
            public void onOpen() {
                UIUtils.showToast(MainActivity.this, "open");
            }

            @Override
            public void onDraging(float percent) {
                Log.i(TAG, "onDraging: " + percent);
            }
        });
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.btn_line_chart).setOnClickListener(this);
        findViewById(R.id.btn_bar_chart).setOnClickListener(this);
        findViewById(R.id.btn_path).setOnClickListener(this);
        findViewById(R.id.btn_path_pie).setOnClickListener(this);
        findViewById(R.id.btn_scale).setOnClickListener(this);
        findViewById(R.id.btn_hori_bar).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_line_chart:
                intent = new Intent(this, LineChartActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_bar_chart:
                intent = new Intent(this, BarChartActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_path:
                intent = new Intent(this, PieChartActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_path_pie:
                intent = new Intent(this, HollowPieChartActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_scale:
                intent = new Intent(this, ScaleActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_hori_bar:
                intent = new Intent(this, HoriBarActivity.class);
                startActivity(intent);
                break;
        }
    }
}
