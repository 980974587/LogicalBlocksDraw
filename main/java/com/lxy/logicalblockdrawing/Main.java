package com.lxy.logicalblockdrawing;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.HashMap;

public class Main extends AppCompatActivity {
    private boolean isExit;
    public HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    public CheckBox cb1,cb2;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐去电池等图标和一切修饰部分（状态栏部分）
        setContentView(R.layout.activity_main);
        mediaPlayer=MediaPlayer.create(this,R.raw.music_bg);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                //通过该方法实现检测音乐的播放进度
                mediaPlayer.start();
            }
        });
        cb1=findViewById(R.id.changevoice);
        cb2=findViewById(R.id.changemusic);
        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayMusic();
            }
        });
    }
    //双击返回键退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                PlayMusic();
                this.finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit= false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //按钮退出
    public void exit(View view)
    {
        isExit=true;
        PlayMusic();
        this.finish();
    }

    public void Choose(View view)
    {
        int id=view.getId();
        Intent it=new Intent();
        switch (id)
        {
            case R.id.start:
                it=new Intent(this, level.class);
                it.putExtra("isSave","no");
                break;
            case R.id.learn:
                it=new Intent(this, levelEasy.class);
                it.putExtra("name","door0");
                it.putExtra("tip","教程");
                it.putExtra("isSave","no");
                it.putExtra("useDiy","no");
                break;
            case R.id.diy:
                it=new Intent(this,level.class);
                it.putExtra("isSave","yes");
                break;
        }
        if(cb1.isChecked())
        {
            it.putExtra("playVoice","no");
        }else {
            it.putExtra("playVoice","yes");
            Log.i("-----------------------","音效打开1");
        }
        startActivity(it);
    }

    //控制背景音乐的播放
    public void PlayMusic()
    {
        //如果按钮状态是播放就点击后关闭
        if(cb2.isChecked()||isExit)
        {
            mediaPlayer.pause();
        }else{//否则（一开始就）播放
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }

    }


}


