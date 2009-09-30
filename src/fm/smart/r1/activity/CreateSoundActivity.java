/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fm.smart.r1.activity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Main;
import fm.smart.r1.R;

public class CreateSoundActivity extends Activity implements
		View.OnClickListener {
	public static ProgressDialog myProgressDialog;
	private String item_id = null;
	private String to_record = "";
	private String id = null;
	private String list_id = null;
	private String sound_type = null;

	private MediaRecorder recorder;
	static MediaPlayer mMediaPlayer = null;
	private static CreateSoundResult create_sound_result;
	private Button button;
	private final static String TAG = CreateSoundActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		setContentView(R.layout.create_sound);
		final Intent queryIntent = getIntent();
		Bundle extras = queryIntent.getExtras();
		item_id = (String) extras.get("item_id");
		id = (String) extras.get("id");
		list_id = (String) extras.get("list_id");
		to_record = (String) extras.get("to_record");
		sound_type = extras.getString("sound_type");

		recorder = new MediaRecorder();

		TextView text = (TextView) findViewById(R.id.create_sound_text);
		text.setText(Html.fromHtml(to_record));
		button = (Button) findViewById(R.id.create_sound_submit);
		button.setOnClickListener(this);

	}

	// so there is question of checking for existing items (auto-completion?)
	// and uploading sounds and images ...
	public void onClick(View v) {
		String threegpfile_name = "test.3gp_amr";
		String amrfile_name = "test.amr";
		File dir = this.getDir("sounds", MODE_WORLD_READABLE);
		final File threegpfile = new File(dir, threegpfile_name);
		File amrfile = new File(dir, amrfile_name);
		String path = threegpfile.getAbsolutePath();
		Log.d("CreateSoundActivity", path);
		Log.d("CreateSoundActivity", (String) button.getText());
		if (button.getText().equals("Start")) {
			try {
				recorder = new MediaRecorder();

				// ContentValues values = new ContentValues(3);
				//
				// values.put(MediaStore.MediaColumns.TITLE, "test");
				// values.put(MediaStore.MediaColumns.DATE_ADDED,
				// System.currentTimeMillis());
				// values.put(MediaStore.MediaColumns.MIME_TYPE,
				// MediaRecorder.OutputFormat.THREE_GPP);
				//			    
				// ContentResolver contentResolver = new ContentResolver(this);
				//			    
				// Uri base = MediaStore.Audio.INTERNAL_CONTENT_URI;
				// Uri newUri = contentResolver.insert(base, values);

				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				recorder.setOutputFile(path);

				recorder.prepare();
				button.setText("Stop");
				recorder.start();
			} catch (Exception e) {
				Log.w(TAG, e);
			}
		} else {
			FileOutputStream os = null;
			FileInputStream is = null;
			try {
				recorder.stop();
				recorder.release(); // Now the object cannot be reused
				button.setEnabled(false);

				// ThreegpReader tr = new ThreegpReader(threegpfile);
				// os = new FileOutputStream(amrfile);
				// tr.extractAmr(os);
				is = new FileInputStream(threegpfile);
				playSound(is.getFD());

				final String media_entity = "http://test.com/test.mp3";
				final String author = "tansaku";
				final String author_url = "http://smart.fm/users/tansaku";

				if (Main.isNotLoggedIn(this)) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setClassName(this, LoginActivity.class.getName());
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); 
					LoginActivity.return_to = CreateSoundActivity.class.getName();
					LoginActivity.params = new HashMap<String, String>();
					LoginActivity.params.put("item_id", item_id);
					LoginActivity.params.put("list_id", list_id);
					LoginActivity.params.put("id", id);
					LoginActivity.params.put("to_record", to_record);
					LoginActivity.params.put("sound_type", sound_type);
					startActivity(intent);
				} else {

					final ProgressDialog myOtherProgressDialog = new ProgressDialog(
							this);
					myOtherProgressDialog.setTitle("Please Wait ...");
					myOtherProgressDialog.setMessage("Creating Sound ...");
					myOtherProgressDialog.setIndeterminate(true);
					myOtherProgressDialog.setCancelable(true);

					final Thread create_sound = new Thread() {
						public void run() {
							// TODO make this interruptable .../*if
							// (!this.isInterrupted())*/
							CreateSoundActivity.create_sound_result = createSound(
									threegpfile, media_entity, author,
									author_url, "1", id);

							myOtherProgressDialog.dismiss();

						}
					};
					myOtherProgressDialog.setButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									create_sound.interrupt();
								}
							});
					OnCancelListener ocl = new OnCancelListener() {
						public void onCancel(DialogInterface arg0) {
							create_sound.interrupt();
						}
					};
					myOtherProgressDialog.setOnCancelListener(ocl);
					myOtherProgressDialog.show();
					create_sound.start();
				}
			} catch (Exception e) {
				Log.w(TAG, e);
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void onWindowFocusChanged(boolean bool) {
		super.onWindowFocusChanged(bool);
		Log.d("DEBUG", "onWindowFocusChanged");
		if (CreateSoundActivity.create_sound_result != null) {
			synchronized (CreateSoundActivity.create_sound_result) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				final boolean success = CreateSoundActivity.create_sound_result
						.success();
				dialog.setTitle(CreateSoundActivity.create_sound_result
						.getTitle());
				dialog.setMessage(CreateSoundActivity.create_sound_result
						.getMessage());
				CreateSoundActivity.create_sound_result = null;
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO avoid moving to item list if previous thread was
						// interrupted? create_sound.isInterrupted() but need
						// user to be aware if we
						// have created sound already - progress dialog is set
						// cancelable, so back button will work? maybe should
						// avoid encouraging cancel
						// on POST operations ... not sure what to do if no
						// response from server - guess we will time out
						// eventually ...
						// if (success) {
						// want to go back to individual item screen now ...
						ItemListActivity.loadItem(CreateSoundActivity.this,
								item_id);
						// }
						// TODO might want to go to different screens depending
						// on type of error, e.g. permission issue versus
						// network
						// error
					}
				});
				dialog.show();

			}
		}
	}

	private static void playSound(FileDescriptor fileDescriptor) {

		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

		mMediaPlayer = new MediaPlayer();

		try {
			mMediaPlayer.setDataSource(fileDescriptor);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.start();

		// if (action.equals(Intent.ACTION_PICK)
		// || action.equals(Intent.ACTION_GET_CONTENT)) {
		// Uri uri = ContentUris.withAppendedId(getIntent().getData(), list_id);
		//
		// Intent intent = getIntent();
		// intent.setData(uri);
		// setResult(RESULT_OK, intent);
		// } else {
		// Uri uri = ContentUris.withAppendedId(SmartFm.Items.CONTENT_URI,
		// list_id);
		//
		// startActivity(new Intent(Intent.ACTION_VIEW, uri));
		// }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

	//  

	// Field: Description: Possible values:
	// cue[text]
	// The content that needs to be memorized. "Bonjour"
	// cue[language] RFC 3066 language code for source language
	// "fr"
	// cue[part_of_speech] Required part of speech (noun, phrase, verb etc). See
	// Part Of Speech appendix for details
	// 'N', 'P'
	// response[text] The translation of the cue[text]. "Hello"
	// response[language] RFC 3066 language code for translation language "en"
	// character_response[text] Kanji or Hanzi representing the item cue for
	// character based languages. Optional parameter used for character based
	// languages (Japanese, Chinese) used for indicating the kanji or hanzi.
	// list_id When supplied, the item will be added to this list. 9900
	// api_key Your SmartFM! API key.

	// 'response[text]' => 'たくさん',
	// 'response[language]' => 'ja',
	// 'cue[text]' => 'tanti',
	// 'cue[part_of_speech]' => 'a',
	// 'cue[language]' => 'it',

	// #{key}=#{URI.encode(val.to_s)}"
	// so could have add button in itemlist view which takes us to a form
	// should infer language settings from the list we are in
	// want to support addition of sound/image ...
	public CreateSoundResult createSound(File file, String media_entity,
			String author, String author_url, String attribution_license_id,
			String id) {
		String http_response = "";
		int status_code = 0;
		HttpClient client = null;
		String type = "sentence";
		String location = "";
		try {
			client = new DefaultHttpClient();
			if (sound_type.equals(Integer.toString(R.id.cue_sound))) {
				type = "item";
			}
			HttpPost post = new HttpPost("http://api.smart.fm/" + type + "s/"
					+ id + "/sounds");

			String auth = Main.username(this) + ":" + Main.password(this);
			byte[] bytes = auth.getBytes();
			post.setHeader("Authorization", "Basic "
					+ new String(Base64.encodeBase64(bytes)));

			post.setHeader("Host", "api.smart.fm");

			FileBody bin = new FileBody(file, "audio/amr");
			StringBody media_entity_part = new StringBody(media_entity);
			StringBody author_part = new StringBody(author);
			StringBody author_url_part = new StringBody(author_url);
			StringBody attribution_license_id_part = new StringBody(
					attribution_license_id);
			StringBody id_part = new StringBody(id);
			StringBody api_key_part = new StringBody(Main.API_KEY);

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("sound[file]", bin);
			reqEntity.addPart("media_entity", media_entity_part);
			reqEntity.addPart("author", author_part);
			reqEntity.addPart("author_url", author_url_part);
			reqEntity.addPart("attribution_license_id",
					attribution_license_id_part);
			reqEntity.addPart(type + "_id", id_part);
			reqEntity.addPart("api_key", api_key_part);

			post.setEntity(reqEntity);

			Header[] array = post.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("DEBUG", array[i].toString());
			}

			Log.d("CreateSoundActivity", "executing request "
					+ post.getRequestLine());
			HttpResponse response = client.execute(post);
			status_code = response.getStatusLine().getStatusCode();
			HttpEntity resEntity = response.getEntity();

			Log.d("CreateSoundActivity",
					"----------------------------------------");
			Log.d("CreateSoundActivity", response.getStatusLine().toString());
			array = response.getAllHeaders();
			String header;
			for (int i = 0; i < array.length; i++) {
				header = array[i].toString();
				if (header.equals("Location")) {
					location = header;
				}
				Log.d("CreateSoundActivity", header);
			}
			if (resEntity != null) {
				Log.d("CreateSoundActivity", "Response content length: "
						+ resEntity.getContentLength());
				Log.d("CreateSoundActivity", "Chunked?: "
						+ resEntity.isChunked());
			}
			long length = response.getEntity().getContentLength();
			byte[] response_bytes = new byte[(int) length];
			response.getEntity().getContent().read(response_bytes);
			Log.d("CreateSoundActivity", new String(response_bytes));
			http_response = new String(response_bytes);
			if (resEntity != null) {
				resEntity.consumeContent();
			}

			// HttpEntity entity = response1.getEntity();
		} catch (IOException e) {
			/* Reset to Default image on any error. */
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new CreateSoundResult(status_code, http_response, location);
	}

	// read the file into a byte array...
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		// Get the size of the file
		long length = file.length();
		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

}
