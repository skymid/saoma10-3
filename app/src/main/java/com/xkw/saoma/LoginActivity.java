package com.xkw.saoma;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity  {
    private MyData mydata;
    private Button button_login;
    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText editText_user, editText_passwd,edit_Url;
    private CheckBox chk_login;

    private SharedPreferences sharedPreferences;
    private String user, passwd,retCode;
    String str_URL="http://112.116.119.47:81/android/login_php.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //竖屏
        setContentView(R.layout.activity_login);

        // Set up the login form.
        button_login = (Button) findViewById(R.id.login_button);

        chk_login = (CheckBox) findViewById(R.id.chk_autlogin);
        editText_user = (EditText) findViewById(R.id.username_edit);
        editText_passwd = (EditText) findViewById(R.id.password_edit);

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String dl;
        if (sharedPreferences.getString("chkAutLogin", null) == null) {
            dl = "false";
        } else {
            dl = sharedPreferences.getString("chkAutLogin", null);
        }
        if (dl.equals("true")) {
            chk_login.setChecked(true);

            Toast.makeText(LoginActivity.this, "自动登录！！....", Toast.LENGTH_SHORT).show();
            editText_user.setText(sharedPreferences.getString("username", null));
            editText_passwd.setText(sharedPreferences.getString("password", null));
            // button_login.callOnClick(this);
        } else {
            editText_user.setText("");
            editText_passwd.setText("");
        }

        if (chk_login.isChecked()) {

        }

        chk_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    Toast.makeText(LoginActivity.this, "存储用户信息", Toast.LENGTH_SHORT).show();
                    editor.putString("username", editText_user.getText().toString());
                    editor.putString("password", editText_passwd.getText().toString());
                    editor.putString("chkAutLogin", "true");
                } else {
                    Toast.makeText(LoginActivity.this, "清除用户信息", Toast.LENGTH_SHORT).show();
                    editor.putString("username", "");
                    editor.putString("password", "");
                    editor.putString("chkAutLogin", "");
                }
                editor.commit();
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = editText_user.getText().toString();
                passwd = editText_passwd.getText().toString();

                if (user.equals("") || passwd.equals("")) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空，请重新输入!", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, str_URL, listener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("user", user);
                        map.put("password", passwd);
                        return map;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });
    }

    Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            Log.v("123", s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                retCode = jsonObject.getString("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (retCode == "true") {
                Toast.makeText(LoginActivity.this, "OK!", Toast.LENGTH_SHORT).show();
                if (chk_login.isChecked()) {
                    sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", user);
                    editor.putString("password", passwd);
                    editor.putString("chkAutLogin", "true");
                    editor.commit();
                }
                mydata=(MyData)getApplication();
                mydata.setUserID(user);

                Intent intent =new Intent(LoginActivity.this,QRCodeActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("userID", user);
                intent.putExtras(bundle);
                startActivity(intent);



                //startActivity(new Intent(LoginActivity.this, HtmlActivity.class));
            } else {
                Toast.makeText(LoginActivity.this, "用户名或密码错误!", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.e(TAG, volleyError.getMessage(), volleyError);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search: Toast.makeText(this, "你点击了搜索", Toast.LENGTH_SHORT).show();
            case R.id.action_settings: Toast.makeText(this, "你点击了设置", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}

