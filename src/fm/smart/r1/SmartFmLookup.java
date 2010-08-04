package fm.smart.r1;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class SmartFmLookup extends XMLHandler {

	private static final String SMARTFM_API_HTTP_ROOT = "http://api.smart.fm";

	public Vector<Node> searchLists(String keyword) {
		keyword = keyword.replaceAll(" ", "%20");
		parseURL("/lists/matching/" + keyword + ".xml");
		return this.index.get("list");
	}

	public HashMap<String, Vector<Node>> searchItems(String keyword, int page) {
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
			Log.d("DEBUG_SEARCH_ITEMS", keyword);
			keyword = keyword.replaceAll("\\++", "%20");
			Log.d("DEBUG_SEARCH_ITEMS", keyword);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parseURL("/items/matching/" + keyword + ".xml?page=" + page
				+ "&include_sentences=true&language=" + Main.search_lang
				+ "&translation_language=" + Main.result_lang);
		return this.index;
	}

	public HashMap<String, Vector<Node>> searchItemsV2(String keyword, int page) {
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
			Log.d("DEBUG_SEARCH_ITEMS", keyword);
			keyword = keyword.replaceAll("\\++", "%20");
			Log.d("DEBUG_SEARCH_ITEMS", keyword);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parseURL("/items/matching/" + keyword + ".xml?v=2&page=" + page
				+ "&include_sentences=true&language=" + Main.search_lang
				+ "&translation_language=" + Main.result_lang);
		return this.index;
	}

	public Vector<Node> item(String id) {
		parseURL("/items/" + id + ".xml?include_sentences=true");
		return this.index.get("item");
	}

	public Vector<Node> sentence(String id) {
		parseURL("/sentences/" + id + ".xml");
		return this.index.get("sentence");
	}

	public Vector<Node> userLists(String user) {
		Log.d("DEBUG-LIST", "/users/" + user + "/lists/creator.xml");
		parseURL("/users/" + user + "/lists/creator.xml");
		Log.d("DEBUG-LIST", "user-lists: " + this.index.get("list"));
		return this.index.get("list");
	}

	public Vector<Node> userStudyLists(String user) {
		parseURL("/users/" + user + "/lists.xml");
		return this.index.get("list");
	}

	public Vector<Node> recentLists() {
		parseURL("/lists.xml");
		return this.index.get("list");
	}

	public Vector<Node> listItems(String list_id) {
		parseURL("/lists/" + list_id + "/items.xml?include_sentences=true");
		return this.index.get("item");
	}

	private void parseURL(String api_call) throws FactoryConfigurationError {
		try {
			/* Create a URL we want to load some xml-data from. */

			URL url = new URL(SMARTFM_API_HTTP_ROOT + api_call);
			Log.d("DEBUG", url.toString());

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
			/* Create a new ContentHandler and apply it to the XML-Reader */

			xr.setContentHandler(this);

			/* Parse the xml-data from our URL. */
			InputSource is = new InputSource(url.openStream());
			// is.setEncoding("UTF8");
			xr.parse(is);
			/* Parsing has finished. */

		} catch (Exception e) {
			// Log any Error to the GUI.
			Log.e("DEBUG", "SmartFmLookup Error", e);
		}
	}

}