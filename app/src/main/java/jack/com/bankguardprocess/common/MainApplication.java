package jack.com.bankguardprocess.common;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2018/7/24 0024.
 */

public class MainApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // 注册crashHandler
        CrashHandler.getInstance().init(context);

    }


    public static Context getAppContext() {
        return context;
    }
}
