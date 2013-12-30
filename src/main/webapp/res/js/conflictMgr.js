/*
*编辑冲突检查
*/
var conflictMgr = {
	containerEl:null,
	conflictBox:null,
	key:'',
	intervalTime:2,//间隔检查时间 单位：分
	interfaceUrl:'../runtime/online.jhtml',
	init:function(containerEl,key){
		this.key = key;
		this.containerEl = containerEl;
		if(containerEl){
			this.conflictBox = containerEl;
			/*
			this.conflictBox = containerEl.createChild({
				tag:'div',
				style:'height:20px;'
			});*/
		}
		this.check();
		setInterval("conflictMgr.check()",this.intervalTime*1000*60);
		window.onbeforeunload = conflictMgr.remove;
	},
	callback:function(response,opts){
		try{
			var ret = Ext.util.JSON.decode(response.responseText);
			conflictMgr.showDetail(ret);
		}catch(ex){
			console.info(ex);
		}
	},
	check:function(){
		Ext.Ajax.request({  
			url:this.interfaceUrl,
			method:'post',	
			params:{url:this.key},
			success:this.callback
		});
	},
	//移除当前用户的编辑状态
	remove:function(){
		Ext.Ajax.request({  
			url:conflictMgr.interfaceUrl,
			method:'post',	
			params:{url:conflictMgr.key,remove:true},
			success:function(response,opts){
				try{
					var ret = Ext.util.JSON.decode(response.responseText);

				}catch(ex){
					console.debug(ex);
				}
			}
		});
	},
	showDetail:function(users){
		var html = ''
		for(var i=0;i<users.length;i++){
			var u = users[i];
			if(!u.isCurrent){
				if(html!='') html+='、';
				html+=u.userName;
			}else{
				window.serverDateTime__ = u.datetime;
				window.userName__ = u.userName;
			}
		}
		if(conflictMgr.containerEl){
			if(html!=''){
				this.conflictBox.el.setStyle({
					border:'1px inset red',
					color:'red'
				});
				this.conflictBox.setValue('【重要提示】正在编辑该页面的还有：' + html);
			}else{
				this.conflictBox.el.setStyle({
					border:'1px inset #A9BFD3',
					color:'#000'
				});
				this.conflictBox.setValue('');
			}
		}
	}
	

};
