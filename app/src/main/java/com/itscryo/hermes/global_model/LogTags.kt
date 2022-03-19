package com.itscryo.hermes.global_model

class LogTags(private val classname: String) {

	private val baseTag = "CustomLogHermes: "
	val debug = baseTag + "Debug $classname"
	val info = baseTag + "Info $classname"
	val error = baseTag + "Error $classname"
	val warn = baseTag + "Warn $classname"

}