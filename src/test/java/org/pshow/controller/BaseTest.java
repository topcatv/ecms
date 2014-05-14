package org.pshow.controller;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.mvc.Mvcs;

public class BaseTest {
	
	public BaseTest() {
		Ioc ioc = null;
		try {
			ioc = new NutIoc(new ComboIocLoader("*org.nutz.ioc.loader.json.JsonLoader", "ioc", "*org.nutz.ioc.loader.annotation.AnnotationIocLoader", "org.pshow"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		};
		Mvcs.setIoc(ioc);
	}

}
