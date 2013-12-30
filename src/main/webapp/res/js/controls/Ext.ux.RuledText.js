(function () {
    Ext.ux.RuledText = function (config) {
        (config.emphasizedNos && (config.emphasizedNos = config.emphasizedNos.split(",")));
        Ext.ux.RuledText.superclass.constructor.call(this, config);
    }
    Ext.extend(Ext.ux.RuledText, Ext.form.TextField, {
        maxSize: 50
		, emphasizedNos: [20]
        , width: 400
        , height: 30
        , fontSize: 14
        , value: ""
        , counter: 0
        , tipText: ""

        , name: ""
        , counterNo: null
        , total: 0

		, cls: "rulerInput"
		, emphasizedCls: "emphasized"
		, rulerBoxCls: "ruler"
		, cursorCls: "cursor"
		, halfBdrCls: "halfBdr"
		, fullBdrCls: "fullBdr"
        , tenthCls: "tenth"
        , counterCls: "counter"
        , counterOverpassCls: "counterOverpass"
        , counterPattern: { "Normal": 0, "Tip": 1, "None": 2 }
		, onRender: function (ctnr, pos) {
			ctnr=ctnr.createChild({style:"position:relative"});
		    this.constructor.superclass.onRender.call(this, ctnr, pos);
		    this.el.setStyle({ "font-size": this.fontSize + "px", "line-height": this.height + "px" });

		    if (this.counter != this.counterPattern.None) {
		        var counter = ctnr.createChild({ tag: "span", cls: this.counterCls });
		        if (this.counter == this.counterPattern.Normal) {
		            counter.createChild({ "tag": "span", html: "字数：" });
		            this.counterNo = counter.createChild({ "tag": "span", html: "0.0" });
		            counter.createChild({ "tag": "i", html: "/" + this.maxSize });
		            this.el.on("keyup", this.count, this);
		        } else
		            counter.update(this.tipText);
		    }

		    this.createRulerBox(ctnr);

		}
		, createRulerBox: function (ctnr) {
		    var box = ctnr.createChild({ cls: this.rulerBoxCls });
		    box.setStyle({ width: this.width + "px", height: this.height + "px", "font-size": this.fontSize + "px", "line-height": this.height + "px" });

		    var no = this.width / this.fontSize * 2;
		    for (var i = 1; i <= no; i++) {
		        var cls = i % 2 ? this.halfBdrCls : this.fullBdrCls;
		        cls = i % 20 ? cls : this.tenthCls;
		        var len = this.emphasizedNos.length;
		        var html = i % 20 ? "<div></div>" : "<div>" + i / 2 + "</div>";
		        for (var j = 0; j < len; j++) {
		            if (this.emphasizedNos[j] * 2 == i) {
		                cls = this.emphasizedCls;
		                html = "<div>" + i / 2 + "</div>"
		                this.emphasizedNos.splice(j, 1);
		                break;
		            }
		        }
		        var c = box.createChild({ tag: "span", cls: cls, html: html });
		    }
		}
		, count: function () {
		    var val = this.el.dom.value;
		    var len = val.length;
		    var total = 0;
		    if(len>0){
			    while (len--) {
			        total += val.charCodeAt(len) > 255 ? 1 : 0.5;
			    }
		    }
		    this.counterNo.update(total.toFixed(1));
		}
    });
    Ext.reg('xtext', Ext.ux.RuledText);
})();
