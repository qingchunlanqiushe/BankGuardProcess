package jack.com.bankguardprocess.TaskAbout.PHPAbout;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import jack.com.bankguardprocess.common.MyLog;

import static java.net.HttpURLConnection.HTTP_SEE_OTHER;


/**
 * Created by Administrator on 2018/7/16 0016.
 */

public class MyUrlPost {

    public static final byte[] input2byte(InputStream inStream)
  throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] b = baos.toByteArray();
        inStream.close();
        baos.close();
        return b;

    }

    private int responseCode;


    static  public Pair<Integer,String> httpPost(final String Strurl, final String data) throws Exception {

//        Crypto crypto = new Crypto();
//        final byte[] endata = crypto.encryptRSA(data.getBytes());
//        if (endata == null){
//            return "Error";
//        }


      //  MyLog.i("GuardProcess","Send data is:"+data);

        Pair<Integer,String> pair =null;

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(Strurl);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("token", " 2F^naY#tOtH2=W2ZpYAxrL&lA5!b31E!n0VXKQxgYHLZW%do8Q^Oz1jJuAzELZUG");


            OutputStream outputStream = conn.getOutputStream();

            out = new PrintWriter(new OutputStreamWriter(outputStream,"utf-8"));
            // 发送请求参数
            out.print(data);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应

            HttpURLConnection httpconn = (HttpURLConnection)conn;

            int responcode = httpconn.getResponseCode();
           // Log.i("GuardProcess",""+responcode);

//            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            if ( responcode== HttpURLConnection.HTTP_OK || responcode == HttpURLConnection.HTTP_MULT_CHOICE  || responcode == HTTP_SEE_OTHER){
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{

                in = new BufferedReader(new InputStreamReader(((HttpURLConnection) conn).getErrorStream()));
            }


            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            pair  = new Pair<>(httpconn.getResponseCode(),result);

        } catch (Exception e) {
            Log.i("GuardProcess","发送 POST 请求出现异常！"+e.toString());
           // e.printStackTrace();

            pair  = new Pair<>(0,"Error");
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }

      //  MyLog.i("GuardProcess","post返回结果: "+result);

      //  System.out.println("post推送结果："+result);
        return pair;
    }

}
