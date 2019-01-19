package jack.com.bankguardprocess.common;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;
import static jack.com.bankguardprocess.MainActivity.strMac;
import static jack.com.bankguardprocess.TaskAbout.PHPAbout.MyTCP.getOneMsgFromClient;
import static jack.com.bankguardprocess.TaskAbout.PHPAbout.MyTCP.sendMsgToClient;
import static jack.com.bankguardprocess.common.AccountInfo.getAccountName;
import static jack.com.bankguardprocess.common.AccountInfo.getCardNo;
import static java.lang.Thread.sleep;

/**
 * Created by Administrator on 2018/7/13 0013.
 */
public class Util {
    /**
     * 获取当前进程的名字，一般就是当前app的包名
     *
     * @param context 当前上下文
     * @return 返回进程的名字
     */
    public static String getAppName(Context context) {
        int pid = Process.myPid(); // Returns the identifier of this process
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = activityManager.getRunningAppProcesses();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pid) {
                    // 根据进程的信息获取当前进程的名字
                    return info.processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有匹配的项，返回为null
        return null;
    }
//
//    public  static int packageNameToPid(String packagename) {
//
//        int pid = -1;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> mRunningProcess = am.getRunningAppProcesses();
//
//        for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
//            if (amProcess.processName.equals(packagename)) {
//                pid = amProcess.pid;
//                break;
//            }
//            Log.d("jack", "PID: " + amProcess.pid + "(processName=" + amProcess.processName + "UID=" + amProcess.uid + ")");
//
//        }
//        return  pid;
//    }


    public  static void killProcessByPid(int pid){

        DataOutputStream os = null;
        try {
            java.lang.Process sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            try {
                os.writeBytes("kill -9 " + pid + "\n");
                Log.d(TAG, "kill -9");
                sleep(2000);
            } catch (IOException e) {
                Log.d(TAG, "kill -9 catch exception");
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }





    public static String getMac() {
        String macSerial = null;
        String str = "";

        try {
            java.lang.Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }


    public  static String getRequestTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:ms");
        Date date = new Date(System.currentTimeMillis());
        String time = simpleDateFormat.format(date);
        return  time;

    }


//
//    {
//        "ip": "9.9.9.9",
//            "mac": "00:0a:95:9d:68:16",
//            "requested_time": "2017-09-07 13:28:30.297",
//            "limt": 1,
//            "opt_card":"1231231231"
//    }





   static public String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();


        String line = null;

        try {

            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }



}
