package com.example.task1;

import android.Manifest;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import adapter.CustomAdapter;
import adapter.CustomComparisonAdapter;
import model.ComparisonListModel;
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
    String[] coverageArray;
    Spinner coverageSpinner,policyTermSpinner,paymentSpinner;

    ArrayList<ComparisonListModel> comparisonList;
    ArrayList<ListModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialization of variables and layout fields
        listView = findViewById(R.id.listView1);
        comparePlans = findViewById(R.id.comparePlans);
        blur = findViewById(R.id.blurLayout);
        coverageSpinner = findViewById(R.id.coverageSpinner);
        policyTermSpinner = findViewById(R.id.policyTermSpinner);
        paymentSpinner = findViewById(R.id.paymentTypeSpinner);
        list = new ArrayList<>();
        comparisonList = new ArrayList<>();
        callData();
        getOTP();


        resultData=getAllCoverageAmounts(MainActivity.this);

        coverageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int index=i;
                String coverageAmount= coverageArray[i] ;
                System.out.println("value : "+coverageAmount);
                getLocation();
                String Data=InsertMobileparameters(MainActivity.this);
                System.out.println("1234567890 "+Data);
                getCoverage(Data,resultData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    private void getCoverage(String data,String body) {
        try{
            OkHttpClient client=new OkHttpClient();
            String url=getString(R.string.base_url)+"/Coverage/GetAllCoverageAmount";

            RequestBody rb= RequestBody.create(MediaType.parse("application/json; charset=utf-8"),body);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("clientInfo",data)
                    .addHeader("fingerprint","123456")
                    .post(rb)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    System.out.println("My Response : "+response);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

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

            coverageArray = new String[]{getString(R.string.k_250),getString(R.string.k_300),
                    getString(R.string.k_500),getString(R.string.million_1),getString(R.string.million_10)};

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
                System.out.println("latlng : "+latitude+","+longitude);
                SharedPreferences sharedpreferences = getSharedPreferences("LocationPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Latitude", String.valueOf(latitude));
                editor.putString("Longitude", String.valueOf(longitude));
                editor.commit();

                SharedPreferences sp=getSharedPreferences("LocationPref",MODE_PRIVATE);
                Latitude=sp.getString("Latitude",null);
                Longitude=sp.getString("Longitude",null);
                System.out.println("latlng2 : "+Latitude+","+Longitude);
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


    public static String getAllCoverageAmounts(Context context){
        try{
            JsonArray jsonArray=new JsonArray();

            int[] a=new int[]{250000,300000,500000,1000000,10000000};
            String[]b=new String[]{"250k","300k","500k","1 Million","10 Million"};

            for(int i=0;i<a.length;i++){
                JsonObject obj=new JsonObject();
                obj.addProperty("coverageAmount",a[i]);
                obj.addProperty("coverageAmountText",b[i]);
                jsonArray.add(obj);
            }
            System.out.println("My Coverage Amount array : "+jsonArray);
            JsonObject jobj=new JsonObject();
            jobj.add("getAllCoverageAmount", jsonArray);
            System.out.println("My Coverage Amount object : "+jobj);

            String myCoverageAmount= jobj.toString();
            return myCoverageAmount;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
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
            SharedPreferences uniquePref = context.getSharedPreferences("Uniquepref", MODE_PRIVATE);
            final String uniqueidval = uniquePref.getString("imei", null);
            final String address1 = Latitude+","+Longitude;
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            String ipaddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            JsonObject mobileparamters = new JsonObject();
            mobileparamters.addProperty("imeino1", uniqueidval);
            mobileparamters.addProperty("imeino2", uniqueidval);
            mobileparamters.addProperty("timezone", TimeZone.getDefault().getDisplayName());
            mobileparamters.addProperty("currentdatetime", java.text.DateFormat.getDateTimeInstance().format(new Date()));
            mobileparamters.addProperty("Address", address1);
            mobileparamters.addProperty("latitude", Latitude);
            mobileparamters.addProperty("longitude", Longitude);
            mobileparamters.addProperty("IpAddress", ipaddress);
            mobileparamters.addProperty("mobileType", "Android");
            mobileparamters.addProperty("fireBaseuserid", "123456");
            mobileparamters.addProperty("mobileModel", model);
            mobileparamters.addProperty("mobileOSVersion", androidOS);
            mobileparamters.addProperty("appVersion", "1.0.6");
            mobileparamters.addProperty("IsJailBroken", rooteddevice);
            System.out.println(mobileparamters);
            String insertmobileString = mobileparamters.toString();
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