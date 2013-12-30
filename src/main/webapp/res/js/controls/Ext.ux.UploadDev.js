Ext.namespace("Ext.ux");

/**
 * 上传返回json数据结构
 * {
 *	"success":true, //true代表上传成功，false代表上传失败
 *	"message":"http://v.ifeng.com/test/zhanglin/2012/0331/2_a04635f21b69.jpg" //上传成功返回文件路径，失败返回失败原因
 * }
 */

/**
 * 上传接口：系统动态生成路径
 * 
 * 请求地址：../upload!file.jhtml
 * 
 * 参数：
 * domain //分配的资源服务器地址
 * filedataFileName //文件名
 * rename //是否允许自定义文件名
 * filedata //上传文件的主体
 */

/**
 * 上传接口：用户自定义路径
 * 
 * 请求地址：../fileUpload!send.jhtml
 * 
 * 参数：
 * url //自定义的上传到的地址，包括文件名，例如(http://v.ifeng.com/test/zhanglin/2012/0331/test.jpg)
 * filedata //上传文件的主体
 */

/**
 * 上传组件（开发者使用）
 * 主要功能：
 * 1.通过选择文件，选择文件夹，或者拖动文件选择文件
 * 2.可以删除已经选择的文件
 * 3.可以设置上传的线程数
 * 4.可以修改上传文件的路径
 * 5.可以修改上传文件的名称
 * 6.上传失败的文件可以重新转成准备状态重新进行上传
 * 7.上传成功的文件可以方便的复制路径
 * @class Ext.ux.UploadDevPanel
 * @extends Ext.Panel
 */

Ext.ux.UploadDevPanel = Ext.extend(Ext.Panel, {
	
	//设置panel的样式名称，对内部样式进行限制，避免和其他组件样式冲突。
	cls: "x_UploadDevPanel",

	autoHeight: true,
	//是否使用文件本身的路径，默认为true
	useWebkitdirectory : true,
	//是否允许自定义文件名称
	rename: true,
	//是否允许自定义上传路径
	repath: true,

	//备选存储域名地址
	destinationDomains: ['y0.ifengimg.com','y1.ifengimg.com','y2.ifengimg.com','y3.ifengimg.com'],

	//系统自动获取路径的服务器端请求地址
	xhrAutoUrl: '../upload!file.jhtml',
//	xhrAutoUrl: 'result.txt',

	//自定义路径的服务器端请求地址
	xhrCustomUrl: '../fileUpload!send.jhtml',
//	xhrCustomUrl: 'result.txt',

    initComponent: function(){
		
    	//初始化参数
   		this.x_init_option();
   		//初始化dataView
   		this.x_init_dataView();
   		//初始化Html5Upload
   		this.x_init_uploader();

        Ext.ux.UploadDevPanel.superclass.initComponent.call(this);

    },

    onRender: function(ct, position){

        Ext.ux.UploadDevPanel.superclass.onRender.call(this, ct, position);
        
        //为了隐藏x_uploadFile，使其不被误点
        this.x_uploadFile.el.wrap({cls:'x_UploadDev_UploadFileWrap'});

        //如果需要设置路径，则初始化路径输入框
        if(this.repath)
        {
			this.x_uploadPath_box.innerHTML = '<span class="x_upload_path_intro">填写路径：</span><span class="x_UploadDev_UploadPathWrap"></span>';
			this.x_uploadPath = new Ext.form.TextField({ width: 300 , name: "", id: "",emptyText : "请输入完整的路径，如：http://v.ifeng.com/path/"});
			this.x_uploadPath.render(Ext.query(".x_UploadDev_UploadPathWrap",this.x_uploadPath_box)[0]);

        }
        //初始化事件注册
        this.x_init_event();

    },
	
    /**
     * 初始化参数，增加一些需要用到的对象。
     */
	x_init_option: function(){

		//这里由于系统传入的destinationDomains是字符串，需要对其进行decode。
		if(!Ext.isArray(this.destinationDomains))
		{
			this.destinationDomains = Ext.decode(this.destinationDomains);
		}

		//根据repath设置服务器端接收地址
		if(this.repath)
		{
			this.xhrUrl = this.xhrCustomUrl;
		}
		else
		{
			this.xhrUrl = this.xhrAutoUrl;
		}

		if(this.destinationDomains.length<1)
		{
			alert("上传到站点列表不能为空");
		}
		else
		{
			this.x_domain = this.destinationDomains[0];
		}

		//初始文件路径为空
		this.x_base_path = "";
		this.x_uploadFile = new Ext.form.TextField({ width: 0, inputType: "file", name: "", id: ""});
		//创建装载上传状态的容器
		this.x_uploadState = document.createElement("span");
		this.x_uploadState.setAttribute("class","xUploadDev_state");
		//创建装载上传数量的容器
		this.x_uploadCount = document.createElement("span");
		this.x_uploadCount.setAttribute("class","xUploadDev_count");
		//创建装载设置路径的容器
		this.x_uploadPath_box = document.createElement("span");
		this.x_uploadPath_box.setAttribute("class","xUploadDev_pathBox");

		this.tbar = [
			{
				text: "选择文件上传",
				handler: this.x_selectFiles,
				iconCls: "file",
				scope : this
			},
			{
				text: "选择文件夹上传",
				handler: this.x_selectCatalog,
				iconCls: "catalog",
				scope: this
			},
			'->',
			'-',
			this.x_uploadPath_box,
			{
				text: "开始上传",
				handler: this.x_beginUpload,
				iconCls: "upload",
				scope: this
			}
		];
		this.bbar = [this.x_uploadCount,this.x_uploadFile,'->',this.x_uploadState];

	},

	/**
	 * 初始化Html5Upload
	 */
	x_init_uploader: function(){
		//这里使用了将this设置给scope来将作用域传回了回调函数
		this.x_uploader = new Html5Upload({
			threadCount : this.threadCount || 1,
			xhrUrl: this.xhrUrl,
			scope: this,
			xhrParamCallback: this.x_uploadXhrParamHandle,
			setStateCallback: this.x_uploadStateHandle,
			loadstartCallback: this.x_uploadLoadstartHandle,
			progressCallback: this.x_uploadProgressHandle,
			loadCallback: this.x_uploaderLoadHandle,
			errorCallback: this.x_uploadErrorHandle,
			abortCallback: this.x_uploadAbortHandle
		});

	},
	
	/**
	 * 定义设置状态的回调函数
	 * @param {String} sState
	 */
	x_uploadStateHandle: function(sState){

		var sInfo ="";
		//根据状态显示不同的文字
		switch(sState)
		{
			case "Initializing" :
				sInfo = '准备就绪，请选择需要上传的文件';
			break;
			
			case "Ready":
				sInfo = '文件准备就绪，请点击上传按钮上传文件';
			break;
			
			case "Abort":
				sInfo = '您还有取消的没有上传的文件';
			break;
			
			case "Error":
				sInfo = '<span class="x_UploadDev_errorView">您上传的文件中有错误</span>';
			break;
			
			case "Complete":
				sInfo = '<span class="x_UploadDev_successView">文件上传完成</span>';
			break;
			
			case "Uploading":
				sInfo = '文件上传中';
			break;
			
			case "Stop":
				sInfo = '您停止了文件上传';
			break;
			
			default:
				sInfo = sState;
			break;
		}
		
		//this.scope是回调函数的作用域
		this.scope.x_uploadState.innerHTML = sInfo;
		//改变状态的同时，需要刷新数量统计
		this.scope.x_refreshUploadCount();

	},
	
	/**
	 * 文件开始上传事件的回调函数
	 * @param {Event} evt 事件
	 * @param {String} sId 上传文件对应的id
	 */
	x_uploadLoadstartHandle: function(evt,sId){
		
		var scope = this.scope;
		// console.log("loadstrat",sId);
		//根据id查找到对应的record
		var oRecord = scope.x_DataView.store.getById(sId);
		//设置状态
		oRecord.set("state","Uploading");
		var sIndex = scope.x_DataView.store.indexOfId(sId);
		//刷新对应数据的显示
		scope.x_DataView.refreshNode(sIndex);
		//刷新数量统计
		scope.x_refreshUploadCount();
		
	},
	
	/**
	 * 文件完成上传事件的回调函数
	 * @param {Event} evt
	 * @param {String} sId
	 * @return {Booleam} 上传文件成功还是失败
	 */
	x_uploaderLoadHandle: function(evt,sId){

		// console.log("load",sId);
		var scope = this.scope;
		var result,oRecord;
		//将返回内容转换成json
		try
		{
			result = JSON.parse(evt.target.responseText);	
		}
		catch(e)
		{
			result = {"success": false, "message":"返回数据有问题，请检查"};
		}
		
		//对成功和失败进行处理
		if(result.success)
		{
			oRecord = scope.x_DataView.store.getById(sId);
			oRecord.set("state","Success");
			oRecord.set("src",result.message);

		}
		else
		{
			oRecord = scope.x_DataView.store.getById(sId);
			oRecord.set("state","Error");
			result.info = result.message;
		}
		
		//处理消息
		if(typeof result.info !== "undefined" && result.info.trim() !== "")
		{
			oRecord.set("info", result.info);
		}
		
		//刷新对应的文件显示
		var sIndex = scope.x_DataView.store.indexOfId(sId);
		scope.x_DataView.refreshNode(sIndex);
		//刷新数量统计
		scope.x_refreshUploadCount();
		return result.success;

	},

	/**
	 * 上传失败事件对应的回调函数
	 * @param {Event} evt
	 * @param {String} sId
	 */
	x_uploadErrorHandle: function(evt,sId){

		// console.log("Error",sId);
		var scope = this.scope;
		var sInfo = "上传文件时出现未知错误，如果需要上传，请将该文件重新加入上传列表";
		var oRecord = scope.x_DataView.store.getById(sId);
		oRecord.set("state","Error");
		oRecord.set("info", sInfo);
		var sIndex = scope.x_DataView.store.indexOfId(sId);
		scope.x_DataView.refreshNode(sIndex);
		scope.x_refreshUploadCount();

	},
	
	/**
	 * 上传取消事件对应的回调函数
	 * @param {Event} evt
	 * @param {String} sId
	 */
	x_uploadAbortHandle: function(evt,sId){

		// console.log("Abort",sId);
		var scope = this.scope;
		var sInfo = "文件在上传时被取消，如果需要上传，请将该文件重新加入上传列表";
		var oRecord = scope.x_DataView.store.getById(sId);
		oRecord.set("state","Abort");
		oRecord.set("info", sInfo);
		var sIndex = scope.x_DataView.store.indexOfId(sId);
		scope.x_DataView.refreshNode(sIndex);
		scope.x_refreshUploadCount();

	},
	
	/**
	 * 上传进度事件对应的回调函数
	 * @param {Event} evt
	 * @param {String} sId
	 */
	x_uploadProgressHandle: function(evt,sId){
		
		// console.log("Progress",sId);
		var scope = this.scope;
		//文件总体大小
		var total = evt.total;
		//当前上传大小
		var loaded = evt.loaded;
		if(evt.lengthComputable)
		{
			//计算百分比
			var percent = Math.floor(loaded/total*100);
			var oProgress_span = document.querySelector("#x_upload_tr_"+sId + " .progress_span");
			var oProgress_count = document.querySelector("#x_upload_tr_"+sId + " .progress_count");
			//修改进度条样式
			oProgress_span.style.width = percent + "%";
			//修改进度条的文字
			oProgress_count.innerHTML = percent + "%";
			//修改对应record中的数据
			var oRecord = scope.x_DataView.store.getById(sId);
			oRecord.set("progress",percent);
			// console.log(percent);
			//console.log(Math.floor(loaded/total*100));
		}

	},
	
	/**
	 * 设置参数时的回调
	 * 根据repath参数设置的不同，分别返回不同的参数对象
	 * @param {String} sId
	 * @return {Object} 返回以后参数对象
	 */
	x_uploadXhrParamHandle: function(sId){

		var scope = this.scope;
		var oRecord = scope.x_DataView.store.getById(sId);
		var oParam = {};
		//如果能够自定义路径
		if(scope.repath)
		{
			//根据是否需要选择文件时自带的路径来处理路径
			var sPath = scope.useWebkitdirectory ? oRecord.get("path") : "";
			//将路径与文件名拼合在一起
			oParam.url = scope.x_base_path + sPath + oRecord.get("fileName");
		}
		//如果不能够自定义路径
		else
		{
			scope.x_set_domain();
			//需要传回 domain filedataFileName rename 三个参数
			oParam.domain = scope.x_get_domain();
			oParam.filedataFileName = oRecord.get("fileName");
			//根据后端需要的参数形式进行处理，这里暂时没确定0和1的含义。
			oParam.rename = scope.rename ? 0 : 1;
		}
		return oParam;

	},

	x_get_domain: function(){

		return this.x_domain;

	},

	x_set_domain: function(){

		var iLen = this.destinationDomains.length;
		var iDomian = Math.floor(Math.random()*100)%iLen;
		this.x_domain = this.destinationDomains[iDomian];

	},
	
	/**
	 * 初始化DataView
	 */
	x_init_dataView: function(){

		var _this = this;
		
		//构造record的结构 
		//sId：文件对应的id | path：路径 | fileName：文件名称 | size：文件大小 | progress：上传进度 | 
		//state：当前状态 |src: 上传成功返回的路径 | isEdit：是否允许编辑，好像没用到，暂时保留 
		this.x_record = Ext.data.Record.create([{name:"sId"},{name:"path"},{name:"fileName"},{name:"size"},{name:"progress"},{name:"state"},{name:"src"},{name:"isEdit"}]);

		//设置一个空的数据集合
		var json = {
					list:[
					]
		};
		
		//建立一个JsonStore
		var store = new Ext.data.JsonStore({
			data :json,
		    root: 'list',
		    id:"sId",
		    fields: this.x_record
		});

		//建立一个xTemplate用来显示文件列表
		//a.根据是否要设置路径来判断是否需要显示路径的相关内容
		//b.根据数据的状态state来判断数据的表现形式
		var tpl = new Ext.XTemplate(
			'<table cellpadding="0" cellspacing="0" border="0" class="tableStyle">',
			'<thead>{[this.initPathth()]}<th>文件名</th><th style=" width: 100px; ">文件大小</th><th style="text-align: center; width: 110px; ">文件状态</th><th style="text-align:center; width: 100px;">操作</th></tr></thead>',
			'<tbody>',
		    '<tpl for=".">',
		    	'<tr class="thumb-wrap x_upload_tr_{state}" id="x_upload_tr_{sId}">',
		    		'<tpl if="src && src.length &gt; 0">',
		    			'<td {[this.initTdColspan()]} class="filePath">{src}</td>',
		    		'</tpl>',
		    		'<tpl if="!src || src.length == 0">',
				        '{[this.initPath(values.path)]}',
				        '<td class="fileName">{fileName}</td>',
		    		'</tpl>',
			        '<td>{size}</td>',
			        '<td style="text-align: center;">{[this.initState(values)]}</td>',
			        '<td style="text-align: center; padding-bottom: 2px;">{[this.initHandle(values)]}</td>',
			     '</tr>',
		    '</tpl>',
		    '</tbody></table>',
		    '<div class="uploadContinue" ><span>您可以继续拖动添加文件</span></div>'
		);
		
		//根据是否要自定义路径来确定是否返回跨列参数
		tpl.initTdColspan = function(){

			if(!_this.repath)
			{
				return '';
			}
			else
			{
				return 'colspan="2"';
			}

		};
		
		//初始化路径的th，这里也是因为自定义路径要判断路径是否显示
		tpl.initPathth = function(){

			if(!_this.repath)
			{
				return "";
			}

			var cls = "useWebkitdirectory_" + (_this.useWebkitdirectory ? "true" : "false");

			return '<tr><th>（<span class="useWebkitdirectory '+ cls +'">使用系统路径</span>） 上传路径</th>';

		};
		
//		//此方法好像没用，测试完清理掉
//		tpl.initFileName = function(fileName){
//
//			return fileName;
//
//		};
		
		//根据状态和进度来显示进度条
		tpl.initState = function(values){

			var progress = values.progress;
			switch(values.state)
			{
				case "Ready":
					return '<span class="progress_ready">准备上传</span>';
				break;

				case "Success":
					return '<span class="progress_success">上传成功</span>';
				break;

				case "Error":
					return '<span class="progress_error">上传失败</span>';
				break;

				case "Abort":
					return '<span class="progress_abort">取消上传</span>';
				break;

				case "Uploading":

				default:
					return '<div class="progress_bar"><span style="width: '+ progress + '%;" class="progress_span"></span><span class="progress_count">'+ progress +'%</span></div>';
				break;
			}

		};
		
		//根据状态来显示操作栏
		tpl.initHandle = function(values){

			var sHtml = "";
			//为了显示整齐，设置空的span进行占位
			var sEmptyDel = '<span class="x_upload_bt x_upload_bt_del_gray"></span>';
			var sEmptyCopy = '<span class="x_upload_bt x_upload_bt_copy_gray"></span>';
			var sEmptyChange_state_ready = '<span class="x_upload_bt x_upload_bt_change_state_ready_gray"></span>';
			var sEmptyCopyInfo = '<span class="x_upload_bt x_upload_bt_info_gray"></span>';
			
			//根据不同状态，生成不同的操作粒子
			switch(values.state)
			{
				case "Ready":
					sHtml = sHtml + '<span class="x_upload_bt x_upload_bt_del" type="Ready" title="点击删除尚未上传的文件" ></span>';
					sHtml = sHtml + sEmptyCopy + sEmptyChange_state_ready;
				break;

				case "Success":
					sHtml = sHtml + sEmptyDel;
					sHtml = sHtml + '<span class="x_upload_bt x_upload_bt_copy" type="Ready" title="点击复制路径" ></span>';
					sHtml = sHtml + sEmptyChange_state_ready;
				break;

				case "Error":
					sHtml = sHtml + '<span class="x_upload_bt x_upload_bt_del" type="Error" title="点击删除错误文件" ></span>';
					sHtml = sHtml + sEmptyCopy;
					sHtml = sHtml + '<span class="x_upload_bt x_upload_bt_change_state_ready" type="Error" title="重新放入上传列表" ></span>';
				break;

				case "Abort":
					sHtml = sHtml + '<span class="x_upload_bt x_upload_bt_del" type="Abort" title="点击删除取消文件" ></span>';
					sHtml = sHtml + sEmptyCopy;
					sHtml = sHtml + '<span class="x_upload_bt x_upload_bt_change_state_ready" type="Abort" title="重新放入上传列表" ></span>';
				break;
				
				case "Uploading":

				default:
					sHtml = sHtml + sEmptyDel + sEmptyCopy + sEmptyChange_state_ready ;
				break;
			}
			
			if(typeof values.info !="undefined" && values.info !== "")
			{
				sHtml = sHtml + '<span class="x_upload_bt x_upload_bt_info" title="'+ values.info +'"></span>';
			}
			else
			{
				sHtml = sHtml + sEmptyCopyInfo;
			}

			return sHtml;

		};
		
		//初始化路径 这里传入的path是选择文件时file的Webkitdirectory属性中的路径
		tpl.initPath = function(path){
			
			if(!_this.repath)
			{
				return "";
			}
			
			//如果不用Webkitdirectory的话，则将其置空
			if(!_this.useWebkitdirectory)
			{
				path = "";
			}
			
			//处理路径的链接方式
			if(!/\/$/.test(_this.x_base_path))
			{
				path = _this.x_base_path + "/" + path;
			}
			else
			{
				path = _this.x_base_path + path;
			}

			return '<td>' + path + '</td>';

		};
		
		//创建一个dataview
		this.x_DataView = new Ext.DataView({
		        store: store,
		        tpl: tpl,
		        autoHeight:true,
//		        multiSelect: true,
		        overClass:'x-view-over',
		        itemSelector:'tr.thumb-wrap',
		        emptyText: '<div class="x_uploadEmpty_box" >还没有需要上传的文件，请点击上方按钮选择或者直接将文件拖动到此处</div>'
	    });
		
	    //将dataview做为panel的items。
	    this.items = [this.x_DataView];

    },
	
    /**
     * 初始化事件
     */
	x_init_event: function(){
		
		var _this = this;
		//不知道为什么，使用this.body.on的时候evt指向有问题，只能使用dom的addEventListener来绑定。
		//对panel的body设置拖放事件
		this.body.dom.addEventListener("dragover", function(evt){ evt.stopPropagation(); evt.preventDefault(); }, false);
		this.body.dom.addEventListener("drop", function(evt){ _this.x_process_files(evt); }, false);
		
		//在panel的body上做一个点击事件代理，用来完成body中的一些元素上的事件
		this.body.on("click",this.x_useWebkitdirectoryHandle,this);
		
		//为input file绑定change事件，在选择文件后，触发处理文件的方法
		this.x_uploadFile.el.on("change", this.x_process_files, this);
		
		//如果需要自定义路径，则对x_uploadPath增加change和keydown事件，在其改变路径和在路径中输入回车时，触发路径处理方法。
		if(this.repath)
		{
			this.x_uploadPath.on("change", this.x_updata_path, this);
			this.x_uploadPath.el.on("keydown", this.x_updata_path_keydown_handle, this);
		}
		this.x_DataView.on("click", this.x_DataView_click_handle, this);

	},
	
	/**
	 * 根据dom上是否有预定的class来处理是否使用文件自带路径的方法
	 * @param {Event} evt 点击事件
	 * @param {DomElement} target 点击的dom对象 
	 */
	x_useWebkitdirectoryHandle: function(evt,target){
		
		var Etarget = Ext.get(target);
		if(Etarget.hasClass("useWebkitdirectory_true"))
		{
			this.useWebkitdirectory = false;
			this.x_DataView.refresh();
			return;
		}
		if(Etarget.hasClass("useWebkitdirectory_false"))
		{
			this.useWebkitdirectory = true;
			this.x_DataView.refresh();
			return;
		}

	},
	
	/**
	 * 设置dataview的itemSelector的点击事件
	 * @param {DataView} dataView
	 * @param {Number} index
	 * @param {DomElement} node
	 * @param {Event} e
	 */
	x_DataView_click_handle: function(dataView, index, node, e){
		
		//点击对象对应的dom
    	var oTarget = e.target;
    	// console.log(oTarget);
    	
    	//如果点击的dom对象的class里面有fileName并且可以自定义文件名，则对此td内的内容进行编辑处理
    	//步骤：
    	//1.取出td中的内容，
    	//2.拆分开扩展名和文件名，
    	//3.将文件名放入一个input，对此input绑定blur和keydown事件，
    	//4.在此触发事件后修改文件名称，并更新对应的record中的数据，并使用refreshNode刷新展现。
    	//这个处理代码比较长，需要考虑是否需要重构解耦。
    	//需要修改 ok 使其更加健壮 对于已经插入input的部分，由于某种原因input没有消失，再再次点击时，需要做一个容错处理。
		if(Ext.get(oTarget).hasClass("fileName") && this.rename && Ext.get(oTarget).query("input").length<1)
    	{
    		var sFile = oTarget.innerHTML;
    		var aTemp = sFile.split(".");
    		var sFileExpandedName = aTemp.pop();
    		var sFileName = aTemp.join(".");
    		var iPaddingLR = Ext.get(oTarget).getPadding("lr");
   			// console.log(oTarget.clientWidth);
    		//这里给激活的td设置宽度，避免插入input后，表格抖动，增加可用性，这里-10是减去这个表格的左右padding值，有时间可以看下如何动态获取padding，避免样式改变以后出现问题。
    		oTarget.style.width = (oTarget.clientWidth-iPaddingLR) + "px";
    		//根据宽度，动态设定插入input的宽度，这里-60是给扩展名+padding预留的宽度。
    		var iInputWidth = (oTarget.clientWidth-50-iPaddingLR) > 40 ? (oTarget.clientWidth-50-iPaddingLR) : 40;
    		oTarget.innerHTML = '<input type="text" class="x_change_fileName_input" style="width:'+ iInputWidth+'px" value="'+ sFileName +'"/> .' + sFileExpandedName;
			//微调td的padding-top和padding-bottom，避免表格抖动
    		oTarget.style.paddingTop = (Ext.get(oTarget).getPadding("t") - 3) + "px";
    		oTarget.style.paddingBottom = (Ext.get(oTarget).getPadding("b") - 3) + "px";
    		var Dinput = Ext.query("input.x_change_fileName_input",oTarget)[0];
    		Dinput.focus();
    		//对input绑定事件，用来获取修改后的filename名称
    		Ext.get(Dinput).on({
    			"blur":{
    				fn:function(){
    					// console.log("blur_error");
		  				if(this.value !=="") 
		  				{
		  					var record = dataView.store.getAt(index);
		  					record.set("fileName",this.value + "." + sFileExpandedName);
		  				}
		  				//这里对td设置空是为了避免一个在点击浏览器以为地区使input失去焦点以后，导致datview.refreshNode出错的一个问题
		  				//真实原因没找到，需要以后深入查找。
		  				//待研究
		  				oTarget.innerHTML ="";
		  				dataView.refreshNode(index);
	    			}
	    		},
	    		"keydown":{
	    			//在输入框中输入回车键以后，设置input失去焦点。
	    			fn:function(evt){
	    				if(evt.keyCode === 13)
	    				{
	    					this.blur();
		    			}
	    			}
	    		}
    		});
    	}
		
    	//如果点击的是删除按钮，则对该条内容进行删除操作。
    	if(Ext.get(oTarget).hasClass("x_upload_bt_del"))
    	{
    		var record = dataView.store.getAt(index);
    		var sId = record.get("sId");
    		var sType = oTarget.getAttribute("type");
    		//删除uploader中的文件记录
    		this.x_uploader.removeOneFileFromList(sId,sType);
    		this.x_DataView.store.remove(record);
    		//删除完了以后，不要忘记更新数量显示
    		this.x_refreshUploadCount();
    	}
    	
    	//如果点击的是将文件状态重置为准备上传的话，则将该条内容重新放入准备上传的列表
    	if(Ext.get(oTarget).hasClass("x_upload_bt_change_state_ready"))
    	{
    		var record = dataView.store.getAt(index);
    		var sId = record.get("sId");
    		var sType = oTarget.getAttribute("type");
    		var oFile = this.x_uploader.removeOneFileFromList(sId,sType);
    		this.x_uploader.addOneFileToList(oFile.id,oFile.file,"Ready");
    		record.set("state","Ready");
    		record.set("progress",0);
    		record.set("info","");
    		var iIndex = this.x_DataView.store.indexOf(record);
    		this.x_DataView.refreshNode(iIndex);
    		//改变完状态以后，不要忘记更新数量显示
    		this.x_refreshUploadCount();
    	}
		
    	//如果点击的是复制路径按钮或者直接点击含有服务器端返回路径的td，则对路径添加辅助copy的方法，方便复制。
    	if((Ext.get(oTarget).hasClass("x_upload_bt_copy") || Ext.get(oTarget).hasClass("filePath")) && Ext.query(".filePath",node).length>0 && Ext.get(oTarget).query("input").length<1)
    	{
    		//这个地方取巧了，是获取了当前显示条目的第一个子元素，需要改的更加明确一点。
    		//需要修改 ok
    		// var oTd = node.firstChild;
    		var oTd = Ext.query(".filePath",node)[0];
    		var sPath = oTd.innerHTML;
    		var iPaddingLR = Ext.get(oTd).getPadding("lr");
    		//这里给激活的td设置宽度，避免插入input后，表格抖动，增加可用性，这里-10是减去这个表格的左右padding值，有时间可以看下如何动态获取padding，避免样式改变以后出现问题。
    		oTd.style.width = (oTd.clientWidth-iPaddingLR) + "px";
    		//根据宽度，动态设定插入input的宽度，这里-120是给后面说明留的空间。
    		var iInputWidth = (oTd.clientWidth - 110 - iPaddingLR) > 80 ? (oTd.clientWidth - 110 - iPaddingLR) : 80;
    		oTd.innerHTML = '<input type="text" class="x_copy_filePath_input" style="width:'+ iInputWidth +'px" readonly value="'+ sPath +'"/>' + ' <span style="color: #999;" class="x_copy_filePath_info">ctrl+c 进行复制</span>';
			//微调td的padding-top和padding-bottom，避免表格抖动
    		oTd.style.paddingTop = (Ext.get(oTd).getPadding("t") - 3) + "px";
    		oTd.style.paddingBottom = (Ext.get(oTd).getPadding("b") - 3) + "px";
    		var Dinput = Ext.query("input.x_copy_filePath_input",oTd)[0];
    		Dinput.focus();
    		//对input绑定blur，keydown，copy事件，blur和keydown是为了隐藏出现的input，copy事件是监控用户对于input内部内容的copy情况，用于通知用户是否复制成功了。
    		Ext.get(Dinput).on({
    			"blur":{
    				fn:function(){
    					//这里对td设置空是为了避免一个在点击浏览器以为地区使input失去焦点以后，导致datview.refreshNode出错的一个问题
    					//待研究
    					oTd.innerHTML ="";
    					// console.log("blur");
		  				dataView.refreshNode(index);
	    			}
	    		},
	    		"keydown":{
	    			fn:function(evt){
	    				if(evt.keyCode === 13)
	    				{
	    					this.blur();
		    			}
	    			}
	    		},
	    		"click":{
	    			fn:function(){
	    				this.select();
	    			}
	    		},
	    		"copy":{
	    			fn:function(evt){
	    				// console.log(evt);
	    				var Dinfo = Ext.query("span.x_copy_filePath_info",oTd)[0];
	    				Dinfo.innerHTML = "复制成功";
	    				Dinfo.setAttribute("style","color: #157a07");
	    			}
	    		}
    		});    		
    	}

	},
	
	/**
	 * 更新自定义路径
	 */
	x_updata_path: function(){

		var sPath = this.x_uploadPath.getValue().trim();
		//处理路径头部的http://，如果没有正确填写，则动态添加
		if(sPath !== "" && !/^http:\/\//.test(sPath))
		{
			sPath = "http://" + sPath;
			this.x_uploadPath.setValue(sPath);
		}
		//处理路径尾部的"/"，如果没有正确填写，则动态添加
		if(sPath !== "" && !/\/$/.test(sPath))
		{
			this.x_uploadPath.setValue(sPath + "/");
		}
		//将值设置给x_base_path参数
		this.x_base_path = this.x_uploadPath.getValue();
		//刷新整个DataView
		this.x_DataView.refresh();

	},
	
	/**
	 * 处理路径输入框中的keydow事件，如果输入了回车，则让次输入框失去焦点，从而触发输入框的change事件。
	 * @param {Event} evt
	 */
	x_updata_path_keydown_handle: function(evt){

		if(evt.keyCode === 13)
		{
			this.x_uploadPath.el.dom.blur();
		}

	},
	
	/**
	 * 对选择的文件进行处理（选择文件，选择文件夹，拖拽选择文件）
	 * @param {Event} evt
	 */
	x_process_files: function(evt){
		//阻止默认事件
		evt.stopPropagation();
		evt.preventDefault();
		//选择文件和选择文件夹获取的文件数组在evt.target.files中，而拖拽选择文件获取的文件数组在evt.dataTransfer.files
		var files = evt.target.files || evt.dataTransfer.files;
		//遍历文件数组，筛选掉其中不符合要求的文件，这里主要是文件夹。
		for(var i = 0, iLen = files.length; i< iLen; i++)
		{
			var file = files[i];
			//如果此文件的file.type为空（也就是说此file是个文件夹）
			//回头要测试一下用file.type做为筛选的条件是否正确
			//需要修改 ok file.type 果然不靠谱，很多文件类型返回不了值，
			//改用 fileSize 和 type综合判断，如果size是0的话，并且type为空的话，就认为他是一个文件夹。
			if(file.fileSize !== 0 || file.type !== "")
			{
				// var sId = creatId();
				// 生成一个唯一id，用来做为uploader中文件，与dataview中数据与表现的联系。
				var sId = Ext.id("","upload_file_");
				// 处理文件中webkitRelativePath，将其中所带的文件名去掉，只留下路径
				var sPath = file.webkitRelativePath.replace(/[^\/]*$/,"");
				// 初始化一条数据
				var o = new this.x_record({sId:sId,path:sPath,fileName:file.fileName,size:file.fileSize,progress:0,state:"Ready",src:"",isEdit:true,info:""},sId);
				// 将此条数据加入store
				this.x_DataView.store.add(o);
				// 将该文件加入uploader的listReady数组中
				this.x_uploader.addOneFileToList(sId,file,"Ready");
			}
		}
		//刷新这个DataView
		this.x_DataView.refresh();
		//设置uploader的状态为Ready
		this.x_uploader.setState("Ready");
		//如果允许使用自定义路径的话，对路径设置的input获取焦点并全选里面的内容
		if(this.repath)
		{
			this.x_uploadPath.el.dom.focus();
			this.x_uploadPath.el.dom.select();
		}
		//亲，不要忘记更新数量显示
		this.x_refreshUploadCount();

	},
	
	/**
	 * 开始上传文件
	 */
	x_beginUpload: function(){
		
		//如果uploader中的listReady（准备上传数组中）还有文件等待上传，则调用uploader的上传方法。
		if(this.x_uploader.listReady.length >0)
		{
			//如果允许自定义路径，并且路径为空的时候，会跳转的else弹出提示，并将焦点设置到路径输入框中。
			if(!this.repath || this.x_base_path.trim() !== "")
			{
				this.x_uploader.upload();
			}
			else
			{
				alert("填写路径不能为空。");
				this.x_uploadPath.el.dom.focus();
			}
			
		}
		else
		{
			alert("还没上传文件，请先上传文件后再点击上传");
		}

	},
	
	/**
	 * 刷新数量统计，分别显示当前 准备上传 正在上传 上传成功 上传失败 上传取消的数量。
	 */
	x_refreshUploadCount: function(){

		var uploader = this.x_uploader;
		//建立状态对象列表
		var oList = {
					"listReady":"准备上传",
					"listUploading":"正在上传",
					"listSuccess":"上传成功",
					"listError":"上传失败",
					"listAbort":"上传失败"
				};
		var s = "";
		//因为此方法会在uploader没创建以前就会触发，这里需要判断一下uploader是否被建立了。
		if(typeof uploader != "undefined")
		{
			//遍历对象列表，如果状态对应的uploader中的数组长度大于0，则表示此状态中有数量，则将拼到字符串中
			for( i in oList)
			{
				var aTemp = uploader[i];
				if(aTemp.length > 0)
				{
					s = s + oList[i] + ":" + aTemp.length + " ";
				}
			}
		}
		//显示到x_uploadCount容器中。
		this.x_uploadCount.innerHTML = s;
	},

	/**
	 * 以多选文件的方式来打开文件选择
	 */
	x_selectFiles: function(){

		this._openFile("multiple");

	},
	
	/**
	 * 以目录选择的方式来打开文件选择
	 */
	x_selectCatalog: function(){

		this._openFile("catalog");

	},
	
	/**
	 * 根据传入的参数不同，设置打开文件选择器的方式
	 * @param {String} sType (single | multiple | catalog)
	 */
	_openFile: function(sType){

		var DfileUpload = this.x_uploadFile.el.dom;
		switch(sType){
			//只能选择一个文件
			case "single":
				DfileUpload.removeAttribute("multiple");
				DfileUpload.removeAttribute("webkitdirectory");
			break;
			//选择多个文件
			case "multiple":
				DfileUpload.removeAttribute("webkitdirectory");
				DfileUpload.setAttribute("multiple", true);
			break;
			//选择文件夹
			case "catalog":
				DfileUpload.removeAttribute("multiple");
				DfileUpload.setAttribute("webkitdirectory", true);
			break;
			default:
			break;
		}
		//触发input file，打开文件选择器。
		DfileUpload.click();

	}

});

Ext.reg('xUploadDevPanel', Ext.ux.UploadDevPanel);