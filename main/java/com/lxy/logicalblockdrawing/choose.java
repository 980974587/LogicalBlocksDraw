package com.lxy.logicalblockdrawing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//关卡选择类
public class choose extends AppCompatActivity {
    ListView lv;
    List<HashMap<String, Object>> list=new ArrayList<HashMap<String, Object>>();
    String[] namelistDoor={"教程关","入门1","入门2","入门3","入门4","入门5","游玩自制关卡"};
    String[] tipslistDoor={"教程","字母T","汉字田","汉字井","汉字山","机器人脸",""};
    String[] namelistEasy={"简单1","简单2","简单3","简单4","简单5","游玩自制关卡"};
    String[] tipslistEasy={"汉字下","伞","汉字开","飞机","数字5",""};
    String[] namelistMiddle={"中等1","中等2","中等3","中等4","中等5","游玩自制关卡"};
    String[] tipslistMiddle={"机器人","吊起之人在呐喊","咸鱼头","万圣南瓜","猫",""};
    String[] namelistHard={"困难1","困难2","困难3","困难4","困难5","困难6","困难7","游玩自制关卡"};
    String[] tipslistHard={"咕咕头","莲蓬头","插头","狗狗头","猫猫头","鸭鸭头","企鹅头",""};
    Intent itChoose;
    Intent it;
    SimpleAdapter sa;
    String img_path="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐去电池等图标和一切修饰部分（状态栏部分）
        setContentView(R.layout.choose);
        lv=findViewById(R.id.lv);
        it=getIntent();
        Log.i("--------------","选择关卡界面,选择了"+it.getStringExtra("choose"));
        //填入所有的文件
        switch (it.getStringExtra("choose"))
        {
            case "door":
                for(int i=0;i<7;i++)
                {
                    HashMap<String,Object>name=new HashMap<String,Object>();
                    name.put("img",R.drawable.level);
                    name.put("name",namelistDoor[i]+"     "+tipslistDoor[i]);
                    list.add(name);
                }
                sa=new SimpleAdapter(this,list,R.layout.simple_item,
                        new String[]{"img","name"}, new int[]{R.id.img,R.id.tv});
                lv.setAdapter(sa);
                itChoose=new Intent(this,levelEasy.class);
                itChoose.putExtra("playVoice",it.getStringExtra("playVoice"));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i==6)
                        {
                            if(checkPermission())
                            {
                                //打开系统文件夹并选择文件
                                new AlertDialog.Builder(choose.this)
                                        .setTitle("提示")
                                        .setMessage("请选择/sdcard/LogicalBlock路径下的5*5格子图片\n其他路径或不同规格图片会解析出奇怪图案(⑉･̆-･̆⑉)")
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                itChoose.putExtra("useDiy","yes");
                                                itChoose.putExtra("isSave","no");
                                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                intent.setType("image/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                Log.i("--------------------","打开文件浏览器");
                                                try {
                                                    startActivityForResult(intent,1);
                                                } catch (ActivityNotFoundException e) {
                                                    e.printStackTrace();
                                                    Log.i("----------------","打开失败");
                                                }
                                            }
                                        })
                                        .create().show();
                            }

                        }
                        else {
                            Log.i("---------------","进入关卡");
                            itChoose.putExtra("name","door"+i);
                            Log.i("--------------","number:"+i);
                            itChoose.putExtra("tip",tipslistDoor[i]);
                            itChoose.putExtra("useDiy","no");
                            itChoose.putExtra("isSave","no");
                            startActivity(itChoose);
                        }
                    }
                });
                break;
            case "easy":
                for(int i=0;i<6;i++)
                {
                    HashMap<String,Object>name=new HashMap<String,Object>();
                    name.put("img",R.drawable.level);
                    name.put("name",namelistEasy[i]+"     "+tipslistEasy[i]);
                    list.add(name);
                }
                sa=new SimpleAdapter(this,list,R.layout.simple_item,
                        new String[]{"img","name"}, new int[]{R.id.img,R.id.tv});
                lv.setAdapter(sa);
                itChoose=new Intent(this,levelEasy.class);
                itChoose.putExtra("playVoice",it.getStringExtra("playVoice"));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i==5)
                        {
                            if(checkPermission())
                            {
                                //打开系统文件夹并选择文件
                                new AlertDialog.Builder(choose.this)
                                        .setTitle("提示")
                                        .setMessage("请选择/sdcard/LogicalBlock路径下的5*5格子图片\n其他路径或不同规格图片会解析出奇怪图案(⑉･̆-･̆⑉)")
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                itChoose.putExtra("useDiy","yes");
                                                itChoose.putExtra("isSave","no");
                                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                intent.setType("image/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                Log.i("--------------------","打开文件浏览器");
                                                try {
                                                    startActivityForResult(intent,1);
                                                } catch (ActivityNotFoundException e) {
                                                    e.printStackTrace();
                                                    Log.i("----------------","打开失败");
                                                }
                                            }
                                        })
                                        .create().show();
                            }

                        }
                        else {
                            int number=i+1;
                            itChoose.putExtra("name","easy"+number);
                            itChoose.putExtra("useDiy","no");
                            itChoose.putExtra("tip",tipslistEasy[i]);
                            Log.i("--------------","进入关卡");
                            itChoose.putExtra("isSave","no");
                            startActivity(itChoose);
                        }
                    }
                });
                break;
            case "middle":
                for(int i=0;i<6;i++)
                {
                    HashMap<String,Object>name=new HashMap<String,Object>();
                    name.put("img",R.drawable.level);
                    name.put("name",namelistMiddle[i]+"     "+tipslistMiddle[i]);
                    list.add(name);
                }
                sa=new SimpleAdapter(this,list,R.layout.simple_item,
                        new String[]{"img","name"}, new int[]{R.id.img,R.id.tv});
                lv.setAdapter(sa);
                itChoose=new Intent(this,levelMiddle.class);
                itChoose.putExtra("playVoice",it.getStringExtra("playVoice"));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i==5)
                        {
                            if(checkPermission())
                            {
                                //打开系统文件夹并选择文件
                                new AlertDialog.Builder(choose.this)
                                        .setTitle("提示")
                                        .setMessage("请选择/sdcard/LogicalBlock路径下的10*10格子图片\n其他路径或不同规格图片会解析出奇怪图案(⑉･̆-･̆⑉)")
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                itChoose.putExtra("useDiy","yes");
                                                itChoose.putExtra("isSave","no");
                                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                intent.setType("image/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                Log.i("--------------------","打开文件浏览器");
                                                try {
                                                    startActivityForResult(intent,1);
                                                } catch (ActivityNotFoundException e) {
                                                    e.printStackTrace();
                                                    Log.i("----------------","打开失败");
                                                }
                                            }
                                        })
                                        .create().show();
                            }
                        }
                        else {
                            int number=i+1;
                            itChoose.putExtra("useDiy","no");
                            itChoose.putExtra("name","middle"+number);
                            itChoose.putExtra("tip",tipslistMiddle[i]);
                            Log.i("--------------","进入关卡");
                            itChoose.putExtra("isSave","no");
                            startActivity(itChoose);
                        }
                    }
                });
                break;
            case "hard":
                for(int i=0;i<8;i++)
                {
                    HashMap<String,Object>name=new HashMap<String,Object>();
                    name.put("img",R.drawable.level);
                    name.put("name",namelistHard[i]+"     "+tipslistHard[i]);
                    list.add(name);
                }
                sa=new SimpleAdapter(this,list,R.layout.simple_item,
                        new String[]{"img","name"}, new int[]{R.id.img,R.id.tv});
                lv.setAdapter(sa);
                itChoose=new Intent(this, levelHard.class);
                itChoose.putExtra("playVoice",it.getStringExtra("playVoice"));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(i==7)
                        {
                            if (checkPermission())
                            {
                                //打开系统文件夹并选择文件
                                new AlertDialog.Builder(choose.this)
                                        .setTitle("提示")
                                        .setMessage("请选择/sdcard/LogicalBlock路径下的15*15格子图片\n其他路径或不同规格图片会解析出奇怪图案(⑉･̆-･̆⑉)")
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                itChoose.putExtra("useDiy","yes");
                                                itChoose.putExtra("isSave","no");
                                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                intent.setType("image/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                Log.i("--------------------","打开文件浏览器");
                                                try {
                                                    startActivityForResult(intent,1);
                                                } catch (ActivityNotFoundException e) {
                                                    e.printStackTrace();
                                                    Log.i("----------------","打开失败");
                                                }
                                            }
                                        })
                                        .create().show();
                            }
                        }else
                        {
                            int number=i+1;
                            itChoose.putExtra("useDiy","no");
                            itChoose.putExtra("name","hard"+number);
                            itChoose.putExtra("tip",tipslistHard[i]);
                            Log.i("--------------","进入关卡");
                            itChoose.putExtra("isSave","no");
                            startActivity(itChoose);
                        }

                    }
                });
                break;
        }
    }

    //按钮返回事件
    public void back(View view)
    {
        this.finish();
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
                Toast.makeText(choose.this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_LONG).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(choose.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
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

    //选择文件后的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            uri = data.getData();//得到uri
            String imgPath=getPath(this,uri);
            itChoose.putExtra("imgPath",imgPath);
            Log.i("------------------","解析得到path:"+imgPath);
            startActivity(itChoose);
        }
    }


    //uri解析为sd卡文件路径
    //4.4以后uri的格式有所变化，需要转化一下
    //来源文章https://blog.csdn.net/qq_30432883/article/details/80262261
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        //文档解析
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            //外部存储解析
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            //下载地址解析
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            //媒体解析
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        //媒体存储
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        //文件夹解析
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    //获取数据字段
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs)
    {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    //判断uri类型
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
