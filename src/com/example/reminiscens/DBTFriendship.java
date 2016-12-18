package com.example.reminiscens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBTFriendship {
	
	private DBManager dbManager;
	private SQLiteDatabase db;
	private int idCurrentUser;
	
	public static final String DBTName = "Friendship";
	public static final String DBK_Id = "_id";
	public static final String DBK_IdUserA = "idUserA";
	public static final String DBK_IdUserB = "idUserB";
	
	private final String[] columns = {DBK_IdUserA, DBK_IdUserB};
	
	public DBTFriendship(Context ctx, int idCurrentUser){
		dbManager = new DBManager(ctx);
		this.idCurrentUser = idCurrentUser;
	}
	
	public DBTFriendship open(){
		db = dbManager.getWritableDatabase();
		return this;
	}
	
	public void close(){
		db.close();
	}
	
	public int insertRequest(int idUser){
		ContentValues insertValues = new ContentValues();
		insertValues.put(DBK_IdUserA, idCurrentUser);
		insertValues.put(DBK_IdUserB, idUser);
		
		long result = db.insert(DBTName, null, insertValues);
		
		return (int) result;
	}
	
	public boolean insertFriendship(int idUser){
		int resultA = this.insertRequest(idUser);
		
		ContentValues insertValues = new ContentValues();
		insertValues.put(DBK_IdUserB, idCurrentUser);
		insertValues.put(DBK_IdUserA, idUser);
		
		long resultB = db.insert(DBTName, null, insertValues);
		
		return (resultA !=-1)&&(resultB != -1);		
	}
	
	public boolean removeRequest(int idUser){
		String whereClause = DBK_IdUserA + " = ? ";
		whereClause += "AND " + DBK_IdUserB + " = ?";
		String whereArgs[] = { String.valueOf(idCurrentUser), String.valueOf(idUser) };
		
		int result = db.delete(DBTName, whereClause, whereArgs);
		
		return (result > 0) ? true : false;
	}
	
	public boolean removeFriendship(int idUser){
		boolean resultA = this.removeRequest(idUser);
		
		String whereClause = DBK_IdUserA + " = ? ";
		whereClause += "AND " + DBK_IdUserB + " = ?";
		String whereArgs[] = { String.valueOf(idUser), String.valueOf(idCurrentUser) };
		
		int resultB = db.delete(DBTName, whereClause, whereArgs);
		
		return (resultA)&&(resultB > 0);
	}

	public boolean removeAll(){
		String whereClause = DBK_IdUserA + " = ? ";
		whereClause = "OR " + DBK_IdUserB + " = ?";
		String whereArgs[] = {String.valueOf(idCurrentUser), String.valueOf(idCurrentUser)};
		
		int result = db.delete(DBTName, whereClause, whereArgs);
		
		return (result > 0);
	}
	
	public Cursor fetchFriends(){
		String firstQuery = "(SELECT ff." + DBK_IdUserB + " AS friends ";
		firstQuery += "FROM " + DBTName + " ff ";
		firstQuery += "WHERE ff." + DBK_IdUserA + " = ?)";
		
		String secondQuery = "(SELECT sf." + DBK_IdUserA + " AS friends ";
		secondQuery += "FROM " + DBTName + " sf ";
		secondQuery += "WHERE " + DBK_IdUserB + " = ?)";
		
		String query = firstQuery + " INTERSECT " + secondQuery;
		String selectionArgs[] = {String.valueOf(idCurrentUser), String.valueOf(idCurrentUser)};
		
		Cursor c = db.rawQuery(query, selectionArgs);
		
		return c;
	}
	
	public Cursor fetchMyRequests(){
		String selection = DBK_IdUserA + " = ?";
		String selectionArgs[] = { String.valueOf(idCurrentUser) };
		
		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null, null, null);
		
		return c;
 	}
	
	public Cursor fetchOthersRequests(){
		String selection = DBK_IdUserB + " = ?";
		String selectionArgs[] = { String.valueOf(idCurrentUser) };
		
		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null, null, null);
		
		return c;
	}
	
}
