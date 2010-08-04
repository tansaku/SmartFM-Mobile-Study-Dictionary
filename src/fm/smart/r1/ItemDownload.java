package fm.smart.r1;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import fm.smart.r1.activity.ItemActivity;

public abstract class ItemDownload extends Thread {
	SmartFmLookup lookup = new SmartFmLookup();
	Activity context;
	ProgressDialog progress_dialog;

	public ItemDownload(Activity context, ProgressDialog progress_dialog) {
		this.context = context;
		this.progress_dialog = progress_dialog;
	}

	public abstract Vector<Node> downloadCall(SmartFmLookup lookup);

	public void run() {
		Item item = new Item();
		Node author_node = null;
		try {
			// TODO for cancel to work we'll need to keep checking for
			// interrupted state?
			/* if (!this.isInterrupted()) */{

				item.item_node = downloadCall(lookup).firstElement();
				item.cue_node = item.item_node.get("cue").firstElement();

				item.sentences_item = item.item_node.get("sentences");
				if (item.sentences_item != null) {
					Log.d("DEBUG", "sentences_item: "
							+ item.sentences_item.toString());
					Node sentence_list = item.sentences_item.firstElement();
					Log
							.d("DEBUG", "sentence_list: "
									+ sentence_list.toString());
					item.sentences = sentence_list.get("sentence");
				}

				item.cue_text = item.cue_node.getFirstContents("text");
				item.character = item.cue_node.getFirstContents("character");
				if (!item.character.equals("")) {
					item.character = "Åu" + item.character + "Åv";
					item.cue_text += item.character;
				}
				if (item.sentences != null) {
					item.number_groups += item.sentences.size();
				}

				item.groups = new String[item.number_groups];
				item.children = new String[item.number_groups][];

				Log.d("DEBUG", "number_groups:" + item.number_groups);
				item.groups[0] = item.cue_text;
				item.children[0] = new String[1];
				item.response_node = item.item_node.get("responses")
						.firstElement().get("response").firstElement();
				item.children[0][0] = item.response_node.get("text")
						.firstElement().contents;

				item.part_of_speech = item.cue_node.atts.get("part_of_speech")
						.toString();
				author_node = item.item_node.getFirst("author");
				if (author_node != null) {
					item.author_name = author_node.getFirstContents("name");
					item.author_icon_url = author_node.getFirst("icon").atts
							.get("href").toString();
				}
				item.type = item.response_node.atts.get("type").toString();

				Bitmap author_icon_default = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.no_user_image);
				item.author_image = Main.getRemoteImage(item.author_icon_url,
						author_icon_default);

				item.cue_sound_url = item.cue_node.getFirstContents("sound");
				item.response_sound_url = item.response_node
						.getFirstContents("sound");

				if (item.sentences != null) {
					Log.d("DEBUG", "sentences: " + item.sentences.toString());
					for (int i = 0; i < item.sentences.size(); i++) {
						Vector<Node> v = item.sentences.elementAt(i)
								.get("text");
						Log.d("DEBUG", "v: " + v.toString());
						Node n = v.firstElement();
						Log.d("DEBUG", "n: " + n.toString());
						item.groups[i + 1] = n.contents;
						Log.d("DEBUG", "groups[i + 1]: " + item.groups[i + 1]);
					}
				}
				// Will need a Sentence object ...

				if (item.sentences != null) {
					for (int i = 0; i < item.sentences.size(); i++) {
						item.children[i + 1] = new String[1];
						Node sentence_node = item.sentences.elementAt(i);
						Vector<Node> translations = sentence_node
								.get("translations");
						String translation = "no translation available";
						try {
							translation = translations.firstElement().get(
									"sentence").firstElement().get("text")
									.firstElement().contents;
						} catch (Exception e) {
						}
						Vector<Node> translation_sound_vector = null;
						try {
							translation_sound_vector = translations
									.firstElement().get("sound");
						} catch (Exception e) {
						}

						item.children[i + 1][0] = translation;
						Log.d("DEBUG", item.children[i + 1][0]);

						Vector<Node> image_vector = item.sentences.elementAt(i)
								.get("square_image");

						Vector<Node> sentence_sound_vector = item.sentences
								.elementAt(i).get("sound");

						Log.d("DEBUG_SENTENCE", (String) item.sentences
								.elementAt(i).atts.toString());
						item.sentence_vector.addElement(new Sentence(
								(String) item.sentences.elementAt(i).atts
										.get("id"), item.groups[i + 1],
								image_vector, sentence_sound_vector,
								item.children[i + 1][0],
								translation_sound_vector));

					}
				}
				fm.smart.r1.activity.ItemActivity.item = item;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// return;
		}

		progress_dialog.dismiss();
		if (item.item_node == null || item.cue_text == null) {
			// some sort of failure
			((Activity) context).runOnUiThread(new Thread() {
				public void run() {
					final AlertDialog dialog = new AlertDialog.Builder(context)
							.create();
					dialog.setTitle("Network Failure");
					dialog.setMessage("Please try again later");
					dialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					// TODO suggest to user to upload new sound?
					dialog.show();
				}
			});
		} else if (!this.isInterrupted()) {
			// context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
			// .parse("content://" + SmartFm.AUTHORITY + "/item/7")));

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context, ItemActivity.class.getName());
			context.startActivity(intent);
		}
	}

}
