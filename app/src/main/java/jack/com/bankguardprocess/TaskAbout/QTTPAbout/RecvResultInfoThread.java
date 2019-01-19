package jack.com.bankguardprocess.TaskAbout.QTTPAbout;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

//接收返回结果线程
public class RecvResultInfoThread implements Runnable
{
    public static String TAG = "GuardProcess";

    @Override
    public void run()
    {
        Log.i(TAG,"RecvResultInfoThread run");

        try
        {
            if ((ServiceThread.clientSocket) != null)
            {
                byte[] readLength = new byte[4];
                InputStream in = ServiceThread.clientSocket.getInputStream();

                while(true)
                {
                    int nRet = in.read(readLength);
                    if (nRet == -1)
                    {
                        return;
                    }

                    int nLength = ByteArrayToint(readLength);
                    byte[] readbuf = new byte[nLength + 1];
                    readbuf[nLength] = '\0';

                    int offset = 0;//定义偏移量
                    while(offset < nLength)
                    {
                        int recv = in.read(readbuf, offset, nLength - offset);
                        offset += recv;
                    }

                    //取得结果
                  //  byte[] byteArray = Base64.decode(readbuf, Base64.NO_PADDING | Base64.NO_WRAP);
                    String strResult = new String(readbuf);

                    Log.i(TAG,"收到结果： " + strResult);

                  //发送
                    RecvTaskThread.publishMessage(strResult);
                  //  RecvTaskThread.ParseUrlHttp(RecvTaskThread.strUpdateUrl,strResult);

                    TaskHandleThread.IsDoTasking = false;
                    return;
                }
            }
        }catch (IOException e)
        {
            TaskHandleThread.IsDoTasking = false;
            Log.i(TAG,Log.getStackTraceString(e));
        }
    }

    public int ByteArrayToint(byte[] blen)
    {
        int nlen = (blen[0] & 0xFF) + (blen[1] & 0xFF) * 0x100 +  (blen[2] & 0xFF) * 0x10000 + (blen[3] & 0xFF) * 0x1000000;
        return nlen;
    }
}
