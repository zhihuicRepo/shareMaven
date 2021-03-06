#!/usr/bin/env groovy
/*
必须包含(待测试tomcat_update.sh  AppRunAs HealthCheckFunction DIR_SRC_UPDATE CheckUrl) saltmasterIP、NgHostName 、 NGINX_CONF 、 NGINX_DAEMON 、 APP_HOSTS 和APP_PORT 五个属性
*/
def call(body) {
def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = config
	body()
	try {
	def currentTime=System.currentTimeMillis()
	//nginx 主机名称
	def NgHostName="${config.NgHostName}";
	//nginx conf file配置文件
	def NGINX_CONF="${config.NGINX_CONF}";
    //nginx 启动文件
	def NGINX_DAEMON="${config.NGINX_DAEMON}";
	//saltmaster主机 ip
	def saltmasterIP="${config.saltmasterIP}";
	//upstream 后端主机port
	def APP_PORT="${config.APP_PORT}";
	def CheckUrl="${config.CheckUrl}";
	def dstPackageName="${config.dstPackageName}";
	def dir_update="${config.dir_update}";
 	def APP_HOSTIP=config.APP_HOSTS.split(',');
 // 	def APP_HOSTNAMES=config.APP_HOSTNAMES.split(',');
	def APP_HOSTSIZE=APP_HOSTIP.size();
  def MID=APP_HOSTSIZE/2
	def APP_LEFT_HOSTS=APP_HOSTIP[0..(MID-1)]
	def APP_RIGHT_HOSTS=APP_HOSTIP[MID..(APP_HOSTSIZE-1)]
	//update left hosts

	// update left hosts tomcat war and checkUrl
	for (i = 0; i<APP_LEFT_HOSTS.size; i++) {
		def APP_LEFT_HOST=APP_LEFT_HOSTS[i].trim();
		//sh  "ssh ${saltmasterIP}  'Script_retcode=`sudo salt --batch-size 1 --failhard  -S \"${APP_LEFT_HOST}\" cmd.script_retcode salt://scripts/update_jarComs.sh \"update-all ${TOMCAT_HOME} ${dstPackageName} ${dir_update} ${CheckUrl} ${APP_LEFT_HOST} ${APP_PORT}\" runas=\"${AppRunAs}\" 2>/dev/null|tail -1`'";

		sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_LEFT_HOST}\" cmd.run \" cd ${TOMCAT_HOME}/bin && sh shutdown.sh && echo 'shutdown Ok' \" runas=\"${AppRunAs}\" '";
		sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_LEFT_HOST}\" cmd.run \" cksum $dir_update/${dstPackageName} && tar -xf  $dir_update/${dstPackageName} -C  ${TOMCAT_HOME} && echo 'replace tar.gz Ok' \" runas=\"${AppRunAs}\" '";
		sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_LEFT_HOST}\" cmd.run \" cd ${TOMCAT_HOME}/bin && sh startup.sh && echo 'startup Ok' \" runas=\"${AppRunAs}\" '";

	}
	


	// update right hosts tomcat war and checkUrl
	for (i = 0; i<APP_RIGHT_HOSTS.size; i++) {
		def APP_RIGHT_HOST=APP_RIGHT_HOSTS[i].trim();
		sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_RIGHT_HOST}\" cmd.run \" cd ${TOMCAT_HOME}/bin && sh shutdown.sh && echo 'shutdown Ok' \" runas=\"${AppRunAs}\" '";
		sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_RIGHT_HOST}\" cmd.run \" cksum $dir_update/${dstPackageName} && tar -xf  $dir_update/${dstPackageName} -C  ${TOMCAT_HOME} && echo 'shutdown Ok' \" runas=\"${AppRunAs}\" '";
		sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_RIGHT_HOST}\" cmd.run \" cd ${TOMCAT_HOME}/bin && sh startup.sh && echo 'startup Ok' \" runas=\"${AppRunAs}\" '";
		//sh  "ssh ${saltmasterIP}  'sudo salt --batch-size 1 --failhard -S \"${APP_RIGHT_HOST}\" cmd.script_retcode salt://scripts/update_jarComs.sh \"update-all ${TOMCAT_HOME} ${dstPackageName} ${dir_update} ${CheckUrl} ${APP_RIGHT_HOST} ${APP_PORT}\" runas=\"${AppRunAs}\" ' >~/caimi.tmp";
		//sh  "tail -6 ~/caimi.tmp"
		//sh  "Script_retcode=`tail -1 ~/caimi.tmp`;if [[ "$Script_retcode" -eq "0" ]]; then echo "${APP_RIGHT_HOST}" Success;else echo "${APP_RIGHT_HOST} error" && exit 1;fi"
		//sh (script: "ssh ${saltmasterIP}  'sudo salt -S \"${APP_RIGHT_HOST}\" cmd.script salt://scripts/update_jarComs.sh \"update-all ${TOMCAT_HOME} ${dstPackageName} ${dir_update} ${CheckUrl} ${APP_RIGHT_HOST} ${APP_PORT}\" runas=\"${AppRunAs}\" ' ",returnStdout: true);

	}
	


    } catch (Exception err) {
			error "${err}"
      println "Failled: ${err}"
    }
}