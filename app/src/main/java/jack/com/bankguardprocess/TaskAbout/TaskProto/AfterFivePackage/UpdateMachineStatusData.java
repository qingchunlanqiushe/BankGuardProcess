package jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage;

import jack.com.bankguardprocess.TaskAbout.TaskProto.DataBase;

/**
 * Created by admin on 2018/12/29.
 */

public class UpdateMachineStatusData extends DataBase {
    /* 9 */

    public class _Body{
        public int RRT = 0;
        public String ClientTime = "";
        public int MachineStatus = 0;
        public int CardStatus = 0;
        public String CardStatusTxt = "";
        public int TaskStatus = 0;
        public int ErrorCode = 0;
    }

    @Override
    public void initBody() {

    }

    public _Body Body;
}
