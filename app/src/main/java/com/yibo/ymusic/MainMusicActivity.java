package com.yibo.ymusic;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yibo.ymusic.fragment.MainFragment;
import com.yibo.ymusic.fragment.SlideFragment;

public class MainMusicActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private SlideFragment slideFragment = null;
    private MainFragment mainFragment = null;
    private View slideView = null;
    public static DrawerLayout  mainDrawerLayout = null;
    public static MainMusicActivity mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_music);
        mContext = this;
        slideView = findViewById(R.id.slide_frame);
        mainDrawerLayout = findViewById(R.id.main_drawer);
        slideFragment = new SlideFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.slide_frame,slideFragment).commit();
        mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,mainFragment).commit();


    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
