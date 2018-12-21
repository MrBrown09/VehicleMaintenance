package smarter.com.vehiclemaintenance.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.adapter.MaintRecyclerAdapter;
import smarter.com.vehiclemaintenance.component.model.MaintenanceModel;
import smarter.com.vehiclemaintenance.component.view.DividerItemDecoration;
import smarter.com.vehiclemaintenance.component.view.ItemOffsetDecoration;
import smarter.com.vehiclemaintenance.component.view.ViewDialog;
import smarter.com.vehiclemaintenance.utils.APICallInterface;
import smarter.com.vehiclemaintenance.utils.ConfigApi;
import smarter.com.vehiclemaintenance.utils.Constant;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static smarter.com.vehiclemaintenance.utils.Constant.isDeviceOnline;

public class Maintenance extends AppCompatActivity implements View.OnClickListener {

    public static Activity activity;
    Context context;
    private ImageView imgSync, imgLogo;
    private TextView txtTitle;
    LinearLayout progress_bar_lay, txt_norecord, recycler_ll;

    private RecyclerView recyclerView;
    LinearLayoutManager layout_manager;

    private Retrofit retrofit;
    private int mStatusCode;
    private APICallInterface service;
    private String baseUrl;

    List<MaintenanceModel> mainList;
    MaintRecyclerAdapter adapter;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        init();

        getList();
    }

    private void init() {
        context = this;
        activity = this;

        imgSync = findViewById(R.id.id_sync);
        imgSync.setVisibility(View.VISIBLE);
        txtTitle = findViewById(R.id.id_txt_name);
        progress_bar_lay = findViewById(R.id.progress_bar_lay);
        txt_norecord = findViewById(R.id.id_no_detail);
        recycler_ll = findViewById(R.id.id_recyclerll);

        txtTitle.setText("Maintenance List");

        recyclerView = findViewById(R.id.recyle_view);
        layout_manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout_manager);

        int spacing = 10;
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(Maintenance.this, spacing);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.addItemDecoration(
                new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));

        baseUrl = ConfigApi.URL_BASE_SERVER;

//        Creating REST API Calls **/
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(Constant.client)
                .build();
        service = retrofit.create(APICallInterface.class);

        mainList = new ArrayList<MaintenanceModel>();

        /*init_onClick*/
        imgSync.setOnClickListener(this);
    }

    private void getList() {
        progress_bar_lay.setVisibility(View.VISIBLE);
        recycler_ll.setVisibility(View.GONE);

        if(isDeviceOnline(context)){
            getMaintenanceList();
        }else{
            showDialog(Maintenance.this,getResources().getString(R.string.str_networkmessage), "FAIL","NETWORK");
        }
    }

    private void getMaintenanceList() {
        String strUrl = baseUrl + ConfigApi.API_GETLIST;

        Call<ResponseBody> caller = service.callAPI(strUrl);

        caller.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                mStatusCode = response.code();

                if (mStatusCode == Constant.SUCEESSRESPONSECODE) {

                    try {
                        JSONObject json = new JSONObject(response.body().string());

                        String strResult = json.getString("result");
                        Log.e("STR_VALUE",strResult);

                        if(strResult.contentEquals("success")){

                            JSONArray maint_array = json.getJSONArray("model");

                            List<MaintenanceModel> maintenanceitems = new Gson().fromJson(maint_array.toString(), new TypeToken<List<MaintenanceModel>>() {
                            }.getType());
//                            Log.e("PPPPPP",""+orderitems);
                            mainList.clear();
                            mainList.addAll(maintenanceitems);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progress_bar_lay.setVisibility(View.GONE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (mainList.size() > 0) {
                        progress_bar_lay.setVisibility(View.GONE);
                        txt_norecord.setVisibility(View.GONE);
                        recycler_ll.setVisibility(View.VISIBLE);

                        adapter = new MaintRecyclerAdapter(Maintenance.this, mainList, recyclerView);
                        recyclerView.setAdapter(adapter);

                    }else{
                        recycler_ll.setVisibility(View.GONE);
                        txt_norecord.setVisibility(View.VISIBLE);
                    }

                    progress_bar_lay.setVisibility(View.GONE);

                }else if (mStatusCode == Constant.FAILURERESPONSECODE) {

                } else {
                    if (mStatusCode == Constant.INTERNALERRORRESPONSECODE) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress_bar_lay.setVisibility(View.GONE);
                showDialog(Maintenance.this,getResources().getString(R.string.str_networkmessage), "FAIL","NETWORK");
            }
        });
    }


    @Override
    public void onBackPressed() {
        showDialog(Maintenance.this,"Are you sure to exit?", "ASK", "EXIT");
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
                        getList();
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
                getList();
                break;
        }
    }
}
