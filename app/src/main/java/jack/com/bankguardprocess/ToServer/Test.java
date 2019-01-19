package jack.com.bankguardprocess.ToServer;

import org.json.JSONException;
import org.json.JSONObject;

public class Test {



//    {
//“Uuid”:”123E4567-E89B-12D3-A456-556642440000”
//“Type”:1			//CreateRemitter
//“Time”:”15887789789”
//        "Body":
//        {
//            "Group":"000001", //机架编号保留6位，不足前面补0， 按机房分组0-99、100-199、200-299表示不同的机房，以此类推
//“MCode”:"0001", //保留4位，不足前面补0，机器编号1-1000，每个机架唯一
//                "RemitCard": "6217000360005741327",int"AccountName":"张三",
//                "BankName":"工商银行",
//                "BankShortName":"BBC",
//                "Balance":4000.00  //浮点数 保留2位小数 单位元主动从银行查询的余额
//            "MAC": "00:0a:95:9d:68:16",
//                "IP": "9.9.9.9",
//                "Limit": 1
//            "MachineStatus":0//机器状0代表正常，其他值代表具体错误，-1表示获取此状态失败
//            "CardStatus":0//卡状态，0代表正常，其他值代表具体错误，-1表示获取此状态失败
//            "TaskStatus":具体任务状态码，表示正在执行的具体任务，-1表示获取此状态失败
//
//
//        }
//    }



    String CreateRemitter(){


        String strJsonTask = "";
        JSONObject obj = new JSONObject();
        //先初始化为异常任务
        JSONObject obj1 = new JSONObject();
        try {

            obj.put("Uuid","");
            obj.put("Type",1);
            obj.put("Time","");

            obj1.put("Group","000001");
            obj1.put("MCode","0001");
            obj1.put("RemitCard","6217000360005741327");
            obj1.put("AccountName","张三");
            obj1.put("BankName","");
            obj1.put("BankShortName","");
            obj1.put("Balance","");
            obj1.put("MAC","");
            obj1.put("IP","");
            obj1.put("Limit","");
            obj1.put("MachineStatus","");
            obj1.put("CardStatus","");
            obj1.put("TaskStatus","");
            obj.put("Body",obj1);




            strJsonTask  = obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  strJsonTask;
    }

}
