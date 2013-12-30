/*
列表页运行时的一些基础数据
*/


//搜索区操作符列表框数据
var opStore = new Ext.data.SimpleStore({　
　　fields:['value','text'],　
　　data:[['=','等于(=)'],['like','匹配(like)'],['>','大于(>)'],['>=','大于或等于(>=)'],['<','小于(<)'],['<=','小于或等于(<=)'],['<>','不等于(<>)']]
});
var opEnZh__={
	'=':'=',
	'like':'包含',
	'not like':'不包含',
	'>':'>',
	'>=':'≥',
	'<':'<',
	'<=':'≤ ',
	'<>':'≠'
};
////数据库字段类型与操作符对应表///////////////
var ftype_op__ = {//[['INT','INT'],['FLOAT','FLOAT'],['DOUBLE','DOUBLE'],['CHAR','CHAR'],['VARCHAR','VARCHAR'],['TEXT','TEXT'],['mediumtext','mediumtext'],['DATETIME','DATETIME']],
	'int':['=','>','<','>=','<=','<>'],	
	'varchar':['like','not like','=','<>'],
	'text':['like','not like'],
	'char':[],
	'float':[],
	'double':[],
	'datetime':[],
	'mediumtext':[],
	'all':['=','like','not like','>','>=','<','<=','<>']
}
ftype_op__.float =ftype_op__.double =ftype_op__.datetime= ftype_op__.int;
ftype_op__.mediumtext = ftype_op__.text;
ftype_op__.char = ftype_op__.varchar;
/////////////////////////////

var andorEnZH__ ={
	'and':'并且','or':'或者'
}

var controlType__={
	textfield:'Ext.form.TextField',
	combo:'Ext.form.ComboBox',
	treecombo:'Ext.ux.TreeComboBox',
	radiogroup:'Ext.ux.RadioGroup',
	datefieldextent:'Ext.ux.DateFieldExtent',
	multiselect:'Ext.ux.MultiSelect',
};

//搜索值控件的默认配置
var sValueControlsCfg__ = {
	combo:{
		//vtype:'combo',
		triggerAction:"all",
		valueField : 'value',
		displayField : 'text',
		typeAhead: true,
	    editable:  true,
	    forceSelection:true,
		mode:'local',
		width:120
	},
	treecombo:{
		//vtype:'treecombo',
		maxHeight:300,
		width:200
	},	
	multiselect:{
		//vtype:'multiselect',
		width:200,
		editable: false,
		separator:'#',
		valueField :"value",
		displayField: "text",
		mode: 'local',
		//forceSelection: true,一定不要声明此句
		triggerAction: 'all',
		emptyText:'请选择'
	},	
	textfield:{
		//vtype:'textfield',
		width:100
	},
	radiogroup:{
		//vtype:'radiogroup',
		width:150
	},
	datefieldextent:{
		xtype:'datefieldextent',//必须的
		allowBlank:true
	}
}
//判断是否支持 text-overflow
function checkChrome(){
	var browserName = navigator.userAgent.toLowerCase();
	if(/chrome/i.test(browserName) && /webkit/i.test(browserName) && /mozilla/i.test(browserName))
		return true;
	else
		return false;
}
var isChrome = checkChrome();
/*删除前导空格*/ 
function leftTrim(ui){ 
        var notValid=/^\s/; 
        while(notValid.test(ui)){ 
                ui=ui.replace(notValid,"");
        } 
        return ui;
}
function myZhuanyi(str){
	var ret = str;
	//ret = ret.replace(/&/g,'&amp;');
	ret = ret.replace(/"/g,'&quot;');
	ret = ret.replace(/>/g,'&gt;');
	ret = ret.replace(/</g,'&lt;');
	return ret;
}	
function myUnZhuanyi(str){
	if(str){
		var ret = str;
		ret = ret.replace(/&quot;/g,'"');
		ret = ret.replace(/&gt;/g,'>');
		ret = ret.replace(/&lt;/g,'<');
		//ret = ret.replace(/&amp;/g,'&');		
		return ret;
	}else{
		return str;
	}
}	

//根据模板渲染内容
function renderField(coLPCFG){
	var tpl = null;
	coLPCFG.tpl = leftTrim(coLPCFG.tpl);
	if(coLPCFG.tpl && coLPCFG.tpl.substring(0,1)!='\'' && coLPCFG.tpl.substring(0,1)!='"') {
		if(coLPCFG.tpl) tpl = new Ext.XTemplate(coLPCFG.tpl);
	}else{
		var tplStr = 'new Ext.XTemplate(' + coLPCFG.tpl + ')';
		if(coLPCFG.tpl) eval('tpl=' + tplStr);
	}
	var width = coLPCFG.width;
	var isShowTip = coLPCFG.isShowTip;
	var tipTpl = null;
	coLPCFG.tipTpl = myUnZhuanyi(coLPCFG.tipTpl);
	coLPCFG.tipTpl = leftTrim(coLPCFG.tipTpl);

	if(coLPCFG.tipTpl) {
		if(coLPCFG.tipTpl.substring(0,1)!='\'' && coLPCFG.tipTpl.substring(0,1)!='"'){
			if(coLPCFG.tipTpl) tipTpl = new Ext.XTemplate(coLPCFG.tipTpl);
		}else{
			var tplStr = 'new Ext.XTemplate(' + coLPCFG.tipTpl + ')';
			if(coLPCFG.tipTpl) eval('tipTpl=' + tplStr);
		}
	}
	return function(value, metadata, record){
		
		Ext.apply(record.data,queryParams);//注入url传入的参数
		var text = value;
		if(tpl) text = tpl.applyTemplate(record.data);
		var displayHtml=text;
		var style = "";
		var divTitle='';
		
		if(isShowTip){
			var tip = value;
			if(tipTpl){
				tip = tipTpl.applyTemplate(record.data);	
				tip = myZhuanyi(tip);
			}
			metadata.attr = 'ext:qtitle=""' + ' ext:qtip="' + tip + '"'; 
			//divTitle=' title="' + value + '" ';
		}
		if(width) {
			if(!isChrome)
				displayHtml= '<div style="width:'+ width +'px;overflow:hidden;" ' + divTitle + '>'+ text +'</div>';
			else
				displayHtml=text;	
		}
		return displayHtml;
		
	}
}
	

function setActiveTab(url,tabId,title){
	if(top&& top.centerTabPanel){
		top.centerTabPanel.addIframe('tab_' + tabId,title ,url,window);
	}else{
		window.open(url);	
	}
}

openTab = function(url,title){
	if(!title) title='';
	if(top&& top.centerTabPanel){
		var _url = url;
		top.centerTabPanel.addIframe('tab_' + (new Date()).valueOf(),title ,_url);
	}else{
		window.open(url);	
	}
}


/*******onMessage处理******/
function onMessageHandler(e){
	var dataStr = e.data;
	try{
		var dataJson = Ext.util.JSON.decode(dataStr);
		if(dataJson.options){
			var options = Ext.decode(decodeURIComponent(dataJson.options));
			var hanlder = options.handler;//处理函数
			var scope = options.scope;
			if(hanlder){
				eval('0,' + hanlder + '.call(' + scope + ',"'+ encodeURIComponent(Ext.encode(dataJson.data)) + '","'+ encodeURIComponent(Ext.encode(options.data)) +'")');
			}
		}
	}catch(ex){
		console.log(ex);
	}
	
}
//监听postMessage消息事件
if (typeof window.addEventListener != 'undefined') {
	window.addEventListener('message', onMessageHandler, false);
} else if (typeof window.attachEvent != 'undefined') {
	window.attachEvent('onmessage', onMessageHandler);
}
