package com.sd.rafael.sweetdreams.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sd.rafael.sweetdreams.models.Dream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafae on 22/10/2016.
 */

public class DreamDAO extends SQLiteOpenHelper {

    public DreamDAO(Context context) {
        super(context, "SweetDreams", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table dreams (id integer primary key, title text, description text, grade real, tags text, date text, time text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void Insert(Dream dream) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = getContentValuesDream(dream);
        db.insert("dreams", null, data);
    }

    public List<Dream> Read() {
        List<Dream> dreams = new ArrayList<>();
        String sql = "select * from dreams";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        while(c.moveToNext()) {
            Dream dream = new Dream();

            dream.setId(c.getLong(c.getColumnIndex("id")));
            dream.setTitle(c.getString(c.getColumnIndex("title")));
            dream.setDescription(c.getString(c.getColumnIndex("description")));
            dream.setGrade(c.getDouble(c.getColumnIndex("grade")));
            dream.setTags(c.getString(c.getColumnIndex("tags")));
            dream.setDate(c.getString(c.getColumnIndex("date")));
            dream.setTime(c.getString(c.getColumnIndex("time")));

            dreams.add(dream);
        }
        c.close();

        return dreams;
    }

    public void Remove(Dream dream) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {dream.getId().toString()};

        db.delete("dreams", "id = ?", params);
    }

    public void Update(Dream dream) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = getContentValuesDream(dream);
        String[] params = {dream.getId().toString()};
        db.update("dreams", data, "id = ?", params);
    }

    private ContentValues getContentValuesDream(Dream dream) {
        ContentValues data = new ContentValues();
        data.put("title", dream.getTitle());
        data.put("description", dream.getDescription());
        data.put("grade", dream.getGrade());
        data.put("tags", dream.getTags());
        data.put("date", dream.getDate());
        data.put("time", dream.getTime());

        return data;
    }

}
