Ext.namespace("Ext.ux");

/**
 * 带字数提示的多行文本域控件。支持全角和半角的字数统计
 * @class Ext.ux.TextArea2
 * @extends Ext.form.TextArea
 * @author cici
 × @date:2012-5-15
 */
Ext.ux.TextArea2 = function (config) {
	if(!Ext.nore(config.regex))
		config.regex=Ext.decode(config.regex);		
	Ext.ux.TextArea2.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.TextArea2, Ext.form.TextArea, {
	/**标题标尺属性**/
	warnCharCount:300,
	/****/
	initComponent: function () {
		if(typeof(this.rulerNums)=='string'){
			this.rulerNums= Ext.decode(this.rulerNums);
		}
		if(this.warnCharCount){
			this.warnCharCount = parseInt(this.warnCharCount)
		}
	},
	onRender:function(ct,pos){
		Ext.ux.TextArea2.superclass.onRender.call(this,ct,pos);		
		(this.initialConfig.readOnly&&(this.el.addClass(" readOnly")));
		
		var elParent = this.el.parent();
		this.contentLenthEl = elParent.createChild({//显示字数的容器
			tag:'span',
			style:'color:#000;font-size:12px;'
		})
		this.el.on('keyup',this.showContentLenth,this);
		this.showContentLenth();
	},
	showContentLenth:function(){
		if(this.contentLenthEl){
			var content = this.getValue();
			var count = this.getCharLength(content);
			var countText = '<font color="green">' + count + '</font>'; 
			if(count>=this.warnCharCount){
				countText = '<font color="red">' + count + '</font>'; 
			}
			this.contentLenthEl.update('字数:' + countText);
		}
	},
	getCharLength:function(content){
		var pattern = /[^\x00-\x80]+/;
		var contentLength = 0;
		for (var i = 0; i < content.length; i++) {
			if (pattern.test(content.charAt(i))) {
				contentLength++;
			} else {
				contentLength += 0.5;
			}
		}
		return contentLength;
	},
	setValue:function(v){
		Ext.ux.TextArea2.superclass.setValue.call(this, v);
		if(this.el) this.showContentLenth();
	}
});
Ext.reg('textarea2', Ext.ux.TextArea2);