package fm.smart.r1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	public Node start_node = null;
	private Node current_node = null;
	public HashMap<String, Vector<Node>> index = new HashMap<String, Vector<Node>>();

	public XMLHandler() {
		super();
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * called on opening tags like: <tag> Can provide attribute(s), when xml was
	 * like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		Node node = new Node();
		node.name = localName;
		for (int i = 0; i < atts.getLength(); i++) {
			node.atts.put(atts.getLocalName(i), atts.getValue(i));
		}
		Vector vector = index.get(node.name);
		if (vector == null) {
			vector = new Vector();
			index.put(node.name, vector); // this step only required if it was
			// null no?
		}
		vector.add(node);

		if (this.start_node == null) {
			this.start_node = node;
		} else {
			if (this.current_node.closed) {
				this.current_node.parent.add_child(node);
			} else {
				this.current_node.add_child(node);
			}
		}
		this.current_node = node;
	}

	/**
	 * called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		Vector<Node> nodes = this.index.get(localName);
		Vector<Node> reverse_nodes = (Vector<Node>) nodes.clone();
		Collections.reverse(reverse_nodes);
		for (Node n : reverse_nodes) {
			if (n.closed == true) {
				continue;
			} else {
				this.current_node = n;  // not sure this is right ...
				this.current_node.closed = true;
				break;
			}
		}

	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.current_node.contents != null && length == 1)
			return;
		this.current_node.contents = new String(ch, start, length);
	}

}