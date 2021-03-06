package com.nemustech.common.task;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.nemustech.common.exception.CommonException;
import com.nemustech.common.util.FileUtil;
import com.nemustech.common.util.LogUtil;
import com.nemustech.common.util.SFTPUtil;
import com.nemustech.common.util.SSHUtil;
import com.nemustech.common.util.ThreadUtils;
import com.nemustech.common.util.Utils;

/**
 * 톰캣 서버(SSH) 배포
 */
public class TomcatSSHDeployTask extends AbstractDeployTask {
	@Override
	protected void upload(AbstractDeployTask deployTask, String title, DeployServer deployServer)
			throws CommonException {
		try {
			log("---------- " + title + " ----------");

			log("Sending the war file to \"" + deployServer.getServer_ip() + "\"");
			SFTPUtil ftp = new SFTPUtil(deployServer.getServer_ip(), deployServer.getServer_port(),
					deployServer.getUser_id(), deployServer.getUser_pw());

			String target_full_dir = target_dir + File.separator + system_name + File.separator + target_path;
			ftp.backup(target_full_dir + File.separator + source_file);
			ftp.upload(source_dir + File.separator + source_file, target_full_dir);

			if (Utils.isValidate(lib_path) && !lib_path.startsWith("$")) {
				String lib_dir = FilenameUtils.getFullPathNoEndSeparator(lib_path);
				ftp.backup(target_full_dir + File.separator + lib_path);
				ftp.upload(source_dir + File.separator + lib_path, target_dir + File.separator + system_name
						+ File.separator + target_path + File.separator + lib_dir);
			}

			ftp.disconnect();
		} catch (Exception e) {
			throw new CommonException(CommonException.ERROR, LogUtil.buildMessage(toString(), e.getMessage()), e);
		}
	}

	@Override
	protected void restart(AbstractDeployTask deployTask, String title, DeployServer deployServer)
			throws CommonException {
		try {
			log("Restarting the WAS container \"" + system_name + "\"");
			SSHUtil ssh = new SSHUtil(deployServer.getServer_ip(), deployServer.getServer_port(),
					deployServer.getUser_id(), deployServer.getUser_pw(), deployServer.getOs_name());
			ssh.excuteCommand("cd " + target_dir + File.separator + system_name + File.separator + "bin");
			ssh.excuteCommand("./shutdown.sh");
			Thread.sleep(3000);
			ssh.excuteCommand("ps -ef | grep /" + system_name);

			ssh.excuteCommand("cd " + target_dir + File.separator + system_name + File.separator + target_path);
			ssh.excuteCommand("rm -r " + FilenameUtils.getBaseName(deployTask.getSource_file()));

			ssh.excuteCommand("cd " + target_dir + File.separator + system_name + File.separator + "bin");
			ssh.excuteCommand("./startup.sh");

			ssh.excuteCommand("exit");
			ssh.disconnect();
		} catch (Exception e) {
			throw new CommonException(CommonException.ERROR, LogUtil.buildMessage(toString(), e.getMessage()), e);
		}
	}

	public static void main(String[] args) throws Exception {
		String name = "CompanyBrandManageService";
		String version = "1.0.1-SNAPSHOT";

		String os_name = "Linux";
		String system_name = "cbms";
		String source_dir = "target";
		String target_dir = "/var/was";
		String target_path = "webapps";
		String source_file = "v1#" + system_name + ".war";

		String server_ip = "112.217.207.164";
		int server_port = 20022;
		String user_id = "oracle";
		String user_pw = "nemustech";

		// 파일 복사
		File destFile = new File(source_dir + "/" + source_file);
		try {
			FileUtil.copyFile(new File(source_dir + "/" + name + "-" + version + ".war"), destFile);

			// 배포 및 WAS 재가동
			TomcatSSHDeployTask task = new TomcatSSHDeployTask();
			task.setSystem_name(system_name);
			task.setSystem_name(system_name);
			task.setSource_dir(source_dir);
			task.setTarget_dir(target_dir);
			task.setTarget_path(target_path);
			task.setSource_file(source_file);

			DeployServer deployServer = new DeployServer();
			deployServer.setDeploy_yn(true);
			deployServer.setOs_name(os_name);
			deployServer.setServer_ip(server_ip);
			deployServer.setServer_port(server_port);
			deployServer.setUser_id(user_id);
			deployServer.setUser_pw(user_pw);
			task.addDeployServer(deployServer);

			server_ip = "192.168.3.153";
			user_id = "skoh";
			user_pw = "";

			deployServer = new DeployServer();
			deployServer.setDeploy_yn(true);
			deployServer.setOs_name(os_name);
			deployServer.setServer_ip(server_ip);
			deployServer.setUser_id(user_id);
			deployServer.setUser_pw(user_pw);
			task.addDeployServer(deployServer);

			task.execute();
		} finally {
			// 파일 삭제
			FileUtil.deleteQuietly(destFile);

			ThreadUtils.shutdownThreadPool();
		}
	}
}