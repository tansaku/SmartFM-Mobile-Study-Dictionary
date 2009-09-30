package fm.smart.r1.activity;

public class LoginResult {
	int status_code = 0;
	String http_response = "";

	public LoginResult(int status_code, String http_response) {
		this.status_code = status_code;
		this.http_response = http_response;
	}

	public String getTitle() {
		return "Login";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully logged in";// + http_response;
		} else {
			if (http_response.equals("Unauthorized")) {
				message = "Username or password incorrect ...";
			} else {
				message = "Failed: " + http_response;
			}
		}
		return message;
	}

	public boolean success() {
		return this.status_code == 200;
	}

}
