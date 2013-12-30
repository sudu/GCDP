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
	<script type="text/javascript" src="./../develop/monitor/PagedStatusGrid.js"></script>
	<script type="text/javascript" src="./../develop/monitor/PagedErrGrid.js"></script>
</head>
<body>
<script type="text/javascript">
Ext.onReady(function(){
 	Ext.QuickTips.init(); 
	 var stateGrid=new monitor.StateGrid({
	 	applyTo:'group_div1',
	 	urlPagedQuery:'./service!pagedStatus.jhtml?nodeID=${nodeID!"-1"}<#if filterValue?? >&filterValue=${filterValue}</#if>',
	 	title:'状态总揽',
	 	header:true,
	 	pageSize:20
	 	});
	 stateGrid.render(); 

	 var errGrid=new monitor.ErrGrid({
	 applyTo:'group_div',
	 urlPagedQuery:'./service!pagedErr.jhtml?nodeID=${nodeID!"-1"}<#if filterValue?? >&filterValue=${filterValue}</#if>',
	 title:'错误日志',
	 header:true
	 });
	 
	 errGrid.render();
});

function showWindowStatus(url){
	 var win = new Ext.Window({
			 title:"状态图",
             width:635,
             height:460,
             autoScroll:true,
             layout:'auto',
             //overflow:'auto',
             resizable : false, 
             draggable:false,
             constrain:true,//将拖动范围限制在容器内
             autoDestroy:false,
             modal : true,
             html:"<iframe src='" + url + "' frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>"
		  });
		 win.show();
};
		
function showWindowErr(url){
	 var win = new Ext.Window({
		 	title:"日志信息",
            width:600,
            height:150,
            autoScroll:true,
            layout:'auto',
            draggable:false,
            constrain:true,//将拖动范围限制在容器内
            autoDestroy:false,
            modal : true,
            html:"<iframe src='" + url + "' frameborder=\"0\" width=\"100%\" height=\"100%\"></iframe>"
		  });
		 win.show();
};
</script>
<div id="group_div1"></div>
<div id="group_div"></div>
</body>
</html>