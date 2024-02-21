/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oh.common.util;

import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * 이미지 유틸리티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ImageUtil {
	public static final String THUMB_EXTENSION = "jpg";
	public static final int RATIO = 0;
	// 여권 사진: 413px x 531px = 3.5cm x 4.5cm
	public static final int THUMB_WIDTH = 400;
	public static final int THUMB_HEIGHT = 500;

	/**
	 * 썸네일 이미지를 생성
	 *
	 * @param srcFile  원본 이미지 파일 경로
	 * @param destFile 대상 이미지 파일 경로
	 * @param width    가로 크기
	 * @param height   세로 크기
	 */
	public static void createImageIOThumb(String srcFile, String destFile, int width, int height) {
		File src = new File(srcFile);
		File dest = new File(destFile);
		createImageIOThumb(src, dest, width, height);
	}

	/**
	 * 썸네일 이미지를 생성
	 *
	 * @param src    원본 이미지 파일
	 * @param dest   대상 이미지 파일
	 * @param width  가로 크기
	 * @param height 세로 크기
	 */
	public static void createImageIOThumb(File src, File dest, int width, int height) {
		Image img = getImage(src);
		Dimension dim = getSize(img, width, height);
		int destWidth = (int) dim.getWidth();
		int destHeight = (int) dim.getHeight();

		Image imgTarget = img.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
		int[] pixels = new int[destWidth * destHeight];
		PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, destWidth, destHeight, pixels, 0, destWidth);
		BufferedImage destImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
		try {
			pg.grabPixels();
			destImg.setRGB(0, 0, destWidth, destHeight, pixels, 0, destWidth);
			ImageIO.write(destImg, "jpg", dest);
		} catch (InterruptedException | IOException e) {
			Thread.currentThread().interrupt();
			throw new CommonException(CommonError.COM_IMAGING_ERROR,
					"srcFile: " + src.getAbsolutePath() + " destFile: " + dest.getAbsolutePath(), e);
		}
	}

	/**
	 * 스칼라 썸네일 이미지를 생성
	 *
	 * @param srcFile  원본 이미지 파일 경로
	 * @param destFile 대상 이미지 파일 경로
	 * @param width    가로 크기
	 * @param height   세로 크기
	 */
	public static void createScalrThumb(String srcFile, String destFile, int width, int height) {
		File src = new File(srcFile);
		File dest = new File(destFile);
		createScalrThumb(src, dest, width, height);
	}

	/**
	 * 스칼라 썸네일 이미지를 생성
	 *
	 * @param src    원본 이미지 파일
	 * @param dest   대상 이미지 파일
	 * @param width  가로 크기
	 * @param height 세로 크기
	 */
	public static void createScalrThumb(File src, File dest, int width, int height) {
		try {
			try (OutputStream os = new FileOutputStream(dest)) {
				String extension = getExtension(src);
				byte[] resizedImg = resize(ImageIO.read(src), extension, width, height);
				if (resizedImg == null) {
					throw new CommonException(CommonError.COM_IMAGING_ERROR,
							String.format("Resized image is null. srcFile: %s destFile: %s",
									src.getAbsolutePath(), dest.getAbsolutePath()));
				}

				TiffImageMetadata exif = getExif(src);
				write(resizedImg, extension, exif, os);
			}
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_IMAGING_ERROR,
					String.format("srcFile: %s destFile: %s", src.getAbsolutePath(), dest.getAbsolutePath()), e);
		}
	}

	private static String getExtension(File img) {
		ImageReader imgReader = null;
		String extension = "";
		try {
			try (ImageInputStream imgInput = ImageIO.createImageInputStream(img)) {
				Iterator<ImageReader> iter = ImageIO.getImageReaders(imgInput);
				if (!iter.hasNext()) {
					throw new CommonException(CommonError.COM_IMAGING_ERROR,
							"No image readers found. file: " + img.getAbsolutePath());
				}

				imgReader = iter.next();
				extension = imgReader.getFormatName();
			} finally {
				if (imgReader != null) {
					imgReader.dispose();
				}
			}
			return extension;
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_IMAGING_ERROR, "file: " + img.getAbsolutePath(), e);
		}
	}

	private static TiffImageMetadata getExif(File src) {
		ImageMetadata meta;
		try {
			meta = Imaging.getMetadata(src);
		} catch (ImageReadException | IOException e) {
			throw new CommonException(CommonError.COM_IMAGING_ERROR, "file: " + src.getAbsolutePath(), e);
		}
		if (!(meta instanceof JpegImageMetadata)) {
			return null;
		}

		JpegImageMetadata jpegMeta = (JpegImageMetadata) meta;
		return jpegMeta.getExif();
	}

	private static byte[] resize(BufferedImage img, String extension, int width, int height) {
		try {
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				int srcWidth = img.getWidth(null);
				int destWidth = Math.min(width, srcWidth);

				int srcHeight = img.getHeight(null);
				int destHeight = Math.min(height, srcHeight);

				BufferedImage resized;
				if (width == RATIO) {
//					if (extension.equals("JPEG")) {
//						resized = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, destHeight);
//					} else {
					resized = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, destHeight);
//					}
				} else if (height == RATIO) {
//					if (extension.equals("JPEG")) {
//						resized = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, destWidth);
//					} else {
					resized = Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, destWidth);
//					}
				} else {
					resized = Scalr.resize(img, Scalr.Mode.FIT_EXACT, destWidth, destHeight);
				}
				if (resized == null) {
					return null;
				}

				ImageIO.write(resized, extension, bos);
				return bos.toByteArray();
			}
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_IMAGING_ERROR, "extension: " + extension, e);
		}
	}

	private static void write(byte[] imgData, String extension, TiffImageMetadata exif, OutputStream dest) {
		try {
			if (exif != null) {
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					new ExifRewriter().updateExifMetadataLossless(imgData, os, exif.getOutputSet());
					imgData = os.toByteArray();
				}
			}

			IOUtils.write(imgData, dest);
		} catch (ImagingException | IOException e) {
			throw new CommonException(CommonError.COM_IMAGING_ERROR, "extension: " + extension, e);
		}
	}

	private static Image getImage(File file) {
		String suffix = file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase();
		Image srcImg;
		if ("bmp".equals(suffix) || "png".equals(suffix) || "gif".equals(suffix)) {
			try {
				srcImg = ImageIO.read(file);
			} catch (IOException e) {
				throw new CommonException(CommonError.COM_IMAGING_ERROR, "srcFile: " + file.getAbsolutePath(), e);
			}
		} else {
			srcImg = new ImageIcon(file.toString()).getImage();
		}
		return srcImg;
	}

	private static Dimension getSize(Image img, int width, int height) {
		int srcWidth = img.getWidth(null);
		int destWidth = Math.min(width, srcWidth);

		int srcHeight = img.getHeight(null);
		int destHeight = Math.min(height, srcHeight);

		if (width == RATIO) {
			double ratio = ((double) destHeight) / ((double) srcHeight);
			destWidth = (int) (srcWidth * ratio);
		} else if (height == RATIO) {
			double ratio = ((double) destWidth) / ((double) srcWidth);
			destHeight = (int) (srcHeight * ratio);
		} else {
			destWidth = srcWidth;
			destHeight = srcHeight;
		}
		return new Dimension(destWidth, destHeight);
	}
}
