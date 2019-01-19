package jack.com.bankguardprocess.TaskAbout.TaskProto;

public class UpdateBillDataRetRetData extends DataBase {

    /* 5*/

    @Override
    public void initBody() {

    }
//
//    "Body":
//    {
//“ClientTime”:”15887790789”  //等于服务端的时间+任务耗时
//        "Status": 1, （1–Success， 0 –Failed）
//        "ErrorReason":"处理成功”
//    }

    public  class  _Body{
        public  String ClientTime ="";
        public  int Status =1;
        public String ErrorReason = "";
    }

    public _Body  Body;
}
