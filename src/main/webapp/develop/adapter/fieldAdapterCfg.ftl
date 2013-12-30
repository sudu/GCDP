<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>字段映射配置</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/runTime.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/Ext.ux.VerticalTabPanel.css" />
	
	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script> 
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.TextArea.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TextField.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TreeComboBox.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.VerticalTabPanel.js"></script>	
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>
	<style>
		body {font-size:14px;}
		input,select{font-size:12px}
		label {margin:0 2px 0 2px}
		textarea {font-size:12px;font-family:tahoma,arial,helvetica,sans-serif}
		.addField{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.saveField{background:url("../res/js/ext2/resources/images/default/my/save.gif") left  no-repeat !important;}
				
		.itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.itemStyle2{
		    padding:0px;
		    margin-bottom:0;
		}
		.fieldsetStyle{
			margin:4px 10px 4px 10px
		}
		/**checkboxGroup**/
		.inputUnit {
			display: inline-block;
			margin: 5px;
			width: 160px;
		}

		.content{
			BORDER-BOTTOM: #fafafa 1px solid; BORDER-LEFT: #fafafa 1px solid; PADDING-BOTTOM: 0px; BACKGROUND-COLOR: #fafafa; MARGIN: 0px auto; PADDING-LEFT: 5px; WIDTH: auto; PADDING-RIGHT: 5px; DISPLAY: block; BORDER-TOP: #fafafa 1px solid; BORDER-RIGHT: #fafafa 1px solid; PADDING-TOP: 0px;
			font-size: 12px;
		}
		.content .TABLE {
			TEXT-ALIGN: center; BACKGROUND-COLOR: #cccccc; MARGIN: 5px; WIDTH: 95%
		}
		.content .TH {
			BACKGROUND-COLOR: #efeff2; HEIGHT: 30px;width:137px;
		}
		.content .TR {
			BACKGROUND-COLOR: #ffffff
		}
		.content .TD {
			PADDING-LEFT: 1px; HEIGHT: 25px
		}
		.content .head_td {
			TEXT-ALIGN: right; BACKGROUND-COLOR: #efeff2; WIDTH: 15%; PADDING-RIGHT: 10px; HEIGHT: 25px; FONT-WEIGHT: bold;width:137px;
		}
		.content .remark_td {
			TEXT-ALIGN: left; BACKGROUND-COLOR: #ffffd9; PADDING-LEFT: 5px;  HEIGHT: 25px
		}
		.content2 {
			BORDER-BOTTOM: #fafafa 1px solid; BORDER-LEFT: #fafafa 1px solid; PADDING-BOTTOM: 0px; BACKGROUND-COLOR: #fafafa; MARGIN: 0px auto; PADDING-LEFT: 5px; WIDTH: auto; PADDING-RIGHT: 5px; DISPLAY: block; BORDER-TOP: #fafafa 1px solid; BORDER-RIGHT: #fafafa 1px solid; PADDING-TOP: 0px;
			font-size: 12px;
		}
		.content2 .remark_td {
			TEXT-ALIGN: left; BACKGROUND-COLOR: #ffffd9; PADDING-LEFT: 5px;  HEIGHT: 25px
		}
		
		.details {width:100%;margin:5px 0 5px 0;}
		summary {background-color: #99BBE8;cursor:pointer； display: inline-block; cursor: pointer; vertical-align: top; text-indent: 1em; }

		.deleteButton{background:url("../res/img/runTime/delete1.gif") left  no-repeat !important;width:16px;height:16px;cursor:pointer;}
		
		fieldset.x-panel-collapsed{
			border: none;
			padding-bottom: 0 !important;
		}
	</style>
		

</head>
<body>

<script type="text/javascript">
	var nodeId__ = #{nodeId!0};
	var sysFormFieldAdapterCfg__ = ${sysFormFieldAdapterCfg}//系统表单（文章，栏目）字段映射配置
	if(sysFormFieldAdapterCfg__.docFieldAdapterCfg != ''){
		sysFormFieldAdapterCfg__.docFieldAdapterCfg = Ext.util.JSON.decode(sysFormFieldAdapterCfg__.docFieldAdapterCfg);
	}
	if(sysFormFieldAdapterCfg__.columnFieldAdapterCfg != ''){
		sysFormFieldAdapterCfg__.columnFieldAdapterCfg = Ext.util.JSON.decode(sysFormFieldAdapterCfg__.columnFieldAdapterCfg);
	}
	var docFormConfig__ = ${docFormConfig};//文章系统表单配置
	var columnFormConfig__ = ${columnFormConfig};//栏目系统表单配置
	var formListConfig__ = ${formListConfig};//所有表单列表
	/*console.log('======================');
	console.log(nodeId__);
	console.log(sysFormFieldAdapterCfg__);
	console.log(docFormConfig__);
	console.log(columnFormConfig__);
	console.log(formListConfig__);
	console.log('======================');*/
</script>

<script type="text/javascript" src="adapter/fieldAdapterCfg.js"></script>

<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
		
	viewMgr.init();
	
});	
</script>


</body>
</html>