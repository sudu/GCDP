/**
*流程图编码和解码
*/
function ccGraphCodec(editor,config){
	this.editor= editor;
	this.config = config;
}
ccGraphCodec.prototype.constructor = ccGraphCodec;
ccGraphCodec.prototype.editor = null;
ccGraphCodec.prototype.jsonStr = null;
ccGraphCodec.prototype.encode = function(){
	var beginCell = this.getBeginCell();
	if(!beginCell) {
		console.info("该流程图未设定开始结点");
		return;
	}
	if(this.editor.cellWindow.form.isVisible()) this.editor.cellWindow.save(); 
	if(this.editor.graphWindow.form.isVisible()) this.editor.graphWindow.save(); 
	var json={};
	var graphCfg=this.editor.graph.property;
	var cellsCfg={};
	var textCellCfg={};
	
	var cells =this.editor.graph.cellCollection;
	for(var id in cells){
		var cell = cells[id];
		var rect = cell.shape.bounds;
		var point = {x:rect.getCenterX(),y:rect.getCenterY()};
		this.editor.graph.alignPoint(point);
		var c = {pos:point,property:Ext.applyDeep({},cell.property)};
		delete c.property.id;
		var type = this.getCellType(cell);
		c.type = type?type:"and";
		if(cell.isTextCell){
			textCellCfg[id] = c;
		}else{
			var children = this.getCellChildren(cell);
			var parent = this.getCellParent(cell);
			if(children.length>0) c.children = children;
			if(parent.length>0) c.parent = parent;
			cellsCfg[id] = c;
		}
	}
	json.graph = graphCfg;
	json.cells = cellsCfg;
	json.text = textCellCfg;
	return json;
};
ccGraphCodec.prototype.decode = function(){
	//try{
		this.editor.graph.removeAllCell();
		
		var json = this.config;
		var graphCfg = json.graph;
		var cellsCfg = json.cells;
		var textCfg = json.text;
		
		var graph = this.editor.graph;
		graph.setProperty(graphCfg);

		//创建cells
		for(var id in cellsCfg){
			var c = cellsCfg[id];
			var type = c.type;
			type = type?type:"and";
			var tpl = Ext.applyDeep({},this.editor.templates[ccConstants.TYPE_TEMPLATE[type]]);
			var n = Ext.applyDeep({},tpl.buttonConfig);
			n.id = parseInt(id.split('#')[1]);
			var cell = graph.createCell(tpl,n,new ccPoint(c.pos.x,c.pos.y));
			cell.setProperty(c.property);
		}
		
		//连线
		var cells = graph.cellCollection;
		for(var id in cellsCfg){
			var c = cellsCfg[id];
			if(!c.children) continue;
			var fromCell = cells[id];
			for(var i=0;i<c.children.length;i++){
				var toCell = cells[c.children[i]];
				graph.connect(fromCell,toCell,true);
			}
		}
			
		//创建文本Cell
		for(var id in textCfg){
			var c = textCfg[id];
			var type = c.type;
			type = type?type:"text";
			var tpl = Ext.applyDeep({},this.editor.templates[ccConstants.TYPE_TEMPLATE[type]]);
			var n = Ext.applyDeep({},tpl.buttonConfig);
			n.id = parseInt(id.split('#')[1]);
			var cell = graph.createCell(tpl,n,new ccPoint(c.pos.x,c.pos.y));
			cell.setProperty(c.property);
		}
	/*	
	}catch(ex){
		console.info("ccGraphCodec decode error;" + ex);
	}
	*/
};
ccGraphCodec.prototype.getBeginCell = function(){
	var cells =this.editor.graph.cellCollection;
	for(var id in cells){
		var c = cells[id];
		if(c.isBeginCell) return c;
	}
	return null;
};
ccGraphCodec.prototype.getCellType = function(cell){
	if(cell.isBeginCell) return "begin";
	if(cell.isEndCell) return "end";
	
	var shapeName = ccUtils.getFunctionName(cell.shape.constructor);
	switch(shapeName){
		case "ccRectangleShape":
			if(cell.children.length>1)
				return "async";
			break;	
		case "ccRhombusShape":
			return "switch";	
		case "ccTextShape":
			return "text";			
		//case:"ccEllipseShape";
			
		default:
			return "and"	
	}
	return null;
};
ccGraphCodec.prototype.getCellChildren = function(cell){
	var children = [];
	for(var i=0;i<cell.children.length;i++){
		children.push("ccCell#" + cell.children[i].objectIdentity);
	}
	return children;
};
ccGraphCodec.prototype.getCellParent= function(cell){
	var parent = [];
	for(var i=0;i<cell.parent.length;i++){
		parent.push("ccCell#" + cell.parent[i].objectIdentity);
	}
	return parent;
};
