package com.lxy.logicalblockdrawing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;

//游戏类，教程、入门、简单模式
public class levelEasy extends AppCompatActivity
{
    BlockViewEasy blockViewEasy;
    Intent it;
    LinearLayout ll;
    TextView tv,tvfinish;
    ImageView iv;
    String tip;
    Boolean isLearn=false;
    public SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    public HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    int tipnum=1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐去电池等图标和一切修饰部分（状态栏部分）
        setContentView(R.layout.learn);
        ll=findViewById(R.id.save);
        soundMap.put(0, soundPool.load(this, R.raw.music_true, 1));
        soundMap.put(1,soundPool.load(this,R.raw.sucess,1));
        soundMap.put(2,soundPool.load(this,R.raw.fail,1));
        it=getIntent();
        //直接通过布局文件获取id来调用blockview
        blockViewEasy =findViewById(R.id.blockView);
        if(it.getStringExtra("isSave").equals("yes"))
        {
            ll.setVisibility(View.VISIBLE);
            blockViewEasy.isSave();
        }else
        {
            if (it.getStringExtra("useDiy").equals("yes"))
            {
                Log.i("------------------","easy显示path:"+it.getStringExtra("imgPath"));
                blockViewEasy.setDiy(it.getStringExtra("imgPath"));
                tv=findViewById(R.id.sign);
                tip="提示信息：自制关卡";
                tv.setText(tip);
            }
            else
            {
                blockViewEasy.set(it.getStringExtra("name"));
                tv=findViewById(R.id.sign);
                tip="提示信息："+it.getStringExtra("tip");
                tv.setText(tip);
                if(it.getStringExtra("name").equals("door0"))
                {
                    new AlertDialog.Builder(this)
                            .setTitle("教程提示")
                            .setMessage("\n(⋌▀¯▀)=☞在教程关你可以在右上角看到游戏提示信息\n\n也可以点击取消自己尝试，不过错误超过一定次数时，游戏就会提前结束( ･᷄д･᷅ )" +
                                    "\n\n游戏目标：找到所有格子中的蓝格子（空格子默认不用标出），完成像素图")
                            .setCancelable(false)
                            .setPositiveButton("取消",null)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(it.getStringExtra("name").equals("door0"))
                                    {
                                        tip="提示1:点击方向键移动\n“√”标记蓝格，“x”标记空格"+
                                                "\n“√”能覆盖“x”，反之不能";
                                        tv.setText(tip);
                                        tipnum=2;
                                        isLearn=true;
                                    }
                                }
                            })
                            .create().show();
                }
            }
            Log.i("--------------","选择了"+it.getStringExtra("name"));
        }
    }

    //游戏移动按钮方法
    public void Move(View view)
    {
        switch (view.getId())
        {
            case R.id.up:
                blockViewEasy.setnewXY("Up");
                break;
            case R.id.left:
                blockViewEasy.setnewXY("Left");
                break;
            case R.id.right:
                blockViewEasy.setnewXY("Right");
                break;
            case R.id.down:
                blockViewEasy.setnewXY("Down");
                break;
        }
        blockViewEasy.postInvalidate();
        if(isLearn&&tipnum==2)
        {
            tip="提示2:观察第一列和第五行数字\n表该行(列)有5个连续的蓝格子\n可确定该行（列）全为蓝格子";
            tv.setText(tip);
            tipnum=3;
        }
    }

    //游戏判断按钮的方法
    public void Choose(View view)
    {
        if (it.getStringExtra("isSave").equals("yes"))
        {
            if (view.getId()==R.id.chooseTrue){
                if(it.getStringExtra("playVoice").equals("yes"))
                {
                    playVoice();
                }
                if(blockViewEasy.DIYisReapeat())
                {
                    //重复了就不添加
                    blockViewEasy.Choose("chooseTrue");
                }
                blockViewEasy.postInvalidate();
            }
            else
            {
                blockViewEasy.Choose("chooseFalse");
                blockViewEasy.postInvalidate();
            }
        }else
        {
            if (view.getId()==R.id.chooseTrue&& blockViewEasy.Result()&&blockViewEasy.isReapeat())
            {
                if(it.getStringExtra("playVoice").equals("yes"))
                {

                    playVoice();
                }
                blockViewEasy.Choose("chooseTrue");
                blockViewEasy.postInvalidate();
                if (tipnum==6)
                {
                    tip="提示6:这是个对角对称图像\n猜图和运气也很重要\n请结合数字提示多多尝试吧";
                    tv.setText(tip);
                    tipnum=7;
                }
                if (tipnum==5&&blockViewEasy.getCount()==14)
                {
                    tip="提示5:观察第三列数字：1和1和1\n说明至少存在两个空格\n1+1+1，该列共3蓝格\n" +
                            "该列顺序为1蓝1空1蓝1空1蓝";
                    tv.setText(tip);
                    tipnum=6;
                }
                if(tipnum==4&&blockViewEasy.getCount()==12)
                {
                    tip="提示4:同理观察第一行数字:3和1\n该列顺序为3连续蓝1空1蓝";
                    tv.setText(tip);
                    tipnum=5;
                }
                if(tipnum==3&&blockViewEasy.getCount()==9)
                {
                    tip="提示3:观察第五列数字：1和3\n表示间隔了不定数量空格\n1+3:共4个蓝格,剩1空格\n" +
                            "该列顺序为1蓝格1空格3连续蓝格";
                    tv.setText(tip);
                    tipnum=4;
                }
                //如果所有的“存在”都判断出来了，就跳出结束提示
                if(blockViewEasy.isFinished())
                {
                    if(it.getStringExtra("playVoice").equals("yes"))
                    {
                        playSucess();
                    }
                    LinearLayout linearLayout= (LinearLayout) getLayoutInflater().inflate(R.layout.finish,null);
                    iv=linearLayout.findViewById(R.id.iv);
                    tvfinish=linearLayout.findViewById(R.id.tvfinish);
                    tvfinish.setText("找到的蓝格子数目为："+blockViewEasy.getCount()+"\n错误次数为："+ blockViewEasy.getCountFalse());
                    if(it.getStringExtra("useDiy").equals("yes"))
                    {
                        FileInputStream fis = null;
                        try {
                            Log.i("------------------","结束path:"+it.getStringExtra("imgPath"));
                            fis = new FileInputStream(new File(it.getStringExtra("imgPath")));
                            Bitmap bitmap= (Bitmap) BitmapFactory.decodeStream(fis);
                            iv.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            Log.i("-----------------------","easy结束失败diy");
                            e.printStackTrace();
                        }
                    }else
                    {
                        Class drawable = R.drawable.class;
                        try {
                            Field field = drawable.getField(it.getStringExtra("name"));
                            int res_ID = field.getInt(field.getName());
                            iv.setImageDrawable(getDrawable(res_ID));
                        } catch (Exception e) {
                            Log.i("----------------------","放入图片报错了！");
                        }
                    }

                    new AlertDialog.Builder(this)
                            .setTitle("恭喜通关！")
                            .setCancelable(false)
                            .setView(linearLayout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    back();
                                }
                            })
                            .create().show();
                }
            }
            else if(view.getId()==R.id.chooseFalse)
            {
                //“不存在”的正误不影响游戏，所以不另外判断
                blockViewEasy.Choose("chooseFalse");
                blockViewEasy.postInvalidate();
            }
            else if(blockViewEasy.getCountFalse()>=15)
            {
                //错误次数过多时自动结束游戏
                if(it.getStringExtra("playVoice").equals("yes"))
                {

                    playFail();
                }
                new AlertDialog.Builder(this)
                        .setTitle("游戏结束！")
                        .setMessage("\n很遗憾，你错误的次数太多了\n\n找到的蓝格子为："+blockViewEasy.getCount()+"\n\n请再接再厉 ( •ิ◡• ิ)")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                back();
                            }
                        })
                        .create().show();
            }
        }

    }

    //返回
    public void back()
    {
        this.finish();
    }

    public void isBack(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle("返回提醒")
                .setMessage("进度不会保存，确定要退出吗？")
                .setPositiveButton("取消",null)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        back();
                    }
                })
                .create().show();
    }

    //将自制图片保存
    public void Sava(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle("确认保存")
                .setMessage("保存到：/sdcard/LogicalBlock，是否确认保存？\nps:第一次使用需要授权，授权后再次保存就能正常保存")
                .setPositiveButton("取消",null)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(checkPermission())
                        {
                            blockViewEasy.Sava();
                            Toast.makeText(levelEasy.this, "已保存至/sdcard/LogicalBlock", Toast.LENGTH_SHORT).show();
                            back();
                        }
                    }
                })
                .create().show();
    }

    //检查读写权限
    private boolean checkPermission() {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE))
            {
                Toast.makeText(levelEasy.this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_LONG).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(levelEasy.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                Log.e("---------------------", "没有授权！");
                return false;
            }else return true;
        } else {
            Log.e("---------------------", "checkPermission: 已经授权！");
            return true;
        }
    }

    //控制判断音效的播放
    public void playVoice()
    {
        Log.i("----------------------","音效播放");
        this.soundPool.play
        (
            soundMap.get(0),
            0.2f,      //左耳道音量【0~1】
            0.2f,      //右耳道音量【0~1】
            0,         //播放优先级【0表示最低优先级】
            0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
            1        //播放速度【1是正常，范围从0~2】
        );
    }

    //控制判断成功音效的播放
    public void playSucess()
    {
        Log.i("----------------------","音效播放");
        this.soundPool.play
                (
                        soundMap.get(1),
                        0.2f,    //左耳道音量【0~1】
                        0.2f,   //右耳道音量【0~1】
                        0,         //播放优先级【0表示最低优先级】
                        0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                        1        //播放速度【1是正常，范围从0~2】
                );
    }

    //控制判断失败音效的播放
    public void playFail()
    {
        Log.i("----------------------","音效播放");
        this.soundPool.play
                (
                        soundMap.get(2),
                        0.2f,      //左耳道音量【0~1】
                        0.2f,      //右耳道音量【0~1】
                        0,         //播放优先级【0表示最低优先级】
                        0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                        1        //播放速度【1是正常，范围从0~2】
                );
    }
}


