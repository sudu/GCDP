 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>页面配置</title>

	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/patch.css" />
    <link rel="stylesheet" type="text/css" href="../../res/css/runTime.css" />
    <link rel="stylesheet" type="text/css" href="../../res/css/Ext.ux.TextAreaPretty.css" />
    <link rel="stylesheet" type="text/css" href="../../res/css/prettify.css" />

 	<script type="text/javascript" src="../../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="../../res/js/ext2/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="../../res/js/ext_base_extension.js?20130528"></script>
    <script type="text/javascript" src="../../res/js/editors/Ext.createRecordsField.js" ></script>
    <script type="text/javascript" src="../../res/js/controls/Ext.ux.TextAreaPretty.js" ></script>
    <script type="text/javascript" src="../../res/js/lib/prettify.js"></script>
    <script type="text/javascript" src="../../res/js/controls/Ext.ux.JSONEditor.js"></script>
    <script type="text/javascript" src="../../res/js/controls/Ext.ux.ScriptEditor.js?20130927"></script>
    <script type="text/javascript" src="../../res/js/lib/editArea/edit_area_loader.js?20130927"></script>
	<style>
		.itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.itemStyle5 {
		    padding:5px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		/**checkboxGroup radiogroup**/
		.inputUnit {
			display: inline-block;
			margin-right: 5px;
		}

		.rightMark{height:20px;background:url(../../res/js/ext2/resources/images/default/tree/drop-yes.gif) no-repeat center;cursor:pointer;}
		.faultMark{height:20px;background:url(../../res/js/ext2/resources/images/default/layout/panel-close.gif) no-repeat center;cursor:pointer;}

	</style>
	<script type="text/javascript">
		var params = Ext.parseQuery();
		var id__ = params['id'];
		if(typeof(id__)=="undefined")id__= 0;
		id__ = parseInt(id__);
		
		var nodeId__ = params['nodeId'];
		var formId__ = params['formId'];
        window.check = function(){
            if(id__ == 0){
                Ext.CMPP.alert("警告","请保存页面信息");
                return false;
            }
            return true;

        }

        window.request = function(url,pos,type){
            console.log(Ext.getCmp(pos));
            if(!check())
                return;
            Ext.Ajax.request({
                url:url,
                method:"POST",
                params:{nodeId:nodeId__,id1:id__,script:Ext.getCmp(pos).getValue(),stype:type},
                "success":function(xhr){
                    Ext.Toast.show(xhr.responseText,{
                        title:'提示',
                        buttons: Ext.Msg.OK,
                        minWidth:420,
                        icon: Ext.MessageBox.INFO
                    });
                }
                ,'failure':function(){
                    Ext.Msg.alert("错误","保存脚本出错!");
                    Ext.Msg.show({
                        title:'错误',
                        msg: '保存脚本出错',
                        buttons: Ext.Msg.OK,
                        animEl: 'elId',
                        minWidth:420,
                        icon: Ext.MessageBox.ERROR
                    });
                }

            });
        }

        window.scriptSaveCallBack = function() {
            request("script!save.jhtml","Editor_1","dynPage");
        };
        window.scriptReleaseCallBack = function () {
            if(!check())
                return;
            Ext.MessageBox.confirm("提示","确定发布吗？",function(button,text){
                if(button == 'yes'){
                    request("script!release.jhtml","Editor_1","dynPage");
                }
            })

        };
        window.templateSaveCallBack = function() {
            request("script!save.jhtml","Editor_2","dynPage_template");
        };
        window.templateReleaseCallBack = function() {
            if(!check())
                return;
            Ext.MessageBox.confirm("提示","确定发布吗？",function(button,text){
                if(button == 'yes'){
                    request("script!release.jhtml","Editor_2","dynPage_template");
                }
            })
        };
	</script>
	<script type="text/javascript">
		function closePage(){
			if(top&& top.centerTabPanel){
				top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
			}else{
				window.close();
			}
		}
	
		var typeComboCfg={
		   id:"comboFieldType",
		   xtype:"combo",
		   triggerAction: 'all',
		   editable:false,
		   width:145,
			mode: 'local',
			store:new Ext.data.SimpleStore({　
			　　fields:['value','text'],　
			　　data:[["string","string"],["number","number"],["boolean","boolean"]]
			}),
			valueField : 'value',//值
			displayField : 'text',//显示文本
			listClass: 'x-combo-list-small',
			value:"string"
		};
		var newComboCfg=Ext.apply({},typeComboCfg);
		delete newComboCfg.id;	
	
		Ext.createRecordsField("listfield_interface",[{
				header: "参数名",
				dataIndex: 'name',
				width: 150,
				required:true
			}/*,{
				header: "参数类型",
				dataIndex: 'type',
				width: 150,
				renderer:function(v,p,record){
					var text = v;
					var combo = Ext.getCmp('comboFieldType');
					if(combo.valueField){
						var r = combo.findRecord(combo.valueField, v);
						if(r){
							text = r.data[combo.displayField];
						}else if(combo.valueNotFoundText !== undefined){
							text = combo.valueNotFoundText;
						}
					}
					return text; //显示显示值
				},
				editor: new Ext.form.ComboBox(typeComboCfg)
				,newField:newComboCfg
			}*/,{
				header: "参数描述",
				width: 200,
				required:false,
				dataIndex: 'desc'
			}
		]); 
	</script>
				
	<script type="text/javascript">
	
		openTab = function(url,title){
			if(!title) title='';
			alert(url);
			if(top&& top.centerTabPanel){
				var _url = url;
				top.centerTabPanel.addIframe('tab_' + (new Date()).valueOf(),title ,_url);
			}else{
				window.open(url);	
			}
		}
		
		
	</script>
	
</head>
<body>
<textarea id="txtScriptTemplate" style="display:none;">
/**
 *    @title XXX
 *    @author XXX
 *    @version
 *    2012-05-18    create    XXX    Intro
 *    2012-05-19    update    XXX    Intro
 */

//#timeout=60#    脚本超时时长，默认60秒，值为-1时不过期
//#plugin=log#    插件注入 ","分隔
//#import=#       公共库导入 ","分隔
//#cache_timeout=60#    缓存过期时间，默认60秒
//#cache_count=1000#        列表缓存数量，默认1000，值为-1时全部缓存

/**
 * 初始化方法
 * @param
 * @return
 * @put
 * @get
 */
function init(){
    
}

init();
</textarea>

<script type="text/javascript">
var params = Ext.parseQuery();
var id__ = params['id'];
var nodeId__ = params['nodeId'];
var data={};
var viewMgr = {
	isEdit:id__ && id__>0,
	debug:Ext.jsDebugger("../",nodeId__,id__,"","dynPage"),
	init:function(){
		if(this.isEdit){
			//请求数据
			Ext.getBody().mask("正在获取数据...");
			Ext.Ajax.request({  
				url:"data!dycPageData.jhtml?nodeId="+ nodeId__+ "&formId=1&id="+id__,
				method:"get",
				success:function(response,opts){
					Ext.getBody().unmask();
					var ret={success:false};
					try{
						ret = Ext.util.JSON.decode(response.responseText);
					}catch(ex){
						console.log(ex);
					}
					viewMgr.initUI(ret);
				},
				failure:function(response,opts){
					Ext.getBody().unmask();
					Ext.Msg.show({
					   title:'错误提示',
					   msg: decodeURIComponent(response.responseText),
					   buttons: Ext.Msg.OK,
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR  
					});
				}
			});
			
		}else{
			this.initUI({});
		}
	},
	initUI:function(data){
		var viewport = new Ext.Viewport({
			layout: 'fit',
			frame:true,
			items:[{
				xtype:"panel",
				id:"centerPanel",
				region:'center',
				layout:'fit',
				items:[{
					xtype:'form',
					labelWidth:80,
					id:'formPanel1',
					labelAlign:'left',
					layout:'xform2',
					itemCls:"itemStyle",
					autoScroll:true,
					items:[{
						fieldLabel :'名称',
						xtype:'textfield',
						name:'name',
						value:data.name,
						allowBlank :false,
						width:500
					},{
						fieldLabel :'说明',
						xtype:'textarea',
						name:'description',
						value:data.description,
						width:500,
						height:80
					},{
						fieldLabel :'Url地址',
						xtype:'textfield',
						name:'url',
						value:data.url,
						emptyText :'',
						width:500
					},{
						fieldLabel :'参数',
						xtype:'listfield_interface',
						name:'params',
						value:data.params,
						width:300
					},{
                            fieldLabel:"脚本",
                            id:"Editor_1",
                            name:"script",
                            xtype:"scripteditor",
                            value:data.script||txtScriptTemplateStr,
                            //width:600,
                            anchor:"95%",
                            height:300,
                            syntax:'js',
                            debuger:Ext.jsDebugger("../",nodeId__,id__,"","dynPage"),
                            debugButtonEnable:true,
                            loadedCallback:'function(){\
						Ext.getCmp("Editor_1").focus();\
					}',
                            save_callback:'window.scriptSaveCallBack',
                            EA_release_callback:'window.scriptReleaseCallBack',
                            toolbar: "save,undo, redo, |, search, go_to_line, |,select_font,|, change_smooth_selection, highlight, reset_highlight, word_wrap,format_code, |, fullscreen,release"
                        },
                        {
                            fieldLabel:"模板",
                            id:"Editor_2",
                            name:"template",
                            xtype:'scripteditor',
                            value:data.template,
                            //width:600,
                            anchor:"95%",
                            height:300,
                            syntax:'html',
                            debugButtonEnable:false,
                            loadedCallback:'function(){\
						Ext.getCmp("Editor_2").focus();\
					}',
                            toolbar: "save,undo, redo, |, search, go_to_line, |,select_font,|, change_smooth_selection, highlight, reset_highlight, word_wrap,format_code, |, fullscreen,release",
                            save_callback:'window.templateReleaseCallBack',
                            EA_release_callback:'window.templateReleaseCallBack'


                        }
					/*{
						fieldLabel:"脚本",
						name:"script",
						xtype:"textarea",
						value:data.script?data.script:txtScriptTemplateStr,
						width:500,
						anchor:"90%",
						height:200,
						fieldNote:'脚本请参考脚本模板编写',
						extra:{
							xtype:"button",
							text:"调试脚本",
							style:"margin-left:1em;display:inline-block;",
							listeners:{
								'click':function(){
									viewMgr.debug(this.field);
								}
							}						
						}
					},{
						fieldLabel:"模板",
						name:"template",
						xtype:"textarea",
						value:data.template,
						width:500,
						anchor:"90%",
						height:200	
					
					}*/],
					buttons:[{
						text:'保存',
						handler:function(){
							var frm = Ext.getCmp('formPanel1').getForm();
							if(!frm.isValid()){
								Ext.Toast.show('输入不合法！',{
									title:'提示',
									time:1000,
									minWidth:420
								});
								return;
							}
							
							Ext.getBody().mask("正在提交中...");
							var formValues = frm.getValues();
							var params = Ext.apply({id:id__,nodeId:nodeId__,formId:formId__},formValues);
							Ext.Ajax.request({  
								url:"data!saveDycPage.jhtml",
								method:"post",
								params:params,
								success:function(response,opts){
									Ext.getBody().unmask();
									var ret={success:false};
									try{
										ret = Ext.util.JSON.decode(response.responseText);
									}catch(ex){
										console.log(ex);
									}
									if(!ret.success){
										Ext.Msg.show({
										   title:'错误提示',
										   msg: ret&& ret.message ? decodeURIComponent(ret.message):response.responseText,
										   buttons: Ext.Msg.OK,
										   minWidth:420,
										   icon: Ext.MessageBox.ERROR 
										});
									}else{
										Ext.Toast.show('保存成功',{
										   title:'提示',
										   buttons: Ext.Msg.OK,
										   minWidth:420,
										   icon: Ext.MessageBox.INFO,
										   callback:function(){
												//跳转到编辑页
												if(id__==0 ||id__=="0" ){
													location.href="view.jhtml?nodeId="+nodeId__+"&formId=1&id=" + ret.id;
												}
										   }
										});
									}
								},
								failure:function(response,opts){
									Ext.getBody().unmask();
									Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(response.responseText),
									   buttons: Ext.Msg.OK,
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR  
									});
								}
							});
						}
					},{
						text:'运行',
						handler:function(){
						
							var frm = Ext.getCmp('formPanel1').getForm();
							if(!frm.isValid()){
								Ext.Toast.show('输入不合法！',{
									title:'提示',
									time:1000,
									minWidth:420
								});
								return;
							}
							
							/* 新窗口显示运行页面  */
							window.open("dynamic!previewDynPage.jhtml?nodeId=" + nodeId__ + "&id=" + id__);
							
						}
					},{
						text:'关闭',
						handler:function(obj,e){
							closePage();
						}
					}]
				}]
			}]
		});
	
	}
}


</script>

<script>
var txtScriptTemplateStr = document.getElementById('txtScriptTemplate').value;
Ext.onReady(function(){
Ext.BLANK_IMAGE_URL = "./../../res/js/ext2/resources/images/default/s.gif";
Ext.QuickTips.init();
Ext.form.Field.prototype.msgTarget = 'qtip';
viewMgr.init();

});

</script>
</body>	
</html>