 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>文件列表</title>
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
		openTab = function(url,title){
			if(!title) title='';
			if(top&& top.centerTabPanel){
				var _url = url;
				
				// alert(params__);
				
				top.centerTabPanel.addIframe('tab_' + escape(_url),title ,_url);
			}else{
				window.open('../'+url);	
			}
		}
	</script>	
	<script type="text/javascript">
		var params__ = Ext.parseQuery();
		var globalvars = {
			nodeId:params__.nodeId,
			folderID:params__.folderID,
			folder:'http://' + params__.folder
		};
		//复制功能相关
		var _clipDataForCopy;
		var CLIPBOARDSWFID="CLIPBOARDSWFID-XUAS";
	</script>	
	
</head>
<body style="padding:1px;">


<script>
	var listMgr = {
		grid:null,
		store:null,
		init:function(){
			//加载clipBoard.swf
			this.loadClipBoardSwf(16,16);
		
			var toolbarArr = [{ 
				text : "上传文件", 
				iconCls : 'addField', 
				handler : this.addRecord 
			},{ 
				text : "批量删除", 
				iconCls : 'delField', 
				handler : this.deleteRecord 
	
			},{
				xtype: 'tbfill'
			},{
				xtype:'label',
				text:'文件名：'
			},{
				xtype:'textfield',
				id:'txtSearch',
				emptyText:'输入文件名(支持模糊搜索)',
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
				//style:'padding:1px 5px 0px 5px;',
				id:'placeholder',
				layout:'fit',
				frame:false,
				header :false,
				gridConfig:{
					hasPageBar:false,	//不需要分页
					autoExpandColumn:1,	//第二列自由宽度
					//pagesize:15,		//默认为根据高度自动计算
					rowHeight:26,		//行高
					tbar : toolbarArr, 	//列表顶部工具栏
					storeConfig:{
						url:'resourceMgr!listFiles.jhtml',
						remoteSort: false,
						successProperty : 'success',
						root : "data",
						fields: ['id','parentID','size','name','desc','type','distributionAddress','author','creationTimeStr','propertyFlag','result']
					},
					columnConfig:{
						hasRowNumber:false,		//是否显示列序号
						hasSelectionModel:true,	//是否需要复选框
						colunms:[{
						   header: "文件ID",
						   dataIndex: 'id',
						   align:"left",
						   width:50,
						   tpl:'{id}'
						},{				//列表项
							header: "文件名",		//列表栏标头名称
							sortable: true,		//是否支持点击排序
							dataIndex: "name",	//绑定的字段名
							align:"left",		//对齐方式 left center right
							width:180,			//列宽
							tpl:'<a href="{distributionAddress}" title="{name}" target="_blank">{name}</a>'			//模板，参照Ext.XTemplate的语法
						},{
						   header: "文件描述",
						   dataIndex: 'desc',
						   align:"left",
						   width:80
						},{
						   header: "文件地址",
						   dataIndex: 'distributionAddress',
						   sortable:true,
						   align:"left",
						   width:360,
						   tpl:'<tpl if="result===-1">超时</tpl><tpl if="result===0">上传失败</tpl><tpl if="result===1"><progress value="{percentComplete}" max="100"></progress> </tpl><tpl if=" ((result==2) || (result!==-1 && result!==0 && result!==1)) "><input onmouseover="javascript:this.select();" class="UploadTool-list-ct-url-input" type="text" value="{distributionAddress}"/><div class="UploadTool-list-ct-btn-copy" title="复制地址" onmousedown="javascript:listMgr.doCopy.call(listMgr,event,\'{distributionAddress}\');"></div></tpl>'
						},{
						   header: "类型",
						   dataIndex: 'type',
						   align:"left",
						   width:30,
						   tpl:'{type}'
						},{
						   header: "大小",
						   dataIndex: 'size',
						   width:80,//列宽
						   sortable:false,
						   align:"left",
						   tpl:'{[Ext.util.Format.fileSize(values.size)]}'
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
							width: 140,
							align:'center',
							tpl:'<tpl if="type==\'.txt\' || type==\'.css\' || type==\'.js\' || type==\'.html\' || type==\'.htm\'"><a href="javascript:listMgr.editFileContent(\'{distributionAddress}\', \'{id}\', \'{desc}\')">编辑内容</a></tpl><tpl if="type==\'.txt\' || type==\'.css\' || type==\'.js\' || type==\'.html\' || type==\'.htm\'"> | </tpl><tpl if="propertyFlag==true"><a href="javascript:listMgr.reupload(\'{distributionAddress}\',1)">重传</a> | <a href="javascript:listMgr.deleteRecord()">删除</a></tpl>'
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
				nodeId: globalvars.nodeId,
				folderID:globalvars.folderID
			}
			this.store.load();//初次搜索
			this.listenerKeybord();
			
			uploadHelper.init();
		},
		
		/* 添加 修改 删除 搜索 */
		addRecord:function(){
			listMgr.upload(1,globalvars.folder);
		},
		
		/* 编辑文件 */
		editFileContent:function(distributionAddress, fileID, desc){
			Ext.getBody().mask("正在处理中，请稍候...");
			Ext.Ajax.request({
				url:'resourceMgr!editFileContent.jhtml?distributionAddress=' + distributionAddress,
				method:"post",
				success:function(response,opts){
					Ext.getBody().unmask();
					var ret = Ext.util.JSON.decode(response.responseText);
					if(ret.success){
						if(ret.message){
							// 打开修改面板 -- 编辑文件页面
							listMgr.editFileContentHandler(ret.message, fileID, distributionAddress, desc);
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
			});
		},
		
		editFileContentHandler:function(fileContent, fileID, distributionAddress, desc){
			var fileDesc = desc;
			// 创建编辑面板
			var editpannel = new Ext.form.FormPanel({
				method:'POST',
				fileUpload: true,
				labelAlign: 'center',
				frame: true,
				dataId:0,
				autoHeight:true,
				url: 'resourceMgr!saveSend.jhtml', // 修改
				items:[
					{
						 fieldLabel:"文件描述",
						 xtype:"textfield",
						 validateOnBlur:false, 
						 id:"fileDesc",
						 name:"fileDesc", // 文件描述
						 value:fileDesc,  
						 width:470
					},{
						fieldLabel:"文件内容",
						name:"fileContent",
						xtype:"textarea",
						value:fileContent, // 文件内容
						width:400,
						anchor:"100%",
						height:200	
					}
				]
			});
			
			// 面板容器
			var dialog = new Ext.Window({
				width:600,
				height:500,
				title:'修改文件内容',
				autoScroll:true,
				modal:true,
				closeAction :'close',
				items: [editpannel],
				buttonAlign:'center',
				buttons: [{
					text: '确定',
					handler:function()
					{
						var form = editpannel.form;  
						var params = form.getValues(); //获取表单数据
						params.nodeId = globalvars.nodeId;
						params.fileID = fileID;
						params.distributionAddress = distributionAddress;
						Ext.getBody().mask("正在提交保存...");
						Ext.Ajax.request({ 
							url:editpannel.url,  
							method:'POST',                        
							params:params,
							success:function(response,opts){
								Ext.getBody().unmask();
								var ret = Ext.util.JSON.decode(response.responseText);
								if(ret.success){
									// 再次发出查询请求，讲所有文件列表出来
									params.folderID = globalvars.folderID;
									Ext.Ajax.request({ 
										url:'resourceMgr!listFiles.jhtml',  
										method:'POST',                        
										params:params,
										success:function(response,opts){
											Ext.getBody().unmask();
											var ret = Ext.util.JSON.decode(response.responseText);
											/*
											if(ret.success){
											    listMgr.store.load();
											    alert('success');
												dialog.close(); 
											}
											*/
											listMgr.grid.store.load();
											dialog.close(); 
										},
										failure:function(response,opts){
											Ext.getBody().unmask();
											Ext.Msg.show({
											   title:'错误提示',
											   msg: "出错！请重试",
											   buttons: Ext.Msg.OK,
											   animEl: 'elId',
											   minWidth:420,
											   icon: Ext.MessageBox.ERROR  
											});
										}
									});  
									
									// 关闭文件编辑窗体									
									// dialog.close(); 
									// var win = this.ownerCt;
									// win.close();
								}
							},
							failure:function(response,opts){
								Ext.getBody().unmask();
								Ext.Msg.show({
								   title:'错误提示',
								   msg: "出错！请重试",
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.ERROR  
								});
							}
						});  		            
					}
				},{
					text: '取消',
					handler: function() { 
						dialog.close();
					}.createDelegate(this)
				}]
			});
			
			dialog.show();
		},
		
		/* 重传 */
		upload:function(mode,path){
			var win = new Ext.Window({
				title:"上传文件" + path,
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
					nodeId:globalvars.nodeId,
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
		reupload:function(path){
			uploadHelper.fileInputEl.dom.click();
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
								url:'resourceMgr!delete.jhtml?nodeId='+globalvars.nodeId +'&ids=' + ids.join(',') + '&props=' + props, 
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
									   msg: "网络错误",
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
			var name = Ext.getCmp('txtSearch').getValue();
			var reg = eval("/"+name+"/ig");
			listMgr.store.filter("name", reg);
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
		},
		/*
		*加载复制到剪切板flash
		*/
		clipSwfDiv:null,
		loadClipBoardSwf:function(width,height){
			this.clipSwfDiv = Ext.get(CLIPBOARDSWFID);
			if(!this.clipSwfDiv ){
				this.clipSwfDiv  = Ext.getBody().createChild({
					tag:'div',
					id:CLIPBOARDSWFID,
					style:"position:absolute;left:-100px;top:-100px;z-index:0",
					html:'<embed src="../../res/swf/clipBorad-Xuas.swf" name="clipSwf" type="application/x-shockwave-flash" width="'+width+'" height="'+height+'" wmode="transparent" allowScriptAccess="always"></embed>'
				});
			}
			this.clipSwfDiv.on('mouseup',function(){
				this.clipSwfDiv.position("absolute",1,-100,-100);			
			},this);
			return this.clipSwfDiv ;
		},
		doCopy:function(event,value){
			
			var obj = Ext.fly(event.target);
			window._clipDataForCopy = value;
			this.clipSwfDiv.position("absolute",999999999,event.clientX-8,event.clientY-8);
			//this.clipSwfDiv.setSize(obj.getWidth(),obj.getHeight());
			//this.clipSwfDiv.first().setSize(obj.getWidth(),obj.getHeight());
			
			var maskEl = this.grid.tbar.mask("成功复制");
			maskEl.fadeOut({
				endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
				easing: 'easeOut',
				duration: 0.5,
				remove: true,
				useDisplay: true,
				scope:this.grid.tbar,
				callback:function(){
					this.unmask();
				}
			});
			
		}
	};
	
	var uploadHelper= {
		fileInputEl:null,
		listMgr:null,
		uploadUrl2:'../../runtime/fileMgr!customDirUploadFile.jhtml',
		init:function(){
			this.listMgr = listMgr;
			this.fileInputEl = Ext.getBody().insertHtml('beforeEnd','<input type="file" style="position: absolute;opacity: 0;cursor: pointer;top: -1000px;left:-1000px;z-index: 100;" size="30" name="fileselect[]">',true);
			this.fileInputEl.on("change",this.fileUploadHandler,this);
		},
		fileUploadHandler:function(e,obj,opts){
			//阻止默认事件
			e.stopPropagation();
			e.preventDefault();

			//搜集文件
			var files = e.target.files||[];
			var file = files[0];
			if (file.size >= this.fileMaxSize) {	
				Ext.CMPP.warn('提示','文件"'+ file.name +'"大小过大('+ (file.size/1024) +'k)，应小于'+ this.fileMaxSize/1024 +'k');
				return false;
			} else {
				if(file.name.indexOf(' ')!=-1){
					Ext.CMPP.warn('提示','文件"'+ file.name +'"文件名不能包含空格');
					return false;
				}
			}	
			var selRecord = this.listMgr.grid.getSelectionModel().selections.items[0];
			if(!this.uploadStart(selRecord,file)) return ;
			(function(fileJson) {
				var t = fileJson.context;
				var file = fileJson.file;
				var record = fileJson.record;
				var xhr = new XMLHttpRequest();
				xhr.timeout=2*60*1000;
				if (xhr.upload) {
					// 上传中
					xhr.ontimeout = t.uploadTimeout.createDelegate(t,[xhr,fileJson],true);
					xhr.upload.onprogress = t.updateProgress.createDelegate(t,[xhr,fileJson],true);
					xhr.onreadystatechange=t.uploadComplete.createDelegate(t,[xhr,fileJson],true);
					// 开始上传
					xhr.open("POST", t.uploadUrl2, true);
					//使用FormData处理需要上传的数据
					var oParam=t.setParams(file,record);
					var fd = new FormData();
					for( var j in oParam) {
						fd.append(j,oParam[j]);
					}
					fd.append("filedata", file);
					xhr.send(fd);
				}	
			})({context:this,file:file,record:selRecord});

		},
		setParams:function(file,r){
			var params = {};
			var parseUrl = function(url){
				url = url.replace('http://','');
				var pos = url.indexOf('/'),pos1 = url.lastIndexOf('.'),pos2 = url.lastIndexOf('/');
				var domain = url.substring(0,pos);
				if(pos1==-1) 
					var path = url.substring(pos);
				else {
					var path = url.substring(pos,pos2+1);
					var fileName = url.substring(pos2+1);
				}
				return {domain:domain,folderPath:path,fileName:fileName};
			}
			Ext.apply(params,parseUrl(r.get("distributionAddress")));
			params.syncflag = 1;
			params.nodeId = globalvars.nodeId;
			params.reupload = 1;
			return params;
		},
		uploadStart:function(selRecord,file){
			var f = file;
			var r = selRecord;
			
			r.set('size',f.size);
			r.set('result',1);	
	
			return true;
		},
		updateProgress:function(e,xhr,fileJson){
			if (e.lengthComputable) {
				var percentComplete = e.loaded / e.total *100;
				var f = fileJson.file;
				var r = fileJson.record;
				r && r.set('percentComplete',percentComplete);
			}
		},
		uploadTimeout:function(e,xhr,fileJson){
			var f = fileJson.file;
			var r = fileJson.record;
			r && r.set('result',-1);

		},
		uploadComplete:function(e,xhr,fileJson){
			if (xhr.readyState == 4) {
				var url=fileJson.targetUrl;
				var result=0;
				if (xhr.status == 200) {
					//成功 xhr.responseText
					var ret = Ext.decode(xhr.responseText);
					if(ret.success){
						url = ret.message;
						result = 2;
					}else{
						if(ret.fileExists){
							result=-2;//文件已存在
						}
					}
				} else {
					result = 0;//失败
				}
				var f = fileJson.file;
				var r = fileJson.record;
				if(r){
					url && r.set('distributionAddress',url);
					r.set('result',result);
				}
			}
		}
	
		
	};
	
	Ext.onReady(function(){
		Ext.BLANK_IMAGE_URL = "../../res/js/ext2/resources/images/default/s.gif";
		Ext.QuickTips.init();
		Ext.form.Field.prototype.msgTarget = 'qtip';

		listMgr.init();		
	});
	
</script>		
	
</body>
</html>	
