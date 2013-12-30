Ext.namespace("Ext.ux");
/*
 * x_container_type 容器类型 ("fieldset"字段组 || "panel"面板) 形式：选择
 * x_direction 排版 ("Vertical"纵向 || "Horizontal"横向) 形式：选择
 * x_data_type 数据来源 ("Local"本地 || "Asyn"异步) 形式：选择
 * x_localData 本地数据源 (字符串) 形式：输入框 input
 * x_comboBox_local_config 本地级联配置 (数组型字符串) 形式：输入框 textarea
 * x_useCache 使用缓存数据 ("useCache"使用 || "not useCache"不使用)
 * [{"fieldLabel":"选项一","id":"a1","name":"a1","width":300},{"fieldLabel":"选项二","id":"a2","name":"a2"}]
 * x_comboBox_asyn_config 异地级联配置 (数组型字符串) 形式：输入框 textarea
 * [{"fieldLabel":"选项一","id":"d1","name":"d1","columnWidth":0.33,"x_asynUrl":"../Ext.ux.LinkageComboBox/asyn11.txt?aa=11","x_param":{c:1,b:2}},
 * {"fieldLabel":"选项二","id":"d2","name":"d2","columnWidth":0.33,"x_asynUrl":"../Ext.ux.LinkageComboBox/asyn21.txt?b=3","x_param":"d=1&e=1"}]
 */
/**
 * 一个级联comboBox的容器类
 * @class Ext.ux.LinkageFieldSet
 * @extends Ext.form.FieldSet
 */
Ext.ux.LinkageFieldSet = Ext.extend(Ext.form.FieldSet, {
	autoHeight: true,
    initComponent: function(){
    	this.comboBoxGroup = [];
    	this.init_option();
        Ext.ux.LinkageFieldSet.superclass.initComponent.call(this);
    },
    
    //初始化组件配置
    init_option: function(){
    	
    	var aComboBox = this.init_comboBox_config();

    	this.items = aComboBox;
   	
		if(this.x_direction == "Horizontal")
		{
			this.layout = "column";
		}
	
		
		if(this.x_container_type == "panel")
		{
			this.baseCls = "x-fieldset-noborder";
			this.title = "";
		}
		
    },
    
    //初始化级联comboBox的配置
	init_comboBox_config: function(){
		
		var sComboBoxData = this.select_comboBox_config(this.x_data_type);
		
		var aComboBox = this.trans_to_array(sComboBoxData);		
		
		if(aComboBox.length<2)
		{
			alert("级联comboBox不能少于两个，请重新设置参数");
			return aComboBox;
		}
		this.init_aComboBox_id(aComboBox);
		for(var i =0, iLen = aComboBox.length; i < iLen; i++)
		{
			var oComboBox = aComboBox[i];
			
			this.set_comboBox_xtype(oComboBox);
			
			this.set_comboBox_x_useCache(oComboBox);
			
			this.set_comboBox_x_dataSource(oComboBox);
			
			this.set_comboBox_x_asyn_url(oComboBox);
			
			this.init_comboBox_value(oComboBox,i);
			
			this.set_comboBox_next_previous(oComboBox,aComboBox,i,iLen);
			
			oComboBox.x_need_submit = false;
			
			this.add_to_comboBoxGroup(oComboBox);
			
		}
		
		this.set_comboBox_column(aComboBox);
		
		return aComboBox;
	},
	
	init_aComboBox_id: function(aComboBox){
		for(var i =0, iLen = aComboBox.length; i< iLen; i++)
		{
			var oComboBox = aComboBox[i];
			if(typeof oComboBox.id == "undefined")
			{
				oComboBox.id = Ext.id();
			}
		}
	},
	
	select_comboBox_config: function(x_data_type){

		return this["x_comboBox_" + x_data_type + "_config"];
		
	},
	
	trans_to_array: function(sString){
		
		return Ext.isArray(sString) ? sString : Ext.decode(sString);
		
	},
	
	set_comboBox_xtype: function(oComboBox)
	{
		
		oComboBox.xtype = "x"+ this.x_data_type +"LinkageComboBox";
		
	},
	
	set_comboBox_x_useCache: function(oComboBox){
		
		if(typeof this.x_useCache != "undefined" && this.x_useCache == "not useCache")
		{
			oComboBox.x_useCache = false;
		}
		else
		{
			oComboBox.x_useCache = true;
		}
		
	},
	
	set_comboBox_x_dataSource: function(oComboBox){
		
		if(this.x_data_type == "Local")
		{
			oComboBox.x_dataSource = this.dataSource;
		}
		
	},
	
	set_comboBox_x_asyn_url: function(oComboBox){
		if(this.x_data_type == "Asyn")
		{
			oComboBox.x_asynUrl = oComboBox.x_asynUrl || this.x_asynUrl;
		}
		
	},
	
	init_comboBox_value: function(oComboBox,i){
		if(!Ext.isArray(this.x_values) && typeof this.x_values != "undefined")
		{
			this.x_values = Ext.decode(this.x_values);
		}
		if(Ext.isArray(this.x_values) && typeof this.x_values[i] != "undefined")
		{
			oComboBox.value = this.x_values[i];
		}
		
	},
	
	set_comboBox_next_previous: function(oComboBox,aComboBox,i,iLen){
		
		switch(i)
		{
			case 0 :
				oComboBox.x_next = aComboBox[i+1].id;
			break;
			
			case iLen-1:
				oComboBox.x_previous = aComboBox[i-1].id;
			break;
			
			default:
				oComboBox.x_next = aComboBox[i+1].id;
				oComboBox.x_previous = aComboBox[i-1].id;
			break;
		}
		
	},
	
	set_comboBox_column: function(aComboBox){
		
		if(this.x_direction == "Horizontal")
		{
			Ext.each(aComboBox,function(v,i,all){
				aComboBox[i] = {columnWidth:v.columnWidth/1,border: false,layout: 'form',items: [v]};
			});
		}
		
	},

	add_to_comboBoxGroup: function(oComboBox){
		
		this.comboBoxGroup.push(oComboBox);
		
	},
	
	get_comboBox_value: function(){
		
		var result = [];
		
		for(var i =0,iLen = this.comboBoxGroup.length; i < iLen; i++)
		{
			var oComboBoxId = this.comboBoxGroup[i].id;
			var oComboBox = Ext.getCmp(oComboBoxId);
			result.push(oComboBox.getValue());
		}
		
		return Ext.encode(result);
	},
	
	set_comboBox_value: function(sValue){
		
		if(sValue)
		{
			var aValue = Ext.decode(sValue);
		}
		else
		{
			var aValue = [];
		}
		
		for(var i=0, iLen = this.comboBoxGroup.length; i < iLen; i++ )
		{
			var oComboBoxId = this.comboBoxGroup[i].id;
			var oComboBox = Ext.getCmp(oComboBoxId);
			oComboBox.x_setComboBoxDisable();
			if(typeof aValue[i] != "undefined")
			{
				oComboBox.setValue(aValue[i]);
			}
			oComboBox.x_initComboStore();
		}
		
	}
	
});

Ext.reg('xLinkageFieldSet', Ext.ux.LinkageFieldSet);