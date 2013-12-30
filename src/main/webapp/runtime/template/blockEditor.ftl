<script>
	window.document.title = "通栏加载中...";
</script>
${content!""}

<!-- ///////////////////////bannerEditor/////////////////////// -->
<link href="template/res/bannerEditor.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>	
<script type="text/javascript" src="../res/js/ext_base_extension.js"></script>
<script>
var bannerSetting__ = ${bannerSetting!"0"};
if(!bannerSetting__) bannerSetting__ = {formId:158,listId:247,viewId:264};//默认值
bannerSetting__.nodeId = Ext.parseQuery().nodeId;

</script>
<script type="text/javascript" src="template/res/BNE.js?20120712"></script>	

<script>

Ext.onReady(function(){

	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	bannerMaskRender.init();
	document.title = '【通栏可视化编辑】';

});

</script>


