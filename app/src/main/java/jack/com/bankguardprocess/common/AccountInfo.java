package jack.com.bankguardprocess.common;

import android.util.Log;

import org.json.JSONObject;

import static jack.com.bankguardprocess.common.FileConfig.readPwd;

public class AccountInfo {

    static String loginPwd ="";

    static String transferPwd ="";

    static String card_no ="";

    static String accountName ="";

    static String group ="";

    static  public String getloginPwd(){

        return loginPwd;
    }

    static  public String getTransferPwd(){

        return transferPwd;
    }


    static public String getCardNo(){

        return card_no;
    }


    static public String getAccountName(){

        return  accountName;
    }

    static public String getGroup(){
        return  group;
    }


    //从配置文件中读取config
    static public  void parseConfigFile() throws Exception{

            String s = readPwd();

            JSONObject infoJSONObject = new JSONObject(s);

            loginPwd = infoJSONObject.getString("loginPwd");
          //  Log.i("GuardProcess","loginPwd:" + loginPwd);

            transferPwd = infoJSONObject.getString("transferPwd");
           // Log.i("GuardProcess","transferPwd:" + transferPwd);

            card_no = infoJSONObject.getString("card_no");
           // Log.i("GuardProcess","card_no:" + card_no);

            accountName = infoJSONObject.getString("accountName");
           // Log.i("GuardProcess","accountName"+accountName);

             group = infoJSONObject.getString("Group");

    }
}
