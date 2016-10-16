package com.xkw.saoma;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import cn.jpush.android.api.JPushInterface;

public class QRCodeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Intent serviceIntentJg;
    private final static String TAG = "QRCodeActivity";
    private XWalkView mXwalkView;
   // private WebView webview;
    ProgressDialog progressBar = null;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int REQUEST_CODE_IMAGE_CAPTURE = 4;
    private final static int REQUEST_CODE_PICK_IMAGE = 3;
    private final static int FILECHOOSER_RESULTCODE = 2;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private final static String url = "http://112.116.119.47:81/android/index.php";
    private final static String QRcode_URL = "http://112.116.119.47:81/android/qrcode.php";
    private final static String url_jg="http://www.jsdwzx.net:81/android/jpush/index.php";
    private String str_QRcode,userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //竖屏
        //setTheme(android.R.style.Theme_Black);
        setContentView(R.layout.activity_qrcode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//                Intent intent = new Intent();
//                intent.setClass(QRCodeActivity.this, MipcaActivityCapture.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
//            }
//        });
        //极光推送后台服务
        serviceIntentJg = new Intent(QRCodeActivity.this, MyServerJg.class);
        startService(serviceIntentJg);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //获取登录处 传来的userID
        Bundle bundle = this.getIntent().getExtras();
        userID = bundle.getString("userID");

        mXwalkView = (XWalkView)findViewById(R.id.my_webview);
        //XWalkSettings webSettings = mXwalkView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        mXwalkView.getSettings().setJavaScriptEnabled(true);
        //mXwalkView.addJavascriptInterface(new JsInterface(), "baobao");  // 添加一个对象, 让JS可以访问该对象的方法, 该对象中可以调用JS中的方法
        mXwalkView.addJavascriptInterface(new AndroidJavaScript(), "NativeInterface");

        mXwalkView.setDrawingCacheEnabled(false);//不使用缓存
        //mXwalkView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mXwalkView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //取消滚动条白边效果
        mXwalkView.getSettings().setSupportZoom(true);//支持屏幕缩放
        mXwalkView.getSettings().setBuiltInZoomControls(true);
        //开启调式,支持谷歌浏览器调式
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        //设置滑动样式。。。
        mXwalkView.setHorizontalScrollBarEnabled(false);
        mXwalkView.setVerticalScrollBarEnabled(false);
        mXwalkView.setScrollBarStyle(XWalkView.SCROLLBARS_OUTSIDE_INSET);
        mXwalkView.setScrollbarFadingEnabled(true);
        //支持空间导航
        mXwalkView.getSettings().setSupportSpatialNavigation(true);
        mXwalkView.getSettings().setBuiltInZoomControls(true);
        mXwalkView.getSettings().setSupportZoom(true);

        mXwalkView.load(url, null);

        mXwalkView.setResourceClient(new XWalkResourceClient(mXwalkView){
            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
            }
            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
            }
        });
        mXwalkView.setResourceClient(new XWalkResourceClient(mXwalkView){
            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
            }
            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
            }
        });
        mXwalkView.setUIClient(new XWalkUIClient(mXwalkView){
            public void onPageLoadStarted(XWalkView view,String url){
                if (progressBar == null) {
                    progressBar=new ProgressDialog(QRCodeActivity.this);
                    progressBar.setMessage("数据加载中，请稍后。。。");
                    progressBar.show();
                    mXwalkView.setEnabled(false);// 当加载网页的时候将网页进行隐藏
                    Log.v("abc", "onPageLoadStarted");
                }
            }
            public void  onPageLoadStopped(XWalkView view,String url,XWalkUIClient.LoadStatus status){
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                    progressBar = null;
                    mXwalkView.setEnabled(true);
                    Log.v("abc", "onPageLoadStopped");
                    mXwalkView.load("javascript:dip_usrID('"+userID+"')",null);
                    TextView txtuserid = (TextView) findViewById(R.id.txt_userid);
                    txtuserid.setText("Hi: " + userID);
                }
            }
        });

        ////////////////////////////////
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    //显示扫描到的内容
                    Bundle bundle = data.getExtras();
                    str_QRcode=bundle.getString("result");
                    //把结果传给js
                    mXwalkView.load("javascript:dip_QRcode('"+str_QRcode+"','"+userID+"')",null);
                    mXwalkView.load("javascript:takeTab(4)",null);   //打开QRCode选项卡5
                }
                break;
            case FILECHOOSER_RESULTCODE:
                if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data);
                }
                else  if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
                break;
            case REQUEST_CODE_PICK_IMAGE: {

            }
            break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) { } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXwalkView != null) {
            mXwalkView.pauseTimers();
            mXwalkView.onHide();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mXwalkView != null) {
            mXwalkView.resumeTimers();
            mXwalkView.onShow();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXwalkView != null) {
            mXwalkView.onDestroy();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        if (mXwalkView != null) {
            mXwalkView.onNewIntent(intent);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mXwalkView.getNavigationHistory().canGoBack()) {
                mXwalkView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD, 1);//返回上一页面
            } else {
            /*finish();*/
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qrcode, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }if (id == R.id.action_back) {
            Intent intent = new Intent();
            intent.setClass(QRCodeActivity.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
//            Intent intent = new Intent();
//            intent.setClass(QRCodeActivity.this, pic_cute.class);
//            QRCodeActivity.this.startActivity(intent);
            Cermae("Camera");
        } else if (id == R.id.nav_gallery) {
            Cermae("Album");
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent();
            intent.setClass(QRCodeActivity.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
        } else if (id == R.id.nav_manage) {
            mXwalkView.load("javascript:takeTab(4)",null);
        } else if (id == R.id.nav_main) {
            mXwalkView.load(url,null);
        } else if (id == R.id.nav_send) {
            mXwalkView.load(url_jg,null);
        }else if (id == R.id.nav_send_bm) {
            final EditText et = new EditText(this);
            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle("设置推送别名").
                    setIcon(R.drawable.easy).
                    setView(et).
                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            if (input.equals("")) {
                                Toast.makeText(getApplicationContext(), "别名不能为空！" + input, Toast.LENGTH_LONG).show();
                            }
                            else {
                                JPushInterface.setAlias(QRCodeActivity.this,input,null);
                                Toast.makeText(QRCodeActivity.this,"Success",Toast.LENGTH_LONG).show();
                            }
                        }
                    }).
                    create();
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void  Cermae(String lx){
        Intent intent =new Intent(QRCodeActivity.this,pic_cute.class);
        Bundle bundle=new Bundle();
        bundle.putString("lx", lx);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class AndroidJavaScript {
        public AndroidJavaScript() {
        }//JavaScript调用此方法
        @JavascriptInterface
        public void callAndroidMethod(){
            Intent intent = new Intent();
            intent.setClass(QRCodeActivity.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
        }
        @JavascriptInterface
        public void make_QRcode(){
        }
        @JavascriptInterface
        public void login_QRcode(){
        }
        @JavascriptInterface
        public void  JsCermae(String lx){
            Intent intent =new Intent(QRCodeActivity.this,pic_cute.class);
            Bundle bundle=new Bundle();
            bundle.putString("lx", lx);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        @JavascriptInterface
        public String sayHello() {
            return "Hello World!";
        }
    }
}
