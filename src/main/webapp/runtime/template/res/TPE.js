/*********template editor********************/
(function() {
	TPE={};
	(function() {
		var namespaces = [];
		this.idxCache={};
		this.maxIdxNo=0;	
		this.commandJSON=null;//位置和参数指令,xpath为key
		this.ns = function(fn) {
			var ns = {};
			namespaces.push(fn, ns);
			return ns
		};
		this.initialize = function() {
			
			for (var i = 0; i < namespaces.length; i += 2) {
				var fn = namespaces[i];
				var ns = namespaces[i + 1];
				fn.apply(ns)
			}
			TPE.menu.initialize();
			TPE.console.initialize();
			TPE.listBox.initialize();			
			TPE.preEdit.initialize();

		};
		//是否有碎片嵌套
		this.isIdxNesting = function(el){
			
			if(el.select('[cmpp_params^=]').getCount()>0){
				return true;
			}
			//包含内嵌碎片遮罩时返回true
			if(el.select('div.tplidxstyle').getCount()>0 || el.select('div.tplidxstyle2').getCount()>0){
				return true;
			}
			
			if(el.findParentNode('[cmpp_params^=]')){
				return true;
			}
			return false;
		}
	}).apply(TPE);	

	/*********mask***********/
	TPE.ns(function() {
        with(TPE) {
			TPE.mask = {
				bindMaskEvent:function(maskEl,idx){
					maskEl.on('mouseover',this.idxMouseOver,this,{preventDefault:true,stopEvent :false,delay:20,obj:maskEl.dom,idx:idx});
					maskEl.on('mouseout',this.idxMouseOut,this,{preventDefault:true,stopEvent :false,delay:20,obj:maskEl.dom,idx:idx});
					maskEl.on('mousedown',this.idxMouseDown,this,{obj:maskEl.dom});
					maskEl.on('mouseup',this.idxMouseUp,this,{obj:maskEl.dom});
					maskEl.on('click',this.idxClick,this,{
						preventDefault:true,
						stopEvent :false,
						obj:maskEl.dom,
						idxDom:idx.idxDom,
						idx:idx
					});//preventDefault阻止默认事件相应
				},
				unBindMaskEvent:function(maskEl){
					maskEl.un('mouseover',this.idxMouseOver);
					maskEl.un('mouseout',this.idxMouseOut);
					maskEl.un('mousedown',this.idxMouseDown);
					maskEl.un('mouseup',this.idxMouseUp);
					maskEl.un('click',this.idxClick);
				},
				idxMouseOver:function(e,obj,options){
					Ext.fly(options.obj).set({'cls':'tplidxstyleHighlight'});				
				},
				idxMouseOut:function(e,obj,options){
					Ext.fly(options.obj).set({'cls':options.idx.maskClass});
				},
				idxMouseDown:function(e,obj,options){
					Ext.fly(options.obj).set({'cls':'tplidxstyleHighlightMouseDown'});
				},
				idxMouseUp:function(e,obj,options){
					Ext.fly(options.obj).set({'cls':'tplidxstyleHighlight'});
				},
				//配置碎片
				idxClick:function(e,obj,options){
					TPE.menu.config(options);
				}
			}
		}
	});
	
	/*********idx***********/
	TPE.ns(function() {
        with(TPE) {
			TPE.hiddenIdxMaskTop=340;//隐藏碎片的位置
            TPE.idx = function(opt) {
				this.opt = opt;
				this.idxNo = opt.idxNo;
				this.action = opt.action;//delete,new,edit
				this.idxDom= opt.el.dom;		
				this.maskDom = null;
				this.vtype = false;//是否是内嵌碎片
				this.state = 1;//碎片状态 1:有碎片标记，但未配置 2：已配置
				this.init(opt.params);
            };
            TPE.idx.prototype = {
				init:function(params){
					var el = this.getEl();
					this.key = this.getXPath();
					if(params){ //新建
						el.set({
							'cmpp_params':params
						});
					}
					var paramsStr = params?params:el.getAttributeNS('','cmpp_params');
					if(paramsStr){
						this.state = 2;	
						this.setParams(paramsStr);	
					}else{
						var cmppData =  el.getAttributeNS('','cmpp');
						if(cmppData){
							this.setParams(cmppData);	
							this.state = 1;
							el.set({
								'cmpp_params':cmppData
							});
							this.idxDom.removeAttribute('cmpp');
						}
					}
				},
				setParams:function(params){
					if(typeof(params)=='string')
						try{
							this.params = Ext.util.JSON.decode(params);
						}catch(ex){
							this.params = {};	
						}
					else if(typeof(params)=='object')
						this.params = params;
					this.vtype = this.params.vtype;
					this.id = this.params.id;
				},
				getMaskClass:function(){
					if(this.state==1){
						return "tplidxstyle";
					}else if(this.state==2){
						return "tplidxstyle2";
					}
				},
				renderMask:function(){
					var bdy = Ext.getBody();
					var maskEl=null;
					this.maskClass = this.getMaskClass();
					this.maskTpl = new Ext.Template(
						'<div class="'+this.maskClass+'" id="tplidx_{id}" style="width:{width}px;height:{height}px;top:{top}px;left:{left}px;position: absolute;"><span class="maskTitle" style="width:{width}px;" title="{info}">{info}</span></div>'
					)
					if(TPE.idxCache[this.key] && TPE.idxCache[this.key].maskDom){
						maskEl = Ext.fly(TPE.idxCache[this.key].maskDom);
					}
					if(this.vtype==2 || this.vtype=='2') {//内嵌碎片
						if(!maskEl){
							var idxInnerhtml = 	this.idxDom.innerHTML;
							this.idxDom.innerHTML = '';
							var maskEl = this.getEl().createChild({
								'cls':this.maskClass,
								'style':'position:relative;',
								'title':this.getMaskInfo().title,
								html:idxInnerhtml
							});
							TPE.mask.bindMaskEvent(maskEl,this);
						}
					}else{	
						var box = getElBox(this.getEl());
						if(box.height==0 || box.height==0){
							box={
								left:1,
								top:TPE.hiddenIdxMaskTop,
								width:128,
								height:18
							};
							TPE.hiddenIdxMaskTop +=20;
						}
						
						if(maskEl){
							Ext.fly(maskEl).setLeftTop(box.left,box.top);
						}else{
							var maskData = {left:box.left,top:box.top,width:box.width,height:box.height,info:this.getMaskInfo().title.replace(/"/g,"'"),id:'mask_' + this.idxNo};
							var el = this.getEl();
							if(el.getStyle('display')=='none'){
								maskEl = Ext.fly(this.maskTpl.append(bdy,maskData));
								maskEl.setStyle({'position':'absolute'});
							}else{
								maskEl = this.maskTpl.insertAfter(el,maskData,true);
								var p = el.parent();
								if(p.dom.tagName=='CMPP_BANNER') p = p.parent();
								p.setStyle("position",'relative');
								
								//maskEl.alignTo(el,'tl');							
							}
						}
						TPE.mask.bindMaskEvent(maskEl,this);
					}
					this.maskDom = maskEl.dom;
					TPE.idxCache[this.key] =this;
					
					TPE.listBox.add(this);//维护碎片列表
				},
				getMaskInfo:function(){
					var json = {title:'未配置'};
					if(this.params && this.params.title){
						json = {title:this.params.title}
					}
					return json;
				},
				destroy:function(){
					var maskEl = this.getMaskEl();
					TPE.mask.unBindMaskEvent(maskEl);
					if(this.vtype==2 || this.vtype=='2') {
						var html = this.maskDom.innerHTML;
						maskEl.remove();
						this.getEl().update(html);
					}else{
						maskEl.remove();
					}
					this.maskDom = null;	
				},
				//更新
				update:function(params){
					this.setParams(params);
					this.getEl().set({
						'cmpp_params':"{title:'" + params.title + "'}"
					});
					this.setMaskInfo();
					if(this.action!='new') this.action = 'edit';
					this.updateState(2);
				},
				//取消碎片
				cancel:function(){
					TPE.listBox.remove(this);//维护碎片列表
					this.destroy();
					this.idxDom.removeAttribute('cmpp');
					this.idxDom.removeAttribute('cmpp_params');
					if(this.action=='new'){//取消新建的碎片
						TPE.idxCache[this.key]=null;
					}else{//删除现有的碎片
						this.action='delete';
						this.getEl().set({
							'cmpp_action':'cancel'
						});
						TPE.idxCache[this.key]  = this;
					}
				},
				//恢复被删掉的现有碎片
				restore:function(paramsStr){
					this.setParams(paramsStr);
					this.state=2;	
					this.renderMask();
					this.idxDom.removeAttribute('cmpp_action');
					this.getEl().set({
						'cmpp_params':paramsStr
					});
					this.action = 'edit';		
									
				},
				setMaskInfo:function(){
					if(!this.maskDom){//浮层已删除
						this.renderMask();
					}
					var maskEl = this.getMaskEl();	
					var  maskTitle = maskEl.select('.maskTitle');
					if(maskTitle.getCount()>0){
						maskTitle.item(0).update(this.getMaskInfo().title);
					}else{
						maskEl.set({
							title:this.getMaskInfo().title.replace(/"/g,"'"),
						});
					}
				},
				//更新遮罩的状态，颜色和state值
				updateState:function(state){
					if(this.state!=state){
						this.state = state;
						if(this.state==1){
							this.maskClass = "tplidxstyle";
						}else{
							this.maskClass = "tplidxstyle2";
						}
						this.getMaskEl().set({
							cls:this.maskClass
						});
					}
				},
				getEl:function(){
					return Ext.fly(this.idxDom);
				},
				getMaskEl:function(){
					return Ext.fly(this.maskDom);
				},
				getXPath:function(){
					var idxEl = Ext.fly(this.idxDom);
					var cmppid = idxEl.getAttributeNS('','cmppid');
					return cmppid;
					//return FBL.getElementXPath2(this.idxDom);
				}
				
				
			}
			function getElBox(el){
				if(el){
					var width= el.getWidth();
					var height = el.getHeight(); 
					var p = el.parent();
					if(p.dom.tagName=='CMPP_BANNER') p = p.parent();
					var xy = el.getOffsetsTo(p);
					//特殊处理
					if(width==0||height==0){
						var innerHTML  = el.dom.innerHTML;
						el.dom.innerHTML = '';
						var el = el.createChild({
							tag:'div',
							style:'position: relative;float:left',
							html:innerHTML
						});
					}
					if(el) return {width:el.getWidth(),height:el.getHeight(),left:xy[0],top:xy[1]};
					else return {width:0,height:0,left:0,top:0}; //todo 处理切碎片不符合规范的情况
				}else{
					return {width:0,height:0,left:0,top:0};
				}
			}
		}
	});
	
/*********menu***********/
	TPE.ns(function() {
		 with(TPE) {
			TPE.menu = {
				editingIdx:null,
				idxTypeWin:null,
				idxTypes:{},
				win:null,
				idxTypesArr:[],
				initialize:function(){
					Ext.Ajax.request({  
						url:'templatectr!idxTypeList.jhtml?nodeId=' + nodeId__,
						method:'get',	
						success:function(response,opts){
							try{
								var ret = Ext.util.JSON.decode(response.responseText);
								for(var i=0;i<ret.length;i++){
									var item = ret[i];
									TPE.menu.idxTypesArr.push([item.formId,item.title]);
									TPE.menu.idxTypes['type_' + item.formId] = item.configUrl;
								}
							}catch(ex){
								Ext.Msg.show({
								   title:'错误提示',
								   msg: '获取碎片类型出错！',
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.ERROR 
								});
							}

						},
						failure:function(ret,opts){
							Ext.Msg.show({
								title:'错误提示',
								msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
								buttons: Ext.Msg.OK,
								animEl: 'elId',
								minWidth:420,
								icon: Ext.MessageBox.ERROR 
							});
						}
					});	
				},				
				config:function(options){
					var key = Ext.fly(options.idxDom).getAttributeNS('','cmppid');
					var idx = TPE.idxCache[key];
					if(idx){
						if(idx.state==2){
							this.createConfigWin({
								idx:idx,
								idxDom:options.idxDom
							});
						}else{
							this.initIdxTypeWin(options);
						}					
					}else if(options.action=="userCreate"){
						this.initIdxTypeWin(options);
					}else{//xpath出现错乱，无法与碎片缓存里的xpath对应，发警告
						Ext.Msg.show({
							title:'出错啦',
							msg: '页面里含有动态创建的根节点，导致xpath错乱,操作无法继续!<br/>请清除动态创建根节点的脚本此类脚本通常会是广告脚本、监测脚本等.',
							buttons: Ext.Msg.OK,
							animEl: 'elId',
							minWidth:420,
							icon: Ext.MessageBox.ERROR 
						});
					}
					
				},
				initIdxTypeWin:function(options){
					var idx = options.idx;
					
					var win = new Ext.Window({
						title:'请选择碎片类型',
						layout:'fit',
						width:400,
						height:300,
						autoScroll:true,
						buttonAlign:'center',
						items:[{
							xtype:'panel',
							frame:false,
							layout:'fit',
							items:[{
								xtype:'radiogroup',
								style:'padding:50px;',
								fieldLabel:'碎片类型',
								name:'idxType',
								id:'rdIdxTypes',
								value:TPE.menu.idxTypesArr.length>0?TPE.menu.idxTypesArr[0][0]:0,
								data:TPE.menu.idxTypesArr
							}]
						}],
						buttons:[{
							text:'下一步',
							scope:{idx:idx,idxDom:options.idxDom},
							handler:function(obj,e){
								var formId = obj.ownerCt.findById('rdIdxTypes').getValue();
								formId = parseInt(formId);
								TPE.menu.createConfigWin({
									idx:this.idx,
									idxDom:this.idxDom,
									formId:formId
									
								});
								obj.ownerCt.close();
							}
							
						},{
							text:'取消',
							handler:function(){
								this.ownerCt.close();
							}
						}]
					});
					win.show();
					if(options.senderDom){
						win.anchorTo(Ext.fly(options.senderDom));
					}else{
						win.center();
					}
					win.setZIndex(2147483501);
					
				},
				createConfigWin:function(options){
					var params=null;
					var idx = options.idx;
					var id=0;
					var formId;
					
					if(idx){
						params = idx.params
						id = idx.id;
						if(params && params.formId) formId = params.formId;
					}	
					if(options.formId){
						formId = options.formId;
					}else if(params && params.formId) {
						formId = params.formId;
					}else{
						formId=53;//默认为静态碎片
					}
					if(params && formId!=params.formId){//碎片类型变更
						id=0;
					}
					options.formId = formId;
					
					var configUrl = TPE.menu.idxTypes['type_' + formId];
					if(!configUrl){
						Ext.CMPP.warn("碎片类型配置出错","没有找到formId为"+ formId +"碎片表单对应碎片设计时视图。<br>请到碎片类型列表中设置。");
						return;
					}
					configUrl = configUrl.replace(/{id}/g,id);
				
					if(TPE.menu.win) TPE.menu.win.close();
					var win = new Ext.Window({
						id:'configWin',
						title:'请填写碎片配置',
						layout:'border',
						width:650,
						height:600,
						maximizable:true,
						buttonAlign:'center',
						items:[{
							xtype:'panel',
							region:'north',
							autoHeight:true,
							border:false,
							frame:false,
							style:'padding:5px',
							bodyStyle:'padding:5px',
							itemCls:"itemStyle",
							layout:'form',
							items:[{
								xtype:'textfield',
								fieldLabel:'碎片标签',
								width:200,
								id:'txtIdxLabel',
								value:params && params.title ? params.title:''
							},{
								xtype:'radiogroup',
								fieldLabel:'遮罩模式',
								name:'vtype',
								id:'rdIdxVtype',
								value:params && params.vtype ? params.vtype:1,
								data:[['1','漂浮'],['2','内嵌']]
							}]
						},{
							xtype:'panel',
							region:'center',
							layout:'fit',
							items:[{
								xtype:'panel',
								id:'iframePanel',
								html:'<div style="width:100%;height:100%;background:url(../res/img/loading.gif) no-repeat center center;"><iframe id="fr_configView"  scrolling="no" frameborder="0" width="100%" height="100%" style="visibility:hidden"></iframe></div>' 
							}]
						}],
						bbar:[{
							xtype:'button',
							text:'更换碎片类型',
							scope:{options:options},
							handler:function(obj,e){
								TPE.menu.initIdxTypeWin(this.options);
								TPE.menu.win.close();	
							}
						}],
						buttons:[{
							text:'确定',
							scope:{options:options},
							handler:TPE.menu.handlerOK
							
						},{
							text:'删除',
							width:69,
							style:(idx&&idx.action!='delete') ? 'visibility:visible':'visibility:hidden',
							scope:{options:options},
							handler:function(obj,e){
								if(!this.options.idx) return;
								Ext.Msg.confirm('提示', '您确定要取消该碎片吗？', function(btn, text){
									if (btn == 'yes'){
										this.idx.cancel();
										this.obj.ownerCt.close();
									}
								},{idx:this.options.idx,obj:obj});									
							}
						},{
							text:'取消',
							handler:function(){
								this.ownerCt.close();
							}
						}]
					});
					TPE.menu.win = win;
					win.show();
					win.center();
					win.setZIndex(2147483501);
					
					var iframe = document.getElementById('fr_configView');
					var msgJson = {
						action:'init',
						params:params
					};
					iframe.onload = function(){
						Ext.fly(this).fadeIn({
							endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeIn',
							duration: .5,
							useDisplay: false
						});
						this.contentWindow.postMessage(Ext.util.JSON.encode(msgJson), '*')
					};
					iframe.src = configUrl + '&_t='+ (new Date()).valueOf();
				},
				handlerOK:function(obj,e){
					var idx = this.options.idx;
					var idxDom = this.options.idxDom;
					var idxLabel = obj.ownerCt.findById('txtIdxLabel').getValue();
					var vtype = obj.ownerCt.findById('rdIdxVtype').getValue();
					var idxType = this.options.idxType;
					if(!idx){//new 
						idx = TPE.preEdit.createIdx({
							el:Ext.fly(idxDom),
							action:'new',
							params:{
								title:idxLabel,
								type:idxType,
								vtype:vtype
							}
						});
					}else{
						idx.params.title = idxLabel;
						idx.params.type=idxType;
						idx.params.vtype = vtype;
					}
					TPE.menu.editingIdx = idx;	//正在编辑的碎片
					
					//向编辑视图发搜集配置信息的请求信息
					var msgJson={
						action:'submit'
					};
					document.getElementById('fr_configView').contentWindow.postMessage(Ext.util.JSON.encode(msgJson), '*');

				}
			};
		}
	});
	
	
/*********message***********/
	TPE.ns(function() {
		 with(TPE) {
			TPE.message = {
				onMessage:function(e){
					var msg = e.data;
					msg = Ext.util.JSON.decode(msg);
					if(msg.data) {
						Ext.applyDeep(TPE.menu.editingIdx.params,msg.data);
					}
					TPE.menu.editingIdx.update(TPE.menu.editingIdx.params);
					TPE.listBox.updateTitle(TPE.menu.editingIdx);
					
					FBL.Inspector.clear();	//清除Inspector
					TPE.menu.win.close();
				}
			}
		}
	});
/*********console***********/
	TPE.ns(function() {
		with(TPE) {
			TPE.console = {
				consoleWin:null,
				initialize:function(){
					this.createConsole();
				},
				bindConsoleEvent:function(){				
					consoleBox = Ext.get('fbConsoleList');
					consoleBox.on("mouseover", TPE.console.onListMouseMove);
					consoleBox.on("mouseout", TPE.console.onListMouseOut);
					consoleBox.on("click", TPE.console.onListMouseClick);
				},
				createConsole:function(){
					var win = new Ext.Window({
						title:'处理结果公告栏',
						id:'fbconsoleContainer',
						layout:'fit',
						width:300,
						height:150,
						style:'position:fixed;',
						buttonAlign:'center',
						closeAction :'hide',
						autoScroll :true,
						draggable :false,
						resizable :false 
					});	
					win.setPosition(Ext.getBody().getWidth()-win.width-1,27);
					this.consoleWin	 = win;
				},
				//提交结果处理
				submitResponsehandler:function(actionData){
					var output='<ul class="TPE_consoleUl" id="fbConsoleList">';
					if(actionData.length==0){
						output+='<li>没什么需要处理的</li>';
					}else{
						for(var i=0;i<actionData.length;i++){
							var ret = actionData[i];
							//var xpath = ret.xpath;
							var cmppid = ret.cmppid;
							var id = ret.id;
							var idx = TPE.idxCache[cmppid];
							if(ret.success){

								if(idx.action=='delete'){
									idx.idxDom.removeAttribute('cmpp_action');
									TPE.idxCache[cmppid]=null;
									output+='<li><span>【删除成功】</span>碎片:'+idx.params.title +'</li>';
								}else{						
									idx.id = id;
									output+='<li><span>【成功】</span>碎片:<a title="'+ cmppid +'">'+ idx.params.title +'</a></li>';
									idx.action = null;
								}

							}else{
								output+='<li><span style="color:red">【失败】</span>碎片:<a title="'+ cmppid +'">'+ idx.params.title +' </a>'+ ret.message +'</li>';
							}
						}
					}
					output +='</ul>';
					TPE.console.consoleWin.show();
					TPE.console.consoleWin.body.update(output);
					TPE.console.bindConsoleEvent();
				},
				onListMouseMove:function(e,obj,options){
					var targ = e.target;
					var nodeName = targ.nodeName.toLowerCase();
					if(nodeName!='a'){
						return;
					}
					var xpath = targ.title;
					var idx = TPE.idxCache[xpath];
					if(idx){
						//idx.getMaskEl().set({'cls':'tplidxstyle3'})
						idx.maskDom.className = 'tplidxstyle3';
					}
				},
				onListMouseOut:function(e,obj,options){
					var targ = e.target;
					var nodeName = targ.nodeName.toLowerCase();
					if(nodeName!='a'){
						return;
					}
					var xpath = targ.title;
					var idx = TPE.idxCache[xpath];
					if(idx) {
						//idx.getMaskEl().set({'cls':'tplidxstyle2'})
						idx.maskDom.className = 'tplidxstyle2';
					}
				},
				onListMouseClick:function(e,obj,options){
					var targ = e.target;
					var nodeName = targ.nodeName.toLowerCase();
					if(nodeName!='a'){
						return;
					}
					var xpath = targ.title;
					var idx = TPE.idxCache[xpath];
					
					if(idx){
						document.documentElement.scrollTop = idx.getMaskEl().getTop()-50;
					}
				}
			}
		}
	});		 

	/*********idxListBox***********/
	TPE.ns(function() {
		with(TPE) {
			TPE.listBox = {
				idxListBox:null,
				initialize:function(){
					var idxListPanel = new Ext.Panel({
						title: '碎片列表',
						titleCollapse:true,
						collapsible:true,
						collapsed:true,
						height:340,
						autoScroll :true,
						id:'idxListContainer',
						style:'position:fixed;top:0px;left:0px;z-index:2147483552',
						width:200,
						html:'<ul id="fbIdxListBox" title="点击碎片名称可进行碎片配置"></ul>'
					});
					idxListPanel.render(Ext.getBody());
					
					this.idxListBox = Ext.get('fbIdxListBox');
				},
				set:function(){
					var idxs = TPE.idxCache;
					for(var xpath in idxs){
						var idx = idxs[xpath];
						this.add(idx);
					}
				},
				add:function(idx){
					if(idx){
						var liEl = TPE.listBox.idxListBox.createChild({
							tag:'li',
							id:'li_' + idx.getXPath()
						});
						var hiddenText = Ext.fly(idx.idxDom).getStyle("display")=="none"?'(隐藏)':''
						var titleEl = liEl.createChild({
							tag:'span',
							html:idx.params.title?idx.params.title+hiddenText:'[未配置的碎片]'
						});
						var aEl = liEl.createChild({
							tag:'div',
							title:'点击按钮进行配置',
							cls:'icon-tools',
							html:''
						});
						TPE.listBox.bindEvent(aEl,titleEl,idx);
						TPE.listBox.idxListBox.appendChild(liEl);
					}
				},
				updateTitle:function(idx){
					var liEl = Ext.get('li_' + idx.getXPath());
					liEl.child('span').update(idx.params.title);
				},
				remove:function(idx){
					var liEl = Ext.get('li_' + idx.getXPath());
					if(liEl) liEl.remove();
					
				},
				bindEvent:function(aEl,titleEl,idx){
					titleEl.on('mouseover',function(e,obj,options){
						options.obj.className = 'tplidxstyle3';
					},this,{preventDefault:true,stopEvent :false,delay:20,obj:idx.maskDom});
					titleEl.on('mouseout',function(e,obj,options){
						options.obj.className = 'tplidxstyle2';
					},this,{preventDefault:true,stopEvent :false,delay:20,obj:idx.maskDom});
					titleEl.on('click',function(e,obj,options){
						//定位
						document.documentElement.scrollTop = options.idx.getMaskEl().getTop()-70;
					},TPE.mask,{
						preventDefault:true,
						stopEvent :false,
						idx:idx
					});
					
					aEl.on('click',function(e,obj,options){
						//定位
						document.documentElement.scrollTop = options.idx.getMaskEl().getTop()-70;
						TPE.mask.idxClick(e,obj,options);
					},TPE.mask,{
						preventDefault:true,
						stopEvent :false,
						obj:idx.maskDom,
						idxDom:idx.idxDom,
						idx:idx
					});
				}
			};
		}
	});
	
	/*********preEdit***********/
	TPE.ns(function() {
		 with(TPE) {
			TPE.preEdit = {
				initialize:function(){
					var idxEls1 = Ext.select('[cmpp]');
					var idxEls2 = Ext.select('[cmpp_params]');
					
					this.initIdx(idxEls1);
					this.initIdx(idxEls2);
					
				},
				initIdx:function(idxEls){
					for(var i=0;i<idxEls.getCount();i++){
						var el = idxEls.item(i);
						if(el.findParentNode('[cmpp]') || el.findParentNode('[cmpp_params]')){
							//父节点已存在碎片标记，则移除其碎片标记
							el.dom.removeAttribute('cmpp');
							el.dom.removeAttribute('cmpp_params');
							//console.log('该节点的父节点已存在碎片标记');
						}else{
							this.createIdx({
								el:el
							});
						}
					}
					this.bindSubmitButton();
				},
				bindSubmitButton:function(){
					var btnSubmit = Ext.get('fbToolbar_btSubmit').dom;
					btnSubmit.onclick = this.submitHandler;
					
				},
				submitHandler:function(){
					var commandArr = TPE.preEdit.outputCommand();
					if(commandArr.length==0){
						TPE.console.consoleWin.show();
						TPE.console.consoleWin.body.update('<ul class="TPE_consoleUl" id="fbConsoleList"><li>没什么需要处理的</li></ul>');
					}else{
						submitCommands(Ext.util.JSON.encode(commandArr));
					}
				},
				createIdx:function(options){
					var idx = new TPE.idx({
						el:options.el,
						action:options.action,
						params:options.params,
						vtype : options.vtype,
						idxNo:TPE.maxIdxNo++
					});
					idx.renderMask();
					return idx;
				},
				//输出指令
				outputCommand:function(e,obj,options){ 
					var commandArr =[];
					var idxs = TPE.idxCache;
					for(var key in idxs){
						var idx = idxs[key];
						if(key && idx ){
							var params = idx.params;
							var action = idx.action;
							if(!params){
								params = {};
							}
							if(action){
								var cmd = {
									//"xpath":key,
									"cmppid":key,
									"formId":params.formId,
									"id":params.id,
									"title" : params.title,
									"params" :params.params,
									"action":action
								};							
								if(params.vtype){
									cmd.vtype = params.vtype;	
								}
								commandArr.push(cmd);
							}
						}
					}
					return commandArr;
				}
			}
		 }
	});

})();