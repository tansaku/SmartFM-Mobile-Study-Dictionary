package fm.smart.r1.activity;

public class SignUpResult {
	int status_code = 0;
	String http_response = "";

	public SignUpResult(int status_code, String http_response) {
		this.status_code = status_code;
		this.http_response = http_response;
	}

	public String getTitle() {
		return "SignUp";
	}

	public String getMessage() {
		String message = "";
		if(success()){
		  message = "Successfully Signed Up";// + http_response; 
		}
		else{
	      message = "Failed: "+ http_response;
		}	
		return message;
	}

	public boolean success() {
		return this.status_code == 201;
	}

}
