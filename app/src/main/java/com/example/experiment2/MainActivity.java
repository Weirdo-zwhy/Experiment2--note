package com.example.experiment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
                //取出用户名和密码输入内容
                String username1=username.getText().toString();
                String password1=password.getText().toString();
                //读取内容，如果没有则返回默认值
                String username2=pref.getString("username","admin");
                String password2 = pref.getString("password","123456");
                if (username2.equals(username1)&&password2.equals(password1)){
                    Intent intent = new Intent(MainActivity.this,DiaryActivity.class);
                    intent.putExtra("username",username1);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}