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
	def currentTime=System.currentTimeMillis();
	//saltmaster主机 ip
	def saltmasterIP="${config.saltmasterIP}";
	//upstream 后端主机port
	def APP_PORT="${config.APP_PORT}";
	def CheckUrl="${config.CheckUrl}";
	    def projectNameList=config.projectName.split(',');
   	def projectNameSIZE=projectNameList.size();
		    def TOMCAT_HOMEList=config.TOMCAT_HOME.split(',');
   	def TOMCAT_HOMESIZE=TOMCAT_HOMEList.size();
	def dir_update="${config.dir_update}";
 	def APP_HOSTIP=config.APP_HOSTS.split(',');
	def APP_HOSTSIZE=APP_HOSTIP.size();

	for (i = 0; i<APP_HOSTSIZE; i++) {
	    for (f = 0; f<projectNameSIZE; f++) {

			//sh  "ssh ${saltmasterIP}  'Script_retcode=`sudo salt --batch-size 1 --failhard  -S \"${APP_HOSTIP[i]}\" cmd.script_retcode salt://scripts/update_jarComs.sh \"update-all ${TOMCAT_HOMEList[f]} ${projectNameList[f]}.tar.gz ${dir_update} ${CheckUrl} ${APP_HOSTIP[i]} ${APP_PORT}\" runas=\"${AppRunAs}\" 2>/dev/null|tail -1`'";

	        sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_HOSTIP[i]}\" cmd.run \" cd ${dir_update} && rm -rf ${projectNameList[f]}.tar.gz && wget -q http://172.30.33.31:9000/${projectNameList[f]}.tar.gz && tar -xf ${projectNameList[f]}.tar.gz -C ${TOMCAT_HOMEList[f]} && cd ${TOMCAT_HOMEList[f]}/bin && sh shutdown.sh && echo \'${projectNameList[f]} shutdown Ok\' && cksum $dir_update/${projectNameList[f]}.tar.gz && tar -xf  $dir_update/${projectNameList[f]}.tar.gz -C  ${TOMCAT_HOMEList[f]} && echo \'replace ${projectNameList[f]} tar.gz Ok\' && cd ${TOMCAT_HOMEList[f]}/bin && sh startup.sh && echo \'${projectNameList[f]} startup Ok\'||echo \'update ${projectNameList[f]}  error\' \" runas=\"${AppRunAs}\" '";
			//sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_HOSTIP[i]}\" cmd.run \" cd ${TOMCAT_HOMEList[f]}/bin && sh shutdown.sh && echo 'shutdown Ok' \" runas=\"${AppRunAs}\" '";
			//sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_HOSTIP[i]}\" cmd.run \" cksum $dir_update/${projectNameList[f]}.tar.gz && tar -xf  $dir_update/${projectNameList[f]}.tar.gz -C  ${TOMCAT_HOMEList[f]} && echo 'replace tar.gz Ok' \" runas=\"${AppRunAs}\" '";
			//sh "ssh ${saltmasterIP} 'sudo salt --batch-size 1 --failhard -S \"${APP_HOSTIP[i]}\" cmd.run \" cd ${TOMCAT_HOMEList[f]}/bin && sh startup.sh && echo 'startup Ok' \" runas=\"${AppRunAs}\" '";
        }
	}
	

	


    } catch (Exception err) {
			error "${err}"
      println "Failled: ${err}"
    }
}