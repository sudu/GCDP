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
	<script type="text/javascript" src="./../develop/dynamic/Ext.ux.PageSizePlugin.js"></script>
	<script type="text/javascript" src="./../develop/dynamic/Ext.lingo.JsonGrid.js"></script>
	<script type="text/javascript" src="./../develop/dynamic/PagedDynStatus.js"></script>
</head>
<body>
<div id="div">
<script type="text/javascript">
Ext.onReady(function(){
 	Ext.QuickTips.init();
 	
	var pageGrid=new dyn.DynStatusGrid({
		 urlPagedQuery:'./dynamic!pagedDynStatus.jhtml?id=${id}',
		 title:'页面状态',
		 header:true
		 });
	
	var tabs = new Ext.TabPanel({
    renderTo: Ext.getBody(),
    activeTab: 0,
    items: [pageGrid,
           {
    		title:"数据库与缓存",
     		//autoHeight:true,
            height:500,
    		html:"<iframe src='./../monitor/service.jhtml?nodeID=${nodeId}&filterValue=dyn' frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>",
           }]
	});
});

function showWindowStatus(url){
	 var win = new Ext.Window({
		 title:"页面访问历史信息",
		width:1010,
		height:420,
		autoScroll:true,
		layout:'auto',
		buttonAlign:'center',
		resizable:true, 
		draggable:false,
		constrain:true,//将拖动范围限制在容器内
		autoDestroy:false,
		modal : true,
		html:"<iframe src='" + url + "' frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>"
		/*
		buttons:[{
			text:'关闭',
			handler:function(){
				this.ownerCt.close();
			}
		}]*/
	  });
	win.show();
};
</script>
</div>
</body>
</html>