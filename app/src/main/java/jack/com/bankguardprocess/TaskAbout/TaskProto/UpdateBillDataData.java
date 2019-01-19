package jack.com.bankguardprocess.TaskAbout.TaskProto;

import java.util.ArrayList;
import java.util.List;

public class UpdateBillDataData extends DataBase {

    /* 5*/

    @Override
    public void initBody() {

    }


//    "Body":
//    {
//“ClientTime”:”15887790789”  //等于服务端的时间+任务耗时
//        "Id":"6217000450011245058",
//            "AccountName":"石六十三",
//            "UpdateTime":"2018-10-24 11:00:00",
//            "Info":[["2018-10-23","22:48:06","-980.00","14696.62","郦水金","6217001430*****2275","电子汇出","电子汇出"],["2018-10-23","23:07:05","-392.00","14304.62","纪卫东","6222033301*****1997","跨行转出","跨行转出"],["2018-10-23","23:18:33","-392.00","13912.62","于友","6230520580*****0874","跨行转出","跨行转出"],["2018-10-24","00:45:00","-1568.00","12344.62","乔永红","6217230512*****6256","跨行转出","跨行转出"],["2018-10-24","01:06:18","-588.00","11756.62","余庆华","6215581103*****2810","跨行转出","跨行转出"],["2018-10-24","01:18:21","-6414.00","5342.62","张玉凤","6217000190*****5393","电子汇出","电子汇出"],["2018-10-24","01:35:22","-500.00","4842.62","陈杨","6228482469*****1474","跨行转出","跨行转出"],["2018-10-24","01:45:17","-980.00","3862.62","杨绪义","6228483378*****9672","跨行转出","跨行转出"]],		//["交易日期","交易时间","+/-金额","余额","对方户名","对方账号","交易地点","交易方式", "摘要"] //摘要可以没有
//        "Mac":"3C-54-60-A4-70-24"
//        "Status": 1成功，0失败  如果账单是空，返回成功。跟UpdateTime一起表示直到更新时间没有银行账单。
//        "ErrorCode": 30000
//        "MachineStatus":0 //机器状0代表正常，其他值代表具体错误，-1表示获取此状态失败
//        "CardStatus":1 //卡状态，0.正常：1.冻结：2.销卡：3.未知：，-1表示获取此状态失败
//        "CardStatusTxt":"只收不付" //从银行提取的卡片提示信息
//        "TaskStatus":具体任务状态码，表示正在执行的具体任务，-1表示获取此状态失败
//    }



    public class _Body{

        public String ClientTime ="";
        public String Id ="";
        public String AccountName ="";
        public String UpdateTime ="";
        public List<List<String>>Info;
        public String Mac ="";
        public int Status = 0;
        public  int ErrorCode = 0;
        public int MachineStatus = 0;
        public int CardStatus = 0;
        public String CardStatusTxt ="";
        public int  TaskStatus = 0;

    }
    public _Body Body;

}
