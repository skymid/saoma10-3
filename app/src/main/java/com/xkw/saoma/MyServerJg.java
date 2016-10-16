package com.xkw.saoma;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * @author Robot
 * @weibo http://weibo.com/feng88724
 * @date Nov 18, 2011
 */
public class MyServerJg extends Service {
    private static int num = 0;

    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.v("abc", "MyServerJg Service New Message !" + num++);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.v("abc", "MyServerJg ----------");

        JPushInterface.setDebugMode(true);//如果时正式版就改成false
        JPushInterface.init(this);

        return super.onStartCommand(intent, flags, startId);
    }
}
