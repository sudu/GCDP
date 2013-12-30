(function(){
    var DDM = Ext.dd.DDM;
    var DH = Ext.DomHelper;	

Ext.ux.ControlSort=Ext.extend(Ext.ux.Sort,{
	toggleLock : function (drgHostEl, lock) {
	    if (!drgHostEl.dom.ctrl || !drgHostEl.dom.ctrl.items)
	        return;
	    var len = drgHostEl.dom.ctrl.items.length;
	    if (len > 0) {
	        var items = drgHostEl.dom.ctrl.items;
	        var eleList = [];
	        while (len--) {
	            eleList.push(items.get(len).formField);
	        };
	        lock ? DDM.lockEls(eleList) : DDM.unlockEls(eleList);
	    }
	}	
    , startDrag: function (x, y,dd) {
    	Ext.ux.ControlSort.superclass.startDrag.call(this,x,y,dd);
        this.toggleLock(this.drgHostEl, true); //lock children's dragdrop;
    }
    , onDragEnter: function (e, dropElId, isOut,dd) {
        var ths = this;
        var dropTriggerEl=this.dropTriggerEl = Ext.get(dropElId);
        var occupant=this.getOccupant();
        var dpDD = dropTriggerEl.getDDs().dropDD;
        if (dpDD && dpDD.groups["movein"]) {
            this.dropHostEl.dom.ctrl.body.dom.appendChild(occupant.dom);
            this.oriY = e.getPageY();
            if (!isOut) {
                dd.onDragOut = function (eve) {
                    ths.onDragEnter(eve, dropElId, true,this);
                }
            }                        
        } else {
    		Ext.ux.ControlSort.superclass.onDragEnter.call(this,e, dropElId, isOut,dd);
        }
    }
    , endDrag: function (e,dd) {
    	Ext.ux.ControlSort.superclass.endDrag.call(this,e,dd);                	
        var occupant=this.getOccupant();
        var drgHostEl=this.drgHostEl;
        var dropHostEl=this.dropHostEl;
        if (drgHostEl.dom.nextSibling != occupant.dom && drgHostEl.dom.previousSibling != occupant.dom) {
            var dpDD = this.dropTriggerEl.getDDs().dropDD;
            if (dpDD) {
                drgHostEl.parentPanel = dropHostEl;
                drgHostEl.ctnrId = dropHostEl.dom.ctrl.id;
                dropHostEl.dom.ctrl.items.add(drgHostEl.dom.ctrl); //for lock;
                if (dpDD.groups["movein"]) {
                    dpDD.disable();
                }
            } else {
                var panel = Ext.getCmp(drgHostEl.ctnrId); //out of panel
                if (panel.isContainer) {
                    panel.items.remove(drgHostEl.dom.ctrl); //for lock;
                    if (panel.items.length == 0) {
                        panel.formField.dropDiv.getDDs().dropDD.enable();
                    }
                }
                panel = Ext.getCmp(dropHostEl.ctnrId); //into panel
                if (panel.isContainer) {
                    panel.items.add(drgHostEl.dom.ctrl); //for lock;
                }
                drgHostEl.parentPanel = dropHostEl.parentPanel;
                drgHostEl.ctnrId = dropHostEl.ctnrId;
            }
        }
        this.toggleLock(drgHostEl, false); //lock children's dragdrop;
    }
	,init:function(){
    	var d=Ext.ux.ControlSort.superclass.init.call(this);                	
        var drgHostEl=this.drgHostEl;
        if (drgHostEl.dom.ctrl) {
            if (drgHostEl.dom.ctrl.isContainer) {
                if (!drgHostEl.dropDiv) {
                    var b = drgHostEl.dom.ctrl.body;
                    b.setStyle("position", "relative");
                    var div = drgHostEl.dropDiv = DH.append(b.dom, { "tag": "div", "style": "width:100%;height:30px;background:#d3ebf5;z-index:0;" }, true);
                    div.enableDisplayMode();
                    div.ddHost = drgHostEl;
                    var dropDD = new Ext.dd.DDTarget(div, 'movein');
                    dropDD.enable = function () {
                        div.show();
                        this.addToGroup("movein");
                    }
                    dropDD.disable = function () {
                        div.hide();
                        this.removeFromGroup("movein");
                    }
                    div.getDDs()["dropDD"] = dropDD;
                }
            } else {
                var copyD = Ext.apply({}, d);
                copyD.isTarget = false;
                copyD.addToGroup('movein');
            }
        }	
        return d;
	}
});


Ext.sortControl=function(drgHostEl, group, onSortEnd, drgTriggerEl, maskCls, ghostCls){
	return new Ext.ux.ControlSort(drgHostEl, group, onSortEnd, drgTriggerEl, maskCls, ghostCls).init();
}

})();
