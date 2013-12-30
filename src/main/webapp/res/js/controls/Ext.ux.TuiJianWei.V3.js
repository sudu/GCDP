/*
*推荐位V3控件
*说明：
*author:cici
*date:2012/12/9
*依赖：CookiesHelper.js|Ext.ux.TextArea2.js|Ext.ux.TitleField.js|Ext.ux.UploadField.js|Ext.ux.ListPanel.js
*/
if(!Ext.ux)Ext.ns("Ext.ux");
if(!Ext.ux.TuiJianWei)Ext.ns("Ext.ux.TuiJianWei");

Ext.ux.TuiJianWei.V3 = Ext.extend(Ext.form.Field, {
	/****/
	publisher:'',//推送者
	viewSize:10,//每页可见数
	mainTitle: "推荐位数据列表",
	searchItems:[],/*[{item:{//支持多按钮，多个导入
		searchTitle:"文章搜索结果列表",
		searchBtnTxt:"导入文章",
		dataSourceListPage:'',//数据源列表页地址
	}}],*/
	hideEditColumn:false,
	hideAddButton:false,
	addBtnTxt: "手工添加",
	mainPanelCls: "NewsPublisher_Panel",
	maxReserveRecordCount:200,//最大保留记录数,-1表示不限制
	metaData: null,
	store:null,
	searchResult:null,
	resPath:'../res/',
	/**标题标尺属性**/
	fontSize:12,
	rulerNum1:12,
	rulerNum2:14,
	rulerNum3:16,
	rulerNum4:22,
	rulerNums:null,
	titleRulerArr:null,
	/****/
	searchWindowArr:null,
	editWindow:null,//编辑窗口
	/*****/
	fields:null,//列表字段数组
	columnsCfg:null,//列表项
	editView:{value:{//编辑视图
		url:null,
		width:500,
		height:600
	}},
	keyField:"id",//主键字段
	isWidthFreeRow:-1,
	initComponent: function () {
		this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
		this.rulerNums=[];
		this.titleRulerArr=[];
		this.publisher = Cookies.get('cmpp_cn');
		
		if(typeof this.initialConfig.editView ==="string"){
			this.initialConfig.editView = Ext.decode(this.initialConfig.editView);
		}
		if(typeof this.initialConfig.searchItems ==="string"){
			this.initialConfig.searchItems = Ext.decode(this.initialConfig.searchItems);
		}
		Ext.applyDeep(this,this.initialConfig);
		if(typeof this.fields ==="string") this.fields = Ext.decode(this.fields);
		this.fields.push("sys_publisher","sys_publishDateTime","id");
		if(typeof this.columnsCfg ==="string") this.columnsCfg = Ext.decode(this.columnsCfg);
		//if(typeof this.formConfig ==="string") this.formConfig = Ext.decode(this.formConfig);
		if(!this.editView.value || !this.editView.value.url){
			Ext.Msg.alert("没有配置数据项的编辑视图地址");
		}
		this.initTitleRuler();	
		this.searchWindowArr = this.initSearchPanel();	
		this.editWindow = this.initEditWindow();
		Ext.ux.TuiJianWei.V3.superclass.initComponent.call(this);
	},
	onRender:function(ct,position){
		Ext.ux.TuiJianWei.V3.superclass.onRender.call(this, ct, position);
		this.mainPanel = this.initMainPanel(ct, position);
		this.setValue(this.el.dom.value);
	},
	afterRender: function () {
		Ext.ux.TuiJianWei.V3.superclass.afterRender.call(this);
		var t = this;
		if(this.el.up("form")){
			this.el.up("form").on("submit", function () {
				t.getValue();
			});
		}
	},	
	getValue: function () {
		if(!this.metaData)this.setValue();
		//截去超过最大保留记录数之外的数据
		if(this.maxReserveRecordCount>0 && this.metaData.root.length>this.maxReserveRecordCount){
			this.metaData.root = this.metaData.root.slice(0,this.maxReserveRecordCount);
		}
		this.el.dom.value = Ext.encode(this.metaData);
		return Ext.ux.TuiJianWei.V3.superclass.getValue.call(this);
	},
	setValue: function (v) {
		var vObj = v;
		if(this.el) this.el.dom.value =v;
		if(Ext.nore(v)){
			vObj = {root:[]};
			v = Ext.encode(vObj);	        	
		}else{
			if(typeof v=="string"){
				vObj = Ext.decode(v);
			}
		}
		Ext.ux.TuiJianWei.V3.superclass.setValue.call(this, v);

		if(this.store){	
			//只显示前viewSize条数据
			var data = {};
			Ext.applyDeep(data,vObj);
			var rootData = [];
			Ext.apply(rootData,data["root"]);
			if(rootData.length>this.viewSize){
				rootData = rootData.slice(0, this.viewSize);
			}else{
				this.mainPanel.bbar.hide();
			}
			data["root"] = rootData;
		   this.store.loadData(data);
		}
		this.metaData = vObj;   		
	},
	//private function
	/*
	*初始化编辑窗口
	*/
	initEditWindow:function(){
		var url=this.editView.value.url;
		var messageData = {
			handler:'Ext.getCmp("'+ this.id +'").receiveEditData',
			scope:'Ext.getCmp("'+ this.id +'")',
			data:{},
			sender:'window.parent'
		};
		var postMessageParams = '&postMessage__=' + encodeURIComponent(Ext.encode(messageData));
		url += postMessageParams;
		var win = new Ext.Window({
			title:"编辑",
			width:this.editView.value.width||600, 
			height:this.editView.value.height||580,
			buttonAlign: "center",
			closable:true ,
			closeAction:'hide',
			maximizable :true,
			//autoScroll:true,
			border:false,
			modal:true,
			layout:'fit',
			resizable :true,
			items:[{
				layout:'fit',
				html:'<div style="width:100%;height:100%;background:url(../res/img/loading.gif) no-repeat center center;"><iframe scrolling="no" frameborder="0" width="100%" height="100%" style="visibility:visible" src="' + url + '"></iframe></div>' 
			}]
		});
		return win;
	},
	initTitleRuler:function(){
		if(this.rulerNum1 && parseInt(this.rulerNum1)>0){
			this.rulerNums.push(parseInt(this.rulerNum1));
		}
		if(this.rulerNum2 && parseInt(this.rulerNum2)>0){
			this.rulerNums.push(parseInt(this.rulerNum2));
		}
		if(this.rulerNum3 && parseInt(this.rulerNum3)>0){
			this.rulerNums.push(parseInt(this.rulerNum3));
		}
		if(this.rulerNum4 && parseInt(this.rulerNum4)>0){
			this.rulerNums.push(parseInt(this.rulerNum4));
		}
	},
	initSearchPanel:function(){
		var items = this.searchItems;
		var winArr = [];
		for(var i=0;i<items.length;i++){
			var item = items[i].item;
			var searchTitle = item.searchTitle,listPageUrl = item.dataSourceListPage;
			var optionsData = {
				handler:'Ext.getCmp("'+ this.id +'").import',
				scope:'Ext.getCmp("'+ this.id +'")'
			};
			if(listPageUrl.indexOf('?')!=-1){
				listPageUrl+='&optionsData__=' + encodeURIComponent(Ext.encode(optionsData));
			}else{
				listPageUrl+='?optionsData__=' + encodeURIComponent(Ext.encode(optionsData));
			}
			var window = new Ext.Window({
				title:searchTitle,
				closable: true,
				closeAction: "hide",
				modal: true,
				buttonAlign: "center",
				resizable: true,
				layout: "fit", 
				width:645,
				height :500,
				maximizable:true,
				layout:'fit',
				html:'<div style="width:100%;height:100%;background:url(../res/img/loading.gif) no-repeat center center;"><iframe id="'+ this.id +'_searchFrame_'+ i +'" scrolling="no" frameborder="0" width="100%" height="100%" style="visibility:hidden" _src="'+listPageUrl+'"></iframe></div>' ,
				buttons:[{
					text:'关闭',
					handler:function(){
						var win = this.ownerCt;
						win.el.fadeOut({
							endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeOut',
							duration: 0.5,
							callback:function(){
								win.hide();
							}
						});
					}
				}]
			});
			winArr.push(window);
		}
		return winArr;
	},
	initMainPanel:function(ct, position){
		var t = this;
		//准备tbar
		
		var tbarArr = [{
			xtype:'label',
			text:this.mainTitle
		},{
			xtype:'tbfill'
		}];
		var searchBtnHandler = function(button,index){
			var win = this.searchWindowArr[index];
			win.show(null,function(win){
				var t = this.t;
				win.el.fadeIn({
					endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
					easing: 'easeIn',
					duration: 0.3,
					remove: false,
					useDisplay: false,
					scope:this,
					callback:function(){
						if(win.hasIframe===true)return;
						var iframe = Ext.get(t.id + '_searchFrame_' + this.index);
						iframe.on("load",function(e){
							win.hasIframe = true;
							Ext.fly(e.target).fadeIn({
								endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
								easing: 'easeIn',
								duration: .3,
								useDisplay: false
							});	
						},win);	
						var src =iframe.getAttributeNS("","_src");
						iframe.dom.src = src; 	
					}
				});
					
			},{t:t,index:index});
		};
		var items = this.searchItems;		
		for(var i=0;i<items.length;i++){
			var item = items[i].item;
			tbarArr.push({
				text:item.searchBtnTxt,
				scope:t,
				handler:searchBtnHandler.createDelegate(t,i,1)
			});
		}
		this.searchHistoryEnable && tbarArr.push({
			text:"历史数据",
			disabled:(formConfig__&&formConfig__.id)?false:true,
			scope:t,
			handler:function(){
				if(Ext.ux.grid && Ext.ux.grid.GridPanel){
					this.innerSearchWin.show(null,function(){
						this.innerSearchWin.el.fadeIn({
							endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeIn',
							duration: 0.3,
							remove: false,
							useDisplay: false,
							scope:this,
							callback:function(){
								//load list 
								if(!this.historyDataMgr.store){
									this.historyDataMgr.init.apply(this,[this.innerSearchWin]);
								}
							}
						});
					},this);
				}else{
					Ext.Msg.show({msg:'sorry!正在加载控件包,稍等一下下...'});
				}
			}
		});
		if(!this.hideAddButton){
			tbarArr.push({
				text:t.addBtnTxt,
				scope:t,
				handler:this.addNew
			});
		}
		//准备store	 
		var updateValue = function(store){
			this.el.dom.value = Ext.encode(this.metaData);
		}
		var store = new Ext.data.Store({ 
			reader: new Ext.data.JsonReader({
	            root: "root",
	            id: this.keyField,
	            fields: this.fields
	        }),
			listeners :{
				scope:this,
				load:function(){						
					this.setTitleRuler();
				},
				datachanged:updateValue,
				update :updateValue,
				add :updateValue,
				remove :updateValue,
				clear:updateValue
			}
		});
		this.store = store;
		//准备columns
		this.selModel = new Ext.grid.CheckboxSelectionModel({width :25,singleSelect:false});
	    var columns = [new Ext.grid.RowNumberer(),this.selModel];
		if(!this.hideEditColumn){
			columns.push({
				header: "编辑",
				width: 40,
				align:'center',
				renderer: function (v, p, record) {
					return '<a href="javascript:void(0)" title="编辑">编辑</a>';
				}
			});
		}
		for(var i=0;i<this.columnsCfg.length;i++){
			var col = this.columnsCfg[i];
			if(col.isView!=false){
				var o = {
					id:'columns-' + i,
					header : col.title, 
					dataIndex : col.field?col.field:'id',
					align:col.align,
					renderer:this.renderField(col)
				};
				if(col.width)o.width = col.width+8;
				columns.push(o);
			}
		}
		columns.push({
		   header: "推送者",
		   dataIndex: "sys_publisher",
		   width:50,
		   align:'center'
		},{
		    header: "推送时间",
		    dataIndex: "sys_publishDateTime",
		    width:80,
		    align:'left',
			renderer: function (v, p, record) {
				if(v){
					var dt = new Date(v);
					var dtStr = dt.format('m-d H:i');//推送时间
					return '<span title="'+v+'">'+ dtStr +'</span>';
				}else{
					return '';
				}
			}
		},{
			header: "置顶",
			dataIndex:"id",
			width: 40,
			renderer: function (v, p, record) {
				return '<div class="btnUp" title="置顶"></div>';
			}
		},{
			header: "移除",
			dataIndex:"id",
			width: 40,
			renderer: function (v, p, record) {
				return '<div class="btnDelete" title="移除"></div>';
			}
		});		
		
		this.columns = new Ext.grid.ColumnModel(columns);
		
		//初始化列表
		var mainPanel=new Ext.grid.EditorGridPanel({
			context:this,
			renderTo: ct,
			cls: this.mainPanelCls,
			width:this.width||(ct.getWidth()-(this.hideLabel==true?0:ct.getPadding("l"))-ct.getPadding("r")),//chrome 和 ff 的区别
			store: store,
			cm: this.columns,
			sm: this.selModel,
			stripeRows :true,//表格行颜色间隔显示
			autoExpandColumn: this.isWidthFreeRow!=-1?'columns-'+this.isWidthFreeRow:null,
			autoHeight: true,
			autoScroll:true,
			enableHdMenu: false,
			dropConfig: {appendOnly:false},
			ddText:'移动到',
			plugins:(!this.width && Ext.grid.plugins.AutoResize)?new Ext.grid.plugins.AutoResize():null,
			trackMouseOver : true,
			enableDragDrop: true,	
			ddGroup: "GridDD",	
			tbar:tbarArr,	
			bbar:[{
				xtype:'tbbutton',
				text:'显示更多︾',
				hideMode:'display',
				scope:t,
				handler:function(obj,e){
					var viewCount = this.store.getCount();
					if(this.metaData.root.length>viewCount+this.viewSize){
						viewCount += this.viewSize;
					}else{
						viewCount = this.metaData.root.length;
						 this.mainPanel.bbar.hide();
					}
					var data={};
					var rootData=[];
					Ext.applyDeep(data,this.metaData);
					Ext.apply(rootData,data.root);
					data.root = rootData.slice(0,viewCount);
					this.store.loadData(data);						
				}
			}],			
			preEditValue : function(r, field){
				var val=Ext.apply({},r.data);
				return val;
			},	
			onEditComplete : function(ed, val){
				this.editing = false;
				this.activeEditor = null;
				ed.un("specialkey", this.selModel.onEditorKey, this.selModel);
				var r = ed.record;
				var index = this.store.indexOf(r);
				var d=this.metaData.root[index];					
				console.info("onEditComplete todo");//cds

			},
			listeners: {
				scope:t,
				cellclick: function (panel, row, cell, e) {
					var store = this.store;
					switch (e.target.title) {
						case '移除'://移除
							t.removeItem(store, row);
							break;
						case '置顶': //置顶
							//t.moveUp(store.data, row, Ext.get(e.target).up(".x-grid3-row").dom)
							t.moveTop(store, row)
							break;
						case '编辑'://编辑
							t.editRecord(store,row);
							break;
					}
				}, 
				render: function (grid) {
					//拖拽排序
					var ddrow = new Ext.dd.DropTarget(grid.container, { 
						ddGroup : 'GridDD', 
						//overClass:'cmpp-drag-over',
						scope:this,
						notifyDrop : function(dd, e, data) { 
							//var rows = data.selections;
							var sm = grid.getSelectionModel(); 
							var rows = sm.getSelections(); 
							var store = grid.getStore();
							var cindex = dd.getDragData(e).rowIndex; 
							if (cindex == undefined || cindex < 0){ 
								e.cancel=true; 
								return; 
							} 
							for (i = 0; i < rows.length; i++) { 
								var rowData = rows[i]; 
								var oldIndex = store.indexOf(rowData);
								
								this.scope.metaData.root.splice(oldIndex,1);
								this.scope.metaData.root.splice(cindex,0,rowData.data);
								
								store.remove(rowData); 
								store.insert(cindex, rowData); 

								grid.getView().refresh();
								
							}
						} 
					});
					grid.bbar.dom.align = 'center';//按钮居中
				}
			}
		});
		return mainPanel;
	},
	//设置标题标尺
	setTitleRuler:function(){
		var firstTitleEl = this.mainPanel.body.child('.ruler_title');
		if(firstTitleEl){
			var height = this.mainPanel.body.getHeight();
			var left = firstTitleEl.getLeft()-this.mainPanel.body.getLeft();
			var top = 0;
			var fontSize = this.fontSize;
			
			for(var i=0;i<this.rulerNums.length;i++){
				if(this.titleRulerArr[i]){
					this.titleRulerArr[i].setHeight(height);
				}else{						
					var charNum = this.rulerNums[i];
					var rulerWidth = charNum*fontSize;
					var left1 = left + rulerWidth-2;
					var leftNum1 = left1+3;
					this.titleRulerArr[i] = this.mainPanel.body.createChild({
						tag:'div',
						style:"width:1px;height:"+height+"px;background:green;position:absolute;left:"+ left1 +"px;top:"+ top +"px"
					});
					this.mainPanel.body.createChild({
						tag:'div',
						style:"color:green;position:absolute;font-size:10px;left:"+ leftNum1 +"px;top:"+ top +"px",
						html:charNum
					});
				}
			}				
			
		}
	},
	//手工添加
	addNew:function(){
		this.editRecord(null,-1);
	},
	//编辑内容
	editRecord:function(store,row){
		var win = this.editWindow;
		win.setTitle(row==-1?'手工添加':'编辑记录-' + (row+1));
		if(win.iframe){
			win.show(null,function(win){
				win.el.fadeIn({
					endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
					easing: 'easeIn',
					duration: .3,
					useDisplay: false
				});	
			});
			var postData = {
				data:row==-1?{}:store.getAt(row).data,
				action:'init',
				editRow:row
			}
			win.iframe.postMessage(Ext.encode(postData), '*');
		}else{
			win.show(null,function(win){
				var args = this;
				var iframe = win.el.child("iframe").dom.contentWindow;
				win.iframe = iframe;
				iframe.onload = function(e,store,row){
					var el = this.el.child("iframe").parent();
					el.parent().setStyle("background","");
					el.fadeIn({
						endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
						easing: 'easeIn',
						duration: .3,
						useDisplay: false
					});	
					var postData = {
						data:row==-1?{}:store.getAt(row).data,
						action:'init',
						editRow:row
					}
					this.iframe.postMessage(Ext.encode(postData), '*');
				}.createDelegate(win,[args[0],args[1]],true);		
			},arguments);	
		}
	},
	/*
	*接收编辑视图返回的数据并处理
	*/
	receiveEditData:function(receiveData,realData,e){
		realData = Ext.decode(decodeURIComponent(realData));
		var receiveDataStr = decodeURIComponent(receiveData);
		receiveData = Ext.decode(receiveDataStr);
		var editRow = receiveData.editRow;
		var action  = receiveData.action;
		var values = receiveData.values;
		if(action=="submit"){
			this.addComplete(values,editRow)
		}
		this.hideWindow(this.editWindow);
	},
	addComplete:function(val,editRow){
		var t=this;
		if(editRow==-1){//add
			var row =this.store.find(this.keyField,val[this.keyField]);
			if(row!==-1){//排重 从store里移除存在的数据
				this.metaData.root.splice(row, 1);
				this.store.remove(this.store.getAt(row));
			}
			val["sys_publisher"] = t.publisher;//推送者
			val["sys_publishDateTime"] = (new Date()).format('Y-m-d H:i:s');//推送时间
			var record=new this.store.reader.recordType(val,val["id"]);
			var meta = Ext.applyDeep({},val);
			record.set("sys_NewAdd",true);
			t.appendData(record,meta);	

		}else{
			var record = this.store.getAt(editRow);
			val["sys_publisher"] = record.data.sys_publisher;
			val["sys_publishDateTime"] =record.data.sys_publishDateTime
			Ext.applyDeep(t.metaData.root[editRow],val);
			for(var k in val){
				record.set(k,val[k]);
			}				
		}	
		t.mainPanel.selModel.selectRecords(record,true);
		this.mainPanel.getView().refresh();	
		this.setTitleRuler();		
	},
	//先点击加入的放在列表的最顶部。获取新插入数据的位置
	import: function (dataStr) {
		var dataStr = decodeURIComponent(dataStr);
		var data = Ext.decode(dataStr);
		var records = [];
		var meta=[];
		var pushTime = (new Date()).format('Y-m-d H:i:s');//推送时间
		var exist  =false;
		for(var i=data.length-1;i>=0;i--){//先点击加入的放在列表的最顶部
			var r = new Ext.data.Record(data[i]);
			var row = this.store.find(this.keyField,data[i][this.keyField]);
			if(row!==-1){//排重 从store里移除存在的数据
				this.metaData.root.splice(row, 1);
				this.store.remove(this.store.getAt(row));
				exist = true;
			}
			data[i].sys_publisher = this.publisher;
			data[i].sys_publishDateTime = pushTime;
			meta.push(Ext.applyDeep({},this.filterImportData(data[i])));
			
			r.set("sys_NewAdd",true);
			r.set("sys_publisher",this.publisher);//推送者
			r.set("sys_publishDateTime",pushTime);//推送时间
			records.push(r);
		}
		
		var insertPos = 0;
		for(var i=0;i<meta.length;i++){
			this.metaData.root.splice(insertPos,0,meta[i]);
		}			
		if (records.length > 0) {    	
			this.store.insert(insertPos,records);
			//选中新添加的
			this.mainPanel.selModel.selectRecords(records,false);
		}	
	
		//this.searchWin.hide();
		if (records.length > 0) {
			var maskText =exist?"曾经推送过,重新推送将置顶":"导入成功";
			this.mainPanel.getView().refresh();	
			var maskEl = this.mainPanel.body.mask(maskText);
			maskEl.fadeOut({
				endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
				easing: 'easeOut',
				duration: 0.5,
				remove: false,
				useDisplay: false,
				scope:this,
				callback:function(){
					this.mainPanel.body.unmask();
				}
			});
			this.setTitleRuler();
		}
	},
	//过滤不需要的字段
	filterImportData:function(data){
		var newData={};	
		var flds = this.fields;
		for(var i=0;i<flds.length;i++){
			newData[flds[i]] = data[flds[i]];
		}
		return newData;
	},
	appendData:function(records,metaData){    	
		this.metaData.root.splice(0,0,metaData);		
		this.store.insert(0,records);		
	},
	/*
	**更新数据
	*/
	updateData:function(row,data){
		if(row!==-1){
			var metaItem = this.metaData.root[row];
			var record = this.store.getAt(row);
			Ext.applyDeep(metaItem,data);
			for(var key in data){
				record.set(key,data[key]);
			}
		}
	},
	removeItem: function (store, row) {
		this.metaData.root.splice(row, 1);
		var cur = store.getAt(row);
		store.remove(cur);
		this.mainPanel.getView().refresh();
		this.setTitleRuler();
	},
	exchange:function(gIndex,pIndex,data){
		var item=data.splice(gIndex,1)[0];
		data.splice(pIndex,0,item);
	},
	moveUp: function (data, row, curDom) {
		if (row > 0) {
			var prev = data.items.splice(row - 1, 1)[0];
			data.items.splice(row, 0, prev);
			prev = data.keys.splice(row - 1, 1)[0];
			data.keys.splice(row, 0, prev);
			prev = this.metaData.root.splice(row - 1, 1)[0];
			this.metaData.root.splice(row, 0, prev);

			prev = curDom.previousSibling;
			prev.rowIndex = row;
			curDom.rowIndex = row - 1;
			curDom.parentNode.insertBefore(curDom, prev);
		} else {
			var cur = data.items.splice(row, 1)[0];
			data.items.push(cur);
			cur = data.keys.splice(row, 1)[0];
			data.keys.push(cur);

			var list = curDom.parentNode.childNodes;
			var len = list.length;
			while (len--) {
				list[len].rowIndex--;
			}
			curDom.rowIndex = list.length - 1;
			curDom.parentNode.appendChild(curDom)
		}
	},
	moveTop:function(store, row, curDom) {
		if (row > 0) {				
			var record = store.getAt(row);
			this.metaData.root.splice(row,1);
			this.metaData.root.unshift(record.data);				
			store.remove(record); 
			store.insert(0, record); 
			this.mainPanel.getView().refresh();
		}
	},
	//查询结果处理
	storeLoadCallback:function(r,options,success){
		if(success && r.length==0){
			var body = this.searchTable.body;
			var gridbody = body.child('.x-grid3-body');
			var gridbodyParent = gridbody.findParentNode('.x-grid3-scroller');
			gridbodyParent = Ext.get(gridbodyParent);
			var tip = gridbody.createChild({
				tag:'img',
				src:'../res/img/nodata.gif'
			});
			var width = body.getWidth()-318;
			var height = body.getHeight()-73;
			tip.setStyle('margin',height/2 + 'px  ' + width/2 + 'px ' + height/2 + 'px  ' + width/2 + 'px ');
		}	
	},
	hideWindow:function(win){
		win.el.fadeOut({
			endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
			easing: 'easeOut',
			duration: .5,
			useDisplay: false,
			callback:function(){
				win.hide();
			}
		});	
	},
	/*
	*渲染列表模板
	*/
	renderField:function(coLPCFG){
		var tpl = null;
		coLPCFG.tpl = this.leftTrim(coLPCFG.tpl);
		if(coLPCFG.tpl && coLPCFG.tpl.substring(0,1)!='\'' && coLPCFG.tpl.substring(0,1)!='"') {
			if(coLPCFG.tpl) tpl = new Ext.XTemplate(coLPCFG.tpl);
		}else{
			var tplStr = 'new Ext.XTemplate(' + coLPCFG.tpl + ')';
			if(coLPCFG.tpl) eval('tpl=' + tplStr);
		}
		var width = coLPCFG.width;
		var isShowTip = coLPCFG.isShowTip;
		var tipTpl = null;
		var myZhuanyi = this.myZhuanyi;
		coLPCFG.tipTpl = this.myUnZhuanyi(coLPCFG.tipTpl);
		coLPCFG.tipTpl = this.leftTrim(coLPCFG.tipTpl);
		
		if(coLPCFG.tipTpl) {
			if(coLPCFG.tipTpl.substring(0,1)!='\'' && coLPCFG.tipTpl.substring(0,1)!='"'){
				if(coLPCFG.tipTpl) tipTpl = new Ext.XTemplate(coLPCFG.tipTpl);
			}else{
				var tplStr = 'new Ext.XTemplate(' + coLPCFG.tipTpl + ')';
				if(coLPCFG.tipTpl) eval('tipTpl=' + tplStr);
			}
		}
		return function(value, metadata, record){
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
			}
			if(width) {
				if(!Ext.isWebKit)
					displayHtml= '<div style="width:'+ width +'px;overflow:hidden;" ' + divTitle + '>'+ text +'</div>';
				else
					displayHtml=text;	
			}
			return displayHtml;
			
		}
	},
	leftTrim:function(ui){ 
		var notValid=/^\s/; 
		while(notValid.test(ui)){ 
			ui=ui.replace(notValid,"");
		} 
		return ui;
	},
	myZhuanyi:function(str){
		var ret = str;
		//ret = ret.replace(/&/g,'&amp;');
		ret = ret.replace(/"/g,'&quot;');
		ret = ret.replace(/>/g,'&gt;');
		ret = ret.replace(/</g,'&lt;');
		return ret;
	},	
	myUnZhuanyi:function(str){
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

});
Ext.reg('tuijianwei3', Ext.ux.TuiJianWei.V3);
