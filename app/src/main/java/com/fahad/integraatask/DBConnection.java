package com.fahad.integraatask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DBConnection extends SQLiteOpenHelper {

    public static final String DBNAME="dbIntegraa.sqlite";
    public static final String DBLOCATION="/data/data/com.fahad.integraatask/databases/";

    private Context mContext;
    private SQLiteDatabase mDatabase;

    String TABLE_User = "tbl_user_info";
    String col_ID = "ID";
    String col_UserID = "UserID";
    String col_Token = "Token";
    String col_TrakingToken= "TrakingToken";
    String col_Type= "Type";

    String TABLE_Common = "tbl_common";
    String col_com_ID = "ID";
    String col_com_Label = "Label";
    String col_com_Payload = "Payload";


    public void OpenDatabase(){
        String dbPath = mContext.getDatabasePath(DBNAME).getPath();
        if(mDatabase != null && mDatabase.isOpen()){
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public void CloseDatabase(){
        if(mDatabase!=null){
            mDatabase.close();
        }
    }

    public boolean copyDatabase(Context context){
        try{
            InputStream inputStream = context.getAssets().open(DBConnection.DBNAME);
            String outFileName = DBConnection.DBLOCATION + DBConnection.DBNAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length;
            while ((length =  inputStream.read(buff)) > 0){
                outputStream.write(buff,0,length);
            }
            outputStream.flush();
            outputStream.close();
            return true;

        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    public Cursor GetData(String _query) {
        OpenDatabase();
        String query = _query;
        Cursor cursor = mDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean InsertLoginDetail(String _userID,String _token,String _trakingToken,String _type){
        OpenDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_UserID,_userID);
        contentValues.put(col_Token,_token);
        contentValues.put(col_TrakingToken,_trakingToken);
        contentValues.put(col_Type,_type);

        long result = mDatabase.insert(TABLE_User,null,contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean UpdateLoginDetail(String _userID,String _token,String _trakingToken,String _type){
        OpenDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_UserID,_userID);
        contentValues.put(col_Token,_token);
        contentValues.put(col_TrakingToken,_trakingToken);
        contentValues.put(col_Type,_type);

        long result = mDatabase.update(TABLE_User,contentValues,col_ID+"="+1 ,null);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean InsertCommonPermission(String _label,String _payload){
        OpenDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_com_Label,_label);
        contentValues.put(col_com_Payload,_payload);

        long result = mDatabase.insert(TABLE_Common,null,contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean UpdateCommonPermission(String _label,String _payload){
        OpenDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_com_Label,_label);
        contentValues.put(col_com_Payload,_payload);

        long result = mDatabase.update(TABLE_Common,contentValues,col_com_ID+"="+1 ,null);
        if (result == -1)
            return false;
        else
            return true;
    }


    public DBConnection(@Nullable Context context) {
        super(context, DBNAME, null, 3);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
