package com.example.reminiscens;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

public class DBTUser {

	private DBManager dbManager;
	private SQLiteDatabase db;
	public static final String SET_NULL = "invalid";

	public static final String DBTName = "User";
	public static final String DBK_Id = "_id";
	public static final String DBK_Name = "Nome";
	public static final String DBK_Surname = "Cognome";
	public static final String DBK_Birthday = "DataNascita";
	public static final String DBK_SecretQuestion = "DomandaSegreta";
	public static final String DBK_SecretAnswer = "RispostaSegreta";
	public static final String DBK_Email = "Email";

	private final String[] columns = { DBK_Id, DBK_Name, DBK_Surname,
			DBK_Birthday, DBK_Birthday, DBK_Email, DBK_SecretQuestion,
			DBK_SecretAnswer };

	public DBTUser(Context ctx) {
		this.dbManager = new DBManager(ctx);
	}

	public DBTUser open() {
		if ((db==null)||(!db.isOpen())) {
			db = dbManager.getWritableDatabase();
		}
		return this;
	}

	public void close() {
		db.close();
	}

	// all'email si può mettere null tutti gli altri sono obbligatori, usare la
	// costante di questa classe!
	// ritorna l'id della riga inserita, -1 in caso d'errore
	public int insert(String name, String surname, Time birthday,
			String secretQuestion, String secretAnswer, String email) {

		ContentValues insertValues = new ContentValues();

		insertValues.put(DBK_Name, name);
		insertValues.put(DBK_Surname, surname);
		insertValues.put(DBK_Birthday, Utility.dateFromTimeToString(birthday));
		insertValues.put(DBK_SecretQuestion, secretQuestion);
		insertValues.put(DBK_SecretAnswer, secretAnswer);

		if (email != SET_NULL) {
			insertValues.put(DBK_Email, email);
		}

		long result = db.insertOrThrow(DBTName, null, insertValues);

		return (int) result;
	}

	public boolean update(int idUser, ContentValues updateValues) {
		String Clause = DBK_Id + " = ?";
		String Args[] = { String.valueOf(idUser) };

		int result = db.update(DBTName, updateValues, Clause, Args);

		return (result == 1);
	}

	public boolean remove(int idUser) {
		String Clause = DBK_Id + " = ?";
		String Args[] = { String.valueOf(idUser) };

		int result = db.delete(DBTName, Clause, Args);

		return (result == 1);
	}

	public Cursor fetchById(int idUser) {
		String selection = DBK_Id + " = ?";
		String selectionArgs[] = { String.valueOf(idUser) };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);

		return c;
	}

	public Cursor fetchAll() {
		Cursor c = db.query(DBTName, columns, null, null, null, null, null);

		return c;
	}

	// ritorna null se il nome cognome è sbagliato
	public String getSecretQuestion(String name, String surname) {
		String selection = DBK_Name + " = ? ";
		selection += "AND " + DBK_Surname + " = ?";
		String selectionArgs[] = { name, surname };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);

		if (!c.moveToFirst()) {
			c.close();
			return null;
		}

		c.moveToFirst();

		String result = c.getString(c.getColumnIndex(DBK_SecretQuestion));
		c.close();

		return result;
	}

	public int getIdFromLogIn(String name, String surname, String secretAnswer) {
		String selection = DBK_Name + " = ? ";
		selection += "AND " + DBK_Surname + " = ? ";
		selection += "AND " + DBK_SecretAnswer + " = ?";
		String selectionArgs[] = { name, surname, secretAnswer };

		Cursor c = db.query(DBTName, columns, selection, selectionArgs, null,
				null, null);

		if (!c.moveToFirst()) {
			c.close();
			return -1;
		}

		int result = c.getInt(c.getColumnIndex(DBK_Id));
		c.close();

		return result;
	}

}
