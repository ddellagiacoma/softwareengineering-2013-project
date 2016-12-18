package com.example.reminiscens;

import com.example.reminiscens.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionAnswer extends Activity {
	private EditText secretAnswerText;
	private TextView secretQuestionText;
	private int id_User;
	private String secretAnswer = "";
	private SharedPreferences prefs;



	private DBTUser dbUser = new DBTUser(this);

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_in_domanda);
		id_User = getIntent().getExtras().getInt("idUser");

		secretAnswerText = (EditText) this
				.findViewById(R.id.txtRegistrazioneRisposta);
		secretQuestionText = (TextView) this.findViewById(R.id.txtLogInDomanda);
		dbUser.open();

		Cursor c = dbUser.fetchById(id_User);
		c.moveToNext();
		
		secretQuestionText.setText(c.getString(c.getColumnIndex(DBTUser.DBK_SecretQuestion)));
		secretAnswer = c.getString(c.getColumnIndex(DBTUser.DBK_SecretAnswer));

		dbUser.close();

	}

	protected void onRestart() {
		super.onResume();

		secretAnswerText.setText("");
		dbUser.open();
	}

	public void onPause() {
		super.onPause();

		dbUser.close();
	}

	public void login(View v) {
		if (secretAnswer.equals(secretAnswerText.getText().toString())) {
			 		      
			Intent intentLogin = new Intent(QuestionAnswer.this, Home.class);
			intentLogin.putExtra("idUser", id_User);
			
			startActivity(intentLogin);
			finish();
			Toast.makeText(this, "Login succesfull", Toast.LENGTH_SHORT).show();
			
			
			prefs = PreferenceManager.getDefaultSharedPreferences(QuestionAnswer.this);
			prefs.edit().putInt("idUser",id_User).commit();
			
			
		} else {
			Toast.makeText(this, "Risposta errata", Toast.LENGTH_SHORT).show();
			secretAnswerText.setText("");
		}

	}
}
