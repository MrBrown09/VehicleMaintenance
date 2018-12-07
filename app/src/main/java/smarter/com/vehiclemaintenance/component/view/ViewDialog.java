package smarter.com.vehiclemaintenance.component.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.activity.Authentication;
import smarter.com.vehiclemaintenance.activity.Maintenance;
import smarter.com.vehiclemaintenance.activity.Registration;

public class ViewDialog {

    public static void showDialog(Activity activity, String strMessage, String strStatus, String check, final String from) {

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
        mDialogOk.setText("YES");
        TextView mDialogNo = dialog.findViewById(R.id.id_cancel);
        mDialogNo.setText("NO");

        if(check.contentEquals("TWO")){
            mDialogNo.setVisibility(View.VISIBLE);
        }else{
            mDialogNo.setVisibility(View.GONE);
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
                    case "AUTH":
                        break;
                    case "REGI":
                        Registration.activity.finish();
                        break;
                    case "MAIN":
                        Maintenance.activity.finish();
                        break;
                }

                dialog.cancel();
            }
        });
        dialog.show();
    }

}


