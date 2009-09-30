package fm.smart.r1.activity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Main;
import fm.smart.r1.Utils;
import fm.smart.r1.R;

public class SignUpActivity extends Activity implements View.OnClickListener {
	protected static SignUpResult signup_result;
	WebView webview = null;
	static String return_to = Main.class.getName();
	String url = "";
	static String username = null;
	static String password = null;
	static String email = null;
	static String password_confirmation = null;
	public static HashMap<String, String> params;

	// TODO switch all comms to https when Cerego is ready.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		setContentView(R.layout.signup);

		Button signup_button = (Button) findViewById(R.id.signup);
		signup_button.setOnClickListener(this);

		EditText usernameInput = (EditText) findViewById(R.id.username);
		EditText passwordInput = (EditText) findViewById(R.id.password);
		EditText emailInput = (EditText) findViewById(R.id.email);
		EditText passwordConfirmationInput = (EditText) findViewById(R.id.password_confirmation);
		usernameInput.setText(username);
		passwordInput.setText(password);
		passwordConfirmationInput.setText(password);
		emailInput.setText(email);
	}

	public void onClick(View v) {
		EditText usernameInput = (EditText) findViewById(R.id.username);
		EditText passwordInput = (EditText) findViewById(R.id.password);
		EditText emailInput = (EditText) findViewById(R.id.email);
		EditText passwordConfirmationInput = (EditText) findViewById(R.id.password_confirmation);

		final String username = usernameInput.getText().toString();
		SignUpActivity.username = username;
		final String password = passwordInput.getText().toString();
		SignUpActivity.password = password;
		final String email = emailInput.getText().toString();
		SignUpActivity.email = email;
		final String password_confirmation = passwordInput.getText().toString();
		SignUpActivity.password_confirmation = password_confirmation;

		final ProgressDialog myOtherProgressDialog = new ProgressDialog(this);
		myOtherProgressDialog.setTitle("Please Wait ...");
		myOtherProgressDialog.setMessage("Signing up ...");
		myOtherProgressDialog.setIndeterminate(true);
		myOtherProgressDialog.setCancelable(true);

		final Thread login = new Thread() {
			public void run() {
				// TODO make this interruptable .../*if
				// (!this.isInterrupted())*/
				SignUpActivity.signup_result = signup(username, password,
						email, password_confirmation);
				if (SignUpActivity.signup_result.success()) {
					Main.login(SignUpActivity.this, SignUpActivity.username,
							SignUpActivity.password);
					// TODO set users default study list ...

					// TODO set users default study list ...
					// starting to look like the work to support this
					// should go on buckets ...
					LoginActivity.create_default_list = Main.getDefaultStudyList(SignUpActivity.this);
				}
				myOtherProgressDialog.dismiss();

			}
		};
		myOtherProgressDialog.setButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						login.interrupt();
					}
				});
		OnCancelListener ocl = new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				login.interrupt();
			}
		};
		myOtherProgressDialog.setOnCancelListener(ocl);
		myOtherProgressDialog.show();
		login.start();

	}

	public void onWindowFocusChanged(boolean bool) {
		super.onWindowFocusChanged(bool);
		Log.d("DEBUG", "onWindowFocusChanged");
		if (SignUpActivity.signup_result != null) {
			synchronized (SignUpActivity.signup_result) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				final boolean success = SignUpActivity.signup_result.success();
				dialog.setTitle(SignUpActivity.signup_result.getTitle());
				dialog.setMessage(SignUpActivity.signup_result.getMessage());
				SignUpActivity.signup_result = null;
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// if no success then user left on login screen with
						// current data ...
						if (success) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setClassName(SignUpActivity.this,
									SignUpActivity.return_to);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							if (SignUpActivity.params != null) {
								Iterator<String> iter = SignUpActivity.params
										.keySet().iterator();
								String key = null;
								while (iter.hasNext()) {
									key = iter.next();
									if (key.equals("list_id")
											&& TextUtils
													.isEmpty(SignUpActivity.params
															.get(key))) {
										Utils.putExtra(intent, key,
												Main.default_study_list_id);
									} else {
										Utils.putExtra(intent, key,
												SignUpActivity.params.get(key));
									}
								}
							}
							SignUpActivity.this.startActivity(intent);
						}
					}
				});
				dialog.show();

			}
		}
	}

	/*
	 * user[username]=kim_api_test user[email]=kim_api_test@example.com
	 * user[password]=123 user[password_confirmation]=123 api_key=abc
	 */
	public static SignUpResult signup(String username, String password,
			String email, String password_confirmation) {
		String http_response = "";
		int status_code = 0;
		AndroidHttpClient client = null;
		try {

			URI uri = new URI("http://api.smart.fm/users");
			Log.d("DEBUG", uri.toString());
			HttpPost post = new HttpPost(uri);
			// set POST body
			String post_body = "user[username]="
					+ URLEncoder.encode(username, "UTF-8") + "&user[password]="
					+ password + "&user[email]=" + email
					+ "&user[password_confirmation]=" + password_confirmation
					+ "&api_key=" + Main.API_KEY;

			Log.d("DEBUG", post_body);

			// special signup username
			String auth = "androidapp:samjoseph";
			byte[] bytes = auth.getBytes();
			post.setHeader("Authorization", "Basic "
					+ new String(Base64.encodeBase64(bytes)));

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

		return new SignUpResult(status_code, http_response);
	}
}
