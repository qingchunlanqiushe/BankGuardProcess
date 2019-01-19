package jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage;

import jack.com.bankguardprocess.TaskAbout.TaskProto.DataBase;

/**
 * Created by admin on 2018/12/29.
 */

public class UpdateMachineStatusRetData extends DataBase {
    /* 9 */

    public class _Body{
        public int Status = 0;
        public String ErrorReason = "";
    }

    @Override
    public void initBody() {

    }
    public  _Body Body;
}
