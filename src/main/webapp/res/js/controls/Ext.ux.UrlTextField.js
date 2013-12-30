/*
*description:url控件
*author:chengds@ifeng.com
*date:2013-05-13
*/

Ext.ux.UrlTextField = function (config) {	
	Ext.ux.UrlTextField.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.UrlTextField, Ext.form.TextField, {
	/*UI*/
	width:300,
	copyButtonVisible:false,
	viewButtonVisible:true,
	infoEl:null,
	resRoot:'../res/',
	// copy flash Settings
	clipBoardSwf_url : "../res/swf/clipBorad-Xuas.swf",//Author:许爱思
	clipSwfDiv:null,
    initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.clipBoardSwf_url=this.resRoot + "swf/clipBorad-Xuas.swf";
	    Ext.ux.UrlTextField.superclass.initComponent.call(this);
	},
	onRender: function (ct, position) {

		Ext.ux.UrlTextField.superclass.onRender.call(this, ct, position);
		
		var el = this.el;
		var elParent = el.parent();
		if(this.value!=="" && (this.readOnly || this.disabled)){ //使用链接模式
			this.el.setDisplayed(false);
			this.linkEl = this.el.insertSibling({
				tag:'a',
				href:this.value,
				target:'_blank',
				html:this.value
			},'after');
		}
	
		//预览按钮
		if(this.viewButtonVisible!=false){
			var previewEl = elParent.createChild({
				tag:'a',
				style:'margin-left:5px;',
				title:'弹出浏览',
				href:'javascript:void(0);',
				html:'浏览'
			});
			previewEl.on('click',function(){
				var url =this.el.dom.value;
				if(url)window.open(url);
			},this);
		}
		
		//复制链接
		if(this.copyButtonVisible!=false){
			//加载clipBoard.swf
			this.loadClipBoardSwf(16,16);
			
			var btnCopy = elParent.createChild({
				tag:'a',
				title:'复制链接地址',
				style:'margin-left:5px;',
				href:'javascript:void(0);',
				html:'复制'
			});
			btnCopy.on('mousedown',function(event,dd){
				var obj = Ext.fly(event.target);
				window._clipDataForCopy = this.getValue();
				this.clipSwfDiv.position("absolute",999999999,obj.getLeft(),obj.getTop());
				this.clipSwfDiv.setSize(obj.getWidth(),obj.getHeight());
				this.clipSwfDiv.first().setSize(obj.getWidth(),obj.getHeight());
				
				this.clipSwfDiv.on('mouseup',function(){
					this.clipSwfDiv.position("absolute",1,-100,-100);
					this.setCopyInfo();				
				},this,{single:true});
				
			},this);
						
		}
	},
	/*
	*加载复制到剪切板flash
	*/
	loadClipBoardSwf:function(width,height){
		this.clipSwfDiv = Ext.get(CLIPBOARDSWFID);
		if(!this.clipSwfDiv ){
			this.clipSwfDiv  = Ext.getBody().createChild({
				tag:'div',
				id:CLIPBOARDSWFID,
				style:"position:absolute;left:-100px;top:-100px;z-index:0",
				html:'<embed src="'+ this.clipBoardSwf_url +'" name="clipSwf"  pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash"  width="'+width+'" height="'+height+'" wmode="transparent" allowScriptAccess="always"></embed>'
			});
		}
		return this.clipSwfDiv ;
	},
	setCopyInfo:function(){
		Ext.CMPP.alert('提示','已复制');
	},
	setValue:function(v){
		if(this.linkEl) this.linkEl.dom.href = v;
		Ext.ux.UrlTextField.superclass.setValue.call(this, v);
	},
	disable :function(){
		if(this.linkEl) {
			this.linkEl.show();
			this.el.setDisplayed(false);
		}
		Ext.ux.UrlTextField.superclass.disable.call(this);
	},
	enable :function(){
		if(this.linkEl) {
			this.linkEl.hide();
			this.el.setDisplayed(true);
		}
		Ext.ux.UrlTextField.superclass.enable.call(this);
	}
	
});

var _clipDataForCopy;
var CLIPBOARDSWFID="CLIPBOARDSWFID-XUAS";
/*
*clipBorad.swf内部调用的方法
*/
function getClipData(){
	return window._clipDataForCopy;
}
Ext.reg('urltextfield', Ext.ux.UrlTextField);
