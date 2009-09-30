package fm.smart.r1;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class Node {
	public Node parent;
	public String name;
	public HashMap atts = new LinkedHashMap();
	private Vector<Node> children = new Vector<Node>();
	public boolean closed = false;
	public String contents;
	public HashMap<String, Vector<Node>> index = new LinkedHashMap<String, Vector<Node>>();

	public void add_child(Node node) {
		this.children.add(node);
		node.parent = this;
		Vector vector = this.index.get(node.name);
		if (vector == null)
			vector = new Vector();
		vector.add(node);
		this.index.put(node.name, vector);
	}

	public Vector<Node> children() {
		return this.children;
	}

	public Vector<Node> get(String name) {
		return index.get(name);
	}

	public Node getNthElement(String name, int n) {
		Node element = null;
		Vector<Node> vec = this.get(name);
		if (vec != null) {
			element = vec.elementAt(n);
		}
		return element;
	}
	
	public Node getFirst(String name) {
		Node first_element = null;
		Vector<Node> vec = this.get(name);
		if (vec != null) {
			first_element = vec.firstElement();
		}
		return first_element;
	}

	public String getFirstContents(String name) {
		String contents = "";
		Node first_element = getFirst(name);
		if (first_element != null) {
			contents = first_element.contents;
		}
		return contents;
	}

	public String toString() {
		return name + ": " + atts.toString();
	}
}