package fm.smart.r1;

import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import fm.smart.r1.activity.ItemListActivity;

public abstract class ItemListDownload extends Thread {
	SmartFmLookup lookup = new SmartFmLookup();
	Activity context;
	ProgressDialog progress_dialog;

	public ItemListDownload(Activity context, ProgressDialog progress_dialog) {
		this.context = context;
		this.progress_dialog = progress_dialog;
	}

	public abstract HashMap<String, Vector<Node>> downloadCall(
			SmartFmLookup lookup);

	public void run() {
		HashMap<String, Vector<Node>> map = null;
		try {
			if (!this.isInterrupted()) {
				map = downloadCall(lookup);
				ItemListActivity.items = map.get("item");
				ItemListActivity.number_results = Integer.parseInt(map.get(
						"totalResults").firstElement().contents);
				ItemListActivity.start_index = Integer.parseInt(map.get(
						"startIndex").firstElement().contents);
				ItemListActivity.items_per_page = Integer.parseInt(map.get(
						"itemsPerPage").firstElement().contents);
				ItemListActivity.query_string = map.get("Query").firstElement().atts
						.get("searchTerms").toString();
				ItemListActivity.cue_language = Utils.INV_LANGUAGE_MAP
						.get(map.get("item").firstElement().get("cue")
								.firstElement().atts.get("language").toString());
				ItemListActivity.response_language = Utils.INV_LANGUAGE_MAP
						.get(map.get("item").firstElement().get("response")
								.firstElement().atts.get("language").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			// return;
		}

		progress_dialog.dismiss();

		if (map == null || map.get("items") == null) {
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
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context,ItemListActivity.class.getName());
			context.startActivity(intent);
		}
	}

}
