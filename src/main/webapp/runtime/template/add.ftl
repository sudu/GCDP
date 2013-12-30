 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>新建/修改模板</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script>	
		var params__ = {
			id:#{id!0},
			dataFormId:#{dataFormId!0},
			dataId:#{dataId!0},
			recordData:${recordData!"{}"}
		}
	</script>	
<head>
<body>	
<script>	
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';

	new Ext.Viewport({
		layout:'fit',
		items:[{
			xtype:'form',
			layout:'xform',
			items:[{
				fieldLabel:'名称',
				xtype:'textfield',
				emptyText:'不能为空',
				width:300,
				name:'xform.name',
				value:params__.recordData.name
			},{
				fieldLabel:'内容',
				xtype:'textarea',
				width:300,
				name:'xform.content',
				value:params__.recordData.content
			},{
				fieldLabel:'是否启用',
				xtype:'radiogroup',
				name:'xform.enable',
				data:[[1,'启用'],[0,'停用']],
				value:params__.recordData.enable?params__.recordData.enable:1
			}],
			button:[{
				text:'提交',
				handler:function(e,obj){
					submitFormPanel(obj);
				}
			},{
				text:'提交并关闭',
				handler:function(e,obj){
					submitFormPanel(obj,true);
				}
			},{
				text:'关闭',
				handler:function(e,obj){
					
				}
			}]			
		}]
		
	});
	
	function submitFormPanel(obj,isClose){
		var formPanel = obj.form;
		if(!formPanel.form.isValid()){
			Ext.Toast.show('输入未完成或验证不通过',{
			   title:'提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING
			});
			return;
		}

		formPanel.form.submit({  
			waitTitle : "请稍候",  
			waitMsg : "正在提交数据，请稍候......",  
			url :'../../runtime/tplform!editor.jhtml', 
			params:{id:params__.id,dataFormId:params__.dataFormId,dataId:params__.dataId},
			method : "POST",  
			success : function(form, action) {  
				Ext.Msg.show({
				   title:'提示',
				   msg:  '提交成功！',
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.INFO 
				});
				if(isClose){
					if(top && top.centerTabPanel){
						top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
					}
				}
				location.href = location.href +'&id=' + action.response.Id||params__.id;
			},  
			failure : function(form, action) { 
				var msg="出现未知异常!";
				if(action.failureType){
					msg="failureType:"+action.failureType;
				}else
					msg = action.result?action.result.message:action.response.statusText;
				Ext.Msg.show({
				   title:'错误提示',
				   msg:  decodeURIComponent(msg),
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR 
				});						
			}  
		}); 
	}
});
</script>		
	
</body>
</html>	
