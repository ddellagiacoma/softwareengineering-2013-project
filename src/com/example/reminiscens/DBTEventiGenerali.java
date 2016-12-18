package com.example.reminiscens;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Questa classe � responsabile della gestione della tabella degli eventi
 * generali. In particolare implementa dei metodi per le query, modifiche e
 * inserimenti nella tabella EventiGenerali.
 * 
 * @author Franco
 * 
 */
public class DBTEventiGenerali {

	private static final String TAG = "DBT EVENTI GENERALI";
	
	private SQLiteDatabase db;
	private DBManager dbManager;
	private int iduser;

	static final String DBTName = "EventiGenerali";
	static final String DBK_Id = "_id";
	static final String DBK_Year = "Anno";
	static final String DBK_Description = "Descrizione";
	static final String DBK_isItaly = "Italia";
	static final String DBK_Location = "Luogo";
	static final String DBK_IdType = "IdTipo";
	static final String DBK_Title = "Titolo";
	static final String DBK_IdMainImage = "IdImmaginePrincipale";

	private String queryTemplate = "";
	private String queryOrderBy = "";

	/**
	 * Costruttore principale della classe
	 * 
	 * @param context
	 *            il context da usare, passare this in caso di chiamata
	 *            all'interno dell activity.
	 */
	public DBTEventiGenerali(Context ctx, int idUser) {
		dbManager = new DBManager(ctx);

		this.iduser = idUser;
		// select clause
		queryTemplate += "SELECT ";
		queryTemplate += "e." + DBK_Id + ", ";
		queryTemplate += "e." + DBK_Year + ", ";
		queryTemplate += "e." + DBK_Description + ", ";
		queryTemplate += "e." + DBK_isItaly + ", ";
		queryTemplate += "e." + DBK_Location + ", ";
		queryTemplate += "e." + DBK_IdType + ", ";
		queryTemplate += "e." + DBK_Title + ", ";
		queryTemplate += "e." + DBK_IdMainImage + ", ";
		queryTemplate += "t." + DBTTipologia.DBK_Name + ", ";
		queryTemplate += "p." + DBTPreferenze.DBK_Click + " ";
		// from clause
		queryTemplate += "FROM " + DBTName + " e ";
		queryTemplate += "JOIN " + DBTTipologia.DBTName + " t ";
		queryTemplate += "ON e." + DBK_IdType + " = t." + DBTTipologia.DBK_Id
				+ " ";
		queryTemplate += "JOIN " + DBTPreferenze.DBTName + " p ";
		queryTemplate += "ON e." + DBK_IdType + " = p."
				+ DBTPreferenze.DBK_IdType + " ";
		// where clause
		queryTemplate += "WHERE " + DBTPreferenze.DBK_IdUser + " = "
				+ String.valueOf(idUser) + " AND e.";
		// order
		queryOrderBy = " ORDER BY p." + DBTPreferenze.DBK_Click + ", ";
		queryOrderBy += "e." + DBK_Year;
	}

	/**
	 * Apre il database per una successiva richiesta di lettura o scrittura.
	 * Chiamare sempre prima della chiamata di un metodo di questa classe!
	 * 
	 * @return
	 */
	public DBTEventiGenerali open() {
		this.db = dbManager.getReadableDatabase();
		return this;
	}

	/**
	 * Chiude il database per risparmiare risorse importanti. Ricordarsi di
	 * chiudere se non si chiameranno metodi di questa classe a breve!
	 */
	public void close() {
		this.db.close();
	}

	/**
	 * Questo metodo consiste in una query che ritorner� il record con l'id
	 * specificato.
	 * 
	 * @param id
	 *            l'id del record che si vuole ottenere
	 * @return un Cursor puntante al record cercato
	 */
	public Cursor fetchById(int id) {
		String query = queryTemplate;
		query += DBK_Id + " = ?";

		Log.d(TAG, "Query by id: " + query);
		
		String selectionArgs[] = { String.valueOf(id) };

		return db.rawQuery(query, selectionArgs);
	}

	/**
	 * Questo metodo consiste in una query che ritorner� i record con l'anno
	 * specificato.
	 * 
	 * @param year
	 *            l'anno con cui filtrare i record
	 * @return un Cursor puntante ai record cercati
	 */
	public Cursor fetchByYear(int year) {
		String query = queryTemplate;
		query += DBK_Year + " = ?";
		query += queryOrderBy;

		String selectionArgs[] = { String.valueOf(year) };

		return db.rawQuery(query, selectionArgs);
	}

	/**
	 * Questo metodo consiste in una query che ritorner� i record secondo gli
	 * anni specificati.
	 * 
	 * @param years
	 *            l'array di anni con cui filtrare i record
	 * @return un Cursor puntante ai record cercati
	 */
	public Cursor fetchByYears(int[] years) {
		Cursor[] cursors = new Cursor[years.length];

		for (int i = 0; i < years.length; i++) {
			cursors[i] = fetchByYear(years[i]);
		}

		return new MergeCursor(cursors);
	}

	/**
	 * Questo metodo consiste in una query che ritorner� i record filtrati per
	 * un intervallo di anni.
	 * 
	 * @param yearSince
	 *            l'anno di partenza dell'intervallo
	 * @param yearUpTo
	 *            l'anno di fine dell'intervallo
	 * @return un Cursor puntante ai record cercati
	 */
	public Cursor fetchFromYearToYear(int yearSince, int yearUpTo) {
		String query = queryTemplate;
		query += DBK_Year + " >= ? ";
		query += "AND " + "e." + DBK_Year + " <= ?";
		query += queryOrderBy;

		String selectionArgs[] = { String.valueOf(yearSince),
				String.valueOf(yearUpTo) };

		return db.rawQuery(query, selectionArgs);
	}

	public Cursor fetchByItaly(boolean isInItaly) {
		String query = queryTemplate;
		query += DBK_isItaly + " = ?";
		query += queryOrderBy;

		String selectionArgs[] = { String.valueOf(isInItaly) };

		return db.rawQuery(query, selectionArgs);
	}

	public Cursor fetchByLocation(String location) {
		String query = queryTemplate;
		query += DBK_Location + " = ?";
		query += queryOrderBy;

		String selectionArgs[] = { String.valueOf(location) };

		return db.rawQuery(query, selectionArgs);
	}

	public Cursor fetchByType(int type) {
		String query = queryTemplate;
		query += DBK_IdType + " = ?";
		query += queryOrderBy;

		String selectionArgs[] = { String.valueOf(type) };

		return db.rawQuery(query, selectionArgs);
	}

	// non so quanto sia efficiente
	public Cursor fetchByTypes(int[] types) {
		Cursor[] cursors = new Cursor[types.length];

		for (int i = 0; i < types.length; i++) {
			cursors[i] = fetchByType(types[i]);
		}

		return new MergeCursor(cursors);
	}

	public Cursor fetchAll() {
		String[] selectionArgs = {};

		return db.rawQuery(queryTemplate + queryOrderBy, selectionArgs);
	}

}
