<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>新建文件</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	
	<link rel="stylesheet" type="text/css" href="../res/css/Ext.ux.TextAreaPretty.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/prettify.css" />
	
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
 	
 	<script type="text/javascript" src="../res/js/ext2/ext-base-debug.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="../res/js/ext_base_extension.js"></script>	
	
	<script type="text/javascript">
		var params__ = Ext.parseQuery();
	</script>
	<script type="text/javascript">
		function closePage(){
			if(top && top.centerTabPanel){
				var tab = top.centerTabPanel.getActiveTab();
				top.centerTabPanel.remove(tab);
			}else{
				window.close();
			}
		}
	</script>

</head>
<body>
	<script>
		// newFolderMgr
		var newFolderMgr={
			postUrl:'folderMgr!newFolder.jhtml',
			deleteUrl:'interface_mgr!delete.jhtml',
			formId :"folderForm",
			init:function(){
				// 初始化动作
			},
			save:function(){
				Ext.getBody().mask("正在提交中...");
				var form 	= Ext.getCmp(this.formId).form;
				var params 	= form.getFieldValues(true);
				
				params["nodeId"]	= params__.nodeId;
				params["parentID"] 	= params__.parentID;
				
				Ext.Ajax.request({  
					url:this.postUrl,
					method:"post",
					params:params,
					success:function(response,opts){
						Ext.getBody().unmask();
						var ret = Ext.util.JSON.decode(response.responseText);
						// 文件夹已存在
						if (ret.folderExists) 
						{
							Ext.CMPP.warn('', decodeURIComponent(ret.message));
							return;
						}
						// 文件夹不存在
						if(ret.success){
							Ext.CMPP.alert("提示","提交成功",{
								options:{folderExists:ret.folderExists},
								callback:function(){
									/*
									var redirectUrl = "interface_mgr!view.jhtml?nodeId="+ globalvars.nodeId +"&interf.id=" + ret.id;
									location.href = redirectUrl;
									*/
									
									closePage();
									
									// this.openPage('../runtime/resource/list.jhtml?nodeId=' + params__.nodeId,false,'文件夹列表');
									
								}
							});
						}else{
							Ext.CMPP.warn('提交不成功',decodeURIComponent(ret.message));
						}
					},
					failure:function(response,opts){
						Ext.getBody().unmask();
						Ext.CMPP.warn('提交出错',decodeURIComponent(response.responseText));
					}
				});
			},
			
			delete:function(){
				Ext.MessageBox.confirm("提示","确定删除吗？", function(button,text){   
					if(button=='yes'){
						Ext.getBody().mask("正在删除...");
						Ext.Ajax.request({  
							url:this.deleteUrl,
							method:"post",
							params:{"nodeId":globalvars.nodeId,"interf.id":globalvars.id},
							success:function(response,opts){
								Ext.getBody().unmask();
								var ret = Ext.util.JSON.decode(response.responseText);
								if(ret.success){
									Ext.CMPP.alert("提示","删除成功",{
										callback:function(){
											closePage();
										}
									});	
								}else{
									Ext.CMPP.warn('删除不成功',decodeURIComponent(ret.message));
								}
							},
							failure:function(response,opts){
								Ext.getBody().unmask();
								Ext.CMPP.warn('操作出错',decodeURIComponent(response.responseText));
							}
						});
					}
				},newFolderMgr);
			}
		};
		
		
		Ext.onReady(function(){
			Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
			Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
			Ext.QuickTips.init();
			Ext.form.Field.prototype.msgTarget = 'qtip';
			
			// 构建页面 
			new Ext.Viewport({
				layout:'fit',
				items:[{
					xtype:'panel',
					layout:"fit",
					frame:false,
					buttonAlign:'center',
					border:false,
					items:[{
						xtype:'form',
						id:'folderForm',
						autoScroll:true,
						labelAlign:'right',
						layout:'xform2',
						itemCls:"itemStyle5",
						frame:false,
						border:true,
						style:"background:#fff;",
						items:[{
							fieldLabel:"选择域名",
							name:"domain",
							xtype:"combo",
							triggerAction: 'all',
							editable:false,
							width:300,
							mode: 'local',
							store:new Ext.data.SimpleStore({　
							　　fields:['value','text'],　
							　　data:[
									[0,"y0.ifengimg.com"],
									[1,"y1.ifengimg.com"],
									[2,"y2.ifengimg.com"],
									[3,"y3.ifengimg.com"]
								]
							}),
							valueField : 'value', 	//值
							displayField : 'text',	//显示文本
						},{
							fieldLabel:"文件夹路径",
							id:"folderPath",
							name:"folderPath",
							xtype:"textfield",
							width:300
						},{
							fieldLabel:"文件夹名",
							name:"folderName",
							xtype:"textfield",
							width:300
						}],
						buttons:[{
							text:'保存',
							handler:function(){
								newFolderMgr.save();
							}
						},{
							text:'重置',
							handler:function(){
								newFolderMgr.delete();
							}
						}]
					}]
				}]
				
			});
			
		});
		
	</script>

</body>
</html>