package smarter.com.vehiclemaintenance.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.activity.Registration;

public class Constant {



    public static final String TAG_IMEI = "imei";
    public static final String TAG_IsLOGIN = "IsLogin";
    public static final String TAG_Name = "Name";
    public static final String TAG_Version = "Version";

    public static final String TAG_IsValid = "IsValid";
    public static final String TAG_EmployeeInfo = "EmployeeInfo";
    public static final String TAG_EmployeeCode = "EmployeeCode";
    public static final String TAG_WorkplaceId = "WorkplaceId";
    public static final String TAG_DeviceId = "DeviceId";

    public static final String TAG_Code = "Code";
    public static final String TAG_EmployeeName = "EmployeeName";
    public static final String TAG_VehicleCode = "VehicleCode";
    public static final String TAG_Requester = "Requester";
    public static final String TAG_Alias = "Alias";
    public static final String TAG_Vehicle = "Vehicle";

    public static final int SUCEESSRESPONSECODE = 200;
    public static final int FAILURERESPONSECODE = 204;//405
    public static final int INTERNALERRORRESPONSECODE = 500;


    public static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES).addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("AppKey", "SY1WLZCRC07CY0A9")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            }).build();

    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;

    }

    /*copy_to_clipboard*/
    public static void getCopyToClipboard(String stringYouExtracted, String msg, Context context, int sdk) {

        if(stringYouExtracted.length() != 0){
            if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {

                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText("'"+stringYouExtracted+"'" + msg);
                Toast.makeText(context, " DeviceId Copied to Clipboard. Paste it on WorkPlace & Tag Person ", Toast.LENGTH_LONG).show();

            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Clip","'"+stringYouExtracted+"'"+ msg);
                Toast.makeText(context, " DeviceId Copied to Clipboard. Paste it on WorkPlace & Tag Person ", Toast.LENGTH_LONG).show();
                clipboard.setPrimaryClip(clip);
            }}else{
            Toast.makeText(context, "Nothing to Copy", Toast.LENGTH_SHORT).show();
        }
    }

}
