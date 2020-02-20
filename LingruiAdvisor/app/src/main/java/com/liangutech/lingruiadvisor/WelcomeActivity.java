package com.liangutech.lingruiadvisor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//满屏显示
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        handler.sendEmptyMessageDelayed(1,2000);
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 1){
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
            return false;
        }
    });
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }
        return false;
    }
}
