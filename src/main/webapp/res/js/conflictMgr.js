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
			var ret = eval("(" + response + ")");
			conflictMgr.showDetail(ret);
		}catch(ex){
			console.info(ex);
		}
	},
	check:function(){
		$.post(this.interfaceUrl,{url:this.key},this.callback, "text");
	},
	//移除当前用户的编辑状态
	remove:function(){
		$.post(conflictMgr.interfaceUrl,{url:conflictMgr.key,remove:true});
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
				$(this.conflictBox).css({
					border:'1px inset red',
					color:'red'
				}).html('【请注意】正在编辑该数据的还有：' + html);
			}else{
				$(this.conflictBox).css({
					//border:'1px inset #A9BFD3',
					color:'#000'
				}).html('');
			}
		}
	}
	

};
