package com.hummer.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hummer.core.component.network.IRequestCallback;
import com.hummer.demo.loader.HMJavaScriptLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> stringArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stringArr = new ArrayList();
        stringArr.add("Android");
        stringArr.add("iPhone");

        initView();
        loadFiles();
    }

    private void initView() {
        ListView listView = findViewById(R.id.list_view);
        //添加点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = stringArr.get(position);
                DemoActivity.launch(MainActivity.this, Constant.HOST + itemName);
            }
        });

        arrayAdapter = new ArrayAdapter<>(this, R.layout.mylist, R.id.text, stringArr);
        listView.setAdapter(arrayAdapter);
    }

    /**
     * 加载js文件列表
     */
    private void loadFiles(){
        HMJavaScriptLoader.loadBundleWithURL(Constant.HOST+"all_files", new IRequestCallback() {
            @Override
            public void onComplete(String result) {
                String jsonString = result;
                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    String[] strings = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        strings[i] = jsonArray.getString(i);
                    }
                    Arrays.sort(strings);

                    Log.i("MainActivity","++++++++>>>>strings:" + Arrays.toString(strings));
                    reloadData(strings);
                } catch (final JSONException e) {
                    Log.e("MainActivity","++++++++>>>>:" + e.getMessage());
                }

            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity","++++++++>>>>:" + e.getMessage());
            }
        });
    }

    /**
     * 更新list 数据
     * @param strings list数据源
     */
    private void reloadData (String[] strings) {
        if (strings.length == 0) return;
        stringArr.clear();
        for (String s:strings) {
            stringArr.add(s);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }
}

