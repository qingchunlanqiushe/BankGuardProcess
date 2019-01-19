package jack.com.bankguardprocess.TaskAbout.PHPAbout;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jack.com.bankguardprocess.TaskAbout.TaskInfo;
import jack.com.bankguardprocess.common.MyLog;
import jack.com.bankguardprocess.common.SocketClient;

import static jack.com.bankguardprocess.TaskAbout.TaskInfo.ParseTaskInfo;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.craeteJiaoYiMingXiTask;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.createGetBalanceTask;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.createTaskResult;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.parseReturnTask;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.readyCreateRemitData;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.readyGetTaskData;
import static jack.com.bankguardprocess.common.AccountInfo.parseConfigFile;


//和小唐对接
public class PHPTask implements Runnable{



    private  Lock lock = new ReentrantLock();

    ServerSocket ss = null;

    Socket socketClient = null;
    Handler mhandler = null;


    SocketClient client = new SocketClient();


    int nPostRetcode;


    public PHPTask(Handler handler){
        mhandler = handler;
    }


    void SendMsgToMainUI(int what,String strContent){
        Message msg = mhandler.obtainMessage(what,strContent);
        mhandler.sendMessage(msg);
    }


    //获取交易明细的任务
    class  SendJiaoyiMingxi implements  Runnable{
        final long timeInterval = 5*60*1000;

        @Override
        public void run() {
            String jiaoYiMingXiTask = craeteJiaoYiMingXiTask();


            while (true){

                //每隔五分钟取一次
                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                lock.lock();
                try {
                    client.sendRawMsg("kill");
                    //启动apk
                    String msgFromclient = getJiaoYiMingXi();

                    //  MyLog.i("GuardProcess","msgFromClient5 is:"+msgFromclient);
                    if (msgFromclient!= null) {

                        JSONObject jsonObject = new JSONObject(msgFromclient);

                        if (jsonObject.has("task1")) {
                            Object task1 = jsonObject.get("task1");
                            if (task1.toString().contains("Balance")) {
                                Log.i("GuardProcess","get the Balance");
                                //上传余额
                                MyUrlPost.httpPost(Config.getFullReturnBalanceUrl(), task1.toString());  //将余额发送给后台
                            }
                        }
                        if (jsonObject.has("task2")){

                            Object task2 = jsonObject.get("task2");
                            if (task2.toString().contains("InfoData")){
                                Log.i("GuardProcess","get the JiaoYiMingXi");
                                //上传交易明细
                                MyLog.i("GuardProcess","Post data is:"+task2.toString());
                                MyUrlPost.httpPost(Config.getRecordCMBCWebTransferInfo(),task2.toString());  //返回交易明细
                            }

                        }
                    }

                }
                catch (Exception e){
                    Log.i("GuardProcess",Log.getStackTraceString(e));
                }finally {
                    lock.unlock();
                }


            }
        }
    }

    String getJiaoYiMingXi(){
        String jiaoYiMingXiTask = craeteJiaoYiMingXiTask();

        try {
            SendMsgToMainUI(1,"");
            socketClient = ss.accept(); //待连接
            client.socket = socketClient;
            client.sendRawMsg(jiaoYiMingXiTask);
            String msgFromclient = client.recvRawMsg();
            client.sendRawMsg("kill");

            return msgFromclient;
        }
        catch (Exception e){


        }

        return  "";
    }


    String getBalance(){
        try {
            SendMsgToMainUI(1,"");
            socketClient = ss.accept(); //待连接
            client.socket = socketClient;

            String balanceTask = createGetBalanceTask();
            client.sendRawMsg(balanceTask);
            String msgFromclient = client.recvRawMsg();
            client.sendRawMsg("kill");

            return  msgFromclient;
        }
        catch (Exception e){

        }
        return  "";
    }



    public void run() {

        try {

            //读取配置文件
            try {
                parseConfigFile();
            }
            catch (Exception e){
                SendMsgToMainUI(2,"配置文件读取错误，请检查是否存在以及参数是否齐全");
                return;
            }

            ss = new ServerSocket(8989,10, InetAddress.getByName ("127.0.0.1"));

            //获取余额  然后添加机器信息

            String msgFromclient = getJiaoYiMingXi();
            MyLog.i("GuardProcess","get the msgfromclient is:"+msgFromclient);

            try {
                JSONObject jsonObject  =new JSONObject(msgFromclient);

                if (jsonObject.has("task1")) {
                    String task1 = jsonObject.getString("task1");
                    JSONObject jsonObject1 = new JSONObject(task1);
                    if (task1.contains("Balance")) {
                        Log.i("GuardProcess","get the Balance");
                        //上传余额
                        MyUrlPost.httpPost(Config.getFullReturnBalanceUrl(), task1);  //将余额发送给后台
                        String balance = jsonObject1.getString("Balance");
                        String readyCreateRemitData = readyCreateRemitData(balance);
                        MyUrlPost.httpPost(Config.getFullCreateWebRemit(), readyCreateRemitData);  //添加机器信息

                    }
                }

                if (jsonObject.has("task2")){
                    String task2 = jsonObject.getString("task2");
                    if (task2.contains("InfoData")){
                        //上传交易明细
                        MyLog.i("GuardProcess","Post JiaoYiMingXi  data is:"+task2);
                        Pair<Integer, String> pair =  MyUrlPost.httpPost(Config.getRecordCMBCWebTransferInfo(),task2);  //返回交易明细
                        String second = pair.second;
                        MyLog.i("GuardProcess","Get JiaoYiMingXi  response is:"+second);

                    }
                }
            }
            catch (Exception e){
                Log.i("GuardProcess",Log.getStackTraceString(e));
                SendMsgToMainUI(2,"获取账户余额以及明细失败");
                return;
            }

            String strPostRet = "";

            //获取交易明细任务
           //new Thread(new SendJiaoyiMingxi()).start();

           do {
                lock.lock();
                Pair<Integer, String> pair = MyUrlPost.httpPost(Config.getFullGetTaskUrl(), readyGetTaskData());
                strPostRet = pair.second;
                nPostRetcode  = pair.first;
                JSONObject jObject =  null;
                try {
                     jObject = new JSONObject(strPostRet);
                }
                catch (Exception e){
                    Log.i("GuardProcess",e.toString());
                    lock.unlock();
                    continue;
                }

                //判断返回的包里是否有task字段
                if  (jObject.has("task")){
                    TaskInfo taskInfo = ParseTaskInfo(strPostRet);
                    String msgFromClient = "";
                    try {

                        client.sendRawMsg("kill");
                        //启动apk
                        SendMsgToMainUI(1,"");
                        socketClient = ss.accept(); //待连接
                        client.socket = socketClient;

                        client.sendRawMsg(strPostRet);

                        msgFromClient = client.recvRawMsg();

                        client.sendRawMsg("kill");

                        if (msgFromClient == null || msgFromClient.equals("")){
                            msgFromClient =  createTaskResult(taskInfo,"2","客户端断开连接","");
                            MyUrlPost.httpPost(Config.getFullReturnTaskUrl(),msgFromClient);
                        }
                        else{

                            String returnTask = parseReturnTask(msgFromClient);

                            if (returnTask.equals("-1") == true){
                                //发送的任务异常
                                continue;
                            }else {
                                MyUrlPost.httpPost(Config.getFullReturnTaskUrl(),msgFromClient);

                                //此处获取余额
                                //每转账一笔 去获取余额
                                String msgFromclient1 = getBalance();
                                try {

                                    JSONObject jsonObject  =new JSONObject(msgFromclient1);
                                    String balance = jsonObject.getString("Balance"); //没有的话抛出异常
                                    MyUrlPost.httpPost(Config.getFullReturnBalanceUrl(), msgFromclient1);  //上传余额
                                }
                                catch (Exception e){
                                    Log.i("xposed",Log.getStackTraceString(e));
                                    SendMsgToMainUI(2,"获取账户余额失败");
                                   // return;
                                }

                            }

                        }
                    }
                    catch (Exception e){
                        Log.i("GuardProcess",Log.getStackTraceString(e));
                    }
                    finally {
                        lock.unlock();
                    }
                }else{
                    SendMsgToMainUI(2,"正在获取任务中");
                    lock.unlock();
                    Thread.sleep(3000);  //每隔3s去请求任务
                }

            }while (true);

        }
        catch (Exception e){
            SendMsgToMainUI(2,"执行任务已经结束，请重启vxp");
        }finally {
            if (ss != null){
                try {
                    ss.close();
                } catch (IOException e) {
                  //  e.printStackTrace();
                }
                ss = null;
            }
        }

    }
}
