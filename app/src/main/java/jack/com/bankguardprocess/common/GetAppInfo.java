package jack.com.bankguardprocess.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GetAppInfo {

    public class AppInfo {
        public String appName = "";
        public String packageName = "";
        public String versionName = "";
        public int versionCode = 0;
        public Drawable appIcon = null;

        public void print(){
            Log.i("GuardProcess","Name:"+appName+" Package:"+packageName);
            Log.i("GuardProcess","Name:"+appName+" versionName:"+versionName);
            Log.i("GuardProcess","Name:"+appName+" versionCode:"+versionCode);
        }

    }




    /**
     *
     * @param sign 1、本机全部app的信息 2、系统应用的信息 3、非系统应用的信息
     * @return app的信息
     */
    public List<AppInfo> getAppInfo(int sign) {

        Context appContext = MainApplication.getAppContext();
        List<AppInfo> appList = new ArrayList<AppInfo>(); //用来存储获取的应用信息数据　　　　　
        List<PackageInfo> packages = appContext.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(appContext.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(appContext.getPackageManager());
            if (sign == 1) {//全手机全部应用
                appList.add(tmpInfo);
            } else if (sign == 2) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    appList.add(tmpInfo);//如果非系统应用，则添加至appList
                }
            } else if (sign == 3) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    appList.add(tmpInfo);//如果非系统应用，则添加至appList
                }
            }
        }
        return appList;
    }



}
