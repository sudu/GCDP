/*
*description:手动、自动、半自动裁图并上传
*author:chengds@ifeng.com
*date:2013-03-19
*/
Ext.ux.ImageCutter = function (config) {
	if(!Ext.nore(config.regex))
		config.regex=Ext.decode(config.regex);		
	Ext.ux.ImageCutter.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.ImageCutter, Ext.form.TextField, {
	/*UI*/
	
	width:300,
	name:'ImageCutter',
	vtype:'url',
	copyButtonVisible:false,
	viewButtonVisible:true,
	helpButtonVisible:false,
	infoEl:null,
	helpHtml:null,
	resRoot:'../res/',
	/*接口配置*/
	uploadUrl:'../intelliImage!sendfile.jhtml',//自动生成url
	AIUrl:'../intelliImage!coordinate.jhtml',//智能裁图接口
	AIDefaultChecked:true,//智能裁图选项默认选中
	// Button Settings
	button_text:'裁图&上传',
	// copy flash Settings
	clipBoardSwf_url : "../res/swf/clipBorad-Xuas.swf",//Author:许爱思
	/*上传配置*/
	file_types:"image/*",//对应file控件的accept属性；eg.accept="image/*" accept="image/gif, image/jpeg" accept="text/html"
	destinationDomains:['y0.ifengimg.com','y1.ifengimg.com','y2.ifengimg.com','y3.ifengimg.com'],//分发的目标域名，支持多个域名随机
	clipSwfDiv:null,
	
/*裁图参数配置*/
	targetWidth:300,
	targetHeight:200,
	cutRectMultiple:0.75,//默认显示的切割区域倍数
    initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.clipBoardSwf_url=this.resRoot + "swf/clipBorad-Xuas.swf";
		if(this.cutRectMultiple>1) this.cutRectMultiple=1;
		if(this.cutRectMultiple<0) this.cutRectMultiple=0;
		
	    Ext.ux.ImageCutter.superclass.initComponent.call(this);
		if(typeof(this.destinationDomains)=='string'){
			try{
				this.destinationDomains = Ext.decode(this.destinationDomains);
			}catch(ex){
				
			}
		}
	},
	onRender: function (ct, position) {

		Ext.ux.ImageCutter.superclass.onRender.call(this, ct, position);

		var elParent = this.el.parent();
		var btnCtId = this.id + '_btnCt_swf';	
		this.el.setStyle({"cssFloat":"left"});
		elParent = elParent.createChild({
			tag:'div',
			style:'float:left;'
		});
		this.buttonEl = elParent.createChild({
			tag:'button',
			cls:'UploadTool-ct-button',
			html:this.button_text
		});
	
		//预览按钮
		if(this.viewButtonVisible!=false){
			var previewEl = elParent.createChild({
				tag:'a',
				style:'margin-left:5px;',
				title:'查看文件',
				href:'javascript:void(0);',
				html:'查看'
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

		//帮助按钮
		if(this.helpHtml && this.helpButtonVisible!=false){
			btnHelp = elParent.createChild({//显示字数的容器
				tag:'a',
				style:'margin-left:5px;',
				href:'javascript:void(0);',
				title:'查看帮助',
				html:'帮助'
			});
			//初始化帮助
			new Ext.ux.ToolTip({
				target:btnHelp,
				closable :true,
				autoHide :false,
				draggable:true,
				miniWidth: 150,
				showDelay :50,
				title: '帮助',
				html :this.helpHtml
			});
		}
		
		//裁图窗口
		var okHandler = function(){
			var imageData = this.cutterWindow.getValue();
			this.uploadImage(imageData);			
		}
		this.cutterWindow = new Ext.ux.ImageCutterWindow({
			title:'裁图窗口',
			//width:675, 
			//height:440,
			AIUrl:this.AIUrl,
			OKHandler:okHandler,
			contex:this,
			cutterUrl:this.cutterUrl,
			targetWidth:this.targetWidth,
			targetHeight:this.targetHeight,
			AIDefaultChecked:this.AIDefaultChecked
		});
		this.buttonEl.on("click",function(obj){
			this.openCutterWindow(obj);
		},this);
		
		//this.cutterWindow.show();//cds debug
	},
	openCutterWindow:function(obj){
		this.cutterWindow.show();
	},
	setTargetSize:function(targetWidth,targetHeight){
		if(this.targetWidth!=targetWidth || this.targetHeight!=targetHeight){
			this.targetWidth = targetWidth;
			this.targetHeight = targetHeight;
			this.cutterWindow.setTargetSize(targetWidth,targetHeight);
		}
	},
	_getHandler:function(handler,_this){
		var t = _this;
		var h = handler;

		return function(){
			t[h].apply(t,arguments);
		}
	},
	uploadImage:function(fileData){
		this.uploadStart();
		var xhr = new XMLHttpRequest();
		xhr.timeout=2*60*1000;
		var t = this;
		if (xhr.upload) {
			try{
				// 上传中
				xhr.ontimeout = t.uploadTimeout.createDelegate(t,[xhr].true);
				xhr.upload.onprogress = t.updateProgress.createDelegate(t,[xhr],true);
				xhr.onreadystatechange=t.uploadComplete.createDelegate(t,[xhr],true);
				// 开始上传
				var postUrl = this.uploadUrl;
				xhr.open("POST", postUrl, true);
				var domains = this.destinationDomains;
				domain = domains[parseInt(Math.random()*domains.length)];
				//使用FormData处理需要上传的数据
				var fd = new FormData();
				fd.append("domain",domain);
				fd.append("ext",this.cutterWindow.ccImageCutter.fileExtent);
				fd.append("width", t.targetWidth);
				fd.append("height", t.targetHeight);
				fd.append("image", fileData);
				xhr.send(fd);
			}catch(ex){
				Ext.CMPP.warn("错误",ex.message);
				this.cutterWindow.body.unmask();
			}
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
		var infoEl = this.infoEl;
		if(!infoEl)return;
		infoEl.setStyle('color','green');	
		infoEl.setVisible(true);
		infoEl.update('已复制');
		infoEl.fadeOut({
			duration: 1,
			callback:function(){
				infoEl.update('');
			}
		});
	},
	uploadStart:function(){
		this.cutterWindow.body.mask('正在裁剪图片...');
		
		return true;
	},
	updateProgress:function(e,xhr){
		if (e.lengthComputable) {
			var percentComplete = e.loaded / e.total *100;
			console.info(percentComplete);//cds
		}
	},
	uploadTimeout:function(e,xhr){
		this.cutterWindow.body.unmask();
		Ext.CMPP.warn("错误",'超时!');	
	},
	uploadComplete:function(e,xhr){
		if (xhr.readyState == 4) {
			var result=0;
			this.cutterWindow.body.unmask();
			if (xhr.status == 200) {
				//成功 xhr.responseText
				try{
					var ret = Ext.decode(xhr.responseText);
				}catch(ex){
					ret={success:false,msg:'返回结果格式有误'};
				}
				if(ret.success){
					var fileUrl = ret.result;
					this.setValue(fileUrl);
					Ext.CMPP.alert("提示","图片处理成功");
					this.cutterWindow.hide();
				}else{
					Ext.CMPP.warn("错误",'图片处理失败!' + ret.msg);
					this.cutterWindow.hide();
				}
			}else{
				Ext.CMPP.warn("错误",'图片处理出错!');	
			}
		}
	}	

});

/*裁图窗口*/
Ext.ux.ImageCutterWindow = function (config) {	
	Ext.ux.ImageCutterWindow.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.ImageCutterWindow, Ext.Window, {
	title:'裁图窗口',
	width:648, 
	height:430,
	buttonAlign: "center",
	closable:true ,
	closeAction:'hide',
	maximizable :false,
	autoScroll:true,
	modal:true,
	//layout:'xform',
	bodyStyle:'padding:5px;background:#fff',
	resizable :false,
	AIDefaultChecked:true,
	AIUrl:'',
	value:'',
//其他
		
//对外接口
	contex:null,//上下文控件
	OKHandler:null,//点确定按钮时执行的函数
	cutterUrl:'',//裁图接口地址
	targetWidth:300,//目标尺寸
	targetHeight:250,
	buttons:[{
		text:"确定",
		handler:function(){
			var win = this.ownerCt;
			if(typeof win.OKHandler =="function"){
				win.OKHandler.call(win.contex);
			}
		}
	},{
		text:"取消",
		handler:function(){
			var win = this.ownerCt;
			win.hide();
		}
	}],
	listeners:{
		"show":function(win){
			if(!this.ccImageCutter)
				this.initCutterUI();
		}
	},
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
	    Ext.Window.superclass.initComponent.call(this);
	},
	initCutterUI:function(){
		var cutter = new ccImageCutter(this.body,this);
		cutter.targetWidth=this.targetWidth;
		cutter.targetHeight=this.targetHeight;
		cutter.AIUrl = this.AIUrl;
		cutter.AIDefaultChecked = this.AIDefaultChecked;
		cutter.contex = this;
		this.ccImageCutter = cutter;
		cutter.init();
	},
	setTargetSize:function(targetWidth,targetHeight){
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.ccImageCutter && this.ccImageCutter.setTargetSize(targetWidth,targetHeight);		
	},
	//设置源图片地址
	setValue:function(imgUrl){
		this.value = imgUrl;
	},
	//获取裁剪后图片地址
	getValue:function(){
		return this.ccImageCutter.getValue();
	}
});

/*
*裁图
*author:chengds
*/
function ccImageCutter(container,contex){
	this.container = container;
	this.contex = contex;
}
ccImageCutter.prototype.constructor = ccImageCutter;
ccImageCutter.prototype.container = null;//容器
ccImageCutter.prototype.targetWidth = 300;//目标图片宽度
ccImageCutter.prototype.targetHeight = 300;//目标图片高度
ccImageCutter.prototype.contex = null;//上下文
ccImageCutter.prototype.originalImageUrl = null;//原始图片URL
ccImageCutter.prototype.canvasMaxWidth = 300;//画布最大宽度
ccImageCutter.prototype.canvasMaxHeight = 300;//画布最大宽度
ccImageCutter.prototype.canvasWidth = 300;//画布宽度
ccImageCutter.prototype.canvasHeight = 300;//画布宽度
ccImageCutter.prototype.originalWidth = 0;//图片的原始尺寸
ccImageCutter.prototype.originalHeight = 0;//
ccImageCutter.prototype.topMaskEl = null;//上部分遮罩层
ccImageCutter.prototype.cutterRectEl = null;//中间矩形可视
ccImageCutter.prototype.dragHandlerEl = null;//拖拽把柄
ccImageCutter.prototype.chkSuggest = null;//是否启用智能建议
ccImageCutter.prototype.leftImageCt = null;
ccImageCutter.prototype.leftImage = null;
ccImageCutter.prototype.sizeCache = {};//尺寸缓存
ccImageCutter.prototype.AIDefaultChecked=true;
ccImageCutter.prototype.AIUrl = "";//智能裁图接口地址
ccImageCutter.prototype.init = function(){
	this.initUI();
};
ccImageCutter.prototype.initUI = function(){
	var ct = this.container;
	var ct2 = ct.createChild({
		style:'float:left'
	})
	var leftCt = ct2.createChild({
		style:'float: left;font-size:12px;'
	});
	var rightCt = ct2.createChild({
		style:'float: left;font-size:12px;'
	});
	
	var leftTopCt = leftCt.createChild({
		tag:'fieldset',
		style:'font-size: 14px;margin-left:3px; padding-bottom: 4px; padding-top: 4px; border-width: 0px;'
	});
	var leftCenterCt = leftCt.createChild({
		tag:'div',
		style:'border: 2px dotted #888888; width: '+ this.canvasMaxWidth +'px; height: '+ this.canvasMaxHeight +'px; overflow: hidden; position: relative; top: 0px; left: 0px; margin: 4px;'
	});
	var uploadButton = leftTopCt.createChild({
		tag:'button',
		style:'font-size:12px;',
		html:'＋本地图片＋'
	});
	var chkSuggest = leftTopCt.createChild({
		tag:'label',
		style:'margin-left:5px;',
		html:'使用智能裁图建议'
	}).insertHtml('afterBegin','<input type="checkbox" ' + (this.AIDefaultChecked?'checked':'') + ' value="1"/>',true);
	

	var table = leftCenterCt.insertHtml('afterBegin','<table style="border-collapse: collapse; z-index: 10; position: relative; left: 0px; top: 20px; width: 300px; height: 260px; opacity: 0.5;" cellspacing="0" cellpadding="0" border="0" unselectable="on">				<tr>					<td style="background: #cccccc;" colspan="3"><div class="IC_top_mask" style="width:100%;height:50px;"></div></td>				</tr>				<tr>					<td style="background: #cccccc;"><div class="IC_left_mask" style="height:100%;width:50px;"></div></td>					<td style="border: 1px solid #ffffff;">						<div  class="IC_rect_cutter" style="border: 1px solid red;width:200px;height:150px;  border:1px solid #000000; position:relative;cursor:move;" > 							<div  class="IC_drag_handler" style="cursor:nw-resize; bottom:-5px; right:-5px;position:absolute;background:#C00;width:10px;height:10px;z-index:5;font-size:0;"> </div> 						</div> 					</td>					<td style="background: #cccccc; width: 100%;"></td>				</tr>				<tr>					<td style="background: #cccccc; height: 100%;" colspan="3"></td>				</tr>			</table>',true);
	
	var leftImageCt = leftCenterCt.createChild({
		style:'position: absolute; top: 20px; left: 0px;width: 300px; height: 260px;'
	});
	var leftImage = leftImageCt.createChild({
		tag:'img',
		style:'width:300px;height:260px;'
	});
	
	//右侧UI
	var btnSuggest_1 = rightCt.createChild({
		tag:'fieldset',
		style:'font-size: 14px; padding-bottom: 4px; padding-top: 4px; border-width: 0px;height:23px;'
	}).createChild({
		tag:'button',
		style:'font-size:12px;display:none;',
		html:'面部优先'
	});
	var btnSuggest_2 = btnSuggest_1.insertSibling({
		tag:'button',
		style:'font-size:12px;display:none;',
		html:'热点优先'
	},'after');
	this.suggestInfoEl = btnSuggest_2.insertSibling({
		tag:'label',
		style:'font-size:12px;'
	},'after');
	this.btnSuggest_1 = btnSuggest_1;
	this.btnSuggest_2=btnSuggest_2;
	
	this.preveiwCanvas = rightCt.createChild({
		tag:'div',
		style:'border: 2px dotted #888888; width: '+ this.canvasMaxWidth +'px; height: '+ this.canvasMaxHeight +'px; overflow: hidden; position: relative; top: 0px; left: 0px; margin: 4px;'
	}).createChild({
		tag:'fieldset',
		style:'padding-bottom: 5px; border-width: 0px; text-align: center; padding-top: 5px;',
		html:'实时预览'
	}).insertSibling({
		style:'width: 280px;height: 240px; margin: 5px 10px;'
	},'after').createChild({
		style:'position: relative; top: 0px; left: 0px;overflow:hidden'
	});
	
	this.preivewImage = this.preveiwCanvas.createChild({
		tag:'img',
		style:'position:relative;left:0px;top:0px;'
	}); 
	var targetCt=this.preveiwCanvas.parent().insertSibling({
		tag:'fieldset',
		cls:'cmpp-ic-targetSizeControler',
		html:'<span>目标尺寸:</span>'
	},'after').first();
	this.targetWidthEl = targetCt.createChild({
		tag:'input',
		type:"text",
		style:'text-align: right;',
		value:this.targetWidth
	});
	targetCt.insertHtml('beforeEnd','×');
	this.targetHeightEl = targetCt.createChild({
		tag:'input',
		type:"text",
		value:this.targetHeight
	});
	this.targetButtonEl =targetCt.createChild({
		tag:'button',
		html:'更改'
	});
	this.targetButtonEl.on('click',function(obj){
		try{
			var w = parseInt(this.targetWidthEl.dom.value);
			var h = parseInt(this.targetHeightEl.dom.value);
			this.contex.contex.setTargetSize(w,h);
		}catch(ex){}
	},this);
	
	this.leftImageCt = leftImageCt;
	this.leftImage = leftImage;
	this.topMaskEl = table.child('.IC_top_mask');
	this.leftMaskEl = table.child('.IC_left_mask');
	this.bottomMaskEl = table.child('.IC_bottom_mask');
	this.rightMaskEl = table.child('.IC_right_mask');
	
	this.cutterRectEl = table.child('.IC_rect_cutter');
	this.dragHandlerEl = table.child('.IC_drag_handler');
	this.chkSuggest = chkSuggest;
	this.tableCanvas = table;
	//加载上传控件
	this.fileInputEl = ct.insertHtml('beforeBegin','<input type="file" style="position: absolute;opacity: 0;cursor: pointer;top: -1000px;left:-1000px;z-index: 100;" size="30" accept="image/*">',true);
	this.fileInputEl.on("change",this.fileUploadHandler,this);
	uploadButton.on("click",function(){
		this.fileInputEl.dom.click();
	},this);
	//绑定切割区域拖动事件
	this.cutterRectEl.on("mousedown",function(e,obj){
		if(!this.image) return;
		this.cutterRectEl.dragXY=e.xy;	
		Ext.getDoc().on("mousemove",this.cutterRectMoveHandler,this);
		Ext.getDoc().on("mouseup",this.stopMoveHandler,this);
	},this);
	this.cutterRectEl.on("blur",function(){
		this.stopMoveHandler();
	},this);

	//绑定拖拽改变尺寸的把柄事件
	this.dragHandlerEl.on("mousedown",function(e,obj){
		e.stopPropagation();
		if(!this.image) return;
		this.dragHandlerEl.dragXY=e.xy;	
		Ext.getDoc().on("mousemove",this.dragHandlerElMoveHandler,this);
		Ext.getDoc().on("mouseup",this.stopDragMoveHandler,this);
		this.cutterRectEl.setStyle('cursor','nw-resize');
		this.tableCanvas.setStyle('cursor','nw-resize');
	},this);
	
	btnSuggest_1.on("click",function(){
		var s = this.suggest1;
		s && this.applyAISugget(s[0],s[1],s[2]-s[0],s[3]-s[1]);
	},this);
	btnSuggest_2.on("click",function(obj){
		var s = this.suggest2;
		s && this.applyAISugget(s[0],s[1],s[2]-s[0],s[3]-s[1]);
	},this);
	
	if(typeof this.plugin ==="function"){//扩展
		this.plugin();	
	}
};
ccImageCutter.prototype.stopMoveHandler = function(){ 
	Ext.getDoc().un("mousemove", this.cutterRectMoveHandler); 
	Ext.getDoc().un("mouseup", this.stopMoveHandler); 
	window.getSelection ? window.getSelection().removeAllRanges() : document.selection.empty();     
}, 
ccImageCutter.prototype.stopDragMoveHandler = function(){ 
	Ext.getDoc().un("mousemove", this.dragHandlerElMoveHandler); 
	Ext.getDoc().un("mouseup", this.stopDragMoveHandler); 
	window.getSelection ? window.getSelection().removeAllRanges() : document.selection.empty();   
	this.cutterRectEl.setStyle('cursor','move');
	this.tableCanvas.setStyle('cursor','default');
		
}, 
ccImageCutter.prototype.cutterRectMoveHandler = function(e,obj){
	var startXY = this.cutterRectEl.dragXY;
	//e.xy[0]-startXY[0]
	var offset=[e.xy[0]-startXY[0],e.xy[1]-startXY[1]];
	var oldWidth = this.sizeCache.leftMaskEl.width,oldHeight=this.sizeCache.topMaskEl.height;
	if(oldWidth+offset[0]>this.leftImageCt.getWidth()-this.cutterRectEl.getWidth()){
		this.leftMaskEl.setWidth(Math.round(this.leftImageCt.getWidth()-this.cutterRectEl.getWidth()));
	}else{
		this.leftMaskEl.setWidth(Math.round(oldWidth+offset[0]));
	}	
	if(oldHeight+offset[1]>this.leftImageCt.getHeight()-this.cutterRectEl.getHeight()){
		this.topMaskEl.setHeight(Math.round(this.leftImageCt.getHeight()-this.cutterRectEl.getHeight()));
	}else{
		this.topMaskEl.setHeight(Math.round(oldHeight+offset[1]));
	}
	this.sizeCache.topMaskEl={
		height:this.topMaskEl.getHeight()
	};
	this.sizeCache.leftMaskEl={
		width:this.leftMaskEl.getWidth()
	};
	var newWidth = this.leftMaskEl.getWidth(),newHeight=this.topMaskEl.getHeight();
	this.cutterRectEl.dragXY = e.xy;
	//调整预览画面的位置
	if(this.sizeCache.topMaskEl.height-oldHeight!==0 || this.sizeCache.leftMaskEl.width-oldWidth!==0)
		this.repositionPreviewImage();
};
ccImageCutter.prototype.dragHandlerElMoveHandler = function(e,obj){
	var curPos = this.cutterRectEl.getXY();
	var newW = Math.round(e.xy[0]-curPos[0]);
	var newH = Math.round(newW*this.targetHeight/this.targetWidth);
	//区域合法性校验
	var maxSize = this.leftImageCt.getSize();
	var lw = this.leftMaskEl.getWidth();
	var th = this.topMaskEl.getHeight();
	newW=newW<15?15:newW;
	newH=newH<15?15:newH;
	newW = newW+lw>maxSize.width?maxSize.width-lw:newW;
	newH = newH+th>maxSize.height?maxSize.height-th:newH;
	
	this.cutterRectEl.setSize(newW,newH);	
	this.sizeCache.cutterRectEl={
		width:newW,
		height:newH
	}
	//同步校正预览画面UI
	this.adjustPreview();
};
ccImageCutter.prototype.setTargetSize = function(targetWidth,targetHeight){
	this.targetWidth = targetWidth;
	this.targetHeight = targetHeight;
	//this.targetSizeEl.update('目标尺寸:'+ this.targetWidth +'×' + this.targetHeight);
	this.targetWidthEl.dom.value=this.targetWidth;
	this.targetHeightEl.dom.value=this.targetHeight;
	
	this.resizeUI(this.originalWidth,this.originalHeight);
};
/*
*触发上传
*/
ccImageCutter.prototype.fileUploadHandler = function(e,obj,opts){
	//阻止默认事件
	e.stopPropagation();
	e.preventDefault();
	
	//搜集文件
	var files = e.target.files||[];
	if(files.length==0){
		return false;
	}
	var file = files[0];
	var reader = new FileReader();
	reader.onload = (function(that) {
		var _t = that;
		return function(e) {                                
			_t.leftImage.dom.src = e.target.result;  //预览图片 
			_t.preivewImage.dom.src = e.target.result; 
			var image=new Image();
			image.onload=(function(that){
				var t = that;
				return function(){
					_t.originalWidth=this.width;
					_t.originalHeight=this.height;
					_t.resizeUI(this.width,this.height);	
				}
			})(_t);
			image.src = e.target.result;
			_t.image = image;
	
			//请求智能裁图建议
			_t.btnSuggest_1.setDisplayed(false);
			_t.btnSuggest_2.setDisplayed(false);
			_t.suggestInfoEl.update('');
			if(_t.chkSuggest.dom.checked){
				_t.getAISuggest(e.target.result.substring(22),"base64");
			}
		};
	})(this);
	reader.readAsDataURL(file);
	this.fileExtent = file.name.split('.')[1];
};
ccImageCutter.prototype.getAISuggest=function(fileData,type){
	var params = {
		url:fileData,
		width:this.targetWidth,
		height:this.targetHeight
	}
	if(type=="base64"){
		params.ext = this.fileExtent;
	}
	try{
		this.suggestInfoEl.update('<font color="green">正在获取裁图建议...</font>');
		Ext.Ajax.request({  
			url:this.AIUrl,  
			params:params,
			method:"POST",  
			options:this,
			success:function(response,opts){
				try{
					var ret = Ext.util.JSON.decode(response.responseText);
					var t = opts.options;
					if(ret.facedetect)
						t.suggest1 = ret.facedetect.split(',');
					else
						t.suggest1 = null;
					if(ret.saliency)
						t.suggest2 = ret.saliency.split(',');
					else
						t.suggest2 = null;	
						
				}catch(ex){
					ret = {success:false,msg:"接口错误"};
				}			
				if(!ret.success){
					opts.options.suggestInfoEl.update('<font color="red">获取裁图建议出错!' + ret.msg + '</font>');
				}else{
					opts.options.suggestInfoEl.update("");
				}
				var s = t.suggest1;
				for(var i=0;i<s.length;i++){
					s[i]=parseInt(s[i]);
				}
				var suggest1Enable = false;
				if(s && (s[2]!==0||s[3]!==0)){
					t.applyAISugget(s[0],s[1],s[2]-s[0],s[3]-s[1]);
					suggest1Enable=true;
					t.btnSuggest_1.setDisplayed(true);
				}
				s = t.suggest2;
				for(var i=0;i<s.length;i++){
					s[i]=parseInt(s[i]);
				}
				if(s && (s[2]!==0||s[3]!==0)){
					if(!suggest1Enable) t.applyAISugget(s[0],s[1],s[2]-s[0],s[3]-s[1]);
					t.btnSuggest_2.setDisplayed(true);
				}
				
			},
			failure:function(response,opts){
				//Ext.Msg.alert('错误提示',response.responseText)	;
				opts.options.suggestInfoEl.update('<font color="red">获取裁图建议出错</font>');
			}		
		});	
	}catch(ex){
		this.suggestInfoEl.update('<font color="red">获取裁图建议出错!'+ ex.message+'</font>');
	}
}
/*
*根据图片尺寸重新调整UI尺寸和位置
*/
ccImageCutter.prototype.resizeUI=function(imgWidth,imgHeight){
	var newW,newH,_left,_top;
	var maxw=this.canvasMaxWidth,maxh = this.canvasMaxHeight;
	if(imgWidth/imgHeight>maxw/maxh){
		newW = imgWidth>maxw?maxw:imgWidth;
		newH = imgHeight*newW/imgWidth;
		_left=imgWidth>maxw?0:(maxw-newW)/2;
		_top = (maxh-newH)/2;
	}else{
		newH = imgHeight>maxh?maxh:imgHeight;
		newW = imgWidth*newH/imgHeight;
		_top=imgHeight>maxh?0: (maxh-newH)/2;
		_left = (maxw-newW)/2;
	}
	newW = Math.round(newW);
	newH = Math.round(newH);
	var style = {
		width:newW + 'px',
		height:newH + 'px',
		left:Math.round(_left)+"px",
		top:Math.round(_top)+"px"
	};
	this.tableCanvas.applyStyles(style);
	this.leftImageCt.applyStyles(style);
	this.leftImage.setWidth(newW);
	this.leftImage.setHeight(newH);
	this.sizeCache.leftImage={
		width:newW,
		height:newH
	};
	//计算可见区域矩形的尺寸和位置
	this.resizeCutterRectUI(newW,newH);
	
	//调整预览画面UI
	this.resizePreviewCanvas();

};
//计算可见区域矩形的尺寸和位置
ccImageCutter.prototype.resizeCutterRectUI=function(canvasWidth,canvasHeight){
	var tw = this.targetWidth,th = this.targetHeight;
	var op = this.contex.contex.cutRectMultiple || 0.75;
	var rectW,rectH;
	
	if(canvasWidth/canvasHeight<tw/th){
		rectW = canvasWidth*op;
		rectH = rectW*th/tw;
	}else{
		rectH = canvasHeight*op;
		rectW = rectH*tw/th;
	}
	rectW = Math.round(rectW);
	rectH = Math.round(rectH);
	this.cutterRectEl.setSize(rectW,rectH);
	var topMaskHeight = Math.round((canvasHeight-rectH)/2);
	var leftMaskWidth = Math.round((canvasWidth-rectW)/2);
	this.topMaskEl.setHeight(topMaskHeight);
	this.leftMaskEl.setWidth(leftMaskWidth);
	
	//缓存尺寸
	this.sizeCache.cutterRectEl={width:rectW,height:rectH};
	this.sizeCache.topMaskEl={height:topMaskHeight};
	this.sizeCache.leftMaskEl={width:leftMaskWidth};
};
//应用智能裁图建议
ccImageCutter.prototype.applyAISugget=function(_left,_top,width,height){
	//转换原图坐标为画布坐标
	var newLeft,newTop,newWidth,newHeight;
	var leftImage = this.sizeCache.leftImage;
	newLeft = _left*leftImage.width/this.originalWidth;
	newTop = _top*leftImage.height/this.originalHeight;
	newWidth = width*leftImage.width/this.originalWidth;
	newHeight = newWidth*this.targetHeight/this.targetWidth;
	newLeft=Math.round(newLeft);
	newTop=Math.round(newTop);
	newWidth=Math.round(newWidth);
	newHeight=Math.round(newHeight);
	
	//修正尺寸过大情况
	if(newWidth>leftImage.width-newLeft){
		newWidth=leftImage.width-newLeft;
		newHeight = newWidth*this.targetHeight/this.targetWidth;
	}
	if(newHeight>leftImage.height-newTop){
		newHeight = leftImage.height-newTop;
		newWidth = newHeight*this.targetWidth/this.targetHeight;
	}
	this.cutterRectEl.setSize(newWidth,newHeight);
	this.leftMaskEl.setWidth(newLeft);
	this.topMaskEl.setHeight(newTop);
	//缓存尺寸
	this.sizeCache.cutterRectEl={
		width:newWidth,
		height:newHeight
	}
	this.sizeCache.topMaskEl={height:newTop}
	this.sizeCache.leftMaskEl={width:newLeft}
	this.adjustPreview();
};
//调整预览画面UI
ccImageCutter.prototype.resizePreviewCanvas=function(){
	var size = this.preveiwCanvas.parent().getSize();
	var tw = this.targetWidth,th = this.targetHeight;
	var rectW,rectH,_left,_top;
	
	if(size.width/size.height<tw/th){
		rectW = size.width;
		rectH = rectW*th/tw;
		_left=0;
		_top =(size.height-rectH)/2;
	}else{
		rectH = size.height;
		rectW = rectH*tw/th;
		_left=(size.width-rectW)/2;
		_top = 0;
	}
	this.preveiwCanvas.setSize(rectW,rectH);
	this.preveiwCanvas.applyStyles({
		left:_left + "px",
		top:_top + "px"
	});
	
	//缓存尺寸
	this.sizeCache.preveiwCanvas={width:rectW,height:rectH};
	//同步校正预览画面UI
	this.adjustPreview();
};
//同步校正预览画面UI
ccImageCutter.prototype.adjustPreview=function(){
	//设置预览图片缩放后尺寸
	this.resizePreviewImage();
	//设置预览图片的位置
	this.repositionPreviewImage();
};
//设置预览图片缩放后尺寸
ccImageCutter.prototype.resizePreviewImage=function(){
	var leftImage = this.sizeCache.leftImage;
	var preveiwCanvas = this.sizeCache.preveiwCanvas;
	var cutterRectEl=this.sizeCache.cutterRectEl;
	var newW = leftImage.width*preveiwCanvas.width/cutterRectEl.width;
	var newH = leftImage.height*preveiwCanvas.height/cutterRectEl.height;
	this.preivewImage.setSize(newW,newH);

};
//设置预览图片的位置
ccImageCutter.prototype.repositionPreviewImage=function(){
	var preveiwCanvas =this.sizeCache.preveiwCanvas;
	var cutterRectEl=this.sizeCache.cutterRectEl;
	var topMaskEl = this.sizeCache.topMaskEl,leftMaskEl =this.sizeCache.leftMaskEl ;
	var offsetX = leftMaskEl.width * preveiwCanvas.width/cutterRectEl.width;
	var offsetY = topMaskEl.height * preveiwCanvas.height/cutterRectEl.height;
	this.preivewImage.setLeft(-Math.round(offsetX));
	this.preivewImage.setTop(-Math.round(offsetY));
};
//获取可视区域
ccImageCutter.prototype.getViewRegion=function(){
	var oriW= this.originalWidth,oriH=this.originalHeight;
	var psize = this.preivewImage.getSize();
	var x = -parseInt(this.preivewImage.getStyle("left"))*oriW/psize.width;
	var y = -parseInt(this.preivewImage.getStyle("top"))*oriH/psize.height;
	var preveiwCanvas = this.sizeCache.preveiwCanvas;
	var width = preveiwCanvas.width*oriW/psize.width;
	var height = preveiwCanvas.height*oriH/psize.height;
	x = Math.round(x);y = Math.round(y);width = Math.round(width);height = Math.round(height);
	return {x:x,y:y,width:width,height:height};
}

ccImageCutter.prototype.getHandler=function(handler,_this){
	var t = _this;
	var h = handler;

	return function(){
		t[h].apply(t,arguments);
	}
};	


ccImageCutter.prototype.setValue = function(imageUrl){
	this.originalImageUrl = imageUrl;
	//todo.. 
	
};
ccImageCutter.prototype.getValue = function(){
	var tw = this.targetWidth,th=this.targetHeight;
	var canvas = this.canvas;
	if(!canvas){
		canvas = document.createElement('canvas');
		this.canvas = canvas;
	}
	var ctx = canvas.getContext('2d');
	canvas.width = tw;
	canvas.height = th;
	//计算可视区域范围
	var rect = this.getViewRegion();
	ctx.drawImage(this.image, rect.x, rect.y, rect.width, rect.height, 0, 0, tw, th);
	//将图像输出为base64压缩的字符串  默认为image/png  
	var data = canvas.toDataURL();  
	//删除字符串前的提示信息 ”data:image/png;base64,”  
	var b64 = data.substring( 22 );  
	return b64;
};

var _clipDataForCopy;
var CLIPBOARDSWFID="CLIPBOARDSWFID-XUAS";
/*
*clipBorad.swf内部调用的方法
*/
function getClipData(){
	return window._clipDataForCopy;
}
Ext.reg('imagecutter', Ext.ux.ImageCutter);
