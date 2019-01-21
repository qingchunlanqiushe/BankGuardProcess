package jack.com.bankguardprocess;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jack.com.bankguardprocess.TaskAbout.PHPAbout.PHPTask;
import jack.com.bankguardprocess.TaskAbout.XiaoPing.ServerFinally;
import jack.com.bankguardprocess.TaskAbout.XiaoPing.TransfersInterface;
import jack.com.bankguardprocess.common.FileConfig;
import jack.com.bankguardprocess.common.MacUtils;
import jack.com.bankguardprocess.common.Tool;
import jack.com.bankguardprocess.TaskAbout.PHPAbout.Updata;

import static jack.com.bankguardprocess.common.AccountInfo.getAccountName;
import static jack.com.bankguardprocess.common.AccountInfo.getCardNo;
import static jack.com.bankguardprocess.common.AccountInfo.getGroup;
import static jack.com.bankguardprocess.common.AccountInfo.getloginPwd;
import static jack.com.bankguardprocess.common.AccountInfo.parseConfigFile;

public class MainActivity extends Activity implements View.OnClickListener {

    Updata mUpdateManager;


    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {

        }
    }



    public  static ServerFinally serverfinally = null;

    public  static TransfersInterface transfersInterface  = null;


    public  static  String strMac = "";

    TextView tvTaskState = null;

    public  void startApp1(){
        ComponentName componetName = new ComponentName(
                //这个是另外一个应用程序的包名
                "cn.com.cmbc.newmbank",
                //这个参数是要启动的Activity
                "cn.com.cmbc.newmbank.activity.MainActivity");
//        Intent intent= new Intent("chroya.foo");
        Intent intent= new Intent();
        //我们给他添加一个参数表示从apk1传过去的
        Bundle bundle = new Bundle();
        bundle.putString("arge1", "这是跳转过来的！来自apk1");
        intent.putExtras(bundle);
        intent.setComponent(componetName);
        startActivity(intent);

    }



    public void startApp(){
        startActivity(getPackageManager().getLaunchIntentForPackage("cn.com.cmbc.newmbank"));

    }


    class MyHandler extends Handler{

        int ncnt = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    startApp();
//                    if (ncnt == 0)
//                    {
//                        ncnt = 1;
//                        startApp();
//                    }
//                    else{
//                        startApp1();
//                    }

                    break;
                case 2: {
                    String strContent = (String) msg.obj;
                    tvTaskState.setText(strContent);
                }

                case 3:{
//                    Bundle data = msg.getData();
//                    String body = data.getString("body");

                }
                break;
            }
            super.handleMessage(msg);
        }
    }


      Handler myHandler = new MyHandler();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);

        //获取MAC地址
        strMac = MacUtils.getMobileMAC(MainActivity.this);


        mUpdateManager = new Updata(this);


        Button btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        tvTaskState = (TextView) findViewById(R.id.taskstate);

        tvTaskState.setText("未开始执行任务");

        Tool.mhandler = myHandler;

        checkPermission();

        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvVersion.setText("当前客户端版本:"+mUpdateManager.getCurrentGuardProcessVersion()+"    xposed插件版本:"+mUpdateManager.getCurrentXposedVersion()); //设置当前客户端版本号


        FileConfig.deletefile("/sdcard/");


        //读取配置文件
        try {
            parseConfigFile();

            TextView tvCardInfo = (TextView)findViewById(R.id.curCardInfo);

            String strCardInfo = "配置文件的信息:"+"\n"+"当前账户:"+getAccountName()+"\n"+"当前卡号:"+getCardNo()+"\n"+"当前登陆密码:"+getloginPwd()+"\n"+"当前分组:"+getGroup();

            tvCardInfo.setText(strCardInfo);

            mUpdateManager.postData();
        }
        catch (Exception e){
            tvTaskState.setText("配置文件读取错误，请检查是否存在以及参数是否齐全");
            btnStart.setEnabled(false);

        }


    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    @Override
    public void onClick(View v) {

        tvTaskState.setText("开始执行任务");


       new Thread(new PHPTask(myHandler)).start();
    }
}
