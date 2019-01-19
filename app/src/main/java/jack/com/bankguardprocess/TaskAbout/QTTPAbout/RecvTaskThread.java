package jack.com.bankguardprocess.TaskAbout.QTTPAbout;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.VolumeShaper;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jack.com.bankguardprocess.TaskAbout.TaskProto.CreateRemitterData;

import static jack.com.bankguardprocess.common.AccountInfo.getAccountName;
import static jack.com.bankguardprocess.common.AccountInfo.getCardNo;
import static jack.com.bankguardprocess.common.AccountInfo.parseConfigFile;
import static jack.com.bankguardprocess.common.Tool.mhandler;

//从远程服务器接收任务线程
public class RecvTaskThread implements Runnable {
    public static String TAG = "GuardProcess";
    public Context mContext = null;
    Handler mHandler = null;


    public static String TaskType = "2";        //任务类型

    public static MqttAndroidClient client_Recv = null;
    public static MqttAndroidClient client_Send = null;
    public static String clientIdForRecv = "";   //卡号
    public static String clientIdForSend = "";   //卡号
    public static String topicForRecv = "ABAToClient/CMBC/000001/0001/"; //+卡号
    public static String topicForSend = "ABAToServer/CMBC/000001/0001/"; //+卡号


    public final String USERNAME = "CMBC_App";
    public final String PASSWORD = "123456";
    public static int QOS = 2;
    public final static String LASTWILL = "LastWill";
    public final String brokerUrl = "tcp://103.232.84.205:1888";

    public static MqttClient mqttClientRecv = null;
    public static MqttClient mqttClientSend = null;

    public RecvTaskThread(Context context, Handler handler)
    {
        mContext= context;
        mHandler = handler;
    }

    private void connect() {
        MqttConnectOptions connOpts_Recv = new MqttConnectOptions();
        connOpts_Recv.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        connOpts_Recv.setUserName(USERNAME);
        connOpts_Recv.setPassword(PASSWORD.toCharArray());
        connOpts_Recv.setCleanSession(false);
        connOpts_Recv.setAutomaticReconnect(true);

        MemoryPersistence persistence = new MemoryPersistence();

        client_Recv = new MqttAndroidClient(mContext, brokerUrl, clientIdForRecv);
        try {
            client_Recv.connect(connOpts_Recv, null,new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG,"client_Recv connect onSuccess");
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    Log.i(TAG,Log.getStackTraceString(e));
                }
            });
        } catch (MqttException e) {
            Log.i(TAG,Log.getStackTraceString(e));
        }


        MqttConnectOptions connOpts_Send = new MqttConnectOptions();
        connOpts_Send.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        connOpts_Send.setUserName(USERNAME);
        connOpts_Send.setPassword(PASSWORD.toCharArray());
        connOpts_Send.setCleanSession(true);
        connOpts_Send.setAutomaticReconnect(true);
        connOpts_Send.setWill(topicForSend, LASTWILL.getBytes(), QOS, false);

        client_Send = new MqttAndroidClient(mContext, brokerUrl, clientIdForSend);
        try {
            client_Send.connect(connOpts_Send, null,new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG,"client_Send connect onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    Log.i("GuardProcess",Log.getStackTraceString(e));
                }
            });
        } catch (MqttException e) {
            Log.i(TAG,Log.getStackTraceString(e));
        }
    }

    //收到任务
    private void subscribe() {
        try {
            client_Recv.subscribe(topicForRecv, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                    //收到服务端消息后，在这里派发任务

                    String strTask = new String(message.getPayload());
                    Log.i(TAG,"messageArrived:" + strTask);

                    JSONObject json = new JSONObject(strTask);
                    Map<String,JSONObject> map = new HashMap<>();
                    map.put(TaskType,json);

                    synchronized (TaskHandleThread.taskList)
                    {
                        Log.i(TAG,"add task");
                        TaskHandleThread.taskList.add(map);
                    }
                }
            });
        } catch (Exception e) {
            Log.i(TAG,Log.getStackTraceString(e));
        }
    }


    //发送消息
    public static void publishMessage(String content) {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(QOS);
        message.setRetained(false);
        try {
            client_Send.publish(topicForSend, message);
        } catch (MqttException e) {
            Log.i("GuardProcess",Log.getStackTraceString(e));
        }
    }

    @Override
    public void run() {

        //读取配置文件
        try {
            parseConfigFile();
        }
        catch (Exception e){
            SendMsgToMainUI(2,"配置文件读取错误，请检查是否存在以及参数是否齐全");
            return;
        }



        String cardNo = getCardNo();
        clientIdForRecv = cardNo;
        clientIdForSend = cardNo+"_SEND";
        topicForRecv += cardNo;
        topicForSend += cardNo;

        connect();
        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            Log.i("GuardProcess",Log.getStackTraceString(e));
        }


        String content = "OnLine";
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(QOS);
        message.setRetained(false);
        try {
            client_Send.publish(topicForSend, message);
        } catch (Exception e) {
            Log.i("GuardProcess",Log.getStackTraceString(e));
        }


//        ServiceThread serviceThread = new ServiceThread();
//        new Thread(serviceThread).start();

        //启动任务线程
        TaskHandleThread taskHandleThread = new TaskHandleThread();
        new Thread(taskHandleThread).start();


        CreateRemitterData createRemitterData = new CreateRemitterData();
        createRemitterData.Uuid  ="123E4567-E89B-12D3-A456-556642440000";
       // createRemitterData.Time = String.valueOf(getTimeMillis());
        createRemitterData.Type  =1;
        createRemitterData.Body.Group  ="000001";
        createRemitterData.Body.MCode = "0001";

        createRemitterData.Body.BankName = "民生银行";
        createRemitterData.Body.BankShortName = "CMBC";
        createRemitterData.Body.Balance = "200";
        createRemitterData.Body.RemitCard = getCardNo();
        createRemitterData.Body.AccountName = getAccountName();


        Gson gson = new Gson();

        String toJson = gson.toJson(createRemitterData, CreateRemitterData.class);

//            String jsonByMap = createRemitterData.createJsonByMap(ary).toString();
//            serverfinally.send(jsonByMap);

       publishMessage(toJson);




        try
        {
            Thread.sleep(1000);
        } catch (Exception e)
        {
            Log.i("GuardProcess",Log.getStackTraceString(e));
        }


        //启动银行
//        Message msg = mhandler.obtainMessage(1,"");
//        mhandler.sendMessage(msg);

    }

    void SendMsgToMainUI(int what,String strContent){
        Message msg = mhandler.obtainMessage(what,strContent);
        mhandler.sendMessage(msg);
    }


}
