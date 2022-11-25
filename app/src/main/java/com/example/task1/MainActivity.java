package com.example.task1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import adapter.CustomAdapter;
import adapter.CustomComparisonAdapter;
import model.ComparisonListModel;
import model.ListModel;

public class MainActivity extends AppCompatActivity {
    //declaration of variables and layout fields
    TextView comparePlans,didNotReceive,resendOtp,timerTextView;
    ListView listView;
    ImageView close;
    GridView gridView;
    ConstraintLayout blur;
    Spinner coverageSpinner,policyTermSpinner,paymentSpinner;


    ArrayList<ComparisonListModel> comparisonList;
    ArrayList<ListModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialization of variables and layout fields
        listView=findViewById(R.id.listView1);
        comparePlans=findViewById(R.id.comparePlans);
        blur=findViewById(R.id.blurLayout);
        coverageSpinner=findViewById(R.id.coverageSpinner);
        policyTermSpinner=findViewById(R.id.policyTermSpinner);
        paymentSpinner=findViewById(R.id.paymentTypeSpinner);
        list=new ArrayList<>();
        comparisonList=new ArrayList<>();
        callData();
        getOTP();

    }

    //Method for getting OTP Pop up
    //Using Layout inflater and Dialog
    private void getOTP() {
        try{
            LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            //inflating the custom layout otp_pop_up
            View v=inflater.inflate(R.layout.otp_pop_up,null,false);
            Dialog d=new Dialog(MainActivity.this);
            //setting the pop-up layout to Dialog d
            d.setContentView(v);
            d.create();
            d.setCancelable(false);
            // making layout blur to be visible.
            blur.setVisibility(View.VISIBLE);
            d.show();
            didNotReceive=v.findViewById(R.id.didntReceiveOtp);
            resendOtp=v.findViewById(R.id.resendOtp);
            timerTextView=v.findViewById(R.id.timerTextView);
            getTimer();
            try {
                resendOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resendOtp.setVisibility(View.INVISIBLE);
                        didNotReceive.setVisibility(View.INVISIBLE);
                        timerTextView.setVisibility(View.VISIBLE);
                        getTimer();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            EditText getOtp=v.findViewById(R.id.editTextInOTP);
            Button submit= v.findViewById(R.id.submitButtonInOTP);
            //setting up the onclick listener to the submit button
            try {
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if length of the otp is less than 3 an alert dialog will be created
                        // else will be proceeded with next iterations
                        if (getOtp.length() >= 3) {
                            //making the blur layout to be gone
                            blur.setVisibility(View.GONE);
                            d.dismiss();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setCancelable(false);
                            alert.setMessage(R.string.alert_for_otp);
                            alert.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alert.show();
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getTimer() {
        try {
            new CountDownTimer(60000, 1000) {

                @Override
                public void onTick(long l) {
                    timerTextView.setText(new SimpleDateFormat("00:ss").format(new Date(l)));
                }

                @Override
                public void onFinish() {
                    timerTextView.setVisibility(View.INVISIBLE);
                    resendOtp.setVisibility(View.VISIBLE);
                    didNotReceive.setVisibility(View.VISIBLE);
                }
            }.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // method for getting Data into the custom list view and the spinner and for the custom compare plans
    // compare plans is being a pop-up window by use of Layout inflater and Pop up window class
    private void callData(){
        try {
            try {
                //adding the data to the list model
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
                        PopupWindow pw=new PopupWindow(inflater.inflate(R.layout.pop_up_layout,null,false)
                                , WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
                        pw.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM,0,0);


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

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //closing the application
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}