package com.xkw.saoma;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class pic_cute extends AppCompatActivity {
    private MyData mydata;
    private ImageView img_album;
    private final int  ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK = 3;
    private File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());


    private String postUrl ; //处理POST请求的页面
    private String path = Environment.getExternalStorageDirectory().getPath();  //Don't use "/sdcard/" here
    private String uploadFile = path + "/" + getPhotoFileName();    //待上传的文件路径
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_cute);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                uploadFile(postUrl,uploadFile,getPhotoFileName());
                finish();
            }
        });

        mydata=(MyData)getApplication();
        postUrl = "http://www.jsdwzx.net:81/android/android_up_pic.php?userid="+mydata.getUserID();
        Log.v("abc",postUrl);

        Bundle bundle = this.getIntent().getExtras();
        String s = bundle.getString("lx");
        Log.v("aaa",s);
        if(s.equals("Camera")) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
            startActivityForResult(intent, CAMERA_OK);
        }else {
            Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
            albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(albumIntent, ALBUM_OK);
        }

        img_album = (ImageView)findViewById(R.id.img_album);
//        bnt_back=(Button) findViewById(R.id.btn_back);
//        bnt_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode = " + requestCode);
        switch (requestCode) {
            case ALBUM_OK:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            case CAMERA_OK:
                if (tempFile.exists()) {
                    startPhotoZoom(Uri.fromFile(tempFile));
                }
                break;
            case CUT_OK:
                if (data != null) {
                    sentPicToNext(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        //~~~没有这句的话，就不会保存裁剪图片
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); //广播刷新相册
        intentBc.setData(Uri.fromFile(tempFile));
        this.sendBroadcast(intentBc);
        //~~~~
        startActivityForResult(intent, CUT_OK);
    }

    // 将进行剪裁后的图片传递到下一个界面上
    private void sentPicToNext(Intent picdata) {
//        Bundle bundle = picdata.getExtras();
//        if (bundle != null) {
//            Bitmap photo = bundle.getParcelable("data");
//            if (photo==null) {
//                img_album.setImageResource(R.mipmap.ic_launcher);
//            }else {
//                img_album.setImageBitmap(photo);
//                pic_path.setText(tempFile.getAbsolutePath());
//            }
//        }
        img_album.setImageURI(picdata.getData());


    }

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /* 上传文件至Server的方法 */
    private void uploadFile(String postUrl,String uploadFile,String fileName)
    {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try
        {
            URL url = new URL(postUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* Output to the connection. Default is false,
             set to true because post method must write something to the connection */
            con.setDoOutput(true);
          /* Read from the connection. Default is true.*/
            con.setDoInput(true);
          /* Post cannot use caches */
            con.setUseCaches(false);
          /* Set the post method. Default is GET*/
            con.setRequestMethod("POST");
          /* 设置请求属性 */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
          /*设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接*/
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
          /* 设置DataOutputStream，getOutputStream中默认调用connect()*/
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"file\";filename=\"" +
                    fileName + "\"" + end);
            ds.writeBytes(end);
          /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
          /* 设置每次写入8192bytes */
            int bufferSize = 8192;
            byte[] buffer = new byte[bufferSize];   //8k
            int length = -1;
          /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1)
            {
            /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
          /* 关闭流，写入的东西自动生成Http正文*/
            fStream.close();
          /* 关闭DataOutputStream */
            ds.close();
          /* 从返回的输入流读取响应信息 */
            InputStream is = con.getInputStream();  //input from the connection 正式建立HTTP连接
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1)
            {
                b.append((char) ch);
            }
          /* 显示网页响应内容 */
            Toast.makeText(pic_cute.this, b.toString().trim(), Toast.LENGTH_SHORT).show();//Post成功
        } catch (Exception e)
        {
            /* 显示异常信息 */
            Toast.makeText(pic_cute.this, "Fail:" + e, Toast.LENGTH_SHORT).show();//Post失败
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.action_edit:

                break;
            case R.id.action_back:
                finish();
                break;
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
