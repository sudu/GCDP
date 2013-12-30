Ext.ux.FieldSet = function (config) {
	Ext.apply(config, {
		autoHeight: true
		,checkboxToggle:true
		, cls: "collapsiblePanel"
        , layout: 'xform'
        ,hideLabel:true
        ,hideNote:true
        , onCheckClick: function (ev) {
			if (ev.stopPropagation) ev.stopPropagation();
			this[this.checkbox.dom.checked ? 'expand' : 'collapse']();
		}
	});
	Ext.ux.FieldSet.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.FieldSet, Ext.form.FieldSet, {
});
Ext.reg('xfieldset', Ext.ux.FieldSet);