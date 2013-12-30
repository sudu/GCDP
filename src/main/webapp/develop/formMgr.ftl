 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>所有表单</title>
	<link rel="shortcut icon" href="../res/img/favicon.ico" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />

 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript">
		var formId__= #{formId!0};
		var nodeId__=#{nodeId!0};
	</script>	
</head>
<body>	
<script type="text/javascript">
var formMgr = {
	
	init:function(){
		this.initUI();
	},
	initUI:function(){
		new Ext.Viewport({
			layout:"fit",
			items:[{
				xtype:'panel',
				region:"center",
				layout:"column",
				autoScroll:true,
				border:true,
				id:'formListBox',
				tbar:[{
					xtype:'label',
					text:'搜索表单：'
				},{
					xtype:'textfield',
					id:'txtSearch',
					width:180,
					emptyText:'输入节点名称全拼或全拼首字母',
					enableKeyEvents:true,
					style:'background:url(../res/img/search.gif) no-repeat;padding-left:18px;'
					
				}],
				items:[{
					xtype:'panel'
				}]
			}]
		});
	}

}
</script>
<script>

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	formMgr.init();
});
</script>
	
</body>
</html>