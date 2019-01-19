package jack.com.bankguardprocess;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static jack.com.bankguardprocess.TaskAbout.PHPAbout.MyTCP.getOneMsgFromClient;
import static jack.com.bankguardprocess.TaskAbout.PHPAbout.MyTCP.sendMsgToClient;

public class WorkThread implements Runnable {

    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
    ServerSocket ss = null;

    Socket socketClient = null;


    Handler mhandler = null;

    public  WorkThread(Handler handler1){
        mhandler = handler1;

    }



    void SendMsgToMainUI(int what,String strContent){
        Message msg = mhandler.obtainMessage(what,strContent);
        mhandler.sendMessage(msg);
    }



    @Override
    public void run() {

        Log.i("GuardProcess","run thread");

        Handler  handler = new Handler(Looper.getMainLooper());

        try {
            ss = new ServerSocket(8989,10, InetAddress.getByName ("127.0.0.1"));
            String strPostRet;
            int nPostRetcode;
            int nErrcode;
            int nTaskCnt= 0;
            final String strTask = "{\"task\":{\"id\":\"1\",\"type\":\"bank\",\"PayeeName\":\"王洪亮\",\"PayeeAccount\":\"6217933135388476\",\"bank\":\"民生银行\",\"PayAmount\":\"0.01\"}}";

            final String strTask1 = "{\"task\":{\"id\":\"1\",\"type\":\"bank\",\"PayeeName\":\"王洪亮\",\"PayeeAccount\":\"6217933135388476\",\"bank\":\"民生银行\",\"PayAmount\":\"0.01\"}}";

            JSONObject retClientJson = null;


            //启动apk
            SendMsgToMainUI(1,"");


            for (int i = 0; i < 6 ;i++){

                //连接客户端
                socketClient = ss.accept();

                Log.i("GuardProcess","accept socket");

                //发送任务给客户端
                if (sendMsgToClient(socketClient,strTask)){
                   String msgFromClient = getOneMsgFromClient(socketClient);

                   if (msgFromClient == null || msgFromClient.equals("")){
                       Log.i("GuardProcess","recv task result is failed");
                       continue;
                   }

                   Log.i("GuardProcess","recv msg from client is"+msgFromClient);
                    retClientJson = new JSONObject(msgFromClient);
                    JSONObject task = (JSONObject) retClientJson.get("task");
                    if (task != null){
                        String result = task.getString("result");
                        SendMsgToMainUI(2,result);
                    }
                }


              //  Log.i("GuardProcess","ready to kill");
               // sendMsgToClient(socketClient,"kill");
            }
            ss.close();

        }
        catch (Exception e){

            Log.i("GuardProcess",Log.getStackTraceString(e));
        }
    }


}
