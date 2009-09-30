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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Main;
import fm.smart.r1.R;
import fm.smart.r1.Utils;

public class CreateItemActivity extends Activity implements
		View.OnClickListener {
	public static final String API_AUTHORIZATION = "tansaku:samjoseph";
	public static ProgressDialog myProgressDialog;
	protected static CreateItemResult create_item_result;
	private String list_id = null;
	private String cue = null;
	private String response = null;
	private String cue_language = null;
	private String response_language = null;
	private String character_cue = null;
	private String character_response = null;
	private String pos = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		setContentView(R.layout.create_item);
		final Intent queryIntent = getIntent();
		Bundle extras = queryIntent.getExtras();
		list_id = (String) extras.get("list_id");
		cue = (String) extras.get("cue");
		response = (String) extras.get("response");
		cue_language = (String) extras.get("cue_language");
		response_language = (String) extras.get("response_language");
		character_cue = (String) extras.get("character_cue");
		character_response = (String) extras.get("character_response");
		pos = (String) extras.get("pos");

		Button button = (Button) findViewById(R.id.create_item_submit);
		button.setOnClickListener(this);

		TextView cue_text = (TextView) findViewById(R.id.cue);
		if (!TextUtils.isEmpty(cue)) {
			cue_text.setText(cue);
		}
		TextView response_text = (TextView) findViewById(R.id.response);
		if (!TextUtils.isEmpty(response)) {
			response_text.setText(response);
		}
		TextView cue_character_text = (TextView) findViewById(R.id.cue_character);
		if (!TextUtils.isEmpty(character_cue)) {
			cue_character_text.setText(character_cue);
		}
		TextView response_character_text = (TextView) findViewById(R.id.response_character);
		if (!TextUtils.isEmpty(character_response)) {
			response_character_text.setText(character_response);
		}

		TextView cue_legend = (TextView) findViewById(R.id.create_item_cue_legend);
		cue_legend.setText(Utils.INV_LANGUAGE_MAP.get(cue_language) + " Term");
		TextView response_legend = (TextView) findViewById(R.id.create_item_response_legend);
		response_legend.setText(Utils.INV_LANGUAGE_MAP.get(response_language)
				+ " Term");
		TextView cue_character_textView = (TextView) findViewById(R.id.create_item_cue_character);
		cue_character_textView.setText(Utils.INV_LANGUAGE_MAP.get(cue_language)
				+ " Character Text");
		TextView response_character_textView = (TextView) findViewById(R.id.create_item_response_character);
		response_character_textView.setText(Utils.INV_LANGUAGE_MAP
				.get(response_language)
				+ " Character Text");

		List<String> pos_list = new Vector<String>(Utils.POS_MAP.keySet());
		Collections.sort(pos_list);

		ArrayAdapter<String> pos_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, pos_list);
		// .simple_spinner_item
		Spinner pos_textView = (Spinner) findViewById(R.id.pos);
		pos_textView.setAdapter(pos_adapter);
		if (!TextUtils.isEmpty(pos)) {
			pos_textView.setSelection(pos_list.indexOf(pos));
		} else {
			pos_textView.setSelection(pos_list.indexOf("Noun"));
		}

		EditText cue_character_input_textView = (EditText) findViewById(R.id.cue_character);
		if (!Utils.isIdeographicLanguage(Main.search_lang)) {
			cue_character_textView.setVisibility(View.GONE);
			cue_character_input_textView.setVisibility(View.GONE);
		} else if (!TextUtils.isEmpty(character_cue)) {
			cue_character_input_textView.setText(character_cue);
		}

		EditText response_character_input_textView = (EditText) findViewById(R.id.response_character);
		if (!Utils.isIdeographicLanguage(Main.result_lang)) {
			response_character_textView.setVisibility(View.GONE);
			response_character_input_textView.setVisibility(View.GONE);
		} else if (!TextUtils.isEmpty(character_response)) {
			response_character_input_textView.setText(character_response);
		}

	}// INV_LANGUAGE_MAP\.put\("(.*)","(.*)"\);

	// so there is question of checking for existing items (auto-completion?)
	// and uploading sounds and images ...
	public void onClick(View v) {
		EditText cueInput = (EditText) findViewById(R.id.cue);
		EditText responseInput = (EditText) findViewById(R.id.response);
		Spinner posInput = (Spinner) findViewById(R.id.pos);
		EditText characterResponseInput = (EditText) findViewById(R.id.response_character);
		EditText characterCueInput = (EditText) findViewById(R.id.cue_character);
		final String cue = cueInput.getText().toString();
		final String response = responseInput.getText().toString();
		final String pos = posInput.getSelectedItem().toString();
		final String character_cue = characterCueInput.getText().toString();
		final String character_response = characterResponseInput.getText()
				.toString();
		String pos_code = Utils.POS_MAP.get(pos);
		if (TextUtils.isEmpty(pos_code)) {
			pos_code = "NONE";
		}
		final String final_pos_code = pos_code;

		if (Main.isNotLoggedIn(this)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(this, LoginActivity.class.getName());
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // avoid
			// navigation
			// back to this?
			LoginActivity.return_to = CreateItemActivity.class.getName();
			LoginActivity.params = new HashMap<String, String>();
			LoginActivity.params.put("list_id", list_id);
			LoginActivity.params.put("cue", cue);
			LoginActivity.params.put("response", response);
			LoginActivity.params.put("cue_language", cue_language);
			LoginActivity.params.put("response_language", response_language);
			LoginActivity.params.put("pos", pos);
			LoginActivity.params.put("character_cue", character_cue);
			LoginActivity.params.put("character_response", character_response);
			startActivity(intent);
		} else {
			// TODO cue and response languages need to be inferred from list we are
			// adding to ... Might want to fix those, i.e. not allow variation
			// on
			// search ...
			// TODO wondering whether there is some way to edit existing items
			// ...

			final ProgressDialog myOtherProgressDialog = new ProgressDialog(
					this);
			myOtherProgressDialog.setTitle("Please Wait ...");
			myOtherProgressDialog.setMessage("Creating Item ...");
			myOtherProgressDialog.setIndeterminate(true);
			myOtherProgressDialog.setCancelable(true);

			final Thread create_item = new Thread() {
				public void run() {
					// TODO make this interruptable .../*if
					// (!this.isInterrupted())*/
					CreateItemActivity.create_item_result = createItem(cue,
							cue_language, character_cue, final_pos_code,
							response, response_language,
							character_response, list_id);

					myOtherProgressDialog.dismiss();

				}
			};
			myOtherProgressDialog.setButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							create_item.interrupt();
						}
					});
			OnCancelListener ocl = new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					create_item.interrupt();
				}
			};
			myOtherProgressDialog.setOnCancelListener(ocl);
			myOtherProgressDialog.show();
			create_item.start();
		}
	}

	public void onWindowFocusChanged(boolean bool) {
		super.onWindowFocusChanged(bool);
		Log.d("DEBUG", "onWindowFocusChanged");
		if (CreateItemActivity.create_item_result != null) {
			synchronized (CreateItemActivity.create_item_result) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				final boolean success = CreateItemActivity.create_item_result
						.success();
				final String item_id = CreateItemActivity.create_item_result.http_response;
				dialog.setTitle(CreateItemActivity.create_item_result
						.getTitle());
				dialog.setMessage(CreateItemActivity.create_item_result
						.getMessage());
				CreateItemActivity.create_item_result = null;
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO avoid moving to item view if previous thread was
						// interrupted? create_item.isInterrupted() but need
						// user to be aware if we
						// have created example already - progress dialog is set
						// cancelable, so back button will work? maybe should
						// avoid encouraging cancel
						// on POST operations ... not sure what to do if no
						// response from server - guess we will time out
						// eventually ...
						if (success) {
							// want to go to individual item screen now ...
							ItemListActivity.loadItem(CreateItemActivity.this,
									item_id);
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
	public CreateItemResult createItem(String cue, String cue_language,
			String character_cue_text, String part_of_speech, String response,
			String response_language, String character_response_text,
			String list_id) {
		String http_response = "";
		int status_code = 0;
		AndroidHttpClient client = null;
		try {

			URI uri = new URI("http://api.smart.fm/items");
			Log.d("DEBUG", uri.toString());
			HttpPost post = new HttpPost(uri);
			// Main.consumer.setTokenWithSecret(Main.ACCESS_TOKEN,
			// Main.TOKEN_SECRET);// TODO store in preferences ...
			// Main.consumer.sign(post);
			// set POST body
			String post_body = "cue[text]=" + URLEncoder.encode(cue, "UTF-8")
					+ "&cue[part_of_speech]=" + part_of_speech
					+ "&cue[language]=" + cue_language + "&response[text]="
					+ URLEncoder.encode(response, "UTF-8")
					+ "&response[language]=" + response_language + "&api_key="
					+ Main.API_KEY + "&list_id=" + list_id;
			if (character_response_text != null
					&& !character_response_text.equals("")) {
				post_body += "&character_response[text]="
						+ URLEncoder.encode(character_response_text, "UTF-8");
			}

			if (character_cue_text != null && !character_cue_text.equals("")) {
				post_body += "&cue[character]="
						+ URLEncoder.encode(character_cue_text, "UTF-8");
			}

			Log.d("DEBUG", post_body);
			// username/password here has to match the API key?
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
			for (Header h : array) {
				Log.d("DEBUG", h.toString());
			}
			client = AndroidHttpClient.newInstance("Main");
			HttpResponse response1 = client.execute(post);
			status_code = response1.getStatusLine().getStatusCode();
			Log.d("DEBUG", response1.getStatusLine().toString());
			array = response1.getAllHeaders();
			for (Header h : array) {
				Log.d("DEBUG", h.toString());
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

		return new CreateItemResult(status_code, http_response, list_id);
	}

}
