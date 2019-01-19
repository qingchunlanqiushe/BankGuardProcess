package jack.com.bankguardprocess.TaskAbout.QTTPAbout;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jack.com.bankguardprocess.TaskAbout.TaskProto.BankTransfersData;
import jack.com.bankguardprocess.TaskAbout.XiaoPing.ServerFinally;

import static jack.com.bankguardprocess.MainActivity.serverfinally;

//任务处理线程
public class TaskHandleThread implements Runnable {
    public static String TAG = "GuardProcess";
    public static List<Map> taskList = new LinkedList<>(); //任务列表
    public static boolean IsDoTasking = false;
    public static Map map = null;


    @Override
    public void run() {
        Log.i(TAG,"TaskHandleThread run");
        while (true)
        {
            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

//            synchronized (taskList)
//            {
//                Log.i(TAG,"当前任务数量1：" + taskList.size());
//            }

            if ( taskList.size() > 0){
                Log.i(TAG,"IsDoTasking:" + IsDoTasking);
                if (IsDoTasking == false)
                {
                    synchronized (taskList)
                    {
                        try
                        {
                            //取第一个任务
                            map = taskList.get(0);
                           // dispatchTask(parseTask(map));
                           // dispatchTask("123");

                            taskList.remove(map);

                            Log.i(TAG,"当前任务数量2：" + taskList.size());

                            //接收返回结果线程
                          //  RecvResultInfoThread recvResultInfoThread = new RecvResultInfoThread();
                          //  new Thread(recvResultInfoThread).start();
                        }catch (Exception e)
                        {
                            taskList.remove(map);
                            Log.i(TAG,Log.getStackTraceString(e));
                        }
                    }
                }
            }
        }
    }

    //派发任务
    public void dispatchTask(String strTask)
    {
        Log.i(TAG,"开始派发任务");

        serverfinally.send(strTask);

        //SendMsg(strTask);
//        if (strTask != null)
//        {
//            try
//            {
//                OutputStream os = ServiceThread.clientSocket.getOutputStream();
//                byte[] byteArray = Base64.encode(strTask.getBytes(), Base64.NO_PADDING | Base64.NO_WRAP);
//                os.write(intToByteArray(byteArray.length));
//                os.write(byteArray);
//            } catch (IOException e)
//            {
//                Log.i(TAG,Log.getStackTraceString(e));
//            }
//
           IsDoTasking = true;
//        }
    }



    private String parseTask(Map map){
        Log.i(TAG,"开始解析任务");
        String strResult = null;

        if (map != null)
        {
            for (Object key : map.keySet())
            {
                strResult = (String)key;
                Log.i("GuardProcess",strResult);
                strResult += "#";
                Log.i("GuardProcess",strResult);
                strResult += map.get(key).toString();
                Log.i("GuardProcess",strResult);
            }
        }

        return strResult;
    }

    public String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date curDate = new Date(System.currentTimeMillis());
        String strTime = formatter.format(curDate);
        return strTime;
    }

    public byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[3] = (byte)((i >> 24) & 0xFF);
        result[2] = (byte)((i >> 16) & 0xFF);
        result[1] = (byte)((i >> 8) & 0xFF);
        result[0] = (byte)(i & 0xFF);
        return result;
    }
}
