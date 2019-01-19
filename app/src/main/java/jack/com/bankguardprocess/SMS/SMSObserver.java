package jack.com.bankguardprocess.SMS;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2018/12/9.
 */


public class SMSObserver extends ContentObserver {
    private Uri SMS_RAW = Uri.parse("content://sms/raw");
    private Context mContext;
    private Handler mhandler;
    private String beforverCode;

    public SMSObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mhandler = handler;
        beforverCode = "";
    }

    public void getSmsFromPhone() {
        ContentResolver cr = mContext.getContentResolver();
        String[] projection = new String[] { "message_body","address" };//"_id", "address", "person",, "date", "type
        String where = " date >  "
                + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_RAW, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToFirst()) {
            Log.i("GuardProcess","moveToFirst");
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String body = cur.getString(cur.getColumnIndex("message_body"));
            Pattern p = Pattern.compile("\\d{6}");  //"\\d{6}"
            Matcher m = p.matcher(body);
            if (m.find()) {
                body = m.group().toString();
            }else {
                return;
            }

            Log.i("GuardProcess","body is:"+body);

            if (beforverCode.equals(body)){
                return;
            }

            beforverCode = body;
//            Bundle bundle = new Bundle();
//            bundle.putString("number", number);
//            bundle.putString("body", body);
//
//            Message message = new Message();
//            message.what = 3;
//            message.setData(bundle);
//            mhandler.sendMessage(message);
            mhandler.obtainMessage(1, body).sendToTarget();

        }

         cur.close();
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.i("GuardProcess", "sms onchange...");
        super.onChange(selfChange);
        getSmsFromPhone();
    }

}
