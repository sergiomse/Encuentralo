package com.sergiomse.encuentralo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sergiomse.encuentralo.model.Thing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sergiomse@gmail.com on 24/09/2015.
 */
public class ThingsDB {

    public static final String DATABASE_NAME = "things.db";
    public static final String DATABASE_TABLE = "things";
    public static final int DATABASE_VERSION = 1;

    public static final String KEY_ID = "_id";

    public static final String IMAGE_PATH_COLUMN = "imagepath";
    public static final String TAGS_COLUMN = "tags";
    public static final String LOCATION_COLUMN = "location";
    public static final String MODIFICATION_DATE_COLUMN = "modifdate";

    public static final String[] COLS = new String[] {KEY_ID,
                                            IMAGE_PATH_COLUMN,
                                            TAGS_COLUMN,
                                            LOCATION_COLUMN,
                                            MODIFICATION_DATE_COLUMN};


    private static class ThingsDBOpenHelper extends SQLiteOpenHelper {

        private static final String TAG = ThingsDBOpenHelper.class.getSimpleName();



        private static final String CREATE_TABLE = "create table " +
                DATABASE_TABLE + " (" +
                KEY_ID + " integer primary key autoincrement, " +
                IMAGE_PATH_COLUMN + " text, " +
                TAGS_COLUMN + " text, " +
                LOCATION_COLUMN + " text, " +
                MODIFICATION_DATE_COLUMN + " text);";


        public ThingsDBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade database ThingsDB");
        }
    }


    private ThingsDBOpenHelper helper;
    private SQLiteDatabase db;

    public ThingsDB(Context context) {
        helper = new ThingsDBOpenHelper(context);
        establishDb();
    }

    public void establishDb() {
        if(db == null) {
            db = helper.getWritableDatabase();
        }
    }

    public void cleanup() {
        if(db != null) {
            db.close();
            db = null;
        }
    }

    public void insertThing(Thing thing) {
        ContentValues values = new ContentValues();
        values.put(COLS[1], thing.getImagePath());
        values.put(COLS[2], thing.getTags());
        values.put(COLS[3], thing.getLocation());
        values.put(COLS[4], String.valueOf(thing.getModifDate().getTime()));
        db.insert(DATABASE_TABLE, null, values);
    }

    public List<Thing> getThingsOrderedByDate() {
        List<Thing> things = new ArrayList<>();

        Cursor c = db.query(DATABASE_TABLE, COLS, null, null, null, null, MODIFICATION_DATE_COLUMN + " DESC");
        while(c.moveToNext()) {
            Thing thing = new Thing();
            thing.setId(c.getLong(0));
            thing.setImagePath(c.getString(1));
            thing.setTags(c.getString(2));
            thing.setLocation(c.getString(3));
            thing.setModifDate(new Date(Long.parseLong(c.getString(4))));
            things.add(thing);
        }

        return things;
    }
}
