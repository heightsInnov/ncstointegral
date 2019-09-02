package com.ubn.devops.ubnncsintegration.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ApiResponse {
	
	public static final int SERVER_ERROR=500;
	public static final int NOT_FOUND=404;
	public static final int SUCCESSFUL=200;

	private int code;
	private String message;
	private Object body;

	public ApiResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
