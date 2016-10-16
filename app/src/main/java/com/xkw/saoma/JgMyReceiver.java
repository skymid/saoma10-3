package com.xkw.saoma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/10/4.
 */

public class JgMyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)){
            Bundle bundle=intent.getExtras();
            String title=bundle.getString(JPushInterface.EXTRA_TITLE);
            String message=bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Toast.makeText(context,"titles:"+title+"     content:"+message,Toast.LENGTH_LONG).show();
//            Dialog alertDialog = new AlertDialog.Builder(context).
//                    setTitle(title).
//                    setMessage(message).
//                    setIcon(R.drawable.easy).
//                    create();
//            alertDialog.show();
        }
    }
}
