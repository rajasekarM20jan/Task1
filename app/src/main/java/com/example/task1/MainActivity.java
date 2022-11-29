package com.example.task1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BlurMaskFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import adapter.CustomAdapter;
import adapter.CustomComparisonAdapter;
import model.ComparisonListModel;
import model.GetAllCoverageAmount;
import model.ListModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static String UniqueID;
    private static String Latitude;
    private static String Longitude;
    private static String Address1;
    LocationManager locationManager;
    //declaration of variables and layout fields
    TextView comparePlans,didNotReceive,resendOtp,timerTextView;
    ListView listView;
    ImageView close;
    GridView gridView;
    ConstraintLayout blur;
    String resultData;
    ArrayList coverageArray;
    static String uniqueidval;
    Spinner coverageSpinner,policyTermSpinner,paymentSpinner;
    ArrayList<GetAllCoverageAmount> getCoverageAmount;
    ArrayList<ComparisonListModel> comparisonList;
    ArrayList<ListModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialization of variables and layout fields

        uniqueidval = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        listView = findViewById(R.id.listView1);
        comparePlans = findViewById(R.id.comparePlans);
        blur = findViewById(R.id.blurLayout);
        coverageSpinner = findViewById(R.id.coverageSpinner);
        policyTermSpinner = findViewById(R.id.policyTermSpinner);
        paymentSpinner = findViewById(R.id.paymentTypeSpinner);
        list = new ArrayList<>();
        getCoverageAmount=new ArrayList<>();
        comparisonList = new ArrayList<>();
        callData();
        getOTP();

        getLocation();
        InsertMobileparameters(MainActivity.this);

        OTPValidate();
        getAllCoverageAmount();


        coverageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("Selected value is : "+getCoverageAmount.get(i).getCoverageAmountText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void OTPValidate(){

            Thread thread = new Thread(new Runnable() {

                public void run() {

                    String postURL = getString(R.string.base_url) +"/uad/Account/Login";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    String insertString = Details.toString();
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(postURL)
                            .header("fingerprint","79f59867dce4e2910619d92186c090a9")
                            .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjE4NTcwMzk5QzM0MjlDMUFDNjk3MTk5MzZCNDI3Q0Y5OUU2MDExQUQiLCJ0eXAiOiJKV1QifQ.eyJzZXNzaW9uSUQiOiIyYzExZGE0OC0wMjljLTRiODgtOTI2Yi1jODViNTI0ZmFmZTMiLCJuYmYiOjE2Njk3MTQ5NDQsImV4cCI6MTY2OTcxODU0NCwiaWF0IjoxNjY5NzE0OTQ0LCJpc3MiOiJCQzcyRTczQUNBQkY0NzcyOEE3RUQ2MTlDREM3OUMwMSIsImF1ZCI6IjRENTIxMkI3QzA3NTQ0OTJCNjZDRDNCRDM1QzFGNzJBIn0.W7DGRcz2hdFxwXOeGmt9mlRoL_3pzFvCOuMkCCD6penxeIXL1Q3pgibJtY90f-EtcOt64vKpkJK7nUpSKPoEd4mo7ls4jA7KtBHa_HcVKAEJyaR8UwTbOYwUOPlCnqT049Yyu6cf6Mf8WR-7ILkjJWs4Q5iq-RSCF18LDEc7uWsxuLZWVhFhrKeWxquwnK_wRPYrc2_JLUN4d2VV8Sw-lI5u5DeAtT50wgtq3boB01ArVPq8E1QG_LxrzGpyq7tWCtPLVoW3mD2fbThCY5rLXJKO1vd4nTjL523LCafk5PsU2DhIKoCmMelIsJWNBXkWmDaaBlFf8tCxXiYeL71cPLNduHK2FQvx6cpQM5HjUBa5ZmhjOPTnLSdbED4L8hBZedesQmKOgfru3A-6j6SLjQhgoZoN5QurWl2jo3WgwkHP-FXNoxceaLpKPuizn2LC7jFmi-u38-BrcGK1dB3R3cewnN9IQEchKDUs7idCH__gLigOsvhxbhS9xVvjk7Bqk9rqglGmwhvrB3B3tKbr0kDaaNANfBXa9b9rYLU1gUS6S9mbmOkaIDw66eChY8LwcfxLX0wWav_A34aU5ZbX0HTN6aR69fSeIvY7LfVucOh7L718PtbRdXUiHCrh_7RU1949dTIYeLnF86VLW2EkepKTEbCbhwK6BgDfJCJCOFw")
                            .header("clientinfo", "{  \"deviceID\": \"79f59867dce4e2910619d92186c090a9\",  \"deviceID2\": \"79f59867dce4e2910619d92186c090a9\",  \"deviceTimeZone\": \"Gulf Standard Time\",  \"deviceDateTime\": \"23-Nov-2021 08:35 AM\",  \"deviceIpAddress\": \"168.122.1.1\",  \"deviceLatitude\": \"25.1215284\",  \"deviceLongitude\": \"56.3514986\",  \"deviceType\": \"Android\",  \"deviceModel\": \"samsung - SM-A307FN\",  \"deviceVersion\": \"10\",  \"deviceUserID\": \"fGlsj3U6SN\",  \"deviceAppVersion\": \"1.0.8\",  \"deviceIsJailBroken\": true}")
                            .post(body)
                            .build();
                    Response staticResponse = null;
                    try {
                        staticResponse = client.newCall(request).execute();
                        int statuscode = staticResponse.code();
                        if (statuscode == 401) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    return;
                                }
                            });
                        } else {
                            String staticRes = staticResponse.body().string();
                            Log.i(null, staticRes);
                            final JSONObject staticJsonObj = new JSONObject(staticRes);
                            if (staticJsonObj.getInt("rcode") == 200) {

                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            thread.start();

        }



    public void getAllCoverageAmount(){

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String postURL = getString(R.string.base_url)+"/ti/Coverage/GetAllCoverageAmount";
                final MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();
                JsonObject Details = new JsonObject();
                String insertString = Details.toString();
//        String insertString = "{\n" +
//                "  \"quotationID\": \"IQ-AAA2569\",\n" +
//                "  \"productID\": \"85\",\n" +
//                "  \"vehicleTypeID\": \"27\"\n" +
//                "}";
                RequestBody body = RequestBody.create(JSON, insertString);
                Request request = new Request.Builder()
                        .url(postURL)
                        .header("fingerprint","79f59867dce4e2910619d92186c090a9")
                        .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjE4NTcwMzk5QzM0MjlDMUFDNjk3MTk5MzZCNDI3Q0Y5OUU2MDExQUQiLCJ0eXAiOiJKV1QifQ.eyJzZXNzaW9uSUQiOiIyYzExZGE0OC0wMjljLTRiODgtOTI2Yi1jODViNTI0ZmFmZTMiLCJuYmYiOjE2Njk3MTQ5NDQsImV4cCI6MTY2OTcxODU0NCwiaWF0IjoxNjY5NzE0OTQ0LCJpc3MiOiJCQzcyRTczQUNBQkY0NzcyOEE3RUQ2MTlDREM3OUMwMSIsImF1ZCI6IjRENTIxMkI3QzA3NTQ0OTJCNjZDRDNCRDM1QzFGNzJBIn0.W7DGRcz2hdFxwXOeGmt9mlRoL_3pzFvCOuMkCCD6penxeIXL1Q3pgibJtY90f-EtcOt64vKpkJK7nUpSKPoEd4mo7ls4jA7KtBHa_HcVKAEJyaR8UwTbOYwUOPlCnqT049Yyu6cf6Mf8WR-7ILkjJWs4Q5iq-RSCF18LDEc7uWsxuLZWVhFhrKeWxquwnK_wRPYrc2_JLUN4d2VV8Sw-lI5u5DeAtT50wgtq3boB01ArVPq8E1QG_LxrzGpyq7tWCtPLVoW3mD2fbThCY5rLXJKO1vd4nTjL523LCafk5PsU2DhIKoCmMelIsJWNBXkWmDaaBlFf8tCxXiYeL71cPLNduHK2FQvx6cpQM5HjUBa5ZmhjOPTnLSdbED4L8hBZedesQmKOgfru3A-6j6SLjQhgoZoN5QurWl2jo3WgwkHP-FXNoxceaLpKPuizn2LC7jFmi-u38-BrcGK1dB3R3cewnN9IQEchKDUs7idCH__gLigOsvhxbhS9xVvjk7Bqk9rqglGmwhvrB3B3tKbr0kDaaNANfBXa9b9rYLU1gUS6S9mbmOkaIDw66eChY8LwcfxLX0wWav_A34aU5ZbX0HTN6aR69fSeIvY7LfVucOh7L718PtbRdXUiHCrh_7RU1949dTIYeLnF86VLW2EkepKTEbCbhwK6BgDfJCJCOFw")
                        .header("clientinfo", "{  \"deviceID\": \"79f59867dce4e2910619d92186c090a9\",  \"deviceID2\": \"79f59867dce4e2910619d92186c090a9\",  \"deviceTimeZone\": \"Gulf Standard Time\",  \"deviceDateTime\": \"23-Nov-2021 08:35 AM\",  \"deviceIpAddress\": \"168.122.1.1\",  \"deviceLatitude\": \"25.1215284\",  \"deviceLongitude\": \"56.3514986\",  \"deviceType\": \"Android\",  \"deviceModel\": \"samsung - SM-A307FN\",  \"deviceVersion\": \"10\",  \"deviceUserID\": \"fGlsj3U6SN\",  \"deviceAppVersion\": \"1.0.8\",  \"deviceIsJailBroken\": true}")
                        .post(body)
                        .build();
                Response staticResponse = null;
                try {
                    staticResponse = client.newCall(request).execute();
                    int statuscode = staticResponse.code();
                    if (statuscode == 401) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                return;
                            }
                        });
                    } else {
                        String staticRes = staticResponse.body().string();
                        Log.i(null, staticRes);
                        final JSONObject staticJsonObj = new JSONObject(staticRes);
                        if (staticJsonObj.getInt("rcode") == 200) {
                            JSONObject rObj=staticJsonObj.getJSONObject("rObj");
                            JSONArray getAllCoverageAmount=rObj.getJSONArray("getAllCoverageAmount");
                            for(int i=0;i<getAllCoverageAmount.length();i++){
                                JSONObject index=getAllCoverageAmount.getJSONObject(i);
                                String coverageAmountText=index.getString("coverageAmountText");
                                int coverageAmount=index.getInt("coverageAmount");
                                getCoverageAmount.add(new GetAllCoverageAmount(coverageAmountText,coverageAmount));
                            }

                            coverageArray=new ArrayList<>();
                            for (int i=0;i<getCoverageAmount.size();i++){
                                coverageArray.add(getCoverageAmount.get(i).getCoverageAmountText());
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayAdapter arrayAdapter1 = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, coverageArray);
                                    try {
                                        arrayAdapter1.setDropDownViewResource(R.layout.custom_spinner_layout);
                                        MainActivity.this.coverageSpinner.setAdapter(arrayAdapter1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });



                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        thread.start();

    }
   /* public void MobileActionlog(Context context)
    {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("unique id: "+uniqueidval);
                            OkHttpClient client=new OkHttpClient();
                            String url=getString(R.string.base_url)+"/ti/Coverage/GetAllCoverageAmount";
                            String body="";
                            RequestBody rb= RequestBody.create(MediaType.parse("application/json; charset=utf-8"),body);
                            String token="eyJhbGciOiJSUzI1NiIsImtpZCI6IjE4NTcwMzk5QzM0MjlDMUFDNjk3MTk5MzZCNDI3Q0Y5OUU2MDExQUQiLCJ0eXAiOiJKV1QifQ.eyJzZXNzaW9uSUQiOiIyMTk0Y2QxOS1hMDU1LTRkY2UtODM1Yi02OWY0YWQ5NDE1MjgiLCJuYmYiOjE2Njk3MTAwNDYsImV4cCI6MTY2OTcxMzY0NiwiaWF0IjoxNjY5NzEwMDQ2LCJpc3MiOiJCQzcyRTczQUNBQkY0NzcyOEE3RUQ2MTlDREM3OUMwMSIsImF1ZCI6IjRENTIxMkI3QzA3NTQ0OTJCNjZDRDNCRDM1QzFGNzJBIn0.BuAH4P5ova8kNxnFDwuA_T6SA0hkv2ujmKMFXuaZ4jxX18YISV8PH_Ggoi0GAivdRTkiDouE5YZmFwg-o-fqKWF64KTi8pfqVQzybkoiHnscAVdxEWUH4G8D9UhKzEc6qmugnu-wWdlJcqU15_Issytb5GjWYtIy88UDaQzNhAmj25iptL9vgRbd9sHGHrrZZDe2zdsZUnRZeeZUiWQhOte00qHGkszgki2wu8QndTQNA0c0NA4daEuyHC6aSlPrZdE3L7-MyYrkiJRKJb0v-lHq31sHcdCd2e3Ra9IgX4sEHyOtLeh9Nqs7ztMahREbAaO1J94pwX4UJNhFrwD37tqZlRz-YR9DNOPC-vRy5-3ywBf5vbh74QZi08G4iZerIEOWhwMRY9GsKgjjeb2Q-t3zNZBj2bxWEnWfUbLB7X2Mikmg7btLl_UgYsUwo9MnxwpZILp2aIOrONiTgLyKQF9aYyAJWxetO5fNtcqwWUkZsLYHjxfn4Bsaz5an-cSz3Azg4Yjf7OVWHP7-tyZLkKnhN0AE8ToE4KE7kEMWenBZTc5hC_tYMDWlK8VvCmvauJ5jYnore6KFcrJkDsZNECN5CBmXAAEUX5RBpbEhuMgrkVic4RVmw5k1QIjsmKvg50UcS-RVDQr3wkRbTo63nN9C3qPXgEi8VVeJ4RwbiME";
                            Request request = new Request.Builder()
                                    .url(url)
                                    .addHeader("fingerprint",uniqueidval)
                                    .addHeader("clientInfo",MainActivity.this.InsertMobileparameters(MainActivity.this))
                                    .addHeader("Authorization","Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjE4NTcwMzk5QzM0MjlDMUFDNjk3MTk5MzZCNDI3Q0Y5OUU2MDExQUQiLCJ0eXAiOiJKV1QifQ.eyJzZXNzaW9uSUQiOiIyMTk0Y2QxOS1hMDU1LTRkY2UtODM1Yi02OWY0YWQ5NDE1MjgiLCJuYmYiOjE2Njk3MTAwNDYsImV4cCI6MTY2OTcxMzY0NiwiaWF0IjoxNjY5NzEwMDQ2LCJpc3MiOiJCQzcyRTczQUNBQkY0NzcyOEE3RUQ2MTlDREM3OUMwMSIsImF1ZCI6IjRENTIxMkI3QzA3NTQ0OTJCNjZDRDNCRDM1QzFGNzJBIn0.BuAH4P5ova8kNxnFDwuA_T6SA0hkv2ujmKMFXuaZ4jxX18YISV8PH_Ggoi0GAivdRTkiDouE5YZmFwg-o-fqKWF64KTi8pfqVQzybkoiHnscAVdxEWUH4G8D9UhKzEc6qmugnu-wWdlJcqU15_Issytb5GjWYtIy88UDaQzNhAmj25iptL9vgRbd9sHGHrrZZDe2zdsZUnRZeeZUiWQhOte00qHGkszgki2wu8QndTQNA0c0NA4daEuyHC6aSlPrZdE3L7-MyYrkiJRKJb0v-lHq31sHcdCd2e3Ra9IgX4sEHyOtLeh9Nqs7ztMahREbAaO1J94pwX4UJNhFrwD37tqZlRz-YR9DNOPC-vRy5-3ywBf5vbh74QZi08G4iZerIEOWhwMRY9GsKgjjeb2Q-t3zNZBj2bxWEnWfUbLB7X2Mikmg7btLl_UgYsUwo9MnxwpZILp2aIOrONiTgLyKQF9aYyAJWxetO5fNtcqwWUkZsLYHjxfn4Bsaz5an-cSz3Azg4Yjf7OVWHP7-tyZLkKnhN0AE8ToE4KE7kEMWenBZTc5hC_tYMDWlK8VvCmvauJ5jYnore6KFcrJkDsZNECN5CBmXAAEUX5RBpbEhuMgrkVic4RVmw5k1QIjsmKvg50UcS-RVDQr3wkRbTo63nN9C3qPXgEi8VVeJ4RwbiME")
                                    .post(rb)
                                    .build();
                            Response response= null;
                            try {
                                response = client.newCall(request).execute();
                                System.out.println("My Response123 : "+response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            thread.start();
        }
        catch (Exception ex)
        {
            ex.getStackTrace();
        }
    }*/
    //Method for getting OTP Pop up
    //Using Layout inflater and Dialog
    private void getOTP() {
        try{
            LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            //inflating the custom layout otp_pop_up
            View v=inflater.inflate(R.layout.otp_pop_up,null,false);
            v.setBackgroundDrawable(getResources().getDrawable(R.drawable.comparison_bg));
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
                        if (getOtp.length()>=3) {
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
                        View view2 =inflater.inflate(R.layout.pop_up_layout,null,false);
                        view2.setBackgroundDrawable(getResources().getDrawable(R.drawable.comparison_bg));
                        PopupWindow pw=new PopupWindow(view2, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
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
    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }
    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(getActivity(), "GPS and Internet enabled", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onLocationChanged(final Location location) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                SharedPreferences sharedpreferences = getSharedPreferences("LocationPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Latitude", String.valueOf(latitude));
                editor.putString("Longitude", String.valueOf(longitude));
                editor.commit();
                SharedPreferences sp=getSharedPreferences("LocationPref",MODE_PRIVATE);
                Latitude=sp.getString("Latitude",null);
                Longitude=sp.getString("Longitude",null);
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        String addressDet = address.getAddressLine(0);
                        SharedPreferences locashared = getSharedPreferences("LocationCurrent", MODE_PRIVATE);
                        SharedPreferences.Editor editorloca = locashared.edit();
                        editorloca.putString(Address1, addressDet);
                        editorloca.commit();
                    }

                } catch (IOException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }
        });
    }
    public static String InsertMobileparameters(Context context) {
        try
        {
            String rooteddevice;
            if(RootUtil.isDeviceRooted() == true)
            {
                rooteddevice = "1";
            }
            else
            {
                rooteddevice = "0";
            }

            String androidOS = Build.VERSION.RELEASE;
            String model = Build.MANUFACTURER + " - " + Build.MODEL;
            final String address1 = Latitude+","+Longitude;
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            String ipaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            JsonObject mobileparameters = new JsonObject();
            mobileparameters.addProperty("imeino1", uniqueidval);
            mobileparameters.addProperty("imeino2", uniqueidval);
            mobileparameters.addProperty("timezone", TimeZone.getDefault().getDisplayName());
            mobileparameters.addProperty("currentdatetime", java.text.DateFormat.getDateTimeInstance().format(new Date()));
            mobileparameters.addProperty("Address", address1);
            mobileparameters.addProperty("latitude", Latitude);
            mobileparameters.addProperty("longitude", Longitude);
            mobileparameters.addProperty("IpAddress", ipaddress);
            mobileparameters.addProperty("mobileType", "Android");
            mobileparameters.addProperty("fireBaseuserid", "123456");
            mobileparameters.addProperty("mobileModel", model);
            mobileparameters.addProperty("mobileOSVersion", androidOS);
            mobileparameters.addProperty("appVersion", "1.0.6");
            mobileparameters.addProperty("IsJailBroken", rooteddevice);
            System.out.println(mobileparameters);
            String insertmobileString = mobileparameters.toString();

//            encryptedSHA = "";
//            String sourceStr = uniqueidval + ipaddress;
//            try {
//                encryptedSHA = AESUtils.encrypt(sourceStr);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return insertmobileString;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}