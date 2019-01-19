package jack.com.bankguardprocess.TaskAbout.TaskProto.AfterFivePackage;

import jack.com.bankguardprocess.TaskAbout.TaskProto.DataBase;

/**
 * Created by admin on 2018/12/29.
 */

public class GetRemitBalanceData extends DataBase {
    /* 6 */

    public class _Body{
        public String RemitCard = "";
    }

    @Override
    public void initBody() {

    }

    public _Body Body;
}
