/*
*图集编辑器控件
*author:cici
*date:2012/11/8
*依赖js：controls/Ext.ux.Portal.js ; controls/Ext.ux.ImageCollection.js
*依赖css：css/portal.css
*/

Ext.ux.ImageCollection = function (config) {	
	if(typeof config.domains==="string"){
		config.domains = Ext.decode(config.domains);
	}
	Ext.ux.ImageCollection.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.ImageCollection, Ext.form.Field, {
	/****/
	buttonText:"图集编辑",
	windowTitle:"图集编辑器",
	domains:['y0.ifengimg.com','y1.ifengimg.com','y2.ifengimg.com','y3.ifengimg.com'],
	fileMaxSize:1024000,
	uploadUrl:'../runtime/upload!file.jhtml',
	syncflag:1,//同步上传
	resRoot:'../res/',
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
		Ext.ux.ImageCollection.superclass.initComponent.call(this);
	},
	onRender:function(ct,pos){
		Ext.ux.ImageCollection.superclass.onRender.call(this,ct,pos);	
		
		this.itemsPanel = new Ext.Panel({
			border:false,
			items:[{
				xtype:'button',
				text:this.buttonText,
				handler:function(){
					var field = this.ownerCt.field;
					var value = field.getValue();
										
					if(!field.editorWin){
						var itemTpl = new Ext.XTemplate('<span style="width:100%;height:100%"><div style="float:left;width:200px;height:auto;background:url('+ field.resRoot +'img/loading3.gif) no-repeat center center;"><a title="点击查看原图"  href="{url}" target="_blank"><img width="200" src="{url}"/></a></div><textarea style="font-size:12px;width:330px;height:158px;margin:3px;">{summary}</textarea></span>');
					
						if(value){	
							
							var slide=[];
							var origData = Ext.decode(value);
							var des = origData.description;
							var path = origData.path;
							for(var i=0;i<path.length;i++){
								slide.push({
									"url":path[i],
									"summary":des[i]||""
								});
							}
							var tools = [{
								id:'close',
								handler: function(e, target, panel){
									panel.el.fadeOut({
										endOpacity: 0,
										easing: 'easeOut',
										duration: .3,
										useDisplay: false,
										callback:function(){
											panel.ownerCt.remove(panel, true);
										}
									});	
									
								}
							}];
							
							var items=[];
							for(var i=0;i<slide.length;i++){
								var it = slide[i];
								items.push({
									title: "图片" + (i+1),
									tools:tools,
									html:itemTpl.applyTemplate(it)
								});
							}
						}
							
						var win = new Ext.Window({
							title:this.windowTitle|| ("幻灯中的图片") + "(支持拖拽排序)",
							width:field.width||600,
							height:field.height||600,
							autoScroll:true,
							layout:"fit",
							buttonAlign:"center",
							closeAction:'hide',
							maximizable:true,
							items:[{
								xtype:'portal',
								region:'center',
								margins:'35 5 5 0',
								items:[{
									columnWidth:1,
									style:'padding:10px 10px 10px 10px',
									items:items && items.length>0 ? items:null
								}],
								listeners:{
									"drop":function(){
										//更新序号
										var items = arguments[0].portal.items.items[0].items;
										for(var i=0;i<items.length;i++){
											var p = items.itemAt(i);
											p && p.setTitle("图片" + (i+1));
										}
									}
								}
							}],
							bbar:[{
								text:"删除全部",
								scope:field,
								iconCls :'delField',
								handler:function(){
									var portal = field.editorWin.items.items[0];
									var items = portal.items.items[0].items;
									for(var i=items.length-1;i>=0;i--){
										var panel = items.itemAt(i);
										panel.ownerCt.remove(panel, true);
									}
								}
							},{
								xtype:'tbfill'
							}/*,{
								xtype:'panel',
								border:false,
								frame:true,
								bodyStyle:'padding-left: 18px; background: url("../res/img/topnewedit/icon_add.png") no-repeat scroll 0px -19px transparent;',
								html:'添加图片<input  type="file" style="position: absolute;opacity: 0;cursor: pointer;top: 0;left:-25px;z-index: 100;" size="30" name="fileselect[]" multiple>'
							}*/,{
								text:"添加图片",
								scope:field,
								iconCls :'addField',
								handler:function(){
									field.editorWin.body.child("input[type=file]").dom.click();
								}
							}],
							html:'<input  type="file" style="position: absolute;opacity: 0;cursor: pointer;top: 0;left:-25px;z-index: 100;" size="30" name="fileselect[]" multiple>',
							listeners:{
								'show':function(win){
									var fileInputEl = win.body.child("input[type=file]");
									var portal = win.items.items[0];
									if(!portal.setPortalItemData){
										portal.setPortalItemData = function(p,data){
											var img = p.body.child("img");
											var textarea = p.body.child("textarea");
											var link = p.body.child("a");
											img.dom.src = data.url||"";
											textarea.dom.value = data.summary||"";
											link.dom.href = data.url||"#";
										};
									}
									
									!fileInputEl.hasChangeEvent && fileInputEl.on("change",function(evt){
										//阻止默认事件
										evt.stopPropagation();
										evt.preventDefault();
										//搜集文件
										var files = evt.target.files||[];
										var arrFiles=[];
										for(var i = 0, iLen = files.length; i< iLen; i++)
										{
											var file = files[i];
											if (file.type.indexOf("image") == 0) {
												if (file.size >= win.field.fileMaxSize) {
													Ext.Msg.alert('您这张"'+ file.name +'"图片大小过大('+ (file.size/1000) +'k)，应小于'+ win.field.fileMaxSize/1000 +'k');	
												} else {
													arrFiles.push({file:file});	
												}			
											} else {
												Ext.Msg.alert('文件"' + file.name + '"不是图片。');	
											}
										}
										
										//在图片portal上预览
										
										var length = portal.items.items[0].items.length;
										
										win.files = arrFiles;
										var appendImage = function(index){
											var win = this;
											var currentIndex = index;
											return function(){
												if(win.files.length<=currentIndex) {
													//开始上传
													for (var i = 0, fileJson; fileJson = win.files[i]; i++) {
														console.log("正在上传" + i);
														(function(fileJson) {
															var file = fileJson.file;
															var xhr = new XMLHttpRequest();
															if (xhr.upload) {
																// 上传中
																xhr.upload.addEventListener("progress", function(e) {
																	//self.onProgress(file, e.loaded, e.total);
																}, false);
													
																// 文件上传成功或是失败
																xhr.onreadystatechange = function(e) {
																	if (xhr.readyState == 4) {
																		if (xhr.status == 200) {
																			//成功 xhr.responseText
																			var ret = Ext.decode(xhr.responseText);
																			if(ret.success){
																				var url = ret.message;
																				portal.setPortalItemData(fileJson.portlet,{url:url});
																			}
																		} else {
																			console.log("失败");
																		}
																	}
																};
																// 开始上传
																xhr.open("POST", win.field.uploadUrl, true);
																//使用FormData处理需要上传的数据
																var oParam={
																	domain:win.field.domains[parseInt(Math.random()*win.field.domains.length)],
																	syncflag:win.field.syncflag,//同步
																	filedataFileName:file.name
																}
																var fd = new FormData();
																for( var j in oParam) {
																	fd.append(j,oParam[j]);
																}
																fd.append("filedata", file);
																xhr.send(fd);
															}	
														})(fileJson);	
													}
													
													return;
												}
												var p = portal.items.itemAt(0).add({
													title: "图片" + (portal.items.itemAt(0).items.length+1),
													tools:tools,
													html:win.portletTpl.applyTemplate({url:"",summary:''})
												});
												win.files[currentIndex].portlet = p;
												portal.doLayout();
												portal.body.dom.scrollTop = portal.body.dom.scrollHeight;
												appendImage.call(win,currentIndex+1)();
												
											}
										}
										appendImage.call(win,0)();
										
									},win);
									fileInputEl.hasChangeEvent = true;
								}
							},
							buttons:[{
								text:"确定",
								handler:function(){
									var portal = this.ownerCt.items.items[0];
									if(!portal.getPortalItemData){
										portal.getPortalItemData = function(p){
											var img = p.body.child("img");
											var textarea = p.body.child("textarea");
											return {
												url:img.dom.src,
												summary:textarea.dom.value
											};
										};
									}
									//搜集数据
									
									var items = portal.items.items[0].items;
									var dataJson = {description:[],path:[]};
									for(var i=0;i<items.length;i++){
										var p = items.itemAt(i);
										var itemData = portal.getPortalItemData(p);
										dataJson.description.push(itemData.summary);
										dataJson.path.push(itemData.url);
									}
									var field = this.ownerCt.field;
									field.setValue(Ext.encode(dataJson));
									var win=this.ownerCt;
									win.el.fadeOut({
										endOpacity: 0,
										easing: 'easeOut',
										duration: .3,
										useDisplay: false,
										callback:function(){
											win.hide();
										}
									});	
								}
							},{
								text:"取消",
								scope:field,
								handler:function(){
									var win=this.editorWin;
									win.el.fadeOut({
										endOpacity: 0,
										easing: 'easeOut',
										duration: .3,
										useDisplay: false,
										callback:function(){
											win.close();
										}
									});	
									this.editorWin = null;
								}
							},{
								text:"关闭",
								scope:field,
								handler:function(){
									var win=this.editorWin;
									win.el.fadeOut({
										endOpacity: 0,
										easing: 'easeOut',
										duration: .3,
										useDisplay: false,
										callback:function(){
											win.hide();
										}
									});	
								}
							}]
						});
						win.portletTpl = itemTpl;
						field.editorWin = win;
						field.editorWin.field = field;
					}
					field.editorWin.show(null,function(){
						var win = this;
						win.el.fadeIn({
							endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
							easing: 'easeIn',
							duration: .3,
							useDisplay: false
						});	
					});
					field.editorWin.center();
				}
			}]
		
		});
		this.itemsPanel.render(ct);
		this.itemsPanel.field = this;
	},	
	setValue:function(v){
		Ext.ux.ImageCollection.superclass.setValue.call(this,v);
	}
});
Ext.reg('ImageCollection', Ext.ux.ImageCollection);
