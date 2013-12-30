var idxMaskRender = {
	hasInitListBox:false,//是否已经初始化了碎片列表
	idxEls:[],
	idxInfo:[],//{idxDom:xx,maskDom:xx,html:xx}
	tpl:new Ext.Template(
		'<div class="tplidxstyle" id="tplidx_{id}" style="width:{width}px;height:{height}px;top:{top}px;left:{left}px;position:absolute"><span class="maskTitle" style="width:{width}px;" title="{info}">{info}</span></div>'
	),	
	tpl_delete:new Ext.Template(
		'<div class="tplidxstyle" id="tplidx_{id}" style="width:{width}px;height:{height}px;"><span class="maskTitle" style="width:{width}px;" title="{info}">{info}</span></div>'
	),
	init:function(){
		idxMaskRender.listBox.init();
		var idxEls = this.getIdxEls();
		this.idxEls = idxEls;
		this.renderMask(idxEls);
		//Ext.fly(window).on('resize',this.renderMask,this);
		this.hasInitListBox = true;
		
	},
	renderMask:function(){
		var idxEls =  this.idxEls;
		var bdy = Ext.getBody();
		idxMaskRender.hiddenIdxMaskTop=340;//隐藏碎片的位置
		for(var i=0;i<idxEls.getCount();i++){
			var el = idxEls.item(i);
			var editdata =  el.getAttributeNS('','editdata');
			editdata = Ext.util.JSON.decode(editdata);
			var idxTitle = editdata.title ? editdata.title : "";
			if(el.getStyle('display')=='none'){
				idxTitle+='(隐藏)';
				editdata.title = idxTitle;
			}
			
			var idxInnerhtml = 	el.dom.innerHTML;
			var maskEl=null;
			//if(el.getStyle('display')!='none'){
				var isInner = editdata.type==2;
				maskEl=null;
				if(this.idxInfo[i]){
					maskEl = this.idxInfo[i].maskDom;
				}
				if(isInner) {//内嵌碎片
					if(!maskEl){
						var newObj = document.createElement('div');
						maskEl = Ext.fly(newObj);
						maskEl.set({
							'cls':'tplidxstyle2',
							'id':'tplidx_' + i,
							'title':idxTitle,
							'style':'position:relative;'
						});
						el.dom.innerHTML='';
						maskEl.update(idxInnerhtml);
						maskEl.appendTo(el);
					}
				}else{	
					var box = this.getElBox(el);
					if(box.height==0 || box.height==0){
						box={
							left:1,
							top:idxMaskRender.hiddenIdxMaskTop,
							width:128,
							height:18
						};
						idxMaskRender.hiddenIdxMaskTop +=20;
					}
					if(maskEl){
						Ext.fly(maskEl).setLeftTop(box.left,box.top);
						//Ext.fly(maskEl).alignTo(el,'tl');
					}else{
						var maskData = {left:box.left,top:box.top,width:box.width,height:box.height,id:i,info:idxTitle};
						
						if(el.getStyle('display')=='none'){
							maskEl = Ext.fly(this.tpl.append(bdy,maskData));
							maskEl.setStyle({'position':'absolute'});
						}else{
							maskEl = this.tpl.insertAfter(el,maskData,true);
							el.parent().setStyle("position",'relative');
							//maskEl.alignTo(el,'tl');							
						}
					}

				}
				if(!idxMaskRender.hasInitListBox)idxMaskRender.listBox.add(maskEl.dom,i,editdata);
			/*}else{
				if(!idxMaskRender.hasInitListBox)idxMaskRender.listBox.add(null,i,editdata);
			}*/
			if(!this.idxInfo[i]){
				if(maskEl)this.bindMaskEvent(maskEl,i,editdata);
				this.idxInfo[i] = {
					editdata:editdata,
					idxDom:el.dom,
					maskDom:maskEl?maskEl.dom:null,
					html:idxInnerhtml
				};	
			}
		}
	},
	edit:function(e,obj,options){
		if(this.idxInfo[options.idxNo].isPreview==true) {
			e.stopEvent();	
			return;
		}
		var idxNo = options.idxNo;
		var idxId = options.idxId;
		idxEditor.init(options.editUrl,idxNo,idxId);
	},
	bindMaskEvent:function(maskEl,i,editdata){
		maskEl.on('mouseover',this.idxMouseOver,this,{preventDefault:true,stopEvent :false,delay:20,idxNo:i,obj:maskEl.dom});
		maskEl.on('mouseout',this.idxMouseOut,this,{preventDefault:true,stopEvent :false,delay:20,idxNo:i,obj:maskEl.dom});
		maskEl.on('mousedown',this.idxMouseDown,this,{idxNo:i,obj:maskEl.dom});
		maskEl.on('mouseup',this.idxMouseUp,this,{idxNo:i,obj:maskEl.dom});
		maskEl.on('click',this.edit,this,{
			editUrl:editdata.editurl,
			idxNo:i,
			idxId:editdata.id,
			preventDefault:true,
			stopEvent :false,
			obj:maskEl.dom
		});//preventDefault阻止默认事件相应
	},
	getElBox:function(el){
		if(el){
			var width= el.getWidth();
			var height = el.getHeight(); 
			var xy = el.getOffsetsTo(el.parent())
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
	},
	idxMouseOver:function(e,obj,options){
		if(this.idxInfo[options.idxNo].isPreview==true) return;
		Ext.fly(options.obj).set({'cls':'tplidxstyleHighlight'});
	},
	idxMouseOut:function(e,obj,options){
		var editdata = this.idxInfo[options.idxNo].editdata;
		if(editdata.isPreview==true) return;
		Ext.fly(options.obj).set({'cls':(editdata.type==2?'tplidxstyle2':'tplidxstyle')});
	},
	idxMouseDown:function(e,obj,options){
		if(this.idxInfo[options.idxNo].isPreview==true) return;
		Ext.fly(options.obj).set({'cls':'tplidxstyleHighlightMouseDown'});
	},
	idxMouseUp:function(e,obj,options){
		if(this.idxInfo[options.idxNo].isPreview==true) return;
		Ext.fly(options.obj).set({'cls':'tplidxstyleHighlight'});
	},
	getIdxEls:function () {
		var idxEls2 = Ext.select('*[editdata^={]');
		return idxEls2;
	}
	
}
/*********idxListBox***********/
idxMaskRender.listBox = {
	idxListBox:null,
	init:function(){
		var idxListPanel = new Ext.Panel({
			title: '碎片列表',
			titleCollapse:true,
			collapsible:true,
			collapsed:true,
			height:340,
			autoScroll :true,
			style:'position:fixed;top:0px;left:0px;z-index:8001',
			width:200,
			html:'<ul id="fbIdxListBox" title="点击碎片名称可进行碎片编辑"></ul>'
		});
		idxListPanel.render(Ext.getBody());
		
		this.idxListBox = Ext.get('fbIdxListBox');
	},
	add:function(maskDom,idxNo,editdata){
		if(editdata){
			var liEl = idxMaskRender.listBox.idxListBox.createChild({
				tag:'li',
				id:'idx_listBox_' + idxNo
			});
			var titleEl = liEl.createChild({
				tag:'div',
				cls:'idx-title',
				html:editdata.title
			});
			var aEl = liEl.createChild({
				tag:'div',
				title:'点击按钮进行碎片编辑',
				cls:'icon-tools',
				html:''
			});
			idxMaskRender.listBox.bindEvent(aEl,titleEl,maskDom,idxNo,editdata);
			idxMaskRender.listBox.idxListBox.appendChild(liEl);
		}
	},
	bindEvent:function(aEl,titleEl,maskDom,idxNo,editdata){
		titleEl.on('mouseover',function(e,obj,options){
			options.obj.className = 'tplidxstyle3';
		},this,{preventDefault:true,stopEvent :false,delay:20,obj:maskDom});
		titleEl.on('mouseout',function(e,obj,options){
			options.obj.className = 'tplidxstyle2';
		},this,{preventDefault:true,stopEvent :false,delay:20,obj:maskDom});
		titleEl.on('click',function(e,obj,options){
			//定位
			document.documentElement.scrollTop = Ext.fly(options.maskDom).getTop()-50;
		},this,{
			preventDefault:true,
			stopEvent :false,
			maskDom:maskDom
		});

		aEl.on('click',function(e,obj,options){
			//定位
			document.documentElement.scrollTop =  Ext.fly(options.maskDom).getTop()-50;
			idxMaskRender.edit(e,obj,options);
		},this,{
			editUrl:editdata.editurl,
			idxNo:idxNo,
			idxId:editdata.id,
			preventDefault:true,
			stopEvent :false,
			maskDom:maskDom
		});
	}
};


var idxEditor = {
	currentIdxNo:-1,
	wins:{},
	init:function(url,idxNo,idxId){
		if(this.wins[idxNo]){
			this.wins[idxNo].close();
		}
		
		var win = new Ext.Window({
			title:'编辑碎片',
			height:590,
			width:680, 
			modal: true,
			buttonAlign: "center",
			closable:true ,
			closeAction:'close',
			modal:false,
			maximizable:true,
			layout:'fit',
			items:[{
				xtype:'panel',
				id:'iframePanel_' + idxNo,
				html:'<div style="width:100%;height:100%;background:url(../res/img/loading.gif) no-repeat center center;"><iframe id="fr_configView_'+ idxNo + '" scrolling="no" frameborder="0" width="100%" height="100%" style="visibility:hidden"></iframe></div>' 
			}],
			listeners: {
				close:function(w){
					//修复滚动条才消失的Bug
					w.restore();
					idxEditor.unPreview();
				},
				maximize:function(w){  
					//修复位置bug 
					var scrollPos = Ext.getBody().getScroll();		
					w.setPosition(scrollPos.left,scrollPos.top);
				}
			}
		});
		
		this.wins[idxNo] = win;	
		win.show();
		var iframe = document.getElementById('fr_configView_' + idxNo);
		iframe.onload = function(){
			//this.style.visibility = 'visible';
			//跨域发送数据，当前页面的url地址
			var msgJson = {
				data:{href:window.location.href},
				options:encodeURIComponent(Ext.encode({
					handler:'(function(receiveData){\
						receiveData = Ext.decode(decodeURIComponent(receiveData));\
						window.parentUrl__ = receiveData.href;\
					})',
					scope:"window",
				}))
			}
			this.contentWindow.postMessage(Ext.util.JSON.encode(msgJson), '*')
			
			Ext.fly(this).fadeIn({
				endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
				easing: 'easeIn',
				duration: .5,
				useDisplay: false
			});
		};
		iframe.src = url +'&idxNo='+ idxNo +'&idxId=' + idxId + '&domain=' + document.domain + '&_t='+ (new Date()).valueOf() ;
	},
	close:function(){
		this.wins[idxEditor.currentIdxNo].el.fadeOut({
			endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
			easing: 'easeOut',
			duration: .5,
			remove: false,
			useDisplay: false,
			scope:this.wins[idxEditor.currentIdxNo],
			callback:function(){
				this.close();
			}

		});
		//this.wins[idxEditor.currentIdxNo].close();
		//this.unPreview();
		delete this.wins[idxEditor.currentIdxNo];
	},
	isInnerIdx:function(idxEl){
		var ret=false;
		var editdata =  idxEl.getAttributeNS('','editdata');
		try{
			editdata = Ext.util.JSON.decode(editdata);
			if(editdata.type==2) ret = true;
		}catch(ex){
			
		}
		return ret;
	},
	getInnerHTML:function(content){
		var innerHTML = content;
		var tmpObj = document.createElement('div');
		tmpObj.innerHTML = content; 
		if(tmpObj.childNodes.length>0){
			for(var i=0;i<tmpObj.childNodes.length;i++){
				var element = tmpObj.childNodes[i];
				if(element.nodeType==1){
					innerHTML = element.innerHTML;
					break;
				}
			}
		}
		tmpObj = null;
		return innerHTML;
	},
	preview:function(content){
		var idxInfo = idxMaskRender.idxInfo[idxEditor.currentIdxNo]; 
		if(!idxInfo) return;
		content = idxEditor.getInnerHTML(content);
		var idxEl = Ext.fly(idxInfo.idxDom);
		if(idxEditor.isInnerIdx(idxEl)){
			idxInfo.isPreview = true;
			idxEl.first().update(content,true);	
			idxEl.first().removeClass('tplidxstyle');
			Ext.fly(idxInfo.maskDom).removeListener("click",idxMaskRender.edit,idxMaskRender);
			idxInfo.isRemoveClickEvent = true;
		}else{
			idxEl.update(content,true);	
			Ext.fly(idxInfo.maskDom).setDisplayed(false);
		}
		
	},
	unPreview:function(){
		var idxInfo = idxMaskRender.idxInfo[idxEditor.currentIdxNo];
		if(!idxInfo) return;
		var idxEl = Ext.fly(idxInfo.idxDom);
		if(idxEditor.isInnerIdx(idxEl)){
			idxInfo.isPreview = false;
			var editdata = idxInfo.editdata;
			if(idxInfo.isRemoveClickEvent==true){
				Ext.fly(idxInfo.maskDom).on('click',idxMaskRender.edit,idxMaskRender,{
					editUrl:editdata.editurl,
					idxNo:idxEditor.currentIdxNo,
					idxId:editdata.id,
					preventDefault:true,
					stopEvent :false,
					obj:idxInfo.maskDom
				});
				idxInfo.isRemoveClickEvent = false;
			}
			var maskEl = Ext.fly(idxInfo.maskDom);
			maskEl.update(idxInfo.html,true);
			maskEl.addClass('tplidxstyle');
		}else{
			idxEl.update(idxInfo.html,true);	//update( String html, [Boolean loadScripts], [Function callback] ) : Ext.Element 
			Ext.fly(idxInfo.maskDom).setDisplayed(true);
		}
	},
	saved:function(content){
		idxMaskRender.idxInfo[idxEditor.currentIdxNo].html = idxEditor.getInnerHTML(content);
		idxEditor.close();
	},
	onMessage:function(e){
		var data = e.data;
		data = Ext.util.JSON.decode(data);
		idxEditor.currentIdxNo = data.idxNo;
		if(data.action=='close'){
			idxEditor.close();
		}else if(data.action=='preview'){
			idxEditor.preview(decodeURIComponent(data.msg));
		}else if(data.action=='unPreview'){
			idxEditor.unPreview();
		}else if(data.action=='save'){
			idxEditor.saved(decodeURIComponent(data.msg));
			
		}
	}
};
//idxEditor.init();

