 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>CMPP</title>

	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>

	<style>
#header,#souther{height:25px;background:#46A3FF;border-bottom:2px solid #46A3FF; border-radius: 2px;padding:0 1em;}
#souther{border-top:2px solid #46A3FF;border-bottom:none;}
#header .x-panel-body,#souther .x-panel-body{background:transparent;color:#D2E9FF;font-size:12px;padding-top:2px}
#souther .copyright{float:left;}
#btmRight{float:right;}
#btmRight div{float:right;margin-left:1em;cursor:pointer;}
#btmRight div:hover{text-decoration:underline;}
#btmRight div span{color:#faf7d3}
.x-tool-collapse-west{background:url("../res/js/ext2/resources/images/default/panel/tools-sprites-trans.gif") no-repeat scroll 0 -180px transparent}
.x-tool-btnAddCtnr{background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") no-repeat scroll center center transparent}
#header .x-panel-body div,#header .x-panel-body ul{float:left;}
#btnAddBtn{width:15px;height:15px;margin-top:3px;cursor:pointer;background:url("../res/js/ext2/resources/images/default/dd/drop-add.gif") no-repeat scroll center center transparent}
#topLeft li{display:inline-block;margin-right:1em;}
#btnWindow_form .x-form-item,#ctnrWindow_form .x-form-item{padding:5px 0;}
.x-form-check-wrap{margin-top:5px;}
.iconAdd{background:url("../res/js/ext2/resources/images/default/my/add-form.gif") no-repeat center center transparent}
.iconDel{background:url("../res/js/ext2/resources/images/default/my/del-form.gif") no-repeat center center transparent}
.iconEdit{background:url("../res/js/ext2/resources/images/default/my/modify-form.gif") no-repeat center center transparent}

.tBtn{display:inline-block;height:21px;line-height:21px;color:#003c74;text-decoration:none;}
#topLeft .tBtn span{display:inline-block;height:100%;vertical-align:middle;}
#topLeft .tBtn:hover .bLeft,#topLeft .tBtn:hover .bRight,#topLeft .tBtn:hover .bCenter{background-image:url(../res/js/ext2/resources/images/default/toolbar/tb-btn-sprite.gif);background-repeat:no-repeat;}
#topLeft .tBtn .bLeft,#topLeft .tBtn .bRight{width:3px;}
#topLeft .tBtn .bCenter{padding:0 3px;}
#topLeft .tBtn:hover .bRight{background-position:0 -21px;}
#topLeft .tBtn:hover .bCenter{background-position:0 -42px;background-repeat:repeat-x;}

#wester .tBtn{width:100%;padding:5px 0;padding-left:30px;font-size:14px;background:url(../res/js/ext2/resources/images/default/shared/right-btn.gif) no-repeat 5px 7px}
#wester .tBtn:hover{background:#d5eafd;}

#popWin{visibility:hidden;z-index:999;width:300px;height:200px;position:absolute;right:0;bottom:0;background:#eee;}
#popWin_t{overflow:hidden;}
#popWin_closer{float:right;background:red;width:20px;height:20px;}
#popWin_tl{float:left;}
#popWin_title{height:30px;overflow:visible;}
#popWin_content{height:120px;overflow:auto;}
#popWin_prev{float:left;cursor:pointer;}
#popWin_next{float:right;cursor:pointer;}
.x-tool-refresh{position:absolute;right:2px;top:5px;z-index:9;}
	</style>
	
	<script>
Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
            
var _nodeId=Ext.parseQuery().nodeId;

Ext.onReady(function(){_sys.init();});



var _sys,S=_sys={
	ctnrTop:null
	,ctnrLeft:null
	,btnAddBtn:null
	,btnAddCtnr:null
	,centerTabPanel:null
	,btnMenu:null
	,ctnrMenu:null
	,allTabs:{}
	,key:new Date().valueOf()
	,localData:null
	,localDataKey:"CMPP_BUTTONS_DATA"
	/*
	,data:{
		topBar:{buttons:[
			{text:"按键一","url":"http://126.com",comment:"这是按钮一",blank:false}
			,{text:"按键二","url":"http://126.com",comment:"这是按钮一",blank:true}
		]}
		,accordion:[
			{title:"工具箱一",comment:"这是工具箱一",buttons:[
				{text:"按键一","url":"http://126.com",comment:"这是按钮一",blank:true}				
				,{text:"按键二","url":"http://126.com",comment:"这是按钮一",blank:false}
			]}
		]
	}
	*/
	,init:function(){
		if(!localStorage)
			return alert("您的浏览器版本太低，无法正常使用。");

		var tt = new Ext.Template(
		     '<div class="x-tool x-tool-{id}" id="{id}" title="{tip}">&#160;</div>'
		);
		tt.disableFormats = true;
		tt.compile();
		Ext.Panel.prototype.toolTemplate = tt;

		
		
		new Ext.Viewport(this.ui);
		
		this.btnWindow=new Ext.Window(this.btnWindow);
		this.ctnrWindow=new Ext.Window(this.ctnrWindow);
		
		this.btnAddBtn=Ext.get("btnAddBtn");
		this.btnAddCtnr=Ext.get("btnAddCtnr");
		this.ctnrTop=Ext.get("topLeft");
		this.ctnrLeft=Ext.getCmp("wester");
		this.centerTabPanel=Ext.getCmp("centerTabPanel");
		window.centerTabPanel = this.centerTabPanel;
		window.centerTabPanel.addIframe=function(id,title,url){
			_sys.addIframe({id:id,text:title,url:url})
		}

		var ths=this;
		this.btnAddBtn.on("click",function(){ths.showBtnWindow({text:"",url:"",comment:"",blank:false},null,ths.ctnrTop,ths.localData.topBar.buttons);});
		this.btnAddCtnr.on("click",function(){ths.showCtnrWindow({title:"",comment:""});});
		
		var m=this.btnMenu=new Ext.menu.Menu(this.btnMenu);
		m.on("itemclick",this.btnContextHandle,this);
		
		m=this.ctnrMenu=new Ext.menu.Menu(this.ctnrMenu);
		m.on("itemclick",this.ctnrContextHandle,this);			
		
		this.readData();
		Ext.EventManager.on(window,"unload",this.unload,this);
	}
	,render:function(data,isSys){
		var t=data.topBar.buttons;
		var i,i=len=t.length,idx;
		len--;
		while(i--){
			idx=isSys?i:len-i;
			this.addButton(t[idx],this.ctnrTop,isSys?null:"topBar.buttons["+idx+"]");
		}
		t=data.accordion;
		i=len=t.length;
		len--;
		while(i--){
			idx=isSys?i:len-i;
			this.addAccordion(t[idx],idx,isSys);
		}		
		
		this.popWin.init();		
	}
	,unload:function(){
		this.storeLocal();
		this.popWin.unload();
	}
	,collectData:function(){
		var d=this.localData;
		this._clenseData(d.topBar.buttons);
		var t=d.accordion;
		var i=t.length;
		while(i--){
			if(t[i])
				this._clenseData(t[i].buttons);
			else
				t.splice(i,1);
		}
		return d;		
	}
	,_clenseData:function(arr){
		var i=arr.length;
		while(i--){
			(!arr[i]&&arr.splice(i,1));
		}	
	}
	,storeLocal:function(){
		var d=this.collectData();
		d=Ext.encode(d);
		localStorage.setItem(this.localDataKey,d);
	}
	,readData:function(){
		var d=localStorage.getItem(this.localDataKey);
		//d=null;
		if(d){
			d=this.localData=Ext.decode(d);
			this.render(d);
		}else{
        	this.localData={topBar:{buttons:[]},accordion:[]};
        }
		this.loadDefault();
	}
	,loadDefault:function(){
		var t=this;
		Ext.getBody().mask("正在加载系统配置...");
		Ext.Ajax.request({
			"url":"./../runtime/rtMgr!readSysMenu.jhtml?nodeId=" + _nodeId ,
			"success":function(xhr){
				Ext.getBody().unmask();
				if(!Ext.nore(xhr.responseText)){
					var data=Ext.decode(xhr.responseText);	
					t.render(data,true);		
				}
			}
			,'failure':function(){
				Ext.getBody().unmask();
				Ext.Msg.show({
					title:'错误提示',
					msg: '读取系统配置失败',
					buttons: Ext.Msg.OK,
					animEl: 'elId',
					minWidth:420,
					icon: Ext.MessageBox.ERROR 
				});
			}
		});	
	}
	,addAccordion:function(data,index,isSys){
		var pnl=this.ctnrLeft.insert(0,{title:data.title});
		this.ctnrLeft.doLayout();
		if(!isSys){
			pnl.header.dom.setAttribute("path","accordion["+index+"]")
			pnl.header.on("contextmenu",this.showCtnrContext,this);
		}
		pnl.header.dom.setAttribute("title",data.comment);
	
		var ul=pnl.body.createChild({tag:"ul"});

		var btns=data.buttons;
		var len,i=len=btns.length,idx;
		len--;
		while(i--){
			idx=isSys?i:len-i;
			this.addButton(btns[idx],ul,isSys?null:"accordion["+index+"].buttons["+idx+"]");
		}
	}
	,addButton:function(data,ctnr,path,li){
		var url,target;
		if(data.blank){
			url=data.url,
			target="_blank";
		}
		else{
			url="javascript:void(0)";
			target="_self";
		}
		if(li){
			var orili=li;
		}
		if(path){//local
			li=ctnr.createChild({tag:"li",path:path,text:data.text,title:data.comment,url:data.url,blank:data.blank});
			li.on("contextmenu",this.showBtnContext,this);
		}else
			li=ctnr.createChild({tag:"li",text:data.text,title:data.comment,url:data.url,blank:data.blank},ctnr.dom.firstChild);
		if(orili)
			orili.parentNode.replaceChild(li.dom,orili);
		
		
		var btn=li.createChild({tag:"a",key:"key"+(++this.key),cls:"tBtn",html:"<span class='bLeft'></span><span class='bCenter'>"+data.text+"</span><span class='bRight'></span>",href:url,target:target});
		if(!data.blank){
			var t=this;
			btn.on("click",function(){
				data.id=this.dom.getAttribute("key");
				t.addIframe(data);
			});
		}
	}
	,showBtnContext:function(e){
		this.btnMenu.target=e.browserEvent.currentTarget;//a;
		this.btnMenu.showAt(e.xy);
		e.preventDefault();
	}	
	,showCtnrContext:function(e){
		this.ctnrMenu.target=e.target;//a;
		this.ctnrMenu.showAt(e.xy);
		e.preventDefault();
	}
	,ctnrContextHandle:function(menuItem,e){
		var type=menuItem.id;
		var tar=menuItem.parentMenu.target;
		if(type=="btnEditCtnr"){
			var values={
				title:tar.lastChild.innerHTML
				,comment:tar.getAttribute("title")
			}			
			this.showCtnrWindow(values,tar);
		}		
		else if(type=="btnAddBtn"){
			eval("var arr=_sys.localData."+tar.getAttribute("path"));
			this.showBtnWindow({text:"",url:"",comment:"",blank:false},null,Ext.get(tar.nextSibling.getElementsByTagName("ul")[0]),arr.buttons);
		}	
		else if(type=="btnDelCtnr"){
			var t=this;
			Ext.Msg.confirm("小心","确定删除？",function(id){
				if(id=="yes")
					t.delCtnr(tar);
			});		
		}
	}
	,delCtnr:function(tar){
		eval("delete _sys.localData."+tar.getAttribute("path"));
		this.ctnrLeft.remove(Ext.getCmp(tar.parentNode.id));
	}
	,btnContextHandle:function(menuItem,e){
		var type=menuItem.id;
		var tar=menuItem.parentMenu.target;
		if(type=="btnEditButton"){
			var values={
				text:tar.getAttribute("text")
				,comment:tar.getAttribute("title")
				,url:tar.getAttribute("url")
				,blank:tar.getAttribute("blank")=="true"?true:false
			}		
			this.showBtnWindow(values,tar);
		}		
		else if(type=="btnDelButton"){
			var t=this;
			Ext.Msg.confirm("小心","确定删除？",function(id){
				if(id=="yes")
					t.delButton(tar);
			});
		}
	}
	,delButton:function(target){
		var path=target.getAttribute("path");	
		eval("delete this.localData."+path); 
		target.parentNode.removeChild(target);
	}
	,editButton:function(values,target){
		var path=target.getAttribute("path");	
		eval("this.localData."+path+"=values");
		this.addButton(values,Ext.get(target.parentNode),path,target);
	}
	,showBtnWindow:function(values,target,ctnr,arr){
		var w=this.btnWindow;
		w.show();
		w.setValues(values);
		var t=this;
		if(target){
			w.callback=(function(_target){
				return function(vals){
					t.editButton(vals,_target)
				};
			})(target);
		}else{
			w.callback=(function(a){
				return function(vals){
					a.push(vals);
					t.addButton(vals,ctnr,ctnr.dom.getAttribute("path")+".buttons["+(a.length-1)+"]");
				};
			})(arr);
		}
	}
	,showCtnrWindow:function(values,target){
		var w=this.ctnrWindow;
		w.show();
		w.setValues(values);
		var t=this;
		if(target){
			w.callback=(function(_target){
				return function(vals){
					t.editCtnr(vals,_target)
				};
			})(target);
		}else{
			w.callback=(function(){
				return function(vals){
					vals.buttons=[];
					var data=t.localData.accordion;
					data.push(vals);
					t.addAccordion(vals,data.length-1);
				};
			})();
		}		
	}
	,editCtnr:function(vals,target){
		target.lastChild.innerHTML=vals.title;
		target.setAttribute("title",vals.comment);
		var path=target.getAttribute("path");
		eval("this.localData."+path+".title='"+vals.title+"'");
		eval("this.localData."+path+".comment='"+vals.comment+"'");
	}
	,addIframe:function(data){
	var t=this;
	var menu=new Ext.menu.Menu({
		items:[
			{
				text:"关闭这个窗口"
				,id:"btnCloseThisTab"
				,iconCls:"iconDel"
				,handler:function(){
					var id=this.parentMenu.tab.id;
					if(t.allTabs[id] && t.allTabs[id].tab){
						t.centerTabPanel.remove(t.allTabs[id].tab);
						delete t.allTabs[id];
					}
				}
			}
			,{
				text:"关闭其它窗口"
				,id:"btnCloseOtherTabs"
				,iconCls:"iconDel"
				,handler:function(){
					var id=this.parentMenu.tab.id;
					var panel=t.allTabs[id];
					for(var i in t.allTabs){
						if(i!=id){
							t.centerTabPanel.remove(t.allTabs[i].tab);
							delete t.allTabs[id];
						}
					}
					t.allTabs[id] = panel;
				}
			}
			,{
				text:"关闭所有窗口"
				,id:"btnCloset.allTabs"
				,iconCls:"iconDel"
				,handler:function(){
					for(var i in t.allTabs){
						t.centerTabPanel.remove(t.allTabs[i].tab);
						delete t.allTabs[i];
					}
					t.allTabs={};
				}
			}	
			,{
				text:"重新载入"
				,id:"btnRefreshTab"
				,iconCls:"iconReresh"
				,handler:function(){
					var id=this.parentMenu.tab.id;
					var iframe = t.allTabs[id].iframe;
					iframe.dom.contentWindow.location.reload();
					t.centerTabPanel.setActiveTab(t.allTabs[id].tab);
				}
			}		
		]
	});					
	var showMenu=function(e){
		menu.tab=e.browserEvent.currentTarget;
		menu.showAt(e.xy);
	}


	this.addIframe=function(data){	
		(data.id||(data.id="tab"+data.url));

		var n=Ext.getCmp(data.id);
		if(n){
			this.centerTabPanel.setActiveTab(n);
			return this.allTabs["centerTabPanel__"+data.id].iframe.dom.contentWindow.location.reload();
		}
		
		n=this.centerTabPanel.add({
			id:data.id,
			closable:true,
			title:data.text,
			layout:"fit",
		});
		this.centerTabPanel.setActiveTab(n);
		
		var iframe=n.body.createChild({tag:"iframe",src:data.url,scrolling:"auto", frameborder:"0", width:"100%", height:"100%"});
		var t=this.centerTabPanel.getTabEl(this.centerTabPanel.activeTab);
		t.setAttribute("title","双击在新窗口打开");
		var el=Ext.fly(t)
		el.on("dblclick",function(){
			window.open(iframe.dom.getAttribute("src").toString());
		});
		el.on("contextmenu",showMenu);
		this.allTabs["centerTabPanel__"+data.id]={tab:n,iframe:iframe};				
		return {iframe:iframe,panel:n};	
		}
		return this.addIframe(data);				
	}
	,freshIframe:function(){
		var tab=this.centerTabPanel.getActiveTab();
		if(tab){
			var id = "centerTabPanel__"+tab.id;
			if(this.allTabs[id]){
				this.allTabs[id].iframe.dom.contentWindow.location.reload();
			}
		}
	}
};

S.popWin={
	pollInterval:10*1000
	,closeInterval:5000
	,closer:"popWin_closer"
	,total:"popWin_total"
	,current:"popWin_current"
	,prev:"popWin_prev"
	,next:"popWin_next"
	,title:"popWin_title"
	,content:"popWin_content"
	,window:"popWin"
	,boxRead:"read"
	,boxUnread:"unread"
	,width:0
	,height:0
	,msgsRead:[{title:"aaaaa",content:"aaaaaaaaaa"},{title:"bbbbbbbb",content:"bbbbbbbbbbbbbbbb"}]
	,msgsUnread:[{title:"ccccccc",content:"ccccccccc"},{title:"ddddddd",content:"dddddddddd"}]
	,msgFlags:{read:0,unread:1}
	,flag:null
	,data:null
	,closeTimer:null
	,index:0
	,readMax:10
	,localDataKey:"CMPP_MSG_DATA_"+_nodeId
	,init:function(){
		this.readLocal();

		this.closer=Ext.get(this.closer);
		this.total=Ext.get(this.total);
		this.current=Ext.get(this.current);
		this.prev=Ext.get(this.prev);
		this.next=Ext.get(this.next);
		this.title=Ext.get(this.title);
		this.content=Ext.get(this.content);
		this.window=Ext.get(this.window);
		this.boxRead=Ext.get(this.boxRead);
		this.boxUnread=Ext.get(this.boxUnread);
	
		var anchor=this.boxRead;
		var span=anchor.span=anchor.dom.getElementsByTagName("span")[0];
		span.innerHTML=this.msgsRead.length;
		anchor=this.boxUnread;
		span=anchor.span=anchor.dom.getElementsByTagName("span")[0];
		span.innerHTML=this.msgsUnread.length;
		
		var p=this.window;
		this.width=p.getWidth();
		this.height=p.getHeight();
		
		this.prev.enableDisplayMode();
		
		this.boxRead.on("click",this.readHandler,this);
		this.boxUnread.on("click",this.unreadHandler,this);		
		this.closer.on("click",this.closerHandler,this);
		this.prev.on("click",this.prevHandler,this);
		this.next.on("click",this.nextHandler,this);
		
		this.pollNew(100);
	}
	,pollNew:function(interval){
		var t=this;
		window.setTimeout(function(){
			t.loadNew();
		},interval||this.pollInterval);	
	}
	,unload:function(){
		this.storeLocal();
	}
	,storeLocal:function(){
		var d={msgsRead:this.msgsRead,msgsUnread:this.msgsUnread};
		d=Ext.encode(d);
		localStorage.setItem(this.localDataKey,d);
	}
	,readLocal:function(){
		var d=localStorage.getItem(this.localDataKey);
		//rest:d=null;
		if(!d){
			this.msgsRead=[];
			this.msgsUnread=[];
		}else{
			d=Ext.decode(d);
			this.msgsRead=d.msgsRead;
			this.msgsUnread=d.msgsUnread;
		}
	}	
	,loadNew:function(){
		var t=this;
		Ext.Ajax.request({
			"url":"./../data/message/notify.html",
			"success":function(xhr){
				var d=Ext.decode(xhr.responseText);			
				if(d.success){
					t.popNew(d.message);
				}
				t.pollNew();
			}
			,"failure":function(){
				t.pollNew();
			}
		});	
	}
	,popNew:function(msg){
		this.msgsUnread.unshift(msg);
		this.boxUnread.span.innerHTML=this.msgsUnread.length;				
		var t=this;
		this.closeTimer=window.setTimeout(function(){
			t.disappear();
		},this.closeInterval)
		this.unreadHandler();	
	}
	,readPrev:function(){
		if(this.data.length==0)
			return null;
		var msg= this.data[--this.index];
		this.current.update(this.index+1);
		this.total.update(this.data.length);		
		return msg;
	}
	,readNext:function(manual){
		if(this.data.length==0)
			return null;
		var msg=null;
		if(this.flag==this.msgFlags.unread){
			if(manual)
				this.markRead();
			this.total.update(this.data.length);
			msg=this.data[0];
		}else{
			msg= this.data[++this.index];
			this.current.update(this.index+1);
		}
		return msg;			
	}
	,markRead:function(){
		this.clearTimer();
		if(this.data.length>0){
			var msg=this.data.splice(0,1)[0];
			this.msgsRead.push(msg);
			if(this.msgsRead.length>this.readMax)
				this.msgsRead.shift();
			else
				this.boxRead.span.innerHTML=this.msgsRead.length;	
			this.boxUnread.span.innerHTML=this.msgsUnread.length;				
		}
	}
	,clearTimer:function(){
		if(this.closeTimer){
			window.clearTimeout(this.closeTimer);
			this.closeTimer=null;
		}		
	}
	,readHandler:function(){
		this.appear(this.msgFlags.read);
	}
	,unreadHandler:function(){
		this.appear(this.msgFlags.unread);
	}
	,prevHandler:function(){
		if(this.index>0){
			var data=this.readPrev();
			if(data){
				this.showContent(data);
				if(this.index==this.data.length-2)
					this.next.setStyle("cursor","pointer");	
				if(this.index==0)
					this.prev.setStyle("cursor","default");							
			}
		}	
	}
	,nextHandler:function(manual){
		if(this.index<this.data.length-1){
			var data=this.readNext(manual);
			if(data){
				this.showContent(data);
				if(this.index==1)
					this.prev.setStyle("cursor","pointer");	
				if(this.index==this.data.length-1)
					this.next.setStyle("cursor","default");
			}
		}		
	}
	,showContent:function(data){
		this.title.update(data.title);
		this.content.update(data.content);
	}
	,toggle:function(isShow){
		var p=this.window;
		if(isShow){
			var anchor=this.anchor;
			var xy=anchor.getXY();
			xy[0]-=this.width;
			xy[1]-=this.height;
			p.setXY(xy);
			p.slideIn("br");
		}else
			p.slideOut("br");
	}
	,appear:function(flag){
		this.flag=flag;
		if(flag==this.msgFlags.read){
			this.data=this.msgsRead;
			this.anchor=this.boxRead;
			this.prev.setStyle("cursor","default");	
			this.current.update(0);
			this.prev.show();
			this.index=-1;
		}else{
			this.data=this.msgsUnread;
			this.anchor=this.boxUnread;	
			this.prev.hide();
			this.current.update(this.data.length>0?1:0);
			this.index=0;
		}
		this.total.update(this.data.length);
		this.title.update();
		this.content.update();
		if(this.data.length>1)
			this.next.setStyle("cursor","pointer");
		else
			this.next.setStyle("cursor","default");
		
		this.toggle(true);
		this.nextHandler();
	}
	,disappear:function(anchor){
		this.toggle();
	}
	,closerHandler:function(){
		if(this.flag==this.msgFlags.unread)		
			this.markRead ();
		this.disappear();
	}
}

S.btnMenu={
	items:[
		{
			text:"修改"
			,id:"btnEditButton"
			,iconCls:"iconEdit"
		}
		,{
			text:"删除"
			,id:"btnDelButton"
			,iconCls:"iconDel"
		}
	]
}

S.ctnrMenu={
	items:[
		{
			text:"添加按钮"
			,id:"btnAddBtn"
			,iconCls:"iconAdd"
		}
		,{
			text:"修改工具箱"
			,id:"btnEditCtnr"
			,iconCls:"iconEdit"
		},{
			text:"删除工具箱"
			,id:"btnDelCtnr"
			,iconCls:"iconDel"
		}
	]
}

S.btnWindow={
	title:"按钮配置"
	,closeAction:"hide"
	,callback:null
	,inputs:null
	,form:null
	,setValues:function(values){
		var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
		var len=inputs.length;
		while(len--){
			var ipt=inputs[len];
			if(ipt.type=="checkbox"){
				ipt.checked=values[ipt.name];
			}
			else
				ipt.value=values[ipt.name];			
		}		
	}
	,confirm:function(){
		var f=this.form||(this.form=Ext.getCmp("btnWindow_form"));
		if(!f.form.isValid())
			return;
		var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
		var len=inputs.length;
		var values={};
		while(len--){
			var ipt=inputs[len];
			if(ipt.type=="checkbox")
				values[ipt.name]=ipt.checked;
			else
				values[ipt.name]=ipt.value;
		}
		this.callback(values);	
		this.hide();
	}
	,items:{
		xtype:"form"
		,bodyStyle:"padding:2em"
		,id:"btnWindow_form"	
		,items:[{
			fieldLabel:"文字"
			,xtype:"textfield"
			,width:"300"
			,name:"text"
			,allowBlank:false
		},{
			fieldLabel:"链接"
			,xtype:"textfield"
			,name:"url"
			,width:"300"
			,allowBlank:false
		},{
			fieldLabel:"说明"
			,xtype:"textfield"
			,name:"comment"
			,width:"300"
		},{
			fieldLabel:"在新窗口打开"
			,name:"blank"
			,xtype:"checkbox"
		}]
	}
	,buttons:[{
		text:"确定"
		,handler:function(){
			this.ownerCt.confirm();
		}
	},{
		text:"取消"
		,handler:function(){
			this.ownerCt.hide();
		}
	}]
}


S.ctnrWindow={
	title:"工具箱配置"
	,width:500
	,closeAction:"hide"
	,callback:null
	,inputs:null
	,form:null
	,setValues:function(values){
		var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
		var len=inputs.length;
		while(len--){
			var ipt=inputs[len];
			ipt.value=values[ipt.name];			
		}		
	}
	,confirm:function(){
		var f=this.form||(this.form=Ext.getCmp("ctnrWindow_form"));
		if(!f.form.isValid())
			return;
		var inputs=this.inputs||(this.inputs=this.body.dom.getElementsByTagName("input"));
		var len=inputs.length;
		var values={};
		while(len--){
			var ipt=inputs[len];
			values[ipt.name]=ipt.value;
		}
		this.callback(values);	
		this.hide();
	}
	,items:{
		xtype:"form"
		,bodyStyle:"padding:2em"
		,id:"ctnrWindow_form"
		,items:[{
			fieldLabel:"名称"
			,xtype:"textfield"
			,width:"300"
			,name:"title"
			,allowBlank:false
		},{
			fieldLabel:"说明"
			,width:"300"
			,xtype:"textfield"
			,name:"comment"
		}]
	}
	,buttons:[{
			text:"确定"
			,handler:function(){
				this.ownerCt.confirm();
			}
		},{
			text:"取消"
			,handler:function(){
				this.ownerCt.hide();
			}
	}]
}

S.ui={
	frame:true,
	layout:"border",
	margins:'5',
	items:[{
		region: 'north',	
		border:false,
		id:'header',
		html:'<div id="logo"></div><ul id="topLeft" path="topBar.buttons"></ul><div id="btnAddBtn" title="添加按钮"></div><div id="topRight"></div>'
	},{
		region:"west",
		id:"wester",
		title: 'CMPP',
		width:200,
		collapsible: true,
		split:true,
		autoScroll: true,
		layout:"accordion",
		layoutConfig:{
			titleCollapse: true,   
			collapseFirst : true,   
			animate: true
		}
		,tools:[{id:"btnAddCtnr",tip:"添加工具箱"}]
	},{
		region:"south",
		id:"souther",
		html:'<div class="copyright">运营技术开发组 Copyright @ 2011 Phoenix New Media Inc. All Rights Reserved.</div>\
				<div id="btmRight"><div id="unread">未读(<span>10</span>)</div><div id="read">已读(<span>10</span>)</div></div>'
	},{
		region:"center",
		id:"centerTabPanel",
		xtype:"tabpanel",
		items:{title:"Welcome"},
		tools:[
			{tip:"刷新",id:"refresh",handler:function(){_sys.freshIframe();}}
		]
	}]
};




	</script>
		
</head>
<body>	
<div id="popWin">
	<div id="popWin_t">
		<div id="popWin_tl"><span id="popWin_current"></span>/<span id="popWin_total"></span></div>
		<div id="popWin_closer"></div>
	</div>
	<div id="popWin_title"></div>
	<div id="popWin_content"></div>
	<div>
		<div id="popWin_prev">上一条</div>
		<div id="popWin_next">下一条</div>
	</div>	
</div>
</body>
</html>