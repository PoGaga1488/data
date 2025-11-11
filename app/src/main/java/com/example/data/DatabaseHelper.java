package com.example.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nazwa i wersja bazy danych
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2;

    // Nazwa tabeli i jej kolumn
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NOTE = "note";

    // Zapytanie SQL do stworzenia tabeli
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_NOTE + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Wykonanie zapytania tworzącego tabelę
        db.execSQL(TABLE_CREATE);
        Log.d("DatabaseHelper", "onCreate: Tworzenie bazy danych");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Przy prostej aktualizacji można usunąć starą tabelę i stworzyć nową
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
        Log.d("DatabaseHelper", "onUpgrade: Aktualizacja bazy danych");
    }
}
