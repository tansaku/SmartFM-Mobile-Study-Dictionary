package fm.smart.r1.activity;

import android.text.TextUtils;

public class CreateSoundResult {
	int status_code = 0;
	String http_response = "";
	String location = "";

	public CreateSoundResult(int status_code, String http_response, String location) {
		this.status_code = status_code;
		this.http_response = http_response;
		this.location = location;
	}

	public String getTitle() {
		return "Create Sound Result";
	}
	
	public boolean success(){
		return (this.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if(this.success()){
		  message = "Successfully Created Sound";//+ location;
		}
		else{
	      message = "Failed: "+ prettifyResponse();
		}	
		return message;
	}
	
	private String prettifyResponse() {
		String message = this.status_code + ", " + this.http_response;
		if (TextUtils
				.equals(this.http_response, "No access to modify Item.")) {
			message = "Apologies, at the moment sound can only be added to items you have created yourself.";
		}
		if (TextUtils
				.equals(this.http_response, "No access to modify Sentence.")) {
			message = "Apologies, at the moment sound can only be added to sentences you have created yourself.";
		}
		return message;
	}

}
