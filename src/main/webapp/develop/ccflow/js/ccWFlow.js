/*
* @ ccConstants 
*/
var ccConstants = {
	SHAPE_RECTANGLE: "rectangle",SHAPE_RHOMBUS: "rhombus",SHAPE_ELLIPSE:"ellipse",SHAPE_TEXT:'text',NS_SVG:"http://www.w3.org/2000/svg",CELL_BEGIN:"begin",CELL_END:"end",CELL_SWITCH:"switch",CELL_ASYNC:"async",TYPE_TEMPLATE:{"and":"rectangle","async":"rectangle","switch":"rhombus",begin:"begin",end:"end",text:"text"}
};

/*
 @ccEditor
 a is config
*/
function ccEditor(a) {
	this.config = a;
	this.locked = a.locked==true?true:false;
    if (document.body != null) {
		this.graph = this.createGraph(a.graph.container);
		this.initTextEditor();
		this.templates = this.decodeTemplates(a.templates);
		this.toolbar = this.createToolbar(a.toolbar);
		this.cellWindow = new ccCellWindow(a.propertyEditor.container,this);
		this.graphWindow = new ccGraphWindow(a.propertyEditor.container,this);
		this.graphCodec = new ccGraphCodec(this,a.graph.workflow);
		if(a.graph.workflow){
			this.initWorkflow();
		}else{
			this.initNewWorkflow(a);
		}
		this.bindEvents();
    }
}
ccEditor.prototype.constructor = ccEditor;
ccEditor.prototype.config = null;
ccEditor.prototype.graph = null;
ccEditor.prototype.templates = null;
ccEditor.prototype.toolbar = null;
ccEditor.prototype.locked = !1;//是否锁定，锁定状态流程图不可编辑
ccEditor.prototype.cellWindow = null;//单元属性编辑窗口
ccEditor.prototype.graphWindow = null;//流程图属性编辑窗口
ccEditor.prototype.textEditor = null;//流程图文本标签编辑窗

ccEditor.prototype.createGraph = function(container){
	return new ccGraph(container,this);
};
ccEditor.prototype.bindEvents = function(){
	var doc = Ext.get(document);   
	var clickHandler = function(){
		if(this.graph.clickTimeoutId) clearTimeout(this.graph.clickTimeoutId);
		var arg = arguments;
		this.graph.clickTimeoutId = setTimeout((function(_t,arg) {
			var event = arg[0],target=arg[1];
			var t = _t;
			return function(){
				var elem = Ext.get(target);
				var propertyPanelId = t.config.propertyEditor.container.id;	
				var propertyPane = elem.findParent('#' + propertyPanelId);
				if(!propertyPane) {
					if(t.cellWindow.form.isVisible()) t.cellWindow.save(); 
					if(t.graphWindow.form.isVisible()) t.graphWindow.save(); 
				}
				t.textEditor.ownerCt.isDisplayed() && t.textEditor.save();
				t.graph.clickTimeoutId=null;
			}
		})(this,arg),200);
		window.focus();
	};	
	doc.on('click', clickHandler ,this);
};

ccEditor.prototype.createToolbar = function(config) {
	var toolbar = new ccToolbar(config.container,this);
	var btns = config.buttons;
	for(var i=0;i<btns.length;i++){
		var n = btns[i];
		var o = this.templates[n.template];
		o.buttonConfig = n;		
		if(n.visible===false) continue;
		toolbar.addItem.call(toolbar,n.text, n.icon,n,o);
	}
	return toolbar;
};
ccEditor.prototype.decodeTemplates = function(config) {
	if(!config)return;
	var tpls = {};
	for(var key in config){
		var item = config[key];
		tpls[key] = item;
	}
	return tpls;
};
ccEditor.prototype.initNewWorkflow = function(a){//新建流程图
	this.graph.setProperty(a.graph.property);

	var btns = a.toolbar.buttons;
	var c = this.graph.container;
		
	//创建结束cell	
	n = btns[btns.length-1];
	var o = this.templates[n.template];
	x = c.getWidth() - 100,y=c.getHeight() - 60;
	this.graph.createCell.call(this.graph,o,n,new ccPoint(x,y));
	
	//创建 开始cell
	n =btns[0];
	var o = this.templates[n.template];
	var x = 50,y=50;
	
	this.graph.createCell.call(this.graph,o,n,new ccPoint(x,y));

		
};
ccEditor.prototype.initWorkflow = function(){//初始化流程图
	this.graphCodec.decode();
};
ccEditor.prototype.initTextEditor = function(){//初始化流程图
	this.textEditor = new ccTextWindow(this);
};

function ccGraph(container,editor) {
	this.ownerCt = container;
	this.editor = editor;
	this.property={};
	this.container = container.body.first();
	this.init();
}
ccGraph.prototype.constructor = ccGraph;
ccGraph.prototype.cellCollection = null;
ccGraph.prototype.property = null;
ccGraph.prototype.container = null;
ccGraph.prototype.ownerCt = null;
ccGraph.prototype.paper = null;
ccGraph.prototype.editor = null;
ccGraph.prototype.validator = null;
ccGraph.prototype.cellRenderer = null;
ccGraph.prototype.insertGradientNodeId = "cc-gradient-c3d9ff-white-south";
ccGraph.prototype.insertGradientNodeId2 = "cc-gradient-FFCF8A-white-south";
ccGraph.prototype.isConnectDrag=!1;
ccGraph.prototype.gridSize= 10;//画布网格尺寸
ccGraph.prototype.titleElement=null;
ccGraph.prototype.authorElement=null;
ccGraph.prototype.createTimeElement=null;
ccGraph.prototype.init = function(){
	//创建画布
	var c = this.container;
	this.paper = Raphael(c.id, 2560, 1600);
	this.createSvgGradient(this.insertGradientNodeId,"#C3D9FF","white");
	this.createSvgGradient(this.insertGradientNodeId2,"#FFCF8A","white");
	this.createGraphInfoElement();
	this.cellRenderer = new ccCellRenderer();
	this.graphSelection = new ccGraphSelection(this);
	this.validator = new ccValidator(this);
	
	this.bindEvents();
	this.cellCollection={};
};
ccGraph.prototype.removeAllCell = function(){
	var cells =this.editor.graph.cellCollection;
	for(var id in cells){
		var cell = cells[id];
		cell.destroy();
		cell=null;delete cell;
	}
}
ccGraph.prototype.bindEvents = function(){
	new Ext.dd.DDTarget(this.container, 'ccWFlow');
	
	this.container.on("click",function(e,obj){
		if(this.clickTimeoutId){
			clearTimeout(this.clickTimeoutId);
		}
		this.editor.graphWindow.setGraph(this);
		this.graphSelection.clear();
		window.focus();
	},this);
	
};
ccGraph.prototype.createGraphInfoElement = function(){ 
	if(!this.titleElement){
		var p = {x:this.container.getWidth()/2,y:20};
		this.alignPoint(p);
		var titleEl = this.paper.text(p.x,p.y,"我的脚本流程");
		titleEl.attr({"font-size":"18px"});
		this.titleElement= titleEl;
	}
	if(!this.authorElement){
		var p = {x:this.container.getWidth()-90,y:30};
		this.alignPoint(p);
		var authorEl = this.paper.text(p.x,p.y,"作者");
		authorEl.attr({"font-size":"10px"});
		this.authorElement= authorEl;
	}	
};
//往画布添加一个图形单元
ccGraph.prototype.createCell = function(template,config,point){
	this.alignPoint(point);
	var cfg ={};
	Ext.applyDeep(cfg,template);
	cfg.x = point.x;
	cfg.y = point.y;
	cfg.cellConfig = config;
	var cell = new ccCell(cfg,this);
	return cell;
};
ccGraph.prototype.createSvgGradient = function(id, b, c) {
	var d = document.createElementNS(ccConstants.NS_SVG, "linearGradient");
	d.setAttribute("id", id);
	d.setAttribute("x1", "0%");
	d.setAttribute("y1", "0%");
	d.setAttribute("x2", "0%");
	d.setAttribute("y2", "100%")
	a = document.createElementNS(ccConstants.NS_SVG, "stop");
	a.setAttribute("offset", "0%");
	a.setAttribute("style", "stop-color:" + b);
	d.appendChild(a);
	a = document.createElementNS(ccConstants.NS_SVG, "stop");
	a.setAttribute("offset", "100%");
	a.setAttribute("style", "stop-color:" + c);
	d.appendChild(a);
	
	this.paper.canvas.appendChild(d);

};
//转换为相对画布坐标
ccGraph.prototype.translatePoint = function(point){
	var ctx = this.container.getX(),cty=this.container.getY();
	point.x -= ctx;
	point.y -= cty;
};
//微调坐标点使之对齐网格 
ccGraph.prototype.alignPoint = function(point) {
	var size = this.gridSize;
	point.x = Math.round(point.x/size) * size;
	point.y = Math.round(point.y/size) * size;
};
ccGraph.prototype.connect = function(fromCell,toCell,firstInit){
	if(!firstInit){
		if(fromCell==toCell || fromCell.findChild(toCell) || fromCell.findParent(toCell)) {
			Ext.CMPP.warn("操作受限","这两个结点是有连接的。");
			return;
		}
		if(fromCell.chilrenLimit!=-1 && fromCell.children.length>=fromCell.chilrenLimit) {
			Ext.CMPP.warn("操作受限","该结点限制子节点数为" + fromCell.chilrenLimit);
			return;
		}
		if(toCell.parentLimit!=-1 && toCell.parentLimit.length>=toCell.parentLimit) {
			Ext.CMPP.warn("操作受限","目标结点限制父节点数为" + toCell.parentLimit);
			return;
		}
		var validateResult = this.validator.validate(fromCell,toCell);
		if(validateResult){
			Ext.CMPP.warn("操作受限",validateResult);
			return;
		}
	}
	
	var connectObj = this.paper.drawConnect({ fromShape: fromCell.shape, toShape: toCell.shape });
	//todo 设置connectLine的样式
	connectObj.connectLine.attr({"stroke":"#36393D","stroke-width":1,"cursor":"move"});
	connectObj.connectLine.node.setAttribute("pointer-events","visibleStroke");
	connectObj.connectLineForSel.attr({"stroke-width":9,"cursor":"move"});//宽度够宽时鼠标点击选择容易选中
	connectObj.connectLineForSel.node.setAttribute("visibility","hidden");
	connectObj.connectLineForSel.node.setAttribute("pointer-events","stroke");
	
	fromCell.children.push(toCell);
	toCell.parent.push(fromCell);
	fromCell.fromConnectLines.push(connectObj);
	toCell.toConnectLines.push(connectObj);
	connectObj.fromCell = fromCell;
	connectObj.toCell = toCell;
	
	connectObj.connectLineForSel.click((function(cnObj,t){
		var _t = t;
		var _connectObj = cnObj;
		return function(e){
			_t.graphSelection.select(_connectObj);
			e.cancelBubble = true;
			e.stopPropagation();
		}
	})(connectObj,this));
	
	//连接后验证  todo
	
	if(!firstInit){
		var validateResult = this.validator.validateAfter(fromCell,toCell);
		if(validateResult){
			Ext.Msg.confirm("可疑操作",validateResult+"<br>确定要连接吗?",function(btn){
				if(btn!=='yes'){
					this.disConnect(connectObj);
				}
			},this);
			return;
			//Ext.CMPP.warn("操作受限",validateResult);
		}
	}

	return connectObj;
};
//删除连接线
ccGraph.prototype.disConnect = function(connectObj){
	var line = connectObj;
	line.connectLine.remove();
	line.connectLineForSel.remove();
	
	for(var j=0;j<line.fromCell.fromConnectLines.length;j++){
		if(line==line.fromCell.fromConnectLines[j]){
			line.fromCell.fromConnectLines.splice(j,1);
			break;
		}
	}
	for(var j=0;j<line.toCell.toConnectLines.length;j++){
		if(line==line.toCell.toConnectLines[j]){
			line.toCell.toConnectLines.splice(j,1);
			break;
		}
	}			
	for(var j=0;j<line.fromCell.children.length;j++){
		if(line.fromCell.children[j]==line.toCell){
			line.fromCell.children.splice(j,1);
			break;
		}
	}
	for(var j=0;j<line.toCell.parent.length;j++){
		if(line.toCell.parent[j]==line.fromCell){
			line.toCell.parent.splice(j,1);
			break;
		}
	}
	line=null;delete line;	
};

ccGraph.prototype.setProperty = function(o) {
	if(typeof o==="object"){
		this.updateLabel(o);
		Ext.applyDeep(this.property,o);
	}
};
ccGraph.prototype.updateLabel = function(o) {
	var p = this.property;
	if(o.title && p.title!==o.title){
		this.titleElement.attr("text",o.title);
	}
	if(o.author && p.author!==o.author){
		var text = "created by "+ o.author +" at " + o.createTime;
		this.authorElement.attr("text",text);
		var width = this.authorElement.getBBox().width;
		this.authorElement.attr("x",this.container.getWidth()-width/2 - 5);
	}	
};
ccGraph.prototype.getCellByPoint = function(clientX,clientY) {
//寻找可以连接的元素
	var selectedEl = this.paper.getElementByPoint(clientX,clientY);
	if(selectedEl){
		var index = selectedEl.data("index");
		var selectedCell = this.cellCollection["ccCell#" + index];
		return selectedCell;
	}else{
		return null;
	}	
};	

/*
*流程图合法性验证
*/
function ccValidator(graph) {
	this.graph = graph;
}
ccValidator.prototype.constructor = ccValidator;
ccValidator.prototype.validateText1 = "异步活动分支里不能再嵌套异步活动，只能是顺序活动。";
ccValidator.prototype.validateText2 = "异步活动分支里不能有条件判断。建议在脚本内部增加条件判断。";
ccValidator.prototype.validateText3 = "条件不能直接连接条件";
ccValidator.prototype.validateText4 = "各分支之间不能互连";
ccValidator.prototype.validateText5 = "条件结点不能直接结束";
ccValidator.prototype.validateText6 = "条件内的结点不能相互连接";
ccValidator.prototype.validate = function(fromCell,toCell){
	if(this.validate5(fromCell,toCell)){
		return this.validateText5;
	}
	if(this.validate3(fromCell,toCell)){
		return this.validateText3;
	}
	if(this.validate1(fromCell,toCell)){
		return this.validateText1;
	}
	
	return null;
};
/*
*连线后验证
*/
ccValidator.prototype.validateAfter = function(fromCell,toCell){
	if(this.validate2(fromCell,toCell)){
		return this.validateText2;
	}	
	if(this.validate4(fromCell,toCell)){
		return this.validateText4;
	}	
		
	
	return null;
};
ccValidator.prototype.validate1 = function(fromCell,toCell){
	if(this.isCellInAsync(fromCell)){
		if(fromCell.children.length>0){
			return this.validateText1;
		}
	}
};
ccValidator.prototype.validate2 = function(fromCell,toCell){
	return this.isCellInAsync(fromCell) && this.getCellType(toCell)==="switch"
};
ccValidator.prototype.validate3 = function(fromCell,toCell){
	return this.getCellType(fromCell)==="switch" && this.getCellType(toCell)==="switch";
};
ccValidator.prototype.validate4 = function(fromCell,toCell){
	return this.isCellInAsync(fromCell) && toCell.parent.length>0 && this.getCellType(toCell.parent[0])==="async"
};
ccValidator.prototype.validate5 = function(fromCell,toCell){
	return this.getCellType(fromCell)==="switch" && this.getCellType(toCell)==="end";
};
ccValidator.prototype.validate6 = function(fromCell,toCell){
	
};
ccValidator.prototype.getCellType = function(cell){
	return this.graph.editor.graphCodec.getCellType(cell);
};
/*判断结点是否在条件分支内*/
ccValidator.prototype.isCellInSwitch = function(cell){
	var c = cell;
	while(c.parent.length==1 && this.getCellType(c)!=="async" && this.getCellType(c)!=="switch"){
		c = c.parent[0];
		if(this.getCellType(c)==="switch"){
			return c;
		}
	}
	return false;
};
/*判断结点是否在异步活动分支内*/
ccValidator.prototype.isCellInAsync = function(cell){
	var c = cell;
	while(c.parent.length==1 && this.getCellType(c)!=="async" && this.getCellType(c)!=="switch"){
		c = c.parent[0];
		if(this.getCellType(c)==="async"){
			return c;
		}
	}
	return false;
};

function ccToolbar(container,editor){
	this.ownerCt = container;
	this.container = container.body.first();
	this.editor = editor;
}
ccToolbar.prototype.constructor = ccToolbar;
ccToolbar.prototype.editor = null;
ccToolbar.prototype.container = null;
ccToolbar.prototype.ownerCt = null;
//ccToolbar.prototype.buttonTpl='<img class="ccToolbarMode" width="32" height="32" src="{icon}" title="{tooltip}" />';
ccToolbar.prototype.addItem = function(text,icon,config,tpl){
	var itemEl = this.container.createChild({
		tag:'img',
		title:text,
		src:icon,
		cls:'ccToolbarMode'
	});
	itemEl.template = tpl;
	itemEl.config = config;
	this.bindDragEvent(itemEl);
};
ccToolbar.prototype.bindDragEvent = function(el){
	var prox = new Ext.dd.DragSource(el,{
		group:'ccWFlow'
	});
	//prox.centerFrame =false;
	prox.afterDragEnter = function(target,e){
		this.deltaX = 28 ;//todo
		this.deltaY = 20;
	};
	prox.afterDragDrop = (function(toolbar,template,config){
		var _t=toolbar;
		var _tpl = template;
		var _config = config;
		return function(target, e, id){
			_t.drop.call(_t,target, e, id,_tpl,_config);
		}
	})(this,el.template,el.config)
};
ccToolbar.prototype.drop = function(target, e, id,template,config) {
	if (target.id != id) return; 
	var graph = this.editor.graph;
	var p  = new ccPoint(e.xy[0]+graph.container.dom.scrollLeft,e.xy[1]+graph.container.dom.scrollTop);
	graph.translatePoint(p);
	var cell = graph.createCell(template,config,p);
	cell.shape.element.events[1].f.call(cell,e);//相应click事件
};

function ccCell(cfg,graph){
	if(typeof cfg.cellConfig.id !=="undefined"){
		this.objectIdentity = cfg.cellConfig.id;
		if(this.objectIdentity>=ccObjectIdentity.counter){
			ccObjectIdentity.counter = this.objectIdentity + 1;
		}
	}else{
		this.objectIdentity = ccObjectIdentity.get();
	}
	this.graph = graph;
	this.label = cfg.cellConfig.text;
	this.shape = cfg.shape;
	this.property = {};
	
	var btnCfg = cfg.cellConfig;
	if(typeof btnCfg.connectable !=="undefined") this.connectable = btnCfg.connectable;
	if(typeof btnCfg.beConnectable !=="undefined") this.beConnectable = btnCfg.beConnectable;
	if(typeof btnCfg.isBeginCell !=="undefined") this.isBeginCell = btnCfg.isBeginCell;
	if(typeof btnCfg.isEndCell !=="undefined") this.isEndCell = btnCfg.isEndCell;
	if(typeof btnCfg.chilrenLimit !=="undefined") this.chilrenLimit = btnCfg.chilrenLimit;
	if(typeof btnCfg.parentLimit !=="undefined") this.parentLimit = btnCfg.parentLimit;
	if(typeof btnCfg.identifyVisible !=="undefined") this.identifyVisible = btnCfg.identifyVisible;
	if(typeof btnCfg.isTextCell !=="undefined") this.isTextCell = btnCfg.isTextCell;
	
	this.init(cfg);
}
ccCell.prototype.graph = null;
ccCell.prototype.label = null;
ccCell.prototype.property = null;
ccCell.prototype.shape = null;
ccCell.prototype.labelElement = null;
ccCell.prototype.identifyElement = null;
ccCell.prototype.connectElement = null;
ccCell.prototype.elementSet = null;
ccCell.prototype.parent = null;
ccCell.prototype.children = null;
ccCell.prototype.dragBounds = null;//拖拽时的边框
ccCell.prototype.dragConnectLine = null;//拖拽时的连接线
ccCell.prototype.dragTempBoundsElement = null;//鼠标进入元素内部时显示的边框
ccCell.prototype.startDragX = null;
ccCell.prototype.startDragY = null;
ccCell.prototype.objectIdentity = null;
ccCell.prototype.fromConnectLines = null;//从我这里连出去的线
ccCell.prototype.toConnectLines = null;//连到我这里的线
ccCell.prototype.connectable = !0;//可连接
ccCell.prototype.beConnectable = !0;//可被连接
ccCell.prototype.identifyVisible = !0;//序号是否可见
ccCell.prototype.isTextCell = !1;//是否是text标注
ccCell.prototype.isBeginCell = !1;//是否是开始节点
ccCell.prototype.isEndCell = !1;//是否是结束节点
ccCell.prototype.chilrenLimit = -1;//儿子数限制
ccCell.prototype.parentLimit = -1;//父亲数限制
ccCell.prototype.lineMaxCharNum = 5;//标签处单行最大显示的字符数，这决定是否这行显示
ccCell.prototype.init = function(cfg) {
	this.children=[];
	this.parent=[];
	this.fromConnectLines=[];
	this.toConnectLines=[];
	if(!this.shape){
		var p =this.graph.paper;
		p.setStart();
		this.shape = this.graph.cellRenderer.createShape.call(this.graph.cellRenderer,cfg,this.graph);
		this.shape.element.node.setAttribute("pointer-events","all");
		this.shape.element.data("index",this.objectIdentity);
		
		if(this.identifyVisible!=false && !this.identifyElement){
			//创建序号
			var el = p.text(cfg.x+5,cfg.y+5,this.objectIdentity);
			el.attr({"cursor":"move","font-size":"11px"});
			el.node.setAttribute("pointer-events","none");
			this.identifyElement = el;
		}
		
		if(!this.labelElement){
			//创建label
			var labelEl = p.text(cfg.x,cfg.y,this.label);
			labelEl.attr({"cursor":"move","font-size":"12px"});
			labelEl.node.setAttribute("pointer-events","none");
			labelEl.data("index",this.objectIdentity);
			this.labelElement = labelEl;
			this.setLabelCenterAlign();
		}
		if(this.connectable && !this.connectElement){
			//创建连接控制点
			var b = this.shape.bounds;
			var size = 16;
			var cnnEl = p.image("ccflow/images/connector.gif",b.getCenterX()-size/2,b.getCenterY()-size/2,size,size);
			cnnEl.attr({"cursor":"pointer"});
			cnnEl.node.setAttribute("pointer-events","stroke");
			cnnEl.data("index",this.objectIdentity);
			cnnEl.node.setAttribute("display","none");
			this.connectElement = cnnEl;
		}
		this.elementSet = p.setFinish();
		this.bindEvents();
		
		this.graph.cellCollection["ccCell#" + this.objectIdentity]=this;
		this.setProperty({
			id:"ccCell#" + this.objectIdentity,
			label:this.label
		});
	}

};
ccCell.prototype.setProperty = function(o) {
	if(typeof o==="object"){
		Ext.applyDeep(this.property,o);
		if(typeof o.label==="string" && o.label!=this.label) this.updateLabel(o.label);
	}
};
ccCell.prototype.setGradient = function(gradientId) {
	this.shape.element.node.setAttribute("fill","url(#"+ gradientId +")");
};

ccCell.prototype.updateLabel = function(text) {
	this.label = text;
	text = ccUtils.autoLineBreak(text,this.lineMaxCharNum);
	this.labelElement.attr("text",text);
};
ccCell.prototype.restoreConnectElement = function(cfg) {
	if(!this.connectElement) return;
	var b = this.shape.bounds;
	var size = 16;
	var x=b.getCenterX()-size/2,y=b.getCenterY()-size/2;
	this.connectElement.attr({x:x,y:y});
	this.connectElement.node.setAttribute("display","none");
};
//坐标点合法性检查
ccCell.prototype.validatePositon = function(a) {
	var paper = this.graph.paper;
	if(a.x<0) a.x=0;
	if(a.y<0) a.y=0;
	if(a.x>paper.width-a.width) a.x=paper.width-a.width;
	if(a.y>paper.height-a.height) a.y=paper.height-a.height;
};
ccCell.prototype.setLabelCenterAlign = function() {
	var b = this.shape.bounds;
	var x = b.getCenterX();
	var y = b.getCenterY();
	this.labelElement.attr({x:x,y:y});
	
};
ccCell.prototype.setShape = function(o) {
	this.shape = o;
};
ccCell.prototype.setLabelElement = function(o) {
	this.labelElement = o;
};
ccCell.prototype.dragStartHandler = function(x,y,e) {
	this.startDragX = x;
	this.startDragY = y;
	
};
ccCell.prototype.redawConnectLine = function() {//重画连接线
	var lines = this.fromConnectLines;
	var paper = this.graph.paper;
	for(var i=0;i<lines.length;i++){
		paper.drawConnect(lines[i]);
	}
	lines = this.toConnectLines;
	for(var i=0;i<lines.length;i++){
		paper.drawConnect(lines[i]);
	}
};
ccCell.prototype.dragMoveHandler = function(a,b,x,y,e) {
	if(this.dragBounds==null) {
		this.dragBounds=this.createDragBounds();
	}
	if(this.dragBounds.node.attributes["visibility"]!=="visible") this.dragBounds.node.setAttribute("visibility","visible");
	
	var offsetX = x-this.startDragX;
	var offsetY = y-this.startDragY;
	x = this.shape.bounds.x + offsetX;
	y = this.shape.bounds.y + offsetY;
	var p ={x:x,y:y};
	this.graph.alignPoint.call(this.graph,p);
	this.dragBounds.attr({x:p.x,y:p.y});
	
};
ccCell.prototype.dragEndHandler = function(e) {
	if(this.dragBounds==null) return;
	
	var offsetX = e.clientX-this.startDragX;
	var offsetY = e.clientY-this.startDragY;
	this.move(offsetX,offsetY);
	this.dragBounds.remove();
	delete this.dragBounds;
	this.startDragX=0;
	this.startDragY=0;
	//重新选中
	//this.shape.element.events[1].f.call(this,e);//相应click事件
	this.graph.graphSelection.select(this);
	//重画连接线
	this.redawConnectLine();
};
ccCell.prototype.createDragBounds = function() {
	var b = this.shape.bounds;
	var dragBounds = this.graph.paper.rect(b.x,b.y,b.width,b.height);
	var n=dragBounds.node;
	n.setAttribute("visibility","hidden");
	n.setAttribute("stroke","black");
	n.setAttribute("fill","none");
	n.setAttribute("shape-rendering","crispEdges");
	n.setAttribute("stroke-width","1");
	n.setAttribute("stroke-dasharray","3 3");
	
	return dragBounds;
};
ccCell.prototype.move = function(offsetX,offsetY) {
	var el = this.shape;
	var r = new ccRectangle(el.x + offsetX,el.y + offsetY,el.width,el.height);
	this.validatePositon(r);
	this.graph.alignPoint.call(this.graph,r);
	el.redraw(r);
	this.setLabelCenterAlign();
	this.identifyElement && this.identifyElement.attr({x:r.x+5,y:r.y+5});
	var size = 16;
	if(this.connectElement) this.connectElement.attr({x:r.getCenterX()-size/2,y:r.getCenterY()-size/2});
	r=null;	
};
ccCell.prototype.bindEvents = function() {
	var el = this.shape.element,labelEl = this.labelElement,connEl = this.connectElement;
	el.drag(this.dragMoveHandler, this.dragStartHandler, this.dragEndHandler,this,this,this);
	//labelEl.drag(this.dragMoveHandler, this.dragStartHandler, this.dragEndHandler,this,this,this);
	
	var clickEvent = function(cell,type){
		var _t = cell;
		var _type = type;
		return function(e,x,y){
			e.cancelBubble = true;
			e.stopPropagation();
			_t.clickType = _type;
			clearTimeout(_t.clickTimer);
			//console.info(type + "clearTimeout" + _t.clickTimer);//cds
			if(_type==1){
				_t.clickTimer = setTimeout(function(_cell,_e,_x,_y){
					var cell = _cell,e = _e,x = _x ; y=_y;
					return function(){
						cell.clickHandler(e,x,y);
					}
				}(_t,e,x,y),300); 
				//console.info(type + "setTimeout" + _t.clickTimer);//cds
			}
			if(_type==2){
				//console.info(_type + "clearTimeout" + _t.clickTimer);//cds
				clearTimeout(_t.clickTimer);
				_t.clickHandler(e,x,y);
			}
		}
	}
		
	if(!this.isBeginCell && !this.isEndCell){
		el.click(clickEvent(this,1));
		el.dblclick(clickEvent(this,2));

		//if(connEl) connEl.click(clickHandler(this));
	}
	
	var mouseoverHandler = function(_t){
		var t = _t;
		return function(){
			if(t.mouseoverTimeoutId) clearTimeout(t.mouseoverTimeoutId);
			t.mouseoverTimeoutId = setTimeout((function(cell){
				var _t = cell;
				return function(){
					if(_t.connectElement && !_t.graph.isConnectDrag)_t.connectElement.node.setAttribute("display","block");//显示连接线头
					//拖拽连接线头时触发
					if(_t.beConnectable &&  _t.graph.isConnectDrag && _t.graph.isConnectDrag!=_t){
						_t.createTempCellBounds();
					}
				}
			})(t),50);
		}
	}(this);
	
	var mouseoutHandler = function(_t){
		var t = _t;
		return function(e){
			if(t.mouseoutTimeoutId) clearTimeout(t.mouseoutTimeoutId);
			if(t.mouseoverTimeoutId) clearTimeout(t.mouseoverTimeoutId);
			t.mouseoutTimeoutId = setTimeout((function(cell,_e){
				var _t = cell;
				var e = _e;
				return function(){
					var cn =  _t.connectElement!=null?_t.connectElement.node:null;
					var node = e.relatedTarget;
					if(node){
						while(node.nodeType!= 1)   node = node.parentNode; 	
						if(node==cn || node==_t.labelElement.node || _t.labelElement.node == node.parentNode) {
							return;
						}
					}
					if(cn && !_t.graph.isConnectDrag)cn.setAttribute("display","none");
					//拖拽连接线头时触发
					if(_t.graph.isConnectDrag){
						_t.dragTempBoundsElement!=null && _t.dragTempBoundsElement.remove();
						_t.dragTempBoundsElement=null;
					}
				}
			})(t,e),20);
		}
	}(this);
	
	//el.hover(mouseoverHandler,mouseoutHandler,this.this);connEl && connEl.hover(mouseoverHandler,mouseoutHandler,this.this);
	
	el.mouseover(mouseoverHandler);//labelEl.mouseover(mouseoverHandler);if(connEl)connEl.mouseover(mouseoverHandler);
	el.mouseout(mouseoutHandler);//labelEl.mouseout(mouseoutHandler);if(connEl)connEl.mouseout(mouseoutHandler);
	
	if(connEl) connEl.drag(this.dragConnectMoveHandler, this.dragConnectStartHandler, this.dragConnectEndHandler,this,this,this);
	
	
};
ccCell.prototype.clickHandler = function(e,x,y) {
	clearTimeout(this.graph.clickTimeoutId);
	window.focus();
	this.connectElement && this.connectElement.node.setAttribute("display","none");
	this.graph.editor.textEditor.ownerCt.isDisplayed() && this.graph.editor.textEditor.save();
	var type = this.clickType;
	if(type==1){
		//单击事件
		this.singleClickHandler(e,x,y);
	} else if(type==2){
		//双击事件
		this.dblClickHandler(e,x,y);
	}
	e.cancelBubble = true;
	e.stopPropagation();
};
ccCell.prototype.singleClickHandler = function(e,x,y) {
	//console.info("singleClickHandler");//cds
	if(!this.isTextCell){
		var cw = this.graph.editor.cellWindow;
		cw.setCell.call(cw,this);
	}
	this.graph.graphSelection.select(this);
	window.focus();
};
ccCell.prototype.dblClickHandler = function(e,x,y) {
	//console.info("dblClickHandler");//cds
	this.graph.graphSelection.clear();
	
	var editor = this.graph.editor.textEditor;
	editor.cell = this;
	editor.show();
	
};
ccCell.prototype.dragConnectStartHandler = function(x,y,e) {

};
ccCell.prototype.dragConnectMoveHandler = function(a,b,x,y,e) {
	x += this.graph.container.dom.scrollLeft;
	y += this.graph.container.dom.scrollTop;
	this.graph.isConnectDrag = this;
	if(!this.dragConnectLine) {
		this.dragConnectLine=this.createDragConnectLine(x,y);
	}
	var p = new ccPoint(x,y);
	this.graph.translatePoint(p);
	this.connectElement.attr({x:p.x+16,y:p.y+16});
	if(this.dragConnectLine){
		var startx = this.shape.bounds.getCenterX(),starty = this.shape.bounds.getCenterY();
		this.dragConnectLine.attr("path","M" + startx + " " + starty +"L" + p.x + " " + p.y);
	}
};
//拖拽结束
ccCell.prototype.dragConnectEndHandler = function(e) {	
	if(!this.dragConnectLine) return;
	//归位
	this.restoreConnectElement();
	//删除指导线
	this.dragConnectLine.remove();this.dragConnectLine=null;
	this.graph.isConnectDrag = !1;
	//寻找可以连接的元素
	var selectedCell = this.graph.getCellByPoint(e.clientX,e.clientY);
	if(selectedCell && selectedCell!=this){
		if(selectedCell.beConnectable){
			var connectObj = this.graph.connect.call(this.graph,this,selectedCell);	
			connectObj && this.graph.graphSelection.select(connectObj);	
		}
		selectedCell.dragTempBoundsElement!=null && selectedCell.dragTempBoundsElement.remove();
		selectedCell.dragTempBoundsElement = null;
		
		if(selectedCell.connectElement)selectedCell.connectElement.node.setAttribute("display","none");//显示连接线头
	}

};
ccCell.prototype.createDragConnectLine = function(x,y) {	
	var p = new ccPoint(x,y);
	this.graph.translatePoint(p);
	var startx = this.shape.bounds.getCenterX(),starty = this.shape.bounds.getCenterY();
	var connectLine = this.graph.paper.path("M" + startx + " " + starty +"L" + p.x + " " + p.y);
	connectLine.attr({"stroke":"#00FF00","fill":"none","stroke-width":"3"});
	var n = connectLine.node;
	n.setAttribute("shape-rendering","crispEdges");
	n.setAttribute("stroke-dasharray","9 9");
	n.setAttribute("pointer-events","none");
	
	return connectLine;
};
ccCell.prototype.createTempCellBounds = function(){
	var el = this.dragTempBoundsElement;
	if(!el){	
		var b = this.shape.bounds.clone();
		b.grow(2);
		el = this.graph.paper.rect(b.x,b.y,b.width,b.height);
		var n=el.node;
		n.setAttribute("stroke","#00FF00");
		n.setAttribute("fill","none");
		n.setAttribute("shape-rendering","crispEdges");
		n.setAttribute("stroke-width","3");
		n.setAttribute("cursor","default");
		n.setAttribute("pointer-events","none");
		this.dragTempBoundsElement = el;
	}
};
ccCell.prototype.findChild = function(cell){
	return cell.findParent(this);
};
ccCell.prototype.findParent= function(cell){
	var ret = false;
	for(var i=0;i<this.parent.length;i++){
		if(this.parent[i]==cell){
			ret = true;
			break;
		}
	}
	if(!ret){
		for(var i=0;i<this.parent.length;i++){
			if(this.parent[i].findParent(cell)){
				return true;
			}
		}
	}else{
		return true;
	}
};
ccCell.prototype.destroy= function(cell){
	var lines = this.fromConnectLines;
	for(var i =lines.length-1;i>=0;i--){
		lines[i].connectLine.remove();
		lines[i].connectLineForSel.remove();
	}
	lines = this.toConnectLines;
	for(var i =lines.length-1;i>=0;i--){
		lines[i].connectLine.remove();
		lines[i].connectLineForSel.remove();
	}
	
	for(var i =this.parent.length-1;i>=0;i--){
		var c = this.parent[i];
		for(var j=0;j<c.children.length;j++){
			if(c.children[j]==this){
				c.children.splice(j,1);
				break;
			}
		}		
	}
	for(var i =this.children.length-1;i>=0;i--){
		var c = this.children[i];
		for(var j=0;j<c.parent.length;j++){
			if(c.parent[j]==this){
				c.parent.splice(j,1);
				break;
			}
		}		
	}
	
	delete this.graph.cellCollection["ccCell#" + this.objectIdentity];
	this.elementSet.remove();	
	//关闭编辑窗口
	this.graph.editor.cellWindow.hide();
};
/*
*	@ccCellRenderer
*/
function ccCellRenderer() {
    this.shapes = Ext.applyDeep({},this.defaultShapes);
}
ccCellRenderer.prototype.shapes = null;
ccCellRenderer.prototype.defaultShapes = {};
ccCellRenderer.prototype.defaultShapes[ccConstants.SHAPE_RECTANGLE] = ccRectangleShape;
ccCellRenderer.prototype.defaultShapes[ccConstants.SHAPE_RHOMBUS] = ccRhombusShape;
ccCellRenderer.prototype.defaultShapes[ccConstants.SHAPE_ELLIPSE] = ccEllipseShape;
ccCellRenderer.prototype.defaultShapes[ccConstants.SHAPE_TEXT] = ccTextShape;
ccCellRenderer.prototype.createShape = function(a,graph) {
	var b = this.getShapeConstructor(a.shapeType);
	this.tranformPosition(a,graph.paper);//调整位置，保证鼠标点在元素边框中间
	var shape = new b(a,graph);
	return shape;
};
ccCellRenderer.prototype.getShapeConstructor = function(shapeType) {
    return this.shapes[shapeType]
};
ccCellRenderer.prototype.tranformPosition = function(a,paper) {
    a.x -= a.width/2;
	a.y -= a.height/2;
	if(a.x<0) a.x=0;
	if(a.y<0) a.y=0;
	if(a.x>paper.width-a.width) a.x=paper.width-a.width;
	if(a.y>paper.height-a.height) a.y=paper.height-a.height;
	
};

function ccGraphSelection(graph){
	this.cells=[];
	this.graph = graph;
	this.init();
}
ccGraphSelection.prototype.constructor = ccGraphSelection;
ccGraphSelection.prototype.cells = null;
ccGraphSelection.prototype.graph = null;
ccGraphSelection.prototype.keyNav = null;
ccGraphSelection.prototype.singleSelection = !1;

ccGraphSelection.prototype.init = function(){
	//初始化快捷键功能
	var nav = new Ext.KeyMap(Ext.getBody(), [{
		key: [Ext.EventObject.DELETE,Ext.EventObject.BACKSPACE],//DELETE\BACKSPACE
		fn: function(key,e){
			if(e.target && (e.target.tagName.toLowerCase()=="body" || e.target.tagName.toLowerCase()=="svg" || Ext.fly(e.target).parent("svg")!=null)){
				this.deleteSelected();
			}else{
				return true;
			}
		},
		scope : this
	}]);
	//nav.disable();	
	this.keyNav = nav;

};
ccGraphSelection.prototype.select = function(cell){
	if(this.singleSelection){
		this.clear();
	}
	if(this.indexOf(cell)!==-1) return ;
	
	var model = new ccGraphSelectionModel(cell,this.graph);
	this.cells.push(model);
	
	this.selectionChange();
};
ccGraphSelection.prototype.remove = function(cell){
	var index = this.indexOf(cell);
	if(index!==-1) return ;
	this.cells[index].destory();
	this.cells.splice(index,1);
	this.selectionChange();
};
ccGraphSelection.prototype.indexOf = function(cell){
	for(var i=this.cells.length-1;i>=0;i--){
		if(this.cells[i].cell == cell){
			return i;
		}
	}
	return -1;
};
ccGraphSelection.prototype.selectionChange = function(){
	if(this.cells.length==1){
		this.singleSelection = true;
	}
	if(this.cells.length>0){
		this.keyNav.enable();
	}else{
		this.keyNav.disable();
	}
};
ccGraphSelection.prototype.clear = function(){
	for(var i=this.cells.length-1;i>=0;i--){
		this.cells[i].destroy();
		this.cells.splice(i,1);
	}
	this.selectionChange();
};
ccGraphSelection.prototype.deleteSelected = function(){
	if(this.graph.editor.locked) return;
	for(var i=this.cells.length-1;i>=0;i--){
		var cell = this.cells[i].cell;
		var cellName = ccUtils.getFunctionName(cell.constructor);
		if(cellName==="ccCell"){
			cell.destroy();
			cell=null;delete cell;
		}else{
			var line = cell;
			line.connectLine.remove();
			line.connectLineForSel.remove();
			
			for(var j=0;j<line.fromCell.fromConnectLines.length;j++){
				if(line==line.fromCell.fromConnectLines[j]){
					line.fromCell.fromConnectLines.splice(j,1);
					break;
				}
			}
			for(var j=0;j<line.toCell.toConnectLines.length;j++){
				if(line==line.toCell.toConnectLines[j]){
					line.toCell.toConnectLines.splice(j,1);
					break;
				}
			}			
			for(var j=0;j<line.fromCell.children.length;j++){
				if(line.fromCell.children[j]==line.toCell){
					line.fromCell.children.splice(j,1);
					break;
				}
			}
			for(var j=0;j<line.toCell.parent.length;j++){
				if(line.toCell.parent[j]==line.fromCell){
					line.toCell.parent.splice(j,1);
					break;
				}
			}
			line=null;delete line;	
		}
	}
	this.clear();
};

function ccGraphSelectionModel(cell,graph){
	this.cell = cell;
	this.graph = graph;
	this.init();
}
ccGraphSelectionModel.prototype.constructor = ccGraphSelectionModel;
ccGraphSelectionModel.prototype.cell = null;
ccGraphSelectionModel.prototype.graph = null;
ccGraphSelectionModel.prototype.outlineSegement = null;
ccGraphSelectionModel.prototype.init = function(){
	var cellName = ccUtils.getFunctionName(this.cell.constructor);
	if(cellName==="ccCell"){
		var b = this.cell.shape.bounds;
		var x,y;
		var paper = this.graph.paper;
		var maskEl = paper.rect(b.x,b.y,b.width,b.height);
		maskEl.attr({stroke:"#00FF00",fill:"none","stroke-width":"1"});
		maskEl.node.setAttribute("shape-rendering","crispEdges");
		maskEl.node.setAttribute("stroke-dasharray","3,3");

		paper.setStart();
		x = b.x;y=b.y;
		this.createVertex(x,y);
		x = b.x + b.width/2;y=b.y;
		this.createVertex(x,y);
		x = b.x+ b.width;y=b.y;
		this.createVertex(x,y);
		x = b.x+ b.width;y=b.y+b.height/2;
		this.createVertex(x,y);
		x = b.x+ b.width;y=b.y+b.height;
		this.createVertex(x,y);
		x = b.x+ b.width/2;y=b.y+b.height;
		this.createVertex(x,y);
		x = b.x;y=b.y+b.height;
		this.createVertex(x,y);
		x = b.x;y=b.y+b.height/2;
		this.createVertex(x,y);	
		
		var st = paper.setFinish();
		st.attr({stroke:"black",fill:"#00FF00","stroke-width":1});
		st.push(maskEl);
		this.outlineSegement = st;
	}else{
		var b = this.cell.connectLine.getBBox();
		var p = this.cell.connectLine.realPath;
		var x,y;
		var paper = this.graph.paper;
		var maskEl = paper.path(p);
		maskEl.attr({stroke:"#00FF00",fill:"none","stroke-width":"1"});
		maskEl.node.setAttribute("shape-rendering","crispEdges");
		maskEl.node.setAttribute("stroke-dasharray","3,3");
		
		paper.setStart();
		x = p[0][1];y=p[0][2];
		this.createVertex(x,y);
		x = b.x + b.width/2;y=b.y+b.height/2;
		this.createVertex(x,y);	
		x = p[5][1];y=p[5][2];
		this.createVertex(x,y);			
		var st = paper.setFinish();
		st.attr({stroke:"black",fill:"#00FF00","stroke-width":1});
		st.push(maskEl);
		this.outlineSegement = st;
	}
};
ccGraphSelectionModel.prototype.createVertex = function(x,y){
	var r = 7;
	var paper = this.graph.paper;
	var vertex = paper.rect(x-4,y-4,7,7);
	vertex.node.setAttribute("shape-rendering","crispEdges");
	return vertex;
};
ccGraphSelectionModel.prototype.destroy= function(){
	this.outlineSegement.remove();
};


/*
* @ model
* ccPoint\ccRectangle
*/
function ccPoint(a, b) {
    this.x = a != null ? a : 0;
    this.y = b != null ? b : 0
}
ccPoint.prototype.x = null;
ccPoint.prototype.y = null;
ccPoint.prototype.equals = function(a) {
    return a.x == this.x && a.y == this.y
};
ccPoint.prototype.clone = function() {
    return new ccPoint(this.x,this.y);
};
function ccRectangle(a, b, c, d) {
    ccPoint.call(this, a, b);
    this.width = c != null ? c : 0;
    this.height = d != null ? d : 0
}
ccRectangle.prototype = new ccPoint;
ccRectangle.prototype.constructor = ccRectangle;
ccRectangle.prototype.width = null;
ccRectangle.prototype.height = null;
ccRectangle.prototype.setRect = function(a, b, c, d) {
    this.x = a;
    this.y = b;
    this.width = c;
    this.height = d
};
ccRectangle.prototype.getCenterX = function() {
    return this.x + this.width / 2
};
ccRectangle.prototype.getCenterY = function() {
    return this.y + this.height / 2
};
ccRectangle.prototype.add = function(a) {
    if (a != null) {
        var b = Math.min(this.x, a.x), c = Math.min(this.y, a.y), d = Math.max(this.x + this.width, a.x + a.width), a = Math.max(this.y + this.height, a.y + a.height);
        this.x = b;
        this.y = c;
        this.width = d - b;
        this.height = a - c
    }
};
ccRectangle.prototype.grow = function(a) {
    this.x = this.x - a;
    this.y = this.y - a;
    this.width = this.width + 2 * a;
    this.height = this.height + 2 * a
};
ccRectangle.prototype.getPoint = function() {
    return new ccPoint(this.x, this.y)
};
ccRectangle.prototype.equals = function(a) {
    return a.x == this.x && a.y == this.y && a.width == this.width && a.height == this.height
};
ccRectangle.prototype.clone = function() {
	return new ccRectangle(this.x,this.y,this.width,this.height);
}
function ccShape() {

}
ccShape.prototype.width = null;
ccShape.prototype.height = null;
ccShape.prototype.x = null;
ccShape.prototype.y = null;
ccShape.prototype.bounds = null;
ccShape.prototype.paper = null;
ccShape.prototype.graph = null;
ccShape.prototype.element = null;
ccShape.prototype.updateBounds = function(o) {
	if(!this.element) return ;
	var b = this.bounds,e = this.element.getBBox();
	this.x = b.x = e.x;this.y = b.y = e.y;this.width = b.width = e.width;this.height = b.height = e.height;
};
ccShape.prototype.getConnectablePoints = function() {
	var b1 = this.element.getBBox();
	if(!b1) return null;
	var x,y,w,h;
	x=b1.x;y=b1.y;w=b1.width;h=b1.height;
	var pts = {//四个方向的连接点
		"N":{x:x+w/2,y:y},
		"E":{x:x+w,y:y+h/2},
		"S":{x:x+w/2,y:y+h},
		"W":{x:x,y:y+h/2}
	}
	return pts;
};

function ccRectangleShape(cfg,graph) {
	if(cfg && graph){
		if(cfg.width>0) this.width=cfg.width;
		if(cfg.height>0) this.height=cfg.height;
		if(cfg.x>0) this.x=cfg.x;
		if(cfg.y>0) this.y=cfg.y;
		this.graph = graph;
		this.paper = graph.paper;
		this.init();
	}
}
ccRectangleShape.prototype = new ccShape;
ccRectangleShape.prototype.constructor = ccRectangleShape;
ccRectangleShape.prototype.radius = 4;
ccRectangleShape.prototype.init = function() {
	this.element = this.creatSvg();
	var a = this;
	this.bounds = new ccRectangle(a.x, a.y, a.width, a.height);
};
ccRectangleShape.prototype.creatSvg = function() {
    if(this.paper){
		var shape = this.paper.rect(this.x,this.y,this.width,this.height,this.radius);
		shape.attr({ 
			"stroke":"#C3D9FF", 
			"stroke-width": 1, 
			"cursor":"move" 
		});
		shape.node.setAttribute("fill","url(#"+ this.graph.insertGradientNodeId +")");
		shape.node.setAttribute("shape-rendering","crispEdges");
		 
		return shape;
	}
	return null;
};
ccRectangleShape.prototype.redraw = function(o) {
	var x = o.x,y=o.y;
	this.element.attr({x:x,y:y});
	this.updateBounds();
};
ccRectangleShape.prototype.updateBounds = function(o) {
	if(!this.element) return ;
	var b = this.bounds,e = this.element.attrs;
	this.x = b.x = e.x;this.y = b.y = e.y;this.width = b.width = e.width;this.height = b.height = e.height;
};

function ccTextShape(cfg,graph) {
	if(cfg.width>0) this.width=cfg.width;
	if(cfg.height>0) this.height=cfg.height;
	if(cfg.x>0) this.x=cfg.x;
	if(cfg.y>0) this.y=cfg.y;
	this.graph = graph;
	this.paper = graph.paper;
	this.init();

}
ccTextShape.prototype = new ccRectangleShape;
ccTextShape.prototype.constructor = ccTextShape;
ccTextShape.prototype.creatSvg = function() {
    if(this.paper){
		var shape = this.paper.rect(this.x,this.y,this.width,this.height,this.radius);
		shape.attr({ 
			"stroke":"transparent", 
			"stroke-width": 0, 
			"cursor":"move" 
		});
		shape.node.setAttribute("fill","transparent");
		shape.node.setAttribute("shape-rendering","crispEdges");
		 
		return shape;
	}
	return null;
};


function ccRhombusShape(cfg,graph) {
	if(cfg.width>0) this.width=cfg.width;
	if(cfg.height>0) this.height=cfg.height;
	if(cfg.x>0) this.x=cfg.x;
	if(cfg.y>0) this.y=cfg.y;
	this.graph = graph;
	this.paper = graph.paper;
	this.init();

};
ccRhombusShape.prototype = new ccShape;
ccRhombusShape.prototype.constructor = ccRhombusShape;
ccRhombusShape.prototype.init = function() {
	this.element = this.creatSvg();
	var a = this;
	this.bounds = new ccRectangle(a.x, a.y, a.width, a.height);
};
ccRhombusShape.prototype.creatSvg = function() {
    if(this.paper){
		var pathArr = this.getPathArr();
		var shape = this.paper.path(pathArr);
		shape.attr({ 
			"stroke":"#C3D9FF", 
			"stroke-width": 1, 
			"cursor":"move" 
		});
		shape.node.setAttribute("fill","url(#"+ this.graph.insertGradientNodeId2 +")");
		shape.node.setAttribute("shape-rendering","crispEdges");
		 
		return shape;
	}
	return null;
};
ccRhombusShape.prototype.redraw = function(o) {
	this.x = o.x;
	this.y = o.y;
	var pathArr = this.getPathArr();
	this.element.attr("path",pathArr);
	this.updateBounds();
};
ccRhombusShape.prototype.getPathArr= function() {
	/*
	var nx = this.x + this.width/2,ny=this.y,ex = this.x + this.width,ey=this.y + this.height/2,sx = this.x + this.width/2,sy=this.y+this.height,wx = this.x,wy=this.y +this.height/2;
	return ["M",nx,ny,"L",ex,ey,"L",sx,sy,"L",wx,wy];
	*/
	var x = this.x,y=this.y;	
	var w = this.width,h=this.height;
	var wf = w/(2+1.414);
	var hf = h/(2+1.414);
	var n1x = s2x = x + wf,n1y = y,n2x = s1x = x + (1+1.414)*wf,n2y=y,e1x = x + w,e1y=w2y=y + hf,e2x=x + w,e2y=w1y=y+h-hf, s1y=s2y=y+h,w1x=w2x =x;
	return ["M",n1x,n1y,"L",n2x,n2y,"L",e1x,e1y,"L",e2x,e2y,"L",s1x,s1y,"L",s2x,s2y,"L",w1x,w1y,"L",w2x,w2y,"L",n1x,n1y];
};

/**
* ccEllipseShape
*/
function ccEllipseShape(cfg,graph) {
	if(cfg.width>0) this.width=cfg.width;
	if(cfg.height>0) this.height=cfg.height;
	if(cfg.x>0) this.x=cfg.x;
	if(cfg.y>0) this.y=cfg.y;
	this.graph = graph;
	this.paper = graph.paper;
	this.init();

};
ccEllipseShape.prototype = new ccShape;
ccEllipseShape.prototype.constructor = ccEllipseShape;
ccEllipseShape.prototype.init = function() {
	this.element = this.creatSvg();
	var a = this;
	this.bounds = new ccRectangle(a.x, a.y, a.width, a.height);
};
ccEllipseShape.prototype.creatSvg = function() {
    if(this.paper){
		var shape = this.paper.ellipse(this.x+this.width/2,this.y+this.height/2,this.width/2,this.height/2);
		shape.attr({ 
			"stroke":"#C3D9FF", 
			"stroke-width": 1, 
			"cursor":"move" 
		});
		shape.node.setAttribute("fill","url(#"+ this.graph.insertGradientNodeId +")");
		shape.node.setAttribute("shape-rendering","crispEdges");
		 
		return shape;
	}
	return null;
};
ccEllipseShape.prototype.redraw = function(o) {
	var x=o.x+this.width/2,y=o.y+this.height/2,w=o.width/2,h=o.height/2;
	var rect={};
	if(typeof x!="undefined") rect.cx = x;if(typeof y!="undefined") rect.cy = y;if(typeof w!="undefined") rect.rx = w;if(typeof h!="undefined") rect.ry = h;
	
	this.element.attr(rect);
	this.updateBounds();
};

var ccObjectIdentity = {
	counter: 0,
	get: function() {
		return ccObjectIdentity.counter++
	}
};

var ccUtils={
	getFunctionName:function(a) {
		var b = null;
		if (a != null)
			if (a.name != null)
				b = a.name;
			else {
				a = a.toString();
				for (b = 9; a.charAt(b) == " "; )
					b++;
				var c = a.indexOf("(", b), b = a.substring(b, c)
			}
		return b
	},
	//计算字符数
	getCharLength:function(content){
		var pattern = /[^\x00-\x80]+/;
		var contentLength = 0;
		for (var i = 0; i < content.length; i++) {
			if (pattern.test(content.charAt(i))) {
				contentLength++;
			} else {
				contentLength += 0.5;
			}
		}
		return contentLength;
	},
	//返回字符数超长点的位置
	getCharLengthPositon:function(content,lineMaxCharNum){
		var pattern = /[^\x00-\x80]+/;
		var contentLength = 0;
		for (var i = 0; i < content.length; i++) {
			if (pattern.test(content.charAt(i))) {
				contentLength++;
			} else {
				contentLength += 0.5;
			}
			if(contentLength>lineMaxCharNum){
				return i;
			}
		}
		return -1;
	},
	//给超长度的字符换行
	autoLineBreak:function(content,lineMaxCharNum){
		var linebreakPos = this.getCharLengthPositon(content,lineMaxCharNum);
		if(linebreakPos!=-1){
			 var front = content.substring(0,linebreakPos);
			 var end =  content.substring(linebreakPos);
			 content = front+'\n'+end;
		}
		return content;
	}
};
/*
* @ Raphael.fn.drawConnect
*/
(function(Raphael){
	function getDistance(p1,p2) {
		return Math.sqrt(Math.pow(p2.x-p1.x,2) + Math.pow(p2.y-p1.y,2))
	}

	function getStartEnd(pts1, pts2) {		
		//计算各种方向连接线的距离，南只能连北，北只能连南，东只能连西，西只能连东
		var disN = {dr:"N",dr2:"S",dt:getDistance(pts1.N,pts2.S)};
		var disS = {dr:"S",dr2:"N",dt:getDistance(pts1.S,pts2.N)};
		var disE = {dr:"E",dr2:"W",dt:getDistance(pts1.E,pts2.W)};
		var disW = {dr:"W",dr2:"E",dt:getDistance(pts1.W,pts2.E)};
		
		var dis1=disN.dt<disS.dt?disN:disS;
		var dis2=disE.dt<disW.dt?disE:disW;
		var dis = dis1.dt<dis2.dt?dis1:dis2;//找出距离最短的
		
		var result={};
		result.start = pts1[dis.dr];
		result.start.direction = dis.dr;
		result.end = pts2[dis.dr2];
		result.end.direction = dis.dr2;
				
		return result;
	}
	
	function getArr(point) {
		var size=8;//箭头的长度
		var x1 = point.start.x, y1=point.start.y, x2=point.end.x, y2=point.end.y;	
		//M 210 60 L 225 60 C 240 60 240,60 240 80 L 240 230 C 240 250 240,250 255 250 L 264 250 
		var flagx = x2>x1?1:-1;
		var flagy = y2>y1?1:-1;
		var torrencex = Math.abs(x2-x1)*0.15;
		var torrencey = Math.abs(y2-y1)*0.15;
		torrencex = torrencex>10?10:torrencex;
		torrencey = torrencey>10?10:torrencey;
		var torrence = torrencex<torrencey?torrencex:torrencey;
		
		var angle;
		var M1,L1,C1,L2,C2,L3;
		M1=[x1,y1];
		L3=[x2,y2];
		var cx = (x1+x2)/2,cy = (y1+y2)/2;
		var direction = point.start.direction;
		if(direction==="E" || direction=="W"){
			L1 = [cx-torrence*flagx,y1];
			C1 = [cx-torrence*flagx,y1,cx,y1,cx,y1+torrence*flagy];
			angle = 180;
		}else{
			L1 = [x1,cy-torrence*flagy];
			C1 = [x1,cy-torrence*flagy,x1,cy,x1+torrence*flagx,cy];
			angle = 90;
		}
		var direction = point.end.direction;
		if(direction==="E" || direction=="W"){
			L2 = [cx,y2-torrence*flagy];
			C2 = [cx,y2-torrence*flagy,cx,y2,cx+torrence*flagx,y2];
		}else{	
			L2 = [x2-torrence*flagx,cy];
			C2 = [x2-torrence*flagx,cy,x2,cy,x2,cy+torrence*flagy];
		}	
		
		//var angle = Raphael.angle(L2[0], L2[1], L3[0], L3[1]);
		var a45 = Raphael.rad(angle - 45);
		var a45m = Raphael.rad(angle + 45);
		var x2a = x2 + Math.cos(a45) * size * flagx;
		var y2a = y2 - Math.sin(a45) * size * flagy;
		var x2b = x2 + Math.cos(a45m) * size * flagx;
		var y2b = y2 - Math.sin(a45m) * size * flagy;
		
		var result = ["M",M1[0],M1[1],"L",L1[0],L1[1],"C",C1[0],C1[1],C1[2],C1[3],C1[4],C1[5],"L",L2[0],L2[1],"C",C2[0],C2[1],C2[2],C2[3],C2[4],C2[5],"L",L3[0],L3[1],"L", x2a, y2a, "M", x2, y2, "L", x2b, y2b,"Z"];
		
		//var result = ["M", x1, y1, "L", x2, y2, "L", x2a, y2a, "M", x2, y2, "L", x2b, y2b];
		return result;
	}
	Raphael.fn.drawConnect = function (obj) {
		var fromPoints = obj.fromShape.getConnectablePoints();
		var toPoints = obj.toShape.getConnectablePoints();
		if(!fromPoints || !toPoints) return null;
		var point = getStartEnd(fromPoints,toPoints );
		var path1 = getArr(point);
		if (obj.connectLine) {
			obj.connectLine.attr({ path: path1 });
		} else {
			obj.connectLine = this.path(path1);
		}
		
		if (obj.connectLineForSel) {
			obj.connectLineForSel.attr({ path: path1 });
		} else {
			obj.connectLineForSel = this.path(path1);
		}				
		return obj;
	};
})(Raphael);

(function(Raphael){
	Raphael.fn.moveElement = function (element,offsetX,offsetY) {
		if(!element) return;
		var x = element.attrs.x + offsetX;
		var y = element.attrs.y + offsetY;
		element.attr({x:x,y:y});
	};
})(Raphael);