/**
 * 
 */
package org.pshow.common;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * @author topcat
 *
 */
public class CopyProcesser implements Runnable {

	private PDFConverter convert;
	private File input;
	private String cid;
	private CopyCallback callback;

	public CopyProcesser(CopyCallback callback, File input, String cid) {
		this.callback = callback;
		this.input = input;
		this.cid = cid;
		this.convert = new OpenOfficePDFConverter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		File tempFile = FileUtils.getTempDirectory();
		String output = tempFile.getAbsolutePath() + File.separator
				+ input.getName() + ".pdf";
		convert.convert2PDF(input, output);
		callback.execute(cid, output);
	}

	public void setConvert(PDFConverter convert) {
		this.convert = convert;
	}
}
