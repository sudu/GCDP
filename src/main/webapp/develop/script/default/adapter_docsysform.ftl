//#timeout=60#    脚本超时时长，默认60秒，值为-1时不过期
//#plugin=log,util,http#    插件注入 ","分隔
//#import=#       公共库导入 ","分隔
//@author ${userName!"XXX"} @ ${today!""}

var mailList = "${email!""}";//多个邮箱用 , 分割


//===============================start
//需要的本地表单字段，返回本地表单所需字段，格式 "field1,field2,field3"
function getNeedField(){
    // TODO 需要根据实际情况实现
    
    return "id,title_x,subTitle_x,author_x";
}

//本地表单数据与系统表单数据进行字段适配，localFormData与sysFormData为Map
function fieldAdapter(localFormData, sysFormData){
    // TODO 需要根据实际情况实现
    
    var id = localFormData.get("id");
    var title = localFormData.get("title_x");
    var url = localFormData.get("subTitle_x");
    var author = localFormData.get("author_x");
    
    sysFormData.put("uuid","");
    sysFormData.put("id",id);
    sysFormData.put("type","");
    sysFormData.put("title",title);
    sysFormData.put("url",url);
    sysFormData.put("author",author);
    sysFormData.put("source","");
    sysFormData.put("sourceUrl","");
    sysFormData.put("keywords","");
    sysFormData.put("weight","");
    sysFormData.put("summary","");
    sysFormData.put("channelIds","");
    sysFormData.put("newsTime","");
    sysFormData.put("status","");
    sysFormData.put("mainImage","");
    sysFormData.put("thumbnail","");
    sysFormData.put("content","");
    
    
    //文章系统表单所有字段
    /*sysFormData.put("id","");
    sysFormData.put("uuid","");
    sysFormData.put("oid","");
    sysFormData.put("type","");
    sysFormData.put("title","");
    sysFormData.put("url","");
    sysFormData.put("author","");
    sysFormData.put("source","");
    sysFormData.put("sourceUrl","");
    sysFormData.put("keywords","");
    sysFormData.put("weight","");
    sysFormData.put("summary","");
    sysFormData.put("channelIds","");
    sysFormData.put("newsTime","");
    sysFormData.put("status","");
    sysFormData.put("mainImage","");
    sysFormData.put("thumbnail","");
    sysFormData.put("content","");*/
}

//对查询条件Q进行适配，
//q原格式为 "value1:field1,value2:field2,value3:field3"；（fieldX为文章系统表单字段）
//适配后格式"value1:adapterfield1,value2:adapterfield2,value3:adapterfield3"（adapterfieldX为本地表单字段）
var fieldEnum = {"id":0,"title":1,"channelIds":2};
function getInAdapterQ(q){
    // TODO 需要根据实际情况实现
    
    var adapterQ = "";
    var arr = q.split(",");
    for(var i=0;i<arr.length;i++){
        var arr2 = arr[i].split(":");
        if(arr2.length != 2){
            continue;
        }

        switch(fieldEnum[arr2[1]]){
        case fieldEnum.id:
            adapterQ = adapterQ + arr[i] + ",";
            break;
        case fieldEnum.title:
            adapterQ = adapterQ + arr2[0] + ":" + "title_x,";//title_x为对应的本地表单字段
            break;
        case fieldEnum.channelIds:
            //查询某个栏目下的文章列表，拼装查询条件
            adapterQ = adapterQ + getQueryToChannelIds(arr2[0]) + ",";
            break;
        //...TODO 需要什么查询条件，增加
        default:
            break;
        }
    }
    if (adapterQ.length < 1) {
        return "";
    } else {
        return adapterQ.substring(0, adapterQ.length - 1);
    }
}
//查询某个栏目下的文章列表，拼装查询条件;返回格式"value1:adapterfield1,value2:adapterfield2,value3:adapterfield3"（adapterfieldX为本地表单字段）
function getQueryToChannelIds(value){
    // TODO 需要根据实际情况实现
    
    return value + ":" + "id";
}

//===============================end






function start(){
    try{
    
        var formId = dataPool.get("formId");
        var listId = dataPool.get("listId");
        var from = dataPool.get("from");
        //var fd = dataPool.get("fd");
        //需要的本地表单字段
        var fd = getNeedField(); 
        var q = dataPool.get("q");
        //log.info("1q="+q);
        q = getInAdapterQ(q);
        //log.info("2q="+q);
        var sort = dataPool.get("sort");
        var start = dataPool.get("start");
        var limit = dataPool.get("limit");
        var url = dataPool.get("url");

        var param = util.initMap();
        param.put("formId", formId);
        param.put("listId", listId);
        param.put("from", from);
        param.put("fd", fd);
        param.put("q", q);
        param.put("sort", sort);
        param.put("start", start);
        param.put("limit", limit);
        var rensponse = http.sendPost(url, param);
        var rensponseMap = util.JsonToMap(rensponse);
        
        //sendPost出现异常时
        if(!rensponseMap.containsKey("data")){
            dataPool.put("result", rensponse);
            return;
        }
        
        var ret = util.initMap();
        var datas = util.initList();
        ret.put("totalCount",rensponseMap.get("totalCount"));
        ret.put("data",datas);
        
        var list = util.JsonToList( rensponseMap.get("data").toString() )
        for(var i=0;i<list.size();i++){
            var localFormData = util.JsonToMap( list.get(i).toString() )
            var sysFormData = util.initMap();
            //本地表单数据与系统表单数据进行字段适配
            fieldAdapter(localFormData,sysFormData);
            
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




