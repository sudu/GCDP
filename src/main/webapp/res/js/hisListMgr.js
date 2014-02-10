/*
历史记录管理。包括获取历史记录列表、回滚
*/
//历史记录列表
var hisListMgr = {
	pageSize:15,
	grid:null,
	column:null,
	versionKey:"",
	assistUrl:"",
	formPanel:null,
	pagerBar:null,
	formDataCache:{},
	store:null,
	init:function(){
		this.store = new Ext.data.Store({ 
			proxy : new Ext.data.HttpProxy({url : this.assistUrl + 'version!list.jhtml',method:'post'}), 
			baseParams :{
				'version.key':this.versionKey,
				limit:15
			},
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				root : "result",
				totalProperty : "totalCount",
				fields: ['id','key','lastmodifyStr','username','timestamp']
			}),
			remoteSort: false
		});
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
			displayMsg : '{0}-{1}/{2}',
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
			renderTo:'placeholder_his',
			stripeRows: true,//隔行换色
			loadMask:{ 
				msg:"数据正在加载中...." 
			}, 
			columnLines: true,//显示列线条
			plugins:Ext.grid.plugins.AutoResize?new Ext.grid.plugins.AutoResize():null,
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
			autoExpandColumn :'lastmodify',
			height:this.datagridPanel_his.body.getHeight(),

			iconCls:'icon-grid',
			frame:false,
			bbar : hisListMgr.pagerBar
		}); 
		this.grid = grid;
	},
	initColumn:function(){
		var cm = new Ext.grid.ColumnModel([{
				id:'lastmodify',
			   header: "更新时间",
			   dataIndex: 'lastmodifyStr',
			   menuDisabled: true,
			   sortable:true
			},{
			    header: "操作人",
			    dataIndex: 'username',
			    menuDisabled: true,
				sortable:true,
			    
			},{
			   header: "回滚",
			   menuDisabled: true,
			   sortable:true,
			   dataIndex: 'key',
			   width: 80,
			   align:'center',
			   renderer:function(exttpl){
					var tplStr = exttpl;
					return function(value, p, record){
						var tpl = new Ext.XTemplate(tplStr);
						return tpl.applyTemplate(record.data);
					}
			   }('<a href ="javascript:hisListMgr.rollback(\'{key}\',{timestamp})">回滚</a>')
			}
		]);
		this.column = cm;
	},
	reSetValues:function(cfg, fromCache, key){
		if(this.formPanel != null){
			// 兼容以前场景
			if(!fromCache){
				for(var name in cfg){
					cfg['xform.' + name] = cfg[name];
					delete cfg[name];
				}
				this.formDataCache[key] = cfg;
			}
			this.formPanel.form.setValues(cfg);
		}else if( typeof this.btnViewHistory !== 'undefined' && typeof this.reSetFormPanels === 'function'){
			// 针对按钮场景
			if(!fromCache){
				this.formDataCache[key] = cfg;
			}
			this.reSetFormPanels(cfg);
		}
	},
	rollback:function(key,timestamp){
		if(hisListMgr.formDataCache[key+timestamp]){
			this.reSetValues(hisListMgr.formDataCache[key+timestamp], true);
		}else{
			Ext.getBody().mask("正在回滚......");
			Ext.Ajax.request({  
				url:this.assistUrl + 'version!getFormDatabyKey.jhtml?version.key='+ key +'&timespan=' + timestamp,  
				method:'get',	
				options:{key:key+timestamp},
				success:function(response,opts){
					Ext.getBody().unmask();
					try{
						var cfg = Ext.util.JSON.decode(response.responseText);
						hisListMgr.reSetValues(cfg, false, opts.options.key);
					}catch(ex){
						Ext.Msg.show({
							title:'错误提示',
							msg: '回滚数据失败',
							buttons: Ext.Msg.OK,
							animEl: 'elId',
							minWidth:420,
							icon: Ext.MessageBox.ERROR 
						});
					}
					
				},
				failure:function(ret,opts){
					Ext.getBody().unmask();
					Ext.Msg.show({
						title:'错误提示',
						msg: '获取回滚数据失败',
						buttons: Ext.Msg.OK,
						animEl: 'elId',
						minWidth:420,
						icon: Ext.MessageBox.ERROR 
					});
				}
			});
		}
		hisListMgr.historyWin.hide();
	},
	//对于按钮场景的初始化操作接口
	initHistory:function(config){
		// check valid
		config = config || {};
		if( typeof config.btnViewHistory === 'undefined' || typeof config.reSetFormPanels !== 'function'){
			return;
		}
		
		// override properties
		Ext.apply(this, config);
		
		// banding btn handler
		var _self = this;
		this.btnViewHistory.handler = function(obj){
			if(!_self.historyWin){
				_self.historyWin = new Ext.Window({
					title:'历史记录',
					height:465,
					width:400, 
					buttonAlign: "center",
					closable:true ,
					closeAction:'hide',
					autoScroll:true,
					modal:false,
					layout:'fit',
					resizable :false,
					items:[{
						xtype:'panel',
						layout:'anchor',
						frame:false,
						header :false,
						border:false,
						id:'datagridPanel_his',
						items:[{
							xtype:'panel',
							anchor:'100%',
							autoHeight:true,
							frame:false,
							border:false,
							header :false,		
							id:'placeholder_his'
						}]
					}],
					defaultButton:'btnHisClose',
					buttons:[{
						scope:this,
						text:'关闭',
						id:'btnHisClose',
						handler:function(){
							_self.historyWin.hide(this.el);
						}
					}]
				});
				_self.historyWin.show(this.el,function(){
					_self.datagridPanel_his = Ext.getCmp('datagridPanel_his');
					_self.init();
					_self.grid.render();	
				});
			}else{
				_self.historyWin.center();
				_self.historyWin.show(this.el);
			}
		};
	}

}

//初始化历史记录
function initHistory(){
	//添加回滚按钮
	if(!hisListMgr.formPanel) return;

	var bbar = hisListMgr.formPanel.getBottomToolbar();
	bbar.add({
		text:'历史记录>>',
		handler:function(obj){
			if(!hisListMgr.historyWin){
				hisListMgr.historyWin = new Ext.Window({
					title:'历史记录',
					height:465,
					width:400, 
					buttonAlign: "center",
					closable:true ,
					closeAction:'hide',
					autoScroll:true,
					modal:false,
					layout:'fit',
					resizable :false,
					items:[{
						xtype:'panel',
						layout:'anchor',
						frame:false,
						header :false,
						border:false,
						id:'datagridPanel_his',
						items:[{
							xtype:'panel',
							anchor:'100%',
							autoHeight:true,
							frame:false,
							border:false,
							header :false,		
							id:'placeholder_his'
						}]
					}],
					defaultButton:'btnHisClose',
					buttons:[{
						scope:this,
						text:'关闭',
						id:'btnHisClose',
						handler:function(){
							hisListMgr.historyWin.hide(this.el);
						}
					}]
				});
				hisListMgr.historyWin.show(this.el,function(){
					hisListMgr.datagridPanel_his = Ext.getCmp('datagridPanel_his');
					hisListMgr.init();
					hisListMgr.grid.render();	
				});
			}else{
				hisListMgr.historyWin.center();
				hisListMgr.historyWin.show(this.el);
			}
		}
	});
	hisListMgr.formPanel.ownerCt.doLayout();

};
