package com.lxy.logicalblockdrawing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//读取图片文件并生成对应提示数组
public class ImageRead {
    private List<Integer> listX;//纵向的数字提示（横向显示）
    private List<Integer> listY;//横向的数字提示（纵向显示）
    private List<List> listsX = new ArrayList<List>();//分列的纵向提示
    private List<List> listsY=new ArrayList<List>();//分行的横向提示
    private int[] isXY;//存放当前“存在”的坐标,如(1,1)
    private List<int[]> xy=new ArrayList<int[]>();//存放所有“存在”的xy坐标
    private boolean isEmpty=true;//默认某一列（行）为空
    private int color;
    private  int width;//图片的宽
    private  int height;//图片的长
    private  int blockWidth;//方块的边宽
    private int red;
    private int blue;
    private int green;
    private int sum=0;//记录连续的“存在”格子数
    private int count=0;//记录所有“存在”的格子数
    private boolean result=false;//判断的结果

    //获取纵向数字(横向显示）
    public List getListX(Bitmap bitmap, int level)
    {
        width=bitmap.getWidth();
        Log.i("------------------","图片宽"+width);
        blockWidth=width/level;
        Log.i("------------------","格子宽"+blockWidth);
        for(int i=1;i<=level;i++)
        {
            listX=new ArrayList();
            for(int j=1;j<=level;j++)
            {
                color=bitmap.getPixel(i*blockWidth-5,j*blockWidth-5);
                red= Color.red(color);
                green=Color.green(color);
                blue=Color.blue(color);
                if(red>=190&&green>=190&&blue>=190)
                {
                    //如果之前有过"存在"的记录，就结束分隔并计入
                    if(sum!=0){
                        listX.add(sum);
                        Log.i("----------------------","x写入sum："+sum);
                    }
                    sum=0;
                    //最后一个为“不存在”时，如果该列全“不存在”，则写入提示为“0”
                    if(j==level&&isEmpty)
                    {
                        listX.add(0);
                    }
                }else {
                    //该列不为空
                    isEmpty=false;
                    //记录“存在”的连续数
                    isXY=new int[]{i,j};
                    //同时将该“存在”方块的坐标存入，如（1,1）
                    xy.add(isXY);
                    sum++;
                    count++;
                    //该列最后如果是“存在”，就要直接计入
                    if(j==level)
                    {
                        listX.add(sum);
                        isEmpty=true;
                    }
                    //Log.i("-----------------------","x第"+i+"列第"+j+"行");
                }
            }
            sum=0;
            isEmpty=true;
            listsX.add(listX);
        }
        return listsX;
    }

    //获取纵向数字(横向显示）
    public List getListY(Bitmap bitmap, int level)
    {
        height=bitmap.getHeight();
        Log.i("------------------","图片高"+height);
        blockWidth=height/level;
        for(int i=1;i<=level;i++)
        {
            listY=new ArrayList();
            for(int j=1;j<=level;j++)
            {
                color=bitmap.getPixel(j*blockWidth-5,i*blockWidth-5);
                red= Color.red(color);
                green=Color.green(color);
                blue=Color.blue(color);
                if(red>=190&&green>=190&&blue>=190)
                {
                    //如果之前有过"存在"的记录，就结束分隔并计入
                    if(sum!=0){
                        listY.add(sum);
                        //Log.i("---------------------","y写入sum："+sum);
                    }
                    sum=0;
                    //最后一个为“不存在”时，如果该列全“不存在”，则写入提示为“0”
                    if(j==level&&isEmpty)
                    {
                        listY.add(0);
                    }
                }else {
                    //该列不为空
                    isEmpty=false;
                    sum++;
                    //最后如果是“存在”，就要直接计入
                    if(j==level)
                    {
                        listY.add(sum);
                    }
                    //Log.i("----------------------","y第"+i+"行第"+j+"列");
                }
            }
            sum=0;
            isEmpty=true;
            listsY.add(listY);
        }
        return listsY;
    }


    //判断“存在"的结果是否正确
    public boolean result(int[] nowxy)
    {
        result=false;
        for(int i=0;i<xy.size();i++)
        {
            if(nowxy[0]==xy.get(i)[0]&&nowxy[1]==xy.get(i)[1])
            {
                result=true;
            }
        }
        return result;
    }

    //通过判断“存在”格子数目来判断游戏是否结束
    public boolean isFinished(int count)
    {
        //Log.i("----------------------","数量："+this.count);
        if (count==this.count)
        {
            return true;
        }else return false;
    }

    }


