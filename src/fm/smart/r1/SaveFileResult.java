package fm.smart.r1;

import java.io.FileDescriptor;

public class SaveFileResult {
	int status_code = 0;
	String http_response = "";
	FileDescriptor fd = null;

	public SaveFileResult(int status_code, String http_response,
			FileDescriptor fd) {
		this.status_code = status_code;
		this.http_response = http_response;
		this.fd = fd;
	}

	public String getTitle() {
		return "Download";
	}

	public String getMessage() {
		String message = "";
		if (success()) {
			message = "Successfully Downloaded Sound";// + http_response;
		} else if (this.status_code == 404) {
			message = "Failed: file not found";
		} else {
			message = "Failed:" + this.status_code;
		}
		return message;
	}

	public boolean success() {
		return this.status_code == 200;
	}

	public FileDescriptor getFileDescriptor() {
		return fd;
	}

}
