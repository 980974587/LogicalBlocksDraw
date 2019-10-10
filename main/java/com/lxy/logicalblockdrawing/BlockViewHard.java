package com.lxy.logicalblockdrawing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//自定义控件，困难模式
public class BlockViewHard extends View {

    private int[] oldXY={1,1};//表示原本显示的xy轴的顺序，默认一开始为1,1
    private int[] newXY={1,1};//表示新的显示xy轴的顺序，默认一开始为1,1
    private ArrayList alTrue=new ArrayList();//用来记录所有判断为√的正确坐标,如1,1
    private ArrayList alFalse=new ArrayList();//用来记录所有判断为x的正确坐标
    Bitmap bitmap=Bitmap.createBitmap(1315,1180,Bitmap.Config.ARGB_4444);//Bitmap.Config.ARGB_4444表示21位图
    Canvas cachecanvas=new Canvas();//画布缓冲区
    boolean getTips=false;//是否已经将要解析的文件解析完毕，默认还没有
    //读取并解析图片
    ImageRead imageRead=new ImageRead();
    List<List> listsX;
    List<List> listsY;
    Paint paint=new Paint();//背景画笔
    Paint line=new Paint();//内部线画笔
    Paint line1=new Paint();//内部粗线画笔
    Paint border=new Paint();//边框画笔
    Paint choose=new Paint();//选择框画笔
    Paint chooseTrue=new Paint();//填充格子画笔
    Paint chooseFalse=new Paint();//打错画笔
    Paint tips=new Paint();//数字提示画笔
    Paint showfalse=new Paint();//绘制错误提示
    private int count=0;//统计判断“存在”的格子数
    private boolean result=true;//判断结果
    private int countFalse=0;//错误次数
    private boolean isSave=false;//是否是diy图片，diy则不解析图片

    public BlockViewHard(Context context) {
        super(context);
    }

    //解析图片
    public void set(String name)
    {
        Class drawable = R.drawable.class;
        Field field = null;
        int res_ID;
        try {
            field = drawable.getField(name);
            res_ID = field.getInt(field.getName());
            listsX = imageRead.getListX(((BitmapDrawable)getResources().getDrawable(res_ID)).getBitmap(),15);
            listsY = imageRead.getListY(((BitmapDrawable)getResources().getDrawable(res_ID)).getBitmap(),15);
            getTips=true;
        } catch (Exception e) {}
    }

    //解析自制的图片
    public  void setDiy(String imgPath)
    {
        Bitmap bitmap = null;
        try
        {
            FileInputStream fis = null;
            try {
                Log.i("------------------","easy解析显示path:"+imgPath);
                fis = new FileInputStream(new File(imgPath));
            } catch (FileNotFoundException e) {
                Log.i("-----------------------","hard解析失败diy");
                e.printStackTrace();
            }
            bitmap= (Bitmap) BitmapFactory.decodeStream(fis);
        } catch (Exception e)
        {
        }
        listsX=imageRead.getListX(bitmap,15);
        listsY=imageRead.getListY(bitmap,15);
        getTips=true;
    }

    //如果是diy，则不解析图片
    public void isSave()
    {
        isSave=true;
    }

    //当android需要调用自定义view时就会调用view的ondraw方法
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        buffer();
        canvas.drawBitmap(bitmap,0,0,null);
        if(isSave)
        {

        }
        else {
            //绘制错误提示
            showfalse.setTextSize(40);
            showfalse.setColor(Color.rgb(150,0,0));
            if(result==false)
            {
                canvas.drawText("判断错误！",310+45*(newXY[0]-1),420+45*newXY[1],showfalse);
                result=true;
            }
        }

        //绘制选中框
        choose.setColor(Color.RED);
        choose.setStrokeWidth(8);
        choose.setStyle(Paint.Style.STROKE);
        canvas.drawRect(370+45*(newXY[0]-1),370+45*(newXY[1]-1),370+45*newXY[0],370+45*newXY[1],choose);
    }

    //调用方向键后的新定位
    public int[] setnewXY(String direction)
    {
        switch (direction)
        {
            case "Left":
                if(oldXY[0]!=1)
                {
                    newXY[0]=oldXY[0]-1;
                    oldXY[0]=newXY[0];
                }else{
                    newXY[0]=15;
                    oldXY[0]=newXY[0];
                }
                break;
            case "Up":
                if(oldXY[1]!=1)
                {
                    newXY[1]=oldXY[1]-1;
                    oldXY[1]=newXY[1];
                }
                else{
                    newXY[1]=15;
                    oldXY[1]=newXY[1];
                }
                break;
            case "Right":
                if(oldXY[0]!=15)
                {
                    newXY[0]=oldXY[0]+1;
                    oldXY[0]=newXY[0];
                }
                else{
                    newXY[0]=1;
                    oldXY[0]=newXY[0];
                }
                break;
            case "Down":
                if(oldXY[1]!=15)
                {
                    newXY[1]=oldXY[1]+1;
                    oldXY[1]=newXY[1];
                }
                else {
                    newXY[1]=1;
                    oldXY[1]=newXY[1];
                }
                break;
        }
        Log.i("----------------------","4：("+newXY[0]+","+newXY[1]+")");
        return newXY;
    }


    //自制关卡时判断坐标是否重复，重复就删除
    public boolean DIYisReapeat(){
        //默认不重复
        boolean repeat=true;
        Log.i("-------------------","是否重复");
        for(int i=0;i<alTrue.size()/2;i++)
        {
            if(newXY[0]==(int)alTrue.get(2*i)&&newXY[1]==(int)alTrue.get(2*i+1))
            {
                alTrue.remove(2*i);
                //因为删除之后自动超前推进了，所以2*i+1变成2*i
                alTrue.remove(2*i);
                repeat=false;
            }
        }
        return repeat;
    }


    //游玩时判断坐标是否重复
    public boolean isReapeat(){
        //默认不重复
        boolean repeat=true;
        Log.i("-------------------","是否重复");
        for(int i=0;i<alTrue.size()/2;i++)
        {
            if(newXY[0]==(int)alTrue.get(2*i)&&newXY[1]==(int)alTrue.get(2*i+1))
            {
                Log.i("-------------------","Y");
                repeat=false;
            }
        }
        if(repeat)
        {
            Log.i("-------------------","N");
            count++;
        }
        return repeat;
    }

    //判断最终结果
    public boolean Result()
    {
            if(imageRead.result(newXY))
            {
                return true;
            }
        else
        {
            //判断错了
            countFalse++;
            result=false;
            postInvalidate();
            return false;
        }
    }

    //将判断的坐标填入数组
    public void Choose(String string)
    {
        if(string.equals("chooseTrue"))
        {
            alTrue.add(newXY[0]);
            alTrue.add(newXY[1]);
        }
        else {
            alFalse.add(newXY[0]);
            alFalse.add(newXY[1]);
        }
    }

    //判断游戏是否结束
    public boolean isFinished()
    {
        if(imageRead.isFinished(count))
        {
            return true;
        }else return false;
    }

    //传递错误次数
    public int getCountFalse()
    {
        return countFalse;
    }

    //传递总的正确数目
    public int getCount()
    {
        return count;
    }

    //保存图片
    @SuppressLint("WrongThread")
    public void Sava()
    {
        buffer();
        Bitmap bitmapSave=Bitmap.createBitmap(bitmap,370,370,675,675);
        //创建app的文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "LogicalBlock");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //用系统时间生成文件名，可以确保不会因为重名出错
        String fileName=System.currentTimeMillis()+".jpg";
        //创建文件
        File file = new File(appDir, fileName);
        try {
            //写入文件内容
            FileOutputStream fos = new FileOutputStream(file);
            bitmapSave.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 再把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(),file.getAbsolutePath() ,fileName,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("--------------------","写入失败");
        }
        // 最后通知图库更新
        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + appDir)));
    }


    public BlockViewHard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //建立缓冲区，事先把背景和已经判断好的绘制成图片
    public void buffer()
    {
        cachecanvas.setBitmap(bitmap);
        //背景颜色
        paint.setColor(Color.WHITE);
        cachecanvas.drawRect(370,370,1045,1045,paint);
        //绘制判断的结果
        chooseTrue.setColor(Color.rgb(0,153,255));
        chooseFalse.setColor(Color.rgb(192,192,192));
        for(int i=0;i<(alFalse.size()/2);i++)
        {

            cachecanvas.drawRect(370+45*((Integer) alFalse.get(i*2)-1),370+45*((Integer) alFalse.get(i*2+1)-1),
                    370+45*(Integer) alFalse.get(i*2),370+45*(Integer) alFalse.get(i*2+1),chooseFalse);
        }
        for(int i=0;i<(alTrue.size()/2);i++)
        {
            cachecanvas.drawRect(370+45*((Integer) alTrue.get(i*2)-1),370+45*((Integer) alTrue.get(i*2+1)-1),
                    370+45*(Integer) alTrue.get(i*2),370+45*(Integer) alTrue.get(i*2+1),chooseTrue);
        }
        //绘制内部横线
        float[]xlines={370,415,1045,415,  370,460,1045,460,  370,505,1045,505,  370,550,1045,550,      370,640,1045,640, 370,685,1045,685,  370,730,1045,730,
                370,775,1045,775,      370,865,1045,865,  370,910,1045,910,  370,955,1045,955,  370,1000,1045,1000,};
        float[]xlines1={370,595,1045,595,  370,820,1045,820,};
        line.setColor(Color.rgb(100,100,100));
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(3);
        line1.setColor(Color.rgb(70,70,70));
        line1.setStyle(Paint.Style.STROKE);
        line1.setStrokeWidth(5);
        cachecanvas.drawLines(xlines,line);
        cachecanvas.drawLines(xlines1,line1);
        //绘制内部竖线
        float[]ylines={415,370,415,1045, 460,370,460,1045,  505,370,505,1045,  550,370,550,1045,       640,370,640,1045,  685,370,685,1045,  730,370,730,1045,
                775,370,775,1045,      865,370,865,1045,  910,370,910,1045,  955,370,955,1045,  1000,370,1000,1045,  };
        float[]ylines1={ 595,370,595,1045,  820,370,820,1045, };
        cachecanvas.drawLines(ylines,line);
        cachecanvas.drawLines(ylines1,line1);
        //绘制外部边框
        border.setColor(Color.BLACK);
        border.setStrokeWidth(8);
        border.setStyle(Paint.Style.STROKE);
        cachecanvas.drawRect(370,370,1045,1045,border);

        //绘制数字提示信息
        tips.setTextSize(45);
        if(getTips)
        {
            for(int i=0;i<listsX.size();i++)
            {
                for (int j=0;j<listsX.get(i).size();j++)
                {
                    if((int)listsX.get(i).get(j)>=10)
                    {
                        cachecanvas.drawText(""+listsX.get(i).get(listsX.get(i).size()-j-1),360+45*i,350-45*j,tips);
                    }else{
                        cachecanvas.drawText(""+listsX.get(i).get(listsX.get(i).size()-j-1),375+45*i,350-45*j,tips);
                    }
                    //Log.i("----------------------","x第"+i+"列第"+j+"行"+listsX.get(i).get(j));
                }
                for(int j=0;j<listsY.get(i).size();j++)
                {
                    if((int)listsY.get(i).get(j)>=10)
                    {
                        cachecanvas.drawText(""+listsY.get(i).get(listsY.get(i).size()-j-1),300-45*j,410+45*i,tips);
                    }else{
                        cachecanvas.drawText(""+listsY.get(i).get(listsY.get(i).size()-j-1),315-45*j,410+45*i,tips);
                    }
                }
            }
        }

    }
}
