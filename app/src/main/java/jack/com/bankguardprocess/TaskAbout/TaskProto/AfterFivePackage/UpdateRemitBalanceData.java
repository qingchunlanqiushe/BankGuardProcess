package jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage;

import jack.com.bankguardprocess.TaskAbout.TaskProto.DataBase;

/**
 * Created by admin on 2018/12/29.
 */

public class UpdateRemitBalanceData extends DataBase {
    /* 7 */

    public class _Body{
        public String ClientTime = "";
        public String RemitCard = "";
        public String Balance = "";
        public String MachineStatus = "";
        public String CardStatus = "";
        public String CardStatusTxt = "";
        public String TaskStatus = "";
    }

    @Override
    public void initBody() {

    }

    public  _Body Body;
}
