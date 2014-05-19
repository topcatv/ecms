package org.pshow.mvc;

import java.util.HashMap;

public class Result extends HashMap<String, Object> {

	private static final long serialVersionUID = -4901570011106238816L;
	
	public Result(boolean success) {
		this.put("success", success);
	}
	
}
