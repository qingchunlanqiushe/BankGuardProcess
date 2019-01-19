package jack.com.bankguardprocess.TaskAbout.TaskProto;

public class BankTransfersRetData extends DataBase {

    /* 2*/


    @Override
    public void initBody() {

    }


    public  class  _Body{
        public String ClientTime;
        public  int Status;
        public String ErrorReason;

        public String ErrorCode;
        public double Balance;

        public int MachineStatus;
        public int CardStatus;

        public  String CardStatusTxt;
        public int TaskStatus;
    }

    public  _Body Body;


}
