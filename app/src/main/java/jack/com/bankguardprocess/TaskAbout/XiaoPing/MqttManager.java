package jack.com.bankguardprocess.TaskAbout.XiaoPing;

/**
 * Created by admin on 2018/12/25.
 */

import android.content.Context;
import android.util.Log;
import android.webkit.WebMessagePort;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static jack.com.bankguardprocess.common.AccountInfo.getCardNo;

public class MqttManager {
    public static final String TAG = "GuardProcess";

    private String host = "tcp://103.113.9.205:1888";

//    private String host = "tcp://103.232.84.205:1888";
    private String userName = "CMBC_App";
    private String passWord = "123456";
    private String mSclientId = "";
    private String mPclientId = "";

    private static MqttManager mqttManager = null;
    private MqttClient SubscribeMqttClient;
    private MqttClient PublishMqttClient;

    private MqttConnectOptions SubscribeConnectOptions;
    private MqttConnectOptions PublishConnectOptions;
    private MqttMsgArrivedCallback mqttMsgArrivedCallback;


    private void setClientId(){
        String cardNo = getCardNo();
        mSclientId = cardNo;
        mPclientId = cardNo+"_SEND";

    }

    public MqttManager(Context context , MqttMsgArrivedCallback mqttMsgArrivedCallback){
       // clientId = MqttClient.generateClientId();
        setClientId();
        this.mqttMsgArrivedCallback = mqttMsgArrivedCallback;
    }

    public MqttManager getInstance(Context context, MqttMsgArrivedCallback mqttMsgArrivedCallback){
        if(mqttManager == null){
            mqttManager = new MqttManager(context, mqttMsgArrivedCallback);
        }else{
            return mqttManager;
        }
        return null;
    }

    public void connect(){
        try{
            //订阅 MqttClient 连接...
            SubscribeMqttClient = new MqttClient(host, mSclientId,new MemoryPersistence());
            SubscribeConnectOptions = new MqttConnectOptions();
            SubscribeConnectOptions.setUserName(userName);
            SubscribeConnectOptions.setPassword(passWord.toCharArray());
            SubscribeConnectOptions.setCleanSession(false);
            SubscribeConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            SubscribeMqttClient.setCallback(mqttCallback);
            SubscribeMqttClient.connect(SubscribeConnectOptions);

            //推送 MqttClient 连接...
            PublishMqttClient = new MqttClient(host, mPclientId,new MemoryPersistence());
            PublishConnectOptions = new MqttConnectOptions();
            PublishConnectOptions.setUserName(userName);
            PublishConnectOptions.setPassword(passWord.toCharArray());
            PublishConnectOptions.setCleanSession(true);
            PublishConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            PublishMqttClient.connect(SubscribeConnectOptions);

        }catch (MqttException e){

            String stackTraceString = Log.getStackTraceString(e);
            Log.i("GuardProcess", stackTraceString);
        }
    }

    public void subscribe(String topic,int qos){
        if(SubscribeMqttClient != null){
            int[] Qos = {qos};
            String[] topic1 = {topic};
            try {
                SubscribeMqttClient.subscribe(topic1, Qos);
                Log.d(TAG,"订阅topic : "+ topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(String topic,String msg,int qos) {
        try {
            if (PublishMqttClient != null) {
                MqttMessage message = new MqttMessage();
                message.setQos(qos);
                message.setRetained(false);
                message.setPayload(msg.getBytes());
                PublishMqttClient.publish(topic, message);
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.i(TAG,"connection lost" + Log.getStackTraceString(cause));
        }

        @Override
        public void messageArrived(String topic, MqttMessage message){

            String payload = new String(message.getPayload());
            mqttMsgArrivedCallback.messageArrived(payload);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.i(TAG,"deliveryComplete");
        }
    };
}