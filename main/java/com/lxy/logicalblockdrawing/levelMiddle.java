package com.lxy.logicalblockdrawing;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

//游戏类，中等模式
public class levelMiddle extends AppCompatActivity
{
    BlockViewMiddle blockViewMiddle;
    Intent it;
    LinearLayout ll;
    TextView tv,tvfinish;
    ImageView iv;
    String tip;
    int pv=0;
    public SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    public HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐去电池等图标和一切修饰部分（状态栏部分）
        setContentView(R.layout.level_middle);
        it=getIntent();
        soundMap.put(0, soundPool.load(this, R.raw.music_true, 1));
        soundMap.put(1,soundPool.load(this,R.raw.sucess,1));
        soundMap.put(2,soundPool.load(this,R.raw.fail,1));
        //直接通过布局文件获取id来调用blockview
        blockViewMiddle =findViewById(R.id.blockView);
        ll=findViewById(R.id.save);
        if(it.getStringExtra("isSave").equals("yes"))
        {
            ll.setVisibility(View.VISIBLE);
            blockViewMiddle.isSave();
        }else
        {
            if (it.getStringExtra("useDiy").equals("yes"))
            {
                Log.i("------------------","easy显示path:"+it.getStringExtra("imgPath"));
                blockViewMiddle.setDiy(it.getStringExtra("imgPath"));
                tv=findViewById(R.id.sign);
                tip="提示信息：自制关卡";
                tv.setText(tip);
            }
            else
            {
                blockViewMiddle.set(it.getStringExtra("name"));
                Log.i("--------------","选择了"+it.getStringExtra("name"));
                tv=findViewById(R.id.sign);
                tip="提示信息："+it.getStringExtra("tip")+"\n作画人:不愿意透露姓名的咸鱼梦碎";
                tv.setText(tip);
            }
        }
    }
    //游戏移动按钮方法
    public void Move(View view)
    {
        switch (view.getId())
        {
            case R.id.up:
                blockViewMiddle.setnewXY("Up");
                break;
            case R.id.left:
                blockViewMiddle.setnewXY("Left");
                break;
            case R.id.right:
                blockViewMiddle.setnewXY("Right");
                break;
            case R.id.down:
                blockViewMiddle.setnewXY("Down");
                break;
        }
        blockViewMiddle.postInvalidate();
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
                if(blockViewMiddle.DIYisReapeat())
                {
                    blockViewMiddle.Choose("chooseTrue");
                }
                blockViewMiddle.postInvalidate();
            }
            else
            {
                blockViewMiddle.Choose("chooseFalse");
                blockViewMiddle.postInvalidate();
            }
        }else {
            if (view.getId() == R.id.chooseTrue && blockViewMiddle.Result() && blockViewMiddle.isReapeat()) {
                Log.i("----------------------", "1");
                if(it.getStringExtra("playVoice").equals("yes"))
                {
                    playVoice();
                }
                blockViewMiddle.Choose("chooseTrue");
                blockViewMiddle.postInvalidate();
                //如果所有的“存在”都判断出来了，就跳出结束提示
                if (blockViewMiddle.isFinished()) {
                    if(it.getStringExtra("playVoice").equals("yes"))
                    {
                        playSucess();
                    }
                    LinearLayout linearLayout= (LinearLayout) getLayoutInflater().inflate(R.layout.finish,null);
                    iv=linearLayout.findViewById(R.id.iv);
                    tvfinish=linearLayout.findViewById(R.id.tvfinish);
                    tvfinish.setText("找到的蓝格子数目为："+blockViewMiddle.getCount()+"\n错误次数为:"+ blockViewMiddle.getCountFalse());
                    if(it.getStringExtra("useDiy").equals("yes"))
                    {
                        FileInputStream fis = null;
                        try {
                            Log.i("------------------","结束path:"+it.getStringExtra("imgPath"));
                            fis = new FileInputStream(new File(it.getStringExtra("imgPath")));
                            Bitmap bitmap= (Bitmap) BitmapFactory.decodeStream(fis);
                            iv.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            Log.i("-----------------------","middle结束失败diy");
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

            } else if (view.getId() == R.id.chooseFalse) {
                //“不存在”的正误不影响游戏，所以不另外判断
                Log.i("----------------------", "1");
                blockViewMiddle.Choose("chooseFalse");
                blockViewMiddle.postInvalidate();
            } else if (blockViewMiddle.getCountFalse() >= 40)
            {
                //错误次数过多时自动结束游戏
                if(it.getStringExtra("playVoice").equals("yes"))
                {

                    playFail();
                }
                new AlertDialog.Builder(this)
                        .setTitle("游戏结束！")
                        .setMessage("\n很遗憾，你错误的次数太多了\n\n找到的蓝格子为：" + blockViewMiddle.getCount() + "\n\n请再接再厉 ( •ิ◡• ิ)")
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
                            blockViewMiddle.Sava();
                            Toast.makeText(levelMiddle.this, "已保存至/sdcard/LogicalBlock", Toast.LENGTH_LONG).show();
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
                Toast.makeText(levelMiddle.this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(levelMiddle.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
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
        pv=this.soundPool.play(
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
                        0.2f,      //左耳道音量【0~1】
                        0.2f,      //右耳道音量【0~1】
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


