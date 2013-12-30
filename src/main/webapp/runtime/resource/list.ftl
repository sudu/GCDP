 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>文件夹列表</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../../res/css/runTime.css" />
	<style>
		html, body {
	        font:normal 12px verdana;
	        margin:0;
	        padding:0;
	        border:0 none;
	        height:100%;
	    }		
		a{color:#004A99;}
		a:hover{color:#BA2636;}
		
	    .itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.itemStyle5 {
		    padding:5px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.blank{clear:both;height:18px}
		.blank2{clear:both;height:10px;line-height:0;font-size:0}
		
		/*按钮*/
		.addField{background:url("../../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("../../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.modifyField{background:url("../../res/js/ext2/resources/images/default/my/modify-view.gif") left  no-repeat !important;}
		
		/*****for gridpanel***/
		.x-grid3-cell-inner, .x-grid3-hd-inner {
			overflow: hidden;
			padding: 5px 3px 5px 5px;
			white-space: nowrap;
		}
		td a {
			color:#004A99;
		}
		td a:hover {
			color:#BA2636;
		}
		/*****for gridpanel end*****/
	</style>

 	<script type="text/javascript" src="../../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../../res/js/controls/Ext.ux.ListPanel.js"></script>
	<script type="text/javascript" src="../../res/js/controls/Ext.ux.UploadTool.js"></script>
	<script type="text/javascript">
		var params__ = Ext.parseQuery();
	</script>

	
</head>
<body style="padding:1px;">

<script type="text/javascript">
	openTab = function(url,title){
		if(!title) title='';
		if(top&& top.centerTabPanel){
			var _url = url;
			top.centerTabPanel.addIframe('tab_' + escape(_url),title ,_url);
		}else{
			window.open('../'+url);	
		}
	}
</script>	

<script>
	var listMgr = {
		grid:null,
		store:null,
		init:function(){
			var toolbarArr = [{ 
				text : "上传文件到默认目录", 
				iconCls : 'addField', 
				scope:this,
				handler : function(){
					this.upload.call(listMgr,1);
				}
			},{ 
				text : "自定义目录上传", 
				iconCls : 'addField',  
				scope:this,
				handler : function(){
					this.upload.call(listMgr,2);
				}
			},{ 
				text : "自定义URL上传", 
				iconCls : 'addField',  
				scope:this,
				handler : function(){
					this.upload.call(listMgr,3);
				}
			},{
				xtype:'tbseparator'
			},{ 
				text : "批量删除", 
				iconCls : 'delField', 
				handler : this.deleteRecord 
			},{
				xtype: 'tbfill'
			},{
				xtype:'label',
				text:'目录名：'
			},{
				xtype:'textfield',
				id:'txtSearch',
				emptyText:'输入目录名(支持模糊搜索)',
				width:180		
			},{
				xtype: 'tbbutton',
				cls: 'x-btn-text-icon',
				text:'搜索',
				icon:'../../res/img/search.gif',
				handler:this.doSearch
			}];
			
			//初始化列表页
			this.listPanel = new Ext.ux.ListPanel({
				region:'center',
				id:'placeholder',
				layout:'fit',
				style:'padding:1px 5px 0px 0px;',
				layout:'fit',
				frame:false,
				header :false,
				border:false,
				gridConfig:{
					hasPageBar:true,	//不需要分页
					autoExpandColumn:2,	//第二列自由宽度
					//pagesize:15,		//默认为根据高度自动计算
					rowHeight:27,		//行高
					tbar : toolbarArr, 	//列表顶部工具栏
					storeConfig:{
						url:'../../resourceMgr!listFolders.jhtml',
						remoteSort: false,
						successProperty : 'success',
						totalProperty : "totalCount",
						root : "data",
						fields: ['id','name','size','type','folderPath','distributionPath','author','creationTimeStr','propertyFlag']
					},
					columnConfig:{
						hasRowNumber:false,		//是否显示列序号
						hasSelectionModel:true,	//是否需要复选框
						colunms:[{				//列表项
							header: "ID",		//列表栏标头名称
							sortable: false,		//是否支持点击排序
							dataIndex: "id",	//绑定的字段名
							align:"left",		//对齐方式 left center right
							width:50,			//列宽
							tpl:'{id}'			//模板，参照Ext.XTemplate的语法
						},{
						   header: "目录",
						   width:200,
						   dataIndex: 'folderPath',
						   sortable:true,
						   align:"left",
						   tpl:'{folderPath}'
						},{
						   header: "分发路径",
						   width:400,
						   // dataIndex: 'distributionPath',
						   sortable:true,
						   align:"left",
						   tpl:'<tpl if="propertyFlag==false"><a title="点击查看目录下文件" href="javascript:openTab(\'../runtime/resource/fileList.jhtml?nodeId=' + params__.nodeId + '&folderID={id}&folder={distributionPath}\',\'文件列表\')">{distributionPath}</a></tpl>'
						},{
						   header: "创建者",
						   width:80,
						   dataIndex: 'author',
						   sortable:true,
						   align:"left"
						},{
						   header: "创建时间",
						   dataIndex: 'creationTimeStr',
						   width:130,
						   sortable:true,
						   align:"left"
						},{
						    header: "操作",
						    width:100,
							align:"left",	
							dataIndex:'id',	
							tpl:'<tpl if="propertyFlag==false"><a href="javascript:listMgr.uploadByPath()">上传</a> | <a href="javascript:openTab(\'../runtime/resource/fileList.jhtml?nodeId=' + params__.nodeId + '&folderID={id}&folder={distributionPath}\',\'查看文件列表\')">查看</a> | <a href="javascript:listMgr.deleteRecord()">删除</a></tpl>'
						}]
					}
				}
			});
			
			var mainViewport = new Ext.Viewport({
				layout: 'border',
				items:[this.listPanel]
			});
			this.grid = this.listPanel.grid;
			this.store = this.grid.store;
					
			this.store.baseParams={ //从接口获取参数时传递的参数
				// sort :'[{"field":"id","order":"desc"}]',
				nodeId: params__.nodeId,
			}
			this.store.load({params:{limit:this.grid.pagesize,start:0}});//初次搜索
			this.listenerKeybord();
		},
		
		/* 添加 修改 删除 搜索 */
		addRecord:function(){
			// openTab('fileMgr!customDirUploadPage.jhtml?nodeId=' + params__.nodeId ,'新建文件');
			
		},
		upload:function(mode,path){
			var win = new Ext.Window({
				title:"上传文件" + (path||''),
				width:600, 
				height:480,
				buttonAlign: "center",
				closable:true ,
				closeAction:'close',
				maximizable :false,
				autoScroll:true,
				modal:true,
				//layout:'xform',
				bodyStyle:'padding:10px',
				resizable :true,
				items:[{
					xtype:'uploadtool',
					rootPath:'../../',
					sendfilePrefix:'a',//文件目录的根目录前缀
					uploadMode:mode,
					width:250,
					nodeId:params__.nodeId,
					value:path
				}],
				buttons:[{
					text:"确定",
					handler:function(){
						var win = this.ownerCt;
						win.close();
					}
				}],
				listeners :{
					scope:this,
					close :function(){
						this.store.load();//初次搜索
					}
				}
			});
			win.show();
		},
		
		/* 上传文件 */
		uploadByPath:function(){
			var selItems = listMgr.grid.getSelectionModel().selections.items;
			if(selItems.length>0){
				var path = "http://" + selItems[0].data.distributionPath;
				listMgr.upload.call(listMgr,1,path);
			}			
		},
		
		deleteRecord:function(){
			var selItems = listMgr.grid.getSelectionModel().selections.items;
			if(selItems.length>0){
				Ext.MessageBox.confirm("提示","确定删除吗？", 
					function(button,text){   
						if(button=='yes'){
							var ids=[];
							var props=[];
							for(var i=0;i<selItems.length;i++){
								ids.push(selItems[i].data.id);
								props.push(selItems[i].data.propertyFlag);
							}
							Ext.getBody().mask("正在处理中，请稍候...");
							Ext.Ajax.request({  
								url:'resourceMgr!delete.jhtml?nodeId='+params__.nodeId +'&ids=' + ids.join(',') + '&props=' + props, 
								method:"get", 
								success:function(response,opts){
									Ext.getBody().unmask();
									var ret = Ext.util.JSON.decode(response.responseText);
									if(ret.success){
										/*
										Ext.Toast.show('删除成功',{
											title:'提示',
										    buttons: Ext.Msg.OK,
										    icon: Ext.MessageBox.INFO,  
											time:1000,
											minWidth:420
										});
										listMgr.store.load();
										*/
										if(ret.deletionKey){
											listMgr.showDeleteProcess(ret.deletionKey);
										}
									}else{
										Ext.Msg.show({
										   title:'错误提示',
										   msg: decodeURIComponent(ret.message),
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
									   msg: decodeURIComponent(response.responseText),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR  
									});
								}
							});
						}
					}
				);
			}else{
				Ext.Toast.show('未选择任何记录',{
				   title:'操作提示',
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.WARNING  
				});
			}
		},
		/*
		*显示文件删除进度数据
		*/
		showDeleteProcess:function(deletionKey){
			var win = new Ext.Window({
				title:"正在删除相关文件...",
				width:600, 
				height:480,
				buttonAlign: "center",
				closable:true ,
				closeAction:'close',
				maximizable :false,
				autoScroll:true,
				modal:true,
				//layout:'xform',
				bodyStyle:'padding:10px',
				resizable :true,
				html:"",
				buttons:[{
					text:"确定",
					handler:function(){
						var win = this.ownerCt;
						win.mgr.stopAutoRefresh();	
						win.close();
					}
				}]
			});
			win.show(null,function(){
				var mgr = new Ext.Updater(win.body);
				win.mgr = mgr;
				mgr.startAutoRefresh(3, "../runtime/resourceMgr!fetchUndeletedFiles.jhtml?deletionKey="+deletionKey+"&nodeId=" + params__.nodeId,null,null,true);
				mgr.on("update", function(el,response){
					var tpl = new Ext.XTemplate('<ul>',
											'<tpl for=".">',  
												'<li>{distributionAddress}</li>',  
											'</tpl>',
											'</ul>');
					var ret = Ext.decode(response.responseText);
					if(ret.undeletedFiles)
						tpl.overwrite(el,ret.undeletedFiles||[]);
					else{
						mgr.stopAutoRefresh();	
						el.update("已完成！");
						listMgr.store.load();
					}					
				});
			});
		},
		// 搜索
		doSearch:function(){
			var folderPath = Ext.getCmp('txtSearch').getValue();
			this.store.load({params:{limit:this.grid.pagesize,start:0,folderPath:folderPath}});//搜索
		},
		
		// 搜索 - 绑定键盘事件
		listenerKeybord:function(){
			new Ext.KeyMap(Ext.get('txtSearch'), {
				key: Ext.EventObject.ENTER,
				fn: function(){
					this.doSearch();
				},
				scope: this
			});	
		}
	}

	Ext.onReady(function(){
		Ext.BLANK_IMAGE_URL = "../../res/js/ext2/resources/images/default/s.gif";
		Ext.QuickTips.init();
		Ext.form.Field.prototype.msgTarget = 'qtip';
		listMgr.init();		
	});
	
</script>		
	
</body>
</html>	
