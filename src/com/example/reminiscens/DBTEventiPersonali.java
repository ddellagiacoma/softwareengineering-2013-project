package com.example.reminiscens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

public class DBTEventiPersonali {

	private SQLiteDatabase db;
	private DBManager dbManager;
	public static final int SET_NULL = -1;

	static final String DBTName = "EventiPersonali";
	static final String DBK_Id = "_id";
	static final String DBK_Date = "Data";
	static final String DBK_Description = "Descrizione";
	static final String DBK_Location = "Luogo";
	static final String DBK_IdUser = "IdUser";
	static final String DBK_Title = "Titolo";
	static final String DBK_IdMainImage = "IdImmaginePrincipale";

	private String queryTemplate = "";

	public DBTEventiPersonali(Context ctx) {
		this.dbManager = new DBManager(ctx);

		//select clause
		queryTemplate += "SELECT ";
		queryTemplate += "e." + DBK_Id + ", ";
		queryTemplate += "e." + DBK_Date + ", ";
		queryTemplate += "e." + DBK_Description + ", ";
		queryTemplate += "e." + DBK_Location + ", ";
		queryTemplate += "e." + DBK_IdUser + ", ";
		queryTemplate += "e." + DBK_Title + ", ";
		queryTemplate += "e." + DBK_IdMainImage + ", ";
		queryTemplate += "u." + DBTUser.DBK_Name + ", ";
		queryTemplate += "u." + DBTUser.DBK_Surname + " ";
		//from clause
		queryTemplate += "FROM " + DBTName + " e ";
		queryTemplate += "JOIN " + DBTUser.DBTName + " u ";
		queryTemplate += "ON e." + DBK_IdUser + " = u." + DBTUser.DBK_Id + " ";
	}

	public DBTEventiPersonali open() {
		db = dbManager.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}
	
	/* Se si vuole settare l'id della main image null usare la specifica costante di questa classe (solo per idMainImage)
	 * 
	 * Ritorna l'id della riga inserita, -1 in caso di errore
	 */
	public int insert(Time date, String description, String location, int idUser, String title, int idMainImage){
		ContentValues insertValues = new ContentValues();
		
		insertValues.put(DBK_Date, Utility.dateFromTimeToString(date));
		insertValues.put(DBK_Description, description);
		insertValues.put(DBK_Location, location);
		insertValues.put(DBK_IdUser, idUser);
		insertValues.put(DBK_Title, title);
		
		if (idMainImage != SET_NULL){
			insertValues.put(DBK_IdMainImage, idMainImage);
		}
		
		long result = db.insert(DBTName, null, insertValues);
		
		return (int) result;
	}
	
	/*
	 * Modifica la storia con l id specificato, secondo il contentValues, ritorna true se tutto ok
	 */
	public boolean update(int idStory, ContentValues updateValues){
		String whereClause = DBK_Id + " = ?";
		String whereArgs[] = { String.valueOf(idStory) };
		
		int result = db.update(DBTName, updateValues, whereClause, whereArgs);
		
		return (result == 1) ? true : false;
	}
	
	public boolean remove(int idStory){
		String whereClause = DBK_Id + " = ?";
		String whereArgs[] = { String.valueOf(idStory) };
		
		int result = db.delete(DBTName, whereClause, whereArgs);
		
		return (result == 1) ? true : false;
	}
	
	public boolean removeAll(int idUser){
		String whereClause = DBK_IdUser + " = ?";
		String whereArgs[] = { String.valueOf(idUser) };
		
		int result = db.delete(DBTName, whereClause, whereArgs);
		
		return (result > 0) ? true : false;
	}

	public Cursor fetchById(int id) {
		String query = queryTemplate;
		query += "WHERE e." + DBK_Id + " = ?";

		String selectionArgs[] = { String.valueOf(id) };

		return db.rawQuery(query, selectionArgs);
	}

	public Cursor fetchByDate(Time date) {
		String query = queryTemplate;
		query += "WHERE e." + DBK_Date + " = ?";

		String selectionArgs[] = { Utility.dateFromTimeToString(date) };

		return db.rawQuery(query, selectionArgs);
	}

	public Cursor fetchFromDateToDate(Time dateSince, Time dateUpTo) {
		String query = queryTemplate;
		query += "WHERE e." + DBK_Date + " >= ? AND e." + DBK_Date + " <= ?";

		String selectionArgs[] = { Utility.dateFromTimeToString(dateSince),
				Utility.dateFromTimeToString(dateUpTo) };

		return db.rawQuery(query, selectionArgs);
	}

	public Cursor fetchByLocation(String location) {
		String query = queryTemplate;
		query += "WHERE e." + DBK_Location + " = ?";

		String selectionArgs[] = { location };

		return db.rawQuery(query, selectionArgs);
	}
	
	public Cursor fetchByUser(int idUser) {
		String query = queryTemplate;
		query += "WHERE e." + DBK_IdUser + " = ?";
		
		String selectionArgs[] = { String.valueOf(idUser) };

		return db.rawQuery(query, selectionArgs);
	}
	
	public Cursor fetchByUsers(int[] idUsers){
		Cursor[] cursors = new Cursor[idUsers.length];

		for (int i = 0; i < idUsers.length; i++) {
			cursors[i] = fetchByUser(idUsers[i]);
		}

		return new MergeCursor(cursors);
	}
	
	public Cursor fetchByUserAndFromDateToDate(int idUser, Time dateSince, Time dateUpTo) {
		String query = queryTemplate;
		query += "WHERE e." + DBK_Date + " <= ? ";
		query += "AND e." + DBK_Date + " >= ? ";
		query += "AND e." + DBK_IdUser + " = ?";

		String selectionArgs[] = { Utility.dateFromTimeToString(dateUpTo),
				Utility.dateFromTimeToString(dateSince), String.valueOf(idUser)};

		return db.rawQuery(query, selectionArgs);
	}
	
	public Cursor fetchByUsersAndFromDateToDate(int[] idUsers, Time dateSince, Time dateUpTo){
		Cursor[] cursors = new Cursor[idUsers.length];

		for (int i = 0; i < idUsers.length; i++) {
			cursors[i] = fetchByUserAndFromDateToDate(idUsers[i], dateSince, dateUpTo);
		}

		return new MergeCursor(cursors);
	}
	

	public Cursor fetchAll() {
		String selectionArgs[] = {};

		return db.rawQuery(queryTemplate, selectionArgs);
	}
}