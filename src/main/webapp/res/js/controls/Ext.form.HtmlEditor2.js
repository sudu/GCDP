/** 
* 重载EXTJS-HTML编辑器 
* 
* @class HTMLEditor 
* @extends Ext.form.HtmlEditor 
* @author cici 
 × @date:2012-10-19
 */
var pasteEventoPendente=new Array(); 
pasteEventoPendente[0] = false;
 Ext.override(Ext.form.HtmlEditor, {
	fixKeys: function(){
	    if(Ext.isIE){
	        return function(e){
	            var k = e.getKey(),
	                doc = this.doc,
	                    r;
	            if(k == e.TAB){
	                e.stopEvent();
	                r = doc.selection.createRange();
	                if(r){
	                    r.collapse(true);
	                    r.pasteHTML('&nbsp;&nbsp;&nbsp;&nbsp;');
	                    this.deferFocus();
	                }
	            }else if(k == e.ENTER){
	                r = doc.selection.createRange();
	                if(r){
	                    var target = r.parentElement();
	                    if(!target || target.tagName.toLowerCase() != 'li'){
	                        e.stopEvent();
	                        r.pasteHTML('<p />');
	                        r.collapse(false);
	                        r.select();
	                    }
	                }
	            }
	        };
	    }else if(Ext.isOpera){
	        return function(e){
	            var k = e.getKey();
	            if(k == e.TAB){
	                e.stopEvent();
	                this.win.focus();
	                this.execCmd('InsertHTML','&nbsp;&nbsp;&nbsp;&nbsp;');
	                this.deferFocus();
	            }
	        };
	    }else if(Ext.isWebKit){
	        return function(e){
	            var k = e.getKey();
	            if(k == e.TAB){
	                e.stopEvent();
	                this.execCmd('InsertText','\t');
	                this.deferFocus();
	            }else if(k == e.ENTER){
	            	//console.log('webkit enter');
	                e.stopEvent();
					this.execCmd('InsertText', '\n'); 
					//this.insertAtCursor("<p></p>");
	                this.deferFocus();
	            }
	         };
	    }
	    else {
	    	return function(e){
	            var k = e.getKey();
	            if(k == e.TAB){
	                e.stopEvent();
	                this.execCmd('InsertText','\t');
	                this.deferFocus();
	            }else if(k == e.ENTER){
	            	//console.log('other enter');
	                e.stopEvent();
	                this.execCmd('InsertHtml','<p /><p />');
	                this.deferFocus();
	            }
	         };
	    }
	}(),
	insertAtCursor : function(text){
        if(!this.activated){
            return;
        }
		this.focus();
        var element = document.createElement("span"); 
		element.innerHTML = text;
		var selection = this.win.getSelection(); 
		if (!selection.isCollapsed) { 
			selection.deleteFromDocument(); 
		} 
		var rg = selection.getRangeAt(0);
		for(var i=element.childNodes.length-1;i>=0;i--){
			rg.insertNode(element.childNodes[i]); 
		}
		this.deferFocus();
		selection.collapseToEnd();//光标移到最后
		this.syncValue();
    },
	insertValueAtCursor : function(value){
		var textarea = this.el.dom;
		if (textarea.selectionStart || textarea.selectionStart == '0') { 
			//起始位置 
			var startPos = textarea.selectionStart; 
			//结束位置 
			var endPos = textarea.selectionEnd; 
			//插入信息 
			textarea.value = textarea.value.substring(0, startPos)+ value + textarea.value.substring(endPos, textarea.value.length); 
		} else { 
          //没有焦点的话直接加在TEXTAREA的最后一位 
          textarea.value += value; 
		}
		this.syncValue();
	},
	newLine:function() {
		this.focus();
		var doc=this.doc;
		doc.execCommand('InsertText', false, '\n');
	},
    toggleSourceEdit : function(sourceEditMode){
        if(sourceEditMode === undefined){
            sourceEditMode = !this.sourceEditMode;
        }
        this.sourceEditMode = sourceEditMode === true;
        var btn = this.tb.items.get('sourceedit');
        if(btn.pressed !== this.sourceEditMode){
            btn.toggle(this.sourceEditMode);
            //return;
        }
        if(this.sourceEditMode){
            this.tb.items.each(function(item){
				if(item.itemId != 'sourceedit'){
					if(!item.sourceEditStateEnable){
						item.disable();
					}else{
						item.enable();
					}
                }
            });
			for(var i=0;i<this.customToolbars.length;i++){
				this.customToolbars[i].items.each(function(item){
					if(!item.sourceEditStateEnable){
						item.disable();
					}else{
						item.enable();
					}
				});
			}
            this.syncValue();
            this.iframe.className = 'x-hidden';
            this.el.removeClass('x-hidden');
            this.el.dom.removeAttribute('tabIndex');
            this.el.focus();
        }else{
            if(this.initialized){
                this.tb.items.each(function(item){
                    if(!item.sourceEditStateEnable || item.sourceEditStateEnable===2)
						item.enable();
					else
						item.disable();
                });
				for(var i=0;i<this.customToolbars.length;i++){
					this.customToolbars[i].items.each(function(item){
						if(!item.sourceEditStateEnable || item.sourceEditStateEnable===2)
							item.enable();
						else
							item.disable();
					});
				}
				
            }
            this.pushValue();
            this.iframe.className = '';
            this.el.addClass('x-hidden');
            this.el.dom.setAttribute('tabIndex', -1);
            this.deferFocus();
        }
        var lastSize = this.lastSize;
        if(lastSize){
            delete this.lastSize;
            this.setSize(lastSize);
        }
        this.fireEvent('editmodechange', this, this.sourceEditMode);
    },
	createLink : function(){
		var url = prompt(this.createLinkText, this.defaultLinkValue);
		/*if(url && url != 'http:/'+'/'){
			var selection = this._getSelectedContents();//获得选中的文本
			var title = selection||url;
			
			var html = '<a href="'+url+'" target="_blank">'+title+'</a>';	
			if(this.sourceEditMode){
				this.insertValueAtCursor(html);
			}else{
				this.insertAtCursor(html); 
			}
		}
		*/
		
		if(url && url != 'http:/'+'/') {
			var sel = false;
			if (window.getSelection) {
				sel = this.win.getSelection();
			} else if (document.getSelection) {
				sel = this.doc.getSelection();
			}
			if (sel) {
				this.insertAtCursor('<a target="_blank" href="'+url+'">'+sel+'</a>');
			} else if (document.selection) {
				sel = this.doc.selection.createRange();
				sel.pasteHTML('<a target="_blank" href="'+url+'">'+sel.text+'</a>');
		   } else this.relayCmd('createlink', url);
		}
	},
	//chrome,firefox,opera
	_getSelectedContents:function(){
		var selection = this.win.getSelection(); 	
		var range=selection.getRangeAt(0);
		var container = this.doc.createElement('div');
		container.appendChild(range.cloneContents());
		return container.innerHTML;
	}	
});
 
Ext.form.HtmlEditor2 = Ext.extend(Ext.form.HtmlEditor,{
	resRoot:'../res/',
	uploadUrl:'../upload!file.jhtml',
	destinationDomains:['y0.ifengimg.com','y1.ifengimg.com','y2.ifengimg.com','y3.ifengimg.com'],//分发的目标域名，支持多个域名随机
	pageBreakHtml:'<div style="page-break-after: always;"><span style="display: none;">&nbsp;</span></div>',//分页符   
	pageBreakFakeImage:'<img class="cmpp-page-break" src="../res/img/runTime/spacer.gif" _fckfakelement="true" _fckrealelement="0">',//分页符映射  
	editorStyleInject:'',//这段自定义样式将注入到编辑器里的style里，以满足自定义样式	
    customButtons:null,
	enableSourceEdit:false,
	enableSourceEdit2:true,
	enableAddImage:true,
	enableCutImage:true,
	enableInsertTable:true,
	enablePasteAsText:true,
	enableFullScreen:true,
	enablePageBreak:true,//启用分页符
	enableFindAndReplace:true,
	width:700,
	height:600,
	editMode:1,//1:设计模式编辑 2：HTML编辑
	customToolbars:null,//扩展工具条key:value
	isFullScreen:false,
	getDocMarkup : function(){
        return '<html><head><style type="text/css">body{border:0;margin:0;padding:3px;height:98%;cursor:text;} p {font-size: 12px;margin-bottom: 25px;text-indent: 28px;} .cmpp-page-break{ background: url("'+ this.resRoot +'img/fck-pagebreak.gif") no-repeat scroll center center transparent;border: 1px dotted #999999;clear: both;height: 5px;margin: 5px 0;page-break-after: always;text-align: center;width: 95%;} .detailPic{text-align:center;} '+ this.editorStyleInject +'</style></head><body></body></html>';
    },
	initComponent: function () {
		//初始化插件
		var plugins = [];
		this.pluginRedoUndo = new Ext.form.HtmlEditor.RedoUndo();
		plugins.push(this.pluginRedoUndo);
		this.enableAddImage && plugins.push(new Ext.form.HtmlEditor.AddImage());
		this.enableCutImage && plugins.push(new Ext.form.HtmlEditor.UploadAndCutImage());
		this.enableInsertTable && plugins.push(new Ext.form.HtmlEditor.Table());
		this.enablePageBreak && plugins.push(new Ext.form.HtmlEditor.AddPageBreak());
		this.enableFindAndReplace && plugins.push(new Ext.form.HtmlEditor.Separator(),new Ext.form.HtmlEditor.FindAndReplace());
		this.enableFullScreen && plugins.push(new Ext.form.HtmlEditor.Fill(),new Ext.form.HtmlEditor.FullScreen());
		//plugins.push(new Ext.form.HtmlEditor.Fill(),new Ext.form.HtmlEditor.Link());
		

		this.plugins = plugins;
		Ext.applyDeep(this,this.initialConfig);
		if(typeof this.fontFamilies == "string"){
			this.fontFamilies = Ext.decode(this.fontFamilies);
		}
		this.customToolbars=[];
		if(typeof this.customButtons ==="string"){
			this.customButtons = Ext.decode(this.customButtons);
		}
		
		if(!this.value){
			this.value = "<p>&nbsp</p>";
		}
		Ext.form.HtmlEditor2.superclass.initComponent.call(this);
				
		if(typeof(this.destinationDomains)=='string'){
			try{
				this.destinationDomains = Ext.decode(this.destinationDomains);
			}catch(ex){
				
			}
		}
		this.addListener('initialize',function (h){  
			if(this.editMode===2){
				var btn = this.tb.items.get('sourceedit');   
				btn.toggle(true);  
				this.toggleSourceEdit(true);  
				this.setValue(this.value);
			}	
			//创建增高和减高按钮
			var ct = this.el.parent();
			var w = ct.getWidth();
			var addHeightEl = ct.insertSibling({
				tag:'div',
				cls:'cmpp-htmlEditor-addHeight'
			},'after');
			var addHeight = addHeightEl.createChild({
				tag:'img',
				title:'增加高度',
				style:'cursor: pointer; position: relative; left: '+ (w-40) +'px; top: 0px;',
				src:this.resRoot + 'img/runTime/editor_add.jpg',
			});
			var diffHeight = addHeightEl.createChild({
				tag:'img',
				title:'减少高度',
				style:'cursor: pointer; position: relative; left: '+ (w-35) +'px; top: 0px;',
				src:this.resRoot + 'img/runTime/editor_diff.jpg',
			});
			addHeight.on('click',function(){
				var h2 = 250;	
				var h = this.getSize().height;
				this.setHeight(h+h2);
			},this);
			diffHeight.on('click',function(){
				var h2 = 250;	
				var ct = this.el.parent();
				var h = this.getSize().height;
				if(h>h2){
					this.setHeight(h-h2);
				}
			},this);
		});
	},
	
	onRender : function(ct, position){
		Ext.form.HtmlEditor2.superclass.onRender.call(this, ct, position);
		var editor = this;
		
		/******阻止chrome浏览器的粘贴，接管粘贴*********/
		var on_editor_paste = function(e){
			if(!e.browserEvent.clipboardData) return;
			var editor = this;
			
			var readClipboardDataCallback=function(edtior){
				return function(clipboardData){
					var text = clipboardData;
					text = (function(text){
						text = text.replace(/\r/g,'');
						var ps = text.split('\n');
						var html="";
						for(var i=0;i<ps.length;i++){
							if(ps[i]!=""){
								html+="<p>"+ ps[i] +"</p>\n"
							}	
						}
						return html;
					})(text);
					if(edtior.sourceEditMode){
						edtior.insertValueAtCursor(text);
					}else{
						edtior.insertAtCursor(text); 
					}
					edtior.deferFocus();
				}			
			};

			var item = e.browserEvent.clipboardData.items[0];
			if (item.kind === "string"){
				item.getAsString(readClipboardDataCallback(editor));
			}
			e.preventDefault();
		}		

		Ext.fly(editor.iframe.contentDocument).on('paste',on_editor_paste,editor);
				
	},
	getValue : function() {  
		this.syncValue();
		var ret = Ext.form.HtmlEditor2.superclass.getValue.apply(this, arguments);  
		if (ret) {  
		// fix edit grid panel compare startvalue & nowvalue  
		// fix '\n' patch for webkit(chrome) when ENTER pressed  
			ret = ret.replace(/^(&nbsp;|<br>|\s)*|(&nbsp;|<br>|\s)*$/ig, '')  
			ret = ret.replace(/<div>(.+?)<\/div>/ig,'<br>$1')  
		}  
		if (!ret) {  
			ret = '';  
		}  
		return ret;  
	},
	setValue : function(value) {  
		if(!value){
			value = "<p>&nbsp;</p>";
		}
		Ext.form.HtmlEditor2.superclass.setValue.call(this, value);  
	},
	//从设计模式同步源码到源码模式
	syncValue : function(){
        if(this.initialized && !this.sourceEditMode){
            var bd = this.getEditorBody();
            var html = bd.innerHTML;
            html = this.cleanHtml(html);
            if(this.fireEvent('beforesync', this, html) !== false){
				html = html.replace(new RegExp(this.pageBreakFakeImage,'g'),this.pageBreakHtml);
                this.el.dom.value = html;
                this.fireEvent('sync', this, html);
            }
        }
    },
  
    pushValue : function(){
        if(this.initialized){
            var v = this.el.dom.value;
            if(!this.activated && v.length < 1){
                v = '&nbsp;';
            }
            if(this.fireEvent('beforepush', this, v) !== false){
				v = v.replace(new RegExp(this.pageBreakHtml,'g'),this.pageBreakFakeImage);
                this.getEditorBody().innerHTML = v;
                this.fireEvent('push', this, v);
            }
        }
    },
	//覆写方法
	onEditorEvent : function(e){
		if(e.target.tagName=="IMG" || e.target.tagName=="INPUT"){//点击选中图片和input元素
			var sel = this.win.getSelection();
			var range = this.doc.createRange();
			var referenceNode = e.target;
			range.selectNode(referenceNode);

			sel.removeAllRanges();
			sel.addRange(range);
		}
        this.updateToolbar();
    },
	pasteAsText:function(obj){
		var editor = this; 
		var text = new Ext.form.TextArea({
			emptyText:'请使用键盘快捷键(Ctrl+V)把内容粘贴到下面的方框里，再按 确定。'
		});
		var win = new Ext.Window({ 
			title : "粘贴为无格式文本", 
			width : 400, 
			height : 300, 
			modal : true, 
			border : false, 
			layout : "fit", 
			items : [text],
			buttonAlign:"center",
			buttons:[{
				text : '确定', 
				scope:this,
                handler : function() { 
					var win = this.pasteWin;
					var text = win.textarea.getValue();
					if(text==="")return;
					text = (function(text){
						var ps = text.split('\n');
						var html="";
						for(var i=0;i<ps.length;i++){
							if(ps[i]){
								html+="<p>"+ ps[i] +"</p>\n"
							}	
						}
						return html;
					})(text);
					if(this.sourceEditMode){
						this.insertValueAtCursor(text);
					}else{
						this.insertAtCursor(text); 
					}
						
					win.close(this); 
				}		
			},{ 
                text : '取消', 
                handler : function() { 
					var win = this.ownerCt;
                    win.close(this); 
                } 
            }]	
		}); 
		win.textarea = text;
		editor.pasteWin = win;
        win.show(); 
		win.alignTo(obj.el,"br",[-400,3]);
	},
	//上传图片并裁图
	uploadAndCutImage:function(obj,targetWidth,targetHeight){
		if(this.uploadAndCutImageWindow){
			var win = this.uploadAndCutImageWindow ;
		}else{
			var formPanel = new Ext.FormPanel({
				labelWidth : 58, 
				layout:'xform',
				frame : true, 
				bodyStyle : 'padding:5px 5px 0', 
				autoScroll : true, 
				border : false, 
				items:[{
					fieldLabel:'目标宽度',
					xtype:'numberfield',
					name:'targetWidth',
					value:targetWidth,
					width:60
				},{
					fieldLabel:'目标高度',
					xtype:'numberfield',
					name:'targetHeight',
					value:targetHeight,
					width:60
				},{
					xtype:'imagecutter',
					fieldLabel:'图片',
					name:'imageUrl',
					resRoot:this.resRoot,
					uploadUrl:'../intelliImage!sendfile.jhtml',//自动生成url
					AIUrl:'../intelliImage!coordinate.jhtml',//智能裁图接口
					width:138,
					targetWidth:targetWidth||300,
					targetHeight:targetHeight||200,
					copyButtonVisible:false,
					helpButtonVisible:false,
					cutRectMultiple:1,
					vtype:null,//字段类型 null,url
					AIDefaultChecked:false,//默认开启智能建议
					openCutterWindow:function(obj){
						var win = this.cutterWindow;
						var form = this.ownerCt.form;
						var values = form.getValues();
						
						values.targetWidth && values.targetHeight && win.setTargetSize(parseInt(values.targetWidth),parseInt(values.targetHeight));
						win.show();
					}
				}]
			});
			var win = new Ext.Window({ 
				title : "上传图片&裁图", 
				width : 350, 
				height : 170, 
				modal : true, 
				border : false, 
				layout : "fit", 
				items : [formPanel],
				buttonAlign:"center",
				closeAction:'hide',
				buttons : [{ 
					text : '插入图片', 
					scope:this,
					handler:function(obj){
						var win = obj.ownerCt;
						var values = win.formPanel.form.getValues()
						var url = values.imageUrl;
						if(url){
							var img = document.createElement("img"); 
							img.src = url; 
							//img.width = values.targetWidth;
							//img.height = values.targetHeight;
							var html = '<p style="text-align:center">&nbsp;' + img.outerHTML + '&nbsp;</p>';
							if(this.sourceEditMode){
								this.insertValueAtCursor(html);
							}else{
								this.insertAtCursor(html); 
							}
							win.hide();
						}
					}
				},{
					text:'取消',
					handler:function(obj){
						obj.ownerCt.hide();
					}
				}]
			});
			win.formPanel = formPanel;
			this.uploadAndCutImageWindow = win;
		}
		win.show(); 
		obj && win.alignTo(obj.el,"br",[-300,3]);
	},
	//凤凰正文强力自动排版
	ifengFormat:function(obj){
		var f = new IFengTextFormatter();
		if(this.sourceEditMode){
			//this.el.dom.value = f.format(this.CtoH(this.el.dom.value));
			this.setValue(f.format(this.CtoH(this.getValue())));
		} else {
			var ed = this.getEditorBody();
			ed.innerHTML = f.format(this.CtoH(ed.innerHTML));
		}
		this.syncValue();
		this.pluginRedoUndo && this.pluginRedoUndo.record();
	},
	createToolbar : function(editor) { 
        Ext.form.HtmlEditor2.superclass.createToolbar.call(this, editor); 
		this.enableSourceEdit2 && this.tb.insertButton(0,{ 
			text: '排版',
			cls:null,
			icon:this.resRoot + 'js/ext2/resources/images/default/s.gif',
			tooltip:'强力自动排版',
			handler : function(btn){
				this.ifengFormat(btn);
			},
			scope : this 
		});
		this.enableSourceEdit2 && this.tb.insertButton(0,{ 
			text: 'HTML模式',
			cls:null,
			icon:this.resRoot + 'js/ext2/resources/images/default/s.gif',
			tooltip:'设计/HTML模式切换',
			itemId:'sourceedit',
			enableToggle :true,
			sourceEditStateEnable:2,
			handler : function(btn){
				this.toggleSourceEdit(btn.pressed);
			},
			scope : this 
		});

        this.enablePasteAsText && this.tb.addButton({ 
			cls : "x-btn-icon", // x-edit-pasteAsText
			icon : this.resRoot + "img/runTime/pasteAsText.png", 
			tooltip:'粘贴为无格式文本',
			handler : this.pasteAsText, 
			scope : this 
		}); 
		if(this.customButtons){
			for(var tbId in this.customButtons){
				var tbButtons = this.customButtons[tbId];
				if(Ext.isArray(tbButtons)){
					if(tbButtons.length>0){
						var tbar = new Ext.Toolbar(); 
						tbar.render(this.tb.el.parent()); 
						this.customToolbars.push(tbar);	
					}
					for(var i=0;i<tbButtons.length;i++){
						var btn = tbButtons[i];
						var item = tbar.addButton({
							cls : btn.text?null:"x-btn-icon",
							icon : btn.icon || this.resRoot + 'js/ext2/resources/images/default/s.gif', 
							text: btn.text,
							tooltip:btn.tooltip,
							handler : btn.handler, 
							disabled:btn.sourceEditStateEnable,
							scope : this 
						}); 
						item.sourceEditStateEnable = btn.sourceEditStateEnable;
					}
				}
			}
		}
    } 
}); 


//================强力排版 开始========================
 Ext.override(Ext.form.HtmlEditor2, {
	CtoH:function(str){
		var result="";
		for (var i = 0; i < str.length; i++){
			if(
			 (str.charCodeAt(i)>=65296 && str.charCodeAt(i)<=65305 )
			 ||(str.charCodeAt(i)>=65345 && str.charCodeAt(i)<=65370)
			 ||(str.charCodeAt(i)>=65313 && str.charCodeAt(i)<=65338)
			 ) {
			 result+= String.fromCharCode(str.charCodeAt(i)-65248);
			} else {
			 result+= String.fromCharCode(str.charCodeAt(i));
			}
		}
		return result;
	}
 });

IFengTextFormatter = function(){this.initialize();};
IFengTextFormatter.prototype = {
	//_specialTags : ['script','form','table','ul','strong','b','i','u'],
	_specialTags : ['script','form','table','ul','strong'],
	initialize:function(){},
	/**
	 * @param String
	 * @return String
	 */
	format:function(content){
		content = content.replace(/<a\s+[^>]*>/ig,'').replace(/<\/a>/ig,"");
		var sp = new IFengSpecialTagProcessor(content, this._specialTags);
		var c = sp.getProcessedContent();
		c = this._highLightImages(c);
		var ps = c.replace(/<{0,1}br\s*\/{0,1}>/ig,'<p>').split(/<\/{0,1}p\s{0,1}[^>]*?>/ig);
		var out = sp.revertContent(this._filter(ps).join('\n'));
		//out = out.replace(/([\u4e00-\u9fa5])(\s|(&nbsp;))+/gi, "$1");
		out = out.replace(/([\u4e00-\u9fa5])(\s)+/gi, "$1");//cds add 保留空格
		return out;
	},
	_highLightImages:function(content){
		var images = new Array();
		var reg = /<img\s+[^>]*>/ig;
		var r;
		while((r = reg.exec(content))!=null){
			images.push(r[0]);
		}
		var texts = content.split(reg);
		var ps = new Array();
		for(var i=0; i<images.length; i++){
			ps.push(texts[i]);
			ps.push('<p>'+images[i]+'</p>');
		}
		ps.push(texts[texts.length-1]);
		return ps.join('');
	},
	_filter:function(ps){
		var out = new Array();
		var lastIsImage = false;
		var currentIsImage = false;
		var preTag;
		var postTag="</p>";
		var p;
		for(var i=0; i<ps.length; i++){
			p = ps[i];
			p = p.replace(/^[\s\ue5e5\u3000]*/,'').replace(/^(&nbsp;)*/,'').replace(/[\r\n]*/g,'');
			if(p == ''){
				continue;
			}
			
			if(/_fckrealelement/i.test(p)){
				currentIsImage = false;
				out.push(p);
				continue;
			}
			
			lastIsImage = currentIsImage;
			currentIsImage = /<img.*>/i.test(p);
			
			if(!currentIsImage){
				p = p.replace( new RegExp("<.*?>",'g'),'');
				p = p.replace(/^[\s\ue5e5\u3000\r\n]*/,'').replace(/^(&nbsp;)*/,'');
				if(p == ''){
					continue;
				}
			}
			postTag = "</p>";
			if(currentIsImage){
				preTag = '<p class="detailPic">';
			}else if(lastIsImage){
				preTag = '<p class="picIntro"><span>';
				postTag = "</span></p>";
			}else{
				preTag = '<p>';
			}
			out.push(preTag+p+postTag);
		}
		return out;
	}
};

IFengSpecialTagProcessor = function(){this.initialize(arguments[0],arguments[1])};
IFengSpecialTagProcessor.prototype = {
	LEFT_DELIMITER: '(((',
	RIGHT_DELIMITER: ')))',
	_replacedItems: null,
	_specialTags:null,
	_commentItems: null,
	_allTagsPattern: null,
	/**
	 * @param String content
	 * @param Array specialTags
	 */
	initialize:function(content, specialTags){
		this._content = content;
		this._specialTags = specialTags;
		this._allTagsPattern = this._buildAllTagsPattern(specialTags);
	},
	_buildAllTagsPattern:function(tags){
		var pstr = '<(';
		for(var i=0;i<tags.length;i++){
			pstr += tags[i];
			if(i<tags.length-1){
				pstr += '|';
			}
		}
		pstr += ')[^>]*>';
		return new RegExp(pstr,'ig');
	},
	getProcessedContent:function(){
		this._replacedItems = new Array();
		this._commentItems = new Array();
		var c= this._extractTags(
			this._replaceComments(
				this._replaceRNs(this._replaceBUIs(this._content))
			)
		);
		return this._revertRNs(c);
	},
	_extractTags:function(c){
		var r;
		var tag;
		var sections = new Array();
		var lastIndex = 0;
		var count = 0;
		while((r = this._allTagsPattern.exec(c))!=null){
			++count;
			var tag = this._getTagContent(c.substr(r.index),r[1]);
			sections.push(c.substring(lastIndex, r.index));
			sections.push(this.LEFT_DELIMITER+(count-1)+this.RIGHT_DELIMITER);
			this._replacedItems.push(tag);
			this._allTagsPattern.lastIndex += tag.length-r[0].length;
			lastIndex = this._allTagsPattern.lastIndex;
		}
		sections.push(c.substring(lastIndex));
		return sections.join('');
	},
	_getTagContent:function(text, tag){
		var reg = new RegExp("<\\/{0,1}"+tag+"\\s*[^>]*>","ig");
		var r;
		var stack = 0;
		while((r=reg.exec(text))!=null){
			if(r[0].indexOf('</')==0){
				--stack;
			}else{
				++stack;
			}
			if(stack==0){
				return text.substr(0, r.index+r[0].length);
			}
		}
		return text;
	},
	_replaceRNs:function(c){
		return c.replace(/\r/g,'(((r)))').replace(/\n/g,'(((n)))');
	},
	_revertRNs:function(c){
		return c.replace(/\({3}r\){3}/g, '\r').replace(/\({3}n\){3}/g, '\n');
	},
	_replaceBUIs:function(c){
		c = c.replace(/<b>/g,'(((b)))').replace(/<u>/g,'(((u)))').replace(/<i>/g,'(((i)))');
		return c.replace(/<\/b>/g,'(((/b)))').replace(/<\/u>/g,'(((/u)))').replace(/<\/i>/g,'(((/i)))');
	},
	_revertBUIs:function(c){
		c = c.replace(/\({3}b\){3}/g,'<b>').replace(/\({3}u\){3}/g,'<u>').replace(/\({3}i\){3}/g,'<i>');
		return c.replace(/\({3}\/b\){3}/g,'</b>').replace(/\({3}\/u\){3}/g,'</u>').replace(/\({3}\/i\){3}/g,'</i>');
	},
	_replaceComments:function(c){
		var p = /<\!--.*?-->/g;
		var r;
		while((r=p.exec(c))!=null){
			this._commentItems.push(r[0]);
		}
		return c.replace(p,this.LEFT_DELIMITER+'!'+this.RIGHT_DELIMITER);
	},
	_revertComments:function(c){
		for(var i=0; i<this._commentItems.length; i++){
			c = c.replace(this.LEFT_DELIMITER+'!'+this.RIGHT_DELIMITER, this._commentItems[i]);
		}
		return c;
	},
	_revertTags:function(c){
		for(var i=0; i<this._replacedItems.length;i++){
			c = c.replace(this.LEFT_DELIMITER+i+this.RIGHT_DELIMITER, this._replacedItems[i]);
		}
		return c;
	},
	revertContent:function(content){
		if(!this._replacedItems){
			this.getProcessedContent();
		}
		
		for(var i=0;i<this._replacedItems.length;i++){
			content = content.replace(this.LEFT_DELIMITER+i+this.RIGHT_DELIMITER, this._replacedItems[i]);
		}
		
		return this._revertBUIs(this._revertRNs(this._revertComments(content)));
	}
};
//================强力排版 结束========================
Ext.form.HtmlEditor.Separator = Ext.extend(Ext.util.Observable, {
	// private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            scope: this
        });
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addSeparator() 
	}	
});
Ext.form.HtmlEditor.Fill = Ext.extend(Ext.util.Observable, {
	// private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            scope: this
        });
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addFill() 
	}
});

/**
 * @author chengds
 * @class Ext.form.HtmlEditor.AddImage
 * @extends Ext.util.Observable
 * <p>插入图片</p>
 */
Ext.form.HtmlEditor.AddImage = Ext.extend(Ext.util.Observable, {
	//language text
		langTitle: '插入图片',
		langInsert:'插入',
		langCancel:'取消',
		langWindowTitle:'插入图片',
	// private
    cmd: 'addimage',
	// private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            scope: this
        });
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addButton({
			iconCls: 'x-edit-' + this.cmd,
            cls : "x-btn-icon", // x-edit-insertunorderedlist
			icon : this.cmp.resRoot + "img/image.png", 
			handler : this.addImage, 
			scope: this,
			disabled :true,
            tooltip: {
                title: this.langTitle, 
            },
            overflowText: this.langTitle
		});
	},
	addImage : function(obj) {
		var that = this.cmp;
		var domains = that.destinationDomains;
		var domain = domains[parseInt(Math.random()*domains.length)];
			
        var editor = that; 
        if (!this.imageWindow){
			var imgform = new Ext.FormPanel({ 
				region : 'center', 
				labelWidth : 55, 
				frame : true, 
				bodyStyle : 'padding:5px 5px 0', 
				autoScroll : true, 
				border : false, 
				fileUpload : true, 
				items : [{ 
					fieldLabel :'图片地址',
					xtype:'uploadfield',
					name:'img',
					width:120,
					copyButtonVisible:false,
					file_types:"image/*"
				}]
			});
			this.imageWindow = new Ext.Window({
				title : this.langWindowTitle, 
				//width : 300, 
				//height : 120, 
				border : false, 
				items : imgform , 
				closeAction: 'hide',
				buttonAlign:"center",
				buttons : [{ 
					text : this.langInsert, 
					scope:this,
					handler : function() { 
						if (!imgform.form.isValid()) {return;} 
						var img = document.createElement("img"); 
						img.src = imgform.form.getValues().img; 
						var html = '<p style="text-align:center">&nbsp;' + img.outerHTML + '&nbsp;</p>';
						if(this.sourceEditMode){
							editor.insertValueAtCursor(html);
						}else{
							editor.insertAtCursor(html); 
						}
						this.imageWindow.hide(); 
					} 
				}, { 
					text : this.langCancel, 
					scope:this,
					handler : function() { 
						this.imageWindow.hide(); 
					} 
				}] 

			});
		}else{
			this.imageWindow.getEl().frame();
		}
		this.imageWindow.show();
		//uploadWin.alignTo(obj.el,"br",[-300,3]);
    }
});

/**
 * @author chengds
 * @class Ext.form.HtmlEditor.UploadAndCutImage
 * @extends Ext.util.Observable
 * <p>上传并裁剪图片</p>
 */
Ext.form.HtmlEditor.UploadAndCutImage = Ext.extend(Ext.util.Observable, {
	//language text
		langTitle: '裁图',
		langInsert:'插入',
		langCancel:'取消',
		langWindowTitle:'裁图',
	// private
    cmd: 'cutimage',
	// private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            scope: this
        });
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addButton({
			iconCls: 'x-edit-' + this.cmd,
            cls : "x-btn-icon",
			icon : this.cmp.resRoot + "img/runTime/cut.png", 
			handler :  function(obj,e){
				this.uploadAndCutImage(obj);
			}, 
			scope: this,
			disabled :true,
            tooltip: {
                title: this.langTitle, 
            },
            overflowText: this.langTitle
		});
	},
	//上传图片并裁图
	uploadAndCutImage:function(obj,targetWidth,targetHeight){
		var that = this.cmp;
		if(this.uploadAndCutImageWindow){
			this.uploadAndCutImageWindow.getEl().frame();
		}else{
			this.uploadAndCutImageWindow = new Ext.Window({ 
				title : this.langTitle, 
				width : 350, 
				height : 170, 
				border : false, 
				layout : "fit", 
				items : [{
					xtype:'form',
					labelWidth : 55, 
					layout:'xform',
					itemId: 'insert-cutimage',
					frame : true, 
					bodyStyle : 'padding:5px 5px 0', 
					autoScroll : true, 
					border : false, 
					
					items:[{
						fieldLabel:'目标宽度',
						xtype:'numberfield',
						name:'targetWidth',
						value:targetWidth,
						width:60,
						value:300
					},{
						fieldLabel:'目标高度',
						xtype:'numberfield',
						name:'targetHeight',
						value:targetHeight,
						width:60,
						value:200
					},{
						xtype:'imagecutter',
						fieldLabel:'图片地址',
						name:'imageUrl',
						resRoot:this.resRoot,
						uploadUrl:'../intelliImage!sendfile.jhtml',//自动生成url
						AIUrl:'../intelliImage!coordinate.jhtml',//智能裁图接口
						width:138,
						targetWidth:targetWidth||300,
						targetHeight:targetHeight||200,
						copyButtonVisible:false,
						helpButtonVisible:false,
						cutRectMultiple:1,
						vtype:null,//字段类型 null,url
						AIDefaultChecked:false,//默认开启智能建议
						openCutterWindow:function(obj){
							var win = this.cutterWindow;
							var form = this.ownerCt.form;
							var values = form.getValues();
							
							values.targetWidth && values.targetHeight && win.setTargetSize(parseInt(values.targetWidth),parseInt(values.targetHeight));
							win.show();
						}
					}]
				}],
				buttonAlign:"center",
				closeAction:'hide',
				buttons : [{ 
					text : this.langInsert, 
					scope:that,
					handler:function(obj){
						var win = obj.ownerCt;
						var frm = win.getComponent('insert-cutimage').getForm();
						if (frm.isValid()) {
							var values = frm.getValues();
							var url = values.imageUrl;
							if(url){
								var img = document.createElement("img"); 
								img.src = url; 
								//img.width = values.targetWidth;
								//img.height = values.targetHeight;
								if(this.sourceEditMode){
									this.insertValueAtCursor(img.outerHTML);
								}else{
									this.insertAtCursor(img.outerHTML); 
								}
								win.hide();
							}
						}
					}
				},{
					text:'取消',
					handler:function(obj){
						obj.ownerCt.hide();
					}
				}]
			});
		}
		this.uploadAndCutImageWindow.show(); 
		//obj && win.alignTo(obj.el,"br",[-300,3]);
	}
});


/**
 * @author chengds
 * @class Ext.form.HtmlEditor.AddPageBreak
 * @extends Ext.util.Observable
 * <p>插入分页符</p>
 */
Ext.form.HtmlEditor.AddPageBreak = Ext.extend(Ext.util.Observable, {
	//language text
		langTitle: '插入分页符',
	// private
    cmd: 'addpagebreak',
	// private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            scope: this
        });
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-addpagebreak',
			cls : "x-btn-icon", // x-edit-insertunorderedlist
			icon : this.cmp.resRoot + "img/runTime/page_break_insert.png", 
            handler: this.addPageBreak,
			scope: this,
			disabled :true,
            tooltip: {
                title: this.langTitle, 
            },
            overflowText: this.langTitle
		});
	},
	//插入分页符 //todo
	addPageBreak:function(){
		var that = this.cmp;
		if(!that.activated){
			return;
		}
		if(that.sourceEditMode){
			that.insertAtCursor(that.pageBreakHtml);
		}else{
			that.insertAtCursor(that.pageBreakFakeImage);
		}
		//this.execCmd('InsertText', '\n');
		that.deferFocus();
	}
});

/**
 * @author Shea Frederick - http://www.vinylfox.com ;chengds modify
 * @class Ext.ux.form.HtmlEditor.Table
 * @extends Ext.util.Observable
 * <p>插入表格</p>
 */
Ext.form.HtmlEditor.Table = Ext.extend(Ext.util.Observable, {
	langTitle:'插入表格',
	langInsert:'插入',
	langCancel:'取消',
	langWindowTitle:'插入表格',
    // private
    cmd: 'table',
    /**
     * @cfg {Array} tableBorderOptions
     * A nested array of value/display options to present to the user for table border style. Defaults to a simple list of 5 varrying border types.
     */
    tableBorderOptions: [[' width="95%" align="center" border="1" cellpadding="1" cellspacing="1"',  '格式一'], [' style="border-collapse: collapse" align="center" border="1" bordercolor="#000000" width="520" cellpadding="1" cellspacing="1"', '格式二']],
    // private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    // private
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-table',
			cls : "x-btn-icon", 
			icon : this.cmp.resRoot + "img/runTime/table.png", 
			disabled :true,
            handler: function(){
                if (!this.tableWindow){
                    this.tableWindow = new Ext.Window({
                        title: this.langWindowTitle,
                        closeAction: 'hide',
                        items: [{
                            itemId: 'insert-table',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 60,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: '行数',
                                name: 'row',
								value:3,
                                width: 60
                            }, {
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: '列数',
                                name: 'col',
								value:4,
                                width: 60
                            }, {
                                xtype: 'combo',
                                fieldLabel: '表格样式',
                                name: 'tableStyle',
                                forceSelection: true,
                                mode: 'local',
                                store: new Ext.data.SimpleStore({
                                    autoDestroy: true,
                                    fields: ['spec', 'val'],
                                    data: this.tableBorderOptions
                                }),
								editable:false,
                                triggerAction: 'all',
                                value: ' width="95%" align="center" border="1" cellpadding="1" cellspacing="1"',
                                displayField: 'val',
                                valueField: 'spec',
                                width: 90
                            }]
                        }],
                        buttons: [{
                            text: this.langInsert,
                            handler: function(){
                                var frm = this.tableWindow.getComponent('insert-table').getForm();
                                if (frm.isValid()) {
                                    var tableStyle = frm.findField('tableStyle').getValue();
                                    var rowcol = [frm.findField('row').getValue(), frm.findField('col').getValue()];
                                    if (rowcol.length == 2 && rowcol[0] > 0 && rowcol[0] < 10 && rowcol[1] > 0 && rowcol[1] < 10) {
                                        var html = "\r<table"+ tableStyle +">";
                                        for (var row = 0; row < rowcol[0]; row++) {
                                            html += "\r\n\t<tr>\r\n";
                                            for (var col = 0; col < rowcol[1]; col++) {
                                                html += "\t\t<td>&nbsp;</td>\r\n";
                                            }
                                            html += "\t</tr>";
                                        }
                                        html += "\r\n</table>\r\n";
                                        this.cmp.insertAtCursor(html);
                                    }
                                    this.tableWindow.hide();
                                }else{
                                    if (!frm.findField('row').isValid()){
                                        frm.findField('row').getEl().frame();
                                    }else if (!frm.findField('col').isValid()){
                                        frm.findField('col').getEl().frame();
                                    }
                                }
                            },
                            scope: this
                        }, {
                            text: this.langCancel,
                            handler: function(){
                                this.tableWindow.hide();
                            },
                            scope: this
                        }]
                    });
                
                }else{
                    this.tableWindow.getEl().frame();
                }
                this.tableWindow.show();
            },
            scope: this,
            tooltip: {
                title: this.langTitle
            },
            overflowText: this.langTitle
        });
    }
});

/**
 * @author chengds
 * @class Ext.form.HtmlEditor.FullScreen
 * @extends Ext.util.Observable
 * <p>全屏</p>
 */
Ext.form.HtmlEditor.FullScreen = Ext.extend(Ext.util.Observable, {
        //language text
        langFullScreen: '全屏',
        langExitFullScreen: '退出全屏',
    // private
    cmd: 'fullscreen',
    // private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            //'editmodechange': this.editModeChange,
            scope: this
        });
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-fullscreen',
			cls : "x-btn-icon",
			icon : this.cmp.resRoot + "img/runTime/fullscreen.gif", 
			enableToggle:false,
			sourceEditStateEnable:2,
            handler: this.toggleFullScreen,
			scope: this,
            tooltip: {
                title: this.cmp.isFullScreen?this.langExitFullScreen:this.langFullScreen, 
            },
            overflowText: this.cmp.isFullScreen?this.langExitFullScreen:this.langFullScreen
		});
		
	},
	//全屏切换
	toggleFullScreen:function(fullScreen){
		var that = this.cmp;
		if(!that.isFullScreen){
			//全屏
			var config = Ext.applyDeep(that.initialConfig,{
				value:that.getValue(),
				isFullScreen:true,
				editMode:that.sourceEditMode?2:1
			});
			delete config.id;
			//var win = that.fullScreenWindow;
			var fullScreenEditor = new Ext.form.HtmlEditor2(config);
			fullScreenEditor.field = that;
			var size = Ext.getBody().getSize();
			var win = new Ext.Window({
				layout:'fit',
				header :false,
				modal:true,
				closable :false,
				border:false,
				frame:false,
				width:size.width,
				height:size.height,
				items:[fullScreenEditor]				
			});
			//that.fullScreenWindow = win;
			win.show();
		}else{
			//退出全屏
			var field = that.field;
			field.setValue(that.getValue());
			that.ownerCt.close();
			field.focus();
			if(that.sourceEditMode!=field.sourceEditMode){
				field.toggleSourceEdit(that.sourceEditMode);
			}
		}
	}
});

/**
 * @author Shea Frederick - http://www.vinylfox.com ,chengds modify
 * @contributor Ronald van Raaphorst - Twensoc
 * @class Ext.form.HtmlEditor.FindReplace
 * @extends Ext.util.Observable
 * <p>A plugin that provides search and replace functionality in design edit mode. Incomplete.</p>
 */
Ext.form.HtmlEditor.FindAndReplace = Ext.extend(Ext.util.Observable, {
        // Find and Replace language text
        langTitle: '查找/替换',//'Find/Replace',
        langFind: '查找下一个',//'Find',
        langReplace:'替换',// 'Replace',
		langReplaceAll:'全部替换',//'Replace All',
        langReplaceWith:'替换为',// 'Replace with',
        langClose: '取消',//'Close',
		langMatchCase:'匹配大小写',//'Match Case',
    // private
    cmd: 'findandreplace',
    // private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
            //'editmodechange': this.editModeChange,
            scope: this
        });
        this.lastSelectionStart=-1;
    },
    editModeChange: function(t, m){
        if (this.btn && m){
            this.btn.setDisabled(false);
        }
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-findandreplace',
			cls:'x-btn-icon',
			icon : this.cmp.resRoot + "img/runTime/find.gif", 
			sourceEditStateEnable:false,
            handler: function(){
				this._setToStart();
                if (!this.farWindow){
                
                    this.farWindow = new Ext.Window({
                        title: this.langTitle,
                        closeAction: 'hide',
						layout:'column',
						width:350,
						buttonAlign:'center',
                        items: [{
							columnWidth: .68,
                            itemId: 'findandreplace',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 80,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'textfield',
                                allowBlank: false,
                                fieldLabel: this.langFind,
                                name: 'find',
                                width: 120
                            }, {
                                xtype: 'textfield',
                                allowBlank: true,
                                fieldLabel: this.langReplaceWith,
                                name: 'replace',
                                width: 120
                            }, {
                                xtype: 'checkbox',
                                fieldLabel: this.langMatchCase,
                                name: 'matchcase',
								boxLabel:' ',
								style:'left:0px;top:4px;position:relative',
                                width: 120
                            }]
                        },{
							columnWidth: .32,
							border:false,
							plain: true,
                            bodyStyle: 'padding: 10px;',
							items:[{
								xtype:'button',
								style:'margin-bottom:4px;min-width:88px',
								text:this.langFind,
								handler:this._searchAndReplaceNext,
								scope: this
							},{
								xtype:'button',
								style:'margin-bottom:4px;min-width:88px',
								text:this.langReplace,
								handler:this._replace,
								scope: this
							},{
								xtype:'button',
								style:'margin-bottom:4px;min-width:88px',
								text:this.langReplaceAll,
								handler:this._replaceAll,
								scope: this
							}]
						}],
                        buttons: [{
                            text: this.langClose,
                            handler: function(){
                                this.farWindow.hide();
                            },
                            scope: this
                        }]
                    });
                
                }else{
                    
                    this.farWindow.getEl().frame();
                    
                }
                this.farWindow.show();
                this.farForm = this.farWindow.getComponent('findandreplace').getForm();
            },
            scope: this,
            tooltip: {
                title: this.langTitle
            },
            overflowText: this.langTitle
        });
    },
	// set to start (search and replace)
    _setToStart: function () {
        // get collection of lines in the content
        this._lines = this.getLines();
        // reset the current line text
        this._line_text = "";
        // reset the current line number
        this._line_number = -1;
        // set the current found text position to MAX
        this._text_position = Number.MAX_VALUE;
        // reset the current found text length
        this._text_length = 0;
    },
	// check whether to save the original content
    _checkChanges: function () {
        // if first change - save the content
        if (!this._contentChanged) {
            //todo
            // set the flag
            this._contentChanged = true;
        }
    },
    // search/replace next match
    _searchAndReplaceNext: function (replace) {
		
        if (!this.farForm.isValid()) {
            return '';
        }
        var findValue = this.farForm.findField('find').getValue();
		var matchcaseValue = this.farForm.findField('matchcase').getValue();
		
        // no replace yet
        this._just_replaced = false;
        // found flag
        var found = false;
        // unhighlight the previously found word
        this.unhighlight();
        // if there is what to search for
        if (findValue.length > 0) {
            // search value
            var searchValue = matchcaseValue?findValue.toLowerCase():findValue; 
            // search the text
            while (!found) {
                // move farther
                this._text_position += this._text_length;
                // end of the line reached?
                if (this._text_position >= this._line_text.length) { // yes
                    this._line_number++; // next line number
                    if (this._line_number >= this._lines.length) { // lines over
                        // leave the loop
                        break;
                    }
                    // set the current found text position to start
                    this._text_position = 0;
                    // reset the current found text length
                    this._text_length = 0;
                    // get the current line text
                    this._line_text = this.getLineText(this._lines[this._line_number]);
                    if (matchcaseValue) {
                        this._line_text = this._line_text.toLowerCase();
                    }
                }
                // try to find the search text
                var index = this._line_text.indexOf(searchValue, this._text_position);
                if (index >= 0) { //found
                    // length of the found text
                    this._text_length = searchValue.length;
                    // position of the found text
                    this._text_position = index;
                    // set the found flag
                    found = true;
                } else { // not found
                    // set the current found text position to MAX
                    // (force the line end)
                    this._text_position = Number.MAX_VALUE;
                }
            }

            if (found) { // found
                // replace?
                if (typeof replace == "string") {
                    // replace the found text

                    // text before the found
                    var start = this._line_text.substr(0, this._text_position);
                    // text after the found
                    var end = this._line_text.substr(this._text_position + this._text_length);
                    // concatinate the new line
                    this._line_text = start + replace + end;
                    // another "found" text length now
                    this._text_length = replace.length;
                    // set the flag
                    this._just_replaced = true;
                    // update the line
                    this._lines = this.updateLine(this._lines[this._line_number], this._line_text);
                    // move cursor to end of the new text
                    this.highlight(this._lines[this._line_number], this._text_position + this._text_length, 0);
                } else {
                    // just highlight it
                    this.highlight(this._lines[this._line_number], this._text_position, this._text_length);
                }
				//在屏幕可见
				this.cmp.doc.body.scrollTop = 0;
				Ext.fly(this._lines[this._line_number].parentNode).scrollIntoView(this.cmp.doc.body);
            } else {
                // not found - start from the begining
                this._setToStart();
                setTimeout(function () { Ext.CMPP.alert("","到末尾了"); }, 0);
            }
        }
        this._found = found;
        return found; // whether the search text was found
    },

    // _replace current match
    _replace: function () {
			var replaceValue = this.farForm.findField('replace').getValue();
	
            if (!this._just_replaced) {
                this._text_length = 0; // just found
            }
            // if was found something
            if (this._found) {
                if (this._searchAndReplaceNext(replaceValue)) { // if found and replaced
                    // then search again
                    this._searchAndReplaceNext();
					this.cmp.syncValue();
                }
            }else{
				this._searchAndReplaceNext();
			}
    },

    // replace all matches
    _replaceAll: function () {
            // start from the begining
            this._setToStart();
            if (!this._just_replaced) {
                this._text_length = 0; // just found
            }
            // replace all
			var replaceValue = this.farForm.findField('replace').getValue();
            while (this._searchAndReplaceNext(replaceValue)) { }
			this.cmp.syncValue();
    },
	    // update the line's text
    updateLine: function (descriptor, newText) {
        descriptor.data = newText;
        // it should be changed
        return this.getLines();
    },

    // get text via the line descriptor
    getLineText: function (descriptor) {
        return descriptor.data + "";
    },

    // get array of all line descriptors
    getLines: function () {
        // array to be returned
        var descriptors = [];
        // get all text nodes
        function recurse(element) {
            if (element.nodeType == 3) {
                if (element.nodeValue != '') {
                    descriptors.push(element);
                }
            } else {
                if (element.childNodes && element.childNodes.length > 0) {
                    for (var i = 0; i < element.childNodes.length; i++) {
                        recurse(element.childNodes[i]);
                    }
                }
            }
        }
        recurse(this.cmp.doc.body);
        return descriptors;
    },

    // unhighlight previously selected text
    unhighlight: function (prize) {
        // was something highlighted and no popup be closed?
        if (!prize && this._highlighted != null) {
            // text node where the previously selected text is in
            var textNode = this._highlighted.textNode;
            // end position of this text
            var position = this._highlighted.position + this._highlighted.length;
            // reset the saved information
            this._highlighted = null;
            // try to set cursor to the end of the previously selected text
            this.highlight(textNode, position, 0);
        }
    },

    // highlight text
    highlight: function (linePointer, position, length) {
        // unhighlight before
        if (this._highlighted != null) {
            this.unhighlight();
        }

        var me = this;
        setTimeout(function () {
             // all not IE browsers
                // get current selection
                var sel = me._getSelection();
                // get range
                var range = me.cmp.doc.createRange();
                // set positions for the range
                range.setStart(linePointer, position);
                range.setEnd(linePointer, position + length);
                // select the range
				me._removeAllRanges(sel);
                me._selectRange(sel, range);            
        }, 0);
        // save the current selected area
        this._highlighted = { textNode: linePointer, position: position, length: length };
    },
	_getSelection: function() {
		this.cmp.focus();
		var a, b, c;
		if (this.cmp.win == null)
			return null;
		var d = this.cmp.win;
		a = d.getSelection();
		b = this._createRange(a);
		c = b.startContainer;
		try {
			while (c && c.nodeType)
				c = c.parentNode
		} catch (e) {
			this._removeAllRanges(a);
			b = this._createRange(a);
			b.setStart(this.cmp.doc.body, 0);
			b.setEnd(this.cmp.doc.body, 0);
			this._selectRange(a, b);
			a = d.getSelection()
		}
		return a
    },
	_createRange: function(a) {
		this.cmp.focus();
		if (typeof a == "undefined")
			return this.cmp.doc.createRange();
		else{
			try {
				return a.getRangeAt(0)
			} catch (c) {
				return this.cmp.doc == null ? null : this.cmp.doc.createRange()
			}
        }
    },_removeAllRanges: function(a) {
        a.removeAllRanges()
    },_selectRange: function(b, a) {
        b.addRange(a);
        this.cmp.focus()
    }


});


/**
 * @author chengds
 * @class Ext.form.HtmlEditor.RedoUndo
 * @extends Ext.util.Observable
 * <p>全屏</p>
 */
Ext.form.HtmlEditor.RedoUndo = Ext.extend(Ext.util.Observable, {
        //language text
        langRedo: '重做',
        langUndo: '撤销',
    // private
    cmd: 'redoundo',
	
	// size parameter limits the rollback history
	volume:-1,
	history:[],
	index :0,
	placeholder:0,
	count:0,
	ignore:false,

	
    // private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on({
            'render': this.onRender,
			'editmodechange':this.onEditModeChange,
			'sync':function() {
				if (this.ignore) {
					this.ignore = false;
				}else {
					this.record();
				}
			},
            scope: this
        });
    },
    // private
    onRender: function(){
        this.btn = this.cmp.getToolbar().insertButton(2,[new Ext.Toolbar.Separator(),{
            iconCls: 'x-edit-' + this.cmd,
			cls : "x-btn-icon",
			icon : this.cmp.resRoot + "img/runTime/undo.png", 
			itemId: 'undo',
			clickEvent :'mousedown',
			sourceEditStateEnable:2,
			disabled :true,
            handler: this.undo,
			scope: this,
            tooltip: {
                title: this.langUndo, 
            }
		},{
            iconCls: 'x-edit-' + this.cmd,
			cls : "x-btn-icon",
			icon : this.cmp.resRoot + "img/runTime/redo.png", 
			itemId: 'redo',
			clickEvent :'mousedown',
			sourceEditStateEnable:2,
			disabled :true,
            handler: this.redo,
			scope: this,
            tooltip: {
                title: this.langRedo, 
            }
		},new Ext.Toolbar.Separator()]);
		
		// monitor for ctrl-z (undo) and ctrl-y (redo) keys
		var keyCommands = [{
			key: 'z',
			ctrlKey: true,
			fn: this.undo,
			scope: this
		}, {
			key: 'y',
			ctrlKey: true,
			fn: this.redo,
			scope: this
		}];
        new Ext.KeyMap(this.cmp.getEditorBody(), keyCommands);

          // record changed data when in source edit mode
        this.cmp.el.on('keyup', this.record, this);
		
	},
	onEditModeChange:function() {
			var editor = this.cmp;
          // set a placeholder when source edit mode is selected
          if (editor.sourceEditMode) {
            this.placeholder = this.index;
          }
  
          // else record all changes made in source edit mode as a
          // single historic entry.
          // note: undo/redo functions continue to work while in
          // source edit mode (even when undoing changes made before
          // the mode was changed), but those made while in source
          // edit mode are no longer available once source edit mode
          // is exited as they can appear undesirable or meaningless
          // when in normal edit mode, so they are rolled together
          // to form a single historic change
          else {

            // if changes were made while in source edit mode then
            if (this.index > this.placeholder) {
  
              // if starting point was lost to history then
              if (this.placeholder < 0) {

                // record all source edit mode changes as first
                // historic record
                this.placeholder == 0;
                this.history[this.placeholder] = this.history[this.index];
              }

              // else check to see if data has actually changed
              // while in source edit mode then
              else if (this.history[this.placeholder].content != this.history[this.index].content) {
    
                // record all source edit mode changes as single
                // historic record, to follow last record change
                // made in normal edit mode
                this.placeholder++;
                this.history[this.placeholder] = this.history[this.index];
              }

              // reset index and count to placeholder
              this.index = this.placeholder;
              this.count = this.index;
            }

            // if no changes were made then reset count as it
            // may have grown if changes were made and reversed
            else {
              this.count = this.placeholder;
            }
  
            // update the undo/redo buttons on the toolbar
            this._updateToolbar();
          }
	},
	// IE only: updates the toolbar buttons
	_updateToolbar:function() {
		var tb = this.cmp.getToolbar();
		tb.items.map.undo.setDisabled(this.index < 2);
		tb.items.map.redo.setDisabled(this.index == this.count);
	},

	// IE only: updates the editor body
	_reset:function() {
		this.cmp.getEditorBody().innerHTML = this.history[this.index].content;
		this.cmp.syncValue();
		//this._resetBookmark();//todo 重定位光标位置
	},

	// IE only: updates the element (when in source edit mode)
	_resetElement:function() {
		this.cmp.el.dom.value = this.history[this.index].content;
		this._resetBookmark();
	},

	// IE only: reposition the cursor
	_resetBookmark :function() {
		//todo
		var selection = this.cmp.doc.getSelection(); 
		
		var bookmark = this.history[this.index].bookmark;
		var range = this.cmp.doc.createRange();
		
		// set positions for the range
		var node = this.cmp.doc.body.firstChild.firstChild;

		range.setStart(node, bookmark.startOffset);
		range.setEnd(node, bookmark.startOffset);
		selection.addRange(range);
		selection.collapse();
	},
 
    // IE only: record changes to data
    record: function() {
		var editor = this.cmp;
      // get the current html content from the element
      var content = editor.el.dom.value;

      // if no historic records exist yet or content has
      // changed since the last record then
      if (this.index == 0 || this.history[this.index].content != content) {

        // if size of rollbacks has been reached then drop
        // the oldest record from the array
        if (this.count == this.volume) {
          this.history.shift();
          this.placeholder--;
        }

        // else increment the index
        else {
          this.index++;
        }
		
        // record the changed content and cursor position
		//var range = this.cmp.win.getSelection().rangeCount>0?this.cmp.win.getSelection().getRangeAt(0):null;
        this.history[this.index] = {
          content: content/*,
		  bookmark: {startOffset:range.startOffset,line:range.startContainer}*///cds todo
        };
        this.count = this.index;
      }

      // update the undo/redo buttons on the toolbar
      this._updateToolbar();
    },
    
    // IE only: perform the undo
    undo: function() {
		var editor = this.cmp;
      // ensure that there is data to undo
      if (this.index > 1) {

        // decrement the index
        this.index--;

        // if in source edit mode then update the element directly
        if (editor.sourceEditMode) {
          this._resetElement();
        }

        // else update the editor body
        else {
          this._reset();

          // ignore next record request as syncValue is called
          // by Ext.form.HtmlEditor.updateToolBar and we don't
          // want our undo reversed again
          this.ignore = true;

          // update the editor toolbar and return focus
          //editor.updateToolbar();
          editor.deferFocus();
        }

        // update the undo/redo buttons on the toolbar
        this._updateToolbar();
      }
    },

    // IE only: perform the redo
    redo: function() {
		var editor = this.cmp;
      // ensure that there is data to redo
      if (this.index < this.count) {

        // increment the index
        this.index++;

        // if in source edit mode then update the element directly
        if (editor.sourceEditMode) {
          this._resetElement();
        }

        // else update the editor body
        else {
          this._reset();

          // ignore next record request as syncValue is called
          // by Ext.form.HtmlEditor.updateToolBar and we don't
          // want our redo reversed again
          this.ignore = true;

          // update the editor toolbar and return focus
          //editor.updateToolbar();
          editor.deferFocus();
        }

        // update the undo/redo buttons on the toolbar
        this._updateToolbar();
      }
    },
	_getSelection: function() {
		this.cmp.focus();
		var a, b, c;
		if (this.cmp.win == null)
			return null;
		var d = this.cmp.win;
		a = d.getSelection();
		b = this._createRange(a);
		c = b.startContainer;
		try {
			while (c && c.nodeType)
				c = c.parentNode
		} catch (e) {
			this._removeAllRanges(a);
			b = this._createRange(a);
			b.setStart(this.cmp.doc.body, 0);
			b.setEnd(this.cmp.doc.body, 0);
			this._selectRange(a, b);
			a = d.getSelection()
		}
		return a
    },
	_createRange: function(a) {
		this.cmp.focus();
		if (typeof a == "undefined")
			return this.cmp.doc.createRange();
		else{
			try {
				return a.getRangeAt(0)
			} catch (c) {
				return this.cmp.doc == null ? null : this.cmp.doc.createRange()
			}
        }
    },_removeAllRanges: function(a) {
        a.removeAllRanges()
    },_selectRange: function(b, a) {
        b.addRange(a);
        this.cmp.focus()
    }

  	
	
});


/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.Link
 * @extends Ext.util.Observable
 * <p>A plugin that creates a button on the HtmlEditor for inserting a link.</p>
 */
Ext.form.HtmlEditor.Link = Ext.extend(Ext.util.Observable, {
    // Link language text
    langTitle   : 'Insert Link',
    langInsert  : 'Insert',
    langCancel  : 'Cancel',
    langTarget  : 'Target',
    langURL     : 'URL',
    langText    : 'Text',
    // private
    linkTargetOptions: [['_self', 'Default'], ['_blank', 'New Window'], ['_parent', 'Parent Window'], ['_top', 'Entire Window']],
    init: function(cmp){
        cmp.enableLinks = false;
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-createlink',
            handler: function(){
                var sel = this.cmp.getSelectedText();
                if (!this.linkWindow) {
                    this.linkWindow = new Ext.Window({
                        title: this.langTitle,
                        closeAction: 'hide',
                        width: 250,
                        height: 160,
                        items: [{
                            xtype: 'form',
                            itemId: 'insert-link',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 40,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'textfield',
                                fieldLabel: this.langText,
                                name: 'text',
                                anchor: '100%',
                                value: sel.textContent,
                                disabled: sel.hasHTML
                            }, {
                                xtype: 'textfield',
                                fieldLabel: this.langURL,
                                vtype: 'url',
                                name: 'url',
                                anchor: '100%',
                                value: 'http://'
                            }, {
                                xtype: 'combo',
                                fieldLabel: this.langTarget,
                                name: 'target',
                                forceSelection: true,
                                mode: 'local',
                                store: new Ext.data.ArrayStore({
                                    autoDestroy: true,
                                    fields: ['spec', 'val'],
                                    data: this.linkTargetOptions
                                }),
                                triggerAction: 'all',
                                value: '_self',
                                displayField: 'val',
                                valueField: 'spec',
                                anchor: '100%'
                            }]
                        }],
                        buttons: [{
                            text: this.langInsert,
                            handler: function(){
                                var frm = this.linkWindow.getComponent('insert-link').getForm();
                                if (frm.isValid()) {
                                    var afterSpace = '', sel = this.cmp.getSelectedText(true), text = frm.findField('text').getValue(), url = frm.findField('url').getValue(), target = frm.findField('target').getValue();
                                    if (text.length && text[text.length - 1] == ' ') {
                                        text = text.substr(0, text.length - 1);
                                        afterSpace = ' ';
                                    }
                                    if (sel.hasHTML) {
                                        text = sel.html;
                                    }
                                    var html = '<a href="' + url + '" target="' + target + '">' + text + '</a>' + afterSpace;
                                    this.cmp.insertAtCursor(html);
                                    this.linkWindow.close();
                                } else {
                                    if (!frm.findField('url').isValid()) {
                                        frm.findField('url').getEl().frame();
                                    } else if (!frm.findField('target').isValid()) {
                                        frm.findField('target').getEl().frame();
                                    }
                                }
                                
                            },
                            scope: this
                        }, {
                            text: this.langCancel,
                            handler: function(){
                                this.linkWindow.close();
                            },
                            scope: this
                        }],
                        listeners: {
                            show: {
                                fn: function(){
                                    var frm = this.linkWindow.getComponent('insert-link').getForm();
                                    frm.findField('url').focus(true, 50);
                                },
                                scope: this,
                                defer: 200
                            }
                        }
                    });
                } else {
                    this.linkWindow.getEl().frame();
                }
                this.linkWindow.show();
            },
            scope: this,
            tooltip: this.langTitle
        });
    }
});

Ext.reg('htmleditor2', Ext.form.HtmlEditor2); 