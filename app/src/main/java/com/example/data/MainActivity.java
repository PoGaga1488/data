package com.example.data;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText titleInput, noteInput, deleteIdInput, updateIdInput, updateNoteInput;
    private Button saveButton, deleteButton, updateButton;
    private TextView notesDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        titleInput = findViewById(R.id.titleInput);
        noteInput = findViewById(R.id.noteInput);
        deleteIdInput = findViewById(R.id.deleteIdInput);
        updateIdInput = findViewById(R.id.updateIdInput);
        updateNoteInput = findViewById(R.id.updateNoteInput);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        notesDisplay = findViewById(R.id.notesDisplay);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });

        // Wyświetl notatki przy starcie
        loadNotes();
    }

    private void addNote() {
        String titleText = titleInput.getText().toString();
        String noteText = noteInput.getText().toString();
        if (titleText.isEmpty() || noteText.isEmpty()) {
            return;
        }

        // Uzyskanie dostępu do bazy danych w trybie zapisu
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Użycie ContentValues do bezpiecznego wstawiania danych
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, titleText);
        values.put(DatabaseHelper.COLUMN_NOTE, noteText);

        // Wstawienie nowego wiersza
        db.insert(DatabaseHelper.TABLE_NOTES, null, values);
        db.close();

        // Wyczyść pole i odśwież listę
        titleInput.setText("");
        noteInput.setText("");
        loadNotes();
    }

    private void loadNotes() {
        // Uzyskanie dostępu do bazy danych w trybie odczytu
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Zapytanie, które pobierze wszystkie dane z tabeli
        String[] projection = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_NOTE};
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                projection,
                null, null, null, null, null
        );

        StringBuilder notes = new StringBuilder();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
            String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));
            notes.append(id).append(". ").append(title).append(":\n").append(note).append("\n\n");
        }
        cursor.close();
        db.close();

        notesDisplay.setText(notes.toString());
    }

    private void deleteNote() {
        String idToDelete = deleteIdInput.getText().toString();
        if (idToDelete.isEmpty()) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID + " = ?", new String[]{idToDelete});
        db.close();

        deleteIdInput.setText("");
        loadNotes();
    }

    private void updateNote() {
        String idToUpdate = updateIdInput.getText().toString();
        String newText = updateNoteInput.getText().toString();
        if (idToUpdate.isEmpty() || newText.isEmpty()) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NOTE, newText);

        db.update(DatabaseHelper.TABLE_NOTES, values, DatabaseHelper.COLUMN_ID + " = ?", new String[]{idToUpdate});
        db.close();

        updateIdInput.setText("");
        updateNoteInput.setText("");
        loadNotes();
    }
}
