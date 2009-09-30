package fm.smart.r1.activity;

import java.util.Vector;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Node;
import fm.smart.r1.R;
import fm.smart.r1.SmartFmLookup;
import fm.smart.r1.SmartFmMenus;

/**
 * SmartFM Lists. The adapter used in this
 * example binds to an ImageView and to a TextView for each row in the list.
 * 
 * To work efficiently the adapter implemented here uses two techniques: - It
 * reuses the convertView passed to getView() to avoid inflating View when it is
 * not necessary - It uses the ViewHolder pattern to avoid calling
 * findViewById() when it is not necessary
 * 
 * The ViewHolder pattern consists in storing a data structure in the tag of the
 * view returned by getView(). This data structures contains references to the
 * views we want to bind data to, thus avoiding calls to findViewById() every
 * time getView() is invoked.
 */
public class ListsList extends ListActivity {

	public static ProgressDialog myProgressDialog;
	public static Node selected;
	public static Vector<Node> items;
	public static Vector<Bitmap> icons = new Vector<Bitmap>();
	private SmartFmMenus menus;

	@Override
	public void onConfigurationChanged(Configuration config) {
		// Do nothing, this is to prevent the activity from being restarted when
		// the keyboard opens.
		super.onConfigurationChanged(config);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		ExceptionHandler.register(this);
		menus = new SmartFmMenus(this);
		this.setContentView(R.layout.list_list);

		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();
		if (queryIntent.getAction() == null)
			queryIntent.setAction(Intent.ACTION_VIEW);
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			doSearchQuery(queryIntent, "onCreate()");
		}
		setListAdapter(new EfficientAdapter(this, items, icons));
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		String action = getIntent().getAction();

		if (action.equals(Intent.ACTION_PICK)
				|| action.equals(Intent.ACTION_GET_CONTENT)) {
			Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

			Intent intent = getIntent();
			intent.setData(uri);
			setResult(RESULT_OK, intent);
		} else {

			// so display progress bar here and do download before switching
			// activity
			myProgressDialog = ProgressDialog.show(ListsList.this,
					"Please wait...", "Downloading SmartFm list items ...",
					true, true);
			selected = (Node) (this.getListAdapter().getItem(position));
			new Thread() {
				public void run() {
					SmartFmLookup smart_fm = new SmartFmLookup();
					String id_string = (String) ListsList.selected.atts
							.get("id");
					long id = new Long(id_string).longValue();
					ItemListActivity.items = smart_fm.listItems(id_string);
					//Uri uri = ContentUris.withAppendedId(
					//		SmartFm.Items.CONTENT_URI_LIST, id);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setClassName(ListsList.this,ItemListActivity.class.getName());
					myProgressDialog.dismiss();
					startActivity(intent);
					//startActivity(new Intent(Intent.ACTION_VIEW, uri));
				}
			}.start();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		menus.onOptionsItemSelected(item, this);
		return super.onOptionsItemSelected(item);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return menus.onCreateOptionsMenu(menu);
	}

	public static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Vector<Bitmap> icons;
		private Vector<Node> items;

		public EfficientAdapter(Context context, Vector<Node> items,
				Vector<Bitmap> icons) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			mInflater = LayoutInflater.from(context);
			this.items = items;
			this.icons = icons;
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
				count = this.items.size();
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
			return items.elementAt(position);
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
				convertView = mInflater.inflate(R.layout.list_item_icon_text,
						null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.text);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			// holder.text.setText(items.elementAt(position).get("cue").firstElement().get("text").firstElement().contents);
			String name = "";
			try
			{
			  name = items.elementAt(position).get("title").firstElement().contents;
			  name = name.replaceAll("&quot;", "\"");
			  name = name.replaceAll("&amp;", "&");
			}
			catch(Exception e)
			{}
			

			holder.text.setText(name);
			Log.d("TEXT", name);
			// holder.icon.setImageBitmap(mIcon2);
			holder.icon.setImageBitmap(icons.elementAt(position));
			// holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 :
			// mIcon2);

			return convertView;
		}

		public static class ViewHolder {
			TextView text;
			ImageView icon;
		}

	}

	private void doSearchQuery(final Intent queryIntent, final String entryPoint) {
		menus.doSearchQuery(queryIntent, entryPoint, this);
	}
}
