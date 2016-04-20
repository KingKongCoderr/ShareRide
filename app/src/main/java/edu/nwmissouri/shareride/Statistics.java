package edu.nwmissouri.shareride;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Statistics extends AppCompatActivity {
private RelativeLayout mstatisticslayout;
    private PieChart mChart;
    private int[] ride_offers={5,10,20,35,40};
    private String[] cities={"maryville","st.joseph","jefferson city","st.louis","Kansas city"};

    Client kinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mstatisticslayout = (RelativeLayout)findViewById(R.id.statisticslayout);
        mChart=new PieChart(this);
        mstatisticslayout.addView(mChart);
        mstatisticslayout.setBackgroundColor(Color.LTGRAY);

        kinveyClient = new Client.Builder("kid_ZJCDL-Jpy-", "7ba9e5e0015849b790845e669ab87992", this.getApplicationContext()).build();

        mChart.setMinimumHeight(1300);
        mChart.setMinimumWidth(950);
        mChart.setDescription("Ride offers from cities");
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleRadius(7);
        mChart.setTransparentCircleRadius(10);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        addpieData();

        Legend l=mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setXEntrySpace(1);
        l.setTextSize(6f);
        l.setYEntrySpace(1);
    }

    private void addpieData() {

        kinveyClient.appData("RideCollection", Ride.class).get(new KinveyListCallback<Ride>() {

            @Override
            public void onSuccess(Ride[] rides) {
                ArrayList<String> states = new ArrayList<String>();
                String[] cities;
                int[] stateCount;
                Map<String ,Integer> citiRides = new HashMap<String, Integer>();
                for(Ride ride:rides){
                    String[] bit = ride.getRouteFrom().split(",");
                    String state = bit[bit.length-2];
                    if(!states.contains(state)){
                        states.add(state);
                        citiRides.put(state,1);
                    }else{
                        citiRides.put(state, citiRides.get(state)+1);
                    }
                }

                cities = new String[citiRides.size()];
                stateCount = new int[citiRides.size()];
                Iterator it = citiRides.entrySet().iterator();
                int count= 0;
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    cities[count] = (String) pair.getKey();
                    stateCount[count++] = (int) pair.getValue();
                    it.remove(); // avoids a ConcurrentModificationException
                }


                ArrayList<Entry> offervals=new ArrayList<Entry>();

                for(int i=0;i<stateCount.length;i++){
                    offervals.add(new Entry(stateCount[i],i));
                }

                ArrayList<String> citievals=new ArrayList<String>();
                for(int j=0;j<cities.length;j++){
                    citievals.add(cities[j]);
                }
                PieDataSet dataSet = new PieDataSet(offervals,"ride offers");
                dataSet.setHighlightEnabled(true);
                dataSet.setSliceSpace(3);
                dataSet.setSelectionShift(5);

                ArrayList<Integer> colors=new ArrayList<Integer>();
                for(int c: ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);
                for(int c: ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);
                for(int c: ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);

                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.BLACK);

                PieData data=new PieData(citievals,dataSet);
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.BLACK);

                mChart.setData(data);

                mChart.highlightValues(null);

                mChart.invalidate();
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });


    }
}
