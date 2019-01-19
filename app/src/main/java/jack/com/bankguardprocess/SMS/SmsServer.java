package jack.com.bankguardprocess.SMS;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.smsserver.IServerlInterface;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsServer extends Service {
    public static final String TAG = "GuardProcess";
    SMSObserver smsContentObserver = null;

    String message = null;
    String strResult = null;

    public Object lock = new Object();
    Boolean isSend = false;
    Boolean isTimeout = false;
    int mTimeout = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d(TAG,msg.obj.toString());
                synchronized (lock)
                {
                    Log.i(TAG,"copy that");
                    message = msg.obj.toString();
                    //lock.notify();
                }
            }

            if (msg.what == 2) {
                synchronized (lock)
                {
                    isTimeout = true;
                }
            }
        }};

    private final IServerlInterface.Stub binder = new  IServerlInterface.Stub()
    {
        @Override
        public String getSms(final String strCode, final int nTimeout) throws RemoteException
        {
            strResult = null;
            message = null;
            mTimeout = 0; // 居然没初始化

            Log.i(TAG,"enter the getSms");
            new Thread(new Runnable() {
                @Override
                public void run() {

                        while(true)
                        {
                            try {
                                //lock.wait(nTimeout);
                                Thread.sleep(1000);
                                mTimeout += 1000;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG,"finding Smscode... ");


                            synchronized (lock) {
                                if (message != null) {
                                    Pattern p = Pattern.compile(strCode);  //"\\d{6}"
                                    Log.i(TAG,"the msg is "+message);
                                    Matcher m = p.matcher(message);
                                    if (m.find()) {
                                        strResult = m.group().toString();
                                        Log.i(TAG,"the result is "+strResult);
                                        break;
                                    } else {
                                        message = null;
                                        continue;
                                    }
                                }

                                if (mTimeout == nTimeout) {
                                    handler.sendEmptyMessage(2);
                                    break;
                                }
                            }
                        }

                }
            }).start();

            while(true)
            {
                synchronized (lock)
                {
                    if (isTimeout)
                    {
                        isTimeout = false;
                        return strResult;
                    }

                    if (strResult != null)
                    {
                        return strResult;
                    }
                }
            }
        }

        @Override
        public boolean setSms(String strPhoneNumber, String strMsg) throws RemoteException
        {
            //获取短信管理器
            SmsManager smsManager = SmsManager.getDefault();
            List<String> contents = smsManager.divideMessage(strMsg);

            String SENT_SMS_ACTION = "SENT_SMS_ACTION";
            Intent sentIntent = new Intent(SENT_SMS_ACTION);
            PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent, 0);
            // register the Broadcast Receivers
            getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context _context, Intent _intent)
                {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Log.d(TAG, "短信发送成功");
                            isSend = true;
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
            }, new IntentFilter(SENT_SMS_ACTION));

            //处理返回的接收状态
            String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
            // create the deilverIntent parameter
            Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
            PendingIntent deliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, deliverIntent, 0);
            getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context _context, Intent _intent) {
                    Log.d(TAG, "收信人已经成功接收");
                }
            }, new IntentFilter(DELIVERED_SMS_ACTION));

            for (String text : contents) {
                smsManager.sendTextMessage(strPhoneNumber, null, text, sentPI, deliverPI);
                isSend = true;
            }

            return isSend;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // 注册短信变化监听
        SMSObserver smsContent = new SMSObserver(this, handler);
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContent);
    }

    @Override
    public void onDestroy() {
        if (smsContentObserver != null) {
            getContentResolver().unregisterContentObserver(smsContentObserver);
            smsContentObserver = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return binder;
    }


}