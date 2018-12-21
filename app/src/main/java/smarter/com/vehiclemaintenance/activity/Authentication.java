package smarter.com.vehiclemaintenance.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.component.view.ViewDialog;
import smarter.com.vehiclemaintenance.utils.APICallInterface;
import smarter.com.vehiclemaintenance.utils.ConfigApi;
import smarter.com.vehiclemaintenance.utils.Constant;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static smarter.com.vehiclemaintenance.utils.Constant.isDeviceOnline;

public class Authentication extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    Toast toast;
    private ProgressBar spinner;
    private TextView tvTry;
    TelephonyManager mTelephony;
    String IME = "0", strIsLogin, mVersion;
    private int mStatusCode;
    String baseUrl;
    private APICallInterface service;
    Call<ResponseBody> call = null;
    Activity context;
    Retrofit retrofit;
    public static SharedPreferences sharePrefs;
    SharedPreferences.Editor sharePrefs_Editor;
    ViewDialog alertDialog;
    public static Activity activity;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            return;
        }
        boolean isGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }

        if (isGranted) {
            startApplication();
        } else {
            toast = Toast.makeText(Authentication.this,"permission denied",Toast.LENGTH_LONG);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if( v != null) v.setGravity(Gravity.CENTER);
            toast.show();
        }
    }

    private void setPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        init();

        setPermissions();

        tvTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIemiCheck();
            }
        });
    }

    private void init() {
        context = this;
        activity = this;

        mVersion = getResources().getString(R.string.valid_version);
        alertDialog = new ViewDialog();
        baseUrl = ConfigApi.URL_BASE_SERVER;

        /** Creating REST API Calls **/
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(Constant.client)
                .build();
        service = retrofit.create(APICallInterface.class);

        /** Initialize SharedPreferences **/
        sharePrefs = PreferenceManager
                .getDefaultSharedPreferences(Authentication.this);
        sharePrefs_Editor = sharePrefs.edit();

        strIsLogin = sharePrefs.getString(Constant.TAG_IsLOGIN, null);

        spinner = findViewById(R.id.progressBar);

        spinner.setVisibility(View.VISIBLE);
        tvTry = findViewById(R.id.id_tryagain);
        tvTry.setVisibility(View.GONE);

    }


    public void startApplication() {

        mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(Authentication.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mTelephony.getPhoneCount() == 2) {
                // Dual sim
                IME =  mTelephony.getDeviceId(0);
            }else{
                IME = mTelephony.getDeviceId();
            }
        }else{
            IME = mTelephony.getDeviceId();
        }
        callIemiCheck();

    }

    public void callIemiCheck() {
        spinner.setVisibility(View.VISIBLE);
        tvTry.setVisibility(View.GONE);
        if(isDeviceOnline(context)){
//            checkVersioValidate(mVersion);
            checkAuthUser(IME);
        }else{
            showDialog(Authentication.this,getResources().getString(R.string.str_networkmessage), "FAIL");
        }
    }

    private void checkVersioValidate(String mVersion) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.TAG_Name, "Shipment");
        map.put(Constant.TAG_Version, mVersion);

        JSONObject json = new JSONObject(map);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (json.toString()));
        call = service.postToAPI(ConfigApi.version_Validate, body);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    mStatusCode = response.code();

                    if (mStatusCode == Constant.SUCEESSRESPONSECODE) {

                        JSONObject json = new JSONObject(response.body().string());

                        String strResult = json.getString("result");

                        if(!strResult.contentEquals("success")){

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                            builder1.setCancelable(false);
                            builder1.setTitle("New version available");
                            builder1.setMessage("Please, update app to new version to continue.");
                            builder1.setInverseBackgroundForced(true);
                            builder1.setPositiveButton("UPDATE",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            final String appPackageName = getPackageName();
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                            Button buttonbackground = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
                            buttonbackground.setTextColor(getResources().getColor(R.color.colorGreen));
                        }else{
                            checkAuthUser(IME);
                        }

                    } else if (mStatusCode == Constant.FAILURERESPONSECODE) {

                    } else {

                        if (mStatusCode == Constant.INTERNALERRORRESPONSECODE) {
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                int Splash_Time_Out = 5000;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.GONE);
                    }
                }, Splash_Time_Out);
            }
        });

    }

    private void checkAuthUser(String imein) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.TAG_IMEI, imein);

        String strUrl = baseUrl + ConfigApi.API_AUTH;

        call = service.postAPI(strUrl, map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent intent;
                try {
                    mStatusCode = response.code();
                    Log.v("mStatusCode", "mStatusCode=" + mStatusCode);

                    if (mStatusCode == Constant.SUCEESSRESPONSECODE) {

                        JSONObject json = new JSONObject(response.body().string());
                        Log.e("LOGGGGI",""+json);

                        String strValid = json.getString(Constant.TAG_IsValid);

                        if(strValid.contains("true")){
                            spinner.setVisibility(View.GONE);

                            JSONObject jsonEmp = json.getJSONObject(Constant.TAG_EmployeeInfo);
                            String imei = jsonEmp.getString(Constant.TAG_DeviceId);
                            String empCode = jsonEmp.getString(Constant.TAG_EmployeeCode);
                            String empName = jsonEmp.getString(Constant.TAG_EmployeeName);
                            String empWorkplaceId = jsonEmp.getString(Constant.TAG_WorkplaceId);

                            sharePrefs_Editor.putString(Constant.TAG_IMEI, imei);
                            sharePrefs_Editor.putString(Constant.TAG_EmployeeCode, empCode);
                            sharePrefs_Editor.putString(Constant.TAG_EmployeeName, empName);
                            sharePrefs_Editor.putString(Constant.TAG_WorkplaceId, empWorkplaceId);
                            sharePrefs_Editor.putString(Constant.TAG_IsLOGIN, "true");

                            sharePrefs_Editor.commit();

                            intent = new Intent(Authentication.this, Maintenance.class);
                            startActivity(intent);
                            finish();
                        }else{
                            String strmessage = json.getString("message");
                            if(strmessage.contentEquals("Invalid IMEI Number")){
                                intent = new Intent(Authentication.this, Registration.class);
                                intent.putExtra(Constant.TAG_IMEI, IME);
                                startActivity(intent);
                                finish();
                            }else{

                                new android.app.AlertDialog.Builder(Authentication.this,R.style.AlertDialog)
                                        .setTitle("Alert!")
                                        .setMessage(strmessage)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("smarter.com.smarterworkmemo");
                                                if (launchIntent != null) {
                                                    launchIntent.putExtra("FROM_APP", "Smarter_VEHICLE");
                                                    startActivity(launchIntent);//null pointer check in case package name was not found
                                                    finish();
                                                }

                                            }
                                        }).show();
                            }
                        }
                        spinner.setVisibility(View.INVISIBLE);
                    } else if (mStatusCode == Constant.FAILURERESPONSECODE) {
                        spinner.setVisibility(View.INVISIBLE);
                        Log.e("Server_Response",""+Constant.FAILURERESPONSECODE);
                        toast = Toast.makeText(Authentication.this,response.errorBody().string(),Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        if( v != null) v.setGravity(Gravity.CENTER);
                        toast.show();
                    } else {

                        if (mStatusCode == Constant.INTERNALERRORRESPONSECODE) {
                            spinner.setVisibility(View.INVISIBLE);
                            Log.e("Server_Response",""+Constant.INTERNALERRORRESPONSECODE);
                            toast = Toast.makeText(Authentication.this,response.errorBody().string(),Toast.LENGTH_LONG);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if( v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                    }

                } catch (Exception e) {
                    spinner.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                int Splash_Time_Out = 5000;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Server_Response",""+"Time_Out");
                        spinner.setVisibility(View.GONE);
                        tvTry.setVisibility(View.VISIBLE);
                    }
                }, Splash_Time_Out);
            }
        });

    }

    private void showDialog(Activity activity, String strMessage, String strStatus) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView img = dialog.findViewById(R.id.id_img);
        if(strStatus.contentEquals("FAIL")){
            img.setImageDrawable(activity.getResources().getDrawable(R.drawable.warning));
        }else{
            img.setImageDrawable(activity.getResources().getDrawable(R.drawable.checked));
        }

        TextView tvMsg = dialog.findViewById(R.id.id_message);
        tvMsg.setText(strMessage);

        TextView mDialogOk = dialog.findViewById(R.id.id_ok);

        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                callIemiCheck();
            }
        });
        dialog.show();
    }
}
