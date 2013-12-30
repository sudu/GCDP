/*
* @description:表单运行时公共函数
* @author:chengds
× @date:2012/06/20
*/
	var saveHandler = function(callback,isContinueAdd){
		var cfg = formConfig__;
		var cb = callback;
		var isContinue = isContinueAdd;
		//var jsHanler_b = hanler_b;
		//var jsHanler_a = hanler_a;
		return function(){
			if(!RunTime.formPanel.form.isValid()){
				Ext.Toast.show('输入未完成或验证不通过',{
				   title:'提示',
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING
				});
				return;
			}
			var ret = true;
			if(typeof(hanler_b)=='function'){//执行保存前脚本
				ret = hanler_b();
			}
			if(ret!=false){
				//记忆历史输入
				MEMHelper.write();
				Ext.getBody().mask("正在提交中...");
				var formValues = RunTime.formPanel.form.getFieldValues(false);
				var params = Ext.apply({formId:cfg.formId,viewId:cfg.viewId,id:cfg.id,nodeId:cfg.nodeId},formValues);
				Ext.Ajax.request({  
					url:formPanel.form.url ? formPanel.form.url:RunTime.postUrl,
					method:"post",
					params:params,
					success:function(response,opts){
						Ext.getBody().unmask();
						var ret={success:false};
						try{
							ret = Ext.util.JSON.decode(response.responseText);
						}catch(ex){
							console.log(ex);
						}
						
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
							Ext.Msg.show({
							   title:'提示',
							   msg:  '提交成功！',
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.INFO 
							});
							
							if(typeof(cb)=='function') cb();//执行回调函数
							if(isContinue){
								RunTime.redirect(0);
							}else{
								RunTime.redirect(ret.Id||formConfig__.id);
							}
							
						}else{
							var _msg = '';
							if(ret){
								var errorStep = ret.errorStep || '';
								if(errorStep == '' || errorStep == '保存前脚本'){
									_msg = "保存出错，请重试！";
								}else{
									_msg = "保存出错！";
								}
								errorStep != '' && ( _msg += "<br>出错步骤："+errorStep );
								_msg += "<br>出错信息："+ret.message.replace(/\r/,'').replace(/\n/,'<br>');
							}
							Ext.Msg.show({
								title:'保存不成功',
								msg:  _msg,
								buttons: Ext.Msg.OK,
								animEl: 'elId',
								minWidth:420,
								icon: Ext.MessageBox.ERROR 
							});		
						}
						
					},
					failure:function(response,opts){
						Ext.getBody().unmask();
						Ext.Msg.show({
						   title:'错误提示',
						   msg: "保存不成功,请重试！",//可能超时
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR  
						});
					}
				});
			}
		};
	}
		
	var createUI  = function(ctrlCfg,parentCtrl){
		var cfg = formConfig__;
		for(var i=0;i<ctrlCfg.length;i++){
			var item = ctrlCfg[i];
			if(item.rtPostback){
				item.ui.rtPostback=item.rtPostback;
				delete item.rtPostback;
			}
			var uiType = getUIType(item.name);
			var commonUI = Designer.controls.ui[uiType];
			if(commonUI){
				Ext.applyDeep(item.ui,commonUI.runtime.ui);
			}
			//清除ui项配置为空字符串项
			for(var key in item.ui){
				if(item.ui[key]===""){
					delete item.ui[key];
				}
			}
			
			eval('var ctrClass = ' + item.name);
			if(!ctrClass){
				Ext.Msg.show({
				   title:'错误提示',
				   msg: "缺少控件库" + item.name + "脚本文件",
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR  
				});
				return;
			}
			
			if(item.controls){
				eval('var obj = new ' + item.name + '(item.ui)');			
				parentCtrl.add(obj);
				createUI(item.controls,obj);
			}else{
				if(item.internalCfg)
					item.ui.internalCfg=item.internalCfg;
				if(item.ui.readOnly){
					item.ui.emptyText="";
					item.ui.allowBlank =true;		
				}				
				if(item.data && item.data.ext_dataSource_value){
					if(item.data.ext_dataSource_type=='sql'){
						var dataSource = formConfig__.dataSource[item.ui.id];
						if(dataSource) item.ui.dataSource = dataSource;
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
						item.ui.dataSource=s;	
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
					item.name = 'Ext.form.Hidden';
					if(item.f_name && typeof(cfg.recordData[item.f_name])!='undefined')
						item.ui.value = v;
				}else if(item.mod[inputType]==1 || item.mod[inputType]==undefined  || item.mod[inputType]==''){
					if(item.f_name && v!=="")
						item.ui.value = v;
				}else if(item.mod[inputType]==4){
					continue;
				}
				eval('obj = new ' + item.name + '(item.ui)');
				parentCtrl.add(obj);
				
				//设置记忆功能
				if(obj && item.mem){
					for(var key in item.mem){
						obj[key] = item.mem[key];
					}
				}
				
				if(obj && item.events){
					for(var k in item.events){
						if(onloadJsFunction[item.events[k]])
							obj.on(k,onloadJsFunction[item.events[k]]) ;
					}
				}
			}
		}
	};
	var getUIType = function(uiName){
		var arr = uiName.split('.');
		if(arr.length>0){
			return arr[arr.length-1];
		}else{
			return '';
		}
	};

/*
* 控件输入记忆功能
*/
var MEMHelper = {
	key:'view-memory-' + formConfig__.nodeId + '-' + formConfig__.formId + '-' + formConfig__.viewId, 
	read:function(){
		var memDataStr = localStorage[this.key];
		if(!memDataStr) return;
		var memData = JSON.parse(memDataStr);
		RunTime.formPanel.items.each(function(ctrl){
			if(ctrl.memory_enable){
				this._initCtrlMemory(ctrl,memData[ctrl.id]);
			}
		},this);
	},
	write:function(){
		var memData = {};
		var memDataStr = localStorage[this.key];
		if(!memDataStr) 
			memData={};
		else
			memData = JSON.parse(memDataStr);
		
		RunTime.formPanel.items.each(function(ctrl){
			if(ctrl.memory_enable){
				var maxCount = ctrl.memory_max_count;
				var oldData = memData[ctrl.id] || [];
				var value = this._getCtrlValue(ctrl);
				var text = this._getCtrlText(ctrl);
				var exsitIndex = -1;
				if(value!=""){
					for(var i=0;i<oldData.length;i++){
						if(oldData[i][0]===value){
							exsitIndex=i;
							break;
						}
					}
				}
				value!="" && text!="" && oldData.push([value,text]);
				if(exsitIndex!=-1){
					oldData.splice(exsitIndex,1);
				}
				while(oldData.length>maxCount){
					oldData.shift();
				}
				memData[ctrl.id] = oldData;
			}
		},this);
		localStorage.setItem(this.key,JSON.stringify(memData));
	},
	_initCtrlMemory:function(ctrl,values){
		if(values && !ctrl.disabled){
			var memEl = ctrl.el.parent().createChild({
				tag:'div',
				cls:'cmpp-view-memory',
				html:'最近输入：'
			});
			for(var i=values.length-1;i>=0;i--){
				var item = values[i];
				var memItem = memEl.createChild({
					tag:'a',
					href:'javascript:void(0)',
					title:'点击输入这个值',
					html:item[1]
				});
				memItem.on('click',function(){
					this.ctrl.setValue(this.value);
				},{ctrl:ctrl,value:item[0]});
			}
			if(ctrl.memory_must_fill && values.length>0 && ctrl.getValue()==""){
				ctrl.setValue(values[values.length-1][0]);
			}
		}

	},
	_getCtrlText:function(ctrl){
		if(typeof ctrl.getText == 'function'){
			return ctrl.getText();
		}
		var xtype = ctrl.constructor.xtype;
		var ret = '';
		switch(xtype){
			case 'combo':
				ret = ctrl.lastSelectionText;
				break;
			case 'datefield':
				ret = ctrl.value;
				break;	
			case 'multiselect':
				ret = ctrl.el.dom.value;
				break;	
			default:
				ret = ctrl.getValue();
		}
		return ret;
	},
	_getCtrlValue:function(ctrl){
		var xtype = ctrl.constructor.xtype;
		var ret = '';
		switch(xtype){
			case 'datefield':
				ret = ctrl.value;
				break;
			default:
				ret = ctrl.getValue();
		}
		return ret;

		return ctrl.getValue();
	}
}
	