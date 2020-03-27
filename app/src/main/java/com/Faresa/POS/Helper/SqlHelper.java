package com.Faresa.POS.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SqlHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.linyjaya.smartofficev2/databases/";

	private static final String DATABASE_NAME = "data.db";
	private static final int DATABASE_VERSION = 3;
	
	private Context myContext;

	public SqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext=context;
	}
	public void createDataBase() throws IOException {
		if(DataBaseisExist()){
    		//jika database ada
    	}
		else{
			
        	this.getReadableDatabase();
 
        	try {
        		copyDataBase();
    		} catch (IOException e) {
    			throw new Error("Error copying database");
        	}
    	}
 
    }
	private boolean DataBaseisExist(){
		 
    	SQLiteDatabase checkDB = null;
    	try{
    		String myPath = DB_PATH + DATABASE_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
    	if(checkDB != null){
    		checkDB.close();
    	}

		return checkDB != null;
    }
	private void copyDataBase() throws IOException {
		 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DATABASE_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
     	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		
	}
	

}
