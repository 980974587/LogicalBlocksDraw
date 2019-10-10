package com.lxy.logicalblockdrawing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;


//难度选择类
public class level extends AppCompatActivity {
    Intent it;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐去电池等图标和一切修饰部分（状态栏部分）
        setContentView(R.layout.level);
        it=getIntent();
    }

    public void back(View view)
    {
        this.finish();
    }


    public void choose(View view)
    {
        Intent it1=new Intent();
        if (it.getStringExtra("isSave").equals("yes"))
        {
            switch (view.getId())
            {
                case R.id.levelnew:
                    it1=new Intent(this, levelEasy.class);
                    break;
                case R.id.leveleasy:
                    it1=new Intent(this, levelEasy.class);
                    break;
                case R.id.levelmiddle:
                    it1=new Intent(this, levelMiddle.class);
                    break;
                case R.id.levelhard:
                    it1=new Intent(this, levelHard.class);
                    break;
            }
            it1.putExtra("isSave","yes");
        }else
        {
            it1=new Intent(this, choose.class);
            switch (view.getId())
            {
                case R.id.levelnew:
                    it1.putExtra("choose","door");
                    break;
                case R.id.leveleasy:
                    it1.putExtra("choose","easy");
                    break;
                case R.id.levelmiddle:
                    it1.putExtra("choose","middle");
                    break;
                case R.id.levelhard:
                    it1.putExtra("choose","hard");
                    break;
            }
            it1.putExtra("isSave","no");
        }
        it1.putExtra("playVoice",it.getStringExtra("playVoice"));
        startActivity(it1);
    }

}
