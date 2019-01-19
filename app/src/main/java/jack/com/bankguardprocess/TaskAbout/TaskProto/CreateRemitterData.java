package jack.com.bankguardprocess.TaskAbout.TaskProto;

public class CreateRemitterData extends  DataBase{

    /*
    1
     */

    @Override
    public void initBody() {

    }


    public class _Body{

        public String Group = "";

        public String MCode = "";

        public String RemitCard = "";
        public String AccountName = "";
        public String BankName = "";

        public String BankShortName = "";

        public  String Balance = "";

        public String MAC = "";

        public String IP = "";
        public String Limit = "";

        public int MachineStatus = 0;
        public int CardStatus = 0;
        public int TaskStatus = 0;

        public int ChannelType = 2;
    }

    public _Body Body = new _Body();

}
