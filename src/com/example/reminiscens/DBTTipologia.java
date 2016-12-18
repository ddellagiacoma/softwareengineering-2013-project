package com.example.reminiscens;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBTTipologia {

	private SQLiteDatabase db;
	private DBManager dbManager;

	static final String DBTName = "Tipologia";
	static final String DBK_Id = "_id";
	static final String DBK_Name = "Nome";
	private String columns[] = { DBK_Id, DBK_Name };

	public DBTTipologia(Context ctx) {
		this.dbManager = new DBManager(ctx);
	}

	public DBTTipologia open() {
		db = this.dbManager.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public Cursor fetchById(int id) {
		String selection = DBK_Id + " = ?";
		String selectionArgs[] = { String.valueOf(id) };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);
		return c;
	}
	
	public Cursor fetchByName(String name){
		String selection = DBK_Name + " = ?";
		String selectionArgs[] = { name };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);
		return c;
	}

	public Cursor fetchAll() {
		Cursor c = db.query(DBTName, columns, null, null, null, null, null);
		
		return c;
	}

}
