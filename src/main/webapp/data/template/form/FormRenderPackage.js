/*
* @description:表单运行时公共函数
* @author:chengds
× @date:2014/02/11
*/
var saveHandler = function(callback,isContinueAdd){
	var cfg = formConfig__;
	var cb = callback;
	var isContinue = isContinueAdd;
	return function(){
		var form = RunTime.formPanel;
		if(!$("#form1").form("validate")){
			$.messager.alert('提示',"输入未完成或验证不通过",'error');
			return;
		}

		var formValues = form.formRender("getFieldValues");
		var params = $.extend({formId:cfg.formId,viewId:cfg.viewId,id:cfg.id,nodeId:cfg.nodeId},formValues);
		var ret = true;
		if(typeof(hanler_b)=='function'){//执行保存前脚本
			ret = hanler_b.call(params);//保存前脚本可修改post的数据
		}
		if(ret!=false){		
			//Ext.getBody().mask("正在提交中...");
			$.messager.progress({ 
				title: '请等待', 
				msg: '正在提交...', 
				//text: 'PROCESSING.......' 
			});
			$.post(RunTime.postUrl,params,function(data){
				$.messager.progress("close");
				var ret = data;
				var afterSaveJsReturn = true;
				if(typeof(hanler_a)=='function'){//执行保存后脚本
					var options={
						id:ret.Id||formConfig__.id,
						ret:ret,
						cb:cb,
						isContinue:isContinue
					};
					afterSaveJsReturn = hanler_a(options,function(opts_){
						var opts = opts_;
						return function(){
							if(typeof(opts.cb)=='function') opts.cb();//执行回调函数
							if(opts.isContinue){
								RunTime.redirect(0);
							}else{
								RunTime.redirect(opts.id);
							}
						}
					}(options));
				}
				if(afterSaveJsReturn==false) return;//如果保存后脚本返回false，则终止后面的提示性处理
				if(ret.success){		
					$.messager.show({
						title:'提示',
						msg:'提交成功！',
						timeout:2000,
						showType:'fade',
						style:{
							right:'',
							top:document.body.scrollTop+document.documentElement.scrollTop,
							bottom:''
						}
					}).delay(2000).queue(function(){
						if(typeof(cb)=='function') {
							cb();//执行回调函数
							return;
						}
						if(isContinue){
							RunTime.redirect(0);
						}else{
							RunTime.redirect(ret.Id||formConfig__.id);
						}
					});

				}else{
					var _msg = '';
					if(ret){
						var errorStep = ret.errorStep || '';
						if(errorStep == '' || errorStep == '保存前脚本'){
							_msg = "保存不成功！";
						}else{
							_msg = "保存出错！";
						}
						errorStep != '' && ( _msg += "<br>出错步骤："+errorStep );
						_msg += "<br>出错信息："+ret.message.replace(/\r/,'').replace(/\n/,'<br>');
					}
					$.messager.alert('保存不成功',_msg,'error');						
				}
					
			}, "json");
		}
	};
};

function previewHandler(){
	var form = RunTime.formPanel;
	var cfg = formConfig__;
	if(!$("#form1").form("validate")){
		$.messager.alert('提示',"输入未完成或验证不通过",'error');
		return;
	}

	var formValues = form.formRender("getFieldValues");
	var params = $.extend({formId:cfg.formId,viewId:cfg.viewId,id:cfg.id,nodeId:cfg.nodeId},formValues);
	var ret = true;
	if(typeof(hanler_b)=='function'){//执行保存前脚本
		ret = hanler_b.call(params);//保存前脚本可修改post的数据
	}
	if(ret!=false){		
		//Ext.getBody().mask("正在提交中...");
		$.messager.progress({ 
			title: '请等待', 
			msg: '正在提交预览...'
		});
		$.post("../runtime/template!preview.jhtml",params,function(response){
			$.messager.progress("close");
			var previewWin =window.open();
			previewWin.document.write(response);	
		}, "text");
	}
}

function closePage(){

	//优先判断是否该页面是否包含在弹出的window里，若是则关闭该window
	try{
		var parentMsg = $.parseQuery().postMessage__;
		if(parentMsg){
			var parentMsgJson = JSON.parse(decodeURIComponent(parentMsg));
			if(parentMsgJson.containerId){
				parentMsgJson.handler = '(function(receiveData,realData){\
					if(typeof receiveData==="string"){\
						receiveData = JSON.parse(decodeURIComponent(receiveData));\
					}\
					eval("var win =" + receiveData.containerId);\
					if(typeof win==="string") win = $("#" + win);\
					win.fadeOut("fast",function(){\
					   $(this).hide();\
					});\
				})';
				
				var postData = {
					data:{containerId:parentMsgJson.containerId},
					options: encodeURIComponent(JSON.stringify(parentMsgJson)) //从URL接收到的数据
				};
				var sender;
				if(parentMsgJson.sender){
					eval('0,' + 'sender = ' + decodeURIComponent(parentMsgJson.sender));
				}else if(!window.opener && parentMsgJson.hostFrameId){//Ext的tabPanel页签之间的通讯
					sender = window.parent.$("#" + parentMsgJson.hostFrameId)[0].contentWindow;
				}else{
					sender = window.opener;
				}
				sender && sender.postMessage(JSON.stringify(postData), '*'); 
				return ;
			}
		}
		
	}catch(ex){}

	if(top.centerTabPanel){
		if(conflictMgr && formConfig__.id>0)conflictMgr.remove();//移除正在编辑状态
		var tab = top.centerTabPanel.getActiveTab();
		top.centerTabPanel.remove(tab);
	}else{
		top.open('','_self','');
		top.close();
	}
}

(function ($) {
	var controlItems=[];
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
		controlItems.push({
			id:ui.id,
			name:ui.name,
			ctrlName:item.name,
			ctrl:$('#' + ui.id)
		});
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
		getFieldValues: function(jq, options){
			var data={};
			for(var i=0;i<controlItems.length;i++){
				var item = controlItems[i];
				var value = item.ctrl[item.ctrlName]("getValue");
				data[item.name] = value;
			}
			return data;
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

