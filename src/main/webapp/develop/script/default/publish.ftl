//#timeout=60#    脚本超时时长，默认60秒，值为-1时不过期
//#plugin=log,util,form2,sendFile#    插件注入 ","分隔
//#import=#       公共库导入 ","分隔
//@author ${userName!"XXX"} @ ${today!""}

var mailList = "${email!""}";//多个邮箱用 , 分割

var nodeId = dataPool.get("nodeId");
var formId = dataPool.get("formId");
var tplId = dataPool.get("tplId");
var dataId = dataPool.get("dataId");

function start() {
	try {
		var result = form2.preview(nodeId, formId, tplId, dataId, "publish");
		if(result == null) {
			throw Error("preview result is null");
		}
		var content = result.get("content");
		var url = result.get("url")+"";
		if(content == null || url == null || url.length == 0) {
			return;
		}
		var ret = sendFile.sendData(content, url);
		if(ret != null) {
			if(ret.get("ret") != 1) {
				log.error("分发失败:" + url);
			}
		}
	} catch (e) {
		var err = e.toString() + "(#" + e.lineNumber + ")";
		log.error(err);
		/*
		var _etitle = "发布脚本异常:" + e.name + "@nodeId[${nodeId!"null"}]|formId[${id1!"null"}]";
		var _coder = pluginFactory.getP("coder");
		var _econtent = _coder.urlEncode("script:${cmppUrl!"null"}\r\n" + err, "UTF-8");
		util.sendMail(mailList, _etitle, _econtent, "");
		*/
	}
}

//start();