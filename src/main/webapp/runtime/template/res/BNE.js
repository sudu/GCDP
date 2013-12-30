
/*
*功能描述：通栏编辑、通栏渲染和编辑
*Author:cici
*date:2012/2
*/

var bannerMaskRender = {
	hasInitListBox:false,//是否已经初始化了列表
	idxEls:[],
	idxInfo:[],//{idxDom:xx,maskDom:xx,html:xx}
	tpl:new Ext.Template(
		'<div class="tplidxstyle" id="tplidx_{id}" style="width:{width}px;height:{height}px;top:{top}px;left:{left}px;position: absolute;"><div class="maskTitle" style="width:{width}px;"><span style="float:left" title="{info}">{info}</span><div style="float:right;"><span class="btn moveUpBtn" title="上移"></span><span class="btn moveDownBtn" title="下移"></span><span class="btn deleteBtn" title="删除"></span></div></div>'
	),	
	insertBtnTpl: new Ext.Template(
		'<strong title="插入通栏" style="cursor:pointer;float: left; border-style: solid; border-width: 10px; border-color: #FFFFFF green #FFFFFF #FFFFFF; height:0;top:{top}px;left:{left}px;position: absolute;"></strong>'
	),	
	init:function(){
		//bannerMaskRender.listBox.init();
		Ext.getBody().mask('正在初始化通栏编辑器...');
		
		var idxEls = this.getIdxEls();
		this.idxEls = idxEls;
		this.renderMask();
		//Ext.fly(window).on('resize',this.renderMask,this);
		this.hasInitListBox = true;
		Ext.getBody().unmask();
		bannerEditor.initToolbar();
	},
	renderMask:function(){
		var idxEls =  this.idxEls;
		for(var i=0;i<idxEls.getCount();i++){
			var el = idxEls.item(i);
			var idxTitle="通栏";
			
			var maskEl=null;
			var insertBtnDom=null;

			var idxInfo = this._renderMask(el,{
				idxNo:i,
				newIdxNo:i,
				pos:i,//位置
				title:idxTitle + "【"+ (i+1) +"】"
			});
			this.idxInfo[i] = idxInfo;
		}
	},
	_renderMask:function(idxEl,idxInfo){
		var bdy = Ext.getBody();
		var box = bannerMaskRender.getElBox(idxEl);
		var insertBtnDom = bannerMaskRender.insertBtnTpl.append(bdy,{left:box.left + box.width,top:box.top-10}); 		
		var maskData = {left:box.left,top:box.top,width:box.width,height:box.height,id:idxInfo.idxNo,info:idxInfo.title};
		var maskEl = Ext.fly(bannerMaskRender.tpl.append(bdy,maskData));
		
		var newIdxInfo = {
			idxDom:idxEl.dom,
			maskDom:maskEl?maskEl.dom:null,
			insertBtnDom:insertBtnDom		
		};
		Ext.applyIf(newIdxInfo,idxInfo);
		
		bannerMaskRender.bindMaskEvent(maskEl);
		var downBtnEl = maskEl.child('span.moveDownBtn');
		downBtnEl.on('click',bannerEditor.moveDown,newIdxInfo);
		var upBtnEl = maskEl.child('span.moveUpBtn');
		upBtnEl.on('click',bannerEditor.moveUp,newIdxInfo);
		var delBtnEl = maskEl.child('span.deleteBtn');
		delBtnEl.on('click',bannerEditor.delete,newIdxInfo);
		Ext.fly(insertBtnDom).on('click',bannerEditor.openInsertWin,newIdxInfo);
				
		return newIdxInfo;

	},
	
	bindMaskEvent:function(maskEl,i,editdata){
		maskEl.on('mouseover',this.idxMouseOver,this,{preventDefault:true,stopEvent :false,delay:20,obj:maskEl.dom});
		maskEl.on('mouseout',this.idxMouseOut,this,{preventDefault:true,stopEvent :false,delay:20,obj:maskEl.dom});
		maskEl.on('mousedown',this.idxMouseDown,this,{obj:maskEl.dom});
		maskEl.on('mouseup',this.idxMouseUp,this,{obj:maskEl.dom});

	},
	getElBox:function(el){
		var width= el.getWidth();
		var height = el.getHeight(); 
		var bodyWidth = Ext.getBody().getWidth();
		if(width>=bodyWidth-60) width=bodyWidth-60;
		return {width:width,height:height,left:el.getLeft(),top:el.getTop()};
	},
	idxMouseOver:function(e,obj,options){
		Ext.fly(options.obj).set({'cls':'tplidxstyleHighlight'});
	},
	idxMouseOut:function(e,obj,options){
		Ext.fly(options.obj).set({'cls':'tplidxstyle'});
	},
	idxMouseDown:function(e,obj,options){
		Ext.fly(options.obj).set({'cls':'tplidxstyleHighlightMouseDown'});
	},
	idxMouseUp:function(e,obj,options){
		Ext.fly(options.obj).set({'cls':'tplidxstyleHighlight'});
	},
	getIdxEls:function () {
		var idxEls2 = Ext.select('*[bannerId^=]');
		return idxEls2;
	}
	
}

var bannerEditor = {
	initToolbar:function(){
		var left = Ext.getBody().getViewSize().width/2-40;
		var btnDivEl = Ext.getBody().createChild({
			tag:'div',
			style:'position:fixed;top:5px;left:'+ left +'px;z-index:8001;',
			html:'<input type="button" value="提　交" style="width: 70px;" id="fbToolbar_btSubmit">'
		});
		Ext.get('fbToolbar_btSubmit').on('click',function(){
			var data = [];
			for(var i=0;i<bannerMaskRender.idxInfo.length;i++){
				var idx = bannerMaskRender.idxInfo[i];
				data.push({
					newOrder:i,
					oldOrder:idx.idxNo,
					pos:idx.pos,
					bannerId:idx.bannerId,
					action:idx.action
				});
			}
			var queryParams = Ext.parseQuery();
			var params={
				dataFormId:queryParams.dataFormId,
				dataId:queryParams.dataId,
				nodeId:queryParams.nodeId,
				id:queryParams.id,
				cmd:Ext.encode(data)
			}
			bannerEditor.submitCommands(params);
			
			
		});
	},
//提交操作指令
	submitCommands:function(params){
		Ext.getBody().mask("正在提交.....");
		Ext.Ajax.request({  
			url:'template!buildBlock.jhtml',
			method:'post',	
			params:params,
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				if(ret.success==false){
					Ext.Msg.show({
						   title:'错误提示',
						   msg: "提交失败！"+decodeURIComponent(ret.message?ret.message:ret.statusText),
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
					});
				}else{
					Ext.Toast.show('提交成功',{
						title:'提示',
						buttons: Ext.Msg.OK,
						animEl: 'elId',
						minWidth:420,
						icon: Ext.MessageBox.INFO,
						callback:function(){
							location.reload();
						}
					});
				}

			},
			failure:function(ret,opts){
				Ext.getBody().unmask();
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
	moveUp:function(){
		var idx = this;
		if(idx.newIdxNo==0) return;

		var idxPre = bannerMaskRender.idxInfo[idx.newIdxNo-1];
		bannerEditor._Exchange(idxPre,idx);
	},
	moveDown:function(){
		var idx = this;
		if(idx.newIdxNo==bannerMaskRender.idxInfo.length-1) return;
		
		var idxNext = bannerMaskRender.idxInfo[idx.newIdxNo+1];
		bannerEditor._Exchange(idx,idxNext);
	},
	"delete":function(){
		var idx = this;
		if(idx.idxNo!=-1){
			bannerEditor.mask(idx);
		}else{
			var dis = -Ext.fly(idx.idxDom).getHeight();
			//删除新添加的
			Ext.fly(idx.maskDom).remove();
			Ext.fly(idx.insertBtnDom).remove();
			
			Ext.fly(idx.idxDom).fadeOut({
				endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
				easing: 'easeOut',
				duration: .5,
				remove: true,
				useDisplay: false,
				scope:idx,
				callback:function(){
					var idx = this;
					for(var i=idx.newIdxNo+1;i<bannerMaskRender.idxInfo.length;i++){
						var oIdx = bannerMaskRender.idxInfo[i];
						oIdx.newIdxNo-=1;
						Ext.fly(oIdx.idxDom).move('down',dis,false);
						Ext.fly(oIdx.maskDom).move('down',dis,false);
						Ext.fly(oIdx.insertBtnDom).move('down',dis,false);
					}
					bannerMaskRender.idxInfo.splice(idx.newIdxNo,1);	
				}
			});		
		}
		idx.action = 'delete';
	},
	insertHandler:function(bannerId){
		Ext.getBody().mask("正在请求数据......");
		Ext.Ajax.request({  
			url:'xform!viewData.jhtml?viewId='+ bannerSetting__.viewId +'&formId='+ bannerSetting__.formId +'&id=' + bannerId,  
			method:'get',	
			success:function(response,opts){
				Ext.getBody().unmask();
				try{
					var ret = Ext.util.JSON.decode(response.responseText);
				}catch(ex){
					Ext.Msg.show({
						title:'错误提示',
						msg: '获取数据失败',
						buttons: Ext.Msg.OK,
						animEl: 'elId',
						minWidth:420,
						icon: Ext.MessageBox.ERROR 
					});
					return;
				}
				bannerEditor.insert(ret.recordData);
			},
			failure:function(ret,opts){
				Ext.getBody().unmask();
				Ext.Msg.show({
					title:'错误提示',
					msg: '获取数据失败',
					buttons: Ext.Msg.OK,
					animEl: 'elId',
					minWidth:420,
					icon: Ext.MessageBox.ERROR 
				});
			}
		});

		bannerEditor.insertWin.hide();
		
	},
	//插入通栏内容
	insert:function(data){
		var idx = bannerEditor.insertWin.idx;
		var html = data.content;
		var title = data.title;
		var newBannerEl = Ext.getBody().insertHtml('BeforeEnd',html,true);
		newBannerEl.setY(Ext.fly(idx.idxDom).getY());
		
		//////创建mask///////////
		var newIdxInfo = bannerMaskRender._renderMask(newBannerEl,{
			idxNo:-1,
			newIdxNo:idx.newIdxNo,
			pos:idx.pos,
			bannerId:data.id,
			action:'add',
			title:title
		});
		
		bannerMaskRender.idxInfo.splice(idx.newIdxNo,0,newIdxInfo);
		//移动插入点以下的通栏
		idx.newIdxNo+=1;
		var dis = newBannerEl.getHeight();
		Ext.fly(idx.idxDom).move('down',dis,true);
		Ext.fly(idx.maskDom).move('down',dis,true);
		Ext.fly(idx.insertBtnDom).move('down',dis,true);
		for(var i=bannerMaskRender.idxInfo.length-1;i>idx.newIdxNo;i--){
			var oIdx = bannerMaskRender.idxInfo[i];
			oIdx.newIdxNo+=1;
			Ext.fly(oIdx.idxDom).move('down',dis,false);
			Ext.fly(oIdx.maskDom).move('down',dis,false);
			Ext.fly(oIdx.insertBtnDom).move('down',dis,false);
		}

	},
	openInsertWin:function(){
		var idx = this;
		var win = bannerEditor.insertWin;
		
		if(win){
			win.center();
			win.show(this.insertBtnDom);
		}else{
			win = new Ext.Window({
				title:'通栏库',
				height:465,
				width:500, 
				buttonAlign: "center",
				closable:true ,
				closeAction:'hide',
				autoScroll:true,
				modal:true,
				layout:'fit',
				resizable :false,
				items:[{
					xtype:'panel',
					layout:'anchor',
					frame:false,
					header :false,
					border:false,
					id:'datagridPanel',
					items:[{
						xtype:'panel',
						anchor:'100%',
						autoHeight:true,
						frame:false,
						border:false,
						header :false,		
						id:'placeholderBanner'
					}]
				}],
				defaultButton:'btnHisClose',
				buttons:[{
					text:'关闭',
					id:'btnHisClose',
					handler:function(){
						bannerEditor.insertWin.hide();
					}
				}]
			});
			win.show(this.insertBtnDom,function(){
				bannerListMgr.datagridPanel = Ext.getCmp('datagridPanel');
				bannerListMgr.init();
				bannerListMgr.grid.render('placeholderBanner');	
			});
			bannerEditor.insertWin = win;
		}
		bannerEditor.insertWin.idx = this;
	},
	
	_Exchange:function(idx,idx2){

		var idxH = Ext.fly(idx.idxDom).getHeight() ;
		var maskH = Ext.fly(idx.maskDom).getHeight() ;
		var idxH2 = Ext.fly(idx2.idxDom).getHeight() ;
		var maskH2 = Ext.fly(idx2.maskDom).getHeight() ;
		
		var dis1 = Ext.fly(idx2.maskDom).getY()-Ext.fly(idx.maskDom).getY()-maskH + maskH2;
		var dis2 = Ext.fly(idx2.maskDom).getY()-Ext.fly(idx.maskDom).getY();
		
		Ext.fly(idx.maskDom).move("down",dis1,true);
		Ext.fly(idx.idxDom).move("down",dis1,true);
		Ext.fly(idx.insertBtnDom).move("down",dis1,true);
		
		Ext.fly(idx2.maskDom).move("down",-dis2,true);
		Ext.fly(idx2.idxDom).move("down",-dis2,true);
		Ext.fly(idx2.insertBtnDom).move("down",-dis2,true);
		//交换序列号
		var tempNo = idx.newIdxNo;
		idx.newIdxNo = idx2.newIdxNo;
		idx2.newIdxNo = tempNo;
		//交换位置号
		tempNo = idx.pos;
		idx.pos = idx2.pos;
		idx2.pos = tempNo;
		//交换数组位置
		
		var tempIdx = idx;
		bannerMaskRender.idxInfo[idx.newIdxNo] = idx;
		bannerMaskRender.idxInfo[idx2.newIdxNo] = idx2;
		
	},
	mask:function(idx){
		var el = Ext.fly(idx.maskDom);
		el.createChild({
			tag:'div',
			cls:'ext-el-mask'
		});
		var left = el.getWidth()-60;
		var top =  0;
		var msgMsgEl = el.createChild({
			tag:'div',
			cls:'ext-el-mask-msg',
			style:'cursor:default;left:' + left + 'px;top:' + top + 'px;',
			html:'<a class="buttonMini">恢　复</a>'
		});
		msgMsgEl.child('a.buttonMini').on('click',function(){
			var idx = this;
			idx.action="";
			bannerEditor.unmask(Ext.fly(idx.maskDom));
		},idx);
	},
	unmask:function(el){
		var maskEl= el.child('div.ext-el-mask');
		if(maskEl) maskEl.remove();
		maskEl= el.child('div.ext-el-mask-msg');
		if(maskEl) maskEl.remove();
	}
}
/*
×通栏列表
*/
var bannerListMgr={
	pageSize:15,
	grid:null,
	column:null,
	pagerBar:null,
	formDataCache:{},
	store:new Ext.data.Store({ 
		proxy : new Ext.data.HttpProxy({url : 'xlist!data.jhtml',method:'post'}), 
		baseParams :{
			formId:	bannerSetting__.formId,//从结点配置里读取，故在节点配置时需要配置环境变量 formId
			listId:bannerSetting__.listId,//环境变量 listId
			limit:15,
			from:'db',
			where:'[{"field":"nodeId","op":"=","value":'+ bannerSetting__.nodeId  +',"andor":"and"}]',
			sort:'[{"field":"weight","order":"desc"},{"field":"id","order":"desc"}]'
		},
		reader : new Ext.data.JsonReader({
			autoLoad:true,
			root : "data",
			totalProperty : "totalCount",
			fields: ['id','title','creator','thumb']
		}),
		remoteSort: false
	}),
	init:function(){
		this.initPageBar();
		this.initColumn();
		this.initGrid();
		this.store.load({
			start:0,
			limit:this.pageSize
		});
		
	},
	initPageBar:function(){
		this.pagerBar = new Ext.PagingToolbar({ 
			pageSize : this.pageSize, 
			store : this.store, 
			displayMsg : '显示第 {0} 条到 {1} 条记录,共 {2} 条记录',
			emptyMsg : "没有要显示的数据",
			firstText : "首页",
			prevText : "前一页",
			nextText : "下一页",
			lastText : "尾页",
			refreshText : "刷新",
			displayInfo : true 
		});
	},
	initGrid:function(){
		var grid = new Ext.grid.GridPanel({ 
			stripeRows: true,　　//隔行换色
			loadMask:{ 
				msg:"数据正在加载中...." 
			}, 
			columnLines: true,　　//显示列线条
			store: this.store,
			cm: this.column,
			trackMouseOver:true,
			stripeRows: true,
			sm: new Ext.grid.CheckboxSelectionModel(),
			loadMask: true,
			autoSizeColumns : true,
			autoScroll:true, 
			border:false,
			viewConfig: {
				forceFit:false,//宽度是否平均分
				enableRowBody:true
			},
			autoExpandColumn :'title',
			height:this.datagridPanel.body.getHeight(),

			iconCls:'icon-grid',
			frame:false,
			bbar : this.pagerBar
		}); 
		this.grid = grid;
	},
	initColumn:function(){
		var cm = new Ext.grid.ColumnModel([{
				id:'id',
			    header: "ID",
			    dataIndex: 'id',
			    menuDisabled: true,
				width:60,
				align:'center',
			    sortable:true
			},{
				id:'title',
			    header: "标题",
			    dataIndex: 'title',
			    menuDisabled: true,
				sortable:true
			    
			},{
				id:'thumb',
			    header: "缩略图",
			    dataIndex: 'thumb',
			    menuDisabled: true,
				sortable:false,
				align:'center',
				renderer:this.renderField('<tpl if="thumb!=\'\'">\
				<a href="{thumb}" target="_blank">查看</a>\
				</tpl>',
				'<tpl if="thumb!=\'\'">\
				<img src="{thumb}" width="400"/>\
				</tpl>')
			    
			},{
				id:'creator',
			    header: "创建人",
			    dataIndex: 'creator',
			    menuDisabled: true,
				sortable:true,
				align:'center',
				width:60,
			    align:'center'
			},{
			   header: "插入",
			   menuDisabled: true,
			   sortable:true,
			   dataIndex: 'key',
			   width: 60,
			   align:'center',
			   renderer:this.renderField('<a  href ="javascript:bannerEditor.insertHandler({id})">导入</a>')
			}
		]);
		this.column = cm;
	},
	//根据模板渲染内容
	renderField:function(exttpl,tiptpl){
		var tplStr = exttpl;
		var tipTplStr =null;
		if(tiptpl) tipTplStr = this.myZhuanyi(tiptpl);
		return function(value, metadata, record){
			var tpl = new Ext.XTemplate(tplStr);
			var tipTpl=null;
			if(tipTplStr && record.data.thumb){
				tipTpl = new Ext.XTemplate(tipTplStr);
				if(tipTpl){
					tip = tipTpl.applyTemplate(record.data);	
					metadata.attr = 'ext:qtitle=""' + ' ext:qtip="' + tip + '"'; 
				}
			}
			
			return tpl.applyTemplate(record.data);
		}
	},
	myZhuanyi:function(str){
		var ret = str;
		//ret = ret.replace(/&/g,'&amp;');
		ret = ret.replace(/"/g,'&quot;');
		ret = ret.replace(/>/g,'&gt;');
		ret = ret.replace(/</g,'&lt;');
		return ret;
	}		
}