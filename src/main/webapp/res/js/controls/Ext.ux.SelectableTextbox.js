(function () {
    Ext.ux.SelectableText = Ext.extend(Ext.form.Field, {
        width: 400
        , height: 30
        ,zIndex:99
        , value: ""
        , dataSource: [{"real_name": ["\u4e01\u5b97\u79cb", "dingzongqiu", "dzq"],"type": "现在同事"}]
        , name: ""

	, fribox: null
	, input: null
	, select: null
	, listbox: null
	, friendList: null
	, tip: null
	, autobox: null
	, flag: null
	, selAll: null
	, showgroup: null
	, submit: null
    , resources: {
        'id': 'ID:',
        'tip': '请输入选项(支持拼音首字母输入)',
        'del': '移除',
        'empty': '选项不在选项列表哦，请重新输入',
        'select': '请选择选项！'
    }
        , html: '\
	<div class="shaw">\
		<div class="fribox">\
			<input class="val" type="text" maxlength="20"/>\
		</div>\
		<div class="frisel">\
			<a class="selbtn" href="javascript:" /></a>\
			<div class="allfriend">\
				 <div style="width: 300px;" class="sgt_on">\
					<div class="l">请选择</div>\
						   <div style="padding-right: 20px;" class="r"><span class="selAll" style="display: none;"><a href="javascript:" class="sl">全选</a> </span><span><select name="group" class="group"></select></span></div>\
					</div>\
					<ul class="friendList">\
					</ul>\
					<div class="tac p5">\
						<div class="gbs1"><input type="button" class="gb1-12" title="确定" value="确定"/></div>\
					</div>\
				</div>\
			</div>\
	</div>'

	    , index: 0
	    , keyword: null
		, ctnerCls: "selectableBox"
        , defaultAutoCreate: { "tag": "input", "type": "hidden" }
		, onRender: function (ctnr, pos) {
		    this.constructor.superclass.onRender.call(this, ctnr, pos);
		    this.createBox(ctnr);
		}
        , createBox: function (ctnr) {
            var ctnr = ctnr.createChild({ cls: this.ctnerCls, html: this.html });
            this.fribox = ctnr.child('.fribox');
            this.input = ctnr.child('input');
            this.select = ctnr.child('.selbtn');
            this.listbox = ctnr.child('.allfriend');
            this.friendList = ctnr.child('.friendList');
            this.tip = null;
            this.autobox = null;
            this.selAll = ctnr.child('.selAll');
            this.showgroup = ctnr.child('.group');
            this.submit = ctnr.child('.gbs1 input');
            this.bind();
            
 		    this.listbox.setStyle("z-index",this.zIndex);
		    ctnr.setStyle("z-index",this.zIndex);           
        }
        , bind: function () {
            Ext.get(document).on('click', this.setFocus, this);
            this.input.on('focus', this.showDefault, this);
            this.input.on('blur', this.hideDefault, this);
            this.input.on('keyup', this.autoComplete, this);
            this.submit.on('click', this.doSubmit, this);
            this.submit.on('mouseover', this.over, this);
            this.submit.on('mouseout', this.out, this);
            this.select.on('click', this.showfriendbox, this);
            this.showgroup.on('change', this.getGroup, this);
            this.selAll.on('click', this.selectAll, this);
        }
        , setFocus: function (e) {
            var target = e.target; //获取鼠标点击的对象
            if (target.tagName == 'HTML' && this.autobox != null) {//隐藏下拉层
                return this.autobox.dom.style.display = 'none';
            }
            while (target && target.tagName != "BODY") {
                if (target == this.fribox.dom || (this.autobox && target == this.autobox)) {
                    return this.input.focus();
                }
                target = target.parentNode;
            }
        },
        showDefault: function (e) {
            this.select.dom.className = 'selbtn';
            this.input.value = '';
            this.listbox.dom.style.display = 'none';
            if (this.autobox != null) { this.autobox.dom.style.display = 'none'; }
            if (this.tip == null) {//提示层是否存在，不存在创建
                this.tip = this.fribox.createChild({ cls: "default" });
                this.tip.update(this.resources.tip);
                this.tip.dom.style.backgroundColor = '#eee';
            }
            this.tip.dom.style.top = this.input.dom.offsetTop + this.input.dom.offsetHeight + 6 + 'px';
            this.tip.dom.style.left = this.input.dom.offsetLeft - 3 + 'px';
            this.fribox.dom.parentNode.style.borderLeft = this.fribox.dom.parentNode.style.borderTop = '1px #000000 solid';
            this.tip.dom.style.display = '';
        },
        hideDefault: function (e) {
            if (this.tip != null) {//提示层存在,则隐藏
                this.fribox.dom.parentNode.style.borderLeft = this.fribox.dom.parentNode.style.borderTop = '1px #ffffff solid';
                this.tip.dom.style.display = 'none';
            }
        },

        getData: function (oo) {
            var _data = this.dataSource, group = '';
            if (oo != undefined) {
                group = oo.options[oo.selectedIndex].value;
                this.friendList.update("");
                this.selAll.dom.style.display = (group == '') ? 'none' : 'inline';
            }
            if (_data.length > 0) {
                var oFrag = document.createDocumentFragment(); 
                var slist = this.fribox.select('.fri').elements, flag;
                this.friendList.update("");
                for (var i = 0, dLen = _data.length; i < dLen; i++) {
                    if (group != '' && _data[i].type != group) { continue; }
                    flag = this.strip(slist, _data[i].real_name[0]);
                    var oNod = document.createTextNode(_data[i].real_name[0]);
                    var isChk = (flag == false) ? ' checked' : '';
                    var oInput = (document.all) ? document.createElement('<input type="checkbox"' + isChk + '/>') : document.createElement('input');
                    oInput.type = 'checkbox';
                    //oInput.setAttribute('title', this.resources.id + _data[i].uid);

                    var oLi = document.createElement('li');
                    //oLi.setAttribute('title', this.resources.id + _data[i].uid);
                    oLi.appendChild(oInput);
                    oLi.appendChild(oNod);
                    oFrag.appendChild(oLi);
                    if (flag == false) { oInput.checked = true; }
                }
                this.friendList.dom.appendChild(oFrag);
            }
        },
        setGroup: function () {
	      	this.showgroup.dom.options.length=0;
            var group = '';
            var _data = this.dataSource;
            for (var i = 0, dLen = _data.length; i < dLen; i++) {
                if (group.indexOf(_data[i].type + ',') == -1) {
                    var item = new Option(_data[i].type, _data[i].type);
                    this.showgroup.dom.options.add(item);
                    group += _data[i].type + ',';
                }
            }
        },
        getGroup: function (e) {
            var target = e.target;
            this.flag = false;
            this.getData(target);
        },
        showfriendbox: function (e) {
            var target = e.target;
            target.blur();
            this.getData(); //读取并创建好友列表
            this.setGroup(); //读取并创建好友分组
            this.select.dom.className = (this.select.dom.className == 'on') ? 'selbtn' : 'on';
            this.listbox.dom.style.top = this.fribox.dom.clientHeight + 6 + 'px';
            if (this.autobox != null) { this.autobox.dom.style.display = 'none'; }
            this.listbox.dom.style.display = (this.listbox.dom.style.display == 'block') ? 'none' : 'block';
        },
        delObj: function (e) {
            var evt = e || window.event;
            var target = evt.srcElement || evt.target;
            target.parentNode.parentNode.removeChild(target.parentNode);
            this.output();
        }
        , insertDIV: function (nod, cNod) {
            var img = document.createElement('a');
            img.className = 'delBtn';
            img.href = "javascript:void(0)"
            img.setAttribute('alt', this.resources.del, true);
            Ext.get(img).on('click', this.delObj, this); //创建删除按钮绑定事件
            var odiv = document.createElement('div');
            odiv.className = 'fri';
            odiv.innerHTML = cNod + '  ';
            odiv.appendChild(img);
            nod.appendChild(odiv);
        },
        strip: function (from, to) {
            for (var j = 0, slen = from.length; j < slen; j++) {
                var oInner = this.batch(from[j].innerHTML);
                if (oInner == to) {
                    return flag = false;
                }
            }
            return true;
        }
    , batch: function (html) {
        var trim = function (html) {
            return (html == '') ? '' : html.replace(/(^\s*)|(\s*$)/g, "");
        }
        return (this.batch = function (str) {
            return (trim(str) == '') ? '' : trim(str.replace(/<.*?>/g, ""));
        })(html);
    }
	, doSubmit: function (e) {
	    var flist = this.friendList.select('input').elements, slist = this.fribox.select('.fri').elements, selList = [], friList = [];
	    if (!flist || flist.length == 0) { alert(this.resources.select); return; }
	    friList.length = 0;
	    for (var i = 0, flen = flist.length; i < flen; i++) {
	        if (flist[i].checked == true) {
	            friList.push(this.batch(flist[i].parentNode.innerHTML));
	        }
	    }
	    var oFrag = document.createDocumentFragment(), flag;
	    if (slist.length > 0) {
	        for (var i = 0, olen = friList.length; i < olen; i++) {
	            flag = this.strip(slist, friList[i]);
	            if (flag == true) { this.insertDIV(oFrag, friList[i]); }
	        }
	    } else {
	        for (var i = 0, olen = friList.length; i < olen; i++) {
	            this.insertDIV(oFrag, friList[i]);
	        }
	    }
	    this.fribox.dom.insertBefore(oFrag, this.input.dom);
	    this.listbox.dom.style.display = 'none';
	    this.output();
	},
        over: function (e) {
            this.submit.dom.className = 'gb2-12';
        },
        out: function (e) {
            this.submit.dom.className = 'gb1-12';
        },
        autoComplete: function (e) {
            if (!this.autobox) {
                this.autobox = this.fribox.createChild({ cls: 'autobox' });
            }
            var target = e.target;
            if (target && this.autobox != null) { this.keyDown(target, e); }
            this.autobox.dom.style.top = this.input.dom.offsetTop + this.input.dom.offsetHeight + 6 + 'px';
            this.autobox.dom.style.left = this.input.dom.offsetLeft - 3 + 'px';
            this.fribox.dom.parentNode.style.borderLeft = this.fribox.dom.parentNode.style.borderTop = '1px #000000 solid';
            this.tip.dom.style.display = 'none';
        },
        run: function (path) {
            var allfriList = this.autobox.dom.childNodes;
            if (allfriList.length == 0) { return; }
            if (path == 'child') {
                if (this.index <= 0) {
                    this.index = 0;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = "#F2F6FB"; }
                } else if (this.index >= allfriList.length) {
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = ""; }
                    this.index = 0;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = "#F2F6FB"; }
                } else {
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = ""; }
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = "#F2F6FB"; }
                }
                this.index++;
            } else {
                this.index--;
                if (this.index <= 0) {
                    this.index = 0;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = ""; }
                    this.index = allfriList.length;
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = "#F2F6FB"; }
                } else if (this.index >= allfriList.length - 1) {
                    this.index = allfriList.length - 1;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = ""; }
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = "#F2F6FB"; }
                } else {
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = ""; }
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = "#F2F6FB"; }
                }
            }
        },
        keyDown: function (iobj, e) {
            if (document.all) {
                var keycode = event.keyCode;
            } else {
                var keycode = e.which;
            }
            var target = e.target, searchtxt = target.value.replace(/\s/ig, ''), innerdivhtml = "", alldiv = isyouselect = 0, sdiv = this.autobox, nowsel = true;
            switch (keycode) {
                case 40: /*keyborad child*/
                    this.run('child');
                    break;
                case 38: /*keyboard up*/
                    this.run('up');
                    break;
                default:
                    var selFri = this.fribox.dom.childNodes;
                    if (keycode == 8) {
                        if (this.keyword == null && selFri[selFri.length - 1]) {
                            this.fribox.dom.removeChild(selFri[selFri.length - 1]);
                            this.input.focus();
                            this.output();
                            return false;
                        }
                    }
                    if (searchtxt == "") {
                        sdiv.dom.innerHTML = this.resources.tip;
                        sdiv.dom.style.display = "block";
                        this.keyword = null;
                        this.output();
                        return false;
                    }
                    nowsel = true;
                    var _data = this.dataSource;
                    for (i = 0; i < _data.length; i++) {
                        if (_data[i].real_name[0].substr(0, searchtxt.length).toLowerCase() == searchtxt || _data[i].real_name[1].substr(0, searchtxt.length).toLowerCase() == searchtxt || _data[i].real_name[2].substr(0, searchtxt.length).toLowerCase() == searchtxt) {
                            alldiv++;
                            innerdivhtml = innerdivhtml + "<div>" + _data[i].real_name[0] + "</div>";
                            if ((searchtxt == _data[i].real_name[0] || searchtxt == _data[i].real_name[1] || searchtxt == _data[i].real_name[2]) && isyouselect == 0) {
                                this.index++;
                                isyouselect = 1;
                            }
                        }
                    }
                    if (alldiv != 0 && innerdivhtml != "") {
                        sdiv.dom.innerHTML = innerdivhtml;
                        var autoList = this.autobox.select("div").elements;
                        var mouseover = function (e) {
                            var tar = e.target;
                            tar.style.backgroundColor = "#F2F6FB";
                        }
                        var mouseout = function (e) {
                            var tar = e.target;
                            tar.style.backgroundColor = "";
                        }
                        if (keycode == 13) {
                            this.index--;
                            this.index = (this.index < 0) ? 0 : this.index;
                            var allfriList = this.autobox.dom.childNodes;
                            if (allfriList.length == 1) { this.index = 0; }
                            this.doSelect(e, allfriList[this.index]);
                            this.keyword = null;
                            this.index = 0;
                            this.output();
                            return;
                        }
                        var len = autoList.length;
                        while (len--) {
                            Ext.get(autoList[len]).on('mouseover', mouseover);
                            Ext.get(autoList[len]).on('mouseout', mouseout);
                            Ext.get(autoList[len]).on('click', this.doSelect, this);
                        }
                        sdiv.dom.style.backgroundColor = '#fff';
                    }
                    else {
                        if (searchtxt) {
                            sdiv.dom.innerHTML = this.resources.empty;
                            sdiv.dom.style.backgroundColor = '#eee';
                        }
                        this.index = 0;
                    }
                    sdiv.dom.style.display = "block";
                    this.keyword = this.input.value;
                    return false;
            }
        },
        doSelect: function (e, theObj) {
            var tar = (theObj) ? theObj : e.target, oInner = this.batch(tar.innerHTML);
            var _input = this.input.dom, _fribox = this.fribox.dom;
            var slist = this.fribox.select('.fri').elements, oFrag = document.createDocumentFragment();
            var flag = this.strip(slist, oInner);
            if (flag == true) {
                this.insertDIV(oFrag, oInner);
                _fribox.insertBefore(oFrag, _input);
            }
            _input.value = '';
            this.autobox.dom.style.display = 'none';
            this.output();
        }
        , output: function () {
            //            var outdata = (get('output')) ? get('output') : null; //好友列表输出
            //            if (outdata == null) { return; }
            var valList = [], slist = this.fribox.select('.fri').elements;
            for (var i = 0, len = slist.length; i < len; i++) {
                var val = this.batch(slist[i].innerHTML);
                valList.push(val);
            }
            valList = valList.join(',');
            this.setValue(valList);
            //(outdata.tagName != 'INPUT') ? outdata.innerHTML = valList : outdata.value = valList;
        },
        selectAll: function (e) {
            if (!this.flag) { this.flag = true; }
            else {
                this.flag = (this.flag == true) ? false : true;
            }
            var boxAll = this.friendList.child('input');
            for (var i = 0, olen = boxAll.length; i < olen; i++) {
                boxAll[i].checked = this.flag;
            }
            return this;
        }
    });
    Ext.reg('stext', Ext.ux.SelectableText);
})();
