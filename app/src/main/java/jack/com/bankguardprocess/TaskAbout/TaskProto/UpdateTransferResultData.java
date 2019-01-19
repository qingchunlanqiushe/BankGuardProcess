package jack.com.bankguardprocess.TaskAbout.TaskProto;

public class UpdateTransferResultData extends DataBase {

    /* 3*/

    @Override
    public void initBody() {

    }


//"Body":
//    {
//“ClientTime”:”15887790789”  //等于服务端的时间+任务耗时
//        "OrderNo": "AA0980209617594225",
//            "OrderStatus":1, （1–Success， 0 –Failed,   2 - 未知(网银操作结束，仍不能确定结果),  3-处理中(正在执行网银操作) 4-已创建）
//        "OrderFailedReason": "Bank card not verifid"
//        "TransferImg":"截图base64数据"
//        "Balance":4200.00  //浮点数 保留2位小数 单位元，大于等于0表示正常余额, -1表示获取余额失败
//        "MachineStatus":0 //机器状0代表正常，其他值代表具体错误，-1表示获取此状态失败
//        "CardStatus":1 //卡状态，0.正常：1.冻结：2.销卡：3.未知：，-1表示获取此状态失败
//        "CardStatusTxt":"只收不付" //从银行提取的卡片提示字符
//        "TaskStatus":具体任务状态码，表示正在执行的具体任务，-1表示获取此状态失败
//
//        "ErrorCode": 0
//    }



    public class _Body{
        public  String ClientTime = "";
        public String OrderNo ="";

        public int OrderStatus  = 0;

        public String OrderFailedReason = "";

        public String TransferImg ="";

        public float Balance = 0.0f;

        public int MachineStatus = 0;

        public int CardStatus = 0;

        public String CardStatusTxt ="";

        public int TaskStatus = -1;

        public int ErrorCode = 0;
    }

    public  _Body Body;

}
