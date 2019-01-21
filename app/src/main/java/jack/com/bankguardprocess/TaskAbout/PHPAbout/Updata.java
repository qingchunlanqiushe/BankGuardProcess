package jack.com.bankguardprocess.TaskAbout.PHPAbout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jack.com.bankguardprocess.R;
import jack.com.bankguardprocess.common.GetAppInfo;

/**
 * Created by admin on 2019/1/7.
 */

public class Updata {

    Handler handler = new Handler(Looper.getMainLooper());

    private Context mContext; //上下文
    private static final String savePath = "/sdcard/updateAPK/"; //apk保存到SD卡的路径

    private ProgressBar mProgress; //下载进度条控件
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADOK = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败
    private int progress; //下载进度
    private boolean cancelFlag = false; //取消下载标志位

    private double serverGuardProcessrVersion = 0.0; //从服务器获取的版本号
    private double currentGuardProcessVersion = 1.0; //客户端当前的版本号


    private double serverXposedVersion = 0.0;
    private double currentXposedVersion = 0.0;


    private String updateDescription = "更新描述"; //更新内容描述信息
    private boolean forceUpdate = true; //是否强制更新

    private AlertDialog alertDialog1, alertDialog2; //表示提示对话框、进度条对话框

    /** 构造函数 */
    public Updata(Context context) {
        this.mContext = context;
        getCurVerion();
    }


    GetAppInfo appInfo = new GetAppInfo();


    public double getCurVerion(){

        List<GetAppInfo.AppInfo> appInfo = this.appInfo.getAppInfo(1);

        for (GetAppInfo.AppInfo item : appInfo){
            if (item.packageName.equals("jack.com.bankguardprocess")){
                currentGuardProcessVersion = Double.valueOf(item.versionName);
                Log.i("GuardProcess","currentGuardProcessVersion:"+currentGuardProcessVersion);
            }

            if (item.packageName.equals("com.coffee.xposed")){
                currentXposedVersion  = Double.valueOf(item.versionName);
                Log.i("GuardProcess","currentXposedVersion:"+currentXposedVersion);
            }
        }
        return currentGuardProcessVersion;
    }

    public double getCurrentGuardProcessVersion(){

        return currentGuardProcessVersion;
    }


    public double getCurrentXposedVersion() {
        return currentXposedVersion;
    }

    /** 显示更新对话框 */
    public void showNoticeDialog(List<Pair<String,String>> fileAry) {

        final List<Pair<String,String>> tmpary  = fileAry;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("发现新版本" );
        dialog.setMessage(updateDescription);
        dialog.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                arg0.dismiss();
                showDownloadDialog(tmpary);
            }
        });
        //是否强制更新
        if (forceUpdate == false) {
            dialog.setNegativeButton("待会更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    arg0.dismiss();
                }
            });
        }


        alertDialog1  = dialog.create();
        alertDialog1.setCancelable(false);
        alertDialog1.show();
    }

    /** 显示进度条对话框 */
    public void showDownloadDialog(List<Pair<String,String>> fileAry) {
        final List<Pair<String,String>> tmpary  = fileAry;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("正在更新");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        dialog.setView(v);

        //如果不是强制更新，则显示取消按钮
        if (forceUpdate == false) {
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    arg0.dismiss();
                    cancelFlag = false;
                }
            });
        }

        alertDialog2  = dialog.create();
        alertDialog2.setCancelable(false);
        alertDialog2.show();

        //下载apk

        new Thread(new downloadFile(tmpary)).start();
    }



    public class __postdata implements Runnable{

       public Pair<Integer, String> pair = null;
        public JSONObject  jsonObject = null;

        __postdata(JSONObject obj){
            jsonObject= obj;
        }

        @Override
        public void run() {

            try {
               pair =  MyUrlPost.httpPost(Config.getAppVersionsFile(),jsonObject.toString());
            }
            catch (Exception e){

            }
        }
    }


    void getDownloadList( List<Pair<String,String>> list,String apkName,double apkVersion){


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_name",apkName);
            jsonObject.put("version_num",apkVersion);

            __postdata postdataobj = new __postdata(jsonObject);
            Thread thread = new Thread(postdataobj);
            thread.start();
            thread.join();

            Pair<Integer, String> pair =postdataobj.pair;

            String strPostRet = pair.second;
            int nPostRetcode  = pair.first;

            //{"msg":"http:\/\/61.196.186.165\/programs\/bankguard.apk"}
            JSONObject jsonObject1 = new JSONObject(strPostRet);

            String downloadurl = jsonObject1.getString("msg");

            if (downloadurl.contains("http")){
                String apkUrl = downloadurl;
                String fullSaveName = savePath+apkName;
                Pair<String,String> pair1 = new Pair<>(apkUrl,fullSaveName);
                list.add(pair1);
            }

        }
        catch (Exception e){
            Log.i("xposed",Log.getStackTraceString(e));
        }

    }





    public void postData(){

//        {
//            "app_name": "浦发银行",
//                "version_num": 1.2
//        }

        try {
            List<Pair<String,String>> list = new ArrayList<>();

            getDownloadList(list,"bankxposed.apk",getCurrentXposedVersion());
            getDownloadList(list,"bankguard.apk",getCurrentGuardProcessVersion());


            if (list.size() > 0)
                showNoticeDialog(list);

        }
        catch (Exception e){
            Log.i("GuardProcess",Log.getStackTraceString(e));
        }

    }



    class  downloadFile implements Runnable{

        String mFileUrl;

        String mSaveFileName;

        List<Pair<String,String>> mfileAry;

        public downloadFile(List<Pair<String,String>> fileAry){
            mfileAry = fileAry;
        }

        @Override
        public void run() {
                try {

                    for (int i = 0; i < mfileAry.size();i++){

                        Pair<String, String> pair = mfileAry.get(i);
                        mFileUrl = pair.first;
                        mSaveFileName = pair.second;

                        URL url = new URL(mFileUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.connect();

                        int length = conn.getContentLength();
                        InputStream is = conn.getInputStream();

                        File file = new File(savePath);
                        if(!file.exists()){
                            file.mkdir();
                        }
                        String apkFile = mSaveFileName;
                        File ApkFile = new File(apkFile);
                        if (!ApkFile.exists()){
                            ApkFile.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(ApkFile);

                        int count = 0;
                        byte buf[] = new byte[1024];

                        do{
                            int numread = is.read(buf);
                            count += numread;
                            progress = (int)(((float)count / length) * 100);
                            //更新进度
                            mHandler.sendEmptyMessage(DOWNLOADING);
                            if(numread <= 0){
                                //下载完成通知安装
                                installAPK(mSaveFileName);
                                if (mSaveFileName.contains("bankxposed")) //如果是xposed
                                    Thread.sleep(10*1000);
                                break;
                            }
                            fos.write(buf, 0, numread);
                        }while(!cancelFlag); //点击取消就停止下载.

                        fos.close();
                        is.close();

                        if (i+1 == mfileAry.size()){
                            mHandler.sendEmptyMessage(DOWNLOADOK);
                        }

                    }
                }
                catch (Exception e){
                    mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                    Log.i("GuardProcess",Log.getStackTraceString(e));
                }

        }
    }



    /** 更新UI的handler */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case DOWNLOADING:
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOADOK:
                    if (alertDialog2 != null)
                        alertDialog2.dismiss();

                    break;
                case DOWNLOAD_FAILED:
                    Toast.makeText(mContext, "网络断开，请稍候再试", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    /** 下载完成后自动安装apk */
    public void installAPK(String SaveFileName) {
        File apkFile = new File(SaveFileName);
        if (!apkFile.exists()) {
            return;
        }

        /*if (Build.VERSION.SDK_INT >= 24) {
            Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", new File(SaveFileName));
            Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(intent);
        } else*/ {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + SaveFileName), "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        }
    }
}

