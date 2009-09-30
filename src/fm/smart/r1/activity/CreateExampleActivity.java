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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Main;
import fm.smart.r1.R;
import fm.smart.r1.Utils;

public class CreateExampleActivity extends Activity implements
		View.OnClickListener {
	public static ProgressDialog myProgressDialog;
	private static CreateExampleResult create_example_result = null;
	private String item_id = null;
	private String list_id = null;
	private String example = null;
	private String cue = null;
	private String translation = null;
	public static String example_language = null;
	public static String translation_language = null;
	protected static AddSentenceResult add_sentence_list_result;
	protected static AddItemResult add_item_list_result;
	private String example_transliteration = null;
	private String translation_transliteration = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		setContentView(R.layout.create_example);
		final Intent queryIntent = getIntent();
		Bundle extras = queryIntent.getExtras();
		item_id = (String) extras.get("item_id");
		list_id = (String) extras.get("list_id");
		if (list_id == null || list_id.equals("")) {
			list_id = Main.default_study_list_id;
		}

		cue = (String) extras.get("cue");
		example = (String) extras.get("example");
		translation = (String) extras.get("translation");
		example_language = (String) extras.get("example_language");
		translation_language = (String) extras.get("translation_language");
		example_transliteration = (String) extras
				.get("example_transliteration");
		translation_transliteration = (String) extras
				.get("translation_transliteration");

		TextView example_text = (TextView) findViewById(R.id.create_example_sentence);
		if (!TextUtils.isEmpty(example)) {
			example_text.setText(example);
		}
		example_text.setHint(example_language + " sentence with " + cue);
		TextView translation_text = (TextView) findViewById(R.id.create_example_translation);
		if (!TextUtils.isEmpty(translation)) {
			translation_text.setText(translation);
		}
		translation_text.setHint(translation_language
				+ " translation of example sentence");

		Button button = (Button) findViewById(R.id.create_example_submit);
		button.setOnClickListener(this);

		TextView translation_text_legend = (TextView) findViewById(R.id.create_example_translation_legend);

		TextView sentence_transliteration_textView = (TextView) findViewById(R.id.create_example_sentence_transliteration);
		EditText sentence_transliteration_input_textView = (EditText) findViewById(R.id.sentence_transliteration);
		if (!Utils.isIdeographicLanguage(Main.search_lang)) {
			sentence_transliteration_textView.setVisibility(View.GONE);
			sentence_transliteration_input_textView.setVisibility(View.GONE);
		} else if (!TextUtils.isEmpty(example_transliteration)) {
			sentence_transliteration_input_textView
					.setText(example_transliteration);
		}

		TextView translation_transliteration_textView = (TextView) findViewById(R.id.create_example_translation_transliteration);
		EditText translation_transliteration_input_textView = (EditText) findViewById(R.id.translation_transliteration);
		if (!Utils.isIdeographicLanguage(Main.result_lang)) {
			translation_transliteration_textView.setVisibility(View.GONE);
			translation_transliteration_input_textView.setVisibility(View.GONE);
		} else if (!TextUtils.isEmpty(translation_transliteration)) {
			translation_transliteration_input_textView
					.setText(translation_transliteration);
		}

	}

	// so there is question of checking for existing items (auto-completion?)
	// and uploading sounds and images ...
	public void onClick(View v) {
		EditText exampleInput = (EditText) findViewById(R.id.create_example_sentence);
		EditText translationInput = (EditText) findViewById(R.id.create_example_translation);
		EditText exampleTransliterationInput = (EditText) findViewById(R.id.sentence_transliteration);
		EditText translationTransliterationInput = (EditText) findViewById(R.id.translation_transliteration);
		final String example = exampleInput.getText().toString();
		final String translation = translationInput.getText().toString();
		if (TextUtils.isEmpty(example) || TextUtils.isEmpty(translation)) {
			Toast t = Toast.makeText(this,
					"Example and translation are required fields", 150);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		} else {
			final String example_language_code = Utils.LANGUAGE_MAP
					.get(example_language);
			final String translation_language_code = Utils.LANGUAGE_MAP
					.get(translation_language);
			final String example_transliteration = exampleTransliterationInput
					.getText().toString();
			final String translation_transliteration = translationTransliterationInput
					.getText().toString();

			if (Main.isNotLoggedIn(this)) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName(this, LoginActivity.class.getName());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // avoid
				// navigation
				// back to this?
				LoginActivity.return_to = CreateExampleActivity.class.getName();
				LoginActivity.params = new HashMap<String, String>();
				LoginActivity.params.put("list_id", list_id);
				LoginActivity.params.put("item_id", item_id);
				LoginActivity.params.put("example", example);
				LoginActivity.params.put("translation", translation);
				LoginActivity.params.put("example_language", example_language);
				LoginActivity.params.put("translation_language",
						translation_language);
				LoginActivity.params.put("example_transliteration",
						example_transliteration);
				LoginActivity.params.put("translation_transliteration",
						translation_transliteration);
				startActivity(intent);
			} else {

				final ProgressDialog myOtherProgressDialog = new ProgressDialog(
						this);
				myOtherProgressDialog.setTitle("Please Wait ...");
				myOtherProgressDialog.setMessage("Creating Example ...");
				myOtherProgressDialog.setIndeterminate(true);
				myOtherProgressDialog.setCancelable(true);

				final Thread create_example = new Thread() {
					public void run() {
						// TODO make this interruptable .../*if
						// (!this.isInterrupted())*/
						try {
							// TODO failures here could derail all ...
							CreateExampleActivity.add_item_list_result = ItemActivity
									.addItemToList(list_id, item_id,
											CreateExampleActivity.this);
							CreateExampleActivity.create_example_result = createExample(
									example, example_language_code,
									example_transliteration, translation,
									translation_language_code,
									translation_transliteration, item_id,
									list_id);
							CreateExampleActivity.add_sentence_list_result = ItemActivity
									.addSentenceToList(
											CreateExampleActivity.create_example_result.http_response,
											item_id, list_id,
											CreateExampleActivity.this);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						myOtherProgressDialog.dismiss();

					}
				};
				myOtherProgressDialog.setButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								create_example.interrupt();
							}
						});
				OnCancelListener ocl = new OnCancelListener() {
					public void onCancel(DialogInterface arg0) {
						create_example.interrupt();
					}
				};
				myOtherProgressDialog.setOnCancelListener(ocl);
				myOtherProgressDialog.show();
				create_example.start();
			}
		}
	}

	public void onWindowFocusChanged(boolean bool) {
		super.onWindowFocusChanged(bool);
		Log.d("DEBUG", "onWindowFocusChanged");
		if (CreateExampleActivity.create_example_result != null) {
			synchronized (CreateExampleActivity.create_example_result) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				final boolean success = CreateExampleActivity.create_example_result
						.success();
				dialog.setTitle(CreateExampleActivity.create_example_result
						.getTitle());
				dialog.setMessage(CreateExampleActivity.create_example_result
						.getMessage());
				CreateExampleActivity.create_example_result = null;
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO avoid moving to item view if previous thread was
						// interrupted? create_example.isInterrupted() but need
						// user to be aware if we
						// have created example already - progress dialog is set
						// cancelable, so back button will work? maybe should
						// avoid encouraging cancel
						// on POST operations ... not sure what to do if no
						// response from server - guess we will time out
						// eventually ...
						if (success) {
							// want to go back to individual item screen now ...
							ItemListActivity.loadItem(
									CreateExampleActivity.this, item_id);
						}
					}
				});
				dialog.show();

			}
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
	public CreateExampleResult createExample(String sentence,
			String sentence_language, String sentence_transliteration,
			String translation, String translation_language,
			String translation_transliteration, String item_id, String list_id)
			throws Exception {
		if (item_id == null) {
			throw new Exception("Item ID cannot be null");
		}
		String http_response = "";
		int status_code = 0;
		AndroidHttpClient client = null;
		try {

			// could have just an add_sound activity for individual item ...

			// separate post to upload image:
			// POST http://api.smart.fm/sentences/:sentence_id/images

			// separate post to upload sound:
			// POST http://api.smart.fm/sentences/:sentence_id/sounds
			URI uri = new URI("http://api.smart.fm/lists/" + list_id
					+ "/items/" + item_id + "/sentences");
			Log.d("DEBUG", uri.toString());
			HttpPost post = new HttpPost(uri);
			// set POST body
			String post_body = "sentence[text]="
					+ URLEncoder.encode(sentence, "UTF-8")
					+ "&sentence[language]=" + sentence_language
					+ "&translation[text]="
					+ URLEncoder.encode(translation, "UTF-8")
					+ "&translation[language]=" + translation_language
					+ "&api_key=" + Main.API_KEY + "&item_id=" + item_id
					+ "&list_id=" + list_id;

			if (sentence_transliteration != null
					&& !sentence_transliteration.equals("")) {
				post_body += "&sentence[transliteration]="
						+ URLEncoder.encode(sentence_transliteration, "UTF-8");
			}

			if (translation_transliteration != null
					&& !translation_transliteration.equals("")) {
				post_body += "&translation[transliteration]="
						+ URLEncoder.encode(translation_transliteration,
								"UTF-8");
			}

			Log.d("DEBUG", post_body);
			// hmm not sure here if this username.password should be the same
			// as the user one? does that username/password have to match the
			// API key?
			String auth = Main.username(this) + ":" + Main.password(this);
			byte[] bytes = auth.getBytes();
			post.setHeader("Authorization", "Basic "
					+ new String(Base64.encodeBase64(bytes)));
			// post.setHeader("Authorization",
			// "Basic dGFuc2FrdTpzYW1qb3NlcGg=");
			// [B@434f6610
			// setting content-type is overwritten ...
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			post.setHeader("Host", "api.smart.fm");
			HttpEntity entity = new StringEntity(post_body, "UTF-8");
			post.setEntity(entity);

			Header[] array = post.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("DEBUG", array[i].toString());
			}
			client = AndroidHttpClient.newInstance("Main");
			HttpResponse response1 = client.execute(post);
			status_code = response1.getStatusLine().getStatusCode();
			Log.d("DEBUG", response1.getStatusLine().toString());
			array = response1.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("DEBUG", array[i].toString());
			}
			long length = response1.getEntity().getContentLength();
			byte[] response_bytes = new byte[(int) length];
			response1.getEntity().getContent().read(response_bytes);
			Log.d("DEBUG", new String(response_bytes));
			http_response = new String(response_bytes);
			// HttpEntity entity = response1.getEntity();
		} catch (IOException e) {
			/* Reset to Default image on any error. */
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (client != null) {
				client.close();
			}
		}

		return new CreateExampleResult(status_code, http_response);
	}

}
