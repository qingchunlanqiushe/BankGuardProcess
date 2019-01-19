package jack.com.bankguardprocess.TaskAbout.TaskProto;

public class GetBillDataRetRetData extends DataBase {

    /* 4*/


    @Override
    public void initBody() {

    }




//    "Body":
//    {
//“ClientTime”:”15887790789”  //等于服务端的时间+任务耗时
//        "Status": 1, （1–Success， 0 –Failed）
//        "ErrorReason":"数据正确”（”银行卡信息有误”，“银行简称有误”）
//        "ErrorCode": 30000
//        "MachineStatus":0 //机器状0代表正常，其他值代表具体错误，-1表示获取此状态失败
//        "CardStatus":1 //卡状态，0.正常：1.冻结：2.销卡：3.未知：，-1表示获取此状态失败
//        "CardStatusTxt":"只收不付" //从银行提取的卡片提示字符
//        "TaskStatus":具体任务状态码，表示正在执行的具体任务，-1表示获取此状态失败
//    }



    public  class _Body{
        public String ClientTime ="";
        public  int Status = 0;

        public String ErrorReason = "";

        public int ErrorCode = 0;

        public int MachineStatus = 0;

        public int CardStatus = 0;

        public String CardStatusTxt = "";

        public int TaskStatus = 0;

    }

    public  _Body Body;
}
