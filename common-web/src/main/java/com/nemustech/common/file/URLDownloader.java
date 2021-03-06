package com.nemustech.common.file;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.nemustech.common.helper.IOHelper;
import com.nemustech.common.util.HTTPUtil;

/**
 * URL 기반으로 되어 있는 파일을 download할 수 있는 downloader
 * 
 * 
 * @version 1.0.0
 * 
 */
public class URLDownloader implements IFileDownloader {
	private static final Log log = LogFactory.getLog(URLDownloader.class);

	/**
	 * 읽기 버퍼의 크기
	 */
	public static final int READ_BLOCK = 8192;

	public Files download(String fileName, String URL) throws Exception {
		log.info("Start::download()");
		log.trace("  > fileName: " + fileName);
		log.trace("  > URL: " + URL);

		Files files = null;
		try {
			String[] httpTokens = URL.split("://", 2);
			String[] hostTokens = httpTokens[1].split("/", 2);
			String[] portTokens = hostTokens[0].split(":", 2);
			String host = hostTokens[0];
			int port = 80;

			if (portTokens.length == 2) {
				host = portTokens[0];
				port = Integer.parseInt(portTokens[1]);
			}

			URL url = new URL(httpTokens[0], host, port,
					"/" + URLEncoder.encode(hostTokens[1], "UTF-8").replaceAll("\\+", "%20").replaceAll("%2F", "/"));
			byte[] bytes = IOHelper.readToEnd(url.openStream());

			log.trace("Decoded URL: " + url.toString());
			log.trace("File Name: " + fileName);
			log.trace("File Size: " + bytes.length);

			files = new Files(fileName, bytes);
			log.debug("  > RV(files): " + files);
		} catch (MalformedURLException e) {
			log.error("MalformedURLException > ", e);
			log.trace("Throw MalformedURLException!");

			throw e;
		} catch (IOException e) {
			log.error("IOException > ", e);
			log.trace("Throw IOException!");

			throw e;
		} finally {
			log.info("End::download()");
		}

		return files;
	}

	// 확장 ///

	/**
	 * SSL 지원
	 * 
	 * @param fileName
	 * @param URL
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public Files download(String fileName, String URL, String charset) throws Exception {
		log.info("Start::download()");
		log.trace("  > fileName: " + fileName);
		log.trace("  > URL: " + URL);

		byte[] bytes = (byte[]) HTTPUtil.callHttp(URL, null, null, null, charset).get("content");
		log.trace("Decoded URL: " + URL);
		log.trace("File Name: " + fileName);
		log.trace("File Size: " + bytes.length);

		Files files = new Files(fileName, bytes);
		log.debug("  > RV(files): " + files);

		return files;
	}
}
