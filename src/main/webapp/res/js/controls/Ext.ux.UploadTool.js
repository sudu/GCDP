/*
*通用上传工具控件
*author:cici
*date:2013/01/21
*依赖js：controls/Ext.ux.ListPanel.js
*依赖css：
*/
	
var _clipDataForCopy;
var CLIPBOARDSWFID="CLIPBOARDSWFID-XUAS";
/*
*clipBorad.swf内部调用的方法
*/
function getClipData(){
	return window._clipDataForCopy;
}

Ext.ux.UploadTool = function (config) {
	if(!Ext.nore(config.regex))
		config.regex=Ext.decode(config.regex);	
	Ext.ux.UploadTool.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.UploadTool, Ext.form.TextField, {
	/*UI*/
	allowBlank:false,
	emptyText:'',
	vtype:'url',
	button_text:'上传文件',
	beforeNoteText1:'',//控件前面的说明，这里可以是操作说明或输入实例
	beforeNoteText2:'例如：http://y1.ifengimg.com/a/2010/0601/ 以/结束',
	beforeNoteText3:'例如：http://y1.ifengimg.com/a/2010/0601/a.jpg 文件名要有扩展名',
	width:300,
	uploadMode:1,//上传模式：1-默认上传 2-自定义目录上传 3-自定义URL上传
	postParams:null,
	listPanel:null,
	store:null,
	reuploadRow:null,
	rootPath:'../',
	/*基础配置*/
		nodeId:null,
		sendfilePrefix:'a',
		uploadUrl2:null,//上传接口
		clipBoardSwf_url : null,//复制flash
	/*上传配置*/
		syncflag:1,//同步分发或异步分发 1：同步 0：异步
		fileMaxSize:5*1024*1024,
		file_types:"*.*",
		file_types_description:"所有文件",
		destinationDomains:['y0.ifengimg.com','y1.ifengimg.com','y2.ifengimg.com','y3.ifengimg.com'],//分发的目标域名，支持多个域名随机
		clipSwfDiv:null,
		
    initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		if(!this.uploadUrl2) this.uploadUrl2 = this.rootPath + 'runtime/fileMgr!customDirUploadFile.jhtml';
		if(!this.clipBoardSwf_url) this.clipBoardSwf_url = this.rootPath + "res/swf/clipBorad-Xuas.swf";//Author:许爱思
		this.emptyText = this.uploadMode==2?'例如：http://y1.ifengimg.com/a/2010/0601/ 以/结束':'例如：http://y1.ifengimg.com/a/2010/0601/a.jpg 文件名要有扩展名'
	    Ext.ux.UploadTool.superclass.initComponent.call(this);
		this.postParams={};
		if(typeof(this.destinationDomains)=='string'){
			try{
				this.destinationDomains = Ext.decode(this.destinationDomains);
			}catch(ex){
				
			}
		}

	},
	onRender: function (ct, position) {

		Ext.ux.UploadTool.superclass.onRender.call(this, ct, position);

		var elParent = this.el.parent();
		//this.el.setStyle({"cssFloat":"left"});
		if(this.uploadMode!=1){
			eval('var note = this.beforeNoteText' + this.uploadMode);
			this.beforNoteEl = this.el.insertSibling({
				tag:'span',
				html:note
			},'before');
			this.beforNoteEl.insertSibling({
				cls:'x-form-clear-left'
			},'after');
		}
		//创建输入框前的label
		this.el.insertSibling({
			tag:'label',
			html:this.uploadMode==1?'默认目录:':(this.uploadMode==2?'目录URL:':(this.uploadMode==3?'目标URL:':''))
		},'before');
		
		
		this.uploadMode==1 && this.disable() && this.setDefaultDir();//设置默认目录
		
		/*
		//换行
		elParent.createChild({
			cls:'x-form-clear-left'
		});
		
		this.uploadContainerEl = elParent.createChild({
			tag:'div',
			cls:'UploadTool-ct'
		});
		*/
		this.uploadButtonEl = elParent.createChild({
			tag:'button',
			cls:'UploadTool-ct-button',
			html:this.button_text
		});
		this.fileInputEl = elParent.insertHtml('beforeBegin','<input type="file" style="position: absolute;opacity: 0;cursor: pointer;top: -1000px;left:-1000px;z-index: 100;" size="30" name="fileselect[]" multiple>',true);
		
		this.fileInputEl.on("change",this.fileUploadHandler,this);
		this.uploadButtonEl.on("click",function(){
			if(!this.checkValid()) return false;
			this.reuploadRow = -1;
			this.fileInputEl.dom.click();
		},this);
		
		//创建文件列表容器
		this.listContainerEl = elParent.createChild({
			tag:'div',
			cls:'UploadTool-list-ct'
		});
		
		//初始化列表页
		this.initListPanel(ct);
		//加载clipBoard.swf
		this.loadClipBoardSwf(16,16);
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
				html:'<embed src="'+ this.clipBoardSwf_url +'" name="clipSwf" type="application/x-shockwave-flash" width="'+width+'" height="'+height+'" wmode="transparent" allowScriptAccess="always"></embed>'
			});
		}
		return this.clipSwfDiv ;
	},
	initListPanel:function(ct){
		//初始化列表页
		var listPanel = new Ext.ux.ListPanel({
			renderTo:this.listContainerEl,
			style:'padding:1px 5px 0px 0px;',
			layout:'fit',
			frame:false,
			header :false,
			border:false,
			//height:500,
			gridConfig:{
				autoExpandColumn:1,//第二列自由宽度
				//pagesize:15,//默认为根据高度自动计算
				hasPageBar:false,
				rowHeight:26,//行高
				storeConfig:{
					root : "",
					totalProperty : "",
					fields: ['url','name','kb','type','result']
				},
				columnConfig:{
					hasRowNumber:false,//是否显示列序号
					hasSelectionModel:false,//是否需要复选框
					colunms:[{//列表项
						header: "文件名",//列表栏标头名称
						sortable: true,//是否支持点击排序
						dataIndex: "name",//绑定的字段名
						width:150,//列宽
						align:"left",//对齐方式 left center right
						tpl:'<tpl if="url==\'\'">{name}</tpl><tpl if="url!=\'\'"><a href="{url}" title="{name}" target="_blank">{name}</a></tpl>'//模板，参照Ext.XTemplate的语法
					},{
					   header: "url",
					   dataIndex: 'url',
					   align:"left",
					   tpl:'<tpl if="result==-2">文件url已存在<div class="UploadTool-list-ct-btn-reupload" title="覆盖上传">覆盖上传</div></tpl><tpl if="result==-1">超时</tpl><tpl if="result==0">上传失败</tpl><tpl if="result==1"><progress value="{percentComplete}" max="100"></progress> </tpl><tpl if="result==2"><input onmouseover="javascript:this.select();" class="UploadTool-list-ct-url-input" type="text" value="{url}"/><div class="UploadTool-list-ct-btn-copy" title="复制地址"></div></tpl>'
					},{
					   header: "类型",
					   dataIndex: 'type',
					   align:"left",
					   tpl:'{type}',
					},{
					   header: "大小",
					   dataIndex: 'kb',
					   width:80,//列宽
					   sortable:false,
					   align:"left",
					   tpl:'{kb}'
					},{
						header: "操作",
						width: 60,
						align:'center',
						tpl:'<div class="UploadTool-list-ct-btn-reupload" title="重传">重传</div>'
					}]
				},
				listeners: {
					scope:this,
					rowmousedown: function (panel, row,  e) {
						switch (e.target.className) {
							case 'UploadTool-list-ct-btn-reupload': //重传
								this.reuploadRow = row;
								this.fileInputEl.dom.click();
								break;
							case 'UploadTool-list-ct-btn-copy': //复制链接
								this.doCopy(e,this.store.getAt(row).data.url);
								break;	
								
						}
					}
				}
			}
		});
		this.listPanel = listPanel;
		this.store = this.listPanel.grid.store;	
		this.store.on('update',function(o){
			o.commitChanges();//去掉修改后grid 左上角的红三角
		});
		this.listPanel.hide();
		
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
				html:'<embed src="'+ this.clipBoardSwf_url +'" name="clipSwf" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash"  width="'+width+'" height="'+height+'" wmode="transparent" allowScriptAccess="always"></embed>'
			});
		}
		this.clipSwfDiv.on('mouseup',function(){
			this.clipSwfDiv.position("absolute",1,-100,-100);			
		},this);
		return this.clipSwfDiv ;
	},
	checkValid:function(){
		if(!this.isValid()){
			alert('输入不合法');
			return false;
		}
		if(this.uploadMode==2){
			var v = this.getValue();
			v = v.trim();
			if(v.substring(v.length-1)!=='/'){
				alert("目录URL需要以/结束");
				return false;
			}
		}
		return true;
	},
	/*
	*触发上传
	*/
	fileUploadHandler:function(e,obj,opts){
		var reuploadRow = this.reuploadRow;
		//阻止默认事件
		e.stopPropagation();
		e.preventDefault();
		
		this.listPanel.show();
		//搜集文件
		var files = e.target.files||[];
		if(reuploadRow!==-1 && files.length>1){
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
			var params = this.setParams(fileJson.file,reuploadRow);
			var targetUrl = "http://"+params.domain + params.folderPath + params.fileName;
			fileJson.targetUrl = targetUrl;
			if(!this.uploadStart(null,null,fileJson)) continue;
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
					xhr.open("POST", t.uploadUrl2, true);
					//使用FormData处理需要上传的数据
					var oParam=params;
					if(t.reuploadRow!==-1) oParam.reupload = 1;
					var fd = new FormData();
					for( var j in oParam) {
						fd.append(j,oParam[j]);
					}
					fd.append("filedata", file);
					xhr.send(fd);
				}	
			})({context:this,file:fileJson.file,params:params,targetUrl:fileJson.targetUrl});		
		}
		
	},
	uploadStart:function(e,xhr,fileJson){
		var f = fileJson.file;
		var row = this.store.indexOfId(fileJson.targetUrl);
		if(row!=-1){
			var r = this.store.getAt(row);
			r.set('kb',Ext.util.Format.fileSize(f.size));
			r.set('type',f.type);
			r.set('result',1);	
	
			return true;
		}
		var r = new Ext.data.Record({
			kb:Ext.util.Format.fileSize(f.size),
			type:f.type,
			name:f.name,
			url:'',
			result:1
		},fileJson.targetUrl);
		this.store.insert(0,[r]);
		return true;
	},
	updateProgress:function(e,xhr,fileJson){
		if (e.lengthComputable) {
			var percentComplete = e.loaded / e.total *100;
			var f = fileJson.file;
			var row = this.store.indexOfId(fileJson.targetUrl);
			row!=-1 && this.store.getAt(row).set('percentComplete',percentComplete);
		}
	},
	uploadTimeout:function(e,xhr,fileJson){
		var f = fileJson.file;
		var row = this.store.indexOfId(f.name);
		row!=-1 && this.store.getAt(row).set('result',-1);

	},
	uploadComplete:function(e,xhr,fileJson){
		if (xhr.readyState == 4) {
			var compressedFilesize;
			var url=fileJson.targetUrl;
			var result=0;
			if (xhr.status == 200) {
				//成功 xhr.responseText
				var ret = Ext.decode(xhr.responseText);
				if(ret.success){
					var arr = ret.message.split('|');
					url = arr[0];
					arr.length>1 && ( compressedFilesize = arr[1] );
					result = 2;
				}else{
					if(ret.fileExists){
						result=-2;//文件已存在
					}
				}
			} else {
				result = 0;//失败
			}
			var f = fileJson.file;
			var row = this.store.indexOfId(fileJson.targetUrl);
			if(row!=-1){
				var r = this.store.getAt(row);
				compressedFilesize && r.set('kb',Ext.util.Format.fileSize(compressedFilesize));
				url && r.set('url',url);
				r.set('result',result);
			}
		}
	},
	doCopy:function(event,value){
		var obj = Ext.fly(event.target);
		window._clipDataForCopy = value;
		this.clipSwfDiv.position("absolute",999999999,obj.getLeft(),obj.getTop());
		this.clipSwfDiv.setSize(obj.getWidth(),obj.getHeight());
		this.clipSwfDiv.first().setSize(obj.getWidth(),obj.getHeight());
		var maskEl = obj.parent().mask("成功复制");
		maskEl.fadeOut({
			endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
			easing: 'easeOut',
			duration: 0.5,
			remove: true,
			useDisplay: true,
			scope:obj.parent(),
			callback:function(){
				this.unmask();
			}
		});
	},
	setParams:function(file,reuploadRow){
		var params = {};
		var parseUrl = function(url){
			url = url.replace('http://','');
			var pos = url.indexOf('/'),pos1 = url.lastIndexOf('.'),pos2 = url.lastIndexOf('/');
			var domain = url.substring(0,pos);
			if(pos1==-1) 
				var path = url.substring(pos);
			else {
				var path = url.substring(pos,pos2+1);
				var fileName = url.substring(pos2+1);
			}
			return {domain:domain,folderPath:path,fileName:fileName};
		}
		if(reuploadRow!==-1){//重新上传
			var row = reuploadRow;
			if(row!=-1){
				var r = this.store.getAt(row);
				Ext.apply(params,parseUrl(r.get("url")));
			}
		}else{
			var mode = this.uploadMode;
			var inputUrl = this.getValue();
			inputUrl = inputUrl.trim();
			if(mode==1 || mode==2){
				Ext.apply(params,parseUrl(inputUrl));
				params.fileName=file.name;
			}else if(mode==3){
				Ext.apply(params,parseUrl(inputUrl));
			}
		}
		params.syncflag = this.syncflag;
		params.nodeId = this.nodeId;
		return params;
	},
	//设置默认目录
	setDefaultDir:function(){
		var v = this.getValue();
		if(v){
			return;
		}else{
			var domains = this.destinationDomains;
			var domain = domains[parseInt(Math.random()*domains.length)];
			var now = new Date();
			var date = now.getDate(),m = now.getMonth() + 1;
			var path = '/' + this.sendfilePrefix + '/' + now.getFullYear() + '/' + (m>=10?m:('0' + m)) + (date>=10?date:('0' + date))+'/';
			this.setValue('http://' + domain + path);
			this.postParams.domain = domain;
			this.postParams.folderPath = path;
			
		}
	}

});
Ext.reg('uploadtool', Ext.ux.UploadTool);