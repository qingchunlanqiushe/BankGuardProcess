package jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage;

import jack.com.bankguardprocess.TaskAbout.TaskProto.DataBase;

/**
 * Created by admin on 2018/12/29.
 */

public class UpdateLastOrderStatusData extends DataBase {
    /* 11 */

    public class _Body{
        public String ClientTime = "";
        public String OrderNo = "";
        public int OrderStatus = 0;
        public String OrderFailedReason = "";
        public String TransferImg = "";
        public double Balance = 0.00;
        public int MachineStatus = 0;
        public int CardStatus = 0;
        public String CardStatusTxt = "";
        public String TaskStatus = "";
        public int ErrorCode = 0;
    }

    @Override
    public void initBody() {

    }
    public _Body Body;
}
