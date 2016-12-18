package com.example.reminiscens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBTPreferenze {

	private SQLiteDatabase db;
	private DBManager dbManager;

	static final String DBTName = "Preferenze";
	static final String DBK_Id = "_id";
	static final String DBK_IdUser = "IdUser";
	static final String DBK_IdType = "IdTipologia";
	static final String DBK_Click = "NClick";
	private String columns[] = { DBK_IdType, DBK_IdUser, DBK_Click };

	public DBTPreferenze(Context ctx) {
		this.dbManager = new DBManager(ctx);
	}

	public DBTPreferenze open() {
		db = dbManager.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	private boolean update(int idUser, int idType, int nClick) {
		ContentValues values = new ContentValues();
		values.put(DBK_IdUser, idUser);
		values.put(DBK_IdType, idType);
		values.put(DBK_Click, nClick);

		String whereClause = DBK_IdUser + " = ? AND " + DBK_IdType + " = ?";
		String whereArgs[] = { String.valueOf(idUser), String.valueOf(idType) };

		int r = db.update(DBTName, values, whereClause, whereArgs);

		return (r == 0) ? false : true;
	}

	public boolean insert(int idUser, int idType){
		ContentValues insertValues = new ContentValues();
		
		insertValues.put(DBK_IdUser, idUser);
		insertValues.put(DBK_IdType, idType);
		insertValues.put(DBK_Click, 0);
		
		long result = db.insert(DBTName, null, insertValues);
		
		return (result !=-1) ? true : false;
	}
	
	public boolean incrementClick(int idUser, int idType) {
		String selection = DBK_IdUser + " = ?" + DBK_IdType + " = ?";
		String selectionArgs[] = { String.valueOf(idUser),
				String.valueOf(idType) };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);
		
		if (! c.moveToFirst()) return false;
		
		int result = c.getInt(0);
		c.close();

		return update(idUser, idType, result + 1);
	}
	
	public boolean removeAll(int idUser) {
		String whereClause = DBK_IdUser + " = ?";
		String whereArgs[] = { String.valueOf(idUser) };
		
		int result = db.delete(DBTName, whereClause, whereArgs);
		
		return (result > 0) ? true : false;
	}

	public Cursor fetchByUserId(int idUser) {
		String selection = DBK_IdUser + " = ?";
		String selectionArgs[] = { String.valueOf(idUser) };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);
		return c;
	}

	public Cursor fetchByTypeId(int idType) {
		String selection = DBK_IdType + " = ?";
		String selectionArgs[] = { String.valueOf(idType) };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);
		return c;
	}
	
	public Cursor fetchByTypeIdAndUserId(int idType, int idUser){
		String selection = DBK_IdType +" = ? AND " + DBK_IdUser + " = ?";
		String selectionArgs[] = { String.valueOf(idType), String.valueOf(idUser)};

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);
		return c;
	}

	public Cursor fetchAll() {
		Cursor c = db.query(DBTName, columns, null, null, null, null, null);

		return c;
	}

}
