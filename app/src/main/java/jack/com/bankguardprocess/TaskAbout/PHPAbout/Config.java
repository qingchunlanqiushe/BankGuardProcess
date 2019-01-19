package jack.com.bankguardprocess.TaskAbout.PHPAbout;

/**
 * Created by LittleFish on 2018/4/12.
 */

public class Config {
    public static final String HOSTNAME = "http://61.196.186.165/api";

    public static final String GETTASK = "GetPendingWebBankTransfers";

    public static final String RETURNTASK = "UpdateWebBankTransferStatus";

    public static  final  String RETUENBALANCE = "updateWebRemitBalance";

    public static  final  String CREATEWEBREMIT = "createWebRemit";

    public static  final  String RECORDCMBCWEBTRANSFERINFO = "RecordCMBCWebTransferInfo";

    public static  final  String GETAPPVERSIONSFILE = "GetAppVersionsFile";

    public  static String getFullGetTaskUrl(){
        return   HOSTNAME +"/" + GETTASK;
    }


    public  static String getFullReturnTaskUrl(){
        return   HOSTNAME +"/" + RETURNTASK;
    }


    public static  String getFullReturnBalanceUrl(){
        return   HOSTNAME +"/" + RETUENBALANCE;
    }


    public static  String getFullCreateWebRemit(){
        return  HOSTNAME + "/" + CREATEWEBREMIT;
    }

    public static String getRecordCMBCWebTransferInfo(){
        return   HOSTNAME + "/" + RECORDCMBCWEBTRANSFERINFO;
    }

    public static String getAppVersionsFile(){
        return HOSTNAME + "/" + GETAPPVERSIONSFILE;
    }


}


//http://61.196.186.165/api/GetPendingAlipayTransfers   请求任务
//
//        http://61.196.186.165/api/UpdateAlipayTransferStatus  回复任务