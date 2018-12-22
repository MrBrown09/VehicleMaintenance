package smarter.com.vehiclemaintenance.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class Update extends AppCompatActivity implements View.OnClickListener {

    public static Activity activity;
    Context context;
    private ImageView imgSync, imgLogo;
    private TextView txtTitle, tvRequester, tvVehicle, tvAlias;
    private Button btnSubmit;
    LinearLayout llBack;
    private CheckBox chkOil, chkBreak, chkTire, chkHead, chkAir, chkSpark, chkTurn,
            chkSprocket, chkAll, chkClutch, chkBattery, chkInjector, chkBelt;
    private EditText edtOthers, edtMileage, edtRemark;
    String strChkAction="", strOther, strMileage, strRemark, strEmpCode, strEmpName, strVehiCode, strLogEmpCode,
            strLogEmpName, strCode, strAlias, strRequester, strVehicle;
    Dialog dialog;
    private Retrofit retrofit;
    private int mStatusCode;
    private APICallInterface service;
    private String baseUrl;
    public static SharedPreferences sharePrefs;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        init();
        initDialog();

    }

    private void initDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
    }

    private void init() {
        context = this;
        activity = this;
        sharePrefs = PreferenceManager
                .getDefaultSharedPreferences(Update.this);

        imgSync = findViewById(R.id.id_sync);
        imgSync.setVisibility(View.INVISIBLE);
        txtTitle = findViewById(R.id.id_txt_name);
        btnSubmit = findViewById(R.id.id_submit);
        llBack = findViewById(R.id.id_back);
        llBack.setVisibility(View.VISIBLE);

        txtTitle.setText("Vehicle Maintenance Update");

        chkOil = findViewById(R.id.id_oil);
        chkBreak = findViewById(R.id.id_break);
        chkTire = findViewById(R.id.id_tire);
        chkHead = findViewById(R.id.id_head);
        chkAir = findViewById(R.id.id_air);
        chkSpark = findViewById(R.id.id_spark);
        chkTurn = findViewById(R.id.id_turn);
        chkSprocket = findViewById(R.id.id_sprocket);
        chkAll = findViewById(R.id.id_all);
        chkClutch = findViewById(R.id.id_clutch);
        chkBattery = findViewById(R.id.id_battery);
        chkInjector = findViewById(R.id.id_injector);
        chkBelt = findViewById(R.id.id_belt);
        edtOthers = findViewById(R.id.id_other);
        edtMileage = findViewById(R.id.id_mileage);
        edtRemark = findViewById(R.id.id_remarks);
        tvRequester = findViewById(R.id.id_requester);
        tvAlias = findViewById(R.id.id_alias);
        tvVehicle = findViewById(R.id.id_vehicle);

        /*getIntentValue*/
        if( getIntent().getExtras() != null) {
            strEmpCode = getIntent().getStringExtra(Constant.TAG_EmployeeCode);
            strEmpName = getIntent().getStringExtra(Constant.TAG_EmployeeName);
            strVehiCode = getIntent().getStringExtra(Constant.TAG_VehicleCode);
            strCode = getIntent().getStringExtra(Constant.TAG_Code);
            strRequester = getIntent().getStringExtra(Constant.TAG_Requester);
            strAlias = getIntent().getStringExtra(Constant.TAG_Alias);
            strVehicle = getIntent().getStringExtra(Constant.TAG_Vehicle);

            tvRequester.setText(strRequester);
            tvVehicle.setText(strVehicle);
            tvAlias.setText(strAlias);
        }
        strLogEmpCode = sharePrefs.getString(Constant.TAG_EmployeeCode,null);
        strLogEmpName = sharePrefs.getString(Constant.TAG_EmployeeName,null);


        baseUrl = ConfigApi.URL_BASE_SERVER;
        //        Creating REST API Calls **/
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(Constant.client)
                .build();
        service = retrofit.create(APICallInterface.class);

        /*init_onClick*/
        imgSync.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        llBack.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_sync:

                break;

            case R.id.id_submit:
                strChkAction="";
                if (chkOil.isChecked()) {
                    strChkAction = strChkAction + "," + chkOil.getText();
                }
                if (chkBreak.isChecked()) {
                    strChkAction = strChkAction + "," + chkBreak.getText();
                }
                if (chkTire.isChecked()) {
                    strChkAction = strChkAction + "," + chkTire.getText();
                }
                if (chkHead.isChecked()) {
                    strChkAction = strChkAction + "," + chkHead.getText();
                }
                if (chkAir.isChecked()) {
                    strChkAction = strChkAction + "," + chkAir.getText();
                }
                if (chkSpark.isChecked()) {
                    strChkAction = strChkAction + "," + chkSpark.getText();
                }
                if (chkTurn.isChecked()) {
                    strChkAction = strChkAction + "," + chkTurn.getText();
                }
                if (chkSprocket.isChecked()) {
                    strChkAction = strChkAction + "," + chkSprocket.getText();
                }
                if (chkAll.isChecked()) {
                    strChkAction = strChkAction + "," + chkAll.getText();
                }
                if (chkClutch.isChecked()) {
                    strChkAction = strChkAction + "," + chkClutch.getText();
                }
                if (chkBattery.isChecked()) {
                    strChkAction = strChkAction + "," + chkBattery.getText();
                }
                if (chkInjector.isChecked()) {
                    strChkAction = strChkAction + "," + chkInjector.getText();
                }
                if (chkBelt.isChecked()) {
                    strChkAction = strChkAction + "," + chkBelt.getText();
                }

                Log.e("VALUE_OP",strChkAction);

                strOther = edtOthers.getText().toString();
                strMileage = edtMileage.getText().toString();
                strRemark = edtRemark.getText().toString();

                if(strChkAction!=null & strChkAction.length()>0 || strOther.length()>0){
                    if(strMileage.length()>0 & strRemark.length()>0){
                        if(strChkAction.length()==0){
                            strChkAction = "Not Used";
                        }
                        if(strOther.length()==0){
                            strOther = "Not Used";
                        }
                        callUpdate(strChkAction, strOther, strMileage, strRemark, strLogEmpCode, strLogEmpName, strCode);
                    }else{
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "Fill all fields!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Fill all fields!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;

            case R.id.id_back:
                finish();
                break;
        }
    }

    private void callUpdate(String strChkAction, String strOther, String strMileage, String strRemark, String strEmpCode, String strEmpName, String xstrCode) {
        dialog.show();

        HashMap<String, String> map = new HashMap<>();

        map.put("Code", xstrCode);
        map.put("CurrentOdometer", strMileage);
        map.put("Remarks", strRemark);
        map.put("ActionsMade", strChkAction);
        map.put("Others", strOther);
        map.put("UpdatedByEmployeeFullName", strEmpName);
        map.put("UpdatedByEmployeeCode", strEmpCode);

        JSONObject json = new JSONObject(map);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (json.toString()));
        Log.e("Request_Value",json.toString());

        String strUrl = baseUrl + ConfigApi.API_UPDATE;

        Call<ResponseBody> caller = service.postToAPI(strUrl, body);

        caller.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    mStatusCode = response.code();
                    Log.e("STATUS_CODE_ENTRY", ""+mStatusCode);
                    String strMessage = null, strCode=null;
                    if (mStatusCode == Constant.SUCEESSRESPONSECODE) {
                        String strMsg = response.body().string().toString();
//                        Log.e("CREATE_RESPONSE", ""+response.body().string());
                        showDialog(Update.this, strMsg,"SUCCESS");

                    } else if (mStatusCode == Constant.FAILURERESPONSECODE) {
                    } else {
                        if (mStatusCode == Constant.INTERNALERRORRESPONSECODE) {
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
               dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                showDialog(Update.this,getResources().getString(R.string.network_problem_failure), "FAIL");
            }
        });


    }

    private void showDialog(Update context, String msg, final String status) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView img = dialog.findViewById(R.id.id_img);
//        TextView tvTitle = dialog.findViewById(R.id.id_title);
        TextView tvMsg = dialog.findViewById(R.id.id_message);
        tvMsg.setText(msg);

        TextView mDialogOk = dialog.findViewById(R.id.id_ok);
        mDialogOk.setText("OK");

        if (status.contentEquals("SUCCESS")){
            tvMsg.setText(msg);
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.checked));
        }else{
            tvMsg.setText(msg);
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.warning));
        }

        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.contentEquals("SUCCESS")){
                    dialog.cancel();
                    Intent intent = new Intent(Update.this, Maintenance.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
//                    finish();
                }else{
                    dialog.cancel();
                }
            }
        });

        dialog.show();
    }
}
