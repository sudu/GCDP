
/**
 * @constructor
 * @param {Object} cfg A config object
 *  @cfg {String} tabPosition 'top' (the ext default behaviour), 'bottom' (also ext default), 'left' (vertical tabs on the left side) or right (vertical tabs on the right side)
 *  @cfg {Number} tabWidth (only applies if tabPosition is set to 'left' or 'right') the width of the tab strip in pixel; defaults to 150
 *  @cfg {String} textAlign 'left' or 'right', defaults to 'left' (only applies if tabPosition is set to 'left' or 'right')
 */
Ext.ux.VerticalTabPanel = function(cfg) {
  if (cfg.tabPosition == 'left' || cfg.tabPosition == 'right') {
    cfg.cls = cfg.cls || '';
    cfg.cls = 'ux-vertical-tabs ' + cfg.cls;
    if (cfg.textAlign && cfg.textAlign == 'right') {
      cfg.cls = 'ux-vertical-tabs-alignright ' + cfg.cls;
    }
    cfg.cls = (cfg.tabPosition == 'left' ? 'ux-vertical-tabs-left ' : 'ux-vertical-tabs-right ') + cfg.cls;
    this.intendedTabPosition = cfg.tabPosition;
    this.verticalTabs = true;
    cfg.tabPosition = 'top';
    // autoWidth ，autoHeight 无效
    cfg.autoWidth = false;
    cfg.autoHeight = false;
  }

  Ext.ux.VerticalTabPanel.superclass.constructor.call(this, cfg);

};



Ext.extend(Ext.ux.VerticalTabPanel, Ext.TabPanel, {
	tabWidth : 150,
	verticalText : false,//竖直文本标志
	afterRender : function() {	
		Ext.ux.VerticalTabPanel.superclass.afterRender.call(this);
		if (this.verticalTabs) {
			//竖直文本tabWidth固定
			this.verticalText && ( this.tabWidth = 30 );
			
			this.header.setWidth(this.tabWidth);
			this.header.setHeight(this.height || this.container.getHeight());
      
			var tabs = Ext.select('span.x-tab-strip-inner').elements;
			for(var i=0; i<tabs.length; i++){
				Ext.get(tabs[i]).setWidth(this.tabWidth);
				//水平文本tabHeight不设置，由Ext.TabPanel内部固定其值
				if(this.verticalText && typeof this.tabHeight !== 'undefined'){
					Ext.get(tabs[i]).setHeight(this.tabHeight);
				}
				//竖直文本
				if(this.verticalText){
					Ext.get(tabs[i]).removeClass('x-tab-strip-inner').addClass('x-tab-strip-inner-v');
					var el = Ext.get(tabs[i]).first().dom;
					el.style.whiteSpace = 'normal';
					el.style.float = 'left';
					el.style.width = '14px';
				}
			}
		}
  },


/**
 * Adjust header and footer size.
 * @param {Number} w width of the container
 * @return {Number} the body will be resized to this width
 */

  adjustBodyWidth : function(w) {
    if (this.verticalTabs) {
      if (Ext.isIE6) {
        //I got the value "3" through trial and error; it seems to be related with the x-panel-header border; if the border
        //is set to "none", then this substraction is not necessary - but it does not seem related to the border width, margin or padding of any
        //of the panels so I dont know how to calculate it; please let me know if you have any idea what's going on here
        this.bwrap.setWidth(w );
      }
      return w;
    }
    else {
      return Ext.ux.VerticalTabPanel.superclass.adjustBodyWidth.call(this, w);
    }
  },

/**
 * Get the new body height and adjust the height of the tab strip if it is vertical.
 * @param h {Number}
 */
  adjustBodyHeight : function(h) {
    if (this.verticalTabs) {
      this.header.setHeight(h + (this.tbar ? this.tbar.getHeight() : 0));
    }
    return Ext.ux.VerticalTabPanel.superclass.adjustBodyHeight.call(this, h);
  },

/**
 * If the tab strip is vertical, we need to substract the "header" width.
 * @return {Number} The frame width
 */
  getFrameWidth : function() {
    return Ext.ux.VerticalTabPanel.superclass.getFrameWidth.call(this) + this.verticalTabs ? this.tabWidth : 0;
  },

/**
 * If the tab strip is vertical, we don't need to substract it's height
 * @return {Number} The frame height
 */
  getFrameHeight : function() {
    return Ext.ux.VerticalTabPanel.superclass.getFrameHeight.call(this) - (this.verticalTabs ? this.header.getHeight() : 0);
  }
});
Ext.reg('vrtabpanel', Ext.ux.VerticalTabPanel);
