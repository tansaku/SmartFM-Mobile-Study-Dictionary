package fm.smart.r1.activity;

/*
 * Copyright (C) 2007 The Android Open Source Project
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

import java.util.Vector;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.nullwire.trace.ExceptionHandler;

import fm.smart.r1.Node;
import fm.smart.r1.Utils;
import fm.smart.r1.R;

/**
 * Demonstrates expandable lists using a custom {@link ExpandableListAdapter}
 * from {@link BaseExpandableListAdapter}.
 */
public class ShowItem extends ExpandableListActivity {

	private static final int CREATE_EXAMPLE_ID = Menu.FIRST;
	private static final int CREATE_SOUND_ID = Menu.FIRST + 1;

	ExpandableListAdapter mAdapter;
	private String item_position_id = null;
	private String list_id = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		ExceptionHandler.register(this);

		final Intent queryIntent = getIntent();
		Bundle extras = queryIntent.getExtras();
		item_position_id = (String) extras.get("item_position_id");
		list_id = (String) extras.get("list_id");

		// Set up our adapter
		mAdapter = new MyExpandableListAdapter();
		setListAdapter(mAdapter);
		registerForContextMenu(getExpandableListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, CREATE_EXAMPLE_ID, 0, R.string.menu_create_example)
				.setIcon(android.R.drawable.ic_menu_add);

		menu.add(0, CREATE_SOUND_ID, 0, R.string.menu_create_sound).setIcon(
				android.R.drawable.ic_menu_add);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CREATE_EXAMPLE_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(this, CreateExampleActivity.class.getName());
			Node item_node = ItemListActivity.items.elementAt(Integer
					.parseInt(item_position_id));
			Utils.putExtra(intent, "item_id", (String) item_node.atts.get("id"));
			Utils.putExtra(intent, "list_id", list_id);
			startActivity(intent);
			break;
		}
		case CREATE_SOUND_ID: {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(this, CreateSoundActivity.class.getName());
			Node item_node = ItemListActivity.items.elementAt(Integer
					.parseInt(item_position_id));
			Utils.putExtra(intent, "item_id", (String) item_node.atts.get("id"));
			Utils.putExtra(intent, "list_id", list_id);
			startActivity(intent);
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Sample menu");
		menu.add(0, 0, 0, R.string.expandable_list_sample_action);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();

		String title = ((TextView) info.targetView).getText().toString();

		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			int childPos = ExpandableListView
					.getPackedPositionChild(info.packedPosition);
			Toast.makeText(
					this,
					title + ": Child " + childPos + " clicked in group "
							+ groupPos, Toast.LENGTH_SHORT).show();
			return true;
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			int groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			Toast.makeText(this, title + ": Group " + groupPos + " clicked",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		return false;
	}

	/**
	 * TODOs 1) handle html bold characters (fromHtml stripping but not bolding
	 * ...) 2) add sound icon 3) find space for images?
	 * 
	 * 
	 */
	public class MyExpandableListAdapter extends BaseExpandableListAdapter {
		// Sample data set. children[i] contains the children (String[]) for
		// groups[i].
		Node item;
		Node cue;
		Vector<Node> sentences = null;
		Vector<Node> sentences_item;
		int number_groups = 1;
		private String[] groups;
		private String[][] children;

		public MyExpandableListAdapter() {
			item = ItemListActivity.items.elementAt(Integer.parseInt(item_position_id));
			cue = item.get("cue").firstElement();
			sentences_item = item.get("sentences");
			if (sentences_item != null) {
				Log.d("DEBUG", "sentences_item: "+sentences_item.toString());
				Node sentence_list = sentences_item.firstElement();
				Log.d("DEBUG", "sentence_list: "+sentence_list.toString());
				sentences = sentence_list.get("sentence");
			}
			
			String cue_text = cue.get("text").firstElement().contents;
			if (sentences != null) {
				number_groups += sentences.size();
			}
			groups = new String[number_groups];
			children = new String[number_groups][];

			Log.d("DEBUG", "number_groups:" + number_groups);
			groups[0] = cue_text;
			if (sentences != null) {
				Log.d("DEBUG", "sentences: "+sentences.toString());
				for (int i = 0; i < sentences.size(); i++) {
					Vector<Node> v = sentences.elementAt(i).get("text");
					Log.d("DEBUG", "v: "+v.toString());
					Node n = v.firstElement();
					Log.d("DEBUG", "n: "+n.toString());
					groups[i + 1] = n.contents;
					Log.d("DEBUG", "groups[i + 1]: "+groups[i + 1]);
				}
			}
			children[0] = new String[1];
			children[0][0] = item.get("responses").firstElement().get(
					"response").firstElement().get("text").firstElement().contents;
			if (sentences != null) {
				for (int i = 0; i < sentences.size(); i++) {
					children[i + 1] = new String[1];
					Vector<Node> translations = sentences.elementAt(i).get(
							"translations");
					String translation = "no translation available";
					try {
						translation = translations.firstElement().get(
								"sentence").firstElement().get("text")
								.firstElement().contents;
					} catch (Exception e) {
					}
					children[i + 1][0] = translation;
					Log.d("DEBUG", children[i + 1][0]);
				}
			}
		}

		// private String[][] children = {
		// { "Arnold", "Barry", "Chuck", "David" },
		// { "Ace", "Bandit", "Cha-Cha", "Deuce" },
		// { "Fluffy", "Snuggles" },
		// { "Goldy", "Bubbles" }
		// };

		public Object getChild(int groupPosition, int childPosition) {
			return children[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return children[groupPosition].length;
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 64);

			TextView textView = new TextView(ShowItem.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(36, 0, 0, 0);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(Html.fromHtml(getChild(groupPosition,
					childPosition).toString()));
			return textView;
		}

		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		public int getGroupCount() {
			return groups.length;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText(Html.fromHtml(getGroup(groupPosition).toString()));
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}

	}
}
