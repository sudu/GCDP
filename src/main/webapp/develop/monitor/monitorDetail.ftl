<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>日志显示</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />

 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
    <script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../develop/monitor/Ext.ux.PageSizePlugin.js"></script>
	<script type="text/javascript" src="./../develop/monitor/Ext.lingo.JsonGrid.js"></script>
	<script type="text/javascript" src="./../develop/monitor/PagedLogGrid.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.DateTime.js"></script>
</head>
<body>


<div id="group_div"></div>

<script type="text/javascript">
var logGrid=new monitor.LogGrid({
	//region:'south',
	//autoHeight: true,
	//autoScroll:true,
	//layout:'auto',
	border: true,
	urlPagedQuery:'./service!pagedLog.jhtml?filterValue=${taskName!"NULL"}',
	width:600,
	title:'日志列表',
	header:true
});
	var form = new Ext.form.FormPanel({
		//region:'north',
		layout:'form',
		labelWidth: 70, 
		method: 'POST',
		frame: true,
		autoHeight: true,
	 	border: true,
		title: '选择时间',
		width:600,
		url:'./service!showChart.jhtml?taskName=${taskName!"NULL"}',
	    items:[{
			id:'st',
			fieldLabel:'开始时间',
			name:'startTime',
			xtype:'xdatetime',
			width:100,
			timeFormat:'H:i:s',
			timeConfig: {
				 altFormats:'H:i:s',
				 allowBlank:false
			},
			dateFormat:'Y-n-d',
			dateConfig: {
				 altFormats:'Y-m-d|Y-n-d',
				 allowBlank:false   
		}
		},{
			id:'et',
			fieldLabel:'结束时间',
			name:'endTime',
			xtype:'xdatetime',
			timeFormat:'H:i:s',
			timeConfig: {
				 altFormats:'H:i:s',
				 allowBlank:false
			},
			dateFormat:'Y-n-d',
			dateConfig: {
				 altFormats:'Y-m-d|Y-n-d',
				 allowBlank:false   
			}
		}],
		buttons: [{
				text: '状态图表',
				type: 'button',
				id: 'submit',
				handler:function(){
					form.getForm().submit({
					
						success:function(form, action){
							var res = Ext.util.JSON.decode(action.response.responseText);
							document.getElementById("chart").innerHTML="<img src=\""+res.result+"\" style=\"float:left;\" />";
						},
						failure:function(form, action){
							Ext.Msg,alert('错误','提交失败');						
						}
					
					});
				}
		}]
	});
var panel = new Ext.Panel({
	title:'任务状态图表',
 	autoScroll:true,
 	layout:'auto',
 	region:'center',
 	width:620,
    height:460,
	items:[
		form,
	 	{	//region:'center',
	 		width:600,
	 		height:150,
	 		autoHeight: true,
	 		border: false,
	 		margins: '0 0 5 0',
		 	html:"<div id='chart'><img src=\"../servlet/DisplayChart?filename=${chart}\" style=\"float:left;\" /></div>"
	 	}
	 ]

});

var tabs = new Ext.TabPanel({
    renderTo: Ext.getBody(),
    activeTab: 0,
    items: [panel,logGrid]
	});

Ext.onReady(function(){
 	Ext.QuickTips.init();
 	var view = new Ext.Viewport({
 		renderTo:'group_div',
 		width:635,
        height:760,
 		//autoScroll:true,
 		//layout:'fit',
	 	items:[tabs]
	}); 	 
});	 
</script>
</body>
</html>