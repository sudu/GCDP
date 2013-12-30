<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>脚本编辑器</title>
<script type="text/javascript" src="../res/js/lib/editArea/edit_area_loader.js?20130927"></script>
<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../res/js/ext2/resources/css/patch.css" />
<link rel="stylesheet" type="text/css" href="../res/css/designTime.css" />	
<link rel="stylesheet" type="text/css" href="../res/css/runTime.css" />	
<style>
	.x-column-layout-ct *{height:100%;}
	.mergely-title {width:50%;text-align:center}
    .no-node-icon{background-image: none;display: none}
</style>
<script type="text/javascript" src="../res/js/ext2/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../res/js/ext2/ext-all-debug.js"></script>
<script type="text/javascript" src="../res/js/ext_base_extension.js?20130528"></script>
<script type="text/javascript" src="../res/js/controls/listField.js" ></script>
<script type="text/javascript">
	var DEBUGKEY = "${key?default('')}";
	var globalvar__ = {
		params:Ext.parseQuery()
	};
</script>
</head>
<body>
<textarea id="txtTplScript" style="display:none;">
//#timeout=60#    脚本超时时长，默认60秒，值为-1时不过期
//#plugin=log,util#    插件注入 ","分隔
//#import=#       公共库导入 ","分隔
//@author ${userName!"XXX"} @ ${today!""}

var mailList = "${email!""}";

function start(){
	try{
		//todo
	}catch(e){
		var err = e.toString() + "(#" + e.lineNumber + ")";
		log.error(err);
		/*
		var _etitle = "脚本异常:" + e.name + "@nodeId[${nodeId!0}]|formId[${formId!"0"}]";
		var _coder = pluginFactory.getP("coder");
		var _econtent = _coder.urlEncode("script:${cmppUrl!"null"}\r\n" + err, "UTF-8");
		util.sendMail(mailList, _etitle, _econtent,"");
		*/
	}
}

//start();
</textarea>

<script>

var Editor={
		id:"jsEditor"+new Date().valueOf()
		,window:null
		,workspace:null
		//,east:null
		,south:null
		,north:null
		,helpPanel:null
		,params:null
		,getParams:function(data){
			data=data||{};
			return Ext.apply(this.params,data);	
		}
		,initScript:null
		,activateSouthTab:function(panel){
			if(this.south.curToolBox)
				this.south.curToolBox.hide();
			this.south.curToolBox=panel.toolBox;
			panel.toolBox.show();
			this.south.setActiveTab(panel);
		}
		,loadFile:function(){
			var t=this;
			if(t.initScript!=undefined)
				return t.setScript(t.initScript);			
			if(t.params){
				Ext.getBody().mask("正在获取脚本...");
				Ext.Ajax.request({
					"url":"script!open.jhtml",
					"method":"GET",
					"params":t.params,
					"success":function(xhr){
						Ext.getBody().unmask();
						var content = xhr.responseText;
						t.setScript(content);			
					}
					,'failure':function(){
						Ext.getBody().unmask();
						Ext.Msg.alert("错误","下载脚本出错!");
					}
				});	
			}
		}
		,setScript:function(script){
			editAreaLoader.setValue(this.id, script||scriptTplContent);						
		}
		,getScript:function(){
			return editAreaLoader.getValue(this.id);
		}
		,saveFile:function(){
			var t=this;
			if(t.params){
				var val=editAreaLoader.getValue(t.id);
				t.params.script=val;
				Ext.Ajax.request({
					"url":"script!save.jhtml",
					"method":"POST",
					"params":t.params,
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
		},releaseFile:function(){
             var t = this;
            Ext.MessageBox.confirm("提示","确定要发布吗？",function(button,text){
                if(button=="yes"){
                    if(t.params){
                        var val = editAreaLoader.getValue(t.id);
                        t.params.script=val;
                        Ext.Ajax.request({
                            "url":"script!release.jhtml",
                            "method":"POST",
                            "params":t.params,
                            "success":function(xhr){
                                Ext.Toast.show(xhr.responseText,{
                                    title:'提示',
                                    buttons: Ext.Msg.OK,
                                    minWidth:420,
                                    icon: Ext.MessageBox.INFO
                                });
                            }
                            ,'failure':function(){
                                Ext.Msg.alert("错误","发布脚本出错!");
                                Ext.Msg.show({
                                    title:'错误',
                                    msg: '发布脚本出错',
                                    buttons: Ext.Msg.OK,
                                    animEl: 'elId',
                                    minWidth:420,
                                    icon: Ext.MessageBox.ERROR
                                });
                            }
                        });
                    }
                }
            })

        }
		,saveFileHandler:function(){
			Editor.saveFile();
		},releaseHandler:function(){
            Editor.releaseFile();
        }
		,loaded:function(){
			this.debug.context=window.frames["frame_"+this.id];
			this.loadFile();			
		}
		,init:function(newFile){
			//this.newFile=newFile;
			
			var mnr=Ext.ComponentMgr;
			//this.east=mnr.create(this.ui.east);
			this.helpPanel=mnr.create(this.ui.helpPanel);
			//this.east.add(this.helpPanel);
			this.south=mnr.create(this.ui.south);	
			//this.north=mnr.create(this.ui.north);	
			this.workspace=mnr.create(this.ui.workspace);		
			this.window= new Ext.Viewport({
	            layout: 'border'
	            ,items: [this.helpPanel, this.south, this.workspace]
	        });
	        this.window.getLayout().onResize();
	        editAreaLoader.init(Editor.ui.editor); 
	        
			var param=Ext.parseQuery();
			this.params=Ext.encode(param)=="{}"?null:param;
	        
	        Editor.debug.init();
			
	        this.help.init();
		}
};

Editor.debug={
		context:null
		,panel:null
		,btnRun:null
		,btnDebug:null
		,btnOver:null
		,btnInto:null
		,btnOut:null
		,btnGo:null
		,btnBreak:null
		,timeoutBox:null
		,console:null
		,bp_map : {}
		,result_map:{}
		,last_lineno: 0
		,init:function(){
			var t=this;
			var p=this.panel=new Ext.Panel({ title: "调试器"});
			Editor.south.add(p);
			Ext.fly(Editor.south.getTabEl(p)).on("click",function(){
				t.console=p.body;
			});
			
			var p2=new Ext.Panel({ title: "运行日志"});
			Editor.south.add(p2);
			Editor.south.doLayout();
			var hdr=Ext.fly(Editor.south.getTabEl(p2));
			hdr.dom.setAttribute("title","点击刷新");
			var setStl=function(){
				p2.body.setStyle("overflow","auto");
				setStl=null;
			}
			hdr.on("click",function(){
				(setStl&&setStl());
				t.console=p2.body;
				t.request("../runtime/getScriptLog.jhtml",null,function(msg){
					//p2.body.update(Ext.util.Format.htmlEncode(msg));
					p2.body.update(msg);
					p2.body.dom.scrollTop = p2.body.dom.scrollHeight;//滚动到底部
				});
			});
			
			
			
				
			var box=p.toolBox=Editor.south.header.createChild({cls:"southToolBtnBox"});
			box.enableDisplayMode();
			
			var span=this.btnRun=box.createChild({tag:"span",html:"Run"});
			span.on("click",this.run,this);
			//span=this.btnDebug=box.createChild({tag:"span",html:"Debug"});
			//span.on("click",this.debug,this);
			//span=this.btnOver=box.createChild({tag:"span",html:"Over"});
			//span.on("click",this.over,this);
			//span=this.btnInto=box.createChild({tag:"span",html:"Into"});
			//span.on("click",this.into,this);
			//span=this.btnOut=box.createChild({tag:"span",html:"Out"});
			//span.on("click",this.out,this);
			//span=this.btnGo=box.createChild({tag:"span",html:"Go"});
			//span.on("click",this.go,this);
			//span=this.btnBreak=box.createChild({tag:"span",html:"Break"});
			//span.on("click",this["break"],this);
			box.createChild({tag:"span",id:"timeoutLabel",html:"Timeout"});			
			span=box.createChild({tag:"span",id:"timeoutBoxCtnr"});
			var ipt=this.timeoutBox=span.createChild({tag:"input",id:"timeoutBox"});		
			new Ext.form.TextField({applyTo:ipt,vtype:"num"});
			span=box.createChild({tag:"span",cls:"x-tool x-tool-clear",title:"清空窗口",html:" "});	
			span.on("click",this.clearConsole,this);

			Editor.activateSouthTab(p);
			this.console=p.body;
			p.body.setStyle("overflow","auto");
		}
		,log:function(msg){
			this.console.createChild({html:msg});
			var d=this.console.dom;
			d.scrollTop=d.scrollHeight-d.offsetHeight;
		}
		,clearConsole:function(msg){
			this.console.update("");
		}		
		,toggleBtns:function(btnList,enable){
			return;//roney:other btns removed now;
			if(Object.prototype.toString.call(btnList)!="[object Array]")
				btnList=[btnList];
			var len=btnList.length;
			while(len--){
				btnList[len].removeClass('disabled');
			}
		}
		,disableBtns:function(btnList){
			this.toggleBtns(btnList,false);
		}
		,enableBtns:function(btnList){
			this.toggleBtns(btnList,true);
		}		
		,request:function(url,data,success){
			var t=this;
			Ext.Ajax.request({
				"url":url ,
				"method":"POST",
				"params":Editor.getParams(data),
				"success":function(xhr){
					success.call(t,xhr.responseText);
				}
			});			
		}
		,run:function(){
			var script = editAreaLoader.getValue(Editor.id);
			var timeout = this.timeoutBox.dom.value;
			req_param ={"script":script,"timeout":timeout,"variables":Ext.getCmp("injectedVariables").getValue()};
			//this.disableBtns([this.btnOver,this.btnInto,this.btnOut,this.btnGo,this.btnBreak]);

			this.log("Running ...<br />");
			this.request("script!eval.jhtml",req_param,function(resTxt){
				//this.log(Ext.util.Format.htmlEncode(resTxt)+"<br /><hr />");//
				this.log(resTxt+"<br /><hr />");
			});

		}		
		/*
		,debug:function(){
			var script = editAreaLoader.getValue(Editor.id);
			req_param ={"script":script,"key":DEBUGKEY};
			this.disableBtns([this.btnOver,this.btnInto,this.btnOut,this.btnGo,this.btnBreak]);

			this.log("Debugging ...<br />");
			this.request("scriptdebug!debug.jhtml",req_param,function(resTxt){
				eval("this.result_map="+resTxt);
				this.removeCurrentLine(this.last_lineno);
				this.setCurrentLine(this.result_map["lineno"])
				this.last_lineno = this.result_map["lineno"];
			});
		}
		,over:function(){
			req_param ={"key":DEBUGKEY};
			this.request("scriptdebug!stepOver.jhtml",req_param,function(resTxt){
				eval("this.result_map="+resTxt);
				this.removeCurrentLine(this.last_lineno);
				this.setCurrentLine(this.result_map["lineno"])
				this.last_lineno = this.result_map["lineno"];
			});
		}
		,into:function(){
			req_param ={"key":DEBUGKEY};
			this.request("scriptdebug!stepInto.jhtml",req_param,function(resTxt){
				eval("this.result_map="+resTxt);
				this.removeCurrentLine(this.last_lineno);
				this.setCurrentLine(this.result_map["lineno"])
				this.last_lineno = this.result_map["lineno"];
			});
		}
		,out:function(){
			req_param ={"key":DEBUGKEY};
			this.request("scriptdebug!stepOut.jhtml",req_param,function(resTxt){
				eval("this.result_map="+resTxt);
				this.removeCurrentLine(this.last_lineno);
				this.setCurrentLine(this.result_map["lineno"])
				this.last_lineno = this.result_map["lineno"];
			});
		}
		,go:function(){
			req_param ={"key":DEBUGKEY};
			this.removeCurrentLine(this.last_lineno);
			this.request("scriptdebug!go.jhtml",req_param,function(resTxt){
				eval("this.result_map="+resTxt);
				this.removeCurrentLine(this.last_lineno);
				this.setCurrentLine(this.result_map["lineno"])
				this.last_lineno = this.result_map["lineno"];
			});
		}
		,"break":function(){
			req_param ={"key":DEBUGKEY};
			this.request("scriptdebug!breakk.jhtml",req_param,function(resTxt){
				eval("this.result_map="+resTxt);
				this.removeCurrentLine(this.last_lineno);
				this.setCurrentLine(this.result_map["lineno"])
				this.last_lineno = this.result_map["lineno"];
			});
		}
		*/
		,lineno_div_click:function(lineno){
			var url;
			var req_param = {"lineno":lineno,"key":DEBUGKEY};
			if(typeof( this.bp_map[lineno] ) == 'undefined'){
				url = "scriptdebug!setBreakPoint.jhtml";
			}else if(this.bp_map[lineno] == true){
				url = "scriptdebug!disableBreakPoint.jhtml";
			}else if(this.bp_map[lineno] == false){
				url = "scriptdebug!removeBreakPoint.jhtml";
			}
			this.request(url,req_param,function(msg){
				this.removeBreakPoint(lineno);
				eval("this.bp_map="+msg);				
				this.displayBreakPoint();
			});
		}		
		,displayBreakPoint:function(){
			for(var key in this.bp_map){
				var flag = this.bp_map[key];
				if(flag){
					this.addBreakPoint(key);
				}else{
					this.disableBreakPoint(key);
				}
			}
		}	
		,getLine:function(lineno){
			var dom=this.context._$('line_'+lineno);
			return dom||{};
		}
		,removeBreakPoint :function(lineno){
			this.getLine(lineno).style.backgroundColor='';
		}
		
		,disableBreakPoint :function(lineno){
			this.getLine(lineno).style.backgroundColor='#cccccc';
		}
		
		,addBreakPoint :function(lineno){
			this.getLine(lineno).style.backgroundColor='#888888';
		}
		
		,setCurrentLine :function(lineno){
			this.getLine(lineno).innerHTML='<span class="debugArrow"/> '+lineno;
		}
		
		,removeCurrentLine :function(lineno){
			this.getLine(lineno).innerHTML=lineno;
		}
}

Editor.help={
		applyDireTpl: (function () {
            var tpl = new Ext.Template("<li class='helpItem' key='{1}' title={0}><a href='javascript:void(0)'>{0}</a></li>");
            tpl.compile();
            return tpl.compiled;
        })()
		,taNow:"taCurrentScript"
        ,taHis:"taHistoryScript"
        ,init:function(){
        	this.scriptWindow.render(Ext.getBody());
        	this.taNow=Ext.get(this.taNow);
        	this.taHis=Ext.get(this.taHis);
			if(this.taNow==null || this.taHis==null) return;
        	var nowDom=this.taNow.dom;
        	var hisDom=this.taHis.dom;
        	var t=this;
        	this.taNow.on("keydown",function(){
        		window.setTimeout(function(){
        			t.synHeight(nowDom,hisDom);
        		},10);
        		return true;
        	});
        	this.taHis.on("keydown",function(){
        		window.setTimeout(function(){
        			t.synHeight(hisDom,nowDom);
        		},10);
        		return true;
        	});
          	this.taNow.on("scroll",function(){
        		hisDom.scrollTop=nowDom.scrollTop;
        	});
          	this.taHis.on("scroll",function(){
        		nowDom.scrollTop=hisDom.scrollTop;
        	});
        }
		,synHeight:function(cur,tar){
			(function(){
				if(cur.scrollHeight>tar.scrollHeight){
					tar.value+="\n";
					arguments.callee();
				}
			})();
		}
        ,parseDirectory:function(panel,xhr){//"this" is accordion item panel;
			var ctnr=new Ext.Container({autoEl:"ul"});
			panel.add(ctnr);
			panel.doLayout();
			
			var data=eval(xhr.responseText);
			data.sort(function(a,b){return a>b?1:-1});//从小到大排序
			var len=data.length,i=-1;
			var t=Editor.help;
			var cel=ctnr.el;
			while((++i)<len){
				var font=data[i];
				var el=cel.createChild(t.applyDireTpl([font]));
				el.title=font;
				el.on("click",function(){t.openCtn(this);},el);
			}

			ctnr.doLayout();
			ctnr.show();
        },parseTreeDirectory:function(panel,xhr){
           // data=[{tag:"db",plugins:['cmppDB','db']},{tag:"redis",plugins:['redis']}];
           var data = eval(xhr.responseText);
            var root = new Ext.tree.TreeNode({
                text:'根节点'

            });
            for(var i=0;i<data.length;i++){
                var obj = data[i];
                var tag = obj['tag'];
                var plugins = obj['plugins'];
                var node = new Ext.tree.TreeNode({text:tag,leaf:false,iconCls:'no-node-icon',singleClickExpand :true});
                root.appendChild(node);
                if(plugins){
                    for(var j=0;j<plugins.length;j++){
                        var child = new Ext.tree.TreeNode({text:plugins[j],leaf:true,iconCls:'no-node-icon'});
                        node.appendChild(child);
                        child.on('click',function(){Editor.help.openLeaf(this);},child);
                    }
                }
            }
            var treePanel = new Ext.tree.TreePanel({
                rootVisible : false,
				border:false,
                loader:new Ext.tree.TreeLoader(),
                root:root

            });
            panel.add(treePanel);
            panel.doLayout();
            treePanel.doLayout();
            treePanel.show();
        }
        ,parseVer:function(panel,xhr){//"this" is accordion item panel;
			var ctnr=new Ext.Container({autoEl:"ul"});
			panel.add(ctnr);
			panel.doLayout();
			
			var data=JSON.parse(xhr.responseText);
			var len=data.length,i=-1;
			var t=Editor.help;
			var cel=ctnr.el;
			while((++i)<len){
				var key=data[i];
				var font=parseInt(key.time);
				var d=new Date(font);
				//font=d.getYear()+"-";
				font=(d.getMonth()+1)+"-";
				font+=d.getDate()+" ";
				font+=d.getHours()+":"+ d.getMinutes()+":"+ d.getSeconds();
                if(key.creator)
                    font+=" "+key.creator;
                if(key.state){
                    font+=" "+(key.state=="debug" ?"D":"R");
                }
				var el=cel.createChild(t.applyDireTpl([font,key.time]));
				el.title=font;
				el.on("click",function(){t.openVer(this);},el);
			}

			ctnr.doLayout();
			ctnr.show();
        }
         ,parseVaribles:function(panel,xhr){//"this" is accordion item panel;
			var ctnr=new Ext.Container({autoEl:"ul"});
			panel.add(ctnr);
			panel.doLayout();
			
			var data=eval(xhr.responseText);
			var len=data.length,i=-1;
			var t=Editor.help;
			var cel=ctnr.el;
			while((++i)<len){
				var itm=data[i];
				var el=cel.createChild(t.applyDireTpl([itm.name]));
				el.data=itm;
				el.on("click",function(){t.openVariables(this);},el);
			}

			ctnr.doLayout();
			ctnr.show();
        }       
		,loadDirectories:function(panel){	
			if(panel.loading)
				return;
			panel.loading=true;
			var t=this;
           Ext.Ajax.request({
                "url": panel.url,
                success: function(xhr){
                	delete panel.loading;
                	t.parseTreeDirectory(panel,xhr);
                	panel.un("beforeexpand",Editor.help.loadDirHandler);
                }
            });
		}
		,loadDirHandler:function(){
			Editor.help.loadDirectories(this);//this is panel;
		}
		,loadVerList:function(panel){	
			if(panel.loading)
				return;
			panel.loading=true;
			var t=this;
           Ext.Ajax.request({
                "url": panel.url,
                params:Editor.getParams(),
                success: function(xhr){
                	delete panel.loading;
                	t.parseVer(panel,xhr);
                	panel.un("beforeexpand",Editor.help.loadVerListHandler);
                }
            });
		}
		,loadVaribles:function(panel){	
			if(panel.loading)
				return;
			panel.loading=true;
			var t=this;
           Ext.Ajax.request({
                "url": panel.url,
                params:Editor.getParams(),
                success: function(xhr){
                	delete panel.loading;
                	t.parseVaribles(panel,xhr);
                	panel.un("beforeexpand",Editor.help.loadVariblesHandler);
                }
            });
		}
		,loadVerListHandler:function(){
			Editor.help.loadVerList(this);//this is panel;
		}
		,freshVerListHandler:function(){
			var p=Ext.getCmp("panelHistory");
			(p.items&&p.remove(p.items.get(0)));
			Editor.help.loadVerListHandler.call(p);
		}
		,loadVariblesHandler:function(){
			Editor.help.loadVaribles(this);//this is panel;
		}
		,ctnUrl:"script!pluginDoc.jhtml"+"?pname="
		,ctnWindow:new Ext.Window({
			closeAction:"hide",
			maximizable:true,
			autoScroll: true,
			height:400,
			width:537,
			buttonAlign:"center",
			buttons:[{
				text:'关闭',	
				handler:function(){
					this.ownerCt.el.fadeOut({
						endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
						easing: 'easeOut',
						duration: .5,
						useDisplay: false,
						scope:this,
						callback:function(){
							this.ownerCt.hide();
						}
					});	
					
				}
			}]
		
		})
		,scriptWindow:new Ext.Window({
			closeAction:"hide",
			maximizable:true,
			autoScroll: true,
			height:300,
			width:600,
			layout:"fit",
			buttonAlign:"center",
			items:{
				xtype:"panel",
				layout:"fit",
				title:'<div id="titleCurrent" class="mergely-title" style="float:left">当前内容</div><div id="titleHistory" class="mergely-title"  style="float:right">历史内容</div>',
				id:"mergelyPanel",
				html:'<div style="width:100%;height:100%;background:url(../res/img/loading.gif) no-repeat center center;"><iframe id="fr_editor" scrolling="no" frameborder="0" width="100%" height="100%" style="visibility:hidden"></iframe></div>' 
			},
			buttons:[{
				text:'关闭',	
				handler:function(){
					this.ownerCt.el.fadeOut({
						endOpacity: 0, //can be any value between 0 and 1 (e.g. .5)
						easing: 'easeOut',
						duration: .5,
						useDisplay: false,
						scope:this,
						callback:function(){
							this.ownerCt.hide();
						}
					});	
					
				}
			}]
		})
		,openVariables:function(el){
			var data=el.data;
			var w=this.ctnWindow;
			w.setTitle(data.name);
			w.show();
			var body=w.getLayoutTarget();
			body.update("");
			if(!el.content){
				var c=el.content=body.createChild({cls:"helpContainer"});	
				c.createChild({tag:"h1",html:data.name,cls:"clsTitle"});
				c.createChild({tag:"p",html:data.intro,cls:"h_body"});
				c=c.createChild({cls:"methods",html:"<div class='head'>方法列表</div>"});
				c=c.createChild({tag:"ul",cls:"h_body"});
				var ms=data.methods;
				var i=ms.length,len=i-1;
				while(i--){
					var d=ms[len-i];
					var li=c.createChild({tag:"li",cls:"methodItem"});
					li.createChild({tag:"h3",html:d.name,cls:'methodTitle'});
					li.createChild({tag:"p",html:d.intro,cls:"h_body"});
				}
			}else
				body.appendChild(el.content);			
		}		
		,showCtn:function(el){
			var w=this.ctnWindow;
			w.setTitle(el.title);
			w.show();
			w.getLayoutTarget().update(el.content);			
		}
		,openCtn:function(el){
			var t=this;
			var url=this.ctnUrl+el.title;
			if(el.content)
				t.showCtn(el);
			else
            Ext.Ajax.request({
                "url": url,
                success:function(xhr){
                	el.content=xhr.responseText;
                	t.showCtn(el);
                }         
            });		
		},
        openLeaf:function(el){
            var t = this;
            var url = this.ctnUrl+el.text;
            if(el.content){
                t.showCtn(el);
            }
            else
            Ext.Ajax.request({
                'url':url,
                success:function(xhr){
                    el.content = xhr.responseText;
                    t.showCtn(el);
                }
            })
        }
		,openVer:function(el){
			var t=this;
			var key=el.dom.getAttribute("key");
			var dateStr = el.title;
			if(el.content)
				t.showVer(el.content);
			else{
				var data={version:key};
				data=Ext.apply(data,Editor.params);
	            Ext.Ajax.request({
	                "url": "script!openByVer.jhtml",
	                params:data,
	                success:function(xhr){
	                	var script= el.content=xhr.responseText;
	                	t.showVer(script,dateStr);
	                }         
	            });	
			}
		}
		,showVer:function(script,dateStr){
			this.scriptWindow.show();
			this.scriptWindow.maximize();				

			Ext.get("titleHistory").update(dateStr);

			var iframe = document.getElementById('fr_editor');
			iframe.onload = function(){
				this.contentWindow.init(Editor.getScript(),script);
				Ext.fly(this).fadeIn({
					endOpacity: 1, //can be any value between 0 and 1 (e.g. .5)
					easing: 'easeIn',
					duration: .5,
					useDisplay: false
				});	
			};	
			iframe.src = 'mergely/mergely.html'; 			
		}
}



/*
Editor.commands={
	file:{
		"new":function(){Editor.createFile();}
	}
}
*/
Editor.ui={
	helpPanel:{ 
		title: "帮助"
		,xtype: "panel" 
	    ,region: 'east'
	     , width: 200
		 ,activeOnTop:true
	     , split: true
	     ,layout:"accordion"
		,layoutConfig:{
			animate:true,
			//activeItem:{setSize:function(){},collapse:function(){}}
		}
	    ,items:(function(){
			var items=[{
				title:"工具插件"
				,autoScroll:true
				,url:"./script!pluginList.jhtml"
				,listeners:{
					beforeexpand:Editor.help.loadDirHandler,
					render:Editor.help.loadDirHandler
				}
			}];
			if(globalvar__.params.saveEnable!=="false"){
				items.push({
					title:"历史列表"
					,autoScroll:true
					,id:"panelHistory"
					,url:"./script!getVersionList.jhtml"
					,listeners:{
						beforeexpand:Editor.help.loadVerListHandler
					},tools:[{
						 id:"refresh"
						 ,handler:Editor.help.freshVerListHandler
					}]
				});
			}
			items.push({
				title:"内置变量"
				,autoScroll:true
				,url:"./script/static/varDoc.js"
				,listeners:{
					beforeexpand:Editor.help.loadVariblesHandler
				}
			},{
				title:"调试注入变量"
				,autoScroll:true
				,items:{xtype:"listfield",id:"injectedVariables"}
			});
			
			return items;
		})()
     }
	,south:{
	    region: 'south'
	    ,xtype:"tabpanel"
	     , height: 150
	     , collapsible: true
	     , split: true
	 }
	,workspace:{
		 region: 'center'
		,xtype:"panel"
		,items:{xtype:"field","id":Editor.id,autoCreate:{"tag":"textarea"},style:"width:100%;height:100%"}
	}
	,editor:{
		id: Editor.id,
		language:"zh",
		syntax:"js"	,
		is_multi_files:false,
		allow_resize:false,
		allow_toggle:false,
		//,word_wrap:true
		start_highlight:true,
		ifeng_script_debug: true,
		EA_load_callback:"Editor.loaded",
		save_callback:"Editor.saveFileHandler",
        EA_release_callback:"Editor.releaseHandler",
		toolbar: (globalvar__.params.saveEnable=="false"?"":"save, ") + "search, go_to_line, |, undo, redo, |, select_font,|, change_smooth_selection, highlight, reset_highlight, word_wrap, |,fullscreen" + (globalvar__.params.saveEnable=="false"?"":",release")
   }
};


</script>


<script type="text/javascript">
var scriptTplContent = document.getElementById("txtTplScript").value;

Ext.onReady(function(){
	Editor.init();
});

</script>	
</body>
</html>
