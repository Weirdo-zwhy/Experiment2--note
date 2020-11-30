package com.example.experiment2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class editActivity extends AppCompatActivity {

    private EditText title2;
    private EditText body2;
    private CreateSQL dbHelper;
    private ImageButton btn2;
    private ImageButton btn3;
    private ImageView imageView;
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;
    public static String imagePath =null; //定义一个全局变量，把图片路径变为string保存到数据库中
    public static final int CHOOSE_PHOTO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        btn2= (ImageButton)findViewById(R.id.btn2);
        btn3= (ImageButton)findViewById(R.id.btn3);
        final Intent intent=getIntent();
        imageView= (ImageView)findViewById(R.id.picture);
        Button btn4 = (Button)findViewById(R.id.btn4);
        dbHelper =new CreateSQL(this,"DiaryBook.db",null,2);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ImageButton back = (ImageButton)findViewById(R.id.btn1);
        ImageButton commit = (ImageButton)findViewById(R.id.commit);
        title2=(EditText)findViewById(R.id.title_text);
        body2=(EditText)findViewById(R.id.body_text);
        final int id1= intent.getIntExtra("id",-1);
        //已有日记显示
        Cursor cursor=db.query("diary",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            Log.d("数据表的列数", String.valueOf(cursor.getColumnCount()));
            Log.d("author-----",String.valueOf(cursor.getString(1)));
            Log.d("title-----",String.valueOf(cursor.getString(2)));
            Log.d("body-----",String.valueOf(cursor.getString(3)));

            Log.d("传入的id值", String.valueOf(id1));
            Log.d("count-----",String.valueOf(cursor.getCount()));
            do{
                //获取
                //通过DiaryTitleActivity传来的位置找到数据库对应的行
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                Log.d("id", String.valueOf(id));
                if(id==id1){
                    String url = cursor.getString(cursor.getColumnIndex("photoURL"));
                    String title1=cursor.getString(cursor.getColumnIndex("title")); //取出数据库标题
                    String body1=cursor.getString(cursor.getColumnIndex("body"));//取出数据库内容

                    if(url != null){
                        Log.d("file--------",url);
                        Uri uri = Uri.parse(url);
                        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
                        displayImage(url); // 根据图片路径显示图片
                    }
                    title2.setText(title1); //添加到标题文本
                    body2.setText(body1);//添加到内容文本
                    break;
                }
            }while (cursor.moveToNext());
        }
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePath=null;
                imageView.setImageDrawable(null);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 利用系统自带的相机应用:拍照
//                 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null
//                 如果此处指定，则后来的data为null
//                 只有指定路径才能获取原图
                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
                File outputImage = new File(getExternalCacheDir(),fileName);
                Log.d("file--------",outputImage.toString());
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }

                imageUri = FileProvider.getUriForFile(editActivity.this,"com.example.experiment2.fileProvider",outputImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }

        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(editActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(editActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("title",title2.getText().toString());
                values.put("body",body2.getText().toString());
                values.put("photoURL",imagePath);
                String string1=title2.getText().toString();
                String string2=body2.getText().toString();
                int flag=db.update("diary",values,"id = ?", new String[]{String.valueOf(id1)});
                Toast.makeText(editActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requstCode,String[] permissions,int[] grantResults) {
        switch (requstCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }else {
                    Toast.makeText(this,"you denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    //把拍照照片存到相册
    public void saveToSystemGallery(Bitmap bmp) {
        // 首先保存图片
        String fileName = "mnt/sdcard/Mycamera/IMG_" + System.currentTimeMillis() + ".jpg";
//        File file = new File(appDir, fileName);
        File file = new File(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        imagePath = uri.getPath();
        intent.setData(uri);
        Log.d("zzzzzzzzzzz1",imagePath);
        Log.d("zzzzzzzzzzz2",uri.toString());
        sendBroadcast(intent);// 发送广播，通知图库更新
    }
    //拍照完以后返回到这儿
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {//如果拍照成功
                    try {
                        //将拍摄照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));//把这张照片转换为Bitmap对象
                        saveToSystemGallery(bitmap);
                        imageView.setImageBitmap(bitmap);//显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(Build.VERSION.SDK_INT >=19) {
                    handleImageOnKitKat(data);
                }else {
                    handleImageBeforeKitKat(data);
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        // String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);

        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
                File file = new File(imagePath);
                if (file.exists()) {
                    try {
                        FileInputStream fis = new FileInputStream(imagePath);
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        Log.d("bitmap",bitmap.toString());
                        Log.d("imagepath",imagePath);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }



}