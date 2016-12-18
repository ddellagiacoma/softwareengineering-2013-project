package com.example.reminiscens;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

public class AudioManager {

	private String filePath;
	private File trackFile;
	private MediaRecorder mRecorder;
	public static final String NAME_AUDIO_TRACK = "Reminiscens.amr";

	public AudioManager(String filePath) {
		this.filePath = filePath;

		trackFile = new File(filePath);
	}

	public String getTrackPath() {
		return this.filePath;
	}

	public void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);

		mRecorder.setOutputFile(filePath);

		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("RECORDING", "prepare() failed");
		}

		mRecorder.start();
	}

	public void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	public static void startPlayer(Context ctx, String fileToReproduce) {
		File file = new File(fileToReproduce);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "audio/AMR");

		ctx.startActivity(intent);
	}

	// call on onPause of activity using this class
	public void onPause() {
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void deleteTmpFile() {
		File fileTmp = new File(filePath);
		fileTmp.delete();
	}

	public static void merge(String filePathFirst, String filePathSecond) {
		File fileTmp = new File(filePathSecond);

		if (fileTmp.exists()) {
			try {
				String originalVoiceMessageRecording = filePathFirst;
				String newVoiceMessageRecording = filePathSecond;

				File outputFile = new File(originalVoiceMessageRecording);
				// Second parameter to indicate appending of data
				FileOutputStream fos = new FileOutputStream(outputFile, true);

				File inputFile = new File(newVoiceMessageRecording);
				FileInputStream fis = new FileInputStream(inputFile);

				byte fileContent[] = new byte[(int) inputFile.length()];
				// Reads the file content as byte from the list.
				fis.read(fileContent);

				// copy the entire file, but not the first 6 bytes
				byte[] headerlessFileContent = new byte[fileContent.length - 6];
				for (int j = 6; j < fileContent.length; j++) {
					headerlessFileContent[j - 6] = fileContent[j];
				}
				fileContent = headerlessFileContent;

				// Write the byte into the combine file
				fos.write(fileContent);

				// Delete the new recording as it is no longer required
				File file = new File(newVoiceMessageRecording);
				file.delete();
				fos.close();
				fis.close();
			} catch (Exception ex) {
				Log.e("MERGING",
						"Error while merging audio file: " + ex.getMessage());
			}
		}
	}

}
