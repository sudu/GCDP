Ext.ux.CheckboxGroup=function(cfg){
	cfg.dataSource=cfg.dataSource||cfg.data||cfg.source;//compatible:
    this.addEvents("change");
    Ext.ux.CheckboxGroup.superclass.constructor.call(this,cfg);
}

Ext.extend(Ext.ux.CheckboxGroup,Ext.form.Field, {
	dataSource:''
	,inputList:null
	,name:""
	,value:""
	,labelFirst:false
	,onchange:function(t){
		this.storeValue(t.value);
		this.fireEvent("change");		
	}
	,initComponent:function(){
		Ext.ux.CheckboxGroup.superclass.initComponent.call(this);
	    this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name||"" };
		var s=this.dataSource||this.data||this.source;
		if(Object.prototype.toString.call(s)!="[object Array]"){
			try{
		    	s=eval("("+this.dataSource+")");//[];	
			}catch(e){
				s=[this.dataSource];	//string
			}
		}
		this.dataSource=s;
	}
	,storeValue:function(){
		var i=this.dataSource.length;
		var l=this.inputList;
		var val=[];
		while(i--){
			var input=l[i];
			if(input.checked){
				var v=parseInt(input.value);
				v=isNaN(v)?input.value:v;
				val.push(v);
			}
		}
		this.constructor.superclass.setValue.call(this,val);
	}

	,setValue:function(val){//"1,2,3" or [1,2,3]
		if(val==undefined||val==null)
			val="";
	
		if(Object.prototype.toString.call(val)!="[object Array]"){
			if(typeof val!="string")
				val=val.toString();
			val=val.split(",");
		}
		var i=val.length;
		/*
		while(i--){
			val[i]=val[i].toString();	
		}
		*/
		var l=this.inputList;
		if(l){
			var i=l.length;
			while(i--){
				var input=l[i];
				if(val.indexOf(input.value)>-1)
					input.checked=true;
				else
					input.checked=false;		
			}		
		}
		this.constructor.superclass.setValue.call(this,val);
	}
	,onRender:function(ct,pos){
		this.constructor.superclass.onRender.call(this,ct,pos);		
		var cls="group";
		(this.initialConfig.readOnly&&(cls+=" readOnly"));
		var ctnr=ct.createChild({tag:"span",cls:cls});
		var s=this.dataSource,v=this.value;
		var len,i=len=s.length;
		this.inputList=[];
		while(i--){
			var vs=s[len-i-1];
			vs[0]=vs[0].toString();//convert number to string;
			var cfg={tag:"input",type:"checkbox",value:vs[0]};
			(v.indexOf(vs[0])>-1&&(cfg.checked="checked"));
			(this.initialConfig.readOnly&&(cfg.disabled="disabled"));
			var unit=ctnr.createChild({"tag":"span",cls:"inputUnit"});
			if(this.labelFirst){
				var id="input"+(++Ext.Component.AUTO_ID);
				unit.createChild({tag:"label",for:id,html:vs[1]});
				var input=unit.createChild(cfg);
				this.inputList.push(input.dom);
			}else{
				var input=unit.createChild(cfg);
				this.inputList.push(input.dom);				
				unit.createChild({tag:"label",for:input.id,html:vs[1]});
			}
			input.on("change",this.onchange,this);			
		}
	}	
});

Ext.reg("checkboxgroup",Ext.ux.CheckboxGroup);