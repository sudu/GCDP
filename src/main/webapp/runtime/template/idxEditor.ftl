<script>
	window.document.title = "碎片加载中...";
</script>
${content!""}

<!-- ///////////////////////idxEditor/////////////////////// -->
<link rel="stylesheet" type="text/css" href="template/res/idxEditor.css?20130513"/>
<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>	
<script type="text/javascript" src="template/res/idxEditor.js?20130513"></script>	
<script>
	
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';
	
	//监听postMessage消息事件
	if (typeof window.addEventListener != 'undefined') {
		window.addEventListener('message', idxEditor.onMessage, false);
	} else if (typeof window.attachEvent != 'undefined') {
		window.attachEvent('onmessage', idxEditor.onMessage);
	}
	
	idxMaskRender.init();

	document.title = '【碎片可视化编辑】';
});

</script>