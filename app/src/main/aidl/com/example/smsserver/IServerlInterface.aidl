// IServerlInterface.aidl
package com.example.smsserver;

// Declare any non-default types here with import statements

interface IServerlInterface {
    String getSms(String strCode,int nTimeout);
    boolean setSms(String strPhoneNumber,String strMsg);
}
