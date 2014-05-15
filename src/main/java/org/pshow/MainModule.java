package org.pshow;

import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.pshow.mvc.view.ExceptionViewMaker;

@Modules(scanPackage = true)
@IocBy(type = ComboIocProvider.class, args = {
		"*org.nutz.ioc.loader.json.JsonLoader", "ioc",
		"*org.nutz.ioc.loader.annotation.AnnotationIocLoader", "org.pshow" })
@SetupBy(MvcSetup.class)
@Localization("msg")
@Encoding(input = "UTF-8", output = "UTF-8")
@Views({ ExceptionViewMaker.class })
public class MainModule {

}