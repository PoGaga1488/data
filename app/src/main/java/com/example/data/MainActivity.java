package com.example.data;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText titleInput, noteInput;
    private Button saveButton;
    private RecyclerView notesRecyclerView;
    private TextView emptyMessage;

    private NoteAdapter adapter;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        titleInput = findViewById(R.id.titleInput);
        noteInput = findViewById(R.id.noteInput);
        saveButton = findViewById(R.id.saveButton);

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        emptyMessage = findViewById(R.id.emptyMessage);

        noteList = new ArrayList<>();
        adapter = new NoteAdapter(noteList, this::deleteNote);

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(v -> addNote());

        loadNotes();
    }

    private void addNote() {
        String title = titleInput.getText().toString();
        String text = noteInput.getText().toString();

        if (title.isEmpty() || text.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_NOTE, text);

        db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();

        titleInput.setText("");
        noteInput.setText("");

        loadNotes();
    }

    private void loadNotes() {
        noteList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_ID + " DESC"   // ➜ Zadanie 5
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));

            noteList.add(new Note(id, title, text));
        }

        cursor.close();
        db.close();

        adapter.notifyDataSetChanged();


        if (noteList.isEmpty()) {
            notesRecyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            notesRecyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }
    }


    private void deleteNote(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();

        loadNotes();
    }
}

