package fm.smart.r1;

import java.util.Vector;

import android.graphics.Bitmap;

public class Item{

	public Node item_node;
	public Node cue_node;
	public Node response_node;
	public Vector<Node> sentences_item;
	public Vector<Node> sentences = null;
	public int number_groups = 1;
	public String[] groups;
	public String[][] children;
	public String cue_text;
	public String character;
	public CharSequence part_of_speech;
	public CharSequence author_name;
	public CharSequence type;
	public String author_icon_url;
	public Bitmap author_image;
	public String cue_sound_url;
	public String response_sound_url;
	public Vector<Sentence> sentence_vector = new Vector<Sentence>();

}
