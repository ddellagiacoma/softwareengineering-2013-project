package com.example.reminiscens;

import com.example.reminiscens.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Subscribe extends Activity {

	private EditText nameText;
	private EditText surnameText;
	private Spinner yearSpinner;
	private Spinner monthSpinner;
	private Spinner daySpinner;
	private Spinner questionSpinner;
	private EditText answerText;
	private EditText ctrlanswerText;

	private int id;

	private DBTUser dbUser = new DBTUser(this);
	private DBTPreferenze dbPreference = new DBTPreferenze(this);
	private DBTTipologia dbType = new DBTTipologia(this);
	private DBTEventiPersonali dbPersonal = new DBTEventiPersonali(this);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_registrazione);

		nameText = (EditText) this.findViewById(R.id.txtRegistrazioneNome);
		surnameText = (EditText) this
				.findViewById(R.id.txtRegistrazioneCognome);
		yearSpinner = (Spinner) this
				.findViewById(R.id.spinnerRegistrazioneAnno);
		monthSpinner = (Spinner) this
				.findViewById(R.id.spinnerRegistrazioneMese);
		daySpinner = (Spinner) this
				.findViewById(R.id.spinnerRegistrazioneGiorno);
		questionSpinner = (Spinner) this
				.findViewById(R.id.SpinnerRegistrazioneDomanda);
		answerText = (EditText) this
				.findViewById(R.id.txtRegistrazioneRisposta);
		ctrlanswerText = (EditText) this
				.findViewById(R.id.txtRegistrazioneRisposta2);
		populateSpinners();

		dbUser.open();
		dbType.open();
		dbPreference.open();
		dbPersonal.open();
	}

	protected void onRestart() {
		super.onRestart();
		nameText.setText("");
		surnameText.setText("");
		answerText.setText("");
		ctrlanswerText.setText("");
		daySpinner.setSelection(0);
		monthSpinner.setSelection(0);
		yearSpinner.setSelection(0);
		questionSpinner.setSelection(0);

		dbUser.open();
		dbType.open();
		dbPreference.open();
		dbPersonal.open();
	}

	public void onPause() {
		super.onPause();

		dbUser.close();
		dbType.close();
		dbPreference.close();
		dbPersonal.close();
	}

	public void registrami(View v) {
		String name = nameText.getText().toString();
		String surname = surnameText.getText().toString();
		String year = yearSpinner.getSelectedItem().toString();
		String month = monthSpinner.getSelectedItem().toString();
		String day = daySpinner.getSelectedItem().toString();
		String question = questionSpinner.getSelectedItem().toString();
		String answer = answerText.getText().toString();
		String ctrlanswer = ctrlanswerText.getText().toString();
		String birthday = year + month + day;

		Time bday = Utility.dateFromStringToTime(birthday);

		// testo se uno o piu campi sono vuoti
		boolean emptyField = (name.equals("")) || (surname.equals(""))
				|| (year.equals(""))
				|| (answer.equals("") || (ctrlanswer.equals("")));

		if (emptyField) {
			Toast.makeText(this, "Hai lasciato vuoto uno o più campi",
					Toast.LENGTH_LONG).show();
		} else if (!answer.equals(ctrlanswer)) {
			Toast.makeText(this, "Le due risposte non coincidono",
					Toast.LENGTH_LONG).show();
			answerText.setText("");
			ctrlanswerText.setText("");
			answerText.requestFocus();

		} else {

			// inserimento user
			try {
				id = dbUser.insert(name, surname, bday, question, answer,
						DBTUser.SET_NULL);

			} catch (Exception e) {
				Toast.makeText(this, "Ti sei già registrato", Toast.LENGTH_LONG)
						.show();
				return;
			}
			// inserimenti accessori
			try {
				// inserisco le preferenze con Nclick = 0
				int countType = dbType.fetchAll().getCount();

				for (int i = 0; i < countType; i++) {
					dbPreference.insert(id, i);
				}

				// inserisco due record iniziali
				dbPersonal.insert(bday, "E' nato " + name + " " + surname, "",
						id, "Nascita", DBTEventiPersonali.SET_NULL);

				Time now = new Time();
				now.setToNow();

				dbPersonal.insert(now, "Iscrizione a Reminiscens", "", id,
						"Iscrizione!", DBTEventiPersonali.SET_NULL);
			} catch (Exception e) {
				Toast.makeText(this, "Errore inserimento dati accessori",
						Toast.LENGTH_LONG).show();
			}

			Intent intentRegistrami = new Intent(Subscribe.this,
					MainActivity.class);
			startActivity(intentRegistrami);
		}
	}

	private void populateSpinners() {
		// year
		ArrayAdapter<CharSequence> adapterYear = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		adapterYear
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (int i = 1930; i < 2014; i++) {
			adapterYear.add(String.valueOf(i));
		}

		yearSpinner.setAdapter(adapterYear);
		// month
		ArrayAdapter<CharSequence> adapterMonth = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		adapterMonth
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (int i = 1; i < 13; i++) {
			if (i < 10) {
				adapterMonth.add("0" + String.valueOf(i));
			} else {
				adapterMonth.add(String.valueOf(i));
			}
		}

		monthSpinner.setAdapter(adapterMonth);
		// day
		ArrayAdapter<CharSequence> adapterDay = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item);
		adapterDay
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (int i = 1; i < 32; i++) {
			if (i < 10) {
				adapterDay.add("0" + String.valueOf(i));
			} else {
				adapterDay.add(String.valueOf(i));
			}
		}

		daySpinner.setAdapter(adapterDay);
	}
}
