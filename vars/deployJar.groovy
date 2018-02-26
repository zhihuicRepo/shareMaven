#!/usr/bin/groovy
/*
projectName名称需要指定，具体到子项目名称
packageName名称需要指定，具体到.jar还是.war
必须包含 jarName 、 remoteDir 和 remoteIps 三个属性。javaOpts为可选参数，如 -Xms3g -Xmx3g
*/
def call(body) {
  def config = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = config
  body()
  // 项目位置
  def localSrcWar = "${env.WORKSPACE}/${config.localSrcWar}"
  def localSvnConf = "${env.WORKSPACE}/${config.localSvnConf}"
  // 源包位置
  def srcPackageName = "${localSrcWar}/${config.srcPackageName}"
  // 编译完成包名称
  //def dstPackageName = "${env.WORKSPACE}/${config.dstPackageName}"
  //需要将编译后的软件包拷贝到的路径
  def buildPath="${env.WORKSPACE}/${config.projectName}"
  def srcbuildName="${env.WORKSPACE}/${config.srcProjectName}"
  // delete old buildspace
  sh (script: "rm -rf  ${buildPath}",returnStdout: true)
  sh (script: "rm -rf  ${env.WORKSPACE}/${config.dstPackageName}",returnStdout: true)
  // unzip war file

      sh (script: "tar xf ${srcPackageName} -C ${env.WORKSPACE}",returnStdout: true)
      sh (script: "mv ${srcbuildName} ${buildPath}",returnStdout: true)
	  sh (script: "rsync -av --exclude .svn/ ${localSvnConf}/ ${buildPath}/",returnStdout: true)
	  sh (script: "cd ${env.WORKSPACE} && tar -zcf ${config.dstPackageName} ${config.projectName}/",returnStdout: true)
	  

}