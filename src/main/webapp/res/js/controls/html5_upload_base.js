

/**
 * 基于html5技术上传的基础类，
 * 主要是管理上传文件列表和上传方式
 * @param {Object} opt 需要传入的参数配置
 */
var Html5Upload = function(opt){

	var opt = opt || {};
	var emptyFn = function(){return {}};
	this.scope = opt.scope || this;
	//上传的线程，默认为单线程
	this.threadCount = opt.threadCount || 1;
	//上传到的地址
	this.xhrUrl = opt.xhrUrl || "";
	if(this.xhrUrl === "")
	{
		alert("您没有设置上传地址");
	}
	//上传开始的回调函数
	this.loadstartCallback = opt.loadstartCallback || emptyFn;
	//上传进度的回调函数
	this.progressCallback = opt.progressCallback || emptyFn;
	//上传取消的回调函数
	this.abortCallback = opt.abortCallback || emptyFn;
	//上传错误的回调函数
	this.errorCallback = opt.errorCallback || emptyFn;
	//上传完成的回调函数
	this.loadCallback = opt.loadCallback || emptyFn;
	//上传结束的回调函数
	this.loadendCallback = opt.loadendCallback || emptyFn;
	//设置上传参数的回调函数
	this.xhrParamCallback = opt.xhrParamCallback || emptyFn;
	//设置状态的回调函数
	this.setStateCallback = opt.setStateCallback || emptyFn;
	
	//准备上传文件的文件列表
	this.listReady = [];
	//上传成功文件的数组列表
	this.listSuccess = [];
	//上传错误文件的数组列表
	this.listError = [];
	//上传取消文件的书中列表
	this.listAbort = [];
	//正在上传文件的数组列表
	this.listUploading = [];
	//上传进程xhr的数组列表
	this.listXhr = {};
	//正在上传的文件数
	this.uploadingCount = 0;
	//当前上传实例的状态
	this.state = "";
	this.lasetState = "";
	this.setState("Initializing");
};

Html5Upload.prototype = {
	
	/**
	 * 添加一个文件到指定的文件列表中
	 * @param {String} sId 文件对应的id
	 * @param {Object} oFile file对象
	 * @param {String} listType 列表类型 (Ready | Success | Error | Abort | Uploading )
	 */
	addOneFileToList: function(sId,oFile,listType){

		var aList = this["list" + listType];

		if(Ext.isArray(aList))
		{
			aList.push({id:sId,file:oFile});
			// console.log(sId + ": addOneFileToList" + listType);
		}
		else
		{
			// console.log(sId + ": addOneFileToList (" + listType + ") has problem");
		}
		
	},
	
	/**
	 * 根据文件对应的id从指定的文件列表中获取包含此文件的对象数组项
	 * @param {String} sId sId 文件对应的id
	 * @param {String} listType listType 列表类型
	 * @return {Array Object} [{id:String,file:File}]
	 */
	getOneFileFromList: function(sId,listType){
		
		var aList = this["list" + listType];
		
		if(Ext.isArray(aList))
		{
			var i, iLen = aList.length;
			for(i=0; i< iLen; i++)
			{
				var oFile = aList[i];
				if(oFile.id === sId)
				{
					// console.log(aList[i]);
					// console.log(aList[i].id + ": getOneFileFromList" + listType);
					return aList[i];
				}
			}
			return null;
		}
		else
		{
			// console.log(sId + ": removeOneFileFormList (" + listType + ") has problem");	
		}		
		
	},
	
	/**
	 * 根据id从指定的文件列表中移除一个文件，并返回该文件对象，如果找不到，则返回null
	 * @param {String} sId 文件对应的id
	 * @param {String} listType listType 列表类型
	 * @return {Array Object} [{id:String,file:File}]
	 */
	removeOneFileFromList: function(sId,listType){

		var aList = this["list" + listType];

		if(Ext.isArray(aList))
		{
			var i, iLen = aList.length;
			for(i=0; i< iLen; i++)
			{
				var oFile = aList[i];
				if(oFile.id === sId)
				{
					var aRemoved = aList.splice(i,1);
					// console.log(aRemoved[0]);
					// console.log(aRemoved[0].id + ": removeOneFileFormList" + listType);
					return aRemoved[0];
				}
			}
			return null;
		}
		else
		{
			// console.log(sId + ": removeOneFileFormList (" + listType + ") has problem");	
		}

	},

	/**
	 * 将新建的xhr请求加入xhr列表中
	 * @param {String} sId 请求对应的文件的id
	 * @param {XMLHttpRequest} 建立的请求
	 */
	addOneXhrToListXhr: function(sId,xhr){
		
		this.listXhr[sId] = xhr;
		
	},
	
	/**
	 * 移除已经完成操作的xhr请求
	 * @param {String} sId 请求的唯一id
	 */
	removeFromListXhr: function(sId){
		
		this.listXhr[sId] = null;
		delete this.listXhr[sId];
		// console.log(sId + ": removeFormListXhr");

	},

	/**
	 * 添加一个文件到准备上传的文件列表中
	 * @param {String} sId 文件对应的唯一id
	 * @param {Object} oFile 文件对象
	 * 此函数被addOneFileToList 代替了
	 */
//	addOneFileToListReady: function(sId,oFile){
//		
//		this.addOneFileToList(sId,oFile,"Ready");
//		
//	},
	
	/**
	 * 根据id从准备上传的文件列表中移除一个文件，并返回该文件对象，如果找不到，则返回null
	 * @param {String} sId 文件对应的唯一id
	 * @return {Array Object} 
	 * 此函数被removeOneFileFromList 代替了
	 */
//	removeOneFileFromListReady: function(sId){
//		
//		return this.removeOneFileFromList(sId,"Ready");
//
//	},

	/**
	 * 将文件加入listCache中
	 * @param {Object} oFile 包含文件的数组对象。
	 * 此函数被addOneFileToList 代替了
	 */
//	addOneFileTolistCache: function(oFile){
//		
//		this.addOneFileToList(oFile.id,oFile.file,"Uploading");
//		this.listUploading[oFile.id] = oFile;
//		this.listUploading.length ++;
//		
//	},
	
	/**
	 * 将完成操作的文件从正在上传列表中移除
	 * @param {String} sId 文件对应的id
	 * 此函数被removeOneFileFromList 代替了
	 */
//	removeFormlistCache: function(sId){
//		
//		return this.removeOneFileFromList(sId,"Uploading");
//		this.listUploading[sId] = null;
//		delete this.listUploading[sId];
//		this.listUploading.length --;
//		console.log(sId + ": removeFormlistCache");
//
//	},
	
	/**
	 * 将上传成功的文件添加到上传成功的列表中
	 * @param {Array Object} oFile 包含文件的数组对象
	 * 此函数被addOneFileToList 代替了
	 */
//	addOneFileToListSuccess: function(oFile){
//		this.addOneFileToList(oFile.id,oFile.file,"Success");
//		this.listSuccess.push(oFile);
//		console.log(oFile.id + ": addOneFileToListSuccess");
//
//	},
	
	/**
	 * 将上传失败的文件添加到上传失败的列表中
	 * @param {Array Object} oFile 包含文件的数组对象
	 * 此函数被addOneFileToList 代替了
	 */
//	addOneFileToListError: function(oFile){
//		this.addOneFileToList(oFile.id,oFile.file,"Error");
//		this.listError.push(oFile);
//		console.log(oFile.id + ": addOneFileToListError");
//
//	},

	/**
	 * 将上传取消的文件添加到上传取消的列表中
	 * @param {Array Object} oFile 包含文件的数组对象
	 */
//	addOneFileToListAbort: function(oFile){
//		this.addOneFileToList(oFile.id,oFile.file,"Abort");
//		this.listError.push(oFile);
//		console.log(oFile.id + ": addOneFileToListAbort");
//
//	},
	
	/**
	 * 从准备上传列表中找到文件进行上传
	 */
	upload: function(){
		//如果列表是空的，则修改实例的状态为上传完成。
		if(this.listReady.length === 0)
		{
			if(this.listUploading.length === 0)
			{
				if(this.listError.length >0 )
				{
					this.setState("Error");
				}
				else if(this.listAbort.length >0)
				{
					this.setState("Abort");
				}
				else
				{
					this.setState("Complete");
				}
				// console.log("all list file has upload");
			}
			return;
		}
		//如果当前正在上传的文件数小于允许线程数，则从列表中取出一个文件进行上传。
		while(this.uploadingCount < this.threadCount && this.listReady.length > 0)
		{
			// console.log(this.listReady.length);
			var oFile = this.listReady.shift();
			this.addOneFileToList(oFile.id,oFile.file,"Uploading");
			this.uploadOneFile(oFile);
			this.setState("Uploading");
			this.uploadingCount ++ ;
		}
//		this.stopAllUpload();
	},
	
	/**
	 * 建立一个上传请求
	 * @param {Array Object} oFile 包含文件的数组对象
	 */
	uploadOneFile: function(oFile){

		var _this = this;
		var oFile = oFile;
		var sId = oFile.id;
		//调用参数回调接口，返回外部处理的参数。
		var oParam = this.xhrParamCallback(sId);
		var xhr = new XMLHttpRequest();
		// console.log(xhr);
		//将xhr对象加入xhr列表，用于以后有可能发生的abort操作。
		this.addOneXhrToListXhr(sId,xhr);

		var file = oFile.file;
		
		xhr.open("POST",this.xhrUrl,true);

		xhr.onloadstart = function(evt){
			// console.log("*---------------*");
			// console.log(arguments);
			_this.loadstartCallback(evt,sId);
		};
		
		//上传完成后的处理
		xhr.onload = function(evt){
			//回调函数的调用
			var result = _this.loadCallback(evt,sId);
			
			if(typeof result !== "boolean" )
			{
				result = true;
			}
			
			_this.removeFromListXhr(sId);
			_this.removeOneFileFromList(sId,"Uploading");
			if(result === true)
			{
				_this.addOneFileToList(oFile.id,oFile.file,"Success");
			}
			else
			{
				_this.addOneFileToList(oFile.id,oFile.file,"Error");
			}
			_this.uploadingCount--;
			_this.upload();
					
		};
		
		//上传失败的处理
		xhr.onerror = function(evt){

			//回调函数的调用
			_this.errorCallback(evt,sId);
			_this.removeFromListXhr(sId);
			_this.removeOneFileFromList(sId,"Uploading");
			_this.addOneFileToList(oFile.id,oFile.file,"Error");
			_this.uploadingCount--;
			_this.upload();

		};
		
		//上传进度的处理
		xhr.upload.onprogress = function(evt){

			//回调函数的调用
			_this.progressCallback(evt,sId);

		};
		
		//上传取消的处理
		xhr.onabort = function(evt){

			//回调函数的调用
			_this.abortCallback(evt,sId);
			_this.removeFromListXhr(sId);
			_this.removeOneFileFromList(sId,"Uploading");
			_this.addOneFileToList(oFile.id,oFile.file,"Abort");
			_this.uploadingCount--;
			
		};

		xhr.onloadend = function(evt){
			_this.loadendCallback(evt.sId);
		}
    
	    // xhr.setRequestHeader("X-File-Name", file.fileName);
	    // xhr.setRequestHeader("X-File-Size", file.fileSize);
	    // xhr.setRequestHeader("X-File-Type", file.type);
		
		//使用FormData处理需要上传的数据
	    var fd = new FormData();
	    for( i in oParam) {
	    	fd.append(i,oParam[i]);
	    }
	    // fd.append("X-File-Name",oParam.fileName || file.fileName);
	    // fd.append("X-File-Size",oParam.fileSize || file.fileSize);
	    // fd.append("X-File-Type",oParam.type || file.type);
	    // fd.append("X-File-Path",oParam.path || file.webkitRelativePath);
//	    fd.append("filedata", Ext.encode(file)); //主要为了测试时处理测试对象（非文件对象）
	    fd.append("filedata", file);
	    xhr.send(fd);
	    // xhr.abort();

	},
	
	/**
	 * 停止某个正在上传文件的上传进程（未测试）
	 * @param {String} sId 文件对应的唯一id
	 */
	stopUpload: function(sId){

		var xhr = this.listXhr[sId];
		
		if(typeof xhr !== "undefined")
		{
			// console.log(xhr);
			xhr.abort();
		}

	},
	
	/**
	 * 停止所有正在上传文件的上传（未测试）
	 */
	stopAllUpload: function(){
		
		for(var sId in this.listXhr)
		{
			this.stopUpload(sId);
		}
		this.setState("Stop");

	},
	
	/**
	 * 设置当前上传实例的状态
	 * @param {String} sState
	 */
	setState: function(sState){
		
		//如果传回的状态值没有变化，则直接返回，不触发后面的自定义事件
		if(this.state === sState)
		{
			return;
		}

		//如果当前状态时Uploading，而传入状态时Ready时，即在文件上传中添加新文件，不会去改变上传状态。
		if(this.state === "Uploading" && sState === "Ready")
		{
			return;
		}

		this.state = sState;
		this.setStateCallback(sState);

	},
	
	/**
	 * 返回当前上传实例的状态
	 * @return {String}
	 */
	getState: function(){

		return this.state;

	}

};

/*************************************************/
/***************test of this class****************/
/*************************************************/

/**
 * 创建一个测试数据
 * @param {Number} iCount 数据的数量
 * @return {Array} 返回一个数组对象
 */
// function creatUploadTestData(iCount){

// 	var result = {};
// 	var seed = {fileName:"upload_file_--.jpg",fileSize:"1024--",type:["image/jpg","image/gif","image/png","image/jpeg"],path:"v.ifeng.com/test/2012/02/21--"};
// 	for(var i = 0; i< iCount; i++)
// 	{
// 		var id = creatId();
// 		result[id] = {};
// 		for(var j in seed)
// 		{
// 			var s = seed[j];

// 			if(typeof s === "string")
// 			{
// 				s = s.split("--").join(i);
// 			}
// 			else
// 			{
// 				var ii = Math.floor(Math.random()*100) % s.length;
// 				s = s[ii];
// 			}

// 			result[id][j] = s;
// 		}
// 	}
// 	return result;
// }


// var html5UploadInstance;
// /**
//  * 测试函数
//  */
// function test_html5_upload_base(){

// 	var testData = creatUploadTestData(5);

// 	html5UploadInstance = new Html5Upload({
// 		threadCount : 2,
// 		xhrParamCallback: function(sId){
// 			return testData[sId];
// 		}
// 	});

// 	for(var i in testData)
// 	{
// 		html5UploadInstance.addOneFileToList(i,testData[i],"Ready");
// 	}
// 	html5UploadInstance.removeOneFileFromList("upload_file_3","Ready");
// 	html5UploadInstance.removeOneFileFromList("upload_file_2","Ready");

// 	console.log(html5UploadInstance);
// 	html5UploadInstance.upload();
// //	html5UploadInstance.stopUpload("upload_file_4");

// }

// (function (){
// var id_number = 0;
// var id_header = "upload_file";

// 	return window["creatId"] = function(){
// 		id_number++;
// 		return id_header + "_"	+ id_number;
// 	}
// })();