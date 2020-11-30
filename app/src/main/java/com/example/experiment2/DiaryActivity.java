package com.example.experiment2;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {
    private CreateSQL dbHelper;
    private List<diary> diaryList=new ArrayList<>();
    private TextView t1;
    private diary diary;

    @Override
    protected void onStart() {
        super.onStart();
        dbHelper =new CreateSQL(this,"DiaryBook.db",null,2);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        diaryList.clear();
        Cursor cursor=db.query("diary",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title=cursor.getString(cursor.getColumnIndex("title"));
                long time=cursor.getLong(cursor.getColumnIndex("time"));
                SimpleDateFormat time2=new SimpleDateFormat("yyyy-MM--dd HH:mm:ss");
                String date=time2.format(new Date(time));
                diary=new diary(id,title,date);
                diaryList.add(diary);
            }while (cursor.moveToNext());
        }
        diaryAdapter adapter=new diaryAdapter(DiaryActivity.this,R.layout.diary_every,diaryList);
        ListView listView=(ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        t1 = findViewById(R.id.mytitle);
        t1.setText(getIntent().getStringExtra("username")+"的日记本");
        ImageButton back = (ImageButton)findViewById(R.id.btn1);
        ImageButton commit = (ImageButton)findViewById(R.id.commit);
        final ListView listView=(ListView) findViewById(R.id.list_view);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //对ListView监听，如果点击则传值，并跳转
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                diary diary=diaryList.get(position);
                Intent intent=new Intent(DiaryActivity.this,editActivity.class);//跳转到日记内容页面
                intent.putExtra("id",diary.getId());        //转送listView位置
                intent.putExtra("title", diary.getTitle());  //传送标题名字
                Log.d("title-----------",diary.getTitle());
                Log.d("id-----------",String.valueOf(diary.getId()));
                startActivity(intent);
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryActivity.this,AddActivity.class);
                DiaryActivity.this.startActivity(intent);
            }
        });
        //长按监听
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                //长按则显示出一个对话框
                new AlertDialog.Builder(DiaryActivity.this)
                        .setTitle("删除提示") //无标题
                        .setMessage("是否删除日记") //内容
                        .setNegativeButton("取消",null) //连个按钮
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            //删除键
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                diary diary=diaryList.get(position);
                                dbHelper =new CreateSQL(DiaryActivity.this,"DiaryBook.db",null,2);
                                SQLiteDatabase db=dbHelper.getWritableDatabase();
                                String[] strings = {String.valueOf(diary.getId())}; //获取删除的数据库主键
                                int flag = db.delete("diary", "id=?",strings);    //把对应数据删除
                                Cursor cursor=db.query("diary",null,"id>?",strings,null,null,null);
                                int id1 = 0;
                                while (cursor.moveToNext()) {
                                    id1 = cursor.getInt(cursor.getColumnIndex("id"));
                                    //将查出来的这条数据的状态改为已读，然后插入数据库
                                    ContentValues contentValues = new ContentValues();
                                    //将要改的字段和要改的内容放放到contentValues
                                    contentValues.put("id", id1 - 1);
                                    //表明     要改的字段和要改的内容    要改哪条数据（此处要改id=ID的那条数据）
                                    long dd = db.update("diary", contentValues, "id=?", new String[]{String.valueOf(id1)});
                                }

                                Cursor cursor1=db.query("diary",null,null,null,null,null,null);
                                diaryList.clear();  //清空当前ListView重新写入
                                if(cursor1.moveToFirst()){
                                    do{
                                        int id = cursor1.getInt(cursor1.getColumnIndex("id"));
                                        String title=cursor1.getString(cursor1.getColumnIndex("title"));
                                        long   time1=cursor1.getLong(cursor1.getColumnIndex("time"));
                                        SimpleDateFormat time2=new SimpleDateFormat("yyyy-MM--dd HH:mm:ss");
                                        String time=time2.format(new Date(time1));
                                        diary=new diary(id,title,time);
                                        diaryList.add(diary);
                                    }while (cursor1.moveToNext());
                                }
                                diaryAdapter adapter=new diaryAdapter(DiaryActivity.this,R.layout.diary_every,diaryList);
                                ListView listView=(ListView) findViewById(R.id.list_view);

                                listView.setAdapter(adapter);
                            }
                        }).show();
                return false;
            }
        });





    }
}
class diaryAdapter extends ArrayAdapter<diary> {
    private int resourceId;
    public diaryAdapter(@NonNull Context context, int resource, List<diary> objects) {
        super(context, resource,objects);
        resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        diary diary=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView item_title=(TextView) view.findViewById(R.id.item_title);
        TextView item_time=(TextView) view.findViewById(R.id.item_time);
        TextView item_id=(TextView) view.findViewById(R.id.item_id);
        item_id.setText(String.valueOf(diary.getId()));
        item_title.setText(diary.getTitle());
        item_time.setText(diary.getDate());
        return view;
    }
}