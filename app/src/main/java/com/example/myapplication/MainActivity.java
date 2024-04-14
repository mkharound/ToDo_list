package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private ListView listViewTasks;
    private Button btnAdd, btnSave;

    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "todo_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        listViewTasks = findViewById(R.id.listViewTasks);
        btnAdd = findViewById(R.id.btnAdd);
        btnSave = findViewById(R.id.btnSave);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        tasks = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        listViewTasks.setAdapter(adapter);

        // Add task
        btnAdd.setOnClickListener(view -> {
            String task = editTextTask.getText().toString();
            // Null check
            if (!task.isEmpty()) {
                tasks.add(task);
                adapter.notifyDataSetChanged();
                editTextTask.getText().clear();
            }
        });

        // Save all tasks
        btnSave.setOnClickListener(view -> saveTasks());

        // When pressing the task
        listViewTasks.setOnItemClickListener((adapterView, view, position, id) -> {
            TextView textView = (TextView) view;
            String task = tasks.get(position);
            // If task is not completed, complete on press, else uncomplete on press.
            if (textView.getPaint().isStrikeThruText()) {
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });

        // Long press for removing task
        listViewTasks.setOnItemLongClickListener((adapterView, view, position, id) -> {
            tasks.remove(position);
            adapter.notifyDataSetChanged();
            return true;
        });

        loadTasks();
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> taskSet = new HashSet<>(tasks);
        editor.putStringSet("tasks", taskSet);
        editor.apply();
    }

    private void loadTasks() {
        Set<String> taskSet = sharedPreferences.getStringSet("tasks", null);
        if (taskSet != null) {
            tasks.clear();
            tasks.addAll(taskSet);
            adapter.notifyDataSetChanged();
        }
    }
}
