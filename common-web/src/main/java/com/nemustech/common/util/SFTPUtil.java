package com.nemustech.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {
	public static final int DEFAULT_PORT = 22;

	protected JSch jsch = new JSch();

	protected Session session = null;
	protected ChannelSftp channelSftp = null;

	protected String server = null;
	protected int port = DEFAULT_PORT;
	protected String userName = null;
	protected String password = null;

	public SFTPUtil(String server, String userName, String password) throws JSchException {
		this(server, -1, userName, password);
	}

	public SFTPUtil(String server, int port, String userName, String password) throws JSchException {
		this.server = server;
		if (port == -1)
			port = this.port;
		else
			this.port = port;
		this.userName = userName;
		this.password = password;

		LogUtil.writeLog("[Connect server] " + server + ":" + port, getClass());
		session = jsch.getSession(userName, server, port);
		session.setPassword(password);

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();

		channelSftp = (ChannelSftp) session.openChannel("sftp");

		LogUtil.writeLog("[Login] " + userName, getClass());
		channelSftp.connect();
	}

	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}

	public void setChannelSftp(ChannelSftp channelSftp) {
		this.channelSftp = channelSftp;
	}

	public void list(String targetPath) throws SftpException {
		LogUtil.writeLog("[Print file] " + targetPath, getClass());

		Vector<ChannelSftp.LsEntry> files = ls(targetPath);
		for (ChannelSftp.LsEntry file : files) {
			LogUtil.writeLog(file, getClass());
		}
	}

	public void backup(String targetPath) throws SftpException {
		String targetDir = FilenameUtils.getFullPathNoEndSeparator(targetPath);
		Vector<ChannelSftp.LsEntry> files = ls(targetPath);
		for (ChannelSftp.LsEntry file : files) {
			if (file.getAttrs().isDir())
				continue;

			_backup(targetDir + File.separator + file.getFilename());
		}
	}

	public void remove(String targetPath) throws SftpException {
		LogUtil.writeLog("[Remove file] " + targetPath, getClass());

		String targetDir = FilenameUtils.getFullPath(targetPath);
		Vector<ChannelSftp.LsEntry> files = ls(targetPath);
		for (ChannelSftp.LsEntry file : files) {
			if (file.getAttrs().isDir())
				try {
					channelSftp.rmdir(targetDir + file.getFilename());
				} catch (SftpException e) {
					LogUtil.writeLog(file.getLongname(), e, getClass());
				}
			else {
				channelSftp.rm(targetDir + file.getFilename());
			}
		}
	}

	public void upload(String sourcePath, String targetDir) throws FileNotFoundException, IOException, SftpException {
		upload(sourcePath, targetDir, false);
	}

	public void upload(String sourcePath, String targetDir, boolean mkdir)
			throws FileNotFoundException, IOException, SftpException {
		if (mkdir) {
			channelSftp.mkdir(targetDir);
		}

		Collection<File> fileList = FileUtils.listFiles(new File(FilenameUtils.getFullPathNoEndSeparator(sourcePath)),
				new WildcardFileFilter(FilenameUtils.getName(sourcePath)), null);
		for (File file : fileList) {
			_upload(file.getPath(), targetDir);
		}
	}

	public void download(String sourceDir, String targetPath) throws IOException, SftpException {
		String targetDir = FilenameUtils.getFullPathNoEndSeparator(targetPath);
		Vector<ChannelSftp.LsEntry> files = ls(targetPath);
		for (ChannelSftp.LsEntry file : files) {
			if (file.getAttrs().isDir())
				continue;

			_download(sourceDir, targetDir + File.separator + file.getFilename());
		}
	}

	public void disconnect() {
		LogUtil.writeLog("[Disconnect server] " + server + ":" + port, getClass());
		channelSftp.disconnect();
		session.disconnect();
	}

	protected void _backup(String targetFile) {
		LogUtil.writeLog("[Backup file] " + targetFile, getClass());

		rename(targetFile, targetFile + "_" + StringUtil.getDateTimeUnderline());
	}

	protected void _upload(String sourcePath, String targetDir)
			throws FileNotFoundException, IOException, SftpException {
		String targetPath = targetDir + File.separator + FilenameUtils.getName(sourcePath);
		LogUtil.writeLog("[Upload file] " + targetPath, getClass());

		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(sourcePath));
			channelSftp.put(in, targetPath);
		} finally {
			if (in != null)
				in.close();
		}

		LogUtil.writeLog("[Upload complete]", getClass());
	}

	protected void _download(String sourceDir, String targetPath) throws IOException, SftpException {
		String sourcePath = sourceDir + File.separator + FilenameUtils.getName(targetPath);
		LogUtil.writeLog("[Download file] " + sourcePath, getClass());

		InputStream in = channelSftp.get(targetPath);
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(sourcePath));
			int i;
			while ((i = in.read()) != -1) {
				out.write(i);
			}
		} finally {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		}

		LogUtil.writeLog(" [Download complete]", getClass());
	}

	/**
	 * No such file 에러는 pass
	 * 
	 * @param filePath
	 * @return
	 */
	protected Vector<ChannelSftp.LsEntry> ls(String filePath) {
		try {
			return channelSftp.ls(filePath);
		} catch (SftpException e) {
			LogUtil.writeLog(filePath + " : " + e.getMessage(), getClass());
			return new Vector();
		}
	}

	/**
	 * No such file 에러는 pass
	 * 
	 * @param sourceFile
	 * @param targetFile
	 */
	protected void rename(String sourceFile, String targetFile) {
		try {
			channelSftp.rename(sourceFile, targetFile);
		} catch (SftpException e) {
			LogUtil.writeLog(sourceFile + ", " + targetFile + " : " + e.getMessage(), getClass());
		}
	}

	public static void main(String[] args) throws Exception {
		SFTPUtil ftp = null;
		try {
			String server = "192.168.1.38";
			String userName = "hiserver";
			String password = "spantmxpr";

			String system_name = "ams";
			String source_dir = "target";
			String lib_path = "lib/common-web-*.jar";
			String target_dir = "/var/was";
			String target_path = "apps";
			String target_full_dir = target_dir + File.separator + system_name + File.separator + target_path;
			String source_file = "common-web-1.0.jar";

			ftp = new SFTPUtil(server, 22, userName, password);

			ftp._backup(target_full_dir + File.separator + source_file);
			ftp._upload(source_dir + File.separator + source_file, target_full_dir);
//			ftp._download(source_dir, target_full_dir + File.separator + fileName);

//			ftp.list(target_full_dir + File.separator + lib_path);
//			ftp.backup(target_full_dir + File.separator + lib_path);
//			ftp.remove(target_full_dir + File.separator + "*.jar");
//			String lib_dir = FilenameUtils.getFullPathNoEndSeparator(lib_path);
//			ftp.upload(source_dir + File.separator + lib_path, target_full_dir + File.separator + lib_dir);
//			ftp.download(source_dir, target_full_dir + File.separator + "*.jar");
		} finally {
			if (ftp != null)
				ftp.disconnect();
		}
	}
}
