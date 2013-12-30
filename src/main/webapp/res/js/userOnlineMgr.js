//在线用户管理
var userOnlineMgr={
	userList:[],
	userWin:null,
	url:'../runtime/online.jhtml',//  ../runtime/online!list.jhtml
	init:function(key){
		this.key = key;
		this.userWin = new Ext.Window({
			title:'在线用户',
			width:82,
			height:330,
			autoScroll:true,
			closeAction :'hide',
			draggable :false,
			renderTo:Ext.getBody(),
			cls:'online_list_user',
			border:false,
			closable :false,
			bbar:[{
				text:'︾',
				minWidth :66,
				tooltip:'收起',
				handler:function(){
					userOnlineMgr.userWin.hide();	
				}
			}],
			html:'',
			listeners:{
				render:function(obj){
					obj.bbar.dom.align = 'center';//按钮居中
				}
			},
			tools :[{  
                id :'minus',  
                qtip : '收起',  
                handler : function(event, el, panel){  
					userOnlineMgr.userWin.hide();
				}
			}]
			
		});
		this.refreshUserList();	
	},
	refreshUserList:function(){
		Ext.Ajax.request({  
			url:userOnlineMgr.url,
			method:'post',	
			params:{url:userOnlineMgr.key},
			scope:userOnlineMgr,
			success:function(response,options){
				try{
					var ret = Ext.util.JSON.decode(response.responseText);
					var count = 0;	
					if(Ext.isArray(ret)){
						this.userList = ret;
						count = ret.length;
					}
					Ext.getCmp('btnOnline').setText('在线用户(' + count + ')');
				}catch(ex){
					console.info(ex);
				}
			},
			callback :function(){
				setTimeout(userOnlineMgr.refreshUserList,1000*60*2);
			}
		});
	},
	showUserList:function(){
		var html='<ul>';
		for(var i=0;i<this.userList.length;i++){
			html+='<li>'+ this.userList[i].userName +'</li>';	
		}
		html+='</ul>';
		this.userWin.show();
		this.userWin.body.update(html);
		this.userWin.alignTo(Ext.get('btnOnline'),'br-bl',[82,7]);
	},
	hideUserList:function(){
	
	}
};
