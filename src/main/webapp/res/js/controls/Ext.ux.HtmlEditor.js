(function(){
	Ext.ux.HtmlEditor=function(config){
		this.loadScript();
		this.constructor.superclass.constructor.call(this,config);
	}
	Ext.extend(Ext.ux.HtmlEditor,Ext.form.TextArea,{
		instance:null
		,jsPath:"/ckeditor/ckeditor.js"
		,uploadPath:"../ckupload.jhtml?domain=y0.ifeng.com"
		,toolbar_Full:
		[
			{ name: 'document',		items : [ 'Source','-','Save','NewPage','DocProps','Preview','Print','-','Templates' ] },
			{ name: 'clipboard',	items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
			{ name: 'editing',		items : [ 'Find','Replace','-','SelectAll','-','SpellChecker', 'Scayt' ] },
			{ name: 'forms',		items : [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField' ] },
			{ name: 'basicstyles',	items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
			{ name: 'paragraph',	items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
			{ name: 'links',		items : [ 'Link','Unlink','Anchor' ] },
			{ name: 'insert',		items : [ 'Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak','pagebreak_withTitle','Iframe' ] },
			{ name: 'styles',		items : [ 'Styles','Format','Font','FontSize' ] },
			{ name: 'colors',		items : [ 'TextColor','BGColor' ] },
			{ name: 'tools',		items : [ 'Maximize', 'ShowBlocks','-','About' ] },
			{ name: 'ifeng',		items : [ 'doubleToSingle'] }
		]			
		,loadScript:function(){
			var elems = document.getElementsByTagName('script');
			var bsurl="";
			for( i=0; i<elems.length; i++ ){
				if (elems[i].src && elems[i].src.indexOf("Ext.ux.HtmlEditor.js")!=-1 ) {
					var src = unescape( elems[i].src ); // use unescape for utf-8 encoded urls
					var end=src.lastIndexOf('/');
					src = src.substring(0, src.lastIndexOf('/',end-1));
					bsurl = src;
					break;
				}
			}		
			bsurl+=this.jsPath;
			Ext.loadScript(bsurl);			
		}
		,afterRender:function(){
			this.constructor.superclass.afterRender.call(this);
			this.el.addClass("x-hide-display");
			
			var t=this;
			this.el.up("form").on("submit",function(){
				t.getValue();
			});
			
			var t=this;
			var load;
			(function(){
				if(typeof CKEDITOR!="undefined"&&CKEDITOR.replace){
					var cfg=null;
					if(!t.basicMode){
						var barcfg=t.toolbar_Full;
						var bs=t.initialConfig.internalCfg;
						for(var i in bs){
							if(!bs[i])
							{
								var len=barcfg.length;
								while(len--){
									if(barcfg[len].name==i)
										barcfg.splice(len,1);
								}
							}
						}		
						cfg={toolbar:barcfg,filebrowserImageUploadUrl :t.uploadPath};
					}
					t.instance=CKEDITOR.replace(t.el.id,cfg);
				}else
					window.setTimeout(arguments.callee,300);
			})();
		}
		,getValue:function(){
			if(this.instance){
				var value = this.instance.getData();
				this.el.value = value;
				this.setValue(value);	
			}			
			return this.constructor.superclass.getValue.call(this);
		}
	});
})();

Ext.reg('xhtmleditor',Ext.ux.HtmlEditor);


/*

*/