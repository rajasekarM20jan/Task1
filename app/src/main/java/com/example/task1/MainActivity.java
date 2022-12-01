package com.example.task1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.RadioGroup;
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
import model.PaymentType;
import model.Term;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {

    //declaration of variables and layout fields
    private static String UniqueID;
    private static String Latitude;
    private static String Longitude;
    private static String Address1;
    LocationManager locationManager;
    TextView comparePlans,didNotReceive,resendOtp,timerTextView;
    ListView listView;
    ImageView close;
    GridView gridView;
    ConstraintLayout blur;
    String resultData;
    ArrayList coverageArray;
    static String uniqueidval;
    String error502;
    Spinner coverageSpinner,policyTermSpinner,paymentSpinner;
    ArrayList<GetAllCoverageAmount> getCoverageAmount;
    ArrayList<Term> getTermArray;
    ArrayList<ComparisonListModel> comparisonList;
    ArrayList<ListModel> list;
    ArrayList<PaymentType> getPaymentTypeArray;
    ArrayList policyTermArray;
    ArrayList paymentTypeArray;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialization of variables and layout fields
        error502=getString(R.string.error_msg);
        uniqueidval = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println("uniqueID : "+uniqueidval);
        listView = findViewById(R.id.listView1);
        comparePlans = findViewById(R.id.comparePlans);
        blur = findViewById(R.id.blurLayout);
        coverageSpinner = findViewById(R.id.coverageSpinner);
        policyTermSpinner = findViewById(R.id.policyTermSpinner);
        paymentSpinner = findViewById(R.id.paymentTypeSpinner);
        list = new ArrayList<>();
        policyTermArray=new ArrayList<>();
        getTermArray=new ArrayList<>();
        getCoverageAmount=new ArrayList<>();
        comparisonList = new ArrayList<>();
        coverageArray = new ArrayList<>();
        paymentTypeArray=new ArrayList<>();
        getPaymentTypeArray=new ArrayList<>();
        //calling the initial methods of the layout
        callData();
        getOTP();
        getLocation();
        coverageSpinner.setOnItemSelectedListener(MainActivity.this);
        policyTermSpinner.setOnItemSelectedListener(MainActivity.this);
        paymentSpinner.setOnItemSelectedListener(MainActivity.this);
    }
//method for getting all payout types/payment types
    public void getAllPayout(){

        try {
            Thread thread = new Thread(new Runnable() {

                public void run() {
                    SharedPreferences sf = getSharedPreferences("token", MODE_PRIVATE);
                    String deviceData = InsertMobileparameters(MainActivity.this);
                    System.out.println(deviceData);
                    String token = sf.getString("token", "hi");
                    String postURL = "https://uat-integrationportal.insure.digital/api/v1/ip/ti/PayoutType/GetAllPayoutType";
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
                            .header("fingerprint", uniqueidval)
                            .header("Authorization", "Bearer " + token)
                            .header("clientinfo", deviceData)
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
                                try {
                                    JSONObject rObj = staticJsonObj.getJSONObject("rObj");
                                    JSONArray getAllCoverageAmount = rObj.getJSONArray("getAllPayOutType");
                                    for (int i = 0; i < getAllCoverageAmount.length(); i++) {
                                        JSONObject index = getAllCoverageAmount.getJSONObject(i);
                                        String payoutTypeText = index.getString("payoutTypeName");
                                        int payoutTypeID = index.getInt("payoutTypeID");
                                        getPaymentTypeArray.add(new PaymentType(payoutTypeText, payoutTypeID));
                                    }
                                    for (int i = 0; i < getPaymentTypeArray.size(); i++) {
                                        paymentTypeArray.add(getPaymentTypeArray.get(i).getPayoutTypeName());
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, paymentTypeArray);
                                            try {
                                                arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_layout);
                                                MainActivity.this.paymentSpinner.setAdapter(arrayAdapter);
                                                getForm();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (staticJsonObj.getInt("rcode") == 500) {
                                JSONArray rmsg = staticJsonObj.getJSONArray("rmsg");
                                JSONObject index = rmsg.getJSONObject(0);
                                String errorText = index.getString("errorText");
                                String trnId = staticJsonObj.getString("trnID");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAlertDialog(errorText, trnId);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAlertDialog(error502, "AKI-00015");
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//method of getting all policy terms.
    public void getAllTerm(){
        try{
            Thread thread = new Thread(new Runnable() {

                public void run() {
                    SharedPreferences sf=getSharedPreferences("token",MODE_PRIVATE);

                    String deviceData=InsertMobileparameters(MainActivity.this);
                    System.out.println(deviceData);
                    String token=sf.getString("token","hi");
                    String postURL = "https://uat-integrationportal.insure.digital/api/v1/ip/ti/Coverage/GetAllCoverageTill";
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
                            .header("fingerprint",uniqueidval)
                            .header("Authorization", "Bearer "+token)
                            .header("clientinfo", deviceData)
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

                                try {
                                    JSONObject rObj = staticJsonObj.getJSONObject("rObj");
                                    JSONArray getAllCoverageAmount = rObj.getJSONArray("getAllCoverTill");
                                    for (int i = 0; i < getAllCoverageAmount.length(); i++) {
                                        JSONObject index = getAllCoverageAmount.getJSONObject(i);
                                        String coverTillText = index.getString("coverTillText");
                                        int coverTillID = index.getInt("coverTillID");
                                        getTermArray.add(new Term(coverTillText, coverTillID));
                                    }


                                    for (int i = 0; i < getTermArray.size(); i++) {
                                        policyTermArray.add(getTermArray.get(i).getTermText());
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, policyTermArray);
                                            try {
                                                arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_layout);
                                                MainActivity.this.policyTermSpinner.setAdapter(arrayAdapter);
                                                getAllPayout();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else if(staticJsonObj.getInt("rcode") == 500){
                                JSONArray rmsg=staticJsonObj.getJSONArray("rmsg");
                                JSONObject index=rmsg.getJSONObject(0);
                                String errorText=index.getString("errorText");
                                String trnId=staticJsonObj.getString("trnID");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAlertDialog(errorText,trnId);
                                    }
                                });

                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAlertDialog(error502,"AKI-00015");
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
        }catch (Exception e){
            e.printStackTrace();
        }
        }
//method for validating otp
    public void OTPValidate(){
        try{
            Thread thread = new Thread(new Runnable() {

                public void run() {

                    String postURL = "https://uat-integrationportal.insure.digital/api/v1/ip/uad/Account/OTPValidate";
                    final MediaType JSON
                            = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(120, TimeUnit.SECONDS)
                            .writeTimeout(120, TimeUnit.SECONDS)
                            .readTimeout(120, TimeUnit.SECONDS)
                            .build();
                    JsonObject Details = new JsonObject();
                    Details.addProperty("oTPID","de693561-2b3b-4784-ae9b-6811586b64f7");
                    Details.addProperty("oTP","543400");
                    /*String insertString = Details.toString();*/
                    String insertString="{\"oTPID\":\"56b7e8b6-5a92-4fe9-b8ef-187e647b83ac\",\"oTP\":\"578594\"}";
                    RequestBody body = RequestBody.create(JSON, insertString);
                    Request request = new Request.Builder()
                            .url(postURL)
                            .header("fingerprint",uniqueidval)
                            .header("clientinfo", InsertMobileparameters(MainActivity.this))
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
                                JSONObject jobj=staticJsonObj.getJSONObject("rObj");
                                String token=jobj.getString("token");
                                System.out.println(token);

                                SharedPreferences spf=getSharedPreferences("token",MODE_PRIVATE);
                                SharedPreferences.Editor editor=spf.edit();
                                editor.putString("token",token);
                                editor.apply();

                                getAllCoverageAmount();

                            }else if(staticJsonObj.getInt("rcode") == 500){
                                JSONArray rmsg=staticJsonObj.getJSONArray("rmsg");
                                JSONObject index=rmsg.getJSONObject(0);
                                String errorText=index.getString("errorText");
                                String trnId=staticJsonObj.getString("trnID");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAlertDialog(errorText,trnId);
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getAlertDialog(error502,"AKI-00015");
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
        }catch (Exception e){
            e.printStackTrace();
        }
        }
//method for getting all coverage amount
    public void getAllCoverageAmount(){
        try{
        Thread thread = new Thread(new Runnable() {

            public void run() {
                SharedPreferences sf=getSharedPreferences("token",MODE_PRIVATE);
                String deviceData=InsertMobileparameters(MainActivity.this);
                System.out.println(deviceData);
                String token=sf.getString("token","hi");
                String postURL = "https://uat-integrationportal.insure.digital/api/v1/ip/ti/Coverage/GetAllCoverageAmount";
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
                        .header("fingerprint",uniqueidval)
                        .header("Authorization", "Bearer "+token)
                        .header("clientinfo", deviceData)
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

                            try {
                                JSONObject rObj = staticJsonObj.getJSONObject("rObj");
                                JSONArray getAllCoverageAmount = rObj.getJSONArray("getAllCoverageAmount");
                                for (int i = 0; i < getAllCoverageAmount.length(); i++) {
                                    JSONObject index = getAllCoverageAmount.getJSONObject(i);
                                    String coverageAmountText = index.getString("coverageAmountText");
                                    int coverageAmount = index.getInt("coverageAmount");
                                    getCoverageAmount.add(new GetAllCoverageAmount(coverageAmountText, coverageAmount));
                                }


                                for (int i = 0; i < getCoverageAmount.size(); i++) {
                                    coverageArray.add(getCoverageAmount.get(i).getCoverageAmountText());
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayAdapter arrayAdapter1 = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, coverageArray);
                                        try {
                                            arrayAdapter1.setDropDownViewResource(R.layout.custom_spinner_layout);
                                            MainActivity.this.coverageSpinner.setAdapter(arrayAdapter1);

                                            getAllTerm();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else if(staticJsonObj.getInt("rcode") == 500){
                            JSONArray rmsg=staticJsonObj.getJSONArray("rmsg");
                            JSONObject index=rmsg.getJSONObject(0);
                            String errorText=index.getString("errorText");
                            String trnId=staticJsonObj.getString("trnID");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getAlertDialog(errorText,trnId);
                                }
                            });

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getAlertDialog(error502,"AKI-00015");
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//method for AlertDialog
    private void getAlertDialog(String errorText, String trnId) {
        try{
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage(errorText+"\n"+trnId);
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
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
    //Method for getting OTP Pop-up layout
    //Using Layout inflater and Dialog
    private void getOTP() {
        try{
            LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            //inflating the custom layout otp_pop_up
            View v=inflater.inflate(R.layout.otp_pop_up,null,false);
            Dialog d=new Dialog(MainActivity.this);
            //setting the pop-up layout to Dialog d
            d.setContentView(v);
            d.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
            d.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.comparison_bg));
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
                            pd=new ProgressDialog(MainActivity.this);
                            pd.setCancelable(false);
                            pd.setMessage(getString(R.string.please_wait));
                            pd.show();
                            /*OTPValidate();*/
                            getAllCoverageAmount();

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
    //Method for getting form pop-up layout
    //Using Layout inflater and Dialog
    private void getForm() {
        try{
            if(pd.isShowing()){
                pd.dismiss();
            }
        LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //inflating the custom layout otp_pop_up
        View v=inflater.inflate(R.layout.form_pop_up,null,false);
        Dialog d=new Dialog(MainActivity.this);
        //setting the pop-up layout to Dialog d
        d.setContentView(v);
        d.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        d.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.comparison_bg));
        d.create();
        d.setCancelable(false);
        // making layout blur to be visible.
        d.show();

        Spinner termSpinnerInForm,coverageSpinnerInForm;
        Button getQuotes;
        RadioGroup rg;

        termSpinnerInForm=v.findViewById(R.id.termSpinnerInForm);

        coverageSpinnerInForm=v.findViewById(R.id.coverageSpinnerInForm);


        rg=v.findViewById(R.id.tobaccoConsumption);

        getQuotes=v.findViewById(R.id.getQuotesButton);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.radioButtonYes:{

                    }
                    case R.id.radioButtonNo:{

                    }
                }
            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, policyTermArray);
        try {
            arrayAdapter.setDropDownViewResource(R.layout.custom_spinner_layout);
            termSpinnerInForm.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        termSpinnerInForm.setOnItemSelectedListener(MainActivity.this);

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(MainActivity.this, R.layout.custom_spinner_layout, coverageArray);
        try {
            arrayAdapter2.setDropDownViewResource(R.layout.custom_spinner_layout);
            coverageSpinnerInForm.setAdapter(arrayAdapter2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        coverageSpinnerInForm.setOnItemSelectedListener(MainActivity.this);


        getQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
    }catch (Exception e){
        e.printStackTrace();
    }
    }
    //Method for getting the timer for otp pop-up
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
    //method for getting location values.
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
        try{
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //method for getting all the data for header clientinfo.
    public static String InsertMobileparameters(Context context) {
        try
        {
            boolean rooteddevice;
            if(RootUtil.isDeviceRooted() == true)
            {
                rooteddevice = true;
            }
            else
            {
                rooteddevice = false;
            }

            String androidOS = Build.VERSION.RELEASE;
            String model = Build.MANUFACTURER + " - " + Build.MODEL;
            final String address1 = Latitude+","+Longitude;
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            String ipaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            JsonObject mobileparameters = new JsonObject();
            mobileparameters.addProperty("deviceID", uniqueidval);
            mobileparameters.addProperty("deviceID2", uniqueidval);
            mobileparameters.addProperty("deviceTimeZone", TimeZone.getDefault().getDisplayName());
            mobileparameters.addProperty("deviceDateTime", java.text.DateFormat.getDateTimeInstance().format(new Date()));
            /*mobileparameters.addProperty("deviceIpAddress", address1);*/
            mobileparameters.addProperty("deviceIpAddress", ipaddress);
            mobileparameters.addProperty("deviceLatitude", Latitude);
            mobileparameters.addProperty("deviceLongitude", Longitude);
            mobileparameters.addProperty("deviceType", "Android");
            mobileparameters.addProperty("deviceModel", model);
            mobileparameters.addProperty("deviceVersion", androidOS);
            mobileparameters.addProperty("deviceUserID", "123456");
            mobileparameters.addProperty("deviceAppVersion", "1.0.6");
            mobileparameters.addProperty("deviceIsJailBroken", rooteddevice);
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
    //over ride method for spinner/dropdown
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try{
        //using switch case to get the values from respective spinners with its respective ID.
        switch (adapterView.getId()){
            case R.id.coverageSpinner:{
                System.out.println("Spinner-Coverage "+adapterView.getSelectedItem());
                break;
            }
            case R.id.policyTermSpinner:{
                System.out.println("Spinner-Term "+adapterView.getSelectedItem());
                break;
            }
            case R.id.paymentTypeSpinner:{
                System.out.println("Spinner-PaymentType "+adapterView.getSelectedItem());
                break;
            }
            case R.id.coverageSpinnerInForm:{
                System.out.println("Spinner-coverageInForm "+adapterView.getSelectedItem());
                break;
            }
            case R.id.termSpinnerInForm:{
                System.out.println("Spinner-termInForm "+adapterView.getSelectedItem());
                break;
            }
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}