package org.pshow.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SimpleCharsetDetector {

	public static String detectCharset(InputStream in) throws IOException {
		BufferedInputStream imp = new BufferedInputStream(in);
		byte[] buf = new byte[3];
		imp.read(buf);
		imp.close();
		in.close();
		if (buf[0] == -17 && buf[1] == -69 && buf[2] == -65) {
			return "UTF-8";
		} else {
			return "GBK";
		}
	}
}