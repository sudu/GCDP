(function () {
    (Ext.ux || (Ext.ux = {}));
    var imgPath = "../js/ext-2.0.2/resources";
    Ext.ux.jsonGridView = Ext.extend(Ext.grid.GridView, {
        appendNewRow: function () {
            var cs = this.getColumnData();
            delete cs[2].style;

            var keyId = "id" + (new Date()).valueOf();
            var k = "<div id='" + keyId + "'></div>";
            var valId = keyId + "11";
            var val = "<div id='" + valId + "'></div>";

            var btnId = valId + "11";
            var btn = "<div style='height:20px;background:url(" + imgPath + "/images/default/tree/drop-yes.gif) no-repeat center;cursor:pointer;' id='" + btnId + "'></div>";

            var rs = [new Ext.data.Record({ name: k, value: val, EMPTY: btn }, k)];
            var ds = this.grid.store;

            var rowHTML = this.doRender(cs, rs, ds, 0, 3);
            rowHTML = rowHTML.replace("x-grid3-row", "");
            Ext.DomHelper.append(this.mainBody, rowHTML);

            var keyField = new Ext.form.TextField({ "selectOnFocus": true, "allowBlank": false, "emptyText": "Key", "renderTo": keyId });
            var valField = new Ext.form.TextField({ "selectOnFocus": true, "emptyText": "Value", "renderTo": valId });
            var btnField = Ext.get(btnId);
            var g = this.grid;
            btnField.on("click", function () { g.addItem(keyField, valField); });

        }
        , layout: function () {
            this.appendNewRow();
            Ext.ux.jsonGridView.superclass.layout.call(this);
        }
        , init: function (grid) {
            Ext.ux.jsonGridView.superclass.init.call(this, grid);
            var html = this.templates.row.html;
            this.templates.row.html = html.replace('cellpadding="0" style="{tstyle}', 'cellpadding="0" style="table-layout:fixed;{tstyle}');
            this.templates.row.compile();
        }

    });


    //couldn't be derived(this.constructor is used);
    Ext.ux.JsonEditorField = function (editor) {
        this.editor = editor;
        this.constructor.superclass.constructor.call(this);
    }
    Ext.ux.JsonEditorField.init = function () {
        this.win = new Ext.Window({
            "width": 400
            , "height": 300
            , "closable": false
            , "modal": true
            , "buttonAlign": "center"
            ,layoutOut:"fit"
        })
        this.table = new Ext.grid.EditorGridPanel({
            clicksToEdit: 1
            ,width:400
            , view: new Ext.ux.jsonGridView()
            , ds: new Ext.data.Store({
                recordType: Ext.grid.PropertyRecord
            })
            , columns: [
                    { header: "Key", width: 176, sortable: true, dataIndex: 'name', menuDisabled: true
                        , editor: new Ext.form.TextField({
                            allowBlank: false
                        })
                    }
                    , { header: "Value", width: 176, dataIndex: 'value', menuDisabled: true
                        , editor: new Ext.form.TextField({
                            allowBlank: true
                        })
                    }
                    , { dataIndex: 'EMPTY', width: 31, menuDisabled: true
                    	, css: "background:url(" + imgPath + "/images/default/layout/panel-close.gif) no-repeat center;cursor:pointer;"
                    }
            ]
            , addItem: function (keyField, valField) {
                if (!keyField.validate())
                    return;
                var key = keyField.getValue();
                var val = valField.getValue();
                var v = this.view;
                var ds = this.store;
                ds.add(new Ext.grid.PropertyRecord({ name: key, value: val }));
                this.refreshView();
            }
            , deleteItem: function (rowIndex) {
                var v = this.view;
                var ds = this.store;
                if (ds.getCount() < 1) {
                    return;
                }
                ds.data.removeAt(rowIndex);
                v.removeRows(rowIndex, rowIndex);
                this.refreshView();
            }
            , refreshView: function () {
                var v = this.view;
                v.scroller.dom.style.height = "auto";
                v.scroller.dom.style.width = "auto";
                v.el.dom.style.height = "auto";
                v.refresh();

            }
            , afterRender: function () {
                this.selModel.on('beforecellselect', function (sm, rowIndex, colIndex) {
                    if (colIndex === 2) {
                        this.deleteItem(rowIndex);
                        return false;
                    }
                }, this);
                Ext.grid.EditorGridPanel.superclass.afterRender.apply(this, arguments);
                if (this.source) {
                    this.setSource(this.source);
                }
            }
            , processEvent: function (name, e) {
                this.fireEvent(name, e);
                var t = e.getTarget();
                var v = this.view;
                var header = v.findHeaderIndex(t);
                if (header !== false) {
                    this.fireEvent("header" + name, this, header, e);
                } else {
                    var row = v.findRowIndex(t);
                    if (row == undefined)//for row to add item;
                        return;
                    var cell = v.findCellIndex(t);
                    if (row !== false) {
                        this.fireEvent("row" + name, this, row, e);
                        if (cell !== false) {
                            this.fireEvent("cell" + name, this, row, cell, e);
                        }
                    }
                }
            }

        });
        this.win.add(this.table);
    };
    Ext.ux.JsonEditorField.init();

    Ext.extend(Ext.ux.JsonEditorField, Ext.form.Field, {
        editor: null
         , "defaultTitle": "Json Editor--"
       , win: Ext.ux.JsonEditorField.win
        , table: Ext.ux.JsonEditorField.table
        , store: Ext.ux.JsonEditorField.store
        , canceled: false
        , initComponent: function () {
            Ext.ux.JsonEditorField.superclass.initComponent.call(this);
            this.win.addButton("确定", this.done, this);
            this.win.addButton("取消", this.cancel, this);
        }
        , openWindow: function (title) {
            var dt = this.editor.defaultTitle;
            dt += title;
            this.win.setTitle(dt);
            this.win.show();
        }
        , getValue: function () {
            var v = null;
            if (!this.canceled) {
                v = {};
                var s = this.table.store.data.items;
                //                s.pop();
                var len = s.length;
                while (--len > -1) {
                    var d = s[len].data;
                    v[d["name"]] = d["value"];
                }
            }
            return v;
        }
        , setValue: function (val) {//pass json;
            this.originalValue = val;

            // this.table.store.removeAll();
            var data = [];
            for (var k in val) {
                var v = val[k];
                if (this.isEditableValue(v)) {
                    data.push(new Ext.grid.PropertyRecord({ name: k, value: v }, k));
                }
            }
            //            k = "Key"; v = "Value";
            //            data.push(new Ext.grid.PropertyRecord({ name: k, value: v }, k));

            var v = this.table.view;
            if (v.el) {
                v.scroller.dom.style.height = "auto";
                v.scroller.dom.style.width = "auto";
                v.el.dom.style.height = "auto";
            }
            this.table.store.loadRecords({ records: data }, {}, true);
        }
        , done: function () {
            this.win.hide();
            this.canceled = false;
            this.complete();
        }
        , cancel: function () {
            this.win.hide();
            this.canceled = true;
            this.complete();
        }
        , complete: function () {
            this.editor.allowBlur = false;
            this.editor.onBlur();
        }
        , isEditableValue: function (val) {
            if (Ext.isDate(val)) {
                return true;
            } else if (typeof val == 'object' || typeof val == 'function') {
                return false;
            }
            return true;
        }


    });




    Ext.ux.JsonEditor = function (config) {
        var field = new Ext.ux.JsonEditorField(this);
        Ext.ux.JsonEditor.superclass.constructor.call(this, field, config);
    }

    Ext.extend(Ext.ux.JsonEditor, Ext.grid.GridEditor, {
        "defaultTitle": "Json Editor--"
        , startEdit: function (el, value) {
            if (this.editing) {
                this.completeEdit();
            }
            this.boundEl = Ext.get(el);
            var v = value !== undefined ? value : this.boundEl.dom.innerHTML;
            if (this.fireEvent("beforestartedit", this, this.boundEl, v) === false) {
                return;
            }
            this.startValue = v;
            this.setValue(v);
            this.editing = true;
            this.allowBlur = true;
            this.field.openWindow(this.record.id); //title set here;
        }
        , setValue: function (val) {
            val = Ext.decode("{" + val + "}");
            this.field.setValue(val);
        }
        , getValue: function () {
            var val = this.field.getValue(val);
            if (val == null)
                val = this.startValue;
            else
                val = Ext.encode(val);
        }
    });

    Ext.ux.CssEditor = Ext.extend(Ext.ux.JsonEditor, {
        "defaultTitle": "CSS Editor--"
        , setValue: function (val) {
            if (!Ext.isEmpty(val)) {
                var last = val.length - 1;
                (val.lastIndexOf(";") == last && (val = val.substr(0, last)));
                var arr = val.split(";");
                var index = arr.length - 1;
                val = "";
                while (index > -1) {
                    var pair = arr[index].split(":");
                    try {
                        var id = pair[0];
                        var v = pair[1];
                        v = "'" + v + "'";
                        val += id + ":" + v + ",";
                    } catch (e) {
                        Ext.Msg.alert("格式错误", arr[index]);
                    }
                    index--;
                }
                val = val.substr(0, val.length - 1);
            }
            Ext.ux.CssEditor.superclass.setValue.call(this, val);
        }
        , getValue: function () {
            var val = this.field.getValue(val);
            if (val == null)
                val = this.startValue;
            else {
                var str = "";
                for (var i in val) {
                    str += i + ":" + val[i] + ";";
                }
                str = str.substr(0, str.length - 1);
                val = str;
            }
            return val;
        }
    });
    //Ext.ux.BaseControl.styleEditor = new Ext.ux.CssEditor();
})();