package com.nami.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nami.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final float chartWidth=7f; //当前试图显示几个数据条
    LineChartView chartWen, chartShi, chartCO2;
    String[] date = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10","11","12","13","14","15","16","17","18","19","20"};//X轴的标注
    int[] score = {25, 22, 18, 16, 15, 30, 22};//图表的数据点 , 35, 37, 10,15,18,20,24,25,26,27,28,29,30
    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private List<PointValue> pointValues = new ArrayList<PointValue>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        chartWen = (LineChartView)findViewById(R.id.chart_wen);
        chartShi = (LineChartView)findViewById(R.id.chart_shi);
        chartCO2 = (LineChartView)findViewById(R.id.chart_CO2);

        getAxisPoints();
        getAxisXLables();

        initChart(chartWen);
        initChart(chartShi);
        initChart(chartCO2);

        //设置悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initChart(LineChartView chartView){
        Line line = new Line(mPointValues).setColor(R.color.lightGreen).setCubic(true).setHasLabels(true)
                .setStrokeWidth(2).setShape(ValueShape.CIRCLE).setPointRadius(3);
        List<Line> lines = new ArrayList<Line>();
        line.setFilled(true);
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setValueLabelBackgroundAuto(false);
        data.setValueLabelBackgroundEnabled(true);
        data.setValueLabelBackgroundColor(R.color.colorPrimary);

        Axis axisX = new Axis();
        axisX.setTextColor(Color.BLACK).setTextSize(12).setMaxLabelChars(1).setHasLines(true)
                .setHasSeparationLine(false).setLineColor(Color.GRAY).setValues(mAxisXValues);
        data.setAxisXBottom(axisX);

        chartView.setLineChartData(data);
        chartView.setInteractive(true);
        chartView.setZoomEnabled(true);
        chartView.setMaxZoom((float)2);

        final Viewport v = new Viewport(chartView.getMaximumViewport());
        v.top = 50;
        v.bottom = 0;
        chartView.setMaximumViewport(v);  // 注意max在current上面
        chartView.setCurrentViewport(v);
        chartView.setViewportCalculationEnabled(false);

    }
    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables() {
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
        for(int i = 0; i < 50; i+=10){
            mAxisYValues.add(new AxisValue(i).setLabel(i+"°"));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints() {
        for (int i = 0; i < score.length; i++) {
            mPointValues.add(new PointValue(i, score[i]));
        }
    }
}
