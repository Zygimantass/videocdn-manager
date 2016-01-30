package lt.jkm.magelinskas.zygimantas.flussonicstatus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "flussonic.db";
    public static final String TABLE_SERVERS = "servers";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SERVERNAME = "servername";
    public static final String COLUMN_SERVERIP = "serverip";
    public static final String COLUMN_PORT = "port";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";


    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SERVERS_TABLE = "CREATE TABLE " +
                TABLE_SERVERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_SERVERNAME
                + " TEXT," + COLUMN_SERVERIP + " TEXT, " + COLUMN_PORT
                + " INTEGER, " + COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD
                + " TEXT);";
        Log.e("oncreate", CREATE_SERVERS_TABLE);
        db.execSQL(CREATE_SERVERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
        onCreate(db);
    }

    public void addServer(Server server)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVERNAME, server.getServerName());
        values.put(COLUMN_SERVERIP, server.getServerIP());
        values.put(COLUMN_PORT, Integer.parseInt(server.getPort()));
        values.put(COLUMN_USERNAME, server.getUsername());
        values.put(COLUMN_PASSWORD, server.getPassword());

        SQLiteDatabase db = this.getWritableDatabase();
        Log.v("SERVER", "ADDED");
        db.insert(TABLE_SERVERS, null, values);
        db.close();
    }

    public Server findServer(String serverName)
    {
        String query = "select * from " + TABLE_SERVERS + " where " + COLUMN_SERVERNAME + "=\"" + serverName + "\";";
        Log.e("asd", query);
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Server server = new Server();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            server.setID(Integer.parseInt(cursor.getString(0)));
            server.setServerName(cursor.getString(1));
            server.setServerIP(cursor.getString(2));
            server.setPort(cursor.getString(3));
            server.setUsername(cursor.getString(4));
            server.setPassword(cursor.getString(5));
            cursor.close();
        }
        else{
            server = null;
        }
        db.close();
        return server;
    }

    public boolean deleteServer(String serverName) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_SERVERS + " WHERE " + COLUMN_SERVERNAME + " =  \"" + serverName + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Server product = new Server();

        if (cursor.moveToFirst()) {
            product.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_SERVERS, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(product.getID()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public List<Server> listServers(){
        List<Server> serverList = new ArrayList<Server>();
        String query = "SELECT * FROM " + TABLE_SERVERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do {
                Server server = new Server();
                server.setID(Integer.parseInt(cursor.getString(0)));
                server.setServerName(cursor.getString(1));
                server.setServerIP(cursor.getString(2));
                server.setPort(cursor.getString(3));
                server.setUsername(cursor.getString(4));
                server.setPassword(cursor.getString(5));
                serverList.add(server);
            } while (cursor.moveToNext());
        }

        return serverList;
    }

}
