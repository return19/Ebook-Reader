package com.example.abhinandan.ebook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    Button bt_speak,btnNext,btnSelect;
    TextView speech_text;
    ListView lv_files;
    int curSelection=-1;
    ArrayList< Pair<String,String> > filesInFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_speak=(Button)findViewById(R.id.bt_speak);
        btnNext=(Button)findViewById(R.id.btnNext);
        btnSelect=(Button)findViewById(R.id.btnSelect);
        speech_text=(TextView)findViewById((R.id.speech_text));
        lv_files=(ListView)findViewById(R.id.lv_files);

        bt_speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSpeechInput();
            }
        });

        lv_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(),player.class);
                intent.putExtra("songName",filesInFolder.get(position).first);
                intent.putExtra("songPath",filesInFolder.get(position).second);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSelection();
            }
        });
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),player.class);
                intent.putExtra("songName",filesInFolder.get(curSelection).first);
                intent.putExtra("songPath",filesInFolder.get(curSelection).second);
                startActivity(intent);
            }
        });

        fillListView();
        nextSelection();
    }

    public void nextSelection(){
        curSelection++;
        if(curSelection == filesInFolder.size())
            curSelection=0;
        lv_files.requestFocusFromTouch();
        lv_files.setSelection(curSelection);
        lv_files.requestFocus();
    }

    public  void fillListView(){
        String sd_path= Environment.getExternalStorageDirectory().getAbsolutePath();
        filesInFolder = FileCrawler.GetFiles(sd_path +"/ebook");
        ArrayList<String> fileName= new ArrayList<String>();

        for( Pair<String,String> p : filesInFolder)
            fileName.add(p.first);

        lv_files.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileName));
    }

    // call this function whenever speech input is required
    public void takeSpeechInput(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something");

        try{
            startActivityForResult(intent,100);

        }catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(),"Speech support not found",Toast.LENGTH_SHORT).show();
        }
    }

    // result of startActivityforResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if(resultCode==RESULT_OK && data!=null)
                {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speech_text.setText(result.get(0));
                }
            }
            break;
        }
    }
}
