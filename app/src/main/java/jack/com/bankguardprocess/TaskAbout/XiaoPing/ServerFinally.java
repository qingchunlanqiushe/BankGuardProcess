package jack.com.bankguardprocess.TaskAbout.XiaoPing;

import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by admin on 2018/12/23.
 */

public class ServerFinally implements Runnable{
    private static final String TAG = "GuardProcess";
    LocalServerSocket server;
    LocalSocket client;
    PrintWriter os;
    BufferedReader is;

    MsgCallBackRunnable m_callback =  null;

    Handler handler;


    private void OpenAccept(){
        Log.i(TAG, "Server=======打开服务=========");
        try {
            client = server.accept();
            Log.i(TAG, "Server=======客户端连接成功=========");
            Credentials cre = client.getPeerCredentials();
            Log.i(TAG, "===客户端ID为:" + cre.getUid());
            os = new PrintWriter(client.getOutputStream());
            is = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public ServerFinally(Handler handler){
        this.handler = handler;
        try {
            server = new LocalServerSocket("com.localsocket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ServerFinally(MsgCallBackRunnable callback){
        try {
            server = new LocalServerSocket("com.localsocket");
            m_callback = callback;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //发数据
    public void send(String data){
        if (os != null) {
            os.println(data);
            os.flush();
        }
    }

    //接数据
    @Override
    public void run() {
        String result = "";

        OpenAccept();
        while(true){
            try {
                if (is == null)continue;

                result = is.readLine();
                if (result == null){
                    Log.i("GuardProcess", "客户端似乎已经断开, 从新等待连接");
                    OpenAccept();
                }

                m_callback.handleMsg(result);

//                Message msg = handler.obtainMessage();
//                msg.obj = result;
//                msg.what = 2;
//                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeClient(){
        try {
            if (os != null) {
                os.close();
                os = null;
            }
            if (is != null) {
                is.close();
                is = null;
            }
            if(client != null){
                client.close();
                client = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
            if(client != null){
                client.close();
            }
            if (server != null) {
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}