package jack.com.bankguardprocess.common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.acl.LastOwnerException;


public class FileConfig {

    //读取账号以及密码
    public static String readPwd() throws Exception
    {

            File file = new File("/sdcard/config.txt");
            if (!file.exists()) {
               return "";
            } else
            {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String info = "";
                String infoTmp = "";
                while ((infoTmp = bufferedReader.readLine()) != null)
                {
                    info = info + infoTmp;
                }
                return info;
            }


    }

    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {

            if (dirFile.getName().contains("config.txt") == false){
                Log.i("GuardProcess","file is："+dirFile.getName());
                return dirFile.delete();
            }
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }

        return true;
    }


    public static boolean deletefile(String delpath) {
        try {


            Log.i("GuardProcess","delete all file");
            File file = new File(delpath);

            deleteFile(file);

//            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
//            if (!file.isDirectory()) {
//                file.delete();
//            } else if (file.isDirectory()) {
//                String[] filelist = file.list();
//                for (int i = 0; i < filelist.length; i++) {
//                    File delfile = new File(delpath + "\\" + filelist[i]);
//                    if (!delfile.isDirectory()) {
//                        delfile.delete();
//                        Log.i("GuardProcess",delfile.getAbsolutePath() + "删除文件成功");
//                    } else if (delfile.isDirectory()) {
//                        deletefile(delpath + "\\" + filelist[i]);
//                    }
//                }
//                Log.i("GuardProcess",file.getAbsolutePath() + "删除成功");
//
//                file.delete();
//            }

        } catch (Exception e) {

            Log.i("GuardProcess",Log.getStackTraceString(e));
        }
        return true;
    }
}
