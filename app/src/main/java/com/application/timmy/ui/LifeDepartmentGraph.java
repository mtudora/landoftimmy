package com.application.timmy.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.application.timmy.model.ChangeModel;
import com.application.timmy.storage.TimmyData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.HashMap;

import timmy.application.com.landoftimmy.R;

/**
 * Created by mtudora on 29/03/15.
 */
public class LifeDepartmentGraph extends OfficeBaseActivity {
    private GraphView graphMorale, graphHumour, graphSkill;
    private BarGraphSeries<DataPoint> seriesMorale, seriesHumour, seriesSkill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.life_department_graph);

        graphMorale = (GraphView) findViewById(R.id.graphMorale);
        graphHumour = (GraphView) findViewById(R.id.graphHumour);
        graphSkill = (GraphView) findViewById(R.id.graphSkill);
        graphMorale.setTitle("Morale over department");
        graphHumour.setTitle("Humour over department");
        graphSkill.setTitle("Skill over department");

        populateCharts();
    }

    public void populateCharts(){
        HashMap<String, ChangeModel> depMap = TimmyData.getInstance().getDepartmentMap();

        DataPoint[] dataPointsMorale = new DataPoint[depMap.size()];
        DataPoint[] dataPointsHumour = new DataPoint[depMap.size()];
        DataPoint[] dataPointsSkill = new DataPoint[depMap.size()];
        int i = 0;
        for (String dep : depMap.keySet()){
            dataPointsMorale[i] = new DataPoint(i, depMap.get(dep).getMorale());
            dataPointsHumour[i] = new DataPoint(i, depMap.get(dep).getHumour());
            dataPointsSkill[i] = new DataPoint(i, depMap.get(dep).getSkill());
            i++;
        }

        seriesMorale = new BarGraphSeries<>(dataPointsMorale);
        graphMorale.addSeries(seriesMorale);

        seriesHumour = new BarGraphSeries<>(dataPointsHumour);
        graphHumour.addSeries(seriesHumour);

        seriesSkill = new BarGraphSeries<>(dataPointsSkill);
        graphSkill.addSeries(seriesSkill);

        // draw values on top
        seriesMorale.setDrawValuesOnTop(true);
        seriesMorale.setValuesOnTopColor(Color.RED);
        seriesMorale.setColor(Color.BLUE);

        seriesHumour.setColor(Color.GREEN);
        // draw values on top
        seriesHumour.setDrawValuesOnTop(true);
        seriesHumour.setValuesOnTopColor(Color.RED);

        seriesSkill.setColor(Color.YELLOW);
        // draw values on top
        seriesSkill.setDrawValuesOnTop(true);
        seriesSkill.setValuesOnTopColor(Color.RED);

        String[] depLabels = new String[depMap.size()];
        i = 0;
        for (String dep : depMap.keySet()){
            depLabels[i] = dep;
            i++;
        }

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphMorale);
        staticLabelsFormatter.setHorizontalLabels(depLabels);
        graphMorale.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        staticLabelsFormatter = new StaticLabelsFormatter(graphHumour);
        staticLabelsFormatter.setHorizontalLabels(depLabels);
        graphHumour.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        staticLabelsFormatter = new StaticLabelsFormatter(graphSkill);
        staticLabelsFormatter.setHorizontalLabels(depLabels);
        graphSkill.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    public void updateCharts(){
        HashMap<String, ChangeModel> depMap = TimmyData.getInstance().getDepartmentMap();

        DataPoint[] dataPointsMorale = new DataPoint[depMap.size()];
        DataPoint[] dataPointsHumour = new DataPoint[depMap.size()];
        DataPoint[] dataPointsSkill = new DataPoint[depMap.size()];
        int i = 0;
        for (String dep : depMap.keySet()){
            dataPointsMorale[i] = new DataPoint(i, depMap.get(dep).getMorale());
            dataPointsHumour[i] = new DataPoint(i, depMap.get(dep).getHumour());
            dataPointsSkill[i] = new DataPoint(i, depMap.get(dep).getSkill());
            i++;
        }

        seriesMorale.resetData(dataPointsMorale);
        seriesHumour.resetData(dataPointsHumour);
        seriesSkill.resetData(dataPointsSkill);
    }

    @Override
    public void onDataItemUpdated(Object data) {
        super.onDataItemUpdated(data);

        updateCharts();
    }
}
