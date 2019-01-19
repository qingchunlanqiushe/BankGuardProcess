package jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage;

import jack.com.bankguardprocess.TaskAbout.TaskProto.DataBase;

/**
 * Created by admin on 2018/12/29.
 */

public class GetMachineStatusData extends DataBase {
    /* 8 */

    public class _Body{
        public int LastOrder = 0;
        public String RemitCard = "";
    }

    @Override
    public void initBody() {

    }

    public _Body Body;
}
