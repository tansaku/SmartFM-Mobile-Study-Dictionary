package fm.smart.r1.activity;

import android.text.TextUtils;

public class AddItemResult implements Result {
	int status_code = 0;
	String http_response = "";

	public AddItemResult(int status_code, String http_response) {
		this.status_code = status_code;
		this.http_response = http_response;
	}

	public String getTitle() {
		return "Add Item Result";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Added Item To List"; // success produces
															// empty body at
															// present, leaving
															// for debug
															// purposes
		} else {
			message = "Failed: " + prettifyResponse();
		}
		return message;
	}

	public boolean success() {
		return (this.status_code == 200);
	}

	public boolean alreadyInList() {
		return TextUtils.equals(this.http_response, "item-already-in-list");
	}

	private String prettifyResponse() {
		if (alreadyInList())
			return "Item already in list";
		return this.http_response;
	}

}
