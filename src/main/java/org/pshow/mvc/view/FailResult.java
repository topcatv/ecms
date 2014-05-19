package org.pshow.mvc.view;

import org.pshow.mvc.Result;

public class FailResult extends Result {

	private static final long serialVersionUID = -2036024627743933598L;

	public FailResult(boolean success) {
		super(false);
	}

}
