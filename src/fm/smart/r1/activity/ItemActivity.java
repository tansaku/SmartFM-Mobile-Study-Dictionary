/*
 * Copyright (C) 2008 The Android Open Source Project
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Item;
import fm.smart.r1.Main;
import fm.smart.r1.R;
import fm.smart.r1.Sentence;
import fm.smart.r1.Utils;

/**
 * Okay, so seems like I should have been customising a listActivity rather than
 * drawing things as part of a table:
 * http://developer.android.com/reference/android/app/ListActivity.html I can
 * specify my own row layouts, and the surround the list itself with other stuff
 * 
 */
public class ItemActivity extends ListActivity {
	private static final int CREATE_EXAMPLE_ID = Menu.FIRST;
	private static final int CREATE_SOUND_ID = Menu.FIRST + 1;
	private static final int ADD_TO_LIST_ID = Menu.FIRST + 2;
	private static final int SELECT_IMAGE = 0;

	public static Item item;
	public static AddItemResult add_item_result = null;
	public static AddImageResult add_image_result = null;
	protected static AddSentenceResult add_sentence_list_result;
	private static boolean shown_toast = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this);

		// could check here to see if this was suspended for login and go
		// straight to add_item ...

		setContentView(R.layout.item);

		/*
		 * ImageView author_icon = (ImageView)
		 * findViewById(R.id.item_author_icon); if (item.author_image != null){
		 * author_icon.setImageBitmap(item.author_image); }
		 */

		TextView cue_and_pronunciation = (TextView) findViewById(R.id.cue_and_pronunciation);
		cue_and_pronunciation.setText(item.cue_text);  // TODO handle case where item is null?
		TextView cue_part_of_speech = (TextView) findViewById(R.id.cue_part_of_speech);
		if (!TextUtils.equals(item.part_of_speech, "None")) {
			cue_part_of_speech.setText(item.part_of_speech);
		} else {
			cue_part_of_speech.setVisibility(View.INVISIBLE);
		}
		/*
		 * TextView author = (TextView) findViewById(R.id.item_author);
		 * author.setText(item.author_name);
		 */

		TextView response_and_pronunciation = (TextView) findViewById(R.id.response_and_pronunciation);
		response_and_pronunciation.setText(item.children[0][0]);
		TextView response_part_of_speech = (TextView) findViewById(R.id.response_part_of_speech);
		if (item.type != null && item.type.equals("meaning")) {
			item.type = "Translation";
		}
		response_part_of_speech.setText(item.type);
		response_part_of_speech.setVisibility(View.INVISIBLE);

		ImageView cue_sound = (ImageView) findViewById(R.id.cue_sound);
		setSound(cue_sound, item.cue_sound_url, this, R.id.cue_sound,
				(String) item.item_node.atts.get("id"), item.cue_text);

		ImageView response_sound = (ImageView) findViewById(R.id.response_sound);
		setSound(response_sound, item.response_sound_url, this,
				R.id.response_sound, (String) item.item_node.atts.get("id"),
				item.response_node.getFirstContents("text"));
		EfficientAdapter adapter = new EfficientAdapter(ItemActivity.this,
				item.sentence_vector);
		setListAdapter(adapter);
		if (adapter.getCount() == 0 && !ItemActivity.shown_toast ) {
			Toast t = Toast.makeText(this,
					"Know a good example? Click the menu button to add one",
					250);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			ItemActivity.shown_toast = true;
		}
		// notify();
	}

	static void setSound(ImageView sound_icon, final String sound_url,
			final Context context, final int type_id, final String artifact_id,
			final String to_record) {
		if (!TextUtils.isEmpty(sound_url)) {
			OnClickListener sound_listener = new OnClickListener() {
				public void onClick(View v) {
					Main.playSound(sound_url, ItemListActivity.mMediaPlayer,
							context);
				}
			};
			sound_icon.setOnClickListener(sound_listener);
		} else {
			if (type_id == R.id.response_sound
					|| type_id == R.id.translation_sound) {
				sound_icon.setVisibility(View.INVISIBLE);
			} else {
				sound_icon.setImageBitmap(BitmapFactory.decodeResource(context
						.getResources(), R.drawable.inactive_sound_add));

				OnClickListener listener = new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setClassName(context, CreateSoundActivity.class
								.getName());
						Utils.putExtra(intent, "item_id",
								(String) item.item_node.atts.get("id"));
						Utils.putExtra(intent, "to_record", to_record);
						Utils.putExtra(intent, "id", artifact_id);
						Utils.putExtra(intent, "sound_type", Integer
								.toString(type_id));
						context.startActivity(intent);

					}
				};
				sound_icon.setOnClickListener(listener);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, CREATE_EXAMPLE_ID, 0, R.string.menu_create_example)
				.setIcon(android.R.drawable.ic_menu_add);
		// remove until we have it working ...
		// menu.add(0, CREATE_SOUND_ID, 0, R.string.menu_create_sound).setIcon(
		// R.drawable.microphone);

		menu.add(0, ADD_TO_LIST_ID, 0, R.string.menu_add_to_list).setIcon(
				android.R.drawable.ic_menu_add);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menu_item) {
		switch (menu_item.getItemId()) {
		case CREATE_EXAMPLE_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(this, CreateExampleActivity.class.getName());
			Utils.putExtra(intent, "item_id", (String) item.item_node.atts
					.get("id"));
			Utils.putExtra(intent, "cue", item.cue_text);
			Utils.putExtra(intent, "example_language", Utils.INV_LANGUAGE_MAP
					.get(item.cue_node.atts.get("language").toString()));
			Utils.putExtra(intent, "translation_language",
					Utils.INV_LANGUAGE_MAP.get(item.response_node.atts.get(
							"language").toString()));
			startActivity(intent);
			break;
		}
		case CREATE_SOUND_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(this, CreateSoundActivity.class.getName());
			Utils.putExtra(intent, "item_id", (String) item.item_node.atts
					.get("id"));
			startActivity(intent);
			break;
		}
		case ADD_TO_LIST_ID: {
			// TODO inserting login request here more complicated in as much as
			// this is not an activity we can simply return to with parameters
			// although we could jump to this from switch in onCreate
			// of course there's probably a swish URI way to call, but may take
			// time to get it just right ...
			// or should just bring them back and open menu bar ...
			if (Main.isNotLoggedIn(this)) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName(this, LoginActivity.class.getName());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // avoid
				// navigation
				// back to
				// this?
				LoginActivity.return_to = ItemActivity.class.getName();
				LoginActivity.params = new HashMap<String, String>();
				LoginActivity.params.put("item_id",
						(String) item.item_node.atts.get("id"));
				startActivity(intent);
			} else {
				addToList((String) item.item_node.atts.get("id"));
			}

			break;
		}
		}
		return super.onOptionsItemSelected(menu_item);
	}

	public void addToList(final String item_id) {
		final ProgressDialog myOtherProgressDialog = new ProgressDialog(this);
		myOtherProgressDialog.setTitle("Please Wait ...");
		myOtherProgressDialog.setMessage("Adding item to study list ...");
		myOtherProgressDialog.setIndeterminate(true);
		myOtherProgressDialog.setCancelable(true);

		final Thread add = new Thread() {
			public void run() {
				ItemActivity.add_item_result = addItemToList(
						Main.default_study_list_id, item_id, ItemActivity.this);

				myOtherProgressDialog.dismiss();
				ItemActivity.this.runOnUiThread(new Thread() {
					public void run() {
						final AlertDialog dialog = new AlertDialog.Builder(
								ItemActivity.this).create();
						dialog
								.setTitle(ItemActivity.add_item_result
										.getTitle());
						dialog.setMessage(ItemActivity.add_item_result
								.getMessage());
						ItemActivity.add_item_result = null;
						dialog.setButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});

						dialog.show();
					}
				});

			}
		};
		myOtherProgressDialog.setButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						add.interrupt();
					}
				});
		OnCancelListener ocl = new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				add.interrupt();
			}
		};
		myOtherProgressDialog.setOnCancelListener(ocl);
		closeMenu();
		myOtherProgressDialog.show();
		add.start();
	}

	public void closeMenu() {
		this.getWindow().closePanel(Window.FEATURE_OPTIONS_PANEL);
	}

	// POST /lists/:list_id/items
	// DELETE /lists/:list_id/items/:id
	public static AddItemResult addItemToList(String list_id, String item_id,
			Activity activity) {
		String http_response = "";
		int status_code = 0;
		HttpClient client = null;
		try {
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://api.smart.fm/lists/" + list_id
					+ "/items");
			// Main.consumer.setTokenWithSecret(Main.ACCESS_TOKEN,
			// Main.TOKEN_SECRET);
			// Main.consumer.sign(post);

			String auth = Main.username(activity) + ":"
					+ Main.password(activity);
			byte[] bytes = auth.getBytes();
			post.setHeader("Authorization", "Basic "
					+ new String(Base64.encodeBase64(bytes)));

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			post.setHeader("Host", "api.smart.fm");
			HttpEntity entity = new StringEntity("id=" + item_id + "&api_key="
					+ Main.API_KEY, "UTF-8");
			post.setEntity(entity);

			Header[] array = post.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("DEBUG", array[i].toString());
			}

			Log
					.d("AddItemTolist", "executing request "
							+ post.getRequestLine());
			HttpResponse response = client.execute(post);
			status_code = response.getStatusLine().getStatusCode();

			HttpEntity resEntity = response.getEntity();

			Log.d("AddItemTolist", "----------------------------------------");
			Log.d("AddItemTolist", response.getStatusLine().toString());
			array = response.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("AddItemTolist", array[i].toString());
			}
			if (resEntity != null) {
				Log.d("AddItemTolist", "Response content length: "
						+ resEntity.getContentLength());
				Log.d("AddItemTolist", "Chunked?: " + resEntity.isChunked());
			}
			long length = response.getEntity().getContentLength();
			byte[] response_bytes = new byte[(int) length];
			response.getEntity().getContent().read(response_bytes);
			Log.d("AddItemTolist", new String(response_bytes));
			http_response = new String(response_bytes);
			if (resEntity != null) {
				resEntity.consumeContent();
			}

		} catch (IOException e) {
			e.printStackTrace();
			http_response = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			http_response = e.getMessage();
		}

		return new AddItemResult(status_code, http_response);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// this should be called once image has been chosen by user
		// using requestCode to pass item id - haven't worked out any other way
		// to do it
		// if (requestCode == SELECT_IMAGE)
		if (resultCode == Activity.RESULT_OK) {
			// TODO check if user is logged in
			if (Main.isNotLoggedIn(this)) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName(this, LoginActivity.class.getName());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				// avoid navigation back to this?
				LoginActivity.return_to = ItemActivity.class.getName();
				LoginActivity.params = new HashMap<String, String>();
				LoginActivity.params.put("item_id",
						(String) item.item_node.atts.get("id"));
				startActivity(intent);
				// TODO in this case forcing the user to rechoose the image
				// seems a little
				// rude - should probably auto-submit here ...
			} else {
				// Bundle extras = data.getExtras();
				// String sentence_id = (String) extras.get("sentence_id");
				final ProgressDialog myOtherProgressDialog = new ProgressDialog(
						this);
				myOtherProgressDialog.setTitle("Please Wait ...");
				myOtherProgressDialog.setMessage("Uploading image ...");
				myOtherProgressDialog.setIndeterminate(true);
				myOtherProgressDialog.setCancelable(true);

				final Thread add_image = new Thread() {
					public void run() {
						// TODO needs to check for interruptibility

						String sentence_id = Integer.toString(requestCode);
						Uri selectedImage = data.getData();
						// Bitmap bitmap = Media.getBitmap(getContentResolver(),
						// selectedImage);
						// ByteArrayOutputStream bytes = new
						// ByteArrayOutputStream();
						// bitmap.compress(Bitmap.CompressFormat.JPEG, 40,
						// bytes);
						// ByteArrayInputStream fileInputStream = new
						// ByteArrayInputStream(
						// bytes.toByteArray());

						// TODO Might have to save to file system first to get
						// this
						// to work,
						// argh!
						// could think of it as saving to cache ...

						// add image to sentence
						FileInputStream is = null;
						FileOutputStream os = null;
						File file = null;
						ContentResolver resolver = getContentResolver();
						try {
							Bitmap bitmap = Media.getBitmap(
									getContentResolver(), selectedImage);
							ByteArrayOutputStream bytes = new ByteArrayOutputStream();
							bitmap.compress(Bitmap.CompressFormat.JPEG, 40,
									bytes);
							// ByteArrayInputStream bais = new
							// ByteArrayInputStream(bytes.toByteArray());

							// FileDescriptor fd =
							// resolver.openFileDescriptor(selectedImage,
							// "r").getFileDescriptor();
							// is = new FileInputStream(fd);

							String filename = "test.jpg";
							File dir = ItemActivity.this.getDir("images",
									MODE_WORLD_READABLE);
							file = new File(dir, filename);
							os = new FileOutputStream(file);

							// while (bais.available() > 0) {
							// / os.write(bais.read());
							// }
							os.write(bytes.toByteArray());

							os.close();

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (os != null) {
								try {
									os.close();
								} catch (IOException e) {
								}
							}
							if (is != null) {
								try {
									is.close();
								} catch (IOException e) {
								}
							}
						}

						// File file = new
						// File(Uri.decode(selectedImage.toString()));

						// ensure item is in users default list

						ItemActivity.add_item_result = addItemToList(
								Main.default_study_list_id,
								(String) item.item_node.atts.get("id"),
								ItemActivity.this);
						Result result = ItemActivity.add_item_result;

						if (ItemActivity.add_item_result.success()
								|| ItemActivity.add_item_result.alreadyInList()) {

							// ensure sentence is in users default list

							ItemActivity.add_sentence_list_result = addSentenceToList(
									sentence_id, (String) item.item_node.atts
											.get("id"),
									Main.default_study_list_id,
									ItemActivity.this);
							result = ItemActivity.add_sentence_list_result;
							if (ItemActivity.add_sentence_list_result.success()) {

								String media_entity = "http://test.com/test.jpg";
								String author = "tansaku";
								String author_url = "http://smart.fm/users/tansaku";
								Log.d("DEBUG-IMAGE-URI", selectedImage
										.toString());
								ItemActivity.add_image_result = addImage(file,
										media_entity, author, author_url, "1",
										sentence_id,
										(String) item.item_node.atts.get("id"),
										Main.default_study_list_id);
								result = ItemActivity.add_image_result;
							}
						}
						final Result display = result;
						myOtherProgressDialog.dismiss();
						ItemActivity.this.runOnUiThread(new Thread() {
							public void run() {
								final AlertDialog dialog = new AlertDialog.Builder(
										ItemActivity.this).create();
								dialog.setTitle(display.getTitle());
								dialog.setMessage(display.getMessage());
								dialog.setButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												if (ItemActivity.add_image_result != null
														&& ItemActivity.add_image_result
																.success()) {
													ItemListActivity
															.loadItem(
																	ItemActivity.this,
																	item.item_node.atts
																			.get(
																					"id")
																			.toString());
												}
											}
										});

								dialog.show();
							}
						});

					}

				};

				myOtherProgressDialog.setButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								add_image.interrupt();
							}
						});
				OnCancelListener ocl = new OnCancelListener() {
					public void onCancel(DialogInterface arg0) {
						add_image.interrupt();
					}
				};
				myOtherProgressDialog.setOnCancelListener(ocl);
				closeMenu();
				myOtherProgressDialog.show();
				add_image.start();

			}
		}
	}

	// POST /lists/:list_id/items/:item_id/sentences
	public static AddSentenceResult addSentenceToList(String sentence_id,
			String item_id, String list_id, Activity activity) {
		String http_response = "";
		int status_code = 0;
		HttpClient client = null;
		try {
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://api.smart.fm/lists/" + list_id
					+ "/items/" + item_id + "/sentences");

			String auth = Main.username(activity) + ":"
					+ Main.password(activity);
			byte[] bytes = auth.getBytes();
			post.setHeader("Authorization", "Basic "
					+ new String(Base64.encodeBase64(bytes)));

			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			post.setHeader("Host", "api.smart.fm");
			HttpEntity entity = new StringEntity("id=" + sentence_id
					+ "&api_key=" + Main.API_KEY, "UTF-8");
			post.setEntity(entity);

			Header[] array = post.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("DEBUG", array[i].toString());
			}

			Log.d("AddSentenceTolist", "executing request "
					+ post.getRequestLine());
			HttpResponse response = client.execute(post);
			status_code = response.getStatusLine().getStatusCode();

			HttpEntity resEntity = response.getEntity();

			Log.d("AddSentenceTolist",
					"----------------------------------------");
			Log.d("AddSentenceTolist", response.getStatusLine().toString());
			array = response.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("AddSentenceTolist", array[i].toString());
			}
			if (resEntity != null) {
				Log.d("AddSentenceTolist", "Response content length: "
						+ resEntity.getContentLength());
				Log
						.d("AddSentenceTolist", "Chunked?: "
								+ resEntity.isChunked());
			}
			long length = response.getEntity().getContentLength();
			byte[] response_bytes = new byte[(int) length];
			response.getEntity().getContent().read(response_bytes);
			Log.d("AddSentenceTolist", new String(response_bytes));
			http_response = new String(response_bytes);
			if (resEntity != null) {
				resEntity.consumeContent();
			}

		} catch (IOException e) {
			e.printStackTrace();
			http_response = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			http_response = e.getMessage();
		}

		return new AddSentenceResult(status_code, http_response);

	}

	// POST http://api.smart.fm/sentences/:sentence_id/images
	public AddImageResult addImage(File file, String media_entity,
			String author, String author_url, String attribution_license_id,
			String sentence_id, String item_id, String list_id) {
		String http_response = "";
		int status_code = 0;
		HttpClient client = null;
		try {
			client = new DefaultHttpClient();
			HttpPost post = new HttpPost("http://api.smart.fm/lists/" + list_id
					+ "/items/" + item_id + "/sentences/" + sentence_id
					+ "/images");
			// HttpPost post = new HttpPost("http://api.smart.fm/sentences/" +
			// sentence_id
			// + "/images");

			String auth = Main.username(this) + ":" + Main.password(this);
			byte[] bytes = auth.getBytes();
			post.setHeader("Authorization", "Basic "
					+ new String(Base64.encodeBase64(bytes)));

			// httppost.setHeader("Content-Type",
			// "application/x-www-form-urlencoded");

			post.setHeader("Host", "api.smart.fm");

			FileBody bin = new FileBody(file, "image/jpeg");
			StringBody media_entity_part = new StringBody(media_entity);
			StringBody author_part = new StringBody(author);
			StringBody author_url_part = new StringBody(author_url);
			StringBody attribution_license_id_part = new StringBody(
					attribution_license_id);
			StringBody sentence_id_part = new StringBody(sentence_id);
			StringBody api_key_part = new StringBody(Main.API_KEY);
			StringBody item_id_part = new StringBody(item_id);
			StringBody list_id_part = new StringBody(list_id);

			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("image[file]", bin);
			reqEntity.addPart("media_entity", media_entity_part);
			reqEntity.addPart("author", author_part);
			reqEntity.addPart("author_url", author_url_part);
			reqEntity.addPart("attribution_license_id",
					attribution_license_id_part);
			reqEntity.addPart("sentence_id", sentence_id_part);
			reqEntity.addPart("api_key", api_key_part);
			reqEntity.addPart("item_id", item_id_part);
			reqEntity.addPart("list_id", list_id_part);

			post.setEntity(reqEntity);

			Header[] array = post.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("DEBUG", array[i].toString());
			}

			Log.d("AddImage", "executing request " + post.getRequestLine());
			HttpResponse response = client.execute(post);
			HttpEntity resEntity = response.getEntity();
			status_code = response.getStatusLine().getStatusCode();

			Log.d("AddImage", "----------------------------------------");
			Log.d("AddImage", response.getStatusLine().toString());
			array = response.getAllHeaders();
			for (int i = 0; i < array.length; i++) {
				Log.d("AddImage", array[i].toString());
			}
			if (resEntity != null) {
				Log.d("AddImage", "Response content length: "
						+ resEntity.getContentLength());
				Log.d("AddImage", "Chunked?: " + resEntity.isChunked());
			}
			long length = response.getEntity().getContentLength();
			byte[] response_bytes = new byte[(int) length];
			response.getEntity().getContent().read(response_bytes);
			Log.d("AddImage", new String(response_bytes));
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

		return new AddImageResult(status_code, http_response);
	}

	public static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Vector<Sentence> sentence_vector;
		Context context;

		public EfficientAdapter(Context context,
				Vector<Sentence> sentence_vector) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			this.context = context;
			mInflater = LayoutInflater.from(this.context);
			this.sentence_vector = sentence_vector;
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 * 
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			int count = 0;
			try {
				count = this.sentence_vector.size();
			} catch (Exception e) {
			}
			return count;
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 * 
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return sentence_vector.elementAt(position);
		}

		/**
		 * Use the array index as a unique id.
		 * 
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary calls
			// to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no need
			// to reinflate it. We only inflate a new View when the convertView
			// supplied
			// by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.sentence, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();

				holder.sentence = (TextView) convertView
						.findViewById(R.id.sentence);
				holder.sentence_image = (ImageView) convertView
						.findViewById(R.id.sentence_image);
				holder.sentence_sound = (ImageView) convertView
						.findViewById(R.id.sentence_sound);
				holder.translation = (TextView) convertView
						.findViewById(R.id.sentence_translation);
				holder.translation_sound = (ImageView) convertView
						.findViewById(R.id.translation_sound);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			Sentence sentence = (Sentence) getItem(position);
			holder.sentence.setText(Html.fromHtml(sentence.text));
			setImage(holder.sentence_image, sentence.image, position);
			setSound(holder.sentence_sound, sentence.sound_url, context,
					R.id.sentence_sound,
					sentence_vector.elementAt(position).id, sentence.text);
			holder.translation.setText(Html.fromHtml(sentence.translation));
			setSound(holder.translation_sound, sentence.translation_sound_url,
					context, R.id.translation_sound, sentence.id,
					sentence.translation);

			return convertView;
		}

		private void setImage(ImageView image_view, final Bitmap image,
				final int position) {
			if (image != null) {
				image_view.setImageBitmap(image);
			} else {
				OnClickListener listener = new OnClickListener() {
					public void onClick(View v) {
						// this opens image chooser
						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
						Log.d("DEBUG-SENTENCE2", sentence_vector
								.elementAt(position).id);
						Utils.putExtra(intent, "sentence_id", sentence_vector
								.elementAt(position).id);
						((Activity) context).startActivityForResult(intent,
								Integer.parseInt(sentence_vector
										.elementAt(position).id));

					}
				};
				image_view.setOnClickListener(listener);
				image_view.setImageBitmap(BitmapFactory.decodeResource(context
						.getResources(), R.drawable.camera));
				// image_view.setVisibility(View.GONE);
			}
		}

		public static class ViewHolder {
			TextView sentence;
			ImageView sentence_image;
			ImageView sentence_sound;
			TextView translation;
			ImageView translation_sound;
		}

	}

}
