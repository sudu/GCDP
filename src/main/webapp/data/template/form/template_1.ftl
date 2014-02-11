 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单视图</title>
    <link rel="stylesheet" type="text/css" href="../res/js/easyui/themes/bootstrap/easyui.css">
    <link rel="stylesheet" type="text/css" href="../res/js/easyui/themes/bootstrap/custom.css">
	<link rel="stylesheet" type="text/css" href="../res/css/runTime-easyui.css">

    <script type="text/javascript" src="../res/js/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="../res/js/jquery/jquery.tmpl.min.js"></script>
	
    <script type="text/javascript" src="../res/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="../res/js/easyui/locale/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/config/commonUI4RT_cfg.js?20140101"></script>  
	<style type="text/css">  
    html,body  
    {  
        height:100%;  
        margin:0 auto; 
		font:normal 12px verdana;
		margin:0;
		padding:0;
		border:0 none;		
    }  
	</style> 
	
	<#if relyCSSList??>
	<!-- 控件依赖的CSS 开始 -->
	<#list relyCSSList as src>	
		<#if src??>
		<link rel="stylesheet" type="text/css" href="../res/css/${src!""}" />
		</#if>
	</#list>
	<!-- 控件依赖的CSS 结束 -->
	</#if>
	<#if relyJSList??>
	<!-- 控件依赖的JS 开始 -->	
	<#list relyJSList as src>	
		<#if src??>
		<script type="text/javascript" src="../res/js/${src!""}"></script>
		</#if>
	</#list>
	<!-- 控件依赖的JS 结束 -->
	</#if>
	<script>
	//吐出的数据
	var formConfig__ = {
		id:#{id!0},
		formId:#{formId!0},
		viewId:#{viewId!0},
		nodeId:#{nodeId!0},
		config: ${config!0},
		recordData:decodeURIComponent("${(recordData!"{}")?url('UTF-8')}"),
		dataSource:${dataSource!"{}"}			
	};
	formConfig__.recordData = formConfig__.recordData?JSON.parse(formConfig__.recordData):{};	
	</script>
<!-- headInject注入  -->
	${headInject!""}
<!-- headInject注入 end -->	
</head>
<body class="easyui-layout">  
    <form id="form1" style="height:100%; border:green 0px solid;" region="center">  
    <div region="center">
		<div class="easyui-panel" title="请填写表单" style="" data-options="fit:true,border:true">
			<div style="padding:10px 0 10px 10px">
				<table id="formTable" style="width: 100%;"></table>
			</div>
		</div>
	</div>  
	<div style="height:50px;" region="south">
		<div style="text-align:center;padding:5px">
            <a href="javascript:void(0)" class="easyui-linkbutton">保存</a>
			<a href="javascript:void(0)" class="easyui-linkbutton">保存并添加</a>
            <a href="javascript:void(0)" class="easyui-linkbutton">保存并关闭</a>
			<a href="javascript:void(0)" class="easyui-linkbutton">预览</a>
			<a href="javascript:void(0)" class="easyui-linkbutton">关闭</a>
        </div>
	</div>  
    </form>  
	 	
<script type="text/javascript">
(function ($) {
	function init(target){
		return $(target);
	}
	
	function render(target){
		var state = $.data(target, 'formRender');
		var opts = state.options;
		var controls = opts.controlsConfig;
		createUI(controls,state.formRender,opts);

	}
	function createUI(ctrlCfg,parentCtrl,opts){
		var cfg = formConfig__;
		for(var i=0;i<ctrlCfg.length;i++){
			var item = ctrlCfg[i];
			if(item.rtPostback){
				item.ui.rtPostback=item.rtPostback;
				delete item.rtPostback;
			}
			var uiType = getUIType(item.ctrlName);
			var commonUI = Designer.controls.ui[uiType];
			var inputTemplate=null;
			var formItemTemplate4HideLabel=null;
			var formItemTemplate=null;
	
			if(commonUI){
				item.ui = $.extend({},commonUI.runtime.ui,item.ui);
				inputTemplate= commonUI.runtime.inputTemplate;
				formItemTemplate4HideLabel = commonUI.runtime.formItemTemplate4HideLabel;
				formItemTemplate = commonUI.runtime.formItemTemplate;
			}
			//清除ui项配置为空字符串项 
			for(var key in item.ui){
				if(item.ui[key]===""){
					delete item.ui[key];
				}
			}
			item.ui.inputTemplate = inputTemplate!==null && inputTemplate!==undefined ? inputTemplate : opts.inputTemplate;
			item.ui.formItemTemplate4HideLabel = formItemTemplate4HideLabel!==null && formItemTemplate4HideLabel!==undefined ? formItemTemplate4HideLabel : opts.formItemTemplate4HideLabel;
			item.ui.formItemTemplate = formItemTemplate!==null && formItemTemplate!==undefined ? formItemTemplate : opts.formItemTemplate;

			//处理ui配置项类型为function的属性
			for(var key in item.ui["function"]){
				var value = item.ui["function"][key];
				try{item.ui[key] = eval("(" + value + ")");}
				catch(ex){alert("控件" +item.ctrlName　+ " " + key + "的配置不合法，函数语法有误。")}
			}
			delete item.ui["function"];
			
			//判断控件包是否存在
			eval('var ctrClass = $.fn.' + item.name);
			if(typeof ctrClass=="undefined"){
				$.messager.alert('控件初始化出错',"缺少控件库" + item.name + "脚本文件",'error');
				return;
			}
			
			if(item.controls){
				var ctrl = appendChildCtrl(opts,item,parentCtrl);
				createUI(item.controls,ctrl,opts);
			}else{
				if(item.internalCfg)
					item.ui.internalCfg=item.internalCfg;
				if(item.ui.readOnly){
					item.ui.placeholder="";
					item.ui.required =false;	
				}
				if(item.data && item.data.ext_dataSource_value){
					if(item.data.ext_dataSource_type=='sql'){
						var dataSource = formConfig__.dataSource[item.ui.id];
						if(dataSource) item.ui.data = dataSource;
					}else if(item.data.ext_dataSource_type=='url'){
						item.ui.url = item.data.ext_dataSource_value;
					}else{
						var s = item.data.ext_dataSource_value;
						if(Object.prototype.toString.call(s)!="[object Array]"){
							try{
								s=eval("("+s+")");//[];	
							}catch(e){
								s=[s];	//string
							}
						}
						item.ui.data=s;	
					}
				}
				if(item.f_name){ 
					item.ui.name = 'xform.' + item.f_name;
				}else{
					item.ui.name =item.ui.id;
				}
				var inputType = 'inputType_new';
				if(formConfig__.id==0){//添加状态
					inputType = 'inputType_new';
				}else{//编辑状态
					inputType = 'inputType_edit';
				}
				var obj=null;
				var v = typeof(cfg.recordData[item.f_name])!='undefined'?cfg.recordData[item.f_name]:(item.ui.value?item.ui.value:"");
				if(item.mod[inputType]==3){
					item.ui.readOnly=true;
					item.ui.disabled=true;
					if(item.f_name && v!=="")
						item.ui.value = v;
				}else if(item.mod[inputType]==2){
					item.ui={ 
						id:item.ui.id,
						name:item.f_name?'xform.' + item.f_name:item.ui.id
					};
					item.name = 'hidden';
					if(item.f_name && typeof(cfg.recordData[item.f_name])!='undefined')
						item.ui.value = v;
				}else if(item.mod[inputType]==1 || item.mod[inputType]==undefined  || item.mod[inputType]==''){
					if(item.f_name && v!=="")
						item.ui.value = v;
				}else if(item.mod[inputType]==4){
					continue;
				}	
				
				
				var ctrl = appendChildCtrl(opts,item,parentCtrl);
			}
		}
	}
	function appendChildCtrl(opts,item,parentCtrl){
		var ui = item.ui;
		ui.labelWidth = opts.labelWidth;
		if(opts.hideLabels) ui.hideLabel = true;
		if(opts.hideNotes) ui.hideNote = true;
		
		
		var itemTpl = ui.hideLabel?ui.formItemTemplate4HideLabel:ui.formItemTemplate;
		var inputTemplate = ui.inputTemplate;
		if(itemTpl){
			var inputHtml = $.tmpl(inputTemplate,ui);
			itemTpl = itemTpl.replace(/{{inputTemplate}}/g,inputHtml[0].outerHTML);
			var ctrl = $.tmpl(itemTpl, ui).appendTo(parentCtrl);
		}else{
			var ctrl = $.tmpl(inputTemplate, ui).appendTo(parentCtrl);
		}
		//绑定事件		
		if(item.events){
			for(var k in item.events){
				var func = onloadJsFunction[item.events[k]];
				if(typeof func=="function")
					ui[k]= jQuery.proxy(func, $('#' + ui.id))
					//ui[k]= onloadJsFunction[item.events[k]] ;
			}
		}
			
				
				
		//编译
		$('#' + ui.id)[item.name](ui);
		return ctrl;
	}
	
	function getUIType(controlName){
		return controlName;
	}
    $.fn.formRender = function(options, param){
		if (typeof options == 'string'){
			return $.fn.formRender.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'formRender');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'formRender', {
					options: $.extend({}, $.fn.formRender.defaults, options),
					formRender: init(this)
				});
			}
			render(this);
		});
	};
	
	$.fn.formRender.methods = {
		submit: function(jq, options){
			return jq.each(function(){
				//ajaxSubmit(this, $.extend({}, $.fn.form.defaults, options||{}));
			});
		}
	};
	
	$.fn.formRender.defaults = {
		//formContainerTemplate:'<table style="width: 100%;"></table>',
		formItemTemplate:'\
			<tr>\
				<td width="{{= labelWidth}}">{{= fieldLabel}}:</td>\
				<td>\
					{{inputTemplate}}\
					{{if  !hideNote}}<span class="field_note">{{= fieldNote}}</span>{{/if}}\
				</td>\
			</tr>',
		formItemTemplate4HideLabel:'\
			<tr>\
				<td colspan="2">\
					{{inputTemplate}}\
					{{if  !hideNote}}<span class="field_note">{{= fieldNote}}</span>{{/if}}\
				</td>\
			</tr>',
		inputTemplate:'<input id="{{= id}}" type="text" name="{{= name}}"/>',
		controlsConfig:[],
		"labelWidth" : 100,
		"hideLabels" : false,
		"hideNotes" : false
    };
})(jQuery);
</script>


<script type="text/javascript">
var hanler_b,hanler_a;
var onloadJsFunction={};
var formPanel;
RunTime = {
	postUrl:'../runtime/xform!saveData.jhtml',
	hanler_onload:null
}
//表单渲染
RunTime.Render=function(cfg){
	var frm = cfg.config.form;

	if(frm.script && frm.script.s_beforeJs){//保存前脚本
		hanler_b = eval('0,'+frm.script.s_beforeJs);
	}
	if(frm.script && frm.script.s_savedJs){//保存后脚本
		hanler_a = eval('0,'+frm.script.s_savedJs);
	}

	if(frm.script && frm.script.s_afterJs){//保存后脚本
		RunTime.hanler_onload = eval('0,'+frm.script.s_afterJs);
	}

	if(frm.script && frm.script.s_onloadJs){
		try{
			eval('onloadJsFunction={' + frm.script.s_onloadJs + '}');
		}catch(e){
			;
		}
	}

	$('#formTable').formRender({
		controlsConfig:frm.controls,
		"labelWidth" : frm.ui.labelWidth,
		"hideLabels" : frm.ui.hideLabels,
		"hideNotes" : frm.ui.hideNotes
	});	
	return formPanel;
}

RunTime.formPanel= RunTime.Render(formConfig__);

//运行onload脚本
try{
	if(typeof(RunTime.hanler_onload)=='function'){
		RunTime.hanler_onload();
	}
}catch(ex){
	
}

</script>
<script>		

/*******onMessage处理******/
function onMessageHandler(e){
	var dataStr = e.data;
	try{
		var dataJson = JSON.parse(dataStr);
		if(dataJson.options){
			var options = JSON.parse(decodeURIComponent(dataJson.options));
			var hanlder = options.handler;//处理函数
			var scope = options.scope;
			if(hanlder){
				eval('0,' + hanlder + '.call(' + scope + ',"'+ encodeURIComponent(JSON.stringify(dataJson.data)) + '","'+ encodeURIComponent(JSON.stringify(options.data)) +'")');
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
</script>	
${bodyInject!""}	
</body>
</html>	
	