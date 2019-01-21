package jack.com.bankguardprocess.common;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by admin on 2018/5/26.
 */

public class SocketClient {
    private String result = "";
    public Socket socket;


    String TAG  = "GuardProcess";



    public SocketClient(){

    }

    public void CloseSocket(){
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean Connect(){

        try {
            socket = new Socket("localhost", 8989);
        }catch (IOException e){
           // Log.i("123", e.getMessage());
            return false;
        }

        return true;
    }

    public void SendMsg(final String msg){

        try {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    PrintWriter pout = null;
                    try {
                        pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
                        pout.println(msg);
                    } catch (IOException e) {
                        Log.i(TAG, Log.getStackTraceString(e));
                    }
                }

            });

            thread.start();
            thread.join();

        } catch (InterruptedException e) {
            Log.i(TAG,Log.getStackTraceString(e));
        }

    }

    public String RecvMsg(){

        try {

            Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = in.readLine();
                    if (msg != null){
                        result = msg;
                    }
                } catch (IOException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }

        });
            thread.start();
            thread.join();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String strResult = result;
        result = "";

        return  strResult;
    }


    public int ByteArrayToint(byte[] blen)
    {
        int nlen = (blen[0] & 0xFF) + (blen[1] & 0xFF) * 0x100 +  (blen[2] & 0xFF) * 0x10000 + (blen[3] & 0xFF) * 0x1000000;
        return nlen;
    }


    public byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[3] = (byte)((i >> 24) & 0xFF);
        result[2] = (byte)((i >> 16) & 0xFF);
        result[1] = (byte)((i >> 8) & 0xFF);
        result[0] = (byte)(i & 0xFF);
        return result;
    }

    public boolean sendRawMsg(String strTask){
        try
        {
            OutputStream os = socket.getOutputStream();
            byte[] byteArray = Base64.encode(strTask.getBytes(), Base64.NO_PADDING | Base64.NO_WRAP);
            os.write(intToByteArray(byteArray.length));
            os.write(byteArray);
        }
        catch (Exception e)
        {
            Log.i(TAG,Log.getStackTraceString(e));
            return false;
        }
        return  true;
    }

    public String recvRawMsg(){

        try {
            byte[] readLength = new byte[4];
            InputStream in = socket.getInputStream();


            String strResult = "";

            int nRet = in.read(readLength);
            if (nRet == -1)
            {
                return "";
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
            byte[] byteArray = Base64.decode(readbuf, Base64.NO_PADDING | Base64.NO_WRAP);
            strResult = new String(byteArray);

          //  Log.i(TAG,"收到结果： " + strResult);

            return  strResult;

        }
        catch (Exception e){
                Log.i(TAG,Log.getStackTraceString(e));
        }


        return  "";
    }




}
