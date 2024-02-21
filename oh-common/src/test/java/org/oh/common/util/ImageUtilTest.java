package org.oh.common.util;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

//@Disabled
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ImageUtilTest {
	private static final String PATH = "img/";
	private static final String FILE_NAME = "photo";
	//	private static final String FILE_NAME = "test";
//	private static final String FILE_NAME = "test2";
//	private static final String FILE_NAME = "test3";
	private static final String FILE_JPG = FILE_NAME + ".jpg";
	private static final String FILE_PNG = FILE_NAME + ".png";
	private static final String FILE_BMP = FILE_NAME + ".bmp";
	private static final String FILE_GIF = FILE_NAME + ".gif";
	private static final int WIDTH_1 = ImageUtil.THUMB_WIDTH;
	private static final int HEIGHT_1 = ImageUtil.RATIO;
	private static final int WIDTH_2 = ImageUtil.RATIO;
	private static final int HEIGHT_2 = ImageUtil.THUMB_WIDTH;

	@Test
	void t001jpgImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_JPG, PATH + FILE_JPG + "_imageIO.jpg", WIDTH_1, HEIGHT_1);
	}

	@Test
	void t002jpgScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_JPG, PATH + FILE_JPG + "_scalr.jpg", WIDTH_1, HEIGHT_1);
	}

	//	@Test
	void t011pngImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_PNG, PATH + FILE_PNG + "_imageIO.jpg", WIDTH_1, HEIGHT_1);
	}

	//	@Test
	void t012pngScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_PNG, PATH + FILE_PNG + "_scalr.jpg", WIDTH_1, HEIGHT_1);
	}

	//@Test
	void t021bmpImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_BMP, PATH + FILE_BMP + "_imageIO.jpg", WIDTH_1, HEIGHT_1);
	}

	//@Test
	void t022bmpScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_BMP, PATH + FILE_BMP + "_scalr.jpg", WIDTH_1, HEIGHT_1);
	}

	//@Test
	void t031gifImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_GIF, PATH + FILE_GIF + "_imageIO.jpg", WIDTH_1, HEIGHT_1);
	}

	//@Test
	void t032gifScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_GIF, PATH + FILE_GIF + "_scalr.jpg", WIDTH_1, HEIGHT_1);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t101jpgImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_JPG, PATH + FILE_JPG + "_2imageIO.jpg", WIDTH_2, HEIGHT_2);
	}

	@Test
	void t102jpgScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_JPG, PATH + FILE_JPG + "_2scalr.jpg", WIDTH_2, HEIGHT_2);
	}

	//	@Test
	void t111pngImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_PNG, PATH + FILE_PNG + "_2imageIO.jpg", WIDTH_2, HEIGHT_2);
	}

	//	@Test
	void t112pngScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_PNG, PATH + FILE_PNG + "_2scalr.jpg", WIDTH_2, HEIGHT_2);
	}

	//@Test
	void t121bmpImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_BMP, PATH + FILE_BMP + "_2imageIO.jpg", WIDTH_2, HEIGHT_2);
	}

	//@Test
	void t122bmpScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_BMP, PATH + FILE_BMP + "_2scalr.jpg", WIDTH_2, HEIGHT_2);
	}

	//@Test
	void t131gifImageIO() {
		ImageUtil.createImageIOThumb(PATH + FILE_GIF, PATH + FILE_GIF + "_2imageIO.jpg", WIDTH_2, HEIGHT_2);
	}

	//@Test
	void t132gifScalr() {
		ImageUtil.createScalrThumb(PATH + FILE_GIF, PATH + FILE_GIF + "_2scalr.jpg", WIDTH_2, HEIGHT_2);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t999delete() {
		FileUtil.deleteQuietly(new File(PATH), FILE_JPG + "_*");
	}
}
