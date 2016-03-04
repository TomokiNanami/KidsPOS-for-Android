package info.nukoneko.cuc.kidspos;

import android.app.Application;
import android.content.Context;

import info.nukoneko.cuc.kidspos.util.KPLogger;
import info.nukoneko.cuc.kidspos.util.SQLiteAdapter;
import info.nukoneko.kidspos4j.KidsPos4jConfig;
import info.nukoneko.kidspos4j.util.config.SQLiteSetting;

/**
 * created at 2015/06/13.
 */
public class AppController extends Application {
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        StoreManager.Initialize(this);

        SQLiteSetting.setSqlProvider(new SQLiteAdapter());

        KidsPos4jConfig.setBaseUrl("http://192.168.11.36:8080/api/");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized AppController get(){
        return mInstance;
    }

}
