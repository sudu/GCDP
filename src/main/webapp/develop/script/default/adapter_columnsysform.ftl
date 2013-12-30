//#timeout=60#    脚本超时时长，默认60秒，值为-1时不过期
//#plugin=log,util,http#    插件注入 ","分隔
//#import=#       公共库导入 ","分隔
//@author ${userName!"XXX"} @ ${today!""}

var mailList = "${email!""}";//多个邮箱用 , 分割

var formId = dataPool.get("formId");
var listId = dataPool.get("listId");
var from = dataPool.get("from");
//var fd = dataPool.get("fd");
var where = dataPool.get("where");
var sort = dataPool.get("sort");
var start_ = dataPool.get("start");
var limit = dataPool.get("limit");
var url = dataPool.get("url");
var sourceHost = dataPool.get("sourceHost");
var nodeId = dataPool.get("nodeId");
// 获取最底级域名
var lowLevelDomain = sourceHost.split(":")[0].split("\\.")[0];



//需要的本地表单字段，返回本地表单所需字段，格式 "field1,field2,field3"
function getNeedField(){
    // TODO 需要根据实际情况实现
    return "id,title,parentId,path";
}
//本地表单数据与系统表单数据进行字段适配，localFormData与sysFormData为Map
function fieldAdapter(localFormData, sysFormData){
    // TODO 需要根据实际情况实现
    var id = localFormData.get("id");
    var title= localFormData.get("title");
    var parentId = localFormData.get("parentId");
    var path= localFormData.get("path");
    
    
    sysFormData.put("id",id);
    sysFormData.put("uuid",getUUID(id, "id", "parentId"));
    sysFormData.put("oid",localFormData.get("id"));
    sysFormData.put("name",title);
    sysFormData.put("path",path);  
    sysFormData.put("puuid",getUUID(parentId, "id", "parentId"));
    
    //栏目系统表单所有字段
   /* sysFormData.put("id","");
    sysFormData.put("uuid","");
    sysFormData.put("cmpp","");
    sysFormData.put("nodeId","");
    sysFormData.put("oid","");
    sysFormData.put("name","");
    sysFormData.put("path","");
    sysFormData.put("host","");    
    sysFormData.put("puuid","");*/
}




var localFormDataList = util.initList();//本地表单数据列表
var idMap = util.initMap();//key为栏目ID；value为父栏目ID
var idCacheMap = util.initMap();//key为栏目ID；value为UUID
function getUUID(id, idName, pidName){//id为本地栏目ID字段值；idName为本地栏目ID字段名称；pidName为本地父栏目ID字段名称
    if(idMap.size() < 1){
        for(var i=0;i<localFormDataList.size();i++){
            var localFormData = localFormDataList.get(i);
            idMap.put(localFormData.get(idName)+"", localFormData.get(pidName)+"");
        }
    }
    
    var uuid = "";
    var tempId = id + "";
    while(tempId != "0"){//默认根栏目ID值为0
        if(idCacheMap.containsKey(tempId)){
            uuid = idCacheMap.get(tempId) + uuid;
            idCacheMap.put(id+"", uuid);
            return uuid;
        }
        uuid = tempId + "-" + uuid;
        tempId = idMap.get(tempId);
    }
    
    uuid = lowLevelDomain + "-" + nodeId + "-" + uuid;
    idCacheMap.put(id+"", uuid);
    return uuid;
}



function start(){
    try{

        var param = util.initMap();
        param.put("formId", formId);
        param.put("listId", listId);
        param.put("from", from);
        param.put("fd",getNeedField());
        if(where != null && where != ""){
            param.put("where", where);
        }
        if(sort != null && sort != ""){
            param.put("sort", sort);
        }
        param.put("start", start_);
        param.put("limit", limit);
        var rensponse = http.sendPost(url, param);
        var rensponseMap = util.JsonToMap(rensponse);
        
        //sendPost出现异常时
        if(!rensponseMap.containsKey("data")){
            dataPool.put("result", rensponse);
            return;
        }
        
        var ret = util.initMap();
        // 增加cmpp,nodeId,host
        ret.put("cmpp",sourceHost);
        ret.put("nodeId",nodeId);
        // TODO host 暂时写死
        ret.put("host",lowLevelDomain + ".ifeng.com");
        
        ret.put("totalCount",rensponseMap.get("totalCount"));
        var datas = util.initList();
        ret.put("data",datas);
        
        
        
        /*var list = util.JsonToList( rensponseMap.get("data").toString() )
        for(var i=0;i<list.size();i++){
            var localFormData = util.JsonToMap( list.get(i).toString() )
            var sysFormData = util.initMap();
            //本地表单数据与系统表单数据进行字段适配
            fieldAdapter(localFormData,sysFormData);
            
            datas.add(sysFormData);
        }*/
        
        
        var list = util.JsonToList( rensponseMap.get("data").toString() )
        for(var i=0;i<list.size();i++){
            var localFormData = util.JsonToMap( list.get(i).toString() )
            localFormDataList.add(localFormData);
        }

        for(var i=0;i<localFormDataList.size();i++){
            var sysFormData = util.initMap();
            //本地表单数据与系统表单数据进行字段适配
            fieldAdapter(localFormDataList.get(i),sysFormData);
            datas.add(sysFormData);
        }
        
        dataPool.put("data", util.MapToJson(ret))
        
    }catch(e){
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

start();