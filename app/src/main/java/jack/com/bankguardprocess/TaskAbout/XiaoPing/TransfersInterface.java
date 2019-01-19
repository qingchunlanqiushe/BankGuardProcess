package jack.com.bankguardprocess.TaskAbout.XiaoPing;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.solver.GoalRow;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.GetLastOrderStatusData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.GetMachineStatusData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.GetRemitBalanceData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.GetRemitBalanceRetData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.UpdateLastOrderStatusData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.UpdateLastOrderStatusRetData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.UpdateMachineStatusData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.UpdateMachineStatusRetData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.UpdateRemitBalanceData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage.UpdateRemitBalanceRetData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.BankTransfersData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.BankTransfersRetData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.CreateRemitterData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.GetBillDataData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.GetBillDataRetRetData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.UpdateBillDataData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.UpdateBillDataRetRetData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.UpdateTransferResultData;
import jack.com.bankguardprocess.TaskAbout.TaskProto.UpdateTransferResultRetRetData;

import static jack.com.bankguardprocess.MainActivity.serverfinally;
import static jack.com.bankguardprocess.common.AccountInfo.getAccountName;
import static jack.com.bankguardprocess.common.AccountInfo.getCardNo;
import static jack.com.bankguardprocess.common.AccountInfo.parseConfigFile;

/**
 * Created by admin on 2018/12/27.
 */

public class TransfersInterface {

    private MqttManager mqttManager;
    private Context context;

//    private String topicForRecv = "ABAToClient/CMBCHINA/000001/0001/6214834320686448";
//    private String topicForSend = "ABAToServer/CMBCHINA/000001/0001/6214834320686448";

    private String Uuid = "";
    private String lastOrderNo = "";

    private Handler mhandler;


    public  String topicForRecv = "ABAToClient/CMBC/000001/0001/"; //+卡号
    public  String topicForSend = "ABAToServer/CMBC/000001/0001/"; //+卡号



    public static List<String> BankTransfersDataList = new LinkedList<>();


    public void UpdateStringTransfers(String strMsg){
        mqttManager.publish(topicForSend, strMsg, 2);
    }



    public long getTimeMillis(){
       return System.currentTimeMillis();
    }


    private void setTopic(){

        String cardNo = getCardNo();
        topicForRecv += cardNo;
        topicForSend += cardNo;

    }

    void SendMsgToMainUI(int what,String strContent){
        Message msg = mhandler.obtainMessage(what,strContent);
        mhandler.sendMessage(msg);
    }


    public  TransfersInterface(Context context,Handler handler){

        mhandler = handler;

        //读取配置文件
        try {
            parseConfigFile();
        }
        catch (Exception e){
            SendMsgToMainUI(2,"配置文件读取错误，请检查是否存在以及参数是否齐全");
            return;
        }

        setTopic();
        this.context = context;
        mqttManager = new MqttManager(context, new MsgHandle());
        mqttManager.connect();
        mqttManager.subscribe(topicForRecv, 2);
    }

    public void CreateRemitter(){
        try {
            CreateRemitterData createRemitterData = new CreateRemitterData();
            createRemitterData.Uuid  ="123E4567-E89B-12D3-A456-556642440001";
            createRemitterData.Time = String.valueOf(getTimeMillis());
            createRemitterData.Type  =1;
            createRemitterData.Body.Group  ="000001";
            createRemitterData.Body.MCode = "0001";

            createRemitterData.Body.BankName = "民生银行";
            createRemitterData.Body.BankShortName = "CMBC";
            createRemitterData.Body.Balance = "200";
            createRemitterData.Body.RemitCard = getCardNo();
            createRemitterData.Body.AccountName = getAccountName();
            createRemitterData.Body.MAC = "1.1.1.1";
            createRemitterData.Body.IP = "1.1.1.1";
            createRemitterData.Body.Limit = "1";
            createRemitterData.Body.ChannelType =2;


            Gson  gson = new Gson();

            String toJson = gson.toJson(createRemitterData, CreateRemitterData.class);

//            String jsonByMap = createRemitterData.createJsonByMap(ary).toString();
//            serverfinally.send(jsonByMap);

             mqttManager.publish(topicForSend, toJson, 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //上传
    public void UpdateRemitter(String strMsg){
        mqttManager.publish(topicForSend, strMsg, 2);
    }


    public void UpdateTransferResult(String orderNo, int OrderStatus){
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ClientTime", getTimeMillis());
            jsonBody.put("OrderNo", orderNo);
            jsonBody.put("OrderStatus", OrderStatus);
            jsonBody.put("OrderFailedReason", "Bank");
            jsonBody.put("TransferImg", "");
            jsonBody.put("Balance", 4200.00);
            jsonBody.put("MachineStatus", 0);
            jsonBody.put("CardStatus", 0);
            jsonBody.put("CardStatusTxt", "");
            jsonBody.put("TaskStatus", 1);
            jsonBody.put("ErrorCode", 0);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uuid", Uuid);
            jsonObject.put("Type", 3);
            jsonObject.put("Time", getTimeMillis());
            jsonObject.put("Body", jsonBody);

            String Msg = jsonObject.toString();
            mqttManager.publish(topicForSend, Msg, 2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void UpdateBillData(){
        try {

            JSONArray jsonArrayone = new JSONArray();
            jsonArrayone.put(0, "2018-12-28");
            jsonArrayone.put(1, "22:48:06");
            jsonArrayone.put(2, "-980.00");
            jsonArrayone.put(3, "14696.62");
            jsonArrayone.put(4, "郦水金");
            jsonArrayone.put(5, "6217001430*****2275");
            jsonArrayone.put(6, "电子汇出");
            jsonArrayone.put(7, "电子汇出");

            JSONArray jsonArraytwo = new JSONArray();
            jsonArraytwo.put(0, "2018-12-28");
            jsonArraytwo.put(1, "23:07:05");
            jsonArraytwo.put(2, "-392.00");
            jsonArraytwo.put(3, "14304.62");
            jsonArraytwo.put(4, "纪卫东");
            jsonArraytwo.put(5, "6222033301*****1997");
            jsonArraytwo.put(6, "跨行转出");
            jsonArraytwo.put(7, "跨行转出");

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, jsonArrayone);
            jsonArray.put(1, jsonArraytwo);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ClientTime", "15887790789");
            jsonBody.put("Id", "6217000450011245058");
            jsonBody.put("AccountName", "石六十三");
            jsonBody.put("UpdateTime", "2018-10-24 11:00:00");
            jsonBody.put("Info", jsonArray);
            jsonBody.put("Mac", "3C-54-60-A4-70-24");
            jsonBody.put("Status", 1);
            jsonBody.put("ErrorCode", 30000);
            jsonBody.put("MachineStatus", 0);
            jsonBody.put("CardStatus", 0);
            jsonBody.put("CardStatusTxt", "");
            jsonBody.put("TaskStatus", 1);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uuid", Uuid);
            jsonObject.put("Type", 5);
            jsonObject.put("Time", "15887789789");
            jsonObject.put("Body", jsonBody);

            String Msg = jsonObject.toString();
            mqttManager.publish(topicForSend, Msg, 2);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GetRemitBalance(){
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ClientTime", getTimeMillis());
            jsonBody.put("RemitCard", "6217000360005741832");
            jsonBody.put("Status", 1);
            jsonBody.put("ErrorReason", "数据正确");
            jsonBody.put("MachineStatus", 0);
            jsonBody.put("CardStatus", 0);
            jsonBody.put("CardStatusTxt", "只收不付");
            jsonBody.put("TaskStatus", 1);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uuid", Uuid);
            jsonObject.put("Type", 6);
            jsonObject.put("Time", getTimeMillis());
            jsonObject.put("Body", jsonBody);

            String Msg = jsonObject.toString();
            mqttManager.publish(topicForSend, Msg, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void UpdateRemitBalance(){
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ClientTime", "15887790789");
            jsonBody.put("RemitCard", "6217000360005741832");
            jsonBody.put("Balance", 4200.00);
            jsonBody.put("MachineStatus", 0);
            jsonBody.put("CardStatus", 0);
            jsonBody.put("CardStatusTxt", "只收不付");
            jsonBody.put("TaskStatus", 1);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uuid", Uuid);
            jsonObject.put("Type", 7);
            jsonObject.put("Time", "15887789789");
            jsonObject.put("Body", jsonBody);

            String Msg = jsonObject.toString();
            mqttManager.publish(topicForSend, Msg, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void UpdateMachineStatus(){
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("Status", 1);
            jsonBody.put("ErrorReason", "处理成功");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uuid", Uuid);
            jsonObject.put("Type", 9);
            jsonObject.put("Time", "15887789789");
            jsonObject.put("Body", jsonBody);

            String Msg = jsonObject.toString();
            mqttManager.publish(topicForSend, Msg, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void UpdateLastOrderStatus(){
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ClientTime", getTimeMillis());
            jsonBody.put("OrderNo", lastOrderNo);
            jsonBody.put("OrderStatus", 1);
            jsonBody.put("OrderFailedReason", "Bankcard");
            jsonBody.put("TransferImg", "base64 dataxxxx==");
            jsonBody.put("Balance", 4200.00);
            jsonBody.put("MachineStatus", 0);
            jsonBody.put("CardStatus", 1);
            jsonBody.put("CardStatusTxt", "只收不付");
            jsonBody.put("TaskStatus", 1);
            jsonBody.put("ErrorCode", 0);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uuid", Uuid);
            jsonObject.put("Type", 11);
            jsonObject.put("Time", getTimeMillis());
            jsonObject.put("Body", jsonBody);

            String Msg = jsonObject.toString();
            mqttManager.publish(topicForSend, Msg, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void RetBankTransfers(String uuid,String strAmount){
        try {

            //给平发
//            BankTransfersData bankTransfersData = new BankTransfersData(2);
//
//            Map<String,Object> map = new HashMap<>();
//
//            JSONObject jsonByMap = bankTransfersData.createJsonByMap(map);
//
//            serverfinally.send(jsonByMap.toString());

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ClientTime", String.valueOf(getTimeMillis()));
            jsonBody.put("Status", 1);
            jsonBody.put("ErrorReason", "数据正确");
            jsonBody.put("ErrorCode", 0);
            jsonBody.put("Balance", strAmount);
            jsonBody.put("MachineStatus", 0);
            jsonBody.put("CardStatus", 1);
            jsonBody.put("CardStatusTxt", "只收不付");
            jsonBody.put("TaskStatus", 1);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uuid", uuid);
            jsonObject.put("Type", 2);
            jsonObject.put("Time", String.valueOf(getTimeMillis()));
            jsonObject.put("Body", jsonBody);

            String Msg = jsonObject.toString();
            mqttManager.publish(topicForSend, Msg, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean parseMsg(String msg){

        return true;
    }



    //此处从web后台获取字段
    private class MsgHandle implements MqttMsgArrivedCallback{
        @Override
        public void messageArrived(String msg) {
            Log.i("GuardProcess", "received msg : " + msg);

            try {
                JSONObject jsonObject = new JSONObject(msg);
                int Type = jsonObject.getInt("Type");
                Uuid = jsonObject.getString("Uuid");
                if (Type == 2){

                    //添加原始任务
                    synchronized (BankTransfersDataList){
                        BankTransfersDataList.add(msg);
                    }

                    JSONObject body = jsonObject.getJSONObject("Body");
                    double balance = body.getDouble("Balance");
                    RetBankTransfers(Uuid,   String.valueOf(balance));
                    serverfinally.send(msg);

                }
                else if (Type == 3){

                    UpdateTransferResultData updateTransferResultData = new UpdateTransferResultData();
                    UpdateTransferResultRetRetData updateTransferResultRetRetData = new UpdateTransferResultRetRetData();

                }
                else if (Type == 4){
                    UpdateBillData();

                    GetBillDataData getBillDataData = new GetBillDataData();

                    GetBillDataRetRetData getBillDataRetRetData = new GetBillDataRetRetData();

                }
                else if (Type == 5){

                    UpdateBillDataData updateBillDataData = new UpdateBillDataData();

                    UpdateBillDataRetRetData updateBillDataRetRetData = new UpdateBillDataRetRetData();

                }
                else if (Type == 6){
//                    GetRemitBalance();
//                    Thread.sleep(5000);
//                    UpdateRemitBalance();
//
//                    GetRemitBalanceData getRemitBalanceData = new GetRemitBalanceData();
//                    GetRemitBalanceRetData getRemitBalanceRetData = new GetRemitBalanceRetData();


                //    serverfinally.send(msg);


                }
                else if (Type == 7){

                    UpdateRemitBalanceData updateRemitBalanceData = new UpdateRemitBalanceData();

                    UpdateRemitBalanceRetData updateRemitBalanceRetData = new UpdateRemitBalanceRetData();


                }
                else if (Type == 8){
                    UpdateMachineStatus();

                    GetMachineStatusData getMachineStatusData = new GetMachineStatusData();
                }
                else if (Type == 9){
                    UpdateMachineStatusData updateMachineStatusData = new UpdateMachineStatusData();

                    UpdateMachineStatusRetData updateMachineStatusRetData = new UpdateMachineStatusRetData();


                }
                else if (Type == 10){
                    UpdateLastOrderStatus();

                    GetLastOrderStatusData getLastOrderStatusData = new GetLastOrderStatusData();
                }
                else if (Type == 11) {
                    UpdateLastOrderStatusData updateLastOrderStatusData = new UpdateLastOrderStatusData();
                    UpdateLastOrderStatusRetData updateLastOrderStatusRetData = new UpdateLastOrderStatusRetData();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            parseMsg(msg);
        }
    }
}
