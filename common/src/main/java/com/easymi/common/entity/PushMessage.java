package com.easymi.common.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.db.SqliteHelper;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

public class PushMessage {

    public String data;
    public long timeStamp;

    public static void save(PushBean data) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("data", new Gson().toJson(data));
        values.put("timeStamp", System.currentTimeMillis());
        db.insert("t_cp_temp_message", null, values);
    }

    public void update() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("data", data);
        values.put("timeStamp", timeStamp);
        db.update("t_cp_temp_message", values, "timeStamp = ?",
                new String[]{String.valueOf(timeStamp)});
    }

    public static void deleteAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_temp_message", null, null);
    }

    public static void delete(PushMessage data) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_temp_message", "data = ?", new String[]{String.valueOf(data.data)});
    }


    public static void delete(List<PushMessage> data) {
        if (data.isEmpty()){
            return;
        }
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        long time = data.get(data.size() - 1).timeStamp;
        db.delete("t_cp_temp_message", "timeStamp <= ?", new String[]{String.valueOf(time)});
    }

    public static List<PushMessage> findAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_cp_temp_message", new String[]{});

        List<PushMessage> list = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                PushMessage pushMessage = new PushMessage();
                pushMessage.data = cursor.getString(cursor.getColumnIndex("data"));
                pushMessage.timeStamp = cursor.getLong(cursor.getColumnIndex("timeStamp"));
                list.add(pushMessage);
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
