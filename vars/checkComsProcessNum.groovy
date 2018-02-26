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
	def dstPackageName="${config.projectName}.tar.gz";
	def projectName="${config.projectName}";
	def dir_update="${config.dir_update}";
 	def APP_HOSTIP=config.APP_HOSTS.split(',');
 // 	def APP_HOSTNAMES=config.APP_HOSTNAMES.split(',');
	def APP_HOSTSIZE=APP_HOSTIP.size();


	// update left hosts tomcat war and checkUrl
	for (i = 0; i<APP_HOSTSIZE; i++) {
		def APP_LEFT_HOST=APP_HOSTIP[i].trim();
		//sh  "ssh ${saltmasterIP}  'Script_retcode=`sudo salt --batch-size 1 --failhard  -S \"${APP_LEFT_HOST}\" cmd.script_retcode salt://scripts/update_jarComs.sh \"update-all ${TOMCAT_HOME} ${dstPackageName} ${dir_update} ${CheckUrl} ${APP_LEFT_HOST} ${APP_PORT}\" runas=\"${AppRunAs}\" 2>/dev/null|tail -1`'";

		sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_LEFT_HOST}\" cmd.run \"ps -ef|grep java|grep  ${projectName}.jar && ls -l ${TOMCAT_HOME}/${projectName}.jar \" runas=\"${AppRunAs}\" '|tail -12";
		//sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_LEFT_HOST}\" cmd.run \" tar -xf  $dir_update/${dstPackageName} -C  ${TOMCAT_HOME} && echo 'replace tar.gz Ok' \" runas=\"${AppRunAs}\" '";
		//sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_LEFT_HOST}\" cmd.run \" cd ${TOMCAT_HOME}/bin && sh startup.sh && echo 'startup Ok' \" runas=\"${AppRunAs}\" '";

	}
	



	


    } catch (Exception err) {
			error "${err}"
      println "Failled: ${err}"
    }
}