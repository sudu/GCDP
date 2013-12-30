(function(){
    var DDM = Ext.dd.DDM;
    var DH = Ext.DomHelper;	

	Ext.ux.Sort = function (drgHostEl, group, onSortEnd, drgTriggerEl, maskCls, ghostCls) {
        (drgTriggerEl || (drgTriggerEl = drgHostEl));
        drgTriggerEl.ddHost = drgHostEl;
        (group || (group = ("sortGroup" + new Date().valueOf())));	
        
        this.drgHostEl=drgHostEl;
        this.drgTriggerEl=drgTriggerEl;
        this.group=group;
        this.maskCls=maskCls;
        this.ghostCls=ghostCls;
        this.drgHostDom = drgHostEl.dom;	        
        this.onSortEnd = onSortEnd;	        
	}
	Ext.ux.Sort.prototype={
	    oriY:0
        ,dropHostEl:null
        ,dropTriggerEl:null
        ,drgHostEl:null
        ,drgTriggerEl:null
	    ,drgHostDom:null
	    ,onSortEnd:null
	    ,group:""
	    ,maskCls:""
	    ,ghostCls:""
	    ,dd:null
		,getMask:function(){
		    var mask = DH.append(document.body
	            , { cls: this.maskCls || ""
	                , style: "display:none;position:absolute;left:0;top:0;width:100%;height:100%;z-index:999;background:#C7D3ED;opacity:0.6"
	            }
	            , true);
		    mask.enableDisplayMode();	
		    return (Ext.ux.Sort.prototype.getMask=function(){
		    	return mask;	
		    })();
		}
		,getOccupant:function(){
		    var occupant = DH.append(document.body
	            , { 
	                style: "display:none;width:100%;height:0;border-top:1px #f00 solid"
	            }
	            , true);
		    occupant.enableDisplayMode();	
		    return (Ext.ux.Sort.prototype.getOccupant=function(){
		    	return occupant;	
		    })();		    
		}
		,getGhost:function(){
		    var ghost = DH.append(document.body
	            , { cls: this.ghostCls || ""
	                , style: "position:absolute;border:#222222 1px dotted;z-index:9999;display:none"
	            }
	            , true);
		    ghost.enableDisplayMode();
		    return (Ext.ux.Sort.prototype.getGhost=function(){
		    	return ghost;	
		    })();		    
		}
        , startDrag: function (x, y,dd) {
        	var drgHostEl=this.drgHostEl;
            this.getGhost().applyStyles({ width: drgHostEl.getWidth() + "px", "height": drgHostEl.getHeight() + "px" });
            this.getGhost().show();
            dd.setDragElPos(x, y);
            this.oriY = y;
            drgHostEl.appendChild(this.getMask().show());
            this.getOccupant().insertAfter(drgHostEl);
            this.getOccupant().show();
        }		
        , onDragEnter: function (e, dropElId,out) {
            var ths = this;
        	var dropTriggerEl= Ext.get(dropElId);
            var dropHostEl=this.dropHostEl = dropTriggerEl.ddHost || dropTriggerEl;
            
            if (this.drgHostDom == dropHostEl.dom)
                    return;
            var occupant=this.getOccupant();
           
            var newY=e.getPageY();
            var down;
            //Ext.trace(this.oriY+" "+newY,true)
            if(this.oriY ==newY){
            	down=e.relatedTarget==dropHostEl.dom.nextSibling;
            }else{
				down=this.oriY <newY;            	
            }
            if (down&&occupant.dom!=dropHostEl.dom.nextSibling){
            	dropHostEl.dom.parentNode.insertBefore(occupant.dom, dropHostEl.dom.nextSibling);
            }
            if(!down&&occupant.dom.nextSibling!=dropHostEl.dom){
                dropHostEl.dom.parentNode.insertBefore(occupant.dom, dropHostEl.dom);
            }
         	this.oriY = newY;
            DDM.refreshLocation([this.dropHostEl, this.drgTriggerEl]);
        }
	     , endDrag: function (e,dd) {
	    	var t=this;
	    	DDM.lock();
	        var moved = false;
            var occupant=this.getOccupant();
            var drgHostEl=this.drgHostEl;            
	        //occupant.hide();
	        if (drgHostEl.dom.nextSibling != occupant.dom && drgHostEl.dom.previousSibling != occupant.dom) {
	            moved = true;
	            drgHostEl.insertBefore(occupant);
	        }
	        this.getGhost().Shift({ "x": drgHostEl.getX(), "y": drgHostEl.getY() }, function () {
	            t.getMask().hide();
	            t.getGhost().hide();
	            (moved&&t.onSortEnd && t.onSortEnd(drgHostEl));
	    		DDM.unlock();
	        });
	        occupant.dom.parentNode.removeChild(occupant.dom);
	    }
		 ,init:function(){
			 var t=this;
			return this.dd=this.drgHostEl.getDDs()["sortDD"] = this.drgTriggerEl.initDD(this.group, null, {
				"mask": this.getMask()
				, "ghost": this.getGhost()
			    , getDragEl: function (e) {
			        return t.getGhost();
			    }
			    , startDrag: function (x, y) {
			    	t.startDrag(x, y,this);
			    }
			    , onDragEnter: function (e, dropElId) {
			    	t.onDragEnter(e, dropElId);
			    }
			    , onDragOut: function (e, dropElId) {
			    	t.onDragEnter(e, dropElId,true);
			    }
			    , endDrag: function (e) {
			    	t.endDrag(e,this);
			    }
			});
		}	
	}		


	Ext.sort=function(drgHostEl, group, onSortEnd, drgTriggerEl, maskCls, ghostCls){
		return new Ext.ux.Sort(drgHostEl, group, onSortEnd, drgTriggerEl, maskCls, ghostCls).init();
	}
})();