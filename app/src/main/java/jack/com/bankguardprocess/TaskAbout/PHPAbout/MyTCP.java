package jack.com.bankguardprocess.TaskAbout.PHPAbout;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MyTCP {

    //发送任务给客户端
    public static boolean sendMsgToClient(Socket socketClient, String strCurTask){

        try {
        //    Log.d("GuardProcess", "当前任务:" + strCurTask);
            PrintWriter pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream(), "UTF-8")), true);
            pout.println(strCurTask);

        } catch (Exception e) {
            Log.i("GuardProcess", Log.getStackTraceString(e));
            return  false;
        }
        return  true;
    }


    //从客户端读取消息
    public static String getMsgFromClient(Socket socketClient){

        String strMsg = "";

        try {
            BufferedReader tmpin = null;
            tmpin = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            String line = null;
            while ((line = tmpin.readLine()) != null) {
                strMsg += line;
            }

        } catch (Exception e) {
            Log.i("GuardProcess", Log.getStackTraceString(e));
        }

        return strMsg;
    }


    //只读取一条信息
    public static String getOneMsgFromClient(Socket socketClient){

        String strMsg = "";

        try {
            BufferedReader tmpin = null;
            tmpin = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            String line = null;

            line = tmpin.readLine();
            strMsg  = line;

        } catch (Exception e) {
            Log.i("GuardProcess", Log.getStackTraceString(e));
        }

        return strMsg;
    }



}
