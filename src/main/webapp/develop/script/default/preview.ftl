//#timeout=60#    脚本超时时长，默认60秒，值为-1时不过期
//#plugin=log,util#    插件注入 ","分隔
//#import=#       公共库导入 ","分隔
//@author ${userName!"XXX"} @ ${today!""}

var mailList = "${email!""}";//多个邮箱用 , 分割

var nodeId = dataPool.get("nodeId");
var formId = dataPool.get("formId");
var tplId = dataPool.get("tplId");
var dataId = dataPool.get("dataId");
var renderType = dataPool.get("renderType");

function start() {
	try {
		var xdata = dataPool.get("xdata");
		if(xdata == null) {
		  var _content = form2.render(nodeId, formId, tplId, dataId, renderType);
		} else {
		  var _content = form2.render(nodeId, formId, tplId, xdata, renderType);
		}
		dataPool.put("content", _content);
	} catch (e) {
		var err = e.toString() + "(#" + e.lineNumber + ")";
		log.error(err);
		/*
		var _etitle = "预览脚本异常:" + e.name + "@nodeId[${nodeId!"null"}]|formId[${id1!"null"}]";
		var _coder = pluginFactory.getP("coder");
		var _econtent = _coder.urlEncode("script:${cmppUrl!"null"}\r\n" + err, "UTF-8");
		util.sendMail(mailList, _etitle, _econtent, "");
		*/
	}
}

//start();