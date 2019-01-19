package jack.com.bankguardprocess.TaskAbout.QTTPAbout;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//socket处理线程
public class ServiceThread implements Runnable {
    public static String TAG = "GuardProcess";
    public ServerSocket server = null;
    public static Socket clientSocket = null;

    @Override
    public void run() {

        Log.i(TAG,"ServiceThread run");
        try {
            server = new ServerSocket(8989);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(server != null)
        {
            while(true)
            {
                try{
                    clientSocket = server.accept();
                    Log.i(TAG,"clientSocket is comming...");
                } catch (IOException e) {
                    Log.i(TAG,Log.getStackTraceString(e));
                }
            }
        }
    }
}
