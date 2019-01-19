package jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage;

import jack.com.bankguardprocess.TaskAbout.TaskProto.DataBase;

/**
 * Created by admin on 2018/12/29.
 */

public class UpdateRemitBalanceRetData extends DataBase {
    /* 7 */

    public class _Body{
        public String Status = "";
        public String ErrorReason = "";
    }

    @Override
    public void initBody() {

    }

    public  _Body Body;
}
