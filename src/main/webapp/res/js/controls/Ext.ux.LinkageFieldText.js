Ext.namespace("Ext.ux");
/**
 * 
 * @class Ext.ux.LinkageTextField
 * @extends Ext.form.TextField
 */
Ext.ux.LinkageTextField = Ext.extend(Ext.form.TextField, {
	
	//是否需要选择所有的级联comboBox，默认为不用全选
	x_SelectAll: false,
	
	inputType:"hidden",
    
    initComponent: function(){
        Ext.ux.LinkageTextField.superclass.initComponent.call(this);
    },
    
    onRender: function(ct, position){
        var comboBox_config = this.init_linkageFieldset_param();
        var isPassCheckParam = this.checkParam(comboBox_config);
        if(isPassCheckParam)
        {
        	this.linkageFieldSet = new Ext.ux.LinkageFieldSet(comboBox_config);
        }
        
        Ext.ux.LinkageTextField.superclass.onRender.call(this, ct, position);
        
        if(isPassCheckParam)
        {
	        this.comboBox_container = ct.createChild({tag:"div"},this.el);
	        this.linkageFieldSet.render(this.comboBox_container);
	        this.init_comboBox_event();
        }
        
    },
    
    checkParam: function(oParams){
    	
    	var result = true;
    	
    	switch(oParams.x_data_type)
    	{
    		case "Local":
    			var sConfig = oParams.x_comboBox_Local_config;
    			var dataSource = oParams.dataSource;
				result = this._check_config(sConfig,"x_comboBox_Local_config");
				if(typeof dataSource !="undefined" && Ext.isArray(dataSource[0]))
				{
					if(dataSource[0].length != 3)
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
    		break;
    		
    		case "Asyn":
    			var sConfig = oParams.x_comboBox_Asyn_config;
    			result = this._check_config(sConfig,"x_comboBox_Asyn_config");
    		break;
    	}
    	
    	if(!result)
    	{
    		this.allowBlank = true;
    		this.x_SelectAll = false;
    	}
    	return result;
    	
    },
    
    _check_config: function(sConfig,stype){
    	var result = true;
		if(typeof sConfig == "undefined" || sConfig == "")
		{
			alert('组件"'+this.id+'"的参数(级联配置：'+ stype +')没有设置，请进行设置');
			result = false;
		}
		else
		{
			try{
				var a = Ext.decode(sConfig);
				if(!Ext.isArray(a))
				{
					alert('组件"'+this.id+'"的参数(级联配置：'+ stype +') '+ sConfig +' 设置有问题，不能转换成数组，请进行相应的修改');
					result = false;
				}
				else if(a.length<2)
				{
					alert('组件"'+this.id+'"的参数(级联配置：'+ stype +') '+ sConfig +' 设置有问题，转换成数组后长度小于2，请进行相应的修改');
					result = false;
				}
			}catch(e){
				alert('组件"'+this.id+'"的参数(级联配置：'+ stype +') '+ sConfig +' 设置有问题，不能转换成数组，请进行相应的修改');
				result = false;
			}
		}    	
    	return result;
    },
    //初始化comboBox的事件
    init_comboBox_event: function(){
		
		for(var i = 0 , iLen = this.linkageFieldSet.comboBoxGroup.length; i < iLen; i++)
		{
			var sComboBoxId = this.linkageFieldSet.comboBoxGroup[i].id;
			var oComboBox = Ext.getCmp(sComboBoxId);
			oComboBox.on("select",function(combo,record,index ){
				//这里是取用父类的setValue的方法，避免执行子类setValue中增加的方法。
				Ext.ux.LinkageTextField.superclass.setValue.call(this,this.linkageFieldSet.get_comboBox_value());
//				console.log(this.linkageFieldSet.get_comboBox_value());
			},this);
		}
		
    },
    
    //linkageFieldSet需要使用的参数对照
    linkageFieldSet_param_map: {
    	x_fieldset_id: "id",
    	x_fieldset_title: "title",
//    	x_fieldset_labelWidth: "labelWidth",
    	x_direction: "x_direction",
    	x_data_type: "x_data_type",
//    	x_localData: "x_localData",
    	dataSource: "dataSource",
    	x_asynUrl: "x_asynUrl",
    	x_container_type:"x_container_type",
    	defaults:"defaults",
//    	value: "x_values",
    	x_comboBox_Local_config: "x_comboBox_Local_config",
    	x_comboBox_Asyn_config: "x_comboBox_Asyn_config"
    },
    
    //依照linkageFieldSet_param_map初始化linkageFieldset的参数
    init_linkageFieldset_param: function(){
    	
    	var global_config = this.initialConfig;
    	var comboBox_config = {};
    	
    	for(var i in this.linkageFieldSet_param_map)
    	{
    		if(typeof global_config[i] != "undefined")
    		{
    			comboBox_config[this.linkageFieldSet_param_map[i]] = global_config[i];
    		}
    	}
    	
    	//特殊处理：labelWidth参数
    	var oOwnerCt = this.ownerCt;
    	//寻找到离自己最近的有labelWidth的组件，将其值设置进来。
    	while(oOwnerCt)
    	{
    		if(typeof oOwnerCt.labelWidth != "undefined")
    		{
		    	comboBox_config.labelWidth = oOwnerCt.labelWidth;
		    	comboBox_config.defaults = {labelWidth:oOwnerCt.labelWidth};
		    	break;
    		}
    		oOwnerCt = oOwnerCt.ownerCt
    	}
    	
    	return comboBox_config;
    },
    
    //赋值并对内部的linkageFieldSet赋值。
    setValue: function(v){
    	
    	Ext.ux.LinkageTextField.superclass.setValue.call(this,v);
    	try{
    		this.linkageFieldSet.set_comboBox_value(v);
    	}catch(e){
    		
    	}
   		
   		
	},
	
	//重写了校验方法，一个为空判断，一个是全选判断
    validateValue : function(value){
    	
        if(value.length < 1 || value === this.emptyText)
        { 
         	 try{
	         	 var sfirstComboBoxId = this.linkageFieldSet.comboBoxGroup[0].id;
	         	 var oFirstComboBox = Ext.getCmp(sfirstComboBoxId);
	             if(this.allowBlank)
	             {
	                 oFirstComboBox.clearInvalid();
	             }
	             else
	             {
	                 oFirstComboBox.markInvalid(this.blankText);
	                 return false;
	             }
         	 }catch(e){
				console.log("没有生成级联组件")         	 	
         	 }
        }
        
        if(this.x_SelectAll)
        {
        	var isSelectAll = true;
        	var aComboBoxGroup = this.linkageFieldSet.comboBoxGroup;
        	for(var i=0,iLen = aComboBoxGroup.length; i < iLen; i++)
        	{
        		var oComboBox = Ext.getCmp(aComboBoxGroup[i].id);
        		if(oComboBox.getValue() == "")
        		{
        			isSelectAll = false;
        			oComboBox.markInvalid(this.blankText)
        		}
        		else
        		{
        			oComboBox.clearInvalid();
        		}
        	}
        	return isSelectAll;
        }
        return true;
    }
    
});


Ext.reg('xLinkageTextField', Ext.ux.LinkageTextField);
