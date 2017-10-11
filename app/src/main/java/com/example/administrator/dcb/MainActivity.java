package com.example.administrator.dcb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView=(ListView)findViewById(R.id.list);
        final TextView word=(TextView)findViewById(R.id.word);
        final TextView wordmeaning=(TextView)findViewById(R.id.wordmeaning);
        final TextView wordsample =(TextView)findViewById(R.id.wordsample);
        final String[] array={"小米","华为","魅族","锤子"};

        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String> (this,R.layout.item,array);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(MainActivity.this,array[i],Toast.LENGTH_LONG).show();
                word.setText(array[i]);
                wordmeaning.setText("meaning:\n"+array[i]);
                wordsample.setText("sample\n"+array[i]);
            }
        });
    }
}
