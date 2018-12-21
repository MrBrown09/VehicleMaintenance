package smarter.com.vehiclemaintenance.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.component.rest.ImageLoadTask;
import smarter.com.vehiclemaintenance.component.view.ViewDialog;
import smarter.com.vehiclemaintenance.utils.APICallInterface;
import smarter.com.vehiclemaintenance.utils.ConfigApi;
import smarter.com.vehiclemaintenance.utils.Constant;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static smarter.com.vehiclemaintenance.utils.Constant.TAG_Code;
import static smarter.com.vehiclemaintenance.utils.Constant.TAG_EmployeeCode;
import static smarter.com.vehiclemaintenance.utils.Constant.TAG_EmployeeName;
import static smarter.com.vehiclemaintenance.utils.Constant.TAG_VehicleCode;
import static smarter.com.vehiclemaintenance.utils.Constant.TAG_Version;
import static smarter.com.vehiclemaintenance.utils.Constant.isDeviceOnline;

public class Detail extends AppCompatActivity implements View.OnClickListener {

    public static Activity activity;
    Context context;
    private ImageView imgSync;
    private CircleImageView imgUser, imgVehicle;
    private TextView txtTitle, txtAlias, txtRequesterName, txtVehicle, txtDescript;
    String strCode, strEmpCode, strEmpName, strVehicleCode;
    private LinearLayout llProgress;
    private RelativeLayout llMain;
    private Button btnCreate;

    private Retrofit retrofit;
    private int mStatusCode, intTrans;
    private APICallInterface service;
    private String baseUrl, strUserUrl, strVehicleUrl;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();

        if(isDeviceOnline(context)){
            llProgress.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
            getDetail(strCode);
        }else{
            showDialog(Detail.this,getResources().getString(R.string.str_networkmessage), "FAIL","NETWORK");
        }



    }

    private void init() {
        context = this;
        activity = this;

        imgSync = findViewById(R.id.id_sync);
        imgSync.setVisibility(View.VISIBLE);
        txtTitle = findViewById(R.id.id_txt_name);
        llMain = findViewById(R.id.id_main);
        llMain.setVisibility(View.GONE);
        llProgress = findViewById(R.id.progress_bar_lay);
        txtAlias = findViewById(R.id.id_alias);
        txtRequesterName = findViewById(R.id.id_requestName);
        txtVehicle = findViewById(R.id.id_vehicle_no);
        txtDescript = findViewById(R.id.id_description);
        imgUser = findViewById(R.id.user_pic);
        imgVehicle = findViewById(R.id.vehiclePic);
        btnCreate = findViewById(R.id.id_create_ship);

        txtTitle.setText("Vehicle Maintenance Details");

        /*init_onClick*/
        imgSync.setOnClickListener(this);
        btnCreate.setOnClickListener(this);

        baseUrl = ConfigApi.URL_BASE_SERVER;

//        Creating REST API Calls **/
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(Constant.client)
                .build();
        service = retrofit.create(APICallInterface.class);

        /*getValueformIntent*/
        if( getIntent().getExtras() != null) {
            strCode =  getIntent().getStringExtra(TAG_Code);
        }
    }

    private void getDetail(String xstrCode) {
        String strUrl = baseUrl + ConfigApi.API_GETDETAIL + "?" + "code" + "=" + xstrCode;

        Call<ResponseBody> caller = service.callAPI(strUrl);

        caller.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                mStatusCode = response.code();

                if (mStatusCode == Constant.SUCEESSRESPONSECODE) {

                    try {
                        JSONObject json = new JSONObject(response.body().string());

                        String strResult = json.getString("result");
//                        Log.e("STR_VALUE",""+json);

                        if(strResult.contentEquals("success")){
                            JSONObject result_object = json.getJSONObject("model");

                            txtRequesterName.setText(result_object.getString("CreatedByEmployeeFullName"));
                            txtVehicle.setText(result_object.getString("VehicleNumber"));
                            txtAlias.setText(result_object.getString("Alias"));
                            txtDescript.setText(result_object.getString("Description"));

                            String strImgCode = result_object.getString("ImageCode");
                            String strPhotoUrl = result_object.getString("PhotoUrl");

                            strEmpCode = result_object.getString("CreatedByEmployeeCode");
                            strEmpName = result_object.getString("CreatedByEmployeeFullName");
                            strVehicleCode = result_object.getString("VehicleCode");

                            setImages(strImgCode, strPhotoUrl);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        llProgress.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                        llProgress.setVisibility(View.GONE);
                        llMain.setVisibility(View.VISIBLE);

                }else if (mStatusCode == Constant.FAILURERESPONSECODE) {

                } else {
                    if (mStatusCode == Constant.INTERNALERRORRESPONSECODE) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                llProgress.setVisibility(View.GONE);
//                alertDialog.showDialog(Detail.this,getResources().getString(R.string.network_problem_failure), "FAIL");
            }
        });

    }

    private void setImages(String xstrImgCode, String xstrPhotoUrl) {

        String[] vehiArray = xstrImgCode.split(",");
        String strImageCode = vehiArray[0];

        strUserUrl = ConfigApi.API_PHOTO + xstrPhotoUrl;
        strVehicleUrl = ConfigApi.API_IMAGE + "Code=" + strImageCode;
        Log.e("ppppp",strVehicleUrl);
        /*setProfileImage*/
        new ImageLoadTask(strUserUrl, imgUser).execute();
        new ImageLoadTask(strVehicleUrl, imgVehicle).execute();

    }


    @Override
    public void onBackPressed() {
        finish();
    }

    private void showDialog(Activity activity, String strMessage, String strStatus, final String from) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView img = dialog.findViewById(R.id.id_img);
        if(strStatus.contentEquals("FAIL")){
            img.setImageDrawable(activity.getResources().getDrawable(R.drawable.warning));
        }else if(strStatus.contentEquals("ASK")){
            img.setImageDrawable(activity.getResources().getDrawable(R.drawable.question));
        }else{
            img.setImageDrawable(activity.getResources().getDrawable(R.drawable.checked));
        }

        TextView tvMsg = dialog.findViewById(R.id.id_message);
        tvMsg.setText(strMessage);

        TextView mDialogOk = dialog.findViewById(R.id.id_ok);
        TextView mDialogNo = dialog.findViewById(R.id.id_cancel);


        switch (from){
            case "EXIT":
                mDialogOk.setText("YES");
                mDialogNo.setVisibility(View.VISIBLE);
                mDialogNo.setText("NO");
                break;
        }

        mDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        mDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (from){
                    case "NETWORK":
//                        getList();
                        break;
                    case "EXIT":
                        finish();
                        break;
                }
                dialog.cancel();

            }
        });
        if(dialog.isShowing()){
            dialog.dismiss();
            dialog.show();
        }else{
            dialog.show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_sync:

                break;
            case R.id.id_create_ship:
                Intent intentShip = new Intent(Detail.this, Update.class);
                intentShip.putExtra(TAG_EmployeeCode, strEmpCode);
                intentShip.putExtra(TAG_EmployeeName, strEmpName);
                intentShip.putExtra(TAG_VehicleCode, strVehicleCode);
                intentShip.putExtra(TAG_Code, strCode);

                startActivity(intentShip);
                break;
        }
    }
}
