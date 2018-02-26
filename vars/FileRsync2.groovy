#!/usr/bin/groovy
/*
projectName名称需要指定，具体到子项目名称
packageName名称需要指定，具体到.jar还是.war
*/
def call(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()
  // 项目位置
  def saltmasterIP = "${config.saltmasterIP}"
  def saltMasterTmp = "${config.saltMasterTmp}"
  def saltMasterProjectPath = "${config.saltMasterProjectPath}"
  //def APP_HOSTNAME = "${config.APP_HOSTNAME}"
   	//def APP_HOSTIP=config.APP_HOSTS;
   	def APP_HOSTIP=config.APP_HOSTS.split(',');
   	def APP_HOSTSIZE=APP_HOSTIP.size();
 // 	def APP_HOSTNAMES=config.APP_HOSTNAMES.split(',');


  def saltMasterUPath = "${config.saltMasterUPath}"
  // 源包位置
  def DIR_SRC_UPDATE = "${config.dir_update}"
  // 编译完成包名称
  def dstPackageName = "${config.dstPackageName}"
  def dstPackageDir = "${config.dstPackageDir}"
try {
  // delete old buildspace
  sh (script: "echo `cksum ${env.WORKSPACE}/${dstPackageDir}/${dstPackageName}` && scp -r ${env.WORKSPACE}/${dstPackageDir}/${dstPackageName}  ${saltmasterIP}:${saltMasterTmp}/",returnStdout: true)
  sh (script: "ssh ${saltmasterIP} 'sudo mv ${saltMasterTmp}/${dstPackageName} ${saltMasterProjectPath}/${dstPackageName} ' ",returnStdout: true)
 for (i = 0; i<APP_HOSTSIZE; i++) {
  // salt rm old update_war_file
  sh (script: "ssh ${saltmasterIP} 'sudo salt -S \"${APP_HOSTIP[i]}\" cmd.run \'rm  ${DIR_SRC_UPDATE}/${dstPackageName}\' ' ",returnStdout: true)
  // salt  get_war_file
  sh (script: "ssh ${saltmasterIP} 'sudo salt -S  \"${APP_HOSTIP[i]}\" cp.get_file ${saltMasterUPath}/${dstPackageName} ${DIR_SRC_UPDATE}/${dstPackageName}' ",returnStdout: true)
 }
     
 } catch (err) {
      println "Failled: ${err}"
    }

  
}