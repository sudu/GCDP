//#timeout=60#    脚本超时时长，默认60秒，值为-1时不过期
//#plugin=log,util#    插件注入 ","分隔
//#import=#       公共库导入 ","分隔
//@author ${userName!"XXX"} @ ${today!""}

var mailList = "${email!""}";//多个邮箱用 , 分割

function start() {
	try {
		//todo
	} catch (e) {
		var err = e.toString() + "(#" + e.lineNumber + ")";
		log.error(err);
		/*
		var _etitle = "任务脚本异常:" + e.name + "@nodeId[${nodeId!"null"}]|taskId[${id1!"null"}]";
		var _coder = pluginFactory.getP("coder");
		var _econtent = _coder.urlEncode("script:${cmppUrl!"null"}\r\n" + err, "UTF-8");
		util.sendMail(mailList, _etitle, _econtent,"");
		*/
	}
}

//start();