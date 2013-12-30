<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />

<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="./../res/js/ext2/ext-all.js"></script>	
<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>

<script type="text/javascript" > 
	Ext.onReady(function(){
		Ext.QuickTips.init();  
		var data=${logRecord};
		var store=new Ext.data.JsonStore({
			data:data,
			fields:['id','taskName','result','issueDate','details']
		}); 
		
		var colM=new Ext.grid.ColumnModel([
			{header:'ID',dataIndex:'id'}, 
			{header:'任务名称',dataIndex:'taskName'},
			{header:'结果数值',dataIndex:'result'},
			{header:'检测时间',dataIndex:'issueDate'},
			{
				header:'备注',
				dataIndex:'details',
				renderer:function(v,metadata,record){
					metadata.attr = 'ext:qtitle="报告内容"'+ 'ext:qtip="'+v+'"'; 
					return v;	
				}
			}
		]);
		
		var grid = new Ext.grid.GridPanel({
			id:"handlerTable",
			renderTo:"show",
			title:"日志信息",
			autoWidth : true,
			autoHeight : true,
			height:'auto',
			width:'auto',
			cm:colM,
			store:store,
			trackMouseOver:true
			//autoExpandColumn:1
		});
	});
</script>

</head>
<body>
<div id="show"></div>
</body>
</html>