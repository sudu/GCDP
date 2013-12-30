Ext.namespace("Ext.ux");

/**
 * 级联comboBox的抽象类，不要直接使用
 * @class Ext.ux.LinkageComboBox
 * @extends Ext.form.ComboBox
 */
Ext.ux.LinkageComboBox = Ext.extend(Ext.form.ComboBox, {
    editable: false,
    typeAhead: true,
    triggerAction: 'all',
    mode: 'local',
    valueField: 'key',
    displayField: 'value',
    emptyText : '请选择...',
    x_need_submit: true, 
    initComponent: function(){
    	var a = this;
    	var b = 1;
    	if(this.dataSource)
    	{
    		this.x_dataSource = this.dataSource;
    		this.dataSource = undefined;
    		this.initialConfig.dataSource = undefined;
    	}
//    	if(this.name && !this.hiddenName && this.x_need_submit)
//    	{
//    		if(!this.hiddenId)
//    		{
//    			this.hiddenId = Ext.id();
//    		}
//    		this.hiddenName = this.name;
//    		this.name = "";
//    	}
    	this.x_init_store();
        Ext.ux.LinkageComboBox.superclass.initComponent.call(this);
    },
    // private
    onRender: function(ct, position){
        Ext.ux.LinkageComboBox.superclass.onRender.call(this, ct, position);
        if(!this.x_checkParam())
        {
        	return ;
        }
		this.x_initComboStore();
    	if(typeof this.x_next !="undefined")
    	{
    		this.on("select",this.x_linkSelectAction,this)
    	}
    	if(!this.x_need_submit)
    	{
	    	this.el.dom.name = "";
    	}
    },
    
    x_checkParam: function(){
    	var result = true;
    	
    	if((typeof this.x_previous == "undefined" || this.x_previous == "") && (typeof this.x_next == "undefined" || this.x_next == ""))
    	{
    		alert('组件"'+this.id+'"的(上一级：x_previous)和(上一级：x_previous)没有设置，请根据情况进行设置');
    		result = false;
    	}
    	
    	if(typeof this.x_previous != "undefined" && this.x_previous != "")
    	{
    		var Ecmp = Ext.getCmp(this.x_previous);
    		if(typeof Ecmp == "undefined")
    		{
	    		alert('组件"'+this.id+'"的参数(上一级：x_previous)设置有问题，无法找到与之id匹配的组件，请重新设置');
	    		result = false;
    		}
    		else 
    		{
    			if(this.getXType() != Ecmp.getXType())
    			{
	    			alert('组件"'+this.id+'"的(上一级：x_previous)组件类型和本身不一样，请检查');
	    			result = false;
    			}
    		}
    	}
    	
    	if(typeof this.x_next != "undefined" && this.x_next != "")
    	{
    		var Ecmp = Ext.getCmp(this.x_next);
    		if(typeof Ecmp == "undefined")
    		{
	    		alert('组件"'+this.id+'"的参数(下一级：x_next)设置有问题，无法找到与之id匹配的组件，请重新设置');
	    		result = false;
    		}
    		else 
    		{
    			if(this.getXType() != Ecmp.getXType())
    			{
	    			alert('组件"'+this.id+'"的(下一级：x_next)组件类型和本身不一样，请检查');
	    			result = false;
    			}
    		}
    	}
    	
    	return result;
    },
    
    //初始化comboBox的store
    x_init_store: function(){
    	throw("这是个抽象类，请使用它的子类");
    },

	//第一次加载时初始化数据
    x_initComboStore: function(){
    	throw("这是个抽象类，请使用它的子类");
    },
   	
    //当触发select的时候对下级comboBox进行操作
	x_linkSelectAction: function(combo,record,index){
		throw("这是个抽象类，请使用它的子类");
	},
    
    //将制定的comboBox组件设置为不可用状态
    x_setComboBoxDisable: function(){
		this.emptyText = "---------------------------------";
		this.store.loadData([]);
	    this.setValue("");
		this.disable();
    },
    
    //将制定的comboBox组件设置为可用状态
    x_setComboBoxEnable: function(){
		this.emptyText = "请选择..";
	    this.setValue("");
		this.enable();
    },
    
    //设置根据key值设置comboBox的值
    x_setValue: function(sValue){
		var oRecord = this.store.getById(sValue);
		if(typeof oRecord != "undefined")
		{
			this.setValue(sValue);
		}
		else
		{
			this.setValue("");
			//可以设置第一条数据
			//this.setValue(this.store.data.items[0].data.key);
		}
    }
	
});	

Ext.reg('xLinkageComboBox', Ext.ux.LinkageComboBox);

/**
 * x_dataSource 静态数据源
 * x_previous 设置级联comboBox的上一个comboBox的id，如果是第一个级联comboBox，此项不需要设置
 * x_next 设置级联comboBox的下一个comboBox的id，如果是最后一个级联comboBox，此项不需要设置
 */
/**
 * 基于本地数据的级联comboBox
 * @class Ext.ux.LocalLinkageComboBox
 * @extends Ext.ux.LinkageComboBox
 */
Ext.ux.LocalLinkageComboBox = Ext.extend(Ext.ux.LinkageComboBox, {
    initComponent: function(){
        Ext.ux.LocalLinkageComboBox.superclass.initComponent.call(this);
    },
	x_checkParam: function(){
		var result = Ext.ux.AsynLinkageComboBox.superclass.x_checkParam.call(this);
		if((typeof this.x_next != "undefined" && this.x_next != "") && (typeof this.x_previous == "undefined" || this.x_previous == ""))
		{
			if(Ext.isArray(this.x_dataSource[0]))
			{
				if(this.x_dataSource[0].length != 3)
				{
					alert('组件"'+this.id+'"的数据源设置的有问题，请检查');
					return result;
				}
			}
			else
			{
				alert('组件"'+this.id+'"的数据源设置的有问题，请检查');
				return result;
			}
		}
		return result;
	},    
    //初始化comboBox的store
    x_init_store: function(){
	    this.store= new Ext.data.SimpleStore({
	        id: 0,
	        fields: ['key','value','cld'],
	        data: []
	    });
	    this.valueField = 'key';
		this.displayField = 'value';
    },
    
	//第一次加载时初始化数据
    x_initComboStore: function(){
    	var sValue = this.value || "";
    	var selectData = this.x_getDataByPath();
    	if(selectData == "getDataByPath fail")
    	{
			this.x_setComboBoxDisable();
    	}
    	else
    	{
			this.x_setComboBoxEnable();
			this.store.loadData(selectData);
			this.x_setValue(sValue);
	    	return;
    	}
    },
   	
    //当触发select的时候对下级comboBox进行操作
	x_linkSelectAction: function(combo,record,index){
		var value = this.getValue();
		var isFirst = true;
		var next = Ext.getCmp(this.x_next);
		while(next)
		{
			//这里加上record.data.cld 的判断是为了防止当前数据的cld为空而导致不正确的loadData的行为。
			if(isFirst && record.data.cld)
			{
				next.store.loadData(record.data.cld);
				next.x_setComboBoxEnable();
			}
			else
			{
				next.x_setComboBoxDisable();
			}
			isFirst = false;
			next = Ext.getCmp(next.x_next);
		}
	},
	
	//获取本地数据
    x_getDataSource: function(){
    	var x_dataSource = this.x_dataSource;
    	if(typeof x_dataSource == "undefined")
    	{
    		var previous = this.x_previous; 
	    	while(previous)
	    	{
	    		var cmp = Ext.getCmp(previous);
	    		if(cmp.x_dataSource)
	    		{
	    			return cmp.x_dataSource;
	    		}
	    		previous = cmp.x_previous;
	    	}    		
    	}
    	return x_dataSource;
    	
//    	var localData = this.x_localData;
//    	if(typeof localData == "string")
//    	{
//    		localData = window[localData];
//    	}
//    	if(typeof localData == "undefined")
//    	{
//    		alert("没有找到本地名称为("+ localData +")的本地数据");
//    		localData = [];
//    	}
//    	return localData;
    },
    
    //获取级联的数据路径
    x_getLinkagePath: function(){
    	var aPath = [];
    	var previous = this.x_previous;
    	while(previous)
    	{
    		var cmp = Ext.getCmp(previous);
    		if(cmp)
    		{
	    		var s = cmp.getValue();
	    		if(s == "")
	    		{
	    			return "getLinkagePath fail";
	    		}
	    		aPath.unshift(s);
	    		previous = cmp.x_previous;
    		}
    		else
    		{
    			alert("级联选择框未加载完或者级联上下级定义有问题");
    			previous = null;
    		}
    	}
    	return aPath;
    },
    
    //通过路径获取数据
	x_getDataByPath: function(){
		var aPath = this.x_getLinkagePath();
		if(aPath == "getLinkagePath fail")
		{
			return "getDataByPath fail";
		}
		var data = this.x_getDataSource();
		var aPath = aPath || [];
		var hasDate = true;
		for(var i = 0, iLen = aPath.length; i< iLen; i++)
		{
			var s = aPath[i];
			//这个属性主要是为了解决在路径中查找数据时，数据没有查到的情况。
			var hasDate = false;
			for(var j = 0, jLen = data.length; j < jLen; j++)
			{
				if(s == data[j][0])
				{
					data=data[j][2];
					hasDate = true;
					break;
				}
			}
			if(!hasDate)
			{ 
				return "getDataByPath fail"; 
			}			
		}
		return data;
	}

});	

Ext.reg('xLocalLinkageComboBox', Ext.ux.LocalLinkageComboBox);

/**
 * x_asynUrl 设置请求地址
 * x_param 设置请求的参数，对象字符串，url字符串，对象均可以
 * x_previous 设置级联comboBox的上一个comboBox的id，如果是第一个级联comboBox，此项不需要设置
 * x_next 设置级联comboBox的下一个comboBox的id，如果是最后一个级联comboBox，此项不需要设置
 * x_useCache 是否使用缓存数据，使用缓存的话，已经请求过的数据会在下一次请求时使用，不会重新请求服务器。
 */
/**
 * 基于异步数据的级联comboBox
 * @class Ext.ux.AsynLinkageComboBox
 * @extends Ext.ux.LinkageComboBox
 */
Ext.ux.AsynLinkageComboBox = Ext.extend(Ext.ux.LinkageComboBox, {
    x_useCache: true,
    
    initComponent: function(){
	    this.x_cache = {};
//	    this.x_param = {};
        Ext.ux.AsynLinkageComboBox.superclass.initComponent.call(this);
    },
	x_checkParam: function(){
		var result = Ext.ux.AsynLinkageComboBox.superclass.x_checkParam.call(this);
		if(typeof this.x_asynUrl == "undefined" || this.x_asynUrl == "")
		{
			alert('组件"'+this.id+'"的参数(请求地址：x_asynUrl)没有设置，请设置');
			result = false;
		}
		if(typeof this.x_param != "undefined" && typeof this.x_param == "string" && this.x_param != "")
		{
			if(this.x_param.indexOf("=")<0 && !(this.x_param.indexOf("}")>0 && this.x_param.indexOf("{")==0))
			{
				alert('组件"'+this.id+'"的参数(请求参数：x_param)设置有问题('+ this.x_param +')，请重新设置');
				result = false;
			}else if(this.x_param.indexOf("}")>0 && this.x_param.indexOf("{") == 0)
			{
				try{
					Ext.decode(this.x_param);
				}
				catch(e)
				{
					alert('组件"'+this.id+'"的参数(请求参数：x_param)设置有问题('+ this.x_param +')，请重新设置');
					result = false;
				}
			}
		}
		return result;
	},
    //初始化comboBox的store
    x_init_store : function(){
	    this.store= new Ext.data.SimpleStore({
	    	baseParams: this.x_deal_param(),
	        id: 0,
	        fields: ['key','value','cld'],
	        proxy: new Ext.data.HttpProxy({url:this.x_asynUrl})
	    });
    },
    
	//第一次加载时初始化数据
    x_initComboStore: function(){
    	var sValue = this.value || "";
    	var oParam = this.x_getLinkagePathParam();
//    	console.log(oParam);
    	if(oParam == "getLinkagePathParam fail")
    	{
			this.x_setComboBoxDisable();
    		return;
    	}
    	this.store.load({
    		params:oParam,
    		method:"get",
    		callback:function(r,options,success){
    			if(r.length==0)
    			{
					this.x_setComboBoxDisable();
    			}
    			else
    			{
    				if(this.x_useCache)
    				{
    					this.x_set_cache(Ext.encode(oParam),r);
    				}
    				this.x_setComboBoxEnable();
    				this.x_setValue(sValue);
    			}
    		},
    		scope: this
    	});
    },
    
    //当触发select的时候对下级comboBox进行操作
	x_linkSelectAction: function(combo,record,index){
		var oParam = this.x_getLinkagePathParam();
		oParam[this.name || this.hiddenName || this.x_name] = this.getValue();
		var isFirst = true;
		var next = Ext.getCmp(this.x_next);
		while(next)
		{
			if(isFirst )
			{
		    	//参数需要处理，需要加上comboBox定义的基础参数
		    	var data = next.x_get_cache(Ext.encode(Ext.applyIf(oParam,next.store.baseParams)));
		    	if(this.x_useCache && typeof data != "undefined")
		    	{
		    		next.store.loadData([]);
		    		next.store.insert(0,data);
		    		next.x_setComboBoxEnable();
		    	}
		    	else
		    	{
			    	next.store.load({
			    		params:oParam,
			    		method:"get",
			    		callback:function(r,options,success){
//			    			console.log(options);
			    			if(r.length==0)
			    			{
								this.x_setComboBoxDisable();
			    			}
			    			else
			    			{
								if(this.x_useCache)
								{
									//这个oParam被baseParams applyIf了。
									this.x_set_cache(Ext.encode(oParam),r);
								}
								this.x_setComboBoxEnable();
			    			}
			    		},
			    		scope: next
			    	});
		    	}
			}
			else
			{
//				console.log(next.name);				
				next.x_setComboBoxDisable();
			}
			isFirst = false;
			next = Ext.getCmp(next.x_next);
		}
	},
	
	//处理默认参数
    x_deal_param: function(){
    	var sParam = this.x_param;
    	if(typeof sParam == "string")
    	{
//    		根据字符串的格式使用不同的Decode方法
    		if(sParam.indexOf("=")>0)
    		{
    			return Ext.urlDecode(sParam);
    		}
    		else if(sParam.indexOf("}")>0)
    		{
    			try{
    				return Ext.decode(sParam);
    			}catch(e)
    			{
    				return {};
    			}
    		}
    		else
    		{
    			return {};
    		}
    	}
    	if(typeof sParam == "object")
    	{
    		return sParam;
    	}
    	return {};
    },
    
    //获取级联的参数对象
    x_getLinkagePathParam: function(){
    	var oPathParam = {};
    	var previous = this.x_previous;
    	while(previous)
    	{
    		var cmp = Ext.getCmp(previous);
    		if(cmp)
    		{
	    		var sValue = cmp.getValue();
	    		if(sValue == "")
	    		{
	    			return "getLinkagePathParam fail";
	    		}
	    		oPathParam[cmp.name || cmp.hiddenName || cmp.x_name] = sValue;
	    		previous = cmp.x_previous;
    		}
    		else
    		{
    			alert("级联选择框未加载完或者级联上下级定义有问题");
    			previous = null;
    		}
    	}
    	return oPathParam;
    },
    
	//设置缓存
   	x_set_cache: function(key,data){
   		//这里将data数组倒序的原因是因为回头插入的时候如果不倒序，显示的时候会变成倒序的。
   		this.x_cache[this.x_asynUrl + "|" + key] = data.reverse();
   	},
   	
   	//获取缓存的数据
   	x_get_cache: function(key){
   		return this.x_cache[this.x_asynUrl + "|" +key];
   	}

});	

Ext.reg('xAsynLinkageComboBox', Ext.ux.AsynLinkageComboBox);
