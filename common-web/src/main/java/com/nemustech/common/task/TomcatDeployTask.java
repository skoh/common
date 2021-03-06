package com.nemustech.common.task;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.nemustech.common.exception.CommonException;
import com.nemustech.common.util.FTPUtil;
import com.nemustech.common.util.LogUtil;
import com.nemustech.common.util.TelnetUtil;
import com.nemustech.common.util.ThreadUtils;

/**
 * 톰캣 서버 배포
 */
public class TomcatDeployTask extends AbstractDeployTask {
	@Override
	protected void upload(AbstractDeployTask deployTask, String title, DeployServer deployServer)
			throws CommonException {
		try {
			log("---------- " + title + " ----------");

			log("--- Sending the war file to \"" + deployServer.getServer_ip() + "\"");
			FTPUtil ftp = new FTPUtil(deployServer.getServer_ip(), deployServer.getServer_port(),
					deployServer.getUser_id(), deployServer.getUser_pw());

			String target_full_dir = target_dir + File.separator + system_name + File.separator + target_path;
			ftp.backup(target_full_dir, source_file);
			ftp.upload(source_dir, source_file, target_full_dir);
			ftp.disconnect();
		} catch (Exception e) {
			throw new CommonException(CommonException.ERROR, LogUtil.buildMessage(toString(), e.getMessage()), e);
		}
	}

	@Override
	protected void restart(AbstractDeployTask deployTask, String title, DeployServer deployServer)
			throws CommonException {
		try {
			log("--- Restarting the WAS container \"" + system_name + "\"");
			TelnetUtil telnet = new TelnetUtil(deployServer.getServer_ip(), deployServer.getServer_port(),
					deployServer.getUser_id(), deployServer.getUser_pw(), deployServer.getOs_name(),
					deployServer.getTerminal_type(), deployServer.getCharset_name());
			telnet.excuteCommand("cd " + target_dir + File.separator + system_name + File.separator + "bin");
			telnet.excuteCommand("shutdown");
			Thread.sleep(3000);
			telnet.excuteCommand("ps -ef | grep /" + system_name);

			telnet.excuteCommand("cd " + target_dir + File.separator + system_name + File.separator + target_path);
			telnet.excuteCommand("rm -r " + FilenameUtils.getBaseName(deployTask.getSource_file()));

			telnet.excuteCommand("cd " + target_dir + File.separator + system_name + File.separator + "bin");
			telnet.excuteCommand("startup");

			telnet.excuteCommand("exit");
			telnet.disconnect();
		} catch (Exception e) {
			throw new CommonException(CommonException.ERROR, LogUtil.buildMessage(toString(), e.getMessage()), e);
		}
	}

	public static void main(String[] args) throws Exception {
		String name = "common-web";
		String version = "1.0";

		String os_name = "Microsoft";
		String system_name = "skoh";
		String source_dir = "target";
		String target_dir = "target";
		String target_path = "";
		String source_file = name + "-" + version + ".war";

		String server_ip = "192.168.3.115";
		String user_id = "skoh";
		String user_pw = "skoh";

		try {
			TomcatDeployTask task = new TomcatDeployTask();
			task.setSystem_name(system_name);
			task.setSource_dir(source_dir);
			task.setTarget_dir(target_dir + File.separator + system_name + File.separator + target_path);
			task.setSource_file(source_file);

			DeployServer deployServer = new DeployServer();
			deployServer.setDeploy_yn(true);
			deployServer.setOs_name(os_name);
			deployServer.setServer_ip(server_ip);
			deployServer.setUser_id(user_id);
			deployServer.setUser_pw(user_pw);
			task.addDeployServer(deployServer);

			task.execute();
		} finally {
			ThreadUtils.shutdownThreadPool();
		}
	}
}