package com.example.reminiscens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Questa classe � responsabile della creazione del DB. In particolare creer� le
 * tabelle specificate nel diagramma ER del progetto. Inoltre aggiunger� i fatti
 * storici nella tabella degli eventi generali. Nota: il db viene creato solo
 * una volta, all'installazione dell'app. Se per il testing si ha la necessit�
 * di crearlo ogni run usare il comando adb uninstall com.example.reminiscens da
 * terminale.
 * 
 * @author Franco
 * 
 */
public class DBManager extends SQLiteOpenHelper {

	private static final String DB_NAME = "myDb";
	private static final int DB_VERSION = 1;
	private Context ctx;

	/**
	 * Costruttore principale della classe
	 * 
	 * @param context
	 *            il context da usare, passare this in caso di chiamata
	 *            all'interno dell activity.
	 */
	public DBManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.ctx = context;
	}

	/**
	 * Non chiamare mai esplicitamente! Viene gestito direttamente dal sistema.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DB", "created");
		createTableEventiGenerali(db);
		createTableEventiPersonali(db);
		createTableTipologia(db);
		createTablePreferenze(db);
		createTableUser(db);
		createTableFriendship(db);
	}

	/**
	 * Non chiamare mai esplicitamente! Viene gestito direttamente dal sistema.
	 * Tuttora non implementato (vers 1)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// da implementare se dobbiamo aggiornare il db dell'app
	}

	private void createTableEventiPersonali(SQLiteDatabase db) {
		// creazione della tabella

		String tableCreation = "CREATE TABLE EventiPersonali (";
		tableCreation += "_id INTEGER PRIMARY KEY, ";
		tableCreation += "Data TEXT not null, ";
		tableCreation += "Descrizione TEXT, ";
		tableCreation += "IdUser INTEGER not null references User(_id), ";
		tableCreation += "Luogo TEXT,";
		tableCreation += "Titolo TEXT not null, ";
		tableCreation += "IdImmaginePrincipale INTEGER)";

		db.execSQL(tableCreation);
	}

	private void createTableTipologia(SQLiteDatabase db) {
		// creazione della tabella

		String tableCreation = "CREATE TABLE Tipologia (";
		tableCreation += "_id INTEGER PRIMARY KEY, ";
		tableCreation += "Nome TEXT not null)";

		db.execSQL(tableCreation);
		
		String nameTypes[] = { "Sport", "Politica", "Storia", "Arte", "Scienza", "Spettacolo", "Curiosità", "Economia"};
		ContentValues values = new ContentValues();
		
		for (int i=0; i < nameTypes.length; i++){
			values.put(DBTTipologia.DBK_Name, nameTypes[i]);
			db.insert(DBTTipologia.DBTName, null, values);
			values.clear();
		}
	}

	private void createTablePreferenze(SQLiteDatabase db) {
		// creazione della tabella

		String tableCreation = "CREATE TABLE Preferenze (";
		tableCreation += "_id INTEGER PRIMARY KEY, ";
		tableCreation += "IdTipologia INTEGER not null references Tipologia(_id), ";
		tableCreation += "IdUser INTEGER not null references User(_id), ";
		tableCreation += "NClick INTEGER)";

		db.execSQL(tableCreation);
	}

	private void createTableUser(SQLiteDatabase db) {
		// creazione della tabella

		String tableCreation = "CREATE TABLE User (";
		tableCreation += "_id INTEGER PRIMARY KEY, ";
		tableCreation += "Email TEXT, ";
		tableCreation += "DataNascita TEXT not null, ";
		tableCreation += "RispostaSegreta TEXT not null, ";
		tableCreation += "DomandaSegreta TEXT not null, ";
		tableCreation += "Cognome TEXT not null, ";
		tableCreation += "Nome TEXT not null, ";
		tableCreation += "UNIQUE (Nome, Cognome))";

		db.execSQL(tableCreation);
	}

	private void createTableFriendship(SQLiteDatabase db) {
		// creazione della tabella

		String tableCreation = "CREATE TABLE Friendship (";
		tableCreation += "_id INTEGER PRIMARY KEY, ";
		tableCreation += "idUserA INTEGER references User(_id), ";
		tableCreation += "idUserB INTEGER references User(_id))";

		db.execSQL(tableCreation);
	}

	private void createTableEventiGenerali(SQLiteDatabase db) {
		// creazione della tabella

		String tableCreation = "CREATE TABLE EventiGenerali (";
		tableCreation += "_id INTEGER PRIMARY KEY, ";
		tableCreation += "Anno INTEGER not null, ";
		tableCreation += "Descrizione TEXT, ";
		tableCreation += "Italia BOOLEAN, ";
		tableCreation += "Luogo TEXT, ";
		tableCreation += "IdTipo INTEGER references Tipologia(_id), ";
		tableCreation += "IdImmaginePrincipale INTEGER, ";
		tableCreation += "Titolo TEXT)";

		db.execSQL(tableCreation);

		// popolo la tabella
		InputStream inputStream = ctx.getResources().openRawResource(
				R.raw.generali);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		String eachline = "";

		try {
			eachline = bufferedReader.readLine();
		} catch (IOException e) {
			Log.e("DBMANAGER", "Error reading dump file");
		}

		db.beginTransaction();

		while (eachline != null) {
			db.execSQL(eachline);

			try {
				eachline = bufferedReader.readLine();
			} catch (IOException e) {
				Log.e("DBMANAGER", "Error reading dump file");
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		try {
			bufferedReader.close();
			inputStream.close();
		} catch (IOException e) {
			Log.e("DBMANAGER", "Error closing dump file");
		}
	}

}
