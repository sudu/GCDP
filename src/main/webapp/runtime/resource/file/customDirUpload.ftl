<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>上传工具</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css" />
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
	</style>
 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.ListPanel.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.UploadTool.js"></script>
<script>
	var params__ = Ext.parseQuery();
	var globalvars={
		nodeId:params__.nodeId||params__.nodeid||0,	
		uploadMode:params__.mode||1	,
		target:params__.target//目标url
	};
</script>
</head>
<body>
<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	
	new Ext.Viewport({
		layout:'fit',
		items:[{
			xtype:'form',
			layout:'xform',
			hideLabels:true,
			hideNodes:true,
			autoScroll:true,
			bodyStyle:'padding:10px',
			itemCls:"itemStyle",
			items:{
				xtype:'uploadtool',
				fieldLabel:'默认上传',
				sendfilePrefix:'a',//文件目录的根目录前缀
				uploadMode:globalvars.uploadMode,
				width:250,
				value:globalvars.target,
				nodeId:globalvars.nodeId
			}

		}]
	});
});
	
</script>
</body>
</html>