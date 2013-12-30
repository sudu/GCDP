
Ext.ux.PropertyColumnModel = Ext.extend(Ext.grid.PropertyColumnModel, {
	renderCell: function (val) {
		var rv = val;
		if (Ext.isDate(val)) {
			rv = this.renderDate(val);
		} else if (typeof val == 'boolean') {
			rv = this.renderBool(val);
		} else if (Ext.type(val) == "array") {
			rv = this.renderArray(val);
		}
		return Ext.util.Format.htmlEncode(rv);
	}
	, renderArray: function (val) {
		var len = val.length;
		var str = "";
		while (--len > -1) {
			var v = val[len];
			if (Ext.type(v) == "object") {
				v = v.text;
			}
			str = "," + v + str;
		}
		if (str.length > 1)
			str = str.substr(1);
		return str;
	}
});

Ext.ux.PropertyStore = Ext.extend(Ext.grid.PropertyStore, {
	isEditableValue: function (val) {
		if (Ext.isDate(val) || Ext.isArray(val)) {
			return true;
		} else if (typeof val == 'object' || typeof val == 'function') {
			return false;
		}
		return true;
	}
});

Ext.ux.PropertyGrid = Ext.extend(Ext.grid.PropertyGrid, {
	initComponent: function () {
		this.customEditors = this.customEditors || {};
		this.lastEditRow = null;
		var store = new Ext.ux.PropertyStore(this);
		this.propStore = store;
		var cm = new Ext.ux.PropertyColumnModel(this, store);
		store.store.sort('name', 'ASC');
		this.addEvents(

			'beforepropertychange',

			'propertychange'
		);
		this.cm = cm;
		this.ds = store.store;
		Ext.grid.PropertyGrid.superclass.initComponent.call(this);

		this.selModel.on('beforecellselect', function (sm, rowIndex, colIndex) {
			if (colIndex === 0) {
				this.startEditing.defer(200, this, [rowIndex, 1]);
				return false;
			}
		}, this);
	}

});
