package com.xkw.saoma;

import android.app.Application;

/**
 * Created by Administrator on 2016/10/6.
 */

public class MyData extends Application {

        public  String userid;

        public  String getUserID() {
            return userid;
        }

        public void setUserID(String userid) {
            this.userid = userid;
        }

}
