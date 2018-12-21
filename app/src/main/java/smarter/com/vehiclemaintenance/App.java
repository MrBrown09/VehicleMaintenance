package smarter.com.vehiclemaintenance;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Arnold on 27-03-2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/karla.regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
