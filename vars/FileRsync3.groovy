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
  try {
          // 项目位置
        def projectPath = "${env.WORKSPACE}/${config.svnLocal}"
        def buildPath="${env.WORKSPACE}/buildspace"
        def JarTarDir="${env.WORKSPACE}/jarDir"
        
        def dockerFileContext="""FROM 172.30.33.31:5000/base/nginx:latest
        MAINTAINER devops "devops@quarkfinance.com"
        RUN sed -i '/index.htm;/aallow 172.30.32.242;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.30.53;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.243;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.244;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.69;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.70;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.71;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.75;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.76;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.77;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.79;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.32.78;' /etc/nginx/conf.d/default.conf && sed -i '/index.htm;/aallow 172.30.33.135;' /etc/nginx/conf.d/default.conf && sed -i '/172.30.32.242;/adeny all;' /etc/nginx/conf.d/default.conf
        """
        
        sh (script: "cd ${projectPath} && mkdir -p ../jarDir ../buildspace && find ./ -name \"*.tar.gz\" -exec cp {} ../jarDir \\; && cksum ../jarDir/* || echo 'FileRsync3 changeTo docker_nginx4coms error'",returnStdout: true)
          
        // 生成Dockerfile
        writeFile encoding: 'UTF-8', file: "${buildPath}/Dockerfile",text: dockerFileContext    
        	// 执行docker build
        sh (script: "cd ${buildPath} && docker build -t 172.30.33.31:5000/base/nginx-coms . && docker push 172.30.33.31:5000/base/nginx-coms  && docker stop nginx4coms && docker rm nginx4coms && docker run -d  -p 9000:80  --name nginx4coms -v ${JarTarDir}:/usr/share/nginx/html 172.30.33.31:5000/base/nginx-coms || echo 'FileRsync3 build build error' ",returnStdout: true)
             
 } catch (err) {
      println "Failled: ${err}"
    }

  
}