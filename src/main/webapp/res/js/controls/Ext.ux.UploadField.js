var _clipDataForCopy;
var CLIPBOARDSWFID="CLIPBOARDSWFID-XUAS";
/*
*clipBorad.swf内部调用的方法
*/
function getClipData(){
	return window._clipDataForCopy;
}

(function(){
Ext.ux.UploadField = function (config) {
	if(!Ext.nore(config.regex))
		config.regex=Ext.decode(config.regex);		
	Ext.ux.UploadField.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.UploadField, Ext.form.TextField, {
	/*UI*/
	
	width:300,
	name:'uploadField',
	vtype:'url',
	copyButtonVisible:true,
	viewButtonVisible:true,
	helpButtonVisible:true,
	infoEl:null,
	helpHtml:null,
	/*基础配置*/

		uploadUrl:'../upload!file.jhtml',//自动生成url
		uploadUrl2:'../upload!send.jhtml',//上传接口(自定义url)
		
		btnCopyImg:'../res/img/copy.png',
		btnViewImg:'../res/img/image.png',
		fileMaxSize : 2,//M
		// Button Settings
		button_text:'上传',
		// Flash Settings
		res_url:"../res/",
		clipBoardSwf_url : "../res/swf/clipBorad-Xuas.swf",//Author:许爱思
	/*上传配置*/
		syncflag:1,//同步分发或异步分发 1：同步 0：异步
		file_types:"image/*",//对应file控件的accept属性；eg.accept="image/*" accept="image/gif, image/jpeg" accept="text/html"
		destinationDomains:['y0.ifengimg.com','y1.ifengimg.com','y2.ifengimg.com','y3.ifengimg.com'],//分发的目标域名，支持多个域名随机
		isUpdateLoad:false,//是否覆盖地址上传（更新上传）
		swfUploader:null,
		getHandler:null,
		clipSwfDiv:null,
		
    initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.fileMaxSize = this.fileMaxSize * 1024*1024;
	    Ext.ux.UploadField.superclass.initComponent.call(this);
		if(typeof(this.destinationDomains)=='string'){
			try{
				this.destinationDomains = Ext.decode(this.destinationDomains);
			}catch(ex){
				
			}
		}
	},
	onRender: function (ct, position) {

		Ext.ux.UploadField.superclass.onRender.call(this, ct, position);

		var elParent = this.el.parent();
		var btnCtId = this.id + '_btnCt_swf';	
		this.el.setStyle({"cssFloat":"left"});
		elParent = elParent.createChild({
			tag:'div',
			style:'float:left;'
		});
		this.uploadButtonEl = elParent.createChild({
			tag:'input',
			type:'button',	
			style:'min-width:40px',
			cls:'UploadTool-ct-button',
			value:this.button_text
		});
		this.fileInputEl = elParent.insertHtml('beforeBegin','<input type="file" style="position: absolute;opacity: 0;cursor: pointer;top: -1000px;left:-1000px;z-index: 100;" size="30" name="fileselect[]" accept="'+ this.file_types +'">',true);
		
		this.fileInputEl.on("change",this.fileUploadHandler,this);
		this.uploadButtonEl.on("click",function(){
			if(!this.checkValid()) return false;
			this.fileInputEl.dom.click();
		},this);
		
		//预览按钮
		if(this.viewButtonVisible!=false){
			var previewEl = elParent.createChild({
				tag:'a',
				style:'margin-left:5px;',
				title:'查看文件',
				href:'javascript:void(0);',
				html:'<img src="' + this.btnViewImg + '" width="16" height="16"/>'
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
				title:'复制链接',
				style:'margin-left:5px;',
				href:'javascript:void(0);',
				html:'<img src="' + this.btnCopyImg + '" width="16" height="16"/>'
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
				html:'<img src="' + this.res_url +  'img/help2.gif" width="16" height="16"/>'
			});
			//初始化帮助
			new Ext.ToolTip({
				target:btnHelp,
				miniWidth: 150,
				dismissDelay:60*1000,
				title: '使用说明',
				html :this.helpHtml
			});
		}
	
		//提示信息
		this.infoEl = elParent.createChild({
			tag:'span',
			id:this.id + '_info',
			style:'visibility:visible;color:green;margin:0 5px;'
		});	
			
		var getHandler = function(handler,_this){
			var t = _this;
			var h = handler;

			return function(){
				t[h].apply(t,arguments);
			}
		};	
		this.getHandler = getHandler;
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
		this.infoEl.setStyle('color','green');	
		this.infoEl.setVisible(true);
		this.infoEl.update('已复制');
		infoEl.fadeOut({
			duration: 1,
			callback:function(){
				infoEl.update('');
			}
		});
	},
	checkValid:function(){
		if(!this.isValid()){
			alert('输入不合法');
			return false;
		}
		return true;
	},
	/*
	*触发上传
	*/
	fileUploadHandler:function(e,obj,opts){
		//阻止默认事件
		e.stopPropagation();
		e.preventDefault();
		
		//搜集文件
		var files = e.target.files||[];
		if(files.length>1){
			Ext.CMPP.warn('提示','只能选择一个文件');
			return false;
		}
		var arrFiles=[];
		for(var i = 0, iLen = files.length; i< iLen; i++)
		{
			var file = files[i];
			if (file.size >= this.fileMaxSize) {	
				Ext.CMPP.warn('提示','文件"'+ file.name +'"大小过大('+ (file.size/1024) +'k)，应小于'+ this.fileMaxSize/1024 +'k');
			} else {
				if(file.name.indexOf(' ')!=-1){
					Ext.CMPP.warn('提示','文件"'+ file.name +'"文件名不能包含空格');
				}else{
					arrFiles.push({file:file});	
				}
			}			
		}

		//开始上传
		for (var i = 0, fileJson; fileJson = arrFiles[i]; i++) {
			var params = this.setParams(fileJson.file);
			if(!params) return;
			if(!this.uploadStart(fileJson)) continue;
			(function(fileJson) {
				var t = fileJson.context;
				var file = fileJson.file;
				var params = fileJson.params;
				var xhr = new XMLHttpRequest();
				xhr.timeout=2*60*1000;
				if (xhr.upload) {
					// 上传中
					xhr.ontimeout = t.uploadTimeout.createDelegate(t,[xhr,fileJson],true);
					xhr.upload.onprogress = t.updateProgress.createDelegate(t,[xhr,fileJson],true);
					xhr.onreadystatechange=t.uploadComplete.createDelegate(t,[xhr,fileJson],true);
					// 开始上传
					var postUrl = params.url?t.uploadUrl2:t.uploadUrl;
					xhr.open("POST", postUrl, true);
					//使用FormData处理需要上传的数据
					var oParam=params;
					var fd = new FormData();
					for( var j in oParam) {
						fd.append(j,oParam[j]);
					}
					fd.append("filedata", file);
					xhr.send(fd);
				}	
			})({context:this,file:fileJson.file,params:params});		
		}
		
	},
	setParams:function(file){
		var url = this.getValue();
		var domain =null;
		if(url && this.isUpdateLoad) {
			var pos1 = url.lastIndexOf("/");
			if(pos1!=-1){
				if(url.substr(pos1).indexOf(".")==-1){
					//补全后缀
					url+=file.name;
				}
			}else{
				this.infoEl.setStyle('color','red');	
				this.infoEl.update('路径不合法！url：'+ url);
				return false;
			}
		}else{
			url=null;
			var domains = this.destinationDomains;
			domain = domains[parseInt(Math.random()*domains.length)];
		}
		return {
			'filedataFileName':file.name,
			'domain':domain,
			'url':url,
			'syncflag':this.syncflag
		}
	},
	uploadStart:function(fileJson){
		this.infoEl.setStyle('color','green');	
		this.infoEl.setVisible(true);
		this.infoEl.update('正在上传...');
		
		return true;
	},
	updateProgress:function(e,xhr,fileJson){
		if (e.lengthComputable) {
			var percentComplete = e.loaded / e.total *100;
			console.info(percentComplete);//cds
		}
	},
	uploadTimeout:function(e,xhr,fileJson){
		var f = fileJson.file;
		this.infoEl.setStyle('color','red');	
		this.infoEl.update('超时');

	},
	uploadComplete:function(e,xhr,fileJson){
		if (xhr.readyState == 4) {
			var result=0;
			if (xhr.status == 200) {
				//成功 xhr.responseText
				var ret = Ext.decode(xhr.responseText);
				if(ret.success){
					var fileUrl = ret.message;
					this.setValue(fileUrl);
					this.infoEl.setStyle('color','green');	
					this.infoEl.update('完成上传');
					this.infoEl.fadeOut();
				}else{
					this.infoEl.setStyle('color','red');	
					this.infoEl.update('上传失败!' + ret.message);
				}
			}else{
				this.infoEl.setStyle('color','red');	
				this.infoEl.update('上传过程出错!');	
			}
		}
	}	

});
Ext.reg('uploadfield', Ext.ux.UploadField);
})();