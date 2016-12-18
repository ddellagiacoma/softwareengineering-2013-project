
package com.example.reminiscens;

import com.example.reminiscens.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Context ctx = this;
	private ListView listViewLogIn;
	private int idUser;
	private SharedPreferences prefs;
	private DBTUser dbUser = new DBTUser(this);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		idUser= prefs.getInt("idUser", 0);
		
		
		if (idUser>0){
			Intent intentLogin = new Intent(MainActivity.this, Home.class);
			intentLogin.putExtra("idUser", idUser);
			
			startActivity(intentLogin);
			
			
		}
		
		
		
		
		
		
		setContentView(R.layout.log_in);
		listViewLogIn = (ListView) this.findViewById(R.id.ListLogIn);

		dbUser.open();

		Cursor clogin = dbUser.fetchAll();
		CursorAdapter adapterlogin = new CursorAdapterLogIn(ctx, clogin);
		listViewLogIn.setAdapter(adapterlogin);
		listViewLogIn.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

	}

	protected void onRestart() {
		super.onResume();

		dbUser.open();
	}

	public void onPause() {
		super.onPause();

		dbUser.close();
	}

	public void registrati(View v) {
		dbUser.close();
		Intent intentRegistrazione = new Intent(MainActivity.this,
				Subscribe.class);
		startActivity(intentRegistrazione);
	}

	public void avanti(View v) {
		CursorAdapter cadapter = (CursorAdapter) listViewLogIn.getAdapter();
		Cursor c = cadapter.getCursor();
		int position = listViewLogIn.getCheckedItemPosition();

		if (position == AbsListView.INVALID_POSITION) {
			Toast.makeText(this, "Seleziona un nome", Toast.LENGTH_LONG).show();
		} else {
			c.moveToPosition(position);
			Intent viewLogInIntent = new Intent(MainActivity.this,
					QuestionAnswer.class);

			viewLogInIntent.putExtra("idUser",
					c.getInt(c.getColumnIndex(DBTUser.DBK_Id)));
			dbUser.close();
			this.startActivity(viewLogInIntent);
		}

	}

}
