package smarter.com.vehiclemaintenance.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.component.view.ViewDialog;
import smarter.com.vehiclemaintenance.utils.Constant;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Registration extends AppCompatActivity implements View.OnClickListener{

    String strIMEI;
    private TextView txtIMEI, txtCompMain, txtRefreshHInt, txtAuth;
    private Button btnPost;
    private ImageView imgSync, imgLogo;
    Context context;
    public static Activity activity;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_register);

        context = this;
        activity = this;
        strIMEI = getIntent().getStringExtra(Constant.TAG_IMEI);

        init();

        txtIMEI.setText(strIMEI);

        btnPost.setOnClickListener(this);
        imgSync.setOnClickListener(this);
    }

    private void init() {
        txtIMEI = findViewById(R.id.id_imeivalue);
        btnPost = findViewById(R.id.id_register);
        imgSync = findViewById(R.id.id_sync);
        txtCompMain = findViewById(R.id.id_txt_name);
        imgLogo = findViewById(R.id.id_back_img);
        txtRefreshHInt = findViewById(R.id.id_refresh_hint);
        txtAuth = findViewById(R.id.id_unartho);

        imgLogo.setImageDrawable(getResources().getDrawable(R.drawable.lock));
        txtCompMain.setText(getResources().getText(R.string.unaurthorise_user));

        String strHint = getString(R.string.refresh_hint_o);
        Spanned result = Html.fromHtml(strHint);
        txtRefreshHInt.setText(result);
        String strUnartho = getString(R.string.unartho_user);
        Spanned resultUnArtho = Html.fromHtml(strUnartho);
        txtAuth.setText(resultUnArtho);
    }

    @Override
    public void onClick(android.view.View v) {
        switch (v.getId()){
            case R.id.id_register:
                int sdk = android.os.Build.VERSION.SDK_INT;

//                String shareBody = strIMEI;
                String stringYouExtracted = txtIMEI.getText().toString();
                String msg = " - Register my DeviceId to the management. Thank You! - via SmarterSI app";

                Constant.getCopyToClipboard(stringYouExtracted, msg, context, sdk);

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, stringYouExtracted);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

            case R.id.id_sync:
                Intent intent = new Intent(Registration.this, Authentication.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ViewDialog.showDialog(Registration.this,"Are you sure to exit?", "ASK", "TWO", "REGIS");
    }



}
