package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBase extends SQLiteOpenHelper {
    private static final String TABLE_NAME_AUTO = "Auto";
    private static final String TABLE_NAME_USERS = "Users";

    private static final String USER_LOGIN = "Login";
    private static final String USER_PASSWORD = "Password";
    private static final String USER_MAIL = "Mail";
    private static final String USER_PHONE = "Phone";
    private static final String USER_BONUSES = "Bonuses";

    private static String DB_PATH;
    private static final String DB_NAME = "carsharing.db";
    private static final int DB_VERSION = 1;
    private Context context;
    public DataBase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context=context;
        DB_PATH = context.getDatabasePath(DB_NAME).getAbsolutePath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_AUTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERS);
        onCreate(db);
    }

    public void addUser(String login, String mail, String phone, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USER_LOGIN, login);
        cv.put(USER_PASSWORD, password);
        cv.put(USER_MAIL, mail);
        cv.put(USER_PHONE, phone);
        cv.put(USER_BONUSES, 0);
        long result = db.insert(TABLE_NAME_USERS, null, cv);
        if(result == -1)
            Toast.makeText(context, "Ошибка при регистрации!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Вы успешно зарегистрированы!", Toast.LENGTH_LONG).show();
    }

    public void deleteUser(String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME_USERS, "Phone=?", new String[]{phone});
        if(result == -1)
            Toast.makeText(context, "Ошибка при удалении аккаунта", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Успешно удалено", Toast.LENGTH_LONG).show();
    }

    public Cursor readAllData(boolean isOneID, String id){
        String query;
        SQLiteDatabase db = this.getReadableDatabase();

        if(isOneID)
            query = "SELECT * FROM " + TABLE_NAME_AUTO +" WHERE id = " + id;
        else
            query = "SELECT * FROM " + TABLE_NAME_AUTO +" ORDER BY Name_auto";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("DataBase", "Data found in cursor");
        } else {
            Log.d("DataBase", "No data found in cursor");
        }


        return cursor;
    }

    public void setDataCar(String id, String status){
        String strSQL = "UPDATE "+ TABLE_NAME_AUTO +" SET Status = " + status + " WHERE id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL(strSQL);
    }

    public String readDataUser(String column, String phone){
        String output;
        String query = "SELECT " + column + " FROM " + TABLE_NAME_USERS + " WHERE " + USER_PHONE +" LIKE '" + phone+"'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst())
            output = String.valueOf(cursor.getString(0));
        else output = "NULL";

        db.close();
        return output;
    }

    public boolean FindUser(String phone, String password){
        boolean isFind;
        String query = "SELECT * FROM " + TABLE_NAME_USERS + " WHERE " + USER_PHONE +" LIKE '" + phone+"' AND " + USER_PASSWORD + " LIKE '" + password + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst())
            isFind = true;
        else isFind = false;

        db.close();
        return isFind;
    }

    public Cursor getLatAndLong()
    {
        String query = "SELECT * FROM " + TABLE_NAME_AUTO;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void create_db(){
        File file = new File(DB_PATH);
        if (!file.exists()) {
            try(InputStream myInput = context.getAssets().open(DB_NAME);
                OutputStream myOutput = new FileOutputStream(DB_PATH)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }
    public SQLiteDatabase open()throws SQLException {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
