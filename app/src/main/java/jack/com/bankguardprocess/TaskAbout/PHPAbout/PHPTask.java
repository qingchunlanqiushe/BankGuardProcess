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
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.createTransTaskResult;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.parseReturnTask;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.readyCreateRemitData;
import static jack.com.bankguardprocess.TaskAbout.TaskInfo.readyGetTaskData;



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



    String getJiaoYiMingXiAndBalance(){
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


    boolean handleJiaoYiMingXiAndBalance(String msgFromclient,boolean isforCreateRemitData){

        try {
            JSONObject jsonObject  =new JSONObject(msgFromclient);


            String task1 = jsonObject.getString("task1");
            JSONObject jsonObject1 = new JSONObject(task1);

            jsonObject1.getString("Balance");
            Log.i("GuardProcess","get the Balance");


            if (isforCreateRemitData){
                String balance = jsonObject1.getString("Balance");
                String readyCreateRemitData = readyCreateRemitData(balance);
                MyUrlPost.httpPost(Config.getFullCreateWebRemit(), readyCreateRemitData);  //添加机器信息
            }

            //上传余额
            MyUrlPost.httpPost(Config.getFullReturnBalanceUrl(), task1);  //将余额发送给后台


            try {
                String task2 = jsonObject.getString("task2");
                JSONObject jsonObject2 = new JSONObject(task2);
                jsonObject2.getJSONObject("InfoData");

                //上传交易明细
                MyLog.i("GuardProcess","Post JiaoYiMingXi  data is:"+task2);
                Pair<Integer, String> pair =  MyUrlPost.httpPost(Config.getRecordCMBCWebTransferInfo(),task2);  //返回交易明细
            }
            catch (Exception e){

            }

            return  true;
        }
        catch (Exception e){
            Log.i("GuardProcess",Log.getStackTraceString(e));
            SendMsgToMainUI(2,"获取账户余额以及明细失败");
            return false;
        }


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

    //获取交易明细的任务
    class  SendJiaoyiMingxi implements  Runnable{
        final long timeInterval = 60*60*1000;

        @Override
        public void run() {

            while (true){

                try {
                    Thread.sleep(timeInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                lock.lock();
                try {
                    client.sendRawMsg("kill");
                    //启动apk
                    String msgFromclient = getJiaoYiMingXiAndBalance();

                    handleJiaoYiMingXiAndBalance(msgFromclient,false);

                }
                catch (Exception e){
                    Log.i("GuardProcess",Log.getStackTraceString(e));
                }finally {
                    lock.unlock();
                }


            }
        }
    }




    public void run() {

        try {

            ss = new ServerSocket(8989,10, InetAddress.getByName ("127.0.0.1"));

            //获取余额  然后添加机器信息
            String msgFromclient = getJiaoYiMingXiAndBalance();
            MyLog.i("GuardProcess","get the msgfromclient is:"+msgFromclient);


            if (handleJiaoYiMingXiAndBalance(msgFromclient,true) == false){
                return;
            }

            String strPostRet = "";

            //获取交易明细任务
           new Thread(new SendJiaoyiMingxi()).start();

           do {
                lock.lock();
                Pair<Integer, String> pair = MyUrlPost.httpPost(Config.getFullGetTaskUrl(), readyGetTaskData());
                strPostRet = pair.second;
                nPostRetcode  = pair.first;
                JSONObject jObject =  null;

                int taskType = 0; //2:转账任务 3:获取余额 4:交易明细

                String msgFromClient = "";
                try {

                    jObject = new JSONObject(strPostRet);
                    JSONObject jsonObjectTask = jObject.getJSONObject("task");
                    taskType = jsonObjectTask.getInt("type");


                    if (taskType == 3)
                        continue; //获取余额任务忽视，因为交易明细已经包含了此任务


                    client.sendRawMsg("kill");
                    //启动apk
                    SendMsgToMainUI(1,"");
                    socketClient = ss.accept(); //待连接
                    client.socket = socketClient;

                    client.sendRawMsg(strPostRet);

                    msgFromClient = client.recvRawMsg();

                    client.sendRawMsg("kill");

                    if (msgFromClient == null || msgFromClient.equals("")){

                        if (taskType ==2){
                            TaskInfo taskInfo = ParseTaskInfo(strPostRet);
                            msgFromClient =  createTransTaskResult(taskInfo,"2","客户端断开连接","");
                            MyUrlPost.httpPost(Config.getFullReturnTaskUrl(),msgFromClient);
                        }
                        continue;
                    }

                    if (taskType == 2){

                        String returnTask = parseReturnTask(msgFromClient);
                        if (returnTask.equals("-1") == false){
                            MyUrlPost.httpPost(Config.getFullReturnTaskUrl(),msgFromClient);


                            //此处获取余额
                            //每转账一笔 去获取余额
//                            String msgFromclient1 = getBalance();
//                            try {
//
//                                JSONObject jsonObject  =new JSONObject(msgFromclient1);
//                                String balance = jsonObject.getString("Balance"); //没有的话抛出异常
//                                MyUrlPost.httpPost(Config.getFullReturnBalanceUrl(), msgFromclient1);  //上传余额
//                            }
//                            catch (Exception e){
//                                Log.i("xposed",Log.getStackTraceString(e));
//                                SendMsgToMainUI(2,"获取账户余额失败");
//                            }
                        }
                        continue;
                    }



                    if (taskType ==4){
                        // 4:交易明细
                        try {
                            handleJiaoYiMingXiAndBalance(msgFromClient,false);
                        }
                        catch (Exception e){
                            Log.i("GuardProcess",Log.getStackTraceString(e));
                            SendMsgToMainUI(2,"获取账户余额以及明细失败");
                        }
                        continue;
                    }
                }
                catch (Exception e){
                    Log.i("GuardProcess",Log.getStackTraceString(e));
                }
                finally {
                    lock.unlock();
                    SendMsgToMainUI(2,"正在获取任务中");
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
