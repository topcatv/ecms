package org.pshow.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.web.WebException;
import org.nutz.web.Webs;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;

public class ExceptionView implements View {
	private String value;
	private boolean uc;

	public ExceptionView(String value) {
		uc = false;
		this.value = value;
	}

	public ExceptionView(String value, String useCompact) {
		uc = Strings.isBlank(useCompact) ? false : Boolean
				.parseBoolean(useCompact);
		this.value = value;
	}

	public JsonFormat getJsonFormat() {
		if (uc) {
			// 紧凑模式(生产)
			return JsonFormat.compact();
		} else {
			// 一般模式(开发)
			return JsonFormat.nice();
		}
	}

	@Override
	public void render(HttpServletRequest req, HttpServletResponse resp,
			Object obj) throws Throwable {
		resp.setStatus(Integer.parseInt(this.value));
		WebException err = Webs.Err.wrap((Throwable) obj);
		String msg = Mvcs.getMessage(req, err.getKey());
		AjaxReturn re = Ajax.fail().setErrCode(err.getKey()).setMsg(msg)
				.setData(err.getReason());

		// 写入返回
		Mvcs.write(resp, re, getJsonFormat());
	}
}