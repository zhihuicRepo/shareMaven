#!/usr/bin/env groovy

/*
用用户输入的参数值代替来自配置文件的变量。
Write by weiye in 20171130    
*/


def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    
    insteadValue()

}


def insteadValue() {

	def paramsKeys = params.keySet()

	paramsKeys.each {

	if(env.getEnvironment().containsKey(it) && params[it].trim() && params[it].trim().toLowerCase() != "default") {
          env[it] = params[it]
		}
	}
}

