package jack.com.bankguardprocess.TaskAbout.TaskProto;

import java.util.ArrayList;
import java.util.List;

public class BankTransfersData extends DataBase {
    /* 2*/

//    {
//        "Uuid": "123E4567-E89B-12D3-A456-556642440000",
//            "Type": 2,
//            "Time": "15887789789",
//            "Body": {
//        "OrderList": [{
//            "BankCardNo": "6217000360006309100",
//                    "AccountName": "柳波",
//                    "BankShortName": "CCB",
//                    "OpenAccountBranch": "中国建设银行",
//                    "OrderNo": "AA0980209617594225",
//                    "TransferAmount": 1,
//                    "CurrentBalance": 1,
//                    "ProvKeyName ": "山西省",
//                    "CityKeyName ": "太原市",
//                    "NetKeyName": "太原"
//        }]
//    }
//    }


    public class _OrderList{

        public String BankCardNo;
        public String AccountName;

        public String BankShortName;
        public String OpenAccountBranch;
        public String OrderNo;

        public float TransferAmount;
        public float CurrentBalance;

        public String ProvKeyName;
        public String CityKeyName;
        public String NetKeyName;
    }

    public  class _Body{
        public List<_OrderList> OrderList;
    }


    @Override
    public void initBody() {

    }



}
