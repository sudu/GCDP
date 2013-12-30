/*
*推荐位控件
*author:cici
*date:2012/5/4
*依赖：CookiesHelper.js|Ext.ux.TextArea2.js|Ext.ux.TitleField.js|Ext.ux.UploadField.js
*/

(function(){
	var field=Ext.extend(Ext.form.Field,{
		grid:null
		,onRender:function(ctnr,pos){
			this.grid=new Ext.grid.PropertyGrid({
				renderTo:ctnr    
				,listeners:{
					render:function(){
						var refresh=this.view.refresh;
						this.view.refresh=function (headersToo) {
			                this.scroller.dom.style.height = "auto";
			                this.scroller.dom.style.width = "auto";
			                this.el.dom.style.height = "auto";
			                refresh.call(this,headersToo);
			            }
					}
				}
			});		
			this.el=this.grid.el;
		}
		,setValue:function(v){
			this.grid.setSource(v);
		}
		,getValue:function(){
			return this.grid.getSource();
  		}  
	});
	//转义
	function myZhuanyi(str){
		var ret = str;
		//ret = ret.replace(/&/g,'&amp;');
		ret = ret.replace(/"/g,'&quot;');
		ret = ret.replace(/>/g,'&gt;');
		ret = ret.replace(/</g,'&lt;');
		return ret;
	}	
	Ext.ux.TuiJianWei = Ext.extend(Ext.form.Field, {
		publisher:'',
		dataSourceListPage:''
		,fieldNames:{"root":"root","count":"count","id":"url","title":"title","url":"url","img":"img","abstract":"abstract","createTime":"createTime","sys_ext":"sys_ext"}//默认值
		,tableFields:["id","url","title","abstract","img","createTime","sys_ext","sys_publisher","sys_publishDateTime"]
		,viewSize:10//每页可见数
	    , mainTitle: "文章列表"
	    , searchTitle: "文章搜索结果列表"
	    , searchBtnTxt: "搜索文章"
	    , addBtnTxt: "手动添加"
	    , mainPanelCls: "NewsPublisher_Panel"
		,searchHistoryEnable:null
	    , selected: null//{id:row}
	    , metaData: null
	    ,sourceBox:null
		,rootField:'root'
	    ,store:null
	    ,searchResult:null
	    ,isAdding:null
		,resPath:'../res/',
		/**标题标尺属性**/
		fontSize:12,
		rulerNum1:12,
		rulerNum2:14,
		rulerNum3:16,
		rulerNum4:22,
		rulerNums:null,
		titleRulerArr:null,
		/****/
		historyDataMgr:null,
		searchWin:null, 
		innerSearchWin:null,//内部搜索窗
		addNewWin: null,
		getBaseData:function(){
			return this.fields2fields;
		},
		convertBaseFieldsName:function(records){
			var ret=[];
			var fds=this.getBaseData();
			for(var i=0;i<records.length;i++){
				var r=records[i];
				var r2={};
				Ext.apply(r2,r);
				var rRet = {};
				rRet["id"]=r["id"];
				delete r2["id"];
				for(var key in fds){
					rRet[key] = r[fds[key]];//
					delete r2[fds[key]];
				}
				
				 var sys_ext = Ext.encode(r2);//把非默认字段组合成JSON放到扩展值里面
				 rRet[this.fieldNames["sys_ext"]] = sys_ext
				 rRet["sys_publisher"] = this.publisher;//推送者
				 rRet["sys_publishDateTime"] =  (new Date()).format('Y-m-d H:i:s');//推送时间
				 
				 ret.push(rRet);
				 
			}
			return ret;
		}
		,parseSearchData:function(data){
			var list=data[this.fieldNames.root];
			var fds=this.getBaseData();
			for(var i=0,len=list.length;i<len;i++){
				var itm=list[i];
				var o=list[i]={};
				for(var j=0,len2=this.tableFields.length;j<len2;j++){
					var n=this.tableFields[j];
					o[n]=itm[fds[n]];
				}
			}
		}
		,selectTableData:function(records){
			var len=records.length;
			var tableData=new Array(len);
			var fds=this.getBaseData();
			for(var i=0;i<len;i++){
				 var r=records[i];
				 tableData[i]={};
				 tableData[i][this.fieldNames["id"]]=r[fds[this.fieldNames["url"]]];
				 tableData[i][this.fieldNames["title"]]=r[fds[this.fieldNames["title"]]];
			}
			return tableData;
		}
		, initComponent: function () {
			this.rulerNums=[];
			this.titleRulerArr=[];
			this.isAdding=null;
			
			Ext.applyDeep(this,this.initialConfig);
			
			this.publisher = Cookies.get('cmpp_cn');
		    this.selected = {};
			if(typeof(this.fields2fields)=='string'){
				this.fields2fields = Ext.decode(this.fields2fields);
			}		
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
			
		    this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
			
			var listPageUrl = this.dataSourceListPage;
			var optionsData = {
				handler:'Ext.getCmp("'+ this.id +'").import',
				scope:'Ext.getCmp("'+ this.id +'")'
			};
			if(listPageUrl.indexOf('?')!=-1){
				listPageUrl+='&optionsData__=' + encodeURIComponent(Ext.encode(optionsData));
			}else{
				listPageUrl+='?optionsData__=' + encodeURIComponent(Ext.encode(optionsData));
			}
		    this.searchWin=new Ext.Window({
				title:this.searchTitle,
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
				html:'<div style="width:100%;height:100%;background:url(../res/img/loading.gif) no-repeat center center;"><iframe id="'+ this.id +'_searchFrame" scrolling="no" frameborder="0" width="100%" height="100%" style="visibility:hidden" _src="'+listPageUrl+'"></iframe></div>' ,
				listeners: {
					close:function(w){
						//修复滚动条才消失的Bug
						w.restore();
					},
					maximize:function(w){  
						//修复位置bug 
						var scrollPos = Ext.getBody().getScroll();		
						w.setPosition(scrollPos.left,scrollPos.top);
					}
				},
				buttons:[{
					text:'关闭',
					scope:this,
					handler:function(){
						this.searchWin.el.fadeOut({
							endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeOut',
							duration: 0.3,
							scope:this,
							callback:function(){
								this.searchWin.hide();
							}
						});
					}
				}]
				
			});
			
			this.innerSearchWin=new Ext.Window({
				title:"搜索已经推送过的数据",
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
				listeners: {
					close:function(w){
						//修复滚动条才消失的Bug
						w.restore();
					},
					maximize:function(w){  
						//修复位置bug 
						var scrollPos = Ext.getBody().getScroll();		
						w.setPosition(scrollPos.left,scrollPos.top);
					}
				},
				buttons:[{
					text:'关闭',
					scope:this,
					handler:function(){
						this.innerSearchWin.el.fadeOut({
							endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeOut',
							duration: 0.3,
							scope:this,
							callback:function(){
								this.innerSearchWin.hide();
							}
						});
					}
				}]
				
			});
			
			//加载必须的Js包 Ext.ux.ListPanel
			if(!Ext.ux.ListPanel){
				this.loadJs(this.resPath + "js/controls/Ext.ux.ListPanel.js","Ext.ux.ListPanel.js");
			}
			
			/************定义历史数据管理单例 begin****************/
			//历史数据管理
			this.historyDataMgr={ 
				store:null,
				tjwStore:null,
				tjwKey:null,
				t:null,
				init:function(container){
					var that = this.historyDataMgr;
					that.t = this;
					that.tjwStore = this.store;
					/*
					var nodeId=(formConfig__&&formConfig__.nodeId)?formConfig__.nodeId:0;
					var formId=(formConfig__&&formConfig__.formId)?formConfig__.formId:0;
					var recordId=(formConfig__&&formConfig__.id)?formConfig__.id:0;
					*/
					var guid = formConfig__.recordData?formConfig__.recordData.GUID:'';
					that.tjwKey = guid + "_" + this.name.replace('xform.','');
					
					var toolbarArr = [{
						xtype:'label',
						style:'margin-left:5px;',
						text:'标题：'
					},{
						xtype:'textfield',
						emptyText:'多关键词之间用空格分割',
						name:'title',
						width:150		
					},{
						xtype:'label',
						style:'margin-left:5px;',
						text:'链接：'
					},{
						xtype:'textfield',
						name:'url',
						emptyText:'请输入完整链接地址:http://',
						width:300		
					},{
						text:'搜索',
						style:'margin-left:5px;',
						scope:this,
						handler:function(){
							this.historyDataMgr.doSearch.apply(this);
						}
					},{
						text:'清空',
						style:'margin-left:5px;',
						scope:that,
						handler:function(){
							this.txtTitle.setValue("");
							this.txtUrl.setValue("");
						}
					}];
					
					//初始化列表页
					var listPanel = new Ext.ux.ListPanel({
						//style:'padding:1px 5px 0px 5px;',
						layout:'fit',
						frame:false,
						header :false,
						gridConfig:{
							autoExpandColumn:0,//第二列自由宽度
							//pagesize:15,//默认为根据高度自动计算
							hasPageBar:false,
							rowHeight:26,//行高
							tbar : toolbarArr, //列表顶部工具栏
							storeConfig:{
								root : "root",
								totalProperty : "totalCount",
								fields: this.tableFields//['id','title','url','createTime','sys_publishDateTime','sys_publisher','img']
							},
							columnConfig:{
								hasRowNumber:true,//是否显示列序号
								hasSelectionModel:false,//是否需要复选框
								colunms:[{//列表项
									header: "标题",//列表栏标头名称
									sortable: false,//是否支持点击排序
									dataIndex: "title",//绑定的字段名
									align:"left",//对齐方式 left center right
									tpl:'<tpl if="url==\'\'">{title}</tpl><tpl if="url!=\'\'"><a href="{url}" target="_blank">{title}</a></tpl>'//模板，参照Ext.XTemplate的语法
								},{
								   header: "...",
								   dataIndex: 'createTime',
								   width:30,//列宽
								   sortable:true,
								   align:"center",
								   tpl:'...',
								   isShowTip:true,
								   //todo
								   tipTpl:"<tpl if=\"sys_ext!=''\"><tpl for=\"Ext.decode(sys_ext)\">{values.createTime}</tpl></tpl>"
								},{
								   header: "预览",
								   dataIndex: 'img',
								   width:40,//列宽
								   sortable:false,
								   align:"center",
								   tpl:'<tpl if="img!=\'\'"><a href="{img}" target="_blank" title="预览">预览</a></tpl>',
								   isShowTip:true,
								   tipTpl:'<tpl if="img!=\'\'"><img src="{img}"/></tpl>'
								},{
								   header: "推送者",
								   dataIndex: 'sys_publisher',
								   width:50,
								   align:"center",
								   tpl:'{sys_publisher}'
								},{
								   header: "推送时间",
								   dataIndex: "sys_publishDateTime",
								   width:125,
								   align:'center',
								   tpl:'{sys_publishDateTime}'
								},{
									header: "置顶",
									id:"btnUp",
									width: 40,
									align:'center',
									tpl:'<div class="btnUp" title="再次推送"></div>'
								}]
							},
							listeners: {
								scope:that,
								cellclick: function (panel, row, cell, e) {
									switch (e.target.className) {
										case 'btnUp': //再次推送
											//t.moveUp(store.data, row, Ext.get(e.target).up(".x-grid3-row").dom)
											this.repush(row);
											break;
									}
								}
							}
						}
					});
					container.add(listPanel);
					container.doLayout();
					that.grid = listPanel.grid;
					that.store = listPanel.grid.store;
					
					that.txtTitle = Ext.getCmp(that.grid.topToolbar.el.query('[name^=title]')[0].id);
					that.txtUrl = Ext.getCmp(that.grid.topToolbar.el.query('[name^=url]')[0].id);
					that.listenerKeybord.apply(this);
				},
				doSearch:function(){
					var that = this.historyDataMgr;
					//获取推荐位自己存储的全部数据
					that.store.clearFilter();
					that.store.loadData(this.metaData);
					
					//搜集搜索条件
					var title = that.txtTitle.getValue();
					var url = that.txtUrl.getValue();
					
					if(url!="") {
						var re = new RegExp(url,["g"]);
						that.store.filter("url",re);
					}
					if(title!="") {
						var re = new RegExp(title,["g"]);
						that.store.filter("title",re);
					}
					//从搜索引擎的历史数据中查询
					var condition=[];
					if(title!=""){
						condition.push({"field":"title","value":title});
					}
					if(url!=""){
						condition.push({"field":"url","value":url.replace(/http:\/\//g,'*/')});
					}
					that.searchFromService.apply(this,[condition]);				
				},
				searchFromService:function(condition){
					condition.push({"field":"RV_tjwKey","value":this.historyDataMgr.tjwKey});
					var retFlds = this.tableFields.slice(0);
					retFlds.push("docId");
					var params={
						coreName:"TUIJIANWEI",
						start:0,limit:30,
						returnFiled:retFlds.join(','),
						condition:Ext.encode(condition),
						sort:'[{"field":"sys_publishDateTime","order":"desc"}]'
					};
					this.innerSearchWin.el.mask("正在搜索...");
					Ext.Ajax.request({  
						url:"../runtime/newSearch!search.jhtml",  
						params:params,  
						method:"POST",  
						scope:this,
						success:function(response,opts){
							this.innerSearchWin.el.unmask();
							try{	
								var ret = Ext.util.JSON.decode(response.responseText);
								if(ret.success===false){
									/*
									Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(ret.message?ret.message:response.responseText),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR 
									});
									*/
									if(console && console.log)
										console.log("搜索出错！");
								}else{
									var that = this.historyDataMgr;
									for(var i=0;i<ret.data.length;i++){
										ret.data[i].id = ret.data[i].docId;
									}
									var data = {root:ret.data};
									that.store.loadData(data,true);
								}
							}catch(ex){
								if(console && console.log)
									console.log(ex);
							}
						},
						failure:function(response,opts){
							this.innerSearchWin.el.unmask();
							Ext.Msg.show({
								   title:'错误提示',
								   msg: response?response.responseText:"也许是网络出问题了",
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.ERROR 
							});
						}		
					});
				},
				repush:function(row){//再推送
					var that = this;
					var record = that.store.getAt(row);
					
					var index=that.tjwStore.find("url",record.get("url"));
					if(index==-1){//add
						that.tjwStore.add(record);
						that.t.moveTop(that.tjwStore,that.tjwStore.getTotalCount());
					}else{
						that.t.moveTop(that.tjwStore,index);
					}
					that.t.mainPanel.selModel.selectRow(0,false);
					that.t.setTitleRuler();
				},
				//绑定键盘事件
				listenerKeybord:function(){
					var that = this.historyDataMgr;
					new Ext.KeyMap(that.txtTitle.el, {
						key: Ext.EventObject.ENTER,
						fn: function(){
							this.historyDataMgr.doSearch.apply(this);
						},
						scope: this
					});	
					new Ext.KeyMap(that.txtUrl.el, {
						key: Ext.EventObject.ENTER,
						fn: function(){
							this.historyDataMgr.doSearch.apply(this);
						},
						scope: this
					});	
				}	
			}
			/***********定义历史数据管理单例 end*****************/
		    Ext.ux.TuiJianWei.superclass.initComponent.call(this);
		}
	    , onRender: function (ct, position) {
	        var t = this;
	        var fdNames=this.fieldNames;
	        var reader = new Ext.data.JsonReader({
	            root: fdNames.root,
	            id: fdNames.id,
	            fields: this.tableFields
				, totalProperty: fdNames.count
	        })
			reader.read =function(response){
				var json = response.responseText;
				var o = eval("("+json+")");
				if(!o) {
					throw {message: "JsonReader.read: Json object not found"};
				}
				t.searchResult=o;
				o=Ext.applyDeep({},o);
				t.parseSearchData(o);
				return this.readRecords(o);
			}
				
			var mainPanel;
	
	        reader = new Ext.data.JsonReader({
	            root: fdNames.root,
	            id: fdNames.id,
	            fields: this.tableFields
	        })
	        
			var ds = this.store = new Ext.data.Store({ 
				reader: reader,
				listeners :{
					scope:this,
					load:function(){						
						this.setTitleRuler();
					},
					datachanged:function(store){
						this.el.dom.value = Ext.encode(this.metaData);
					},
					update :function(store){
						this.el.dom.value = Ext.encode(this.metaData);
					},
					add :function(store){
						this.el.dom.value = Ext.encode(this.metaData);
					},
					remove  :function(store){
						this.el.dom.value = Ext.encode(this.metaData);
					},
					clear:function(store){
						this.el.dom.value = Ext.encode(this.metaData);
					}
				}
			});
	
			var selMod = new Ext.grid.CheckboxSelectionModel();
	        cm = [new Ext.grid.RowNumberer(),selMod,{
	            header: "编辑",
	            dataIndex: fdNames.id,
	            width: 40,
	            renderer: function (v, p, record) {
	                return '<a href="javascript:void(0)" title="编辑">编辑</a>';
	            }
	        }, {
			   header: "标题",
			   dataIndex: fdNames.title,
			   //editor:editor,
			   renderer: function (v, metadata, record) {
					var tip='';
					if(record.data.createTime){
						tip = '创建时间:' + record.data.createTime;
					}else{
						if(record.data.sys_ext){
							try{
								var sysExt = Ext.decode(record.data.sys_ext);
								if(sysExt.createTime){
									tip = '创建时间:' + sysExt.createTime;
								}
							}catch(ex){
							
							}
						}
					}
					if(tip) metadata.attr = ' ext:qtip="' + tip + '"'; 
					
					var iconHtml = "";
					if(record.data.sys_NewAdd){
						iconHtml='<a class="ux-TuiJianWei-newIcon" style="margin-left:5px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>';
					}
				   return '<div class="ruler_title"><a target="_blank" title="'+record.data[fdNames["abstract"]]+'" href="' + record.data[fdNames.url] + '">' + v + '</a>'+iconHtml+'</div>';
			   }
			},{
				header: "...",
			    dataIndex: fdNames.id,
				width:30,
			    align:'center',
				renderer:function (v, metadata, record) {
					var tip='';
					if(record.data.createTime){
						tip = '创建时间:' + record.data.createTime;
					}else{
						if(record.data.sys_ext){
							try{
								var sysExt = Ext.decode(record.data.sys_ext);
								if(sysExt.createTime){
									tip = '创建时间:' + sysExt.createTime;
								}
							}catch(ex){
							
							}
						}
					}
					if(tip) metadata.attr = ' ext:qtip="' + tip + '"'; 
					return "..."
				}
			},{
				header: "预览",
			    dataIndex: fdNames.id,
				width:40,
			    align:'center',
			    renderer:function (v, metadata, record) {
					var imgUrl = record.data["img"];
					if(imgUrl){
						var tip = '&lt;img src=&quot;'+ imgUrl +'&quot;/&gt;';
						metadata.attr = 'ext:qtitle=""' + ' ext:qtip="' + tip + '"'; 
					}
					var s='';
					if(imgUrl){
						s+='<a href="'+ imgUrl +'" target="_blank" title="浏览海报">海报</a>';
					}
					return s;
			    }
			}, {
			   header: "推送者",
			   dataIndex: "sys_publisher",
			   width:50,
			   align:'center'
			},{
			   header: "推送时间",
			   dataIndex: "sys_publishDateTime",
			   width:125,
			   align:'center'
			},{
				header: "置顶",
				dataIndex: fdNames.id,
				width: 40,
				renderer: function (v, p, record) {
					return '<div class="btnUp" title="置顶"></div>';
				}
			},{
	            header: "移除",
	            dataIndex: fdNames.id,
	            width: 40,
	            renderer: function (v, p, record) {
	                return '<div class="btnDelete" title="移除"></div>';
	            }
	        }];
	        this.mainPanel = mainPanel=new Ext.grid.EditorGridPanel({
	        	context:t,
	            renderTo: ct,
	            //title: this.mainTitle,
	            cls: this.mainPanelCls,
				width:this.width||(Ext.isSafari3?ct.getWidth():ct.getWidth()-ct.getPadding("l")-ct.getPadding("r")),//chrome 和 ff 的区别
	            store: ds,
	            columns: cm,
	            sm: selMod,
	            stripeRows :true,//表格行颜色间隔显示
	            autoExpandColumn: 3,
	            autoHeight: true,
				autoScroll:true,
	            enableHdMenu: false,
				dropConfig: {appendOnly:false},
				plugins:(!this.width && Ext.grid.plugins.AutoResize)?new Ext.grid.plugins.AutoResize():null,
				trackMouseOver : true,
				enableDragDrop: true,	
				ddGroup: "GridDD",	
				tbar:[{
					xtype:'label',
					text:this.mainTitle
				},{
					xtype:'tbfill'
				},{
					xtype:'tbbutton',
					text:t.searchBtnTxt,
					scope:t,
					handler:function(){
						this.searchWin.show(null,function(){
							this.searchWin.el.fadeIn({
								endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
								easing: 'easeIn',
								duration: 0.3,
								remove: false,
								useDisplay: false,
								scope:this,
								callback:function(){
									if(this.searchWin.hasIframe===true)return;
									var iframe = Ext.get(this.id + '_searchFrame');
									iframe.on("load",function(e){
										this.searchWin.hasIframe = true;
										Ext.fly(e.target).fadeIn({
											endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
											easing: 'easeIn',
											duration: .3,
											useDisplay: false
										});	
									},this);	
									var src =iframe.getAttributeNS("","_src");
									iframe.dom.src = src; 	
								}
							});
								
						},this);
					}
				},(this.searchHistoryEnable?{
					xtype:'tbbutton',
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
				}:' '),{
					xtype:'tbbutton',
					text:t.addBtnTxt,
					scope:t,
					handler:this.addNew
				}],	
				bbar:[{
					xtype:'tbbutton',
					text:'显示更多︾',
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
					var d=t.getMetaList()[index];					
	            	var row=this.view.getRow(index);
	            	row.rowIndex = index;
	            	var lk=row.getElementsByTagName("a")[0];
	            	
	            	r.data[fdNames.title]=d[fdNames.title]=lk.innerHTML=val[fdNames.title];
	            	r.data[fdNames.url]=d[fdNames.url]=lk.href=val[fdNames.url];
	            	r.data[fdNames["abstract"]]=d[fdNames["abstract"]]=lk.title=val[fdNames["abstract"]];
	            	r.data[fdNames["img"]]=d[fdNames["img"]]=lk.title=val[fdNames["img"]];
					r.data[fdNames["createTime"]]=d[fdNames["createTime"]]=lk.title=val[fdNames["createTime"]];
	            	//this.view.focusCell(ed.row, ed.col);
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
			if(!this.addNewWin){
				this.addNewWin = new Ext.Window({
					"closable": true
					,id:'win_' + this.id
					, closeAction: "hide"
					, "modal": true
					, "buttonAlign": "center"
					, resizable: false
					, layout: "fit"
					,maximizable:false
					,draggable :true
					,width:472
					,height:420	
				}) ;
			}	
			if(!this.frmAddNew){
				/**初始化”自定义添加“窗口***/
				var frmAddNew = new Ext.form.FormPanel({
					labelWidth:60,
					id:'form_' + this.id,
					labelAlign:'right',
					layout:'xform',
					itemCls:"itemStyle5",
					bodyStyle:'padding-top:10px',
					items:[{
						fieldLabel :'标题',
						xtype:'titlefield',
						name:'title',
						width:300,
						allowBlank:false,
						fontSize:this.fontSize,
						rulerNums:this.rulerNums
					},{
						fieldLabel :'链接',
						xtype:'textfield',
						name:'url',
						width:300
					},{
						fieldLabel :'图片',
						xtype:'uploadfield',
						name:'img',
						width:180,
						file_types:"*.jpg;*.JPG;*.gif;*.GIF;;*.png;*.PNG;*.jpeg;*.JPEG",
						file_types_description:"图片文件"
					},{
						fieldLabel :'摘要',
						xtype:'textarea2',
						name:'abstract',
						width:300,
						height:140
					},{
						fieldLabel :'扩展值',
						xtype:'textarea',
						name:'sys_ext',
						width:300,
						height:100
					}],
					buttons:[{
						text:'确定',
						scope:this,
						handler:function(){
							if(this.frmAddNew.form.isValid()){
								this.addNewWin.el.fadeOut({
									endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
									easing: 'easeOut',
									duration: 0.3,
									scope:this,
									callback:function(){
										this.addNewWin.hide();
										var v = this.frmAddNew.form.getValues();
										this.addComplete(v,this.frmAddNew.editRow);
									}
								});
							}
						}
					},{
						text:'关闭',
						scope:this,
						handler:function(obj,e){
							this.addNewWin.el.fadeOut({
								endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
								easing: 'easeOut',
								duration: 0.3,
								scope:this,
								callback:function(){
									this.addNewWin.hide();
								}
							});
						}
					}]	
				});
				

				this.addNewWin.add(frmAddNew);
				this.frmAddNew = frmAddNew;
			}
			/***初始化”自定义添加“窗口 end**/
			
	        Ext.ux.TuiJianWei.superclass.onRender.call(this, ct, position);
	    }
	    , afterRender: function () {
	        Ext.ux.TuiJianWei.superclass.afterRender.call(this);
	        var t = this;
	        if(this.el.up("form")){
				this.el.up("form").on("submit", function () {
					t.getValue();
				});
			}
	    }
	    , setValue: function (v) {
	        var vObj = v;
			if(this.el) this.el.dom.value =v;
	        if(Ext.nore(v)){
	            vObj = {};
	            vObj[this.fieldNames.root] = [];
	            v = Ext.encode(vObj);	        	
	        }
	        if(typeof v=="string"&&!Ext.nore(v)){
	            vObj = Ext.decode(v);
	        }

	        Ext.ux.TuiJianWei.superclass.setValue.call(this, v);

			if(this.store){	
				//只显示前viewSize条数据
				var data = {};
				Ext.applyDeep(data,vObj);
				var rootData = [];
				Ext.apply(rootData,data[this.fieldNames.root]);
				if(rootData.length>this.viewSize){
					rootData = rootData.slice(0, this.viewSize);
				}else{
					this.mainPanel.bbar.hide();
				}
				data[this.fieldNames.root] = rootData;
			   this.store.loadData(data);
			}
			this.metaData = vObj;   		
	    }
	    ,getMetaList:function(){
	    	return this.metaData[this.fieldNames.root];	
	    }
	    , getValue: function () {
	    	if(!this.metaData)this.setValue();
	        this.el.dom.value = Ext.encode(this.metaData);
	        return Ext.ux.TuiJianWei.superclass.getValue.call(this);
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
		}
		//手工添加
	    ,addNew:function(){
	    	this.isAdding=true;
			this.frmAddNew.editRow = -1;
			this.frmAddNew.form.reset();
			
			this.addNewWin.show(null,function(){
				this.el.fadeIn({
					endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
					easing: 'easeIn',
					duration: 0.3,
					remove: false,
					useDisplay: false
				});
			});
	    }
		//编辑内容
		,editRecord:function(store,row){
			var record = store.getAt(row);
			this.frmAddNew.editRow = row;
			this.addNewWin.show();
			this.frmAddNew.form.setValues(record.data);			
		}
	    ,addComplete:function(val,editRow){
	    	var t=this;
			if(editRow==-1){//add
				var row =this.store.find('url',val.url);
				if(row!==-1){//排重 从store里移除存在的数据
					this.getMetaList().splice(row, 1);
					this.store.remove(this.store.getAt(row));
				}
				val["sys_publisher"] = t.publisher;//推送者
				val["sys_publishDateTime"] = (new Date()).format('Y-m-d H:i:s');//推送时间
				var record=new this.store.reader.recordType(val,val[this.fieldNames.id]);
				var meta = Ext.applyDeep({},val);
				record.set("sys_NewAdd",true);
				t.appendData(record,meta);	
				t.mainPanel.selModel.selectRecords(record,true);
				
				this.mainPanel.getView().refresh();	
				this.setTitleRuler();
			}else{
				var record = this.store.getAt(editRow);
				val["sys_publisher"] = record.data.sys_publisher;
				val["sys_publishDateTime"] =record.data.sys_publishDateTime
				Ext.applyDeep(t.metaData.root[editRow],val);
				for(var k in val){
					record.set(k,val[k]);
				}	
				this.mainPanel.getView().refresh();	
				this.setTitleRuler();				
			}			
	    },
		////先点击加入的放在列表的最顶部。获取新插入数据的位置
		_getInsertPosition:function(){
			var pos = 0;
			while(pos!=-1){
				var temp = this.store.find("sys_NewAdd","true",pos);
				if(temp!=-1){
					pos=temp+1;
				}else {
					return pos;
				}
			}
			return pos;
		},
		import: function (dataStr) {
			var dataStr = decodeURIComponent(dataStr);
	        var data = Ext.decode(dataStr);
	        var records = [];
			var meta=[];
        	data = this.convertBaseFieldsName(data);
			
			for(var i=data.length-1;i>=0;i--){//先点击加入的放在列表的最顶部
				var r = new Ext.data.Record(data[i]);
				var row = this.store.find('url',data[i].url);
				if(row!==-1){//排重 从store里移除存在的数据
					this.getMetaList().splice(row, 1);
					this.store.remove(this.store.getAt(row));
				}
				meta.push(Ext.applyDeep({},data[i]));
				records.push(r);
				r.set("sys_NewAdd",true);
			}
			
			var insertPos = this._getInsertPosition();//获取新插入数据的位置
			if(insertPos==-1)insertPos=0;
				for(var i=0;i<meta.length;i++){
				this.metaData.root.splice(insertPos,0,meta[i]);
			}			
			if (records.length > 0) {    	
	            this.store.insert(insertPos,records);
				//选中新添加的
				this.mainPanel.selModel.selectRecords(records,false);
	        }	
		
	        //this.searchWin.hide();
			var maskText = '';
			if (records.length > 0) {
				maskText = "导入成功";
				this.mainPanel.getView().refresh();	
			}/*else{
				maskText = "曾经推送过,重新推送将置顶";
			}*/
			//if(records.length>1 || records.length==0){
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
			//}
			this.setTitleRuler();
			
	    }
	    ,appendData:function(records,mateData){    	
			var insertPos = this._getInsertPosition();//获取新插入数据的位置
			this.metaData[this.rootField].splice(insertPos,0,mateData);		
            this.store.insert(insertPos,records);		
	    }
	    , removeItem: function (store, row) {
			this.getMetaList().splice(row, 1);
	        var cur = store.getAt(row);
	        store.remove(cur);
			this.mainPanel.getView().refresh();
			this.setTitleRuler();
	    }
	    ,exchange:function(gIndex,pIndex,data){
    		var item=data.splice(gIndex,1)[0];
    		data.splice(pIndex,0,item);
	    }
	    , moveUp: function (data, row, curDom) {
	        if (row > 0) {
	            var prev = data.items.splice(row - 1, 1)[0];
	            data.items.splice(row, 0, prev);
	            prev = data.keys.splice(row - 1, 1)[0];
	            data.keys.splice(row, 0, prev);
	            prev = this.getMetaList().splice(row - 1, 1)[0];
	            this.getMetaList().splice(row, 0, prev);
	
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
	    }
		,moveTop:function(store, row, curDom) {
			if (row > 0) {				
				var record = store.getAt(row);
				this.getMetaList().splice(row,1);
				this.getMetaList().unshift(record.data);				
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
		loadJs:function(src,id){
			if (src){
				var oHead = document.getElementsByTagName('HEAD').item(0);
				if(document.getElementById(id))return;
				var oScript = document.createElement( "script" );
				oScript.type = "text/javascript";
				oScript.src=src;
				oScript.id=id;
				oScript.defer = true;
				void(oHead.appendChild(oScript)); 
			}
		}	
	});
	
	Ext.reg("tuijianwei",Ext.ux.TuiJianWei);
	
})();