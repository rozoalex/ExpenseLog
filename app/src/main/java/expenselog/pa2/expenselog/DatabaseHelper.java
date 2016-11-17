package expenselog.pa2.expenselog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rozoa on 11/15/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Logs.db";
    public static final String TABLE_NAME="logs_table";
    public static final String COL_1="_id";
    public static final String COL_2="TITLE";
    public static final String COL_3="NOTES";
    public static final String COL_4="DATE";

    public DatabaseHelper(Context c){
        super(c,DATABASE_NAME,null,1 );


    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" ( _id INTEGER PRIMARY KEY AUTOINCREMENT,TITLE TEXT ,NOTES TEXT,DATE TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }



    public long insert(String title,String notes,String date ){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_2,title);
        cv.put(COL_3,notes);
        cv.put(COL_4,date);

        long result = db.insert(TABLE_NAME,null, cv);
        Log.i("DatabaseHelper","insert: "+title+"\nResult: "+String.valueOf(result));
        return result;
    }


    public Cursor getAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLE_NAME,null,null,null,null,null,null);

    }

    public long edit(long id,String title,String notes,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1,String.valueOf(id));
        cv.put(COL_2,title);
        cv.put(COL_3,notes);
        cv.put(COL_4,date);
        return db.update(TABLE_NAME,cv,"_id = ?",new String[]{String.valueOf(id)});
    }

    public Integer remove(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"_id = ?",new String[]{String.valueOf(id)});
    }


}
