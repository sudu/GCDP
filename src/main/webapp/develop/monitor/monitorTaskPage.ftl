<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>任务显示</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />

 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
    <script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../develop/monitor/Ext.ux.PageSizePlugin.js"></script>
	<script type="text/javascript" src="./../develop/monitor/Ext.lingo.JsonGrid.js"></script>
	<script type="text/javascript" src="./../develop/monitor/PagedTaskGrid.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<style>
		/*按钮*/

		.addField{background:url("./../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}

		.delField{background:url("./../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}

		.modifyField{background:url("./../res/js/ext2/resources/images/default/my/modify-view.gif") left  no-repeat !important;}
	</style>
</head>
<body>	

<div id="group_div"></div>
<script type="text/javascript">
Ext.onReady(function(){
 	Ext.QuickTips.init(); 
	 var taskGrid=new monitor.TaskGrid({
		 applyTo:'group_div',
		 pageSize:20,
		 urlPagedQuery:'./service!pagedTask.jhtml?nodeID=${nodeID!"-1"}'
	 });
	 taskGrid.render();
});	 
</script>
</body>
</html>