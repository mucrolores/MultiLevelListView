package com.example.multilevellistview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.multilevellistview.Constants.MAIN_ACTIVITY_TAG;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private MultiLevelListViewAdapter multiLevelListViewAdapter;

    private final String defaultData = "{\"title\":\"/\",\"children\":[{\"title\":\"1\",\"children\":[{\"title\":\"1-1\"},{\"title\":\"1-2\"},{\"title\":\"1-3\"}]},{\"title\":\"2\",\"children\":[{\"title\":\"2-1\"},{\"title\":\"2-2\"},{\"title\":\"2-3\"}]},{\"title\":\"3\",\"children\":[{\"title\":\"3-1\",\"children\":[{\"title\":\"3-1-1\",\"children\":[{\"title\":\"3-1-1-1\"}]}]}]}]}";

    private File dataFile;
    private String jsonStructureString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialUserInterface();
        initialFile();

        Log.d(MAIN_ACTIVITY_TAG,"test");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        multiLevelListViewAdapter = new MultiLevelListViewAdapter(MainActivity.this, jsonStructureString, displayMetrics);
        listView.setAdapter(multiLevelListViewAdapter);
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            MultiLevelListViewItem target = ((MultiLevelListViewAdapter)listView.getAdapter()).getClickedItem(position);

            // Toast the leaf node data
            if(!target.hasChildren()) {
                StringBuilder stringBuilder = new StringBuilder();
                ArrayList<String> targetParentPath = target.getParentPath();
                for(String path : targetParentPath) {
                    stringBuilder.append(path).append(", ");
                }
                stringBuilder.append(target.getTitle());
                Toast.makeText(MainActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            }

            // update clicked view
            ((MultiLevelListViewAdapter)listView.getAdapter()).updateView(position);

        });
    }

    @Override
    protected void onStop() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile));

            bufferedWriter.write(multiLevelListViewAdapter.getAdapterJsonString());
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    private void initialUserInterface() {
        listView = findViewById(R.id.expandableListView_ID);
    }

    private void initialFile() {
        try {
            dataFile = new File(this.getFilesDir(),"dataFile.json");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile));

            StringBuilder tmp = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                tmp.append(line);
            }
            bufferedReader.close();
            if(tmp.length() > 0) {
                jsonStructureString = tmp.toString();
            }
            else {
                jsonStructureString = defaultData;
            }

        } catch (IOException e) {
            logMessage(e.toString());
            e.printStackTrace();
        }
    }

    private void logMessage(String message) {
        Log.d(MAIN_ACTIVITY_TAG, message);
    }

}