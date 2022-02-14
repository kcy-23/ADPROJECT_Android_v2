package iss.nus.adproject_android_v2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Userdashboard extends AppCompatActivity {

    float[] mealTrack = new float[2];
    TextView viewCurrentGoal;
    PieChart pieChart;
    PieData pieData;
    List<PieEntry> pieEntryList = new ArrayList<>();
    NavigationBarView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdashboard);

        LinearLayout lv1 = (LinearLayout) findViewById(R.id.linear);
        viewCurrentGoal = findViewById(R.id.viewCurrentGoal);

        //get userId
        Integer userId=3;

        //get and set current goal to view
        String url1 = "http://192.168.1.176:8080/api/dashboard/getCurrentGoal/" +userId;
        getCurrentGoal(url1);

        //get meal track score
        String url2 = "http://192.168.1.176:8080/api/dashboard/getTrack/"+userId;
        getMealTrack(url2);

        pieChart = findViewById(R.id.pieChart);

        //bottom navigation bar
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.meal: break;
                    case R.id.path: break;
                    case R.id.add: break;
                    case R.id.friends: break;
                    case R.id.settings:
                        Intent settings = new Intent(Userdashboard.this, Settings.class);
                        startActivity(settings);
                        break;

                }
                return true;
            }
        });



    }



    private String getCurrentGoal(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Userdashboard.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                System.out.println("This information return from server side");
                System.out.println(res);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Userdashboard.this, "success", Toast.LENGTH_SHORT).show();
                        viewCurrentGoal.setText(res);

                    }
                });
            }
        });
        return "";
    }

    private String getMealTrack(String url) {
        OkHttpClient client = new OkHttpClient();
        client.newBuilder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Userdashboard.this, "server error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res2 = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(res2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray JArray = json.getJSONArray("mealTrack");
                    System.out.println("JSONArray:");
                    System.out.println(JArray);
                    //float mealTrack[] = new float[2];
                    for (int i = 0; i < JArray.length(); i++) {
                        mealTrack[i] = Float.parseFloat(JArray.getString(i));
                        System.out.println(mealTrack[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Userdashboard.this, "success", Toast.LENGTH_SHORT).show();
                        //mealTrack = calculateData(mealTrack);
                        //MyGraphview graphview = new MyGraphview(MainActivity.this, mealTrack);
                        //LinearLayout lv1 = (LinearLayout) findViewById(R.id.linear);
                        //lv1.addView(graphview);

                        int onTrackPercent = (int) (mealTrack[0]/ (mealTrack[0]+mealTrack[1])*100);

                        pieEntryList.add(new PieEntry(mealTrack[0],"On Track"));
                        pieEntryList.add(new PieEntry(mealTrack[1],"Off Track"));
                        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"Progress");
                        pieDataSet.setColors(getResources().getColor(R.color.blueOnT),
                                getResources().getColor(R.color.greyOffT));
                        pieData = new PieData(pieDataSet);
                        pieData.setValueTextSize(15f);
                        pieData.setValueTextColor(Color.BLACK);
                        pieChart.setEntryLabelColor(Color.BLACK);
                        pieData.setValueFormatter(new PercentFormatter(pieChart));
                        pieChart.setUsePercentValues(true);
                        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        pieChart.setCenterText(onTrackPercent+ "% On Track");


                        pieChart.getLegend().setEnabled(false);
                        pieChart.getDescription().setEnabled(false);

                        pieChart.setData(pieData);
                        pieChart.invalidate();


                    }
                });
            }
        });
        return "";
    }

}