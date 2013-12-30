
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单字段描述</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<style>
		.addField{background:url("./../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("./../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.saveField{background:url("./../res/js/ext2/resources/images/default/my/save.gif") left  no-repeat !important;}
		.deleteButton{background:url("../res/img/runTime/delete1.gif") left  no-repeat !important;width:16px;height:16px;cursor:pointer;}
	</style>
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script> 
	<script>
	var formId__ = #{formId!0};
	var nodeId__ = #{nodeId!0};
	var formConfig__= ${formConfig};
	var formName__ = "${formName}";

	
	function parseConfig(){
		var dataArr=formConfig__.fieldsConfig.fieldsConfig;
		var newDataArr=[];
		var fieldsArr = formConfig__.fieldsConfig.fields;
		var newFieldsArr = fieldsArr.join(',').split(',');
		for(var i=0;i<dataArr.length;i++){
			var saveType = parseInt(dataArr[i].f_saveType );
			if(saveType>1){
				dataArr[i].f_saveType = saveType;
				dataArr[i].indexType = parseInt(dataArr[i].indexType );
				var f_name = dataArr[i].f_name;
				var pos = newFieldsArr.indexOf(f_name);
				if(pos!=-1 && pos%2==0){
					dataArr[i].f_title = newFieldsArr[pos+1];
				}
				newDataArr.push(dataArr[i]);
			}
		}
		return newDataArr;
	}
	var data__ = {data:parseConfig()};
	
	var COMMONDATA = {
		saveType:[[1,'nosave'],[2,'db'],[3,'nosql']],
		indexType:[[0,'不检索'],[1,'索引'],[2,'索引+排序'],[3,'索引+分词'],[4,'全文索引'],[5,'特殊符号分词'],[6,'仅存储']],
		fieldType:[['INT','INT'],['FLOAT','FLOAT'],['DOUBLE','DOUBLE'],['CHAR','CHAR'],['VARCHAR','VARCHAR'],['TEXT','TEXT'],['mediumtext','mediumtext'],['DATETIME','DATETIME']]
	};
	function getTypeText(type,value){
		for(var i=0;i<COMMONDATA[type].length;i++){
			if(value==COMMONDATA[type][i][0]){
				return COMMONDATA[type][i][1];
			}
		}
		return "";
	}
	function getTypeValue(type,text){
		for(var i=0;i<COMMONDATA[type].length;i++){
			if(text==COMMONDATA[type][i][1]){
				return COMMONDATA[type][i][0];
			}
		}
		return 0;
	}	
		
	function storeFilter(key,value,button){
		store.filter(key,value,true,false);
		var fields = store.collect("f_name");
		window.txtStatus.setValue(fields.join(","));
		window.pressedButton && window.pressedButton != button && window.pressedButton.toggle(false);
		window.pressedButton = button;
		if(button) button.toggle(true);
	}
	</script>
</head>
<body>	

<div id="placeholder"></div>

<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
    Ext.QuickTips.init();

    var cm = new Ext.grid.ColumnModel([{
	   id:'f_name',
	   header: "字段名",
	   dataIndex: 'f_name',
	   width: 150,
	   sortable: true
	},{
	   id:'f_title',
	   header: "字段描述",
	   dataIndex: 'f_title',
	   width: 150
	},{
	   header: "字段类型",
	   dataIndex: 'f_type',
	   width: 70,
	   sortable: true
	},{
	   header: "字段长度",
	   dataIndex: 'f_length',
	   width: 60,
	   align: 'right'
	},{
		header: "存储类型",
		dataIndex: 'f_saveType',
		width:60,
		align:"center",
		sortable: true,
		renderer:function(v,p,record){
			return getTypeText("saveType",v);
		}
	}/*,{
		header: "允许搜索(db)?",
		dataIndex: 'l_allowSearch',
		width: 95,
		align:"center",
		sortable: true,
		renderer:function(v,p,record){
			return v?"是":"";
		}
	},{
		header: "允许排序(db)?",
		dataIndex: 'l_allowSort',
		width: 95,
		align:"center",
		renderer:function(v,p,record){
			return v?"是":"";
		}
	}*/,{
		header: "索引类型",
		dataIndex: 'indexType',
		width: 100,
		align:"left",
		sortable: true,
		renderer:function(v,p,record){
			var text = getTypeText("indexType",v);
			
			return text;  //显示显示值
		}
	}]);
    cm.allowSort = true;

    // create the Data Store
    window.store = new Ext.data.Store({
        proxy: new Ext.data.MemoryProxy(data__), 
        reader : new Ext.data.JsonReader({
			autoLoad:true,
			root : "data",
			fields: ["f_name","f_title","f_type","f_length","f_saveType","indexType"]
		}),

        //sortInfo:{field:'f_name', direction:'ASC'}
    });

    // create the editor grid
    var grid = new Ext.grid.EditorGridPanel({
        store: store,
		frame:false,
		hideBorders:true,
		enableColumnHide:false,
		stripeRows :true,//表格行颜色间隔显示
        cm: cm,
		sm: new Ext.grid.CheckboxSelectionModel(),
		//autoHeight:true,
        //autoExpandColumn:'f_title',
		autoScroll:true,
        title:"表单名称:" + formName__ + "  formId:" + formId__,
        frame:true,
        tbar: [{
			xtype:'label',
			style:"font-weight:bold;",
			text:"从数据库搜索："
		},{
			text:"存储在db的字段",
			enableToggle:true,
			handler:function(){
				storeFilter("f_saveType",2,this);
			}
		},{
			xtype:'tbseparator',
		},{
			xtype:'label',
			style:"font-weight:bold;",
			text:"从搜索引擎搜索："
		},{
			text:"可查询到的字段",
			enableToggle:true,
			handler:function(){
				storeFilter("indexType",new RegExp('1|2|3|4|5|6'),this);
			}
		},{
			text:"可索引",//[[0,'不检索'],[1,'索引'],[2,'索引+排序'],[3,'索引+分词'],[4,'全文索引'],[5,'特殊符号分词'],[6,'仅存储']],
			enableToggle:true,
			handler:function(){
				storeFilter("indexType",new RegExp('1|2|3'),this);
			}
		},{
			text:"可分词",
			enableToggle:true,
			handler:function(){
				storeFilter("indexType",new RegExp('3|5'),this);
			}
		},{
			text:"特殊符号分词",
			enableToggle:true,
			handler:function(){
				storeFilter("indexType",new RegExp('5'),this);
			}
		},{
			text:"可排序",
			enableToggle:true,
			handler:function(){
				storeFilter("indexType",new RegExp('2'),this);
			}
		},{
			text:"显示全部",
			handler:function(){
				storeFilter();
			}
		}],
		bbar: [
			(function(){
				window.txtStatus = new Ext.form.TextField({
					xtype:"textfield",
					id:"txtStatus",
					style:'border:1px inset #A9BFD3;background:transparent;',
					readOnly:true,
					width:500
				});
				return window.txtStatus;
			})()
		]
    });

	var viewport = new Ext.Viewport({
		layout: 'fit',
		items:[grid]
	});	
	
    // trigger the data store load
    store.load({
		callback:function(){
			storeFilter();
		}
	});
	 
});

</script>
</body>	
</head>
</html>