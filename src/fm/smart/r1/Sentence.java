package fm.smart.r1;

import java.util.Vector;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

public class Sentence {
	public String id = null;
	public String text = null;
	public String image_url = null;
	public Bitmap image = null;
	public String sound_url = null;
	public String translation = null;
	public String translation_sound_url = null;

	public Sentence(String id, String text, Vector<Node> image_vector,
			Vector<Node> sound_vector, String translation,
			Vector<Node> translation_sound_vector) {
		this.id = id;
		this.text = text;
		if (image_vector != null) {
			try {
				this.image_url = image_vector.firstElement().contents;
			} catch (Exception e) {
				e.printStackTrace();
				loadSentenceImageDirectly();
			}
			Log.d("DEBUG-URI", "image_url: " + this.image_url);

		}
		if (TextUtils.isEmpty(this.image_url)) {
			loadSentenceImageDirectly();
		}
		if (image_url != null) {
			image = Main.getRemoteImage(this.image_url, null);
		}
		Log.d("DEBUG-URI", "image-url: " + this.image_url);

		if (sound_vector != null) {
			try {
				this.sound_url = sound_vector.firstElement().contents;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.translation = translation;
		Log.d("DEBUG-SENTENCE", this.translation);
		if (TextUtils.isEmpty(this.translation)
				|| TextUtils.equals(this.translation,
						"no translation available")) {
			loadSentenceTranslationDirectly();
			Log.d("DEBUG-SENTENCE", this.translation);
		}
		if (translation_sound_vector != null) {
			try {
				this.translation_sound_url = translation_sound_vector
						.firstElement().contents;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loadSentenceTranslationDirectly() {
		try {
			SmartFmLookup lookup = new SmartFmLookup();
			Vector<Node> sentence = lookup.sentence(this.id);

			this.translation = sentence.firstElement().get("translations")
					.firstElement().get("sentence").firstElement().get("text")
					.firstElement().contents;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadSentenceImageDirectly() {
		try {
			SmartFmLookup lookup = new SmartFmLookup();
			Vector<Node> sentence = lookup.sentence(this.id);

			this.image_url = sentence.firstElement().get("square_image")
					.firstElement().contents;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
