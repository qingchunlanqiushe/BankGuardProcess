package jack.com.bankguardprocess.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import static android.content.ContentValues.TAG;

public class Tool {

   public static Handler  mhandler = null;


    public static String getMacAddress() {
        String strMacAddr = null;
        try {
            // 获得IpD地址
            InetAddress ip = getLocalInetAddress();

            Log.i("GuardProcess","ip is:"+ip.toString());

            String ip2 = getLocalIpAddress();

            Log.i("GuardProcess","ip2 is:"+ip2.toString());


            byte[] b = NetworkInterface.getByInetAddress(ip)
                    .getHardwareAddress();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (Exception e) {
        }
        return strMacAddr;
    }
    /**
     * 获取移动设备本地IP
     *
     * @return
     */
    private static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            // 列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface
                    .getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {// 是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface
                        .nextElement();// 得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();// 得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

    /**
     * 获取本地IP
     *
     * @return
     */
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getNetIp() {
        String ip = "";
        InputStream inStream = null;
        try {
            URL infoUrl = new URL("http://2018.ip138.com/ic.asp");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "gb2312"));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    //builder.append(line).append("\n");
                }
                inStream.close();
                int start = builder.indexOf("[");
                int end = builder.indexOf("]");
                ip = builder.substring(start + 1, end);
                return ip;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static  void sendIPToUI(){


        new Thread(new Runnable() {
            @Override
            public void run() {
                String netIp = getNetIp();
                Message msg = mhandler.obtainMessage(2,netIp);
                mhandler.sendMessage(msg);
            }
        }).start();

    }

}
