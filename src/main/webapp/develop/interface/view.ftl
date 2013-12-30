<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>接口-${interf.name!"新建"}</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/Ext.ux.TextAreaPretty.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/prettify.css" />
	
 	<!--script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script-->
 	<script type="text/javascript" src="../res/js/ext2/ext-base-debug.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="../res/js/ext_base_extension.js?20131022"></script>	
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TextAreaPretty.js" ></script>
	<script type="text/javascript" src="../res/js/lib/prettify.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.JSONEditor.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.ScriptEditor.js?20130927"></script>
	<script type="text/javascript" src="../res/js/lib/editArea/edit_area_loader.js?20130927"></script>
<style>
body {font-size:12px;}	
input[readonly=""] {
    background: none repeat scroll 0 0 transparent;
    border-color: #DDDDDD;
	border-style:inset;
}

.itemStyle {
    margin-bottom: 0;
    padding: 8px;
}
.itemStyle5 {
    margin-bottom: 0;
    padding: 5px;
}
.x-panel-footer{background:url("../res/js/ext2/resources/images/default/toolbar/bg.gif")}
</style>
<script>
	var globalvars={
		nodeId:#{nodeId!0},
		id:#{interf.id!0}<#if interf.id??>,
		interf:{
			id:#{interf.id!0},
			name:"${(interf.name!"")?js_string}",
			createTime:"${interf.createdatestr!""}",
			creator:"${interf.creator!""}"
		}</#if>
	};
</script>

<script>
function closePage(){
	if(top && top.centerTabPanel){
		var tab = top.centerTabPanel.getActiveTab();
		top.centerTabPanel.remove(tab);
	}else{
		window.close();
	}
}


window.check = function(){
    if(globalvars.id == 0){
        Ext.CMPP.alert("警告","请先保存接口信息");
        return false;
    }
    return true;
}
window.request = function(url,pos,type){
    Ext.Ajax.request({
        url:url,
        method:"POST",
        params:{id1:globalvars.id,stype:type,nodeId:globalvars.nodeId,script:Ext.getCmp(pos).getValue()},
        "success":function(xhr){
            Ext.Toast.show(xhr.responseText,{
                title:'提示',
                buttons: Ext.Msg.OK,
                minWidth:420,
                icon: Ext.MessageBox.INFO
            });
        }
        ,'failure':function(){
            Ext.Msg.alert("错误","保存脚本出错!");
            Ext.Msg.show({
                title:'错误',
                msg: '保存脚本出错',
                buttons: Ext.Msg.OK,
                animEl: 'elId',
                minWidth:420,
                icon: Ext.MessageBox.ERROR
            });
        }
    });
}
window.scriptSaveCallBack=function(){
   if(!check())
        return;
   request("script!save.jhtml","scriptEditor_1","interf")
}
window.scriptReleaseCallBack = function() {
    if(!check())
        return;
    Ext.MessageBox.confirm("提示","确定发布吗？",function(button,text){
        if(button == 'yes'){
            request("script!release.jhtml","scriptEditor_1","interf");
        }
    });

};
window.templateSaveCallBack = function(){
    if(!check())
        return;
    request("script!save.jhtml","scriptEditor_2","interf_template");
};
window.templateReleaseCallBack = function() {
   if(!check()){
       return;
   }
    Ext.MessageBox.confirm("提示","确定发布吗？",function(button,text){
        if(button == 'yes'){
            request("script!release.jhtml","scriptEditor_2","interf_template");
        }
    });
};
</script>
</head>
<body>
<script>
var InterfaceMgr={
	postUrl:'interface_mgr!update.jhtml',
	deleteUrl:'interface_mgr!delete.jhtml',
	formId :"myForm",
	
	init:function(){
		//快捷键保存
		var docMap = new Ext.KeyMap(Ext.getDoc(), {
			key: 's',
			shift:false,
			ctrl:true,
			handler: function(key,e){
				e.preventDefault();
				InterfaceMgr.save();
			}
		});	
	},
	save:function(){
		Ext.getBody().mask("正在提交中...");
		var form = Ext.getCmp(this.formId).form;
		var params = form.getFieldValues(true);
		params["interf.id"]= globalvars.id;
		params["nodeId"]= globalvars.nodeId;
		Ext.Ajax.request({  
			url:this.postUrl,
			method:"post",
			params:params,
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				if(ret.success){
					Ext.CMPP.alert("提示","提交成功",{
						options:{id:ret.id},
						callback:function(){
							if(globalvars.id==0){
								//刷新当前页面
								var redirectUrl = "interface_mgr!view.jhtml?nodeId="+ globalvars.nodeId +"&interf.id=" + ret.id;
								if(top && top.centerTabPanel && window.frameElement){
									var id = window.frameElement.parentElement.parentElement.parentElement.id;
									var title = opts.params["interf.name"];
									top.reloadTab(id,redirectUrl,"编辑接口-" + title);
								}else{
									location.href = redirectUrl;
								} 
							}
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
		},InterfaceMgr);
    }
};



Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	var interf = globalvars.interf;
		
	new Ext.Viewport({
		layout:'fit',
		items:[{
			xtype:'panel',
			layout:"fit",
			frame:false,
			buttonAlign:'center',
			border:false,
			//tbar:,

			items:[{
				xtype:'form',
				id:'myForm',
				autoScroll:true,
				labelAlign:'right',
				layout:'xform',
				itemCls:"itemStyle5",
				frame:false,
				border:true,
				style:"background:#fff;",
				
				items:[{
					xtype:'panel',
					height:26,
					tbar:globalvars.id==0?null:[{
						xtype:'label',
						text:'接口ID:'
					},{
						xtype:'textfield',
						readOnly:true,
						width:30,
						value:globalvars.id
					},{
						xtype:'tbseparator'
					},{
						xtype:'label',
						text:'创建人:'
					},{
						xtype:'textfield',
						readOnly:true,
						width:55,
						value:interf.creator
					},{
						xtype:'tbseparator'
					},{
						xtype:'label',
						text:'创建时间:'
					},{
						xtype:'textfield',
						readOnly:true,
						width:125,
						value:interf.createTime
					},{
						xtype:'tbseparator'
					},{
						xtype:'tbfill'
					},{
						xtype:'label',
						text:'接口Url:'
					},{
						xtype:'textfield',
						readOnly:true,
						width:170,
						value:'../runtime/interface_'+ interf.id +'.jhtml'
					},{
						text:'打开',
						handler:function(){
							window.open('../runtime/interface_'+ interf.id +'.jhtml');
						}
					},{
						xtype:'tbseparator'
					},{
						text:'查看日志',
						handler:function(){
							window.open('../runtime/getScriptLog.jhtml?nodeId='+ globalvars.nodeId +'&id1='+ interf.id +'&id2=null&stype=interf');
						}
					}]
				},{
					xtype:"hidden",
					name:"interf.id",
					value:globalvars.id
				},{
					fieldLabel:"接口名称",
					id:"interface_name",
					name:"interf.name",
					xtype:"textfield",
					value:"${(interf.name!"")?js_string}",
					width:300
				},{
					fieldLabel:"接口说明",
					name:"interf.description",
					xtype:"textarea",
					value:decodeURIComponent("${(interf.description!"")?url('UTF-8')}"),
					width:500,
					height:80				
				},{
					xtype:'jsoneditor',
					fieldLabel:'接口参数',
					name:"interf.params",
					width:600,
					columns:[{
						header: "参数名",
						field:'param1_name',
						width:120,
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:false,vtype:'alphanum'}}
					},{
						header: "类型",
						field:'param1_type',
						width:100,
						editor:{name: 'Ext.form.ComboBox',ui:{dataSource:[["string","string"],["number","number"],["boolean","boolean"]]}}
					},{
						header: "说明",
						width:300,
						field:'param1_desc',
						editor:{name: 'Ext.form.TextField',ui:{allowBlank:true}}
					}],
					value:"${(interf.params!"")?js_string}"
				},{
					fieldLabel:"脚本",
                    id:"scriptEditor_1",
					name:"interf.script",
					xtype:"scripteditor",
					value:decodeURIComponent("${(interf.script!"")?url('UTF-8')}"),
					//width:600,
					anchor:"95%",
					height:300,
					syntax:'js',
					debuger:Ext.jsDebugger("",globalvars.nodeId,globalvars.id,null,"interf",true,"interf"),
					debugButtonEnable:true,
					loadedCallback:'function(){\
						Ext.getCmp("interface_name").focus();\
					}',
                    save_callback:'window.scriptSaveCallBack',
                    EA_release_callback:'window.scriptReleaseCallBack',
                    toolbar: "save,undo, redo, |, search, go_to_line, |,select_font,|, change_smooth_selection, highlight, reset_highlight, word_wrap,format_code, |, fullscreen,release"
				},{
					fieldLabel:"模板",
                    id:"scriptEditor_2",
					name:"interf.template",
					xtype:'scripteditor',
					value:decodeURIComponent("${(interf.template!"")?url('UTF-8')}"),
					//width:600,
					anchor:"95%",
					height:300,
					syntax:'html',
					debugButtonEnable:false,
					loadedCallback:'function(){\
						Ext.getCmp("interface_name").focus();\
					}',
                    toolbar: "save,undo, redo, |, search, go_to_line, |,select_font,|, change_smooth_selection, highlight, reset_highlight, word_wrap,format_code, |, fullscreen,release",
                    save_callback:'window.templateSaveCallBack',
                    EA_release_callback:'window.templateReleaseCallBack'

                },{
					fieldLabel:"登录验证",
					name:"interf.reqlogin",
					xtype:"combo",
					triggerAction: 'all',
					editable:false,
					width:100,
					mode: 'local',
					store:new Ext.data.SimpleStore({　
					　　fields:['value','text'],　
					　　data:[[1,"是"],[0,"否"]]
					}),
					valueField : 'value',//值
					displayField : 'text',//显示文本
					//listClass: 'x-combo-list-small',
					value:${interf.reqlogin!"0"}
				}],
				buttons:[{
					text:'保存(Ctrl+S)',
					handler:function(){
						InterfaceMgr.save();
					}
				}<#if interf.id?? && (interf.id>0)>,{
					text:'删除',
					handler:function(){
						InterfaceMgr.delete();
					}
				}</#if>,{
					text:'关闭',
					handler:function(){
						closePage();
					}
				}]
			}]
		}]
	});

});

InterfaceMgr.init();	
</script>


</body>
</html>