package smarter.com.vehiclemaintenance.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.component.view.ViewDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Detail extends AppCompatActivity implements View.OnClickListener {

    public static Activity activity;
    Context context;
    private ImageView imgSync;
    private TextView txtTitle;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();


    }

    private void init() {
        context = this;
        activity = this;

        imgSync = findViewById(R.id.id_sync);
        imgSync.setVisibility(View.VISIBLE);
        txtTitle = findViewById(R.id.id_txt_name);

        txtTitle.setText("Vehicle Maintenance Details");

        /*init_onClick*/
        imgSync.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_sync:
                Intent intent = new Intent(Detail.this, Update.class);
                startActivity(intent);
                break;
        }
    }
}
