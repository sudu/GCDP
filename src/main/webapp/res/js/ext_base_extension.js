(function () {
	
//fixation starts:
	//for emptyText starts
    Ext.form.Field.prototype.getAutoCreate=function(){
    	var	a=Ext.form.Field.superclass.getAutoCreate.call(this);
    	(this.emptyText&&(a.emptyText=this.emptyText));
    	return a;
    };
	Ext.lib.Ajax.serializeForm= function (F) {
            if (typeof F == "string") {
                F = (document.getElementById(F) || document.forms[F])
            }
            var G, E, H, J, K = {},K2="",val,
                M = false;
            for (var L = 0; L < F.elements.length; L++) {
                G = F.elements[L];
                J = G.disabled;
                E = encodeURIComponent(G.name);
                H = G.value==G.getAttribute("emptyText")?"":G.value;
                
                if (!J && E) {
                	val=null;
                    switch (G.type) {
                        case "select-one":
                        case "select-multiple":
                            for (var I = 0; I < G.options.length; I++) {
                                if (G.options[I].selected) {
                                    if (Ext.isIE) {
                                        val=G.options[I].attributes["value"].specified ? G.options[I].value : G.options[I].text;
                                    } else {
                                        val=G.options[I].hasAttribute("value") ? G.options[I].value : G.options[I].text;
                                    }
                                }
                            }
                            break;
                        case "radio":
                        case "checkbox":
                            if (G.checked) {
                                val=H;
                            }
                            break;
                        case "file":
                        case undefined:
                        case "reset":
                        case "button":
                            break;
                        case "submit":
                            if (M == false) {
                                val=H;
                                M = true
                            }
                            break;
                        default:
                            val=H;
                            break
                    }
                    if(val!=null){
	                    val=encodeURIComponent(val);
	                    if(K[E]){
	                    	val=K[E]+","+val;	
	                    }
	                    K[E]=val;
                    }
                }
            }
            for(var i in K){
            	K2+=i+"="+K[i]+"&";	
            }
            K2 = K2.substr(0, K2.length - 1);
            return K2
        };
	//for emptyText ends;
	
//fixation ends

Ext.listPosition=[["leftTop","左上"],["rightTop","右上"],["leftBtm","左下"],["rightBtm","右下"],["center","居中"],["tile","平铺"]];
	
	
 Ext.ux={}
 Ext.de={}
 Ext.ex={}
    Ext.trace = function (msg, append, autoHide) {
        var wnd = Ext.get("TRACEWINDOW");
        if (!wnd) {
            var dh = Ext.DomHelper;

            wnd = dh.insertHtml("afterEnd", document.body, "<div id='TRACEWINDOW' style='position:absolute;width:100%;word-wrap:break-word;border:1px black solid;background:#eee;color:Black;left:0;bottom:0;font-size:30px;z-index:9999999'><div id='traceWin_msg' style='padding:3px;text-align:right;'></div><div style='background:black;clear:both;'><input type='text'  id='traceWin_cmd' value='' style='width:300px'/><input type='button'  id='traceWin_btn' value='execute'></div></div>", true)
            wnd = Ext.get(wnd);
            Ext.get("traceWin_btn").on("click", function () {
                var msg = Ext.get("traceWin_cmd").dom.value;
                if(msg=="clear")
                	return Ext.get("traceWin_msg").dom.innerHTML ="";
                
                try {
                    msg = eval(msg);
                } catch (e) {
                    msg = e.message;
                }

                msg = Ext.get("traceWin_msg").dom.innerHTML + "<br/>" + msg;
            	Ext.get("traceWin_msg").dom.innerHTML = msg;
       
            });
            Ext.get("traceWin_cmd").on("keyup", function (e) { e.keyCode === 13 && Ext.get("traceWin_btn").fireEvent("click"); });
        }
        if (msg === null || msg === undefined) {
            msg = (new Date).valueOf();
        }
        if (append)
            msg = Ext.get("traceWin_msg").dom.innerHTML + "|" + msg;
        Ext.get("traceWin_msg").update(msg);
        wnd.show();
        if (autoHide) {
            window.setTimeout(function () {
                wnd.hide()
            }, 500);
        }
    };

    Ext.apply(Ext, {
        parseQuery: function (qs) { // optionally pass a querystring to parse
            var params = {};

            if (qs == null) {
				qs = location.search.substring(1, location.search.length);
			}else if (qs.length > 0) {
				qs = qs.split('?');
				if(qs.length>1){
					qs = qs[1];
				}else{
					return params;
				}
			}

			// Turn <plus> back to <space>
			// See: http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1
			qs = qs.replace(/\+/g, ' ');
			var args = qs.split('&'); // parse out name/value pairs separated via &

			// split out each name=value pair
			for (var i = 0; i < args.length; i++) {
				var pair = args[i].split('=');
				var name = decodeURIComponent(pair[0]);

				var value = (pair.length == 2)
					? decodeURIComponent(pair[1])
					: name;
				params[name] = value;
			}
				
            return params;
        }
    ,nore:function(str){
    	return str == undefined || str == null || (typeof (str) == "string" && Ext.util.Format.trim(str) == "");	
    }
	, isWindow: function (obj) {
	    return obj && typeof obj === "object" && "setInterval" in obj;
	}
	, isPlainObject: function (obj) {
	    // Must be an Object.
	    // Because of IE, we also have to check the presence of the constructor property.
	    // Make sure that DOM nodes and window objects don't pass through, as well
	    if (!obj || this.type(obj) !== "object" || obj.nodeType || this.isWindow(obj)) {
	        return false;
	    }
	    // Not own constructor property must be Object
	    if (obj.constructor &&
	        !Object.prototype.hasOwnProperty.call(obj, "constructor") &&
	        !Object.prototype.hasOwnProperty.call(obj.constructor.prototype, "isPrototypeOf")) {
	        return false;
	    }

	    // Own properties are enumerated firstly, so to speed up,
	    // if last one is own, then all properties are own.

	    var key;
	    for (key in obj) { }

	    return key === undefined || Object.prototype.hasOwnProperty.call(obj, key);
	}
	, applyDeep: function (target, config, defaults) {
	    var t = this.type(target);
	    if (t !== "object" && t != 'function' && t != 'array') {
	        target = {};
	    }
	    if (defaults) {
	        this.applyDeep(target, defaults)
	    }
	    if (config != null) {
	        // Extend the base object
	        var copyIsArray;
	        for (name in config) {
	            src = target[name];
	            copy = config[name];

	            // Prevent never-ending loop
	            if (target === copy) {
	                continue;
	            }
	            // Recurse if we're merging plain objects or arrays
	            if (copy && (this.isPlainObject(copy) || (copyIsArray = this.type(copy) == "array"))) {
	                if (copyIsArray) {
	                    copyIsArray = false;
	                    if(this.type(src) != "array")
							src=[];
	                } else {
	                    if(!this.isPlainObject(src))
			                src={};
	                }
	                target[name] = this.applyDeep(src, copy);

	                // Don't bring in undefined values
	            } else if (copy !== undefined) {
	                target[name] = copy;
	            }

	        }
	    }
	    return target;
	}
	, applyIfDeep: function (target, config, defaults) {//if not exists.
	    var t = this.type(target);
	    if (t !== "object" && t != 'function' && t != 'array') {
	        target = {};
	    }
	    if (defaults) {
	        this.applyIfDeep(target, defaults)
	    }
	    if (config != null) {
	        // Extend the base object
	        var copyIsArray;
	        for (name in config) {
	            src = target[name];
	            copy = config[name];

	            // Prevent never-ending loop
	            if (target === copy) {
	                continue;
	            }
				if(!src&&copy !== undefined)//no src;
					target[name] = copy;
				else{
	            // Recurse if we're merging plain objects or arrays
		            if (copy && (this.isPlainObject(copy) || (copyIsArray = this.type(copy) == "array"))) {
		                if (copyIsArray) {
		                    copyIsArray = false;
		                    if(this.type(src) == "array")
				                target[name] = this.applyIfDeep(src, copy);
	
		                } else {
		                    if(this.isPlainObject(src))
				                target[name] = this.applyIfDeep(src, copy);
		                }
		                // Don't bring in undefined values
		            }
				}
	        }
	    }
	    return target;
	}
	, applyDeepLmt: function (target, config, defaults) {
	    var t = this.type(target);
	    if (t !== "object" && t != 'function' && t != 'array') {
	        return target;
	    }
	    if (defaults) {
	        this.applyDeep(target, defaults)
	    }
	    if (config != null) {
	        // Extend the base object
	        var copyIsArray;
	        for (name in target) {
	            src = target[name];
	            copy = config[name];

	            // Prevent never-ending loop
	            if (target === copy) {
	                continue;
	            }

	            // Recurse if we're merging plain objects or arrays
	            if (copy && (this.isPlainObject(copy) || (copyIsArray = this.type(copy) == "array"))) {
	                if (copyIsArray) {
	                    copyIsArray = false;
	                    clone = (src && this.type(src) == "array") ? src : [];

	                } else {
	                    clone = src && this.isPlainObject(src) ? src : {};
	                }

	                // Never move original objects, clone them
	                target[name] = this.applyDeep(clone, copy);

	                // Don't bring in undefined values
	            } else if (copy !== undefined) {
	                target[name] = copy;
	            }
	        }
	    }

	    return target;
	}
	, applyLmt: function (target, config, defaults) {
	    var t = this.type(target);
	    if (t !== "object" && t != 'function' && t != 'array') {
	        return target;
	    }
	    if (defaults) {
	        this.applyLmt(target, defaults)
	    }
	    if (config != null) {
	        for (name in target) {
	        	(config[name]&&(target[name]=config[name]));
	        }
	    }
    }
	,getDirectory:function(fileName,level,isStyle){
		if(!level)level=1;
		var elems = document.getElementsByTagName(isStyle?'style':'script');
		var bsurl="";
		for( i=0,len1=elems.length; i<len1; i++ ){
			if (elems[i].src && elems[i].src.indexOf(fileName)!=-1 ) {
				var src = unescape( elems[i].src ); // use unescape for utf-8 encoded urls
				var end=src.length;
				for(var j=0;j<level;j++){
					end=src.lastIndexOf('/',end-1);
				}
				src = src.substring(0, end+1);
				bsurl = src;
				break;
			}
		}	
		return bsurl;
	}
	, loadScript: function (src) {
	    var dom = document.createElement("script");
	    dom.setAttribute("src", src);
	    dom.setAttribute("type", "text/javascript");
	    var head = document.head || document.getElementsByTagName("head")[0] || document.documentElement;
	    head.insertBefore(dom, head.firstChild);
	}  
    });

    var EXTLIB = Ext.lib;
    var G = function (v) {
        return typeof v !== 'undefined';
    };

    EXTLIB.Anim.relMotion = function (el, args, duration, easing, cb, scope) {
        return EXTLIB.Anim.run(el, args, duration, easing, cb, scope, EXTLIB.RelMotion);
    };

    Ext.apply(Ext.dd.DragDropMgr, {
        enumerate: function (eleList, processor) {
            if (Object.prototype.toString.call(eleList) != "[object Array]")
                eleList = [eleList];
            var elLen = eleList.length;
            while (elLen--) {
                var element = eleList[elLen];
                var dds = element.getDDs();
                for (var i in dds) {
                    processor(dds[i], element);
                }
            }
        }
        , refreshLocation: function (eleList) {
            var ths = this;
            var grps = {};
            this.enumerate(eleList, function (dd, element) {
                Ext.apply(grps, dd.groups);
                //                var grps = dd.groups;
                //                for (var j in grps)
                //                    ths.locationCache[element.id] = ths.getLocation(ths.ids[j][element.id]);
            });
            this.refreshCache(grps);
        }
        , lockEls: function (eleList) {
            this.enumerate(eleList, function (dd, element) {
                dd.locked = true;
            });
        }
        , unlockEls: function (eleList) {
            this.enumerate(eleList, function (dd, element) {
                dd.locked = false;
            });
        }

    });

    EXTLIB.RelMotion = Ext.extend(EXTLIB.Motion, {
        setRuntimeAttribute: function (S) {
            if (this.patterns.points.test(S)) {
                var K = this.getEl();
                var M = this.attributes;
                var J;
                var O = M["points"]["control"] || [];
                var L;
                var P, R;
                if (O.length > 0 && !Ext.isArray(O[0])) {
                    O = [O]
                } else {
                    var N = [];
                    for (P = 0, R = O.length; P < R; ++P) {
                        N[P] = O[P]
                    }
                    O = N
                }
                Ext.fly(K).position();
                if (G(M["points"]["from"])) {
                    Ext.fly(K).setLeftTop(M["points"]["from"][0], M["points"]["from"][1])
                } else {
                    Ext.fly(K).setLeftTop(K.offsetLeft, K.offsetTop)
                }
                J = this.getAttribute("points");
                if (G(M["points"]["to"])) {
                    //L = E.call(this, M["points"]["to"], J);
                    L = M["points"]["to"];
                    //var Q = Ext.lib.Dom.getXY(this.getEl());
                    //                    for (P = 0, R = O.length; P < R; ++P) {
                    //                        O[P] = E.call(this, O[P], J);
                    //                    }
                } else {
                    if (G(M["points"]["by"])) {
                        L = [J[0] + M["points"]["by"][0], J[1] + M["points"]["by"][1]];
                        for (P = 0, R = O.length; P < R; ++P) {
                            O[P] = [J[0] + O[P][0], J[1] + O[P][1]]
                        }
                    }
                }
                this.runtimeAttributes[S] = [J];
                if (O.length > 0) {
                    this.runtimeAttributes[S] = this.runtimeAttributes[S].concat(O)
                }
                this.runtimeAttributes[S][this.runtimeAttributes[S].length] = L
            } else {
                I.setRuntimeAttribute.call(this, S)
            }
        }
    });
    Ext.apply(Ext.Element.prototype, {
        getDDs: function () {
            (this.dds || (this.dds = {}));
            return this.dds;
        }
        , fireEvent: function (evt) {
            var fireOnThis = this.dom;
            if (document.createEvent) {
                var evObj = document.createEvent("MouseEvents");
                evObj.initEvent(evt, true, false);
                fireOnThis.dispatchEvent(evObj);
            }
            else if (document.createEventObject) {
                fireOnThis.fireEvent("on" + evt);
            }
        }
        , Shift: function (o, callback) {
            var el = this.getFxEl();
            o = o || {};
            el.queueFx(o, function () {
                var a = {}, w = o.width, h = o.height, x = o.x, y = o.y, op = o.opacity;
                if (w !== undefined) {
                    a.width = { to: this.adjustWidth(w) };
                }
                if (h !== undefined) {
                    a.height = { to: this.adjustHeight(h) };
                }
                if (x !== undefined || y !== undefined) {
                    a.points = { to: [
                    x !== undefined ? x : this.getX(),
                    y !== undefined ? y : this.getY()
                ]
                    };
                }
                if (op !== undefined) {
                    a.opacity = { to: op };
                }
                if (o.xy !== undefined) {
                    a.points = { to: o.xy };
                }
                this.fxanim(a,
                o, 'motion', .35, "easeOut", function () {
                    el.afterFx(o);
                    (callback && callback()); //add callback;
                });
            });
            return this;
        }
        , relShift: function (o, callback) {
            var el = this.getFxEl();
            o = o || {};
            el.queueFx(o, function () {
                var a = {}, w = o.width, h = o.height, x = o.x, y = o.y, op = o.opacity;
                if (w !== undefined) {
                    a.width = { to: this.adjustWidth(w) };
                }
                if (h !== undefined) {
                    a.height = { to: this.adjustHeight(h) };
                }
                if (x !== undefined || y !== undefined) {
                    a.points = { to: [
                    x !== undefined ? x : this.getLeft(), //change to relative position;
                    y !== undefined ? y : this.getTop()
                ]
                    };
                }
                if (op !== undefined) {
                    a.opacity = { to: op };
                }
                if (o.xy !== undefined) {
                    a.points = { to: o.xy };
                }
                this.fxanim(a,
                o, 'relMotion', .35, "easeOut", function () {
                    el.afterFx(o);
                    (callback && callback());
                });
            });
            return this;
        }
    });

    /*
     * 可自动消失的提示框
     * 
     */
    Ext.Toast = function() {  
    	  
    };  
    Ext.Toast.LongTime = 3000;  
    Ext.Toast.ShortTime = 1500;  
    Ext.Toast.show = function(msgText, config) {  
    	var time = config.time?config.time:Ext.Toast.ShortTime;
    	var msgCfg = {  
			msg : msgText,  
			closable : false ,
			maxWidth:420,
			maxHeight:170
		};
    	Ext.applyDeep(msgCfg,config);
        Ext.MessageBox.show(msgCfg);  
        setTimeout(function() {  
			Ext.MessageBox.hide(); 
			if(typeof(msgCfg.callback)=='function'){
				msgCfg.callback(msgCfg.options);
			}
		}, time);  
    }
	
	Ext.CMPP={
		alert:function(title,msg,config){
			var cfg = Ext.applyDeep({
				title:title,
				buttons: Ext.Msg.OK,
				icon: Ext.MessageBox.INFO,  
				time:1000
			},config);
			Ext.Toast.show(msg,cfg);		
		},
		warn:function(title,msg){
			Ext.Msg.show({
			   title:title,
			   msg: msg,
			   buttons: Ext.Msg.OK,
			   //animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.ERROR  
			});		
		},
		/*
		* 将json对象转换成格式化的json字符串（可读性强）	
		*/	
		JsonUti:{
			//定义换行符
			n: "\n",
			//定义制表符
			t: "\t",
			m :{
				"\b": '\\b',
				"\t": '\\t',
				"\n": '\\n',
				"\f": '\\f',
				"\r": '\\r',
				'"' : '\\"',
				"\\": '\\\\'
			},
			//转换String
			toString: function(obj) {
				return this.__writeObj(obj, 1);
			},
			//写对象
			__writeObj: function(obj //对象
			, level //层次（基数为1）
			, isInArray) { //此对象是否在一个集合内
				//如果为空，直接输出null
				if (obj == null) {
					return "null";
				}
				//为普通类型，直接输出值
				if (obj.constructor == Number || obj.constructor == Date || obj.constructor == String || obj.constructor == Boolean) {
					var v = obj.toString();
					var tab = isInArray ? this.__repeatStr(this.t, level - 1) : "";
					if (obj.constructor == String || obj.constructor == Date) {
						//时间格式化只是单纯输出字符串，而不是Date对象
						//return tab + ("\"" + v.replace(/'/g,"\\'").replace(/"/g,'\\"') + "\"");
						return tab + ("\"" + this.__encodeString(v) + "\"");
					}
					else if (obj.constructor == Boolean) {
						return tab + v.toLowerCase();
					}
					else {
						return tab + (v);
					}
				}
				//写Json对象，缓存字符串
				var currentObjStrings = [];
				//遍历属性
				for (var name in obj) {
					var temp = [];
					//格式化Tab
					var paddingTab = this.__repeatStr(this.t, level);
					temp.push(paddingTab);
					//写出属性名
					temp.push('"' + name + '"' + " : ");
					var val = obj[name];
					if (val == null) {
						temp.push("null");
					}
					else {
						var c = val.constructor;
						if (c == Array) { //如果为集合，循环内部对象
							temp.push(this.n + paddingTab + "[" + this.n);
							//temp.push("[" + this.n);
							var levelUp = level + 2; //层级+2
							var tempArrValue = []; //集合元素相关字符串缓存片段
							for (var i = 0; i < val.length; i++) {
								//递归写对象
								tempArrValue.push(this.__writeObj(val[i], levelUp, true));
							}
							temp.push(tempArrValue.join("," + this.n));
							temp.push(this.n + paddingTab + "]");
						}
						else if (c == Function) {
							temp.push("[Function]");
						}
						else {
							//递归写对象
							temp.push(this.__writeObj(val, level + 1));
						}
					}
					//加入当前对象“属性”字符串
					currentObjStrings.push(temp.join(""));
				}
				return  (level > 1 && !isInArray ? this.n: "") //如果Json对象是内部，就要换行格式化
				+ this.__repeatStr(this.t, level - 1) + "{" + this.n //加层次Tab格式化
				//+ "{" + this.n //加层次Tab格式化 cds modify
				+ currentObjStrings.join("," + this.n) //串联所有属性值
				+ this.n + this.__repeatStr(this.t, level - 1) + "}"; //封闭对象
			},
			__isArray: function(obj) {
				if (obj) {
					return obj.constructor == Array;
				}
				return false;
			},
			__repeatStr: function(str, times) {
				var newStr = [];
				if (times > 0) {
					for (var i = 0; i < times; i++) {
						newStr.push(str);
					}
				}
				return newStr.join("");
			},
			__encodeString:function(s){
				if (/["\\\x00-\x1f]/.test(s)) {
					var m = this.m;
					return '' + s.replace(/([\x00-\x1f\\"])/g, function(a, b) {
						var c = m[b];
						if(c){
							return c;
						}
						c = b.charCodeAt();
						return "\\u00" +
							Math.floor(c / 16).toString(16) +
							(c % 16).toString(16);
					}) + '';
				}
				//return '"' + s + '"';
				return s;
			}
		}
		//todo ...
	};


    
 Ext.ux.FormLayout = Ext.extend(Ext.layout.FormLayout, {
	setContainer : function(ct){
        Ext.ux.FormLayout.superclass.setContainer.call(this, ct);

        if(ct.hideNotes){
            this.noteStyle = "display:none";
            this.elementStyle += ";padding-right:0;";
        }else{
            if(typeof ct.noteWidth == 'number'){
                this.noteStyle = "width:"+ct.noteWidth+"px;";
                this.elementStyle += ";padding-right:"+ct.noteWidth+'px';
            }
        }
        
    },
	renderItem: function (c, position, target) {
        if (c && !c.rendered && c.inputType != 'hidden') {
            var args, tpl, ctnrId;
            if (c.isFormField) {
                args = [
                   c.id, c.fieldLabel,
                   c.labelStyle || this.labelStyle || '',
                   this.elementStyle || '',
                   typeof c.labelSeparator == 'undefined' ? this.labelSeparator : c.labelSeparator,
                   (c.itemCls || this.container.itemCls || '') + (c.hideLabel ? ' x-hide-label' : '')+ (c.hideNote ? ' x-hide-note' : ''),
                   c.clearCls || 'x-form-clear-left'
                ];
                tpl = this.fieldTpl;
                ctnrId = 'x-form-el-' + c.id;
		   }
           else {//if it's occupant div;
                tpl = Ext.DomHelper.createTemplate({ "tag": "span", cls: "x-form-item" });
           }
 
            fld = tpl.append(target, args, true);
            fld.position();
            c.render(ctnrId || fld.id);
            c.formField = fld;
            fld.dom.ctrl = c;//dom is used for "previousSibling"
            fld.ctnrId = this.container.id;
            
			//创建说明:
			var noteTpl = new Ext.Template(
				'<span class="x-form-item-note" style="{style}">{note}</span>'
			);
			c.noteEl = noteTpl.append(ctnrId || fld.id,{"style":(c.hideNote?"display:none;":(c.noteStyle||"")),"note":c.fieldNote||""});
			
             if (c.isFormField) {
                c.labelBox=Ext.fly(c.noteEl).parent(".x-form-item").child("label.x-form-item-label").dom;
            	c.setFieldLabel = this.setFieldLabel;
            	c.setFieldNote = this.setFieldNote;
				c.setHideLabel=this.setHideLabel;
				c.setHideNote=this.setHideNote;         
             }
        } else {
            Ext.layout.FormLayout.superclass.renderItem.apply(this, arguments);
        }
    },
	setFieldLabel : function(text) {
		Ext.fly(this.labelBox).update(text+':');
	}
	,setFieldNote : function(text) {
		Ext.fly(this.noteEl).update(text);
	}
	,setHideLabel:function(isHide){
		Ext.fly(this.labelBox).setDisplayed(!isHide);
		this.formField[isHide?"addClass":"removeClass"]("x-hide-label");
	}
	,setHideNote:function(isHide){
		//Ext.fly(this.noteBox).setVisible(!isHide);
		Ext.fly(this.noteEl).setDisplayed(!isHide);
		this.formField[isHide?"addClass":"removeClass"]("x-hide-note");
	}      
});

var t = new Ext.Template(
    '<div class="x-form-item {5}" tabIndex="-1">',
       '<label for="{0}" style="{2}" class="x-form-item-label">{1}{4}</label>',
        '<div class="x-form-element" id="x-form-el-{0}" style="{3}"></div>',
        '<div class="{6}"></div>',
    '</div>'
);
t.disableFormats = true;
t.compile();
Ext.ux.FormLayout.prototype.fieldTpl=t;
Ext.Container.LAYOUTS['xform'] = Ext.ux.FormLayout;   
    
    
Ext.ux.FormLayout2 = Ext.extend(Ext.layout.FormLayout, {//extra components in field area;
    renderItem : function(c, position, target){
        if(c && !c.rendered && c.isFormField && c.inputType != 'hidden'){
            var args = [
                   c.id, c.fieldLabel,
                   c.labelStyle||this.labelStyle||'',
                   this.elementStyle||'',
                   typeof c.labelSeparator == 'undefined' ? this.labelSeparator : c.labelSeparator,
                   (c.itemCls||this.container.itemCls||'') + (c.hideLabel ? ' x-hide-label' : ''),
                   c.clearCls || 'x-form-clear-left' 
            ];
            if(typeof position == 'number'){
                position = target.dom.childNodes[position] || null;
            }
            if(position){
                this.fieldTpl.insertBefore(position, args);
            }else{
                this.fieldTpl.append(target, args);
            }
            c.render('x-form-el-'+c.id);
            if(c.extra){
            	c.extra.renderTo=c.container;
            	c.extra.field=c;
            	c.extra = Ext.ComponentMgr.create(c.extra,'panel');
            }
        }else {
            Ext.layout.FormLayout.superclass.renderItem.apply(this, arguments);
        }
    }
});
Ext.Container.LAYOUTS['xform2'] = Ext.ux.FormLayout2;    

//saveEnable 保存按钮是否有效：默认有效
Ext.jsDebugger=function(dir,nodeId,id1,id2,stype,saveEnable,tpl){
	return function(fld){
		var field=fld;
		var win = new Ext.Window({
			title:'脚本编辑器',
			height:600,
			width:800, 
			modal: true,
			buttonAlign: "center",
			closable:true ,
			closeAction:'hide',
			maximizable:true,
			minimizable:true,
			layout:'fit'
		});
		var editor=null;
		var iname="iframe"+(new Date()).valueOf();
	
		var iframe=new Ext.BoxComponent({
			autoEl:{tag:"iframe",name:iname,src:dir+"scriptdebug.jhtml?nodeId="+nodeId+"&id1="+id1+"&id2="+id2+"&stype="+stype+ (saveEnable==false?"&saveEnable="+saveEnable:"") + (tpl?"&tpl=" +　tpl:"")}
			,anchor:"100%"		
		});
		
		win.add(iframe);
		win.addButton("确定", function(){
			field.setValue(editor.getScript());
			win.hide();
		});
		win.addButton("取消",  function(){
			win.hide();
		});
		
		
		this.debug=function(){
			editor.setScript(field.getValue());
			win.show();
			win.maximize();
		};
		win.show();
		win.maximize();
		iframe.el.dom.onload=function(){
			editor=window.frames[iname].Editor;
			editor.initScript=field.getValue();
		}				
	}
}


Ext.grid.CheckColumn = function(config){
	Ext.apply(this, config);
	if(!this.id){
		this.id = Ext.id();
	}
	this.renderer = this.renderer.createDelegate(this);
};

Ext.grid.CheckColumn.prototype ={
	init : function(grid){
		this.grid = grid;
		this.grid.on('render', function(){
			var view = this.grid.getView();
			view.mainBody.on('mousedown', this.onMouseDown, this);
		}, this);
	},

	onMouseDown : function(e, t){
		if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
			e.stopEvent();
			var index = this.grid.getView().findRowIndex(t);
			var record = this.grid.store.getAt(index);
			record.set(this.dataIndex, !record.data[this.dataIndex]);
		}
	},

	renderer : function(v, p, record){
		p.css += ' x-grid3-check-col-td'; 
		return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
	}
};	

/*
*grid宽度自适应插件
*/
Ext.ns("Ext.grid.plugins");

Ext.grid.plugins.AutoResize = Ext.extend(Ext.util.Observable,{
	init:function(grid){
		grid.applyToMarkup = function(el){
			grid.render(el);
		}
		var containerId = Ext.get(grid.renderTo || grid.applyTo).id;
		if(Ext.isIE){
			Ext.get(containerId).on("resize",function(){
				grid.setWidth.defer(100,grid,[Ext.get(containerId).getWidth()]);
			});
		}else{
			window.onresize = function(){
				var ct = Ext.get(containerId);
				grid.setWidth(Ext.isSafari3?ct.getWidth():ct.getWidth()-ct.getPadding("l")-ct.getPadding("r"));
			}
		}
	}
});

/**
* 添加BasicForm的getFieldValues方法
*参数dirtyOnly：只回去修改过的字段数据
*author:cici
*/
Ext.override(Ext.form.BasicForm,{
	getFieldValues : function(dirtyOnly){
       var o = {},
           n,
           key,
           val;
       this.items.each(function(f) {
           if (dirtyOnly !== true || f.isDirty()) {
               n = f.getName();
               key = o[n];
               val = f.getValue();
               if(typeof key !== 'undefined'){
                   if(Ext.isArray(key)){
                       o[n].push(val);
                   }else{
                       o[n] = [key, val];
                   }
               }else{
                   o[n] = val;
               }
           }
       });
       return o;
	}
});

/**
* ext tooltip鼠标点击显示提示说明
*author:cici
*/
Ext.ns("Ext.ux");
Ext.ux.ToolTip=Ext.extend(Ext.ToolTip,{
	initTarget : function(){
		var t = Ext.get(this.target);
		if(t){
			t.un('click', this.onTargetOver, this);// 把鼠标移动事件改成点击事件
			t.un('mouseout', this.onTargetOut, this);
			t.un('mousemove', this.onMouseMove, this);
			t.on('click', this.onTargetOver, this);// 
			t.on('mouseout', this.onTargetOut, this);
			t.on('mousemove', this.onMouseMove, this);
			this.target = t;
		}
		if(this.anchor){
			this.anchorTarget = this.target;
		}
	}
});

/**
* 增加combobox的getText方法
*author:cici
*/
Ext.override(Ext.form.ComboBox,{
	getText:function(){
		return this.el.dom.value;
	}
});	

/**
* 覆写Ext.form下控件的hide,show方法
*author:cici
*/
Ext.override(Ext.form.Field,{
	hide:function(){
		if(!this.inEditor && this.el){
			var el = this.el.parent();
			while(!el.hasClass('x-form-item') && el.dom.tagName!=="form"){
				el = el.parent();
			}
			el.dom.style.display = "none";	
		}else{
			Ext.form.Field.superclass.hide.call(this);
		}
	},
	show:function(){
		if(!this.inEditor && this.el){
			var el = this.el.parent();
			while(el!=null && !el.hasClass('x-form-item') && el.dom.tagName!=="form"){
				if(el) el = el.parent();
				else break;
			}
			el.dom.style.display = "block";	
		}else{
			Ext.form.Field.superclass.show.call(this);
			//this.constructor.superclass.show.call(this);
		}
	}
});	

/**
* 增加TextField增加语音录入功能
*author:cici
*/
Ext.override(Ext.form.TextField,{
	onRender:function(ct,pos){
		Ext.form.TextField.superclass.onRender.call(this,ct,pos);	
		this.speechEnable && this.el.set({
			"x-webkit-speech":"x-webkit-speech",
			"lang":this.speechLang || "zh-CN"
		});
	}
});	


})();
