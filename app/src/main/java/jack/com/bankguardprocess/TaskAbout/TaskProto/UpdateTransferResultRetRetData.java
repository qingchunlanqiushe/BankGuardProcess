package jack.com.bankguardprocess.TaskAbout.TaskProto;

public class UpdateTransferResultRetRetData extends DataBase {

    /* 3*/

    @Override
    public void initBody() {

    }


//    "Body":
//    {
//        "Status": 1, （1–Success， 0 –Failed）
//        "ErrorReason":"处理成功”（”该订单不是处理中或失败的订单”）
//    }

        public class _Body{
           public int Status = 0;
           public String ErrorReason = "";
        }

    public  _Body Body;

}
