(function(){
	var CheckGroupEditor=function(config){
        var field = new Ext.ux.CheckGroupField({editor:this,dataSource:config.dataSource});
        field.on("render",function(){
        	this.el.setStyle({"text-align":"center","line-height":"40px"});
        });
        CheckGroupEditor.superclass.constructor.superclass.constructor.call(this, field, config);   	
    }
    Ext.createWinEditor({name:'CheckGroup',height:150,baseField:Ext.ux.CheckboxGroup,cstr:CheckGroupEditor});
    
})();