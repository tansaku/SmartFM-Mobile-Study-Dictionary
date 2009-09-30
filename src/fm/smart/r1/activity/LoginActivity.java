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
import org.apache.http.client.methods.HttpGet;
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
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Main;
import fm.smart.r1.Utils;
import fm.smart.r1.R;

public class LoginActivity extends Activity implements View.OnClickListener {
	protected static LoginResult login_result;
	protected static CreateListResult create_default_list;
	WebView webview = null;
	public static String return_to = Main.class.getName();
	String url = "";
	static String username = null;
	static String password = null;
	public static HashMap<String, String> params;

	// TODO so make this page our own username/password form ... already stored
	// in preferences
	// TODO start null and ask for login when about to do post operation if
	// still null ...
	// and switch all comms to https when Cerego is ready.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);
		setContentView(R.layout.login);

		final Button signup_button = (Button) findViewById(R.id.signup);
		signup_button.setOnClickListener(this);
		// signup_button.setEnabled(false);
		final Button login_button = (Button) findViewById(R.id.login);
		login_button.setOnClickListener(this);
		// login_button.setEnabled(false);

		final TextView username_view = (TextView) findViewById(R.id.username);
		final TextView password_view = (TextView) findViewById(R.id.password);

		username_view.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (v.equals(username_view)) {
					if (event != null) {
						if (!TextUtils.isEmpty(((TextView) v).getText())) {
							password_view.requestFocus();
						}
						Log.d("USERNAME-KEY-DEBUG", "enter key pressed");
						return true;
					}
				}
				return false;
			}
		});
		// username_view.setOnKeyListener(new OnKeyListener() {
		//
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (v.equals(username_view)) {
		// Log.d("USERNAME-KEY-DEBUG", "key pressed: " + keyCode);
		// switch (keyCode) {
		// case KeyEvent.KEYCODE_ENTER:
		// if (event.getAction() == KeyEvent.ACTION_UP) {
		// if (!TextUtils.isEmpty(((TextView) v).getText())) {
		// password_view.requestFocus();
		// }
		// Log.d("USERNAME-KEY-DEBUG", "enter key pressed");
		// return true;
		// }
		// }
		// }
		// return false;
		// }
		// });
		password_view.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (v.equals(password_view)) {
					if (!TextUtils.isEmpty(((TextView) v).getText())) {
						signup_button.setEnabled(true);
						login_button.setEnabled(true);
						if (event != null) {
							onClick(v);
							Log.d("PASSWORD-KEY-DEBUG", "enter key pressed");
							return true;
						}
					}
				}
				return false;
			}
		});
		// password_view.setOnKeyListener(new OnKeyListener() {
		//
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (v.equals(password_view)) {
		// Log.d("PASSWORD-KEY-DEBUG", "key pressed: " + keyCode);
		// if (!TextUtils.isEmpty(((TextView) v).getText())) {
		// signup_button.setEnabled(true);
		// login_button.setEnabled(true);
		// }
		// switch (keyCode) {
		// case KeyEvent.KEYCODE_ENTER:
		// if (event.getAction() == KeyEvent.ACTION_UP) {
		// onClick(v);
		// Log.d("PASSWORD-KEY-DEBUG", "enter key pressed");
		// return true;
		// }
		// }
		// }
		// return false;
		// }
		// });

	}

	public void onClick(View v) {
		EditText usernameInput = (EditText) findViewById(R.id.username);
		EditText passwordInput = (EditText) findViewById(R.id.password);

		final String username = usernameInput.getText().toString();
		LoginActivity.username = username;
		final String password = passwordInput.getText().toString();
		LoginActivity.password = password;

		if (v.getId() == R.id.signup) {
			Log.d("CLICK-DEBUG", "Pressed Signup Button");

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(LoginActivity.this, SignUpActivity.class
					.getName());
			SignUpActivity.username = LoginActivity.username;
			SignUpActivity.password = LoginActivity.password;
			SignUpActivity.return_to = LoginActivity.return_to;
			SignUpActivity.params = LoginActivity.params;
			this.startActivity(intent);
		}
		if (v.getId() == R.id.login || v.getId() == R.id.password) {
			Log.d("CLICK-DEBUG", "Pressed Login Button");

			final ProgressDialog myOtherProgressDialog = new ProgressDialog(
					this);
			myOtherProgressDialog.setTitle("Please Wait ...");
			myOtherProgressDialog.setMessage("Logging in ...");
			myOtherProgressDialog.setIndeterminate(true);
			myOtherProgressDialog.setCancelable(true);

			final Thread login = new Thread() {
				public void run() {
					// TODO make this interruptable .../*if
					// (!this.isInterrupted())*/
					LoginActivity.login_result = login(username, password);
					if (LoginActivity.login_result.success()) {
						Main.login(LoginActivity.this, LoginActivity.username,
								LoginActivity.password);
						// TODO set users default study list ...
						// starting to look like the work to support this
						// should go on buckets ...
						LoginActivity.create_default_list = Main
								.getDefaultStudyList(LoginActivity.this);
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

	}

	public void onWindowFocusChanged(boolean bool) {
		super.onWindowFocusChanged(bool);
		Log.d("DEBUG", "onWindowFocusChanged");
		if (LoginActivity.login_result != null) {
			synchronized (LoginActivity.login_result) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				final boolean success = LoginActivity.login_result.success();

				dialog.setTitle(LoginActivity.login_result.getTitle());
				dialog.setMessage(LoginActivity.login_result.getMessage());
				LoginActivity.login_result = null;
				dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// if no success then user left on login screen with
						// current data ...
						if (success) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setClassName(LoginActivity.this,
									LoginActivity.return_to);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							// to remove login from stack?
							if (LoginActivity.params != null) {
								Iterator<String> iter = LoginActivity.params
										.keySet().iterator();
								String key = null;
								while (iter.hasNext()) {
									key = iter.next();
									if (key.equals("list_id")
											&& TextUtils
													.isEmpty(LoginActivity.params
															.get(key))) {
										Utils.putExtra(intent, key,
												Main.default_study_list_id);
									} else {
										Utils.putExtra(intent, key,
												LoginActivity.params.get(key));
									}

								}
							}
							LoginActivity.this.startActivity(intent);
						}
					}
				});
				dialog.show();

			}
		}
	}

	// TODO run this on emulator and see why languages not being set
	public static CreateListResult createList(String name, String description,
			String source_language, String translation_language,
			Activity activity) {
		String http_response = "";
		int status_code = 0;
		AndroidHttpClient client = null;
		try {

			URI uri = new URI("http://api.smart.fm/lists");
			Log.d("DEBUG", uri.toString());
			HttpPost post = new HttpPost(uri);
			// Main.consumer.setTokenWithSecret(Main.ACCESS_TOKEN,
			// Main.TOKEN_SECRET);// TODO store in preferences ...
			// Main.consumer.sign(post);
			// set POST body
			String post_body = "list[name]=" + URLEncoder.encode(name, "UTF-8")
					+ "&list[description]="
					+ URLEncoder.encode(description, "UTF-8")
					+ "&list[language]=" + source_language
					+ "&list[translation_language]=" + translation_language
					+ "&api_key=" + Main.API_KEY;

			Log.d("DEBUG", post_body);
			// hmm not sure here if this username.password should be the same
			// as the user one? does that username/password have to match the
			// API key?
			String auth = Main.username(activity) + ":"
					+ Main.password(activity);
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

		return new CreateListResult(status_code, http_response);
	}

	public static LoginResult login(String username, String password) {
		String http_response = "";
		int status_code = 0;
		AndroidHttpClient client = null;
		try {

			URI uri = new URI("http://api.smart.fm/users");
			Log.d("DEBUG", uri.toString());
			HttpGet get = new HttpGet(uri);

			// hmm not sure here if this username.password should be the same
			// as the user one? does that username/password have to match the
			// API key? apparently not
			String auth = username + ":" + password;
			byte[] bytes = auth.getBytes();
			get.setHeader("Authorization", "Basic "
					+ new String(Base64.encodeBase64(bytes)));
			// post.setHeader("Authorization",
			// "Basic dGFuc2FrdTpzYW1qb3NlcGg=");
			// [B@434f6610
			// setting content-type is overwritten ...
			get.setHeader("Content-Type", "application/x-www-form-urlencoded");

			get.setHeader("Host", "api.smart.fm");

			Header[] array = get.getAllHeaders();
			for (Header h : array) {
				Log.d("DEBUG", h.toString());
			}
			client = AndroidHttpClient.newInstance("Main");
			HttpResponse response1 = client.execute(get);
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

		return new LoginResult(status_code, http_response);
	}
}
