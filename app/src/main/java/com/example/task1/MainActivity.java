package com.example.task1;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import adapter.CustomAdapter;
import adapter.CustomComparisonAdapter;
import model.ComparisonListModel;
import model.ListModel;

public class MainActivity extends AppCompatActivity {

    TextView comparePlans;
    ListView listView;
    ImageView close;
    GridView gridView;
    Spinner coverageSpinner,policyTermSpinner,paymentSpinner;

    ArrayList<ComparisonListModel> comparisonList;
    ArrayList<ListModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView1);
        comparePlans=findViewById(R.id.comparePlans);
        coverageSpinner=findViewById(R.id.coverageSpinner);
        policyTermSpinner=findViewById(R.id.policyTermSpinner);
        paymentSpinner=findViewById(R.id.paymentTypeSpinner);
        list=new ArrayList<>();
        comparisonList=new ArrayList<>();
        callData();

    }

    private void callData(){
        try {
            try {
                list.add(new ListModel(getString(R.string.policy_term), getString(R.string.till_you),
                        getString(R.string.claim_settlement), getString(R.string.lumpsum),
                        getString(R.string.income), getString(R.string.pre_medical),
                        getString(R.string.cover), getString(R.string.limited_pay),
                        getString(R.string.priceInc), getString(R.string.download),
                        getString(R.string.kshButtonTxt), R.drawable.heritage));

                list.add(new ListModel(getString(R.string.policy_term), getString(R.string.till_you),
                        getString(R.string.claim_settlement), getString(R.string.lumpsum),
                        getString(R.string.income), getString(R.string.pre_medical),
                        getString(R.string.cover), getString(R.string.limited_pay),
                        getString(R.string.priceInc), getString(R.string.download),
                        getString(R.string.kshButtonTxt), R.drawable.gainsurance));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                comparisonList.add(new ComparisonListModel(getString(R.string.company_name1), R.drawable.heritage));
                comparisonList.add(new ComparisonListModel(getString(R.string.company_name2), R.drawable.gainsurance));
                comparisonList.add(new ComparisonListModel(getString(R.string.company_name2), R.drawable.gainsurance));
                comparisonList.add(new ComparisonListModel(getString(R.string.company_name1), R.drawable.heritage));
            } catch (Exception e) {
                e.printStackTrace();
            }

            CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.custom_list_layout, list);
            try {
                listView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] coverageArray = new String[]{getString(R.string.million_10), getString(R.string.million_5), getString(R.string.million_3)};

            ArrayAdapter arrayAdapter1 = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, coverageArray);
            try {
                arrayAdapter1.setDropDownViewResource(R.layout.custom_spinner_layout);
                coverageSpinner.setAdapter(arrayAdapter1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] paymentArray = new String[]{getString(R.string.monthly), getString(R.string.quarterly), getString(R.string.yearly)};

            ArrayAdapter arrayAdapter2 = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, paymentArray);
            try {
                arrayAdapter2.setDropDownViewResource(R.layout.custom_spinner_layout);
                paymentSpinner.setAdapter(arrayAdapter2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] policyTermArray = new String[]{getString(R.string.years_28), getString(R.string.years_20), getString(R.string.years_15)};

            ArrayAdapter arrayAdapter3 = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, policyTermArray);
            try {
                arrayAdapter3.setDropDownViewResource(R.layout.custom_spinner_layout);
                policyTermSpinner.setAdapter(arrayAdapter3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                comparePlans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        PopupWindow pw=new PopupWindow(inflater.inflate(R.layout.pop_up_layout,null,false), WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
                        pw.showAtLocation(findViewById(R.id.main), Gravity.CENTER,0,0);

                        close=pw.getContentView().findViewById(R.id.close);
                        gridView=pw.getContentView().findViewById(R.id.gridview);

                        CustomComparisonAdapter csAdapter = new CustomComparisonAdapter(MainActivity.this, R.layout.custom_comparison_layout, comparisonList);
                        gridView.setAdapter(csAdapter);
                        try {
                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pw.dismiss();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}