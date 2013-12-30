(function() {
	FBL={};
    (function() {
        this.pixelsPerInch = null;
        var namespaces = [];
        this.ns = function(fn) {
            var ns = {};
            namespaces.push(fn, ns);
            return ns
        };
        var FBTrace = null;
        this.initialize = function() {
            
            for (var i = 0; i < namespaces.length; i += 2) {
                var fn = namespaces[i];
                var ns = namespaces[i + 1];
                fn.apply(ns)
            }
			FBL.Inspector.initialize();
			FBL.toolbar.initialize();
			FBL.HTML.initialize()
        };
		this.append = function(l, r) {
            for (var n in r) {
                l[n] = r[n]
            }
            return l
        };  	
		var entityConversionLists = this.entityConversionLists = {
            normal: {
                whitespace: {
                    "\t": "\u200c\u2192",
                    "\n": "\u200c\u00b6",
                    "\r": "\u200c\u00ac",
                    " ": "\u200c\u00b7"
                }
            },
            reverse: {
                whitespace: {
                    "&Tab;": "\t",
                    "&NewLine;": "\n",
                    "\u200c\u2192": "\t",
                    "\u200c\u00b6": "\n",
                    "\u200c\u00ac": "\r",
                    "\u200c\u00b7": " "
                }
            }
        };
        var normal = entityConversionLists.normal,
        reverse = entityConversionLists.reverse;
        function addEntityMapToList(ccode, entity) {
            var lists = Array.prototype.slice.call(arguments, 2),
            len = lists.length,
            ch = String.fromCharCode(ccode);
            for (var i = 0; i < len; i++) {
                var list = lists[i];
                normal[list] = normal[list] || {};
                normal[list][ch] = "&" + entity + ";";
                reverse[list] = reverse[list] || {};
                reverse[list]["&" + entity + ";"] = ch
            }
        }
        var e = addEntityMapToList,
        white = "whitespace",
        text = "text",
        attr = "attributes",
        css = "css",
        editor = "editor";
        e(34, "quot", attr, css);
        e(38, "amp", attr, text, css);
        e(39, "apos", css);
        e(60, "lt", attr, text, css);
        e(62, "gt", attr, text, css);
        e(169, "copy", text, editor);
        e(174, "reg", text, editor);
        e(8482, "trade", text, editor);
        e(8210, "#8210", attr, text, editor);
        e(8211, "ndash", attr, text, editor);
        e(8212, "mdash", attr, text, editor);
        e(8213, "#8213", attr, text, editor);
        e(160, "nbsp", attr, text, white, editor);
        e(8194, "ensp", attr, text, white, editor);
        e(8195, "emsp", attr, text, white, editor);
        e(8201, "thinsp", attr, text, white, editor);
        e(8204, "zwnj", attr, text, white, editor);
        e(8205, "zwj", attr, text, white, editor);
        e(8206, "lrm", attr, text, white, editor);
        e(8207, "rlm", attr, text, white, editor);
        e(8203, "#8203", attr, text, white, editor);
        var entityConversionRegexes = {
            normal: {},
            reverse: {}
        };
        var escapeEntitiesRegEx = {
            normal: function(list) {
                var chars = [];
                for (var ch in list) {
                    chars.push(ch)
                }
                return new RegExp("([" + chars.join("") + "])", "gm")
            },
            reverse: function(list) {
                var chars = [];
                for (var ch in list) {
                    chars.push(ch)
                }
                return new RegExp("(" + chars.join("|") + ")", "gm")
            }
        };
        function getEscapeRegexp(direction, lists) {
            var name = "",
            re;
            var groups = [].concat(lists);
            for (i = 0; i < groups.length; i++) {
                name += groups[i].group
            }
            re = entityConversionRegexes[direction][name];
            if (!re) {
                var list = {};
                if (groups.length > 1) {
                    for (var i = 0; i < groups.length; i++) {
                        var aList = entityConversionLists[direction][groups[i].group];
                        for (var item in aList) {
                            list[item] = aList[item]
                        }
                    }
                } else {
                    if (groups.length == 1) {
                        list = entityConversionLists[direction][groups[0].group]
                    } else {
                        list = {}
                    }
                }
                re = entityConversionRegexes[direction][name] = escapeEntitiesRegEx[direction](list)
            }
            return re
        }
        function createSimpleEscape(name, direction) {
            return function(value) {
                var list = entityConversionLists[direction][name];
                return String(value).replace(getEscapeRegexp(direction, {
                    group: name,
                    list: list
                }),
                function(ch) {
                    return list[ch]
                })
            }
        }
        function escapeGroupsForEntities(str, lists) {
            lists = [].concat(lists);
            var re = getEscapeRegexp("normal", lists),
            split = String(str).split(re),
            len = split.length,
            results = [],
            cur,
            r,
            i,
            ri = 0,
            l,
            list,
            last = "";
            if (!len) {
                return [{
                    str: String(str),
                    group: "",
                    name: ""
                }]
            }
            for (i = 0; i < len; i++) {
                cur = split[i];
                if (cur == "") {
                    continue
                }
                for (l = 0; l < lists.length; l++) {
                    list = lists[l];
                    r = entityConversionLists.normal[list.group][cur];
                    if (r) {
                        results[ri] = {
                            str: r,
                            "class": list["class"],
                            extra: list.extra[cur] ? list["class"] + list.extra[cur] : ""
                        };
                        break
                    }
                }
                if (!r) {
                    results[ri] = {
                        str: cur,
                        "class": "",
                        extra: ""
                    }
                }
                ri++
            }
            return results
        }
        this.escapeGroupsForEntities = escapeGroupsForEntities;
        function unescapeEntities(str, lists) {
            var re = getEscapeRegexp("reverse", lists),
            split = String(str).split(re),
            len = split.length,
            results = [],
            cur,
            r,
            i,
            ri = 0,
            l,
            list;
            if (!len) {
                return str
            }
            lists = [].concat(lists);
            for (i = 0; i < len; i++) {
                cur = split[i];
                if (cur == "") {
                    continue
                }
                for (l = 0; l < lists.length; l++) {
                    list = lists[l];
                    r = entityConversionLists.reverse[list.group][cur];
                    if (r) {
                        results[ri] = r;
                        break
                    }
                }
                if (!r) {
                    results[ri] = cur
                }
                ri++
            }
            return results.join("") || ""
        }
        var escapeForTextNode = this.escapeForTextNode = createSimpleEscape("text", "normal");
        var escapeForHtmlEditor = this.escapeForHtmlEditor = createSimpleEscape("editor", "normal");
        var escapeForElementAttribute = this.escapeForElementAttribute = createSimpleEscape("attributes", "normal");
        var escapeForCss = this.escapeForCss = createSimpleEscape("css", "normal");
        var escapeForSourceLine = this.escapeForSourceLine = createSimpleEscape("text", "normal");
        var unescapeWhitespace = createSimpleEscape("whitespace", "reverse");
        this.unescapeForTextNode = function(str) {
            if (Firebug.showTextNodesWithWhitespace) {
                str = unescapeWhitespace(str)
            }
            if (!Firebug.showTextNodesWithEntities) {
                str = escapeForElementAttribute(str)
            }
            return str
        };
        this.escapeNewLines = function(value) {
            return value.replace(/\r/g, "\\r").replace(/\n/g, "\\n")
        };
        this.stripNewLines = function(value) {
            return typeof(value) == "string" ? value.replace(/[\r\n]/g, " ") : value
        };
        this.escapeJS = function(value) {
            return value.replace(/\r/g, "\\r").replace(/\n/g, "\\n").replace('"', '\\"', "g")
        };
        function escapeHTMLAttribute(value) {
            function replaceChars(ch) {
                switch (ch) {
                case "&":
                    return "&amp;";
                case "'":
                    return apos;
                case '"':
                    return quot
                }
                return "?"
            }
            var apos = "&#39;",
            quot = "&quot;",
            around = '"';
            if (value.indexOf('"') == -1) {
                quot = '"';
                apos = "'"
            } else {
                if (value.indexOf("'") == -1) {
                    quot = '"';
                    around = "'"
                }
            }
            return around + (String(value).replace(/[&'"]/g, replaceChars)) + around
        }
        function escapeHTML(value) {
            function replaceChars(ch) {
                switch (ch) {
                case "<":
                    return "&lt;";
                case ">":
                    return "&gt;";
                case "&":
                    return "&amp;";
                case "'":
                    return "&#39;";
                case '"':
                    return "&quot;"
                }
                return "?"
            }
            return String(value).replace(/[<>&"']/g, replaceChars)
        }
        this.escapeHTML = escapeHTML;
        this.cropString = function(text, limit) {
            text = text + "";
            if (!limit) {
                var halfLimit = 50
            } else {
                var halfLimit = limit / 2
            }
            if (text.length > limit) {
                return this.escapeNewLines(text.substr(0, halfLimit) + "..." + text.substr(text.length - halfLimit))
            } else {
                return this.escapeNewLines(text)
            }
        };
        this.isWhitespace = function(text) {
            return ! reNotWhitespace.exec(text)
        };
        this.splitLines = function(text) {
            var reSplitLines2 = /.*(:?\r\n|\n|\r)?/mg;
            var lines;
            if (text.match) {
                lines = text.match(reSplitLines2)
            } else {
                var str = text + "";
                lines = str.match(reSplitLines2)
            }
            lines.pop();
            return lines
        };
        this.safeToString = function(ob) {
            if (this.isIE) {
                return ob + ""
            }
            try {
                if (ob && "toString" in ob && typeof ob.toString == "function") {
                    return ob.toString()
                }
            } catch(exc) {
                return ob + ""
            }
        };
        var getElementType = this.getElementType = function(node) {
            if (isElementXUL(node)) {
                return "xul"
            } else {
                if (isElementSVG(node)) {
                    return "svg"
                } else {
                    if (isElementMathML(node)) {
                        return "mathml"
                    } else {
                        if (isElementXHTML(node)) {
                            return "xhtml"
                        } else {
                            if (isElementHTML(node)) {
                                return "html"
                            }
                        }
                    }
                }
            }
        };
        var getElementSimpleType = this.getElementSimpleType = function(node) {
            if (isElementSVG(node)) {
                return "svg"
            } else {
                if (isElementMathML(node)) {
                    return "mathml"
                } else {
                    return "html"
                }
            }
        };
        var isElementHTML = this.isElementHTML = function(node) {
            return node.nodeName == node.nodeName.toUpperCase()
        };
        var isElementXHTML = this.isElementXHTML = function(node) {
            return node.nodeName == node.nodeName.toLowerCase()
        };
        var isElementMathML = this.isElementMathML = function(node) {
            return node.namespaceURI == "http://www.w3.org/1998/Math/MathML"
        };
        var isElementSVG = this.isElementSVG = function(node) {
            return node.namespaceURI == "http://www.w3.org/2000/svg"
        };
        var isElementXUL = this.isElementXUL = function(node) {
            return node instanceof XULElement
        };
        this.isSelfClosing = function(element) {
            if (isElementSVG(element) || isElementMathML(element)) {
                return true
            }
            var tag = element.localName.toLowerCase();
            return (this.selfClosingTags.hasOwnProperty(tag))
        };
        this.getElementHTML = function(element) {
            var self = this;
            function toHTML(elt) {
                if (elt.nodeType == Node.ELEMENT_NODE) {

                    html.push("<", elt.nodeName.toLowerCase());
                    for (var i = 0; i < elt.attributes.length; ++i) {
                        var attr = elt.attributes[i];
                        if (attr.localName.indexOf("firebug-") == 0) {
                            continue
                        }
                        if (attr.localName.indexOf("-moz-math") == 0) {
                            continue
                        }
                        html.push(" ", attr.nodeName, '="', escapeForElementAttribute(attr.nodeValue), '"')
                    }
                    if (elt.firstChild) {
                        html.push(">");
                        var pureText = true;
                        for (var child = element.firstChild; child; child = child.nextSibling) {
                            pureText = pureText && (child.nodeType == Node.TEXT_NODE)
                        }
                        if (pureText) {
                            html.push(escapeForHtmlEditor(elt.textContent))
                        } else {
                            for (var child = elt.firstChild; child; child = child.nextSibling) {
                                toHTML(child)
                            }
                        }
                        html.push("</", elt.nodeName.toLowerCase(), ">")
                    } else {
                        if (isElementSVG(elt) || isElementMathML(elt)) {
                            html.push("/>")
                        } else {
                            if (self.isSelfClosing(elt)) {
                                html.push((isElementXHTML(elt)) ? "/>": ">")
                            } else {
                                html.push("></", elt.nodeName.toLowerCase(), ">")
                            }
                        }
                    }
                } else {
                    if (elt.nodeType == Node.TEXT_NODE) {
                        html.push(escapeForTextNode(elt.textContent))
                    } else {
                        if (elt.nodeType == Node.CDATA_SECTION_NODE) {
                            html.push("<![CDATA[", elt.nodeValue, "]]>")
                        } else {
                            if (elt.nodeType == Node.COMMENT_NODE) {
                                html.push("<!--", elt.nodeValue, "-->")
                            }
                        }
                    }
                }
            }
            var html = [];
            toHTML(element);
            return html.join("")
        };
        
		this.selfClosingTags = {
            meta: 1,
            link: 1,
            area: 1,
            base: 1,
            col: 1,
            input: 1,
            img: 1,
            br: 1,
            hr: 1,
            param: 1,
            embed: 1
        };
        
    }).apply(FBL);

	/**********xpath****************/
	
	(function() {
        this.getElementXPath = function(element) {
            if (element && element.id) {
                return '//*[@id="' + element.id + '"]'
            } else {
                return this.getElementTreeXPath(element)
            }
        };
        this.getElementTreeXPath = function(element) {
            var paths = [];
            for (; element && element.nodeType == 1; element = element.parentNode) {
                var index = 0;
                for (var sibling = element.previousSibling; sibling; sibling = sibling.previousSibling) {
                    if (sibling.nodeName == element.nodeName) {++index
					//todo:过滤掉自己添加的内嵌遮罩层
                    }
                }
                var tagName = element.nodeName.toLowerCase();
                var pathIndex = (index ? "[" + (index + 1) + "]": "");
                paths.splice(0, 0, tagName + pathIndex)
            }
            return paths.length ? "/" + paths.join("/") : null
        };
        this.getElementXPath2 = function(element) {
            return this.getElementTreeXPath2(element)
        };
        this.getElementTreeXPath2 = function(element) {
            var paths = [];
            for (; element && element.nodeType == 1; element = element.parentNode) {
                var index = 0;
                for (var sibling = element.previousSibling; sibling; sibling = sibling.previousSibling) {
                    if(sibling.nodeType==1) {
						++index
					}
					//todo:过滤掉自己添加的内嵌遮罩层
                }
                var tagName = element.nodeName.toLowerCase();
                var pathIndex = (index ? ":eq(" + index + ")": "");
                paths.splice(0, 0, tagName + pathIndex)
            }
            return paths.length ?  paths.join(">") : null
        };		
        this.getElementsByXPath = function(doc, xpath) {
            var nodes = [];
            try {
                var result = doc.evaluate(xpath, doc, null, XPathResult.ANY_TYPE, null);
                for (var item = result.iterateNext(); item; item = result.iterateNext()) {
                    nodes.push(item)
                }
            } catch(exc) {}
            return nodes
        };
        this.getRuleMatchingElements = function(rule, doc) {
            var css = rule.selectorText;
            var xpath = this.cssToXPath(css);
            return this.getElementsByXPath(doc, xpath)
        }
    }).call(FBL);
	
    FBL.ns(function() {
        with(FBL) {
            FBL.Cache = {
                ID: "cici" + new Date().getTime()
            };
            var cacheUID = 0;
            var createCache = function() {
                var map = {};
                var CID = FBL.Cache.ID;
                var supportsDeleteExpando = !document.all;
                var cacheFunction = function(element) {
                    return cacheAPI.set(element)
                };
                var cacheAPI = {
                    get: function(key) {
                        return map.hasOwnProperty(key) ? map[key] : null
                    },
                    set: function(element) {
                        var id = element[CID];
                        if (!id) {
                            id = ++cacheUID;
                            element[CID] = id
                        }
                        if (!map.hasOwnProperty(id)) {
                            map[id] = element
                        }
                        return id
                    },
                    unset: function(element) {
                        var id = element[CID];
                        if (supportsDeleteExpando) {
                            delete element[CID]
                        } else {
                            if (element.removeAttribute) {
                                element.removeAttribute(CID)
                            }
                        }
                        delete map[id]
                    },
                    key: function(element) {
                        return element[CID]
                    },
                    has: function(element) {
                        return map.hasOwnProperty(element[CID])
                    },
                    clear: function() {
                        for (var id in map) {
                            var element = map[id];
                            cacheAPI.unset(element)
                        }
                    }
                };
                FBL.append(cacheFunction, cacheAPI);
                return cacheFunction
            };
            FBL.Cache.Element = createCache()
        }
    });
	
	FBL.ns(function() {
        with(FBL) {
			FBL.browser={	
				getWindowSize: function() {
                    var width = 0,
                    height = 0,
                    el;
                    if (typeof window.innerWidth == "number") {
                        width = window.innerWidth;
                        height = window.innerHeight
                    } else {
                        if ((el = document.documentElement) && (el.clientHeight || el.clientWidth)) {
                            width = el.clientWidth;
                            height = el.clientHeight
                        } else {
                            if ((el = document.body) && (el.clientHeight || el.clientWidth)) {
                                width = el.clientWidth;
                                height = el.clientHeight
                            }
                        }
                    }
                    return {
                        width: width,
                        height: height
                    }
                },
                getWindowScrollSize: function() {
                    var width = 0,
                    height = 0,
                    el;
                    if ((el = document.documentElement) && (el.scrollHeight || el.scrollWidth)) {
                        width = el.scrollWidth;
                        height = el.scrollHeight
                    }
                    if ((el = document.body) && (el.scrollHeight || el.scrollWidth) && (el.scrollWidth > width || el.scrollHeight > height)) {
                        width = el.scrollWidth;
                        height = el.scrollHeight
                    }
                    return {
                        width: width,
                        height: height
                    }
                },
                getWindowScrollPosition: function() {
                    var top = 0,
                    left = 0,
                    el;
                    if (typeof window.pageYOffset == "number") {
                        top = window.pageYOffset;
                        left = window.pageXOffset
                    } else {
                        if ((el = document.body) && (el.scrollTop || el.scrollLeft)) {
                            top = el.scrollTop;
                            left = el.scrollLeft
                        } else {
                            if ((el = document.documentElement) && (el.scrollTop || el.scrollLeft)) {
                                top = el.scrollTop;
                                left = el.scrollLeft
                            }
                        }
                    }
                    return {
                        top: top,
                        left: left
                    }
                },   
				getElementFromPoint: function(x, y) {
					var scroll = this.getWindowScrollPosition();
					
					return document.elementFromPoint(x-scroll.left, y-scroll.top)
				},					
				getElementPosition: function(el) {
                    var left = 0;
                    var top = 0;
                    do {
                        left += el.offsetLeft;
                        top += el.offsetTop
                    } while ( el = el.offsetParent );
                    return {
                        left: left,
                        top: top
                    }
                },
                getElementBox: function(el) {
                    var result = {};
                    if (el.getBoundingClientRect) {
                        var rect = el.getBoundingClientRect();
                        var offset = Ext.isIE ? document.body.clientTop || document.documentElement.clientTop: 0;
                        var scroll = this.getWindowScrollPosition();
                        result.top = Math.round(rect.top - offset + scroll.top);
                        result.left = Math.round(rect.left - offset + scroll.left);
                        result.height = Math.round(rect.bottom - rect.top);
                        result.width = Math.round(rect.right - rect.left)
                    } else {
                        var position = this.getElementPosition(el);
                        result.top = position.top;
                        result.left = position.left;
                        result.height = el.offsetHeight;
                        result.width = el.offsetWidth
                    }
                    return result
                },
                 getMeasurement: function(el, name) {
                    var result = {
                        value: 0,
                        unit: "px"
                    };
                    var cssValue = this.getStyle(el, name);
                    if (!cssValue) {
                        return result
                    }
                    if (cssValue.toLowerCase() == "auto") {
                        return result
                    }
                    var reMeasure = /(\d+\.?\d*)(.*)/;
                    var m = cssValue.match(reMeasure);
                    if (m) {
                        result.value = m[1] - 0;
                        result.unit = m[2].toLowerCase()
                    }
                    return result
                },
                getMeasurementInPixels: function(el, name) {
                    if (!el) {
                        return null
                    }
                    var m = this.getMeasurement(el, name);
                    var value = m.value;
                    var unit = m.unit;
                    if (unit == "px") {
                        return value
                    } else {
                        if (unit == "pt") {
                            return this.pointsToPixels(name, value)
                        }
                    }
                    if (unit == "em") {
                        return this.emToPixels(el, value)
                    } else {
                        if (unit == "%") {
                            return this.percentToPixels(el, value)
                        }
                    }
                },
                getMeasurementBox1: function(el, name) {
                    var sufixes = ["Top", "Left", "Bottom", "Right"];
                    var result = [];
                    for (var i = 0, sufix; sufix = sufixes[i]; i++) {
                        result[i] = Math.round(this.getMeasurementInPixels(el, name + sufix))
                    }
                    return {
                        top: result[0],
                        left: result[1],
                        bottom: result[2],
                        right: result[3]
                    }
                },
                getMeasurementBox: function(el, name) {
                    var result = [];
                    var sufixes = name == "border" ? ["TopWidth", "LeftWidth", "BottomWidth", "RightWidth"] : ["Top", "Left", "Bottom", "Right"];

					for (var i = 0, sufix; sufix = sufixes[i]; i++) {
						result[i] = this.getMeasurementInPixels(el, name + sufix)
					}
   
                    return {
                        top: result[0],
                        left: result[1],
                        bottom: result[2],
                        right: result[3]
                    }
                },
				getStyle: function(el, name) {
					return document.defaultView.getComputedStyle(el, null)[name] || el.style[name] || undefined
				}                 
			}
		}
	});
	
	/***Inspector**/
	FBL.ns(function() {
        with(FBL) {
			var ElementCache = FBL.Cache.Element;
            var inspectorTS, inspectorTimer, isInspecting;
			var NodeListBox;
			FBL.Inspector={
				inspectButton:null,
				initialize:function(){
					this.createToolBar();
					NodeListBox = Ext.get('fbToolbarDomList');
					this.inspectButton = Ext.get('fbToolbar_btInspect');
					this.clearButton = Ext.get('fbToolbar_btClear');
					this.inspectButton.on('mousedown',FBL.Inspector.toggleInspect,this,{obj:this.inspectButton.dom});
					this.clearButton.on('mousedown',function(obj,e){
						this.clearButton.addClass('fbBtnPressed');
					},this);
					this.clearButton.on('mouseout',function(e,obj){
						this.clearButton.removeClass('fbBtnPressed');
					},this);						
					this.clearButton.on('mouseup',FBL.Inspector.clear,this);
					offlineFragment = document.createDocumentFragment();
                    createBoxModelInspector();
                    createOutlineInspector()
				},
				createToolBar:function(){
					var html = '<div id="fbToolbarContent">\
									<span id="fbToolbarButtons">\
										<span id="fbToolbarButtons_s" style="float:left;margin:0 30px 0 210px;"><a id="fbToolbar_btInspect" class="fbButton fbHover">Inspect</a><a id="fbToolbar_btClear" class="fbButton fbHover">Clear</a></span>\
										<span id="fbToolbarDomList">\
										</span>\
										<span id="fbToolbarButtons_submit" style="float:left;margin:0 10px 0 10px;float:right">\
											<a id="fbToolbar_btFixed" title="固定/自动隐藏" style="float: right; margin-left: 5px;" class="fbButton fbHover">固定</a>\
											<input type="button" id="fbToolbar_btSubmit" style="width: 70px; margin-top: 2px;" value="提交" />\
										</span>\
									</span>\
								</div>';
					var fbToolbar = Ext.getBody().createChild({
						tag:'div',
						id:'fbToolbar',
						title:"模板可视化编辑面板",
						html:html
					});
					FBL.toolbar.panel = fbToolbar;
				},
                destroy: function() {
                    destroyBoxModelInspector();
                    destroyOutlineInspector();
                    offlineFragment = null;					
                },	
				clear:function(){
					if (outlineVisible) {
                        this.hideOutline();
                    }
                    if (boxModelVisible) {
                        this.hideBoxModel();
                    }	
					this.clearButton.removeClass('fbBtnPressed');	
					NodeListBox.update('');
				},				
				toggleInspect: function(e,obj,options){ 
					if (isInspecting) {
						this.stopInspecting();
					} else {
						this.inspectButton.addClass('fbBtnPressed');
						this.startInspecting();
					}
				},
				startInspecting:function(){
					isInspecting = true;
					
					createInspectorFrame();
					fbInspectFrame.setWidth(Ext.getBody().getWidth());
					fbInspectFrame.setHeight(Ext.getBody().getHeight());
					fbInspectFrame.on('mousemove',FBL.Inspector.onInspecting,this);
					fbInspectFrame.on('mousedown',FBL.Inspector.onInspectingClick,this);
				},
				stopInspecting:function(){
					isInspecting = false;					
                    if (outlineVisible) {
                        //this.hideOutline()
                    }
					fbInspectFrame.un("mousemove", FBL.Inspector.onInspecting);
					fbInspectFrame.un("mousedown", FBL.Inspector.onInspectingClick);
                    destroyInspectorFrame();
					this.inspectButton.removeClass('fbBtnPressed');
				},
				onInspecting:function(e,obj,options){ 
					if (new Date().getTime() - lastInspecting > 30) {
                        fbInspectFrame.setDisplayed(false);
                        var targ = FBL.browser.getElementFromPoint(e.xy[0], e.xy[1]);
						
                        fbInspectFrame.setDisplayed(true);
						if(targ==null) return;
						var id = targ.id;
						var cls = targ.className;
                        if (id && /^fbOutline\w$/.test(id)) {
                            return
                        }
                        if (id && /^fbToolbar/.test(id)) { 
                            return
                        }	
                        if (id && /^tplidx_mask/.test(id)) { 
                            return
                        }
                        if (cls=='maskTitle') { 
                            return
                        }							
						
						if(Ext.fly(targ).findParent('div#idxListContainer')){
							return;
						}
						if(Ext.fly(targ).findParent('div#fbconsoleContainer')){
							return;
						}
						if(targ.firebugIgnore){
							return;
						}
						//console.log(targ.innerHTML);//cds
                        while (targ.nodeType != 1) {
                            targ = targ.parentNode
                        }
						var nodeName = targ.nodeName.toLowerCase() ;
                        if (nodeName == "body" || nodeName=="cmpp_banner") {
                            return
                        }
                        FBL.Inspector.drawOutline(targ);
                        if (ElementCache(targ)) {
                            var target = "" + ElementCache.key(targ);
                            var lazySelect = function() {
                                inspectorTS = new Date().getTime();
                                FBL.HTML.selectTreeNode("" + ElementCache.key(targ))
                            };
                            if (inspectorTimer) {
                                clearTimeout(inspectorTimer);
                                inspectorTimer = null
                            }
                            if (new Date().getTime() - inspectorTS > 200) {
                                setTimeout(lazySelect, 0)
                            } else {
                                inspectorTimer = setTimeout(lazySelect, 300)
                            }
                        }						
						lastInspecting = new Date().getTime();
					}	
				},
				onInspectingClick:function(e,obj,options){ 
					if(FBL.toolbar.isHide) FBL.toolbar.show();
					FBL.toolbar.fixed();
					
					fbInspectFrame.setDisplayed(false);
					var targ = FBL.browser.getElementFromPoint(e.xy[0], e.xy[1]);
					fbInspectFrame.setDisplayed(true);
                    var id = targ.id;
					if (id && /^fbOutline\w$/.test(id)) {
						return
					}
					if (id && /^fbToolbar/.test(id)) { 
						return
					}	
					if (id && /^tplidx_mask/.test(id)) { 
						return
					}						
                    while (targ.nodeType != 1) {
                        targ = targ.parentNode
                    }
                    FBL.Inspector.stopInspecting();
				},
                drawOutline: function(el) {
					FBL.currentInspectedEl = el;
				
                    var border = 2;
                    var scrollbarSize = 17;
                    var windowSize = FBL.browser.getWindowSize();
                    var scrollSize = FBL.browser.getWindowScrollSize();
                    var scrollPosition = FBL.browser.getWindowScrollPosition();
                    var box = FBL.browser.getElementBox(el);
                    var top = box.top;
                    var left = box.left;
                    var height = box.height;
                    var width = box.width;
                    var freeHorizontalSpace = scrollPosition.left + windowSize.width - left - width - (scrollSize.height > windowSize.height ? scrollbarSize: 0);
                    var freeVerticalSpace = scrollPosition.top + windowSize.height - top - height - (scrollSize.width > windowSize.width ? scrollbarSize: 0);
                    var numVerticalBorders = freeVerticalSpace > 0 ? 2 : 1;
                    var o = outlineElements;
                    var style;
                    style = o.fbOutlineT.style;
                    style.top = top - border + "px";
                    style.left = left + "px";
                    style.height = border + "px";
                    style.width = width + "px";
                    style = o.fbOutlineL.style;
                    style.top = top - border + "px";
                    style.left = left - border + "px";
                    style.height = height + numVerticalBorders * border + "px";
                    style.width = border + "px";
                    style = o.fbOutlineB.style;
                    if (freeVerticalSpace > 0) {
                        style.top = top + height + "px";
                        style.left = left + "px";
                        style.width = width + "px"
                    } else {
                        style.top = -2 * border + "px";
                        style.left = -2 * border + "px";
                        style.width = border + "px"
                    }
                    style = o.fbOutlineR.style;
                    if (freeHorizontalSpace > 0) {
                        style.top = top - border + "px";
                        style.left = left + width + "px";
                        style.height = height + numVerticalBorders * border + "px";
                        style.width = (freeHorizontalSpace < border ? freeHorizontalSpace: border) + "px"
                    } else {
                        style.top = -2 * border + "px";
                        style.left = -2 * border + "px";
                        style.height = border + "px";
                        style.width = border + "px"
                    }
                    if (!outlineVisible) {
                        this.showOutline()
                    }
                },
                hideOutline: function() {
                    if (!outlineVisible) {
                        return
                    }
                    for (var name in outline) {
                        offlineFragment.appendChild(outlineElements[name])
                    }
                    outlineVisible = false
                },
                showOutline: function() {
                    if (outlineVisible) {
                        return
                    }
                    if (boxModelVisible) {
                        this.hideBoxModel()
                    }
                    for (var name in outline) {
                        document.getElementsByTagName("body")[0].appendChild(outlineElements[name])
                    }
                    outlineVisible = true
                },
                drawBoxModel: function(el) {
                    if (!el || !el.parentNode) {
                        return
                    }
                    var box = FBL.browser.getElementBox(el);
                    var windowSize = FBL.browser.getWindowSize();
                    var scrollPosition = FBL.browser.getWindowScrollPosition();
                    var offsetHeight = 0;
                    if (box.top > scrollPosition.top + windowSize.height - offsetHeight || box.left > scrollPosition.left + windowSize.width || scrollPosition.top > box.top + box.height || scrollPosition.left > box.left + box.width) {
                        return
                    }
                    var top = box.top;
                    var left = box.left;
                    var height = box.height;
                    var width = box.width;
                    var margin = FBL.browser.getMeasurementBox(el, "margin");
                    var padding = FBL.browser.getMeasurementBox(el, "padding");
                    var border = FBL.browser.getMeasurementBox(el, "border");
                    boxModelStyle.top = top - margin.top + "px";
                    boxModelStyle.left = left - margin.left + "px";
                    boxModelStyle.height = height + margin.top + margin.bottom + "px";
                    boxModelStyle.width = width + margin.left + margin.right + "px";
                    boxBorderStyle.top = margin.top + "px";
                    boxBorderStyle.left = margin.left + "px";
                    boxBorderStyle.height = height + "px";
                    boxBorderStyle.width = width + "px";
                    boxPaddingStyle.top = margin.top + border.top + "px";
                    boxPaddingStyle.left = margin.left + border.left + "px";
                    boxPaddingStyle.height = height - border.top - border.bottom + "px";
                    boxPaddingStyle.width = width - border.left - border.right + "px";
                    boxContentStyle.top = margin.top + border.top + padding.top + "px";
                    boxContentStyle.left = margin.left + border.left + padding.left + "px";
                    boxContentStyle.height = height - border.top - padding.top - padding.bottom - border.bottom + "px";
                    boxContentStyle.width = width - border.left - padding.left - padding.right - border.right + "px";
                    if (!boxModelVisible) {
                        this.showBoxModel()
                    }
                },
                hideBoxModel: function() {
                    if (!boxModelVisible) {
                        return
                    }
                    offlineFragment.appendChild(boxModel);
                    boxModelVisible = false
                },
                showBoxModel: function() {
                    if (boxModelVisible) {
                        return
                    }
                    if (outlineVisible) {
                        //this.hideOutline()
                    }
                    document.getElementsByTagName("body")[0].appendChild(boxModel);
                    boxModelVisible = true
                }
            };
            var offlineFragment = null;
            var boxModelVisible = false;
            var boxModel, boxModelStyle, boxMargin, boxMarginStyle, boxBorder, boxBorderStyle, boxPadding, boxPaddingStyle, boxContent, boxContentStyle;
            var resetStyle = "margin:0; padding:0; border:0; position:absolute; overflow:hidden; display:block;";
            var offscreenStyle = resetStyle + "top:-1234px; left:-1234px;";
            var inspectStyle = resetStyle + "z-index: 2147483500;";
            var inspectFrameStyle = resetStyle + "z-index: 2147483550; top:0; left:0; background:url(template/res/images/pixel_transparent.gif);";
            var inspectModelOpacity = "opacity:0.8;";
            var inspectModelStyle = inspectStyle + inspectModelOpacity;
            var inspectMarginStyle = inspectStyle + "background: #EDFF64; height:100%; width:100%;";
            var inspectBorderStyle = inspectStyle + "background: #666;";
            var inspectPaddingStyle = inspectStyle + "background: SlateBlue;";
            var inspectContentStyle = inspectStyle + "background: SkyBlue;";
            var outlineStyle = {
                fbHorizontalLine: "background: #3875D7;height: 2px;",
                fbVerticalLine: "background: #3875D7;width: 2px;"
            };
            var lastInspecting = 0;
            var fbInspectFrame = null;
            var outlineVisible = false;
            var outlineElements = {};
            var outline = {
                fbOutlineT: "fbHorizontalLine",
                fbOutlineL: "fbVerticalLine",
                fbOutlineB: "fbHorizontalLine",
                fbOutlineR: "fbVerticalLine"
            };
            var getInspectingTarget = function() {};
			var createInspectorFrame = function() {
                fbInspectFrame=Ext.getBody().createChild({
					tag:'div',
					id:'fbInspectFrame',
					style:inspectFrameStyle
				});
            };
            var destroyInspectorFrame = function destroyInspectorFrame() {
                if (fbInspectFrame) {
                    fbInspectFrame.remove();
                    fbInspectFrame = null
                }
            };
            var createOutlineInspector = function createOutlineInspector() {
                for (var name in outline) {
                    var el = outlineElements[name] = createGlobalElement("div");
                    el.id = name;
                    el.firebugIgnore = true;
                    el.style.cssText = inspectStyle + outlineStyle[outline[name]];
                    offlineFragment.appendChild(el)
                }
            };
            var destroyOutlineInspector = function destroyOutlineInspector() {
                for (var name in outline) {
                    var el = outlineElements[name];
                    el.parentNode.removeChild(el)
                }
            };
            var createBoxModelInspector = function createBoxModelInspector() {
                boxModel = createGlobalElement("div");
                boxModel.id = "fbBoxModel";
                boxModel.firebugIgnore = true;
                boxModelStyle = boxModel.style;
                boxModelStyle.cssText = inspectModelStyle;
                boxMargin = createGlobalElement("div");
                boxMargin.id = "fbBoxMargin";
                boxMarginStyle = boxMargin.style;
                boxMarginStyle.cssText = inspectMarginStyle;
                boxModel.appendChild(boxMargin);
                boxBorder = createGlobalElement("div");
                boxBorder.id = "fbBoxBorder";
                boxBorderStyle = boxBorder.style;
                boxBorderStyle.cssText = inspectBorderStyle;
                boxModel.appendChild(boxBorder);
                boxPadding = createGlobalElement("div");
                boxPadding.id = "fbBoxPadding";
                boxPaddingStyle = boxPadding.style;
                boxPaddingStyle.cssText = inspectPaddingStyle;
                boxModel.appendChild(boxPadding);
                boxContent = createGlobalElement("div");
                boxContent.id = "fbBoxContent";
                boxContentStyle = boxContent.style;
                boxContentStyle.cssText = inspectContentStyle;
                boxModel.appendChild(boxContent);
                offlineFragment.appendChild(boxModel)
            };
            var destroyBoxModelInspector = function destroyBoxModelInspector() {
                boxModel.parentNode.removeChild(boxModel)
            };
			var createGlobalElement = function(tagName){
				return document.createElement(tagName);
			};
			
					
		}
	});
	
	/****HTML***/
	FBL.ns(function() {
		with(FBL) {
			var ElementCache = FBL.Cache.Element;
			var NodeListBox;
			var cacheID = FBL.Cache.ID;
			FBL.HTML = {
				initialize:function(){
					NodeListBox = Ext.get('fbToolbarDomList');
					NodeListBox.on("mouseover", FBL.HTML.onListMouseMove);
					NodeListBox.on("mouseout", FBL.HTML.onListMouseOut);
					NodeListBox.on("click", FBL.HTML.onListMouseClick);
					
				},
                selectTreeNode: function(id) {
                    id = "" + id;
                    var node, stack = [];
                    while (id && !document.getElementById(id)) {
                        stack.push(id);
                        var node = ElementCache.get(id).parentNode;
                        if (node) {
                            id = ElementCache(node)
                        } else {
                            break
                        }
                    }
                    stack.push(id);
					
					var nodelistHtml='';
                    while (stack.length > 0) {
                        id = stack.pop();
                        node = ElementCache.get(id);
						var nodeName = node.nodeName.toLowerCase();
                        if (stack.length >= 0 && nodeName!='#document' && nodeName!='html'  && nodeName!='cmpp_banner') {
							/*
							if(nodeName!='body'){
								if(node.id && !/^ext-gen/.test(node.id)) {
									nodeName+='#'+node.id;
								}else if(node.className) {
									nodeName+='.'+node.className;
								}
							}
							*/
							var uid = ElementCache(node);
							var s;
							if(stack.length>0) {
								s='<a ' + cacheID +'='+ uid +'>'+ nodeName +'</a><span><</span>';
							}else{
								s='<a ' + cacheID +'='+ uid +' class="current">'+ nodeName +'</a>';
							}
								
							nodelistHtml+=s;
                            
                        }
                    }
					NodeListBox.update(nodelistHtml);
					var newIdxButton = NodeListBox.createChild({
						tag:'a',
						html:'生成碎片',
						id:'newIdxButton',
						style:'padding: 4px 6px 4px 7px;',
						cls:'fbButton fbHover'
					});
					newIdxButton.on('mousedown',function(e,obj){
						var el = Ext.fly(e.target);
						el.addClass('fbBtnPressed');
					});
					newIdxButton.on('mouseup',function(e,obj){
						var el = Ext.fly(e.target);
						el.removeClass('fbBtnPressed');
						
						//生成碎片
						if(FBL.currentInspectedEl){
							var selEl = Ext.fly(FBL.currentInspectedEl);	
							if(selEl.findParent('cmpp') || selEl.select('cmpp').getCount()>0){
								Ext.Toast.show('该元素内部包含逻辑块,不允许创建碎片。',{
									title:'提示',
									time:2000,
									buttons: Ext.Msg.OK,
									animEl: 'elId',
									icon: Ext.MessageBox.WARNING ,  
									minWidth:420
								});
								return;
							}

							//判断该元素的父子结点是否存在碎片嵌套
							if(TPE.isIdxNesting(selEl)){
								Ext.Toast.show('该元素的外部或内部已经存在碎片了，碎片是不允许嵌套的.',{
									title:'提示',
									time:2000,
									buttons: Ext.Msg.OK,
									animEl: 'elId',
									icon: Ext.MessageBox.WARNING ,  
									minWidth:420
								});
								return;
							}
							
							TPE.menu.config({
								idxDom:FBL.currentInspectedEl,
								action:"userCreate",
								senderDom:e.target
							});	
						}
					});					
                },
				onListMouseMove:function(e,obj,options){
					var targ = e.target;

					var nodeName = targ.nodeName.toLowerCase();
					if(nodeName!='a'){
						return;
					}
					if (!targ) {
                        FBL.Inspector.hideBoxModel();
                        hoverElement = null;
                        return
                    }
                    if (typeof targ.attributes[cacheID] == "undefined") {
                        return
                    }
                    var uid = targ.attributes[cacheID];
                    if (!uid) {
                        return
                    }

                    var el = ElementCache.get(uid.value);
					if (el.id == "fbToolbarDomList") {
                        FBL.Inspector.hideBoxModel();
                        hoverElement = null;
                        return
                    }					
					
					if ((new Date().getTime() - hoverElementTS > 40) && hoverElement != el) {
                        hoverElementTS = new Date().getTime();
                        hoverElement = el;
                        FBL.Inspector.drawBoxModel(el)
                    }
				},
				onListMouseOut:function(e,obj,options){
					FBL.Inspector.hideBoxModel();
                    hoverElement = null;
				},
				onListMouseClick:function(e,obj,options){
					var targ = e.target;

					var nodeName = targ.nodeName.toLowerCase();
					if(nodeName!='a'){
						return;
					}
                    if (typeof targ.attributes[cacheID] == "undefined") {
                        return
                    }
                    var uid = targ.attributes[cacheID];
                    if (!uid) {
                        return
                    }
					
					var nodes = NodeListBox.select('a.current');
					nodes.each(function(){
						this.replaceClass('current','');
					});
					targ.className='current';
					
					if (typeof targ.attributes[cacheID] == "undefined") {
                        return
                    }
                    var uid = targ.attributes[cacheID];
                    if (!uid) {
                        return
                    }
 
                    var el = ElementCache.get(uid.value);
					FBL.Inspector.drawOutline(el);
				}
			}; 
			var hoverElement = null;
            var hoverElementTS = 0;
		}
	});

	/****Toolbar***/
	FBL.ns(function() {
		with(FBL) {
			FBL.toolbar = {
				panel:null,
				timeoutId:null,
				btnFixed:null,
				isFixed:true,
				isHide:false,
				initialize:function(){
					this.btnFixed = Ext.get('fbToolbar_btFixed');
					this.panel.on('mouseout',this.toolbarMouseout,this,{						
						stopEvent :false,
						stopPropagation:false,
						delay:50
					});
					this.panel.on('mouseover',this.toolbarMouseover,this,{						
						stopEvent :false,
						stopPropagation:false,
						delay:50
					});		
					this.btnFixed.on('mousedown',FBL.toolbar.toggleFix,this);
					if(this.isFixed) this.fixed();
					else this.unfixed();
				},//todo
				toolbarMouseout:function(e,obj,options){
					//需要在执行函数前，先去判断在这个事件中鼠标移动到的元素是否是包含在它本身当中，不属于时才执行后面函数
					var fromTarget = e.getRelatedTarget();
					if(fromTarget){
						fromTarget= Ext.fly(fromTarget);
						var target =  Ext.fly(e.target);
						if(fromTarget.contains(target)){
							return ;
						}
					}
					if(!FBL.toolbar.isFixed){
						FBL.toolbar.hide();
					}
					
					/*
					if(this.timeoutId) clearTimeout(this.timeoutId);
					if(!FBL.toolbar.isHide){
						setTimeout(FBL.toolbar.hide,100);	
					}
					*/
				},
				toolbarMouseover:function(e,obj,options){
					if(FBL.toolbar.isHide){
						FBL.toolbar.show();
					}
				},
				hide:function(){
					var scrollTop = window.pageYOffset  
						|| document.documentElement.scrollTop  
						|| document.body.scrollTop  
						|| 0;
					FBL.toolbar.panel.moveTo(0,-20+scrollTop,true);
					FBL.toolbar.isHide = true;
				},
				show:function(e,obj){
					var scrollTop = window.pageYOffset  
						|| document.documentElement.scrollTop  
						|| document.body.scrollTop  
						|| 0;
					//FBL.toolbar.panel.move('down',22,true);
					FBL.toolbar.panel.moveTo(0,0+scrollTop,true);
					FBL.toolbar.isHide = false;
				},
				toggleFix:function (e,obj){
					if (FBL.toolbar.isFixed) {
						this.unfixed();
					} else {
						this.fixed();
					}
				},
				fixed:function(){
					FBL.toolbar.isFixed = true;
					this.btnFixed.addClass('fbBtnPressed');
				},
				unfixed:function(){
					FBL.toolbar.isFixed = false;
					this.btnFixed.removeClass('fbBtnPressed');					
				}
					
			};
		}
	});
})();
