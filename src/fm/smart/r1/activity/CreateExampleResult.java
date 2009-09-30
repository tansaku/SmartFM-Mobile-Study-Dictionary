package fm.smart.r1.activity;

import android.text.TextUtils;

public class CreateExampleResult {
	int status_code = 0;
	String http_response = "";

	public CreateExampleResult(int status_code, String http_response) {
		this.status_code = status_code;
		this.http_response = http_response;
	}

	public String getTitle() {
		return "Create Example Result";
	}

	public boolean success() {
		return (this.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if (this.success()) {
			message = "Successfully Created Example";
		} else {
			message = "Failed: " + prettifyResponse();
		}
		return message;
	}

	private String prettifyResponse() {
		String message = this.status_code + ", " + this.http_response;
		if (this.http_response == null) {
			message = "Network Timeout, please try again later";
		}
		if (TextUtils.equals(this.http_response,
				"Translation translation-already-exists")) {
			message = "Apologies, this sentence already exists, possibly in association with another item.";
			//TODO kick off another request to add this sentence to this item ...?
		}
		return message;
	}

}
