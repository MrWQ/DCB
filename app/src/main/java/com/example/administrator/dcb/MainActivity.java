package com.example.administrator.dcb;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.EmptyStackException;

public class MainActivity extends AppCompatActivity {
    private Sqlitehelper sqlitehelper = new Sqlitehelper(this);
    private SQLiteDatabase sqLiteDatabase;
//    final View fragment=(View)findViewById(R.id.wordfragment);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ArrayList arrayList = new ArrayList();
        final ListView listView = (ListView) findViewById(R.id.list);
        final TextView word=(TextView)findViewById(R.id.word);
        final TextView wordmeaning=(TextView)findViewById(R.id.wordmeaning);
        final TextView wordsample =(TextView)findViewById(R.id.wordsample);
        Button insert = (Button) findViewById(R.id.insert);
        Button show = (Button) findViewById(R.id.show);
        Button delete = (Button) findViewById(R.id.delete);
        registerForContextMenu(listView);//注册上下文菜单
//        final String[] array={"小米","华为","魅族","锤子"};

//        arrayList.add("小米");
//        arrayList.add("华为");
//        arrayList.add("魅族");
//        arrayList.add("锤子");
        sqLiteDatabase = sqlitehelper.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery("select * from words", null);
        if (cursor.moveToNext()) {
            do {
                arrayList.add(cursor.getString(cursor.getColumnIndex("word")));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(MainActivity.this, "null", Toast.LENGTH_LONG).show();
        }
        sqLiteDatabase.close();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, arrayList);
        listView.setAdapter(arrayAdapter);


        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase = sqlitehelper.getWritableDatabase();
                sqLiteDatabase.execSQL("insert into words(word,meaning,smaple) values('apple','苹果','this is apple')");
                sqLiteDatabase.execSQL("insert into words(word,meaning,smaple) values('orange','橘子','this is orange')");
                Toast.makeText(MainActivity.this, "inserted", Toast.LENGTH_LONG).show();
                sqLiteDatabase.close();
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sqLiteDatabase = sqlitehelper.getReadableDatabase();
                    Cursor cursor = sqLiteDatabase.rawQuery("select * from words", null);
                    cursor.moveToFirst();
                    do {
                        Toast.makeText(MainActivity.this, cursor.getString(cursor.getColumnIndex("id")) + cursor.getString(cursor.getColumnIndex("word")) + cursor.getString(cursor.getColumnIndex("meaning")) + cursor.getString(cursor.getColumnIndex("smaple")), Toast.LENGTH_LONG).show();
                        word.setText(cursor.getString(cursor.getColumnIndex("word")));
                        wordmeaning.setText(cursor.getString(cursor.getColumnIndex("meaning")));
                        wordsample.setText(cursor.getString(cursor.getColumnIndex("smaple")));


                    } while (cursor.moveToNext());

                    sqLiteDatabase.close();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "null", Toast.LENGTH_LONG).show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase = sqlitehelper.getWritableDatabase();
                sqLiteDatabase.execSQL("delete from words");
                sqLiteDatabase.close();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                sqLiteDatabase=sqlitehelper.getReadableDatabase();
//                Cursor cursor1=sqLiteDatabase.rawQuery("select * from words",null);
//                if(cursor1.moveToNext())

                word.setText(arrayList.get(i).toString());
                sqLiteDatabase = sqlitehelper.getReadableDatabase();
                Cursor cursor1 = sqLiteDatabase.rawQuery("select * from words where word = ?", new String[]{arrayList.get(i).toString()});
                if (cursor1.moveToNext()) {
                    wordmeaning.setText("meaning:\n" + cursor1.getString(cursor1.getColumnIndex("meaning")));
                    wordsample.setText("smaple\n" + cursor1.getString(cursor1.getColumnIndex("smaple")));
                }
                sqLiteDatabase.close();

            }
        });
    }

    //实现上下文菜单 长按时创建菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_content, menu);
    }

    //上下文菜单 菜单选择  点击事件处理
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change:
                try {
                    AlertDialog.Builder builder_change = new AlertDialog.Builder(this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View view = inflater.inflate(R.layout.change_dialog, null);

                    builder_change.setView(view).setTitle("更改")
                            .setNegativeButton("更改", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText oldword = (EditText) view.findViewById(R.id.oldword);
                                    EditText newword = (EditText) view.findViewById(R.id.newword);
                                    EditText newmeaning = (EditText) view.findViewById(R.id.newmeaning);
                                    EditText newsmaple = (EditText) view.findViewById(R.id.newsmaple);
                                    String newwordstr = newword.getText().toString();
                                    String newmeaningstr = newmeaning.getText().toString();
                                    String newsmaplestr = newsmaple.getText().toString();
                                    //判断要更改的单词是不是为空
                                    if (oldword.getText().toString().equals("")) {
                                        Toast.makeText(MainActivity.this, "请输入要更改的单词", Toast.LENGTH_LONG).show();
                                    } else {
                                        sqLiteDatabase = sqlitehelper.getWritableDatabase();
                                        Cursor cursor1 = sqLiteDatabase.rawQuery("select * from words where word = ?", new String[]{oldword.getText().toString()});
                                        if (cursor1.moveToNext()) {
                                            if (newwordstr.equals("") && newmeaningstr.equals("") && newsmaplestr.equals("")) {
                                                Toast.makeText(MainActivity.this, "没有更改", Toast.LENGTH_LONG).show();
                                            } else {
                                                //不为空就更改
                                                if (!newwordstr.equals("")) {
                                                    Toast.makeText(MainActivity.this, "newword 更改", Toast.LENGTH_LONG).show();
                                                }
                                                if (!newmeaningstr.equals("")) {
                                                    Toast.makeText(MainActivity.this, "newmeaning 更改", Toast.LENGTH_LONG).show();
                                                }
                                                if (!newmeaningstr.equals("")) {
                                                    Toast.makeText(MainActivity.this, "newsmaple 更改", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "要更改的单词不存在", Toast.LENGTH_LONG).show();
                                        }

                                    }

                                }
                            })
                            .setPositiveButton("取消", null);
                    builder_change.show();
                } catch (Exception e) {
                }

                break;
            case R.id.delete:
                AlertDialog.Builder builder_delete = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.delete_dialog, null);

                builder_delete.setView(view).setTitle("删除")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    EditText delete = (EditText) view.findViewById(R.id.delete);
                                    final String deleteword = delete.getText().toString();
                                    if (deleteword.equals("")) {//输入为空
                                        Toast.makeText(MainActivity.this, "请输入要删除的单词", Toast.LENGTH_LONG).show();
                                    } else {
                                        sqLiteDatabase = sqlitehelper.getWritableDatabase();
                                        Cursor cursor1 = sqLiteDatabase.rawQuery("select * from words where word = ?", new String[]{deleteword});
                                        if (cursor1.moveToNext()) {
                                            //提示对话框，提示用户是否确定要删除
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setMessage("确定要删除吗").setTitle("提示")
                                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            try {
                                                                sqLiteDatabase = sqlitehelper.getWritableDatabase();
                                                                sqLiteDatabase.execSQL("delete from words where word=?", new String[]{deleteword});
                                                                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                                                            } catch (Exception e) {
                                                                Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    })
                                                    .setPositiveButton("取消", null);
                                            builder.show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "要删除的单词不存在", Toast.LENGTH_LONG).show();
                                        }
                                        sqLiteDatabase.close();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "抱歉出错了", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setPositiveButton("取消", null);
                builder_delete.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    //创建选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //选项菜单 点击事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select:
                try {
                    AlertDialog.Builder builder_select = new AlertDialog.Builder(this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View view = inflater.inflate(R.layout.select_dialog, null);

                    builder_select.setView(view).setTitle("查询")
                            .setNegativeButton("查询", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText select = (EditText) view.findViewById(R.id.select_dialog_select);

//                                    TextView word=(TextView)findViewById(R.id.word);
//                                    TextView wordmeaning=(TextView)findViewById(R.id.wordmeaning);
//                                    TextView wordsample=(TextView)findViewById(R.id.wordsample);
                                    String str = select.getText().toString();
//                                    Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();

                                    sqLiteDatabase = sqlitehelper.getReadableDatabase();
                                    Cursor cursor = sqLiteDatabase.rawQuery("select * from words where word=?", new String[]{str});
                                    if (cursor.moveToNext()) {
                                        Toast.makeText(MainActivity.this, cursor.getString(cursor.getColumnIndex("word")) + "\n" + cursor.getString(cursor.getColumnIndex("meaning")) + "\n" + cursor.getString(cursor.getColumnIndex("smaple")), Toast.LENGTH_LONG).show();

//                                            word.setText(cursor.getString(cursor.getColumnIndex("word")));
//                                            wordmeaning.setText(cursor.getString(cursor.getColumnIndex("meaning")));
//                                            wordsample.setText(cursor.getString(cursor.getColumnIndex("smaple")));
                                        //缺少显示全部内容
                                    } else {
                                        Toast.makeText(MainActivity.this, "没有该单词", Toast.LENGTH_LONG).show();
                                    }
                                    sqLiteDatabase.close();
                                }
                            })
                            .setPositiveButton("取消", null);
                    builder_select.show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "出现错误", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.insert:
                try {
                    AlertDialog.Builder builder_insert = new AlertDialog.Builder(this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View view = inflater.inflate(R.layout.insert_dialog, null);

                    builder_insert.setView(view).setTitle("新增")
                            .setPositiveButton("取消", null)
                            .setNegativeButton("新增", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText insertword = (EditText) view.findViewById(R.id.insert_dialog_word);
                                    EditText insertmeaning = (EditText) view.findViewById(R.id.insert_dialog_meaning);
                                    EditText insertsmaple = (EditText) view.findViewById(R.id.insert_dialog_smaple);
                                    String word = insertword.getText().toString();
                                    String meaning = insertmeaning.getText().toString();
                                    String smaple = insertsmaple.getText().toString();
                                    //先判断输入是否为空
                                    if (word.equals("") == false && meaning.equals("") == false && smaple.equals("") == false) {//都不为空
                                        //新增之前先查询是否存在该单词，存在 提示，不存在 插入该单词
                                        sqLiteDatabase = sqlitehelper.getWritableDatabase();
                                        sqLiteDatabase.execSQL("insert into words(word,meaning,smaple) values(?,?,? )", new String[]{word, meaning, smaple});
                                        sqLiteDatabase.close();
                                        Toast.makeText(MainActivity.this, "已新增", Toast.LENGTH_LONG).show();
                                        //缺少 刷新列表
                                    } else {
                                        Toast.makeText(MainActivity.this, "请输入", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                    builder_insert.show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "出现错误", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
