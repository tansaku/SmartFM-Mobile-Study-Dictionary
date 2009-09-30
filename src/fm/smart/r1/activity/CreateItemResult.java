package fm.smart.r1.activity;

import android.text.TextUtils;

public class CreateItemResult {
	int status_code = 0;
	String http_response = "";
	String list_id = null;

	public CreateItemResult(int status_code, String http_response,
			String list_id) {
		this.status_code = status_code;
		this.http_response = http_response;
		this.list_id = list_id;
	}

	public String getTitle() {
		return "Create Item Result";
	}

	public boolean success() {
		return (this.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if (this.success()) {
			message = "Successfully Created Item";// + http_response;
		} else {
			message = "Failed: " + prettifyResponse();
		}
//		if (!TextUtils.isEmpty(list_id)) {
//			message += ", for list: " + list_id;
//		}
		return message;
	}
	

	public boolean languagePairMismatch() {
		return TextUtils.equals(this.http_response, "incompatible-languages-on-item-and-list");
	}
	public boolean noAccess() {
		return TextUtils.equals(this.http_response, "No access to modify Course.");
	}

	public boolean alreadyInList() {
		return TextUtils.equals(this.http_response, "item-already-in-list");
	}

	private String prettifyResponse() {
		if (languagePairMismatch())
			return "Apologies, the current system only supports one language pair for saving study items.";
		if (noAccess())
			return "Apologies, it seems we do not have access rights to modify your current study list.";
		if (alreadyInList())
			return "This item already exists and is in your study list.";
		return this.http_response;
	}

}
