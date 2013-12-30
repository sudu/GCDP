<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>错误显示</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />

 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
    <script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../develop/monitor/Ext.ux.PageSizePlugin.js"></script>
	<script type="text/javascript" src="./../develop/monitor/Ext.lingo.JsonGrid.js"></script>
	<script type="text/javascript" src="./../develop/monitor/PagedErrGrid.js"></script>
</head>
<body>	

<div id="group_div"></div>
<script type="text/javascript">
Ext.onReady(function(){
 	Ext.QuickTips.init(); 
	 var errGrid=new monitor.ErrGrid({
		 applyTo:'group_div',
		 urlPagedQuery:'./service!pagedErr.jhtml?nodeID=${nodeID!"-1"}'
		 });
	 
	 errGrid.render();
		function showWindow(url){
			 var win = new Ext.Window({
				 title:"状态图",
	                width:600,
	                height:150,
	                draggable:true,
	                constrain:true,//将拖动范围限制在容器内
	                autoDestroy:false,
	                modal : true,
	                html:"<iframe src='" + url + "' frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>"
				  });
				 win.show();
		};
});
</script>
</body>
</html>