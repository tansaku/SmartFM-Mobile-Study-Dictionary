package fm.smart.r1;

import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import fm.smart.r1.activity.ListsList;

public abstract class ListDownload extends Thread {
	final Bitmap mIcon2;
	SmartFmLookup lookup = new SmartFmLookup();
	Activity context;
	ProgressDialog progress_dialog;

	public ListDownload(Activity context, ProgressDialog progress_dialog) {
		this.context = context;
		this.progress_dialog = progress_dialog;
		mIcon2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.default_goal_icon);
	}

	public abstract Vector<Node> downloadCall(SmartFmLookup lookup);

	public void run() {
		Iterator<Node> iter = new Vector<Node>().iterator();
		try {

			ListsList.items = downloadCall(lookup);
			iter = ListsList.items.iterator();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Node item = null;
		ListsList.icons = new Vector<Bitmap>();
		while (iter.hasNext()) {
			item = iter.next();
			ListsList.icons.addElement(Main.getRemoteImage((String) item.get(
					"square_icon").firstElement().atts.get("href"), mIcon2));
		}
		progress_dialog.dismiss();
		if (ListsList.items == null) {
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
		} else if (!this.isInterrupted()){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context,ListsList.class.getName());
			context.startActivity(intent);
			
			//context.startActivity(new Intent(Intent.ACTION_VIEW,
				//	SmartFm.Lists.CONTENT_URI));
		}
	}

}
