<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单设计器</title>
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="../res/css/designTime.css?v=20121123" />

 	<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../res/js/ext2/ext-lang-zh_CN.js"></script>    
	<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>  
	<script type="text/javascript" src="../res/js/ext_vtypes.js"></script>  
	<script type="text/javascript" src="../res/js/designTimeClass.js"></script>  
	<script type="text/javascript" src="../res/js/controls/Ext.ux.grid.GroupPropertyGrid.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.Sort.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.ControlSort.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.TextArea.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.ArrayEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CustomEditors.KeyValueEditor.js"></script>		
	<script type="text/javascript" src="../res/js/editors/Ext.ux.ListItemEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.createRecordsField.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.createWinEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.DimenEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.ArrayEditor.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.JsonEditor.js"></script>	
	<script type="text/javascript" src="../res/js/controls/Ext.ux.CheckboxGroup.js"></script>
	<script type="text/javascript" src="../res/js/editors/Ext.ux.CheckGroupEditor.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TitleField.js"></script> 
	<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageComboBox.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldSet.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.LinkageFieldText.js"></script>
	<script type="text/javascript" src="../res/js/controls/Ext.ux.TextArea2.js"></script> 
	<script type="text/javascript" src="../res/js/controls/Ext.ux.HTMLField.js"></script> 	
	<script type="text/javascript">
		Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
		Ext.namespace('Designer');
		Ext.namespace('Designer.controls');
		Ext.namespace('Designer.controls.ui');	
		function closePage(){
			if(top && top.centerTabPanel){
				var tab = top.centerTabPanel.getActiveTab();
				top.centerTabPanel.remove(tab);
			}else{
				window.close();
			}	
		}
	</script>	
	
</head>
<body>	
<script src="../res/js/designer_v2.0.js?v=20121123" type="text/javascript"></script>
<script src="../res/js/designerUI.js?v=20121123" type="text/javascript"></script>
<script src="../res/js/config/formTemplate.js?v=20121123" type="text/javascript"></script>
<script src="../res/js/config/controlsUI_cfg.js?v=20121123" type="text/javascript"></script>

<script type="text/javascript">

Ext.onReady(function(){
	Designer.Init({});
});

</script>		


</body>
</html>