/*
*description:输入拼音提示的可多选或单选的选择器
*author:cici
*date:2011/9/30
*/

(function () {
    Ext.ux.SelectorField = Ext.extend(Ext.form.Field, {
		width: 400,
		columsCount:2,//列表项分几列显示
        height: 162,
        zIndex:99,
		value:'',
		ctnerCls: "SelectorField",
        defaultAutoCreate: { "tag": "input", "type": "hidden" },
		valueFieldName:'value',
		textFieldName:'text',
		textTemplate:'{text}',
		allowMultiSelect:true,//是否允许多选
		value:'',//默认值
		data:[],
		dataSource:[],
		index: 0,
	    keyword: null,
		resources: {
			'id': 'ID:',
			'tip': '请输入选项(支持拼音首字母输入)',
			'del': '移除',
			'empty': '选项不在选项列表哦，请重新输入',
			'select': '请选择选项！'
		},
		template:'\
		<div class="SelectorField_shaw" style="border-color: #FFFFFF -moz-use-text-color #FFFFFF #FFFFFF;border-style: solid none solid solid; border-width: 1px 0 1px 1px; float: left;padding-top: 1px;width: 99.6%;">\
			<div class="SelectorField_fribox" style=" border-left: 1px solid #FFFFFF;border-top: 1px solid #FFFFFF;cursor: text;float: left;left: 1px;position: relative;width: {width1}px;">\
				<input style="border: 1px solid #FFFFFF;float: left;line-height: 19px;outline: medium none;width: 100px;" type="text" maxlength="25"/>\
			</div>\
			<div class="SelectorField_frisel" style=" float: right;position: relative;right: 1px;">\
				<a class="SelectorField_selbtn" href="javascript:" style="background: url(./../res/img/runTime/xx_xx1.gif) no-repeat scroll center center transparent;cursor: pointer;display: block;float: right;height: 16px;margin: 3px 3px 0;padding: 3px 3px 0;width: 16px;"/></a>\
				<div class="SelectorField_allfriend" style="top:25px;z-index:{zIndex2};background: none repeat scroll 0 0 #FFFFFF;display: block;position: absolute;right: -3px;width: {width2}px;">\
				</div>\
			</div>\
		</div>',
		onRender: function (ctnr, pos) {
		    this.constructor.superclass.onRender.call(this, ctnr, pos);
			
			if(typeof(this.dataSource)=='string'){//url 
				var urlTpl = new Ext.XTemplate(this.dataSource);
				var params = Ext.parseQuery();
				var queryParams={};
				for(var key in params){
					queryParams['query_' + key] = params[key];//注入的url参数必须加 "query_" 前缀
				}
				var url = urlTpl.applyTemplate(queryParams);//从url接受参数
				Ext.Ajax.request({  
					url:url,
					method:'get',
					scope :this,
					success:function(response,opts){
						var ret = Ext.util.JSON.decode(response.responseText);
						this.dataSource = ret;
						this.init();
						this.createUI(ctnr);
						this.setValue2(this.value);
					}
				});
			}
			else{
				this.init();
				this.createUI(ctnr);
				this.setValue2(this.value);
			}
		},
		init:function(){
			if(typeof(this.dataSource)=='string'){
				try{
					this.dataSource = Ext.util.JSON.decode(this.dataSource);
				}catch(ex){
					this.dataSource=[];
				}
			}
			if(this.dataSource.length==0){
				this.dataSource = this.data;
			}
			if(typeof(this.textTemplate)=='string'){
				this.textTemplate = new Ext.XTemplate(this.textTemplate);
			}
			this.zIndex = this.zIndex >=11000-2000?11000-2001:this.zIndex;
		},
		createUI:function(ctnr){
			var tpl = new Ext.XTemplate(this.template);
			var html = tpl.applyTemplate({
				width:this.width,
				width1:this.width-50,
				width2:this.width+2,
				zIndex2:this.zIndex + 2000
			});
			var ctnr = ctnr.createChild({ 
				style:'border: 1px solid #808080;float: left;font-size: 12px;line-height: 19px;width: '+ this.width +'px;z-index: '+ (this.zIndex+1) +';',
				html: html 
			});
            this.fribox = ctnr.child('.SelectorField_fribox');
            this.input = ctnr.child('input');
            this.select = ctnr.child('.SelectorField_selbtn');
            this.allfriend = ctnr.child('.SelectorField_allfriend');

			this.showgroup = new Ext.form.ComboBox({
				allowBlank: true,
				mode: 'local',
				width:80,
				store: new Ext.data.SimpleStore({ fields: ['value', 'text'],data:[['','全部']]}),
				valueField: 'value', //值
				displayField: 'text', //显示文本
				editable: false, //是否允许输入
				forceSelection: false, //必须选择一个选项
				triggerAction: 'all',
				value:'',
				selectOnFocus: false
			});
			this.showgroup.addListener('select',function(obj,record,index){
				this.getGroup(record.data.value);
			},this);
			
			//创建选项列表
			var listPanel = new Ext.Panel({
				renderTo:this.allfriend,
				height:this.height,
				autoScroll:true,
				style:'display:none',
				buttonAlign:'center',
				html:'<div class="friendList"></div>',
				tbar:[{
					xtype:'checkbox',
					boxLabel :this.allowMultiSelect?'全选':'',
					checked :false,
					style:this.allowMultiSelect?'':'display:none;',
					listeners:{
						scope:this,
						check :function(obj,checked){
							this.selectAll(checked);
						}
					}
				},{
					xtype: 'tbfill'
				},
					this.showgroup 
				],
				bbar:[{
					xtype: 'tbfill'
				},{
					text:'确定',
					scope:this,
					handler:this.doSubmit
				}]
			});
			this.listbox = listPanel.getEl();
			this.friendList = listPanel.getEl().child('.friendList');

            this.tip = null;
            this.autobox = null;
            this.bind();
            
 		    this.listbox.setStyle("z-index",this.zIndex+2000);
		    ctnr.setStyle("z-index",this.zIndex);  
		},
		bind:function(){
			Ext.getDoc().on('click', this.setFocus, this);
            this.input.on('focus', this.showTip, this);
            this.input.on('blur', this.hideTip, this);
            this.input.on('keyup', this.autoComplete, this);
            this.select.on('click', this.showfriendbox, this);
		},
		setFocus: function (e) {
            var target = e.target; //获取鼠标点击的对象
            if (target.tagName == 'HTML' && this.autobox != null) {//隐藏下拉层
				//this.autobox.dom.style.display = 'none';
				this.autobox.fadeOut({useDisplay: true});
                return ;
            }
            while (target && target.tagName != "BODY") {
                if (target == this.fribox.dom || (this.autobox && target == this.autobox)) {
                    return this.input.focus();
                }
                target = target.parentNode;
            }
        },
        showTip: function (e) {
            this.select.dom.className = 'SelectorField_selbtn';
			this.select.setStyle({
				background:'url(./../res/img/runTime/xx_xs1.gif) no-repeat scroll center center transparent'
			});
            this.input.dom.value = '';
            //this.listbox.dom.style.display = 'none';
			this.hideListBox();
            if (this.autobox != null) { 
				//this.autobox.dom.style.display = 'none'; 
				this.autobox.fadeOut({useDisplay: true});
			}
            if (this.tip == null) {//提示层是否存在，不存在创建
                this.tip = this.fribox.createChild({
					style: "left: -3px;top: 29px;background: none repeat scroll 0 0 #EEEEEE;border-color: #98B1C8 #7F7F7F #7F7F7F #98B1C8; border-style: solid;border-width: 1px 3px 3px 1px;color: #666666;padding: 2px 5px;position: absolute;width: 220px;",
					html:this.resources.tip
				});
                this.tip.dom.style.backgroundColor = '#eee';
            }
            this.tip.dom.style.top = this.input.dom.offsetTop + this.input.dom.offsetHeight + 6 + 'px';
            this.tip.dom.style.left = this.input.dom.offsetLeft - 3 + 'px';
            this.fribox.dom.parentNode.style.borderLeft = this.fribox.dom.parentNode.style.borderTop = '1px #000000 solid';
            //this.tip.dom.style.display = '';
			this.tip.fadeIn({useDisplay: true});
        },
        hideTip: function (e) {
            if (this.tip != null) {//提示层存在,则隐藏
                this.fribox.dom.parentNode.style.borderLeft = this.fribox.dom.parentNode.style.borderTop = '1px #ffffff solid';
                //this.tip.dom.style.display = 'none';
				this.tip.fadeOut({useDisplay: true});
            }
        },
		hideListBox:function(){
			this.listbox.fadeOut({useDisplay: true});
			this.select.dom.className = 'SelectorField_selbtn' ;
			this.select.setStyle({
				background:'url(./../res/img/runTime/xx_xx1.gif) no-repeat scroll center center transparent'
			});
		},
        getData: function (g) {
            var data = this.dataSource;
			group = this.showgroup.getValue();
            if (data.length > 0) {
				var oFrags={};
				var groups = this.showgroup.store.data;
				for(var i=0;i<groups.length;i++){
					if(groups.item(i).data.value!='') oFrags[groups.item(i).data.value] = document.createDocumentFragment(); 
				}
				
                var slist = this.fribox.select('.SelectorField_fri').elements, flag;
                this.friendList.update("");
				
				var tpl = this.textTemplate;
				
				var value_text = {};
				var params = Ext.parseQuery();
				var queryParams={};
				for(var key in params){
					queryParams['query_' + key] = params[key];
				}
                for (var i = 0, dLen = data.length; i < dLen; i++) {
					
                    if (group != '' && data[i].group != group) { continue; }
					oFrag = oFrags[data[i].group];
                    flag = this.strip(slist, data[i][this.textFieldName]);
					
					var oNod =  document.createElement('div');
					//注入url的参数
					
					Ext.apply(data[i],queryParams);
					oNod.innerHTML = tpl.applyTemplate(data[i]);
					
                    var isChk = (flag == false) ? ' checked' : '';
                    var oInput = document.createElement('input');
					if(!this.allowMultiSelect) oInput.name = this.name + "_ux_selector";
                    oInput.type = this.allowMultiSelect?'checkbox':'radio';
					oInput.style.cssText = 'float:left;';
					oInput.value =  data[i][this.valueFieldName] ;
                    var oLi = document.createElement('li');
					oLi.title = data[i][this.textFieldName];
					oLi.style.cssText = 'width:'+ (this.width/this.columsCount - 20) +'px;background: none repeat scroll 0 0 #FFFFFF;border-bottom: 1px solid #EEEEEE;color: #666666;float: left;height: 20px;list-style: none outside none;padding: 2px 5px;text-align: left;';
                    oLi.appendChild(oInput);
                    oLi.appendChild(oNod);
                    oFrag.appendChild(oLi);
                    if (flag == false) { oInput.checked = true; }
                }
								
				for(var i=0;i<groups.length;i++){
					if(groups.item(i).data.value!='' && oFrags[groups.item(i).data.value].childNodes.length>0){
						var details = this.friendList.createChild({
							tag:'details open',
							style:'float:left;width:100%;margin-bottom: 5px;',
							html:'<summary style="background-color: #d4d4d4;cursor:pointer;">'+ groups.item(i).data.text +'</summary>'
						});
						var gul = details.createChild({
							tag:'ul'
						});
						gul.dom.appendChild(oFrags[groups.item(i).data.value] )
					}
				}

            }
        },
        setGroup: function () {
			var data = this.dataSource;
			var groupArr=[['','全部']];
			var group = '';
			for (var i = 0;i<data.length;i++){
				var item = data[i];
				if (group.indexOf(item.group + ',') == -1) {
					groupArr.push([item.group,item.group]);
				}
				group += item.group + ',';
			}
			this.showgroup.store = new Ext.data.SimpleStore({ fields: ['value', 'text'],data:groupArr});
			
        },
        getGroup: function (group) {
            this.flag = false;
            this.getData(group);
        },
        showfriendbox: function (e) {
            var target = e.target;
            target.blur();
            this.setGroup(); //读取并创建好友分组			
            this.getData(); //读取并创建好友列表
            this.select.dom.className = (this.select.dom.className == 'on') ? 'SelectorField_selbtn' : 'on';
			this.select.setStyle({
				background:(this.select.dom.className == 'on') ? 'url(./../res/img/runTime/xx_xs1.gif) no-repeat scroll center center transparent':'url(./../res/img/runTime/xx_xx1.gif) no-repeat scroll center center transparent'
			});
            this.allfriend.dom.style.top = (this.fribox.dom.clientHeight + 4) + 'px';
            if (this.autobox != null) { 
				//this.autobox.dom.style.display = 'none'; 
				this.autobox.fadeOut({useDisplay: true});
			}
            if(this.listbox.dom.style.display == 'none'){
				this.listbox.fadeIn({useDisplay: true});
			}else{
				this.listbox.fadeOut({useDisplay: true});
			}

        },
        delObj: function (e) {
            var evt = e || window.event;
            var target = evt.srcElement || evt.target;
            target.parentNode.parentNode.removeChild(target.parentNode);
            this.output();
        },
		insertDIV: function (nod, cNod) {
            var img = document.createElement('img');
            img.setAttribute('alt', this.resources.del, true);
			img.style.cssText=' border: 0 none;cursor: pointer;';
			img.src='./../res/img/runTime/del.gif',
            Ext.get(img).on('click', this.delObj, this); //创建删除按钮绑定事件
            var odiv = document.createElement('div');
            odiv.className = 'SelectorField_fri';
			
			odiv.style.cssText='background: none repeat scroll 0 0 #E0E5EE; border: 1px solid #CCD5E4;display: block;float: left;height:18px;line-height:18px;margin: 0px 5px 1px 0;padding: 0 5px;';
			Ext.fly(odiv).set({value:cNod.value});
            odiv.innerHTML = cNod.text + '  ';
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
        },
		batch: function (html) {
			var trim = function (html) {
				return (html == '') ? '' : html.replace(/(^\s*)|(\s*$)/g, "");
			}
			return (this.batch = function (str) {
				return (trim(str) == '') ? '' : trim(str.replace(/<.*?>/g, ""));
			})(html);
		},
		doSubmit: function (e) {
			var flist = this.friendList.select('input').elements, slist = this.fribox.select('.SelectorField_fri').elements, selList = [], friList = [];
			if (!flist || flist.length == 0) { alert(this.resources.select); return; }
			friList.length = 0;
			for (var i = 0, flen = flist.length; i < flen; i++) {
				if (flist[i].checked == true) {
					friList.push({text:this.batch(flist[i].parentNode.title),value:flist[i].value});
				}
			}
			var oFrag = document.createDocumentFragment(), flag;
			if (slist.length > 0) {
				for (var i = 0, olen = friList.length; i < olen; i++) {
					flag = this.strip(slist, friList[i].text);
					if (flag == true) { 
						if(!this.allowMultiSelect){//不允许多选
							for(var j=0;j<slist.length;j++){
								this.fribox.dom.removeChild(slist[j]);
							}
						}
						this.insertDIV(oFrag, friList[i]); 
					}
				}
			} else {
				for (var i = 0, olen = friList.length; i < olen; i++) {
					this.insertDIV(oFrag, friList[i]);
				}
			}
			this.fribox.dom.insertBefore(oFrag, this.input.dom);
			//this.listbox.dom.style.display = 'none';
			this.hideListBox();
			this.output();
		},
        autoComplete: function (e) {
            if (!this.autobox) {
                this.autobox = this.fribox.createChild({ 
					style:'padding: 2px 5px;display:none;position: absolute;width: 200px;border-color: #98B1C8 #7F7F7F #7F7F7F #98B1C8;border-style: solid;border-width: 1px 3px 3px 1px;color: #666666;z-index:' + this.zIndex+2000
				});
				
            }
            var target = e.target;
            if (target && this.autobox != null) { this.keyDown(target, e); }
            this.autobox.dom.style.top = this.input.dom.offsetTop + this.input.dom.offsetHeight + 2 + 'px';
            this.autobox.dom.style.left = this.input.dom.offsetLeft - 3 + 'px';
            this.fribox.dom.parentNode.style.borderLeft = this.fribox.dom.parentNode.style.borderTop = '1px #000000 solid';
            //this.tip.dom.style.display = 'none';
			this.tip.fadeOut({useDisplay: true});
        },
        run: function (path) {
            var allfriList = this.autobox.dom.childNodes;
            if (allfriList.length == 0) { return; }
            if (path == 'down') {
                if (this.index <= 0) {
                    this.index = 0;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = "#E2EAFF"; }
                } else if (this.index >= allfriList.length) {
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = ""; }
                    this.index = 0;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = "#E2EAFF"; }
                } else {
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = ""; }
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = "#E2EAFF"; }
                }
                this.index++;
            } else {
                this.index--;
                if (this.index <= 0) {
                    this.index = 0;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = ""; }
                    this.index = allfriList.length;
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = "#E2EAFF"; }
                } else if (this.index >= allfriList.length - 1) {
                    this.index = allfriList.length - 1;
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = ""; }
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = "#E2EAFF"; }
                } else {
                    if (allfriList[this.index]) { allfriList[this.index].style.backgroundColor = ""; }
                    if (allfriList[this.index - 1]) { allfriList[this.index - 1].style.backgroundColor = "#E2EAFF"; }
                }
            }
        },
        keyDown: function (iobj, e) {
            var keycode = e.keyCode
            var target = e.target, searchtxt = target.value.replace(/\s/ig, ''), innerdivhtml = "", alldiv = isyouselect = 0, sdiv = this.autobox, nowsel = true;
            switch (keycode) {
                case 40: /*keyborad down*/
                    this.run('down');
                    break;
                case 38: /*keyboard up*/
                    this.run('up');
                    break;
                default:
					
                    var selFri = this.fribox.select('.SelectorField_fri').elements;
                    if (keycode == 8) {//删除
                        if (this.keyword == null && selFri[selFri.length - 1]) {
                            this.fribox.dom.removeChild(selFri[selFri.length - 1]);
                            this.input.focus();
                            this.output();
                            return false;
                        }
                    }
					
                    if (searchtxt == "") {
                        sdiv.dom.innerHTML = this.resources.tip;
                        //sdiv.dom.style.display = "block";
						sdiv.fadeIn({useDisplay: true});
                        this.keyword = null;
                        this.output();
                        return false;
                    }
                    nowsel = true;
                    var data = this.dataSource;
					//var tpl = new Ext.XTemplate(this.textTemplate);
                    for (i = 0; i < data.length; i++) {
						var exist = false;
						var text = data[i][this.textFieldName];
						var value =data[i][this.valueFieldName];
						var text_qp_arr = data[i][this.textFieldName + '_qp'];
						var text_jp_arr = data[i][this.textFieldName + '_jp'];
						var len = searchtxt.length;
						if(searchtxt==text.substr(0, len).toLowerCase()){
							exist =true;
						}else{
							for(var j =0;j<text_qp_arr.length;j++){
								if(searchtxt==text_qp_arr[j].substr(0, len).toLowerCase()){
									exist =true;
									break;
								}
							}
							if(!exist){
								for(var j =0;j<text_jp_arr.length;j++){
									if(searchtxt==text_jp_arr[j].substr(0, len).toLowerCase()){
										exist =true;
										break;
									}
								}
							}
						}						
						
                        if (exist) {
                            alldiv++;
                            innerdivhtml = innerdivhtml + '<div value="'+ value +'"  style="cursor: text;margin: 1px;padding: 3px 0;width: 98%;height:19px;overflow:hidden;background: none repeat scroll 0 0 #FFFFFF;border-color: #FFFFFF #FFFFFF #CCCCCC;border-style: solid;border-width: 1px;">' + text + '</div>';
                            /*if ((searchtxt == data[i][this.textFieldName] || searchtxt == data[i][this.textFieldName + '_qp'] || searchtxt == data[i][this.textFieldName + '_jp']) && isyouselect == 0) {
                                this.index++;
                                isyouselect = 1;
                            }
							*/
                        }
                    }
					
                    if (alldiv != 0 && innerdivhtml != "") {
                        sdiv.dom.innerHTML = innerdivhtml;
                        var autoList = this.autobox.select("div").elements;
                        var mouseover = function (e) {
                            var tar = e.target;
                            tar.style.backgroundColor = "#E2EAFF";
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
                    }else {
                        if (searchtxt) {
                            sdiv.dom.innerHTML = this.resources.empty;
                            sdiv.dom.style.backgroundColor = '#eee';
                        }
                        this.index = 0;
                    }
                    //sdiv.dom.style.display = "block";
					sdiv.fadeIn({useDisplay: true});
                    this.keyword = this.input.dom.value;
					
                    return false;
            }
        },
        doSelect: function (e, theObj) {
            var tar = (theObj) ? theObj : e.target, oInner = this.batch(tar.innerHTML);
			var oValue = Ext.fly(tar).getAttributeNS(null,'value');
            var _input = this.input.dom, _fribox = this.fribox.dom;
            var slist = this.fribox.select('.SelectorField_fri').elements, oFrag = document.createDocumentFragment();
			var flag = this.strip(slist, oInner);
			if (flag == true) {
				if(!this.allowMultiSelect){//不允许多选
					for(var i=0;i<slist.length;i++){
						_fribox.removeChild(slist[i]);
					}
				}
				this.insertDIV(oFrag, {text:oInner,value:oValue});
				_fribox.insertBefore(oFrag, _input);
			}
            _input.value = '';
            //this.autobox.dom.style.display = 'none';
			this.autobox.fadeOut({useDisplay: true});
            this.output();
        },
		output: function () {
			
            var valList = [], slist = this.fribox.select('.SelectorField_fri').elements;
            for (var i = 0, len = slist.length; i < len; i++) {
                var val = this.batch(slist[i].getAttributeNS(null,'value'));
                valList.push(val);
            }
            valList = valList.join(',');
			this.setValue(valList);
        },
		//初始化默认值
		setValue2 : function(v){
			var value = v?v + '':'';
			var data = this.dataSource;
			var value_text=[];
			for (var i = 0, dLen = data.length; i < dLen; i++) {
				value_text[data[i].value] = data[i].text;
			}
			var friList=[];
			var valList=[];
			var valueList = value.split(',');
			if(valueList.length>0){
				if(!this.allowMultiSelect){
					valueList = [valueList[0]];
				}
				var oFrag = document.createDocumentFragment()
				for(var i=0;i<valueList.length;i++){
					var text = value_text[valueList[i]];
					if(text!=undefined){
						valList.push(valueList[i]);//合法性验证
						friList.push({text:text,value:valueList[i]});
					}
				}
				for (var i = 0, olen = friList.length; i < olen; i++) {
					this.insertDIV(oFrag, friList[i]);
				}
				if(friList.length>0){
					this.fribox.dom.insertBefore(oFrag, this.input.dom);
				}
			}
			this.setValue(valList.join(','));
		}, 		
        selectAll: function (checked) {
            this.flag = checked;
            var boxAll = this.friendList.select('input').elements;
            for (var i = 0, olen = boxAll.length; i < olen; i++) {
                boxAll[i].checked = this.flag;
            }
            return this;
        }		
	});	
	Ext.reg('selector', Ext.ux.SelectorField);
})();