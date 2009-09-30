package fm.smart.r1.activity;

public class AddSentenceResult implements Result{
	int status_code = 0;
	String http_response = "";

	public AddSentenceResult(int status_code, String http_response) {
		this.status_code = status_code;
		this.http_response = http_response;
	}

	public String getTitle() {
		return "Add Sentence Result";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Added Sentence To List";
		} else {
			message = "Failed: " + this.status_code + ", " + this.http_response;
		}
		return message;
	}

	public boolean success() {
		return (this.status_code == 201);
	}

}
