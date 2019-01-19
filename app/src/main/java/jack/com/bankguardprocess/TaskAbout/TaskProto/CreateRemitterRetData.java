package jack.com.bankguardprocess.TaskAbout.TaskProto;

public class CreateRemitterRetData extends  DataBase{


    /*
    1
     */




    @Override
    public void initBody() {

    }


//"Body":
//    {
//        "Msg": "添加机器信息成功",
//            "Status": 1,or 0
//        "ErrorCode": 0
//    }


    public class _Body{

        public   String Msg = "";

        public  String Status = "";

        public  String ErrorCode = "";

    }


    public _Body Body;
}
