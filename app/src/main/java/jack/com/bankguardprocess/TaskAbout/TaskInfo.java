package jack.com.bankguardprocess.TaskAbout;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static jack.com.bankguardprocess.MainActivity.strMac;
import static jack.com.bankguardprocess.common.AccountInfo.getAccountName;
import static jack.com.bankguardprocess.common.AccountInfo.getCardNo;

public class TaskInfo {




    public String order_no;
    public String type;
    public String account_name;
    public String account_no;
    public String bank_name;
    public String amount;

    TaskInfo() {
        order_no = "";
        type = "";
        account_name = "";
        account_no = "";
        bank_name = "";
        amount = "";
    }



    //创建转账任务
    public static String createPayTask(String strPaidAmount, String strPayeeUsername,String strFullName,String strBank){

        //  final String strTask = "{\"task\":{\"id\":\"1\",\"type\":\"alipay\",\"name\":\"杨志军\",\"account\":\"15135966587\",\"bank\":\"\",\"amount\":\"0.01\"}}";

        String strJsonTask = "";
        JSONObject obj = new JSONObject();
        //先初始化为异常任务
        JSONObject obj1 = new JSONObject();
        try {

            obj1.put("type","bank");

            obj1.put("account",strPayeeUsername);

            obj1.put("amount",strPaidAmount);

            obj1.put("name",strFullName);

            obj1.put("id","");

            obj1.put("bank",strBank);

            obj.put("task",obj1);

            strJsonTask  = obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  strJsonTask;
    }

//{"task":{"opt_card":"6226220636262437","type":4}}  获取流水
//{"task":{"opt_card":"6226220636262437","type":3}}  获取余额


    public static  String createGetBalanceTask(){


        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("opt_card",getCardNo());
            jsonObject1.put("type",3);

            jsonObject.put("task",jsonObject1);

            return jsonObject.toString();
        }
        catch (Exception e){


        }

        return  "";

    //    return  "getBalance";
    }

    public static  String craeteJiaoYiMingXiTask(){

        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("opt_card",getCardNo());
            jsonObject1.put("type",4);

            jsonObject.put("task",jsonObject1);

            return jsonObject.toString();
        }
        catch (Exception e){


        }
        return  "";

       // return  "getJiaoYiMingXi";
    }


    static  public TaskInfo ParseTaskInfo(String strTask){

        TaskInfo taskinfo = new TaskInfo();
        TaskInfo taskinfoTmp = new TaskInfo();
        try {
            JSONObject jsonObjectTask = new JSONObject(strTask).getJSONObject("task");
            taskinfoTmp.order_no = jsonObjectTask.getString("order_no");
            taskinfoTmp.type = jsonObjectTask.getString("type");
            taskinfoTmp.account_name = jsonObjectTask.getString("account_name");
            taskinfoTmp.account_no = jsonObjectTask.getString("account_no");
            taskinfoTmp.bank_name = jsonObjectTask.getString("bank_name");
            taskinfoTmp.amount = jsonObjectTask.getString("amount");
            taskinfo = taskinfoTmp;

        } catch (JSONException e) {
            Log.i("GuardProcess", Log.getStackTraceString(e));
        }

        return taskinfo;
    }



    static public String parseReturnTask(String str){

        try {
            JSONObject obj = new JSONObject(str);

            String status = obj.getString("status");

            return  status;
        }
        catch (Exception e){

            Log.i("GuardProcess",   Log.getStackTraceString(e));
        }
        return "0";
    }


    //创建转账任务
    public static String createTransTaskResult(TaskInfo taskinfo, String nTaskResult, String failedReason, String Base64Bmp){

        String strJsonTask = "";

        //先初始化为异常任务
        JSONObject obj1 = new JSONObject();
        try {

//            {
//                "order_no": "NB20180716173215232162322033590",
//                    "status":"1",
//                    "failed_reason":"",
//            }

            obj1.put("order_no",taskinfo.order_no);
            obj1.put("status",nTaskResult);
            obj1.put("failed_reason",failedReason);
            obj1.put("img",Base64Bmp);

            strJsonTask  = obj1.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  strJsonTask;
    }

    public  static String readyGetTaskData(){

        try {
            JSONObject object=new JSONObject();
            object.put("mac", strMac);
            object.put("RemitCard",   getCardNo());


            return  object.toString();
        }
        catch (Exception e){

            Log.i("GuardProcess",e.toString());
        }
        return  "";

    }


//    {
//        "BankShortName":"SPDB",
//            "AccountName":"刘爽",
//            "Balance":"10.00",
//            "BankName:":"浦发银行",
//            "Group":"浦发测试",
//            "RemitCard":"6217921463459324"
//    }
//



    public static String readyCreateRemitData(String curBalance){

        try {
            JSONObject object=new JSONObject();
            object.put("BankShortName", "CMBC");
            object.put("AccountName", getAccountName());
            object.put("Balance",curBalance);
            object.put("BankName","民生银行");
            object.put("Group","民生测试");
            object.put("RemitCard",getCardNo());

            return  object.toString();
        }
        catch (Exception e){


        }

        return  "";
    }




}
