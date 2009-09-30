package fm.smart.r1.activity;

public class CreateListResult {
	int status_code = 0;
	private String http_response = "";

	public CreateListResult(int status_code, String http_response) {
		this.status_code = status_code;
		this.http_response = http_response;
	}

	public String getTitle() {
		return "Create List Result";
	}
	
	public boolean success(){
		return (this.status_code == 201);
	}

	public String getMessage() {
		String message = "";
		if(this.success()){
		  message = "Successfully Created List";
		}
		else{
	      message = "Failed: "+ getHttpResponse();
		}	
		return message;
	}

	public String getHttpResponse() {
		return http_response;
	}

}
