<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <head> 
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
  <title>表单导入</title> 
  	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="./../res/css/designTime.css" />
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script> 
  </head>
  <body>
  <div id="formDiv" style="margin-top:10px;margin-left:20px;">
  <form method="post" action="xform!importForm.jhtml?nodeId=${nodeId!"0"}" enctype="multipart/form-data">
    请选择要复制的表单包：
   <input type="file" name="conData"/>
   <input type="submit" value="确定" name="btImport"/>
   </form>
   <div>
<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.lOADING_IMAGE_URL= "../res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
	new Ext.Viewport({
		layout:"fit",
		items:[{
			xtype:'panel',
			contentEl:'formDiv',
		}]
	});
});
</script>
  </body>
  </html>