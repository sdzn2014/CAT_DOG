package com.catdog2025;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;







public class    DemoApplication extends MultiDexApplication {

    public static String PROCESS_NAME_XXXX = "process_name_xxxx";
    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        DemoApplication.context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getAppContext() {
        return DemoApplication.context;
    }
    
}
