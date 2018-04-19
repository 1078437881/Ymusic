package com.yibo.ymusic.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yibo.ymusic.R;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }

    public native String baseCall(int x,String y);
}
