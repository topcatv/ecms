package org.pshow.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class ExceptionViewMaker implements ViewMaker {

	public View make(Ioc ioc, String type, String value) {
		if ("exception".equalsIgnoreCase(type)) {
			return new ExceptionView(value);
		}
		return null;
	}

}
