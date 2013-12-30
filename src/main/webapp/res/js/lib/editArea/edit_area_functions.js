	//replace tabulation by the good number of white spaces
	EditArea.prototype.replace_tab= function(text){
		return text.replace(/((\n?)([^\t\n]*)\t)/gi, editArea.smartTab);		// slower than simple replace...	
	};
	
	// call by the replace_tab function
	EditArea.prototype.smartTab= function(){
		val="                   ";
		return EditArea.prototype.smartTab.arguments[2] + EditArea.prototype.smartTab.arguments[3] + val.substr(0, editArea.tab_nb_char - (EditArea.prototype.smartTab.arguments[3].length)%editArea.tab_nb_char);
	};
	
	EditArea.prototype.show_waiting_screen= function(){
		width	= this.editor_area.offsetWidth;
		height	= this.editor_area.offsetHeight;
		if( !(this.isIE && this.isIE<6) )
		{
			width	-= 2;
			height	-= 2;
		}
		this.processing_screen.style.display= "block";
		this.processing_screen.style.width	= width+"px";
		this.processing_screen.style.height	= height+"px";
		this.waiting_screen_displayed		= true;
	};
	
	EditArea.prototype.hide_waiting_screen= function(){
		this.processing_screen.style.display="none";
		this.waiting_screen_displayed= false;
	};
	
	EditArea.prototype.add_style= function(styles){
		if(styles.length>0){
			newcss = document.createElement("style");
			newcss.type="text/css";
			newcss.media="all";
			if(newcss.styleSheet){ // IE
				newcss.styleSheet.cssText = styles;
			} else { // W3C
				newcss.appendChild(document.createTextNode(styles));
			}
			document.getElementsByTagName("head")[0].appendChild(newcss);
		}
	};
	
	EditArea.prototype.set_font= function(family, size){
		var t=this, a=this.textarea, s=this.settings, elem_font, i, elem;
		// list all elements concerned by font changes
		var elems= ["textarea", "content_highlight", "cursor_pos", "end_bracket", "selection_field", "selection_field_text", "line_number"];
		
		if(family && family!="")
			s["font_family"]= family;
		if(size && size>0)
			s["font_size"]	= size;
		if( t.isOpera && t.isOpera < 9.6 )	// opera<9.6 can't manage non monospace font
			s['font_family']="monospace";
			
		// update the select tag
		if( elem_font = _$("area_font_size") )
		{	
			for( i = 0; i < elem_font.length; i++ )
			{
				if( elem_font.options[i].value && elem_font.options[i].value == s["font_size"] )
					elem_font.options[i].selected=true;
			}
		}
		
		/*
		 * somethimes firefox has rendering mistake with non-monospace font for text width in textarea vs in div for changing font size (eg: verdana change between 11pt to 12pt)
		 * => looks like a browser internal random bug as text width can change while content_highlight is updated
		 * we'll check if the font-size produce the same text width inside textarea and div and if not, we'll increment the font-size
		 * 
		 * This is an ugly fix 
		 */ 
		if( t.isFirefox )
		{
			var nbTry = 3;
			do {
				var div1 = document.createElement( 'div' ), text1 = document.createElement( 'textarea' );
				var styles = {
					width:		'40px',
					overflow:	'scroll',
					zIndex: 	50,
					visibility:	'hidden',
					fontFamily:	s["font_family"],
					fontSize:	s["font_size"]+"pt",
					lineHeight:	t.lineHeight+"px",
					padding:	'0',
					margin:		'0',
					border:		'none',
					whiteSpace:	'nowrap'
				};
				var diff, changed = false;
				for( i in styles )
				{
					div1.style[ i ]		= styles[i];
					text1.style[ i ]	= styles[i];
				}
				// no wrap for this text
				text1.wrap = 'off';
				text1.setAttribute('wrap', 'off');
				t.container.appendChild( div1 );
				t.container.appendChild( text1 );
				// try to make FF to bug
				div1.innerHTML 		= text1.value	= 'azertyuiopqsdfghjklm';
				div1.innerHTML 		= text1.value	= text1.value+'wxcvbn^p*ù$!:;,,';
				diff	=  text1.scrollWidth - div1.scrollWidth;
				
				// firefox return here a diff of 1 px between equals scrollWidth (can't explain)
				if( Math.abs( diff ) >= 2 )
				{
					s["font_size"]++;
					changed	= true;
				}
				t.container.removeChild( div1 );
				t.container.removeChild( text1 );
				nbTry--;
			}while( changed && nbTry > 0 );
		}
		
		
		// calc line height
		elem					= t.test_font_size;
		elem.style.fontFamily	= ""+s["font_family"];
		elem.style.fontSize		= s["font_size"]+"pt";				
		elem.innerHTML			= "0";		
		t.lineHeight			= elem.offsetHeight;

		// update font for all concerned elements
		for( i=0; i<elems.length; i++)
		{
			elem	= _$(elems[i]);	
			elem.style.fontFamily	= s["font_family"];
			elem.style.fontSize		= s["font_size"]+"pt";
			elem.style.lineHeight	= t.lineHeight+"px";
		}
		// define a css for <pre> tags
		t.add_style("pre{font-family:"+s["font_family"]+"}");
		
		// old opera and IE>=8 doesn't update font changes to the textarea
		if( ( t.isOpera && t.isOpera < 9.6 ) || t.isIE >= 8 )
		{
			var parNod = a.parentNode, nxtSib = a.nextSibling, start= a.selectionStart, end= a.selectionEnd;
			parNod.removeChild(a);
			parNod.insertBefore(a, nxtSib);
			t.area_select(start, end-start);
		}
		
		// force update of selection field
		this.focus();
		this.update_size();
		this.check_line_selection();
	};
	
	EditArea.prototype.change_font_size= function(){
		var size=_$("area_font_size").value;
		if(size>0)
			this.set_font("", size);			
	};
	
	
	EditArea.prototype.open_inline_popup= function(popup_id){
		this.close_all_inline_popup();
		var popup= _$(popup_id);		
		var editor= _$("editor");
		
		// search matching icon
		for(var i=0; i<this.inlinePopup.length; i++){
			if(this.inlinePopup[i]["popup_id"]==popup_id){
				var icon= _$(this.inlinePopup[i]["icon_id"]);
				if(icon){
					this.switchClassSticky(icon, 'editAreaButtonSelected', true);			
					break;
				}
			}
		}
		// check size
		popup.style.height="auto";
		popup.style.overflow= "visible";
			
		if(document.body.offsetHeight< popup.offsetHeight){
			popup.style.height= (document.body.offsetHeight-10)+"px";
			popup.style.overflow= "auto";
		}
		
		if(!popup.positionned){
			var new_left= editor.offsetWidth /2 - popup.offsetWidth /2;
			var new_top= editor.offsetHeight /2 - popup.offsetHeight /2;
			//var new_top= area.offsetHeight /2 - popup.offsetHeight /2;
			//var new_left= area.offsetWidth /2 - popup.offsetWidth /2;
			//alert("new_top: ("+new_top+") = calculeOffsetTop(area) ("+calculeOffsetTop(area)+") + area.offsetHeight /2("+ area.offsetHeight /2+") - popup.offsetHeight /2("+popup.offsetHeight /2+") - scrollTop: "+document.body.scrollTop);
			popup.style.left= new_left+"px";
			popup.style.top= new_top+"px";
			popup.positionned=true;
		}
		popup.style.visibility="visible";
		
		//popup.style.display="block";
	};

	EditArea.prototype.close_inline_popup= function(popup_id){
		var popup= _$(popup_id);		
		// search matching icon
		for(var i=0; i<this.inlinePopup.length; i++){
			if(this.inlinePopup[i]["popup_id"]==popup_id){
				var icon= _$(this.inlinePopup[i]["icon_id"]);
				if(icon){
					this.switchClassSticky(icon, 'editAreaButtonNormal', false);			
					break;
				}
			}
		}
		
		popup.style.visibility="hidden";	
	};
	
	EditArea.prototype.close_all_inline_popup= function(e){
		for(var i=0; i<this.inlinePopup.length; i++){
			this.close_inline_popup(this.inlinePopup[i]["popup_id"]);		
		}
		this.textarea.focus();
	};
	
	EditArea.prototype.show_help= function(){
		
		this.open_inline_popup("edit_area_help");
		
	};
			
	EditArea.prototype.new_document= function(){
		this.textarea.value="";
		this.area_select(0,0);
	};
	
	EditArea.prototype.get_all_toolbar_height= function(){
		var area= _$("editor");
		var results= parent.getChildren(area, "div", "class", "area_toolbar", "all", "0");	// search only direct children
		//results= results.concat(getChildren(area, "table", "class", "area_toolbar", "all", "0"));
		var height=0;
		for(var i=0; i<results.length; i++){			
			height+= results[i].offsetHeight;
		}
		//alert("toolbar height: "+height);
		return height;
	};
	
	EditArea.prototype.go_to_line= function(line){	
		if(!line)
		{	
			var icon= _$("go_to_line");
			if(icon != null){
				this.restoreClass(icon);
				this.switchClassSticky(icon, 'editAreaButtonSelected', true);
			}
			
			line= prompt(this.get_translation("go_to_line_prompt"));
			if(icon != null)
				this.switchClassSticky(icon, 'editAreaButtonNormal', false);
		}
		if(line && line!=null && line.search(/^[0-9]+$/)!=-1){
			var start=0;
			var lines= this.textarea.value.split("\n");
			if(line > lines.length)
				start= this.textarea.value.length;
			else{
				for(var i=0; i<Math.min(line-1, lines.length); i++)
					start+= lines[i].length + 1;
			}
			this.area_select(start, 0);
		}
		
		
	};
	
	
	EditArea.prototype.change_smooth_selection_mode= function(setTo){
		//alert("setTo: "+setTo);
		if(this.do_highlight)
			return;
			
		if(setTo != null){
			if(setTo === false)
				this.smooth_selection=true;
			else
				this.smooth_selection=false;
		}
		var icon= _$("change_smooth_selection");
		this.textarea.focus();
		if(this.smooth_selection===true){
			//setAttribute(icon, "class", getAttribute(icon, "class").replace(/ selected/g, "") );
			/*setAttribute(icon, "oldClassName", "editAreaButtonNormal" );
			setAttribute(icon, "className", "editAreaButtonNormal" );*/
			//this.restoreClass(icon);
			//this.restoreAndSwitchClass(icon,'editAreaButtonNormal');
			this.switchClassSticky(icon, 'editAreaButtonNormal', false);
			
			this.smooth_selection=false;
			this.selection_field.style.display= "none";
			_$("cursor_pos").style.display= "none";
			_$("end_bracket").style.display= "none";
		}else{
			//setAttribute(icon, "class", getAttribute(icon, "class") + " selected");
			//this.switchClass(icon,'editAreaButtonSelected');
			this.switchClassSticky(icon, 'editAreaButtonSelected', false);
			this.smooth_selection=true;
			this.selection_field.style.display= "block";
			_$("cursor_pos").style.display= "block";
			_$("end_bracket").style.display= "block";
		}	
	};
	
	// the auto scroll of the textarea has some lacks when it have to show cursor in the visible area when the textarea size change
	// show specifiy whereas it is the "top" or "bottom" of the selection that is showned
	EditArea.prototype.scroll_to_view= function(show){
		var zone, lineElem;
		if(!this.smooth_selection)
			return;
		zone= _$("result");
		
		// manage height scroll
		var cursor_pos_top= _$("cursor_pos").cursor_top;
		if(show=="bottom")
		{
			//cursor_pos_top+=  (this.last_selection["line_nb"]-1)* this.lineHeight;
			cursor_pos_top+= this.getLinePosTop( this.last_selection['line_start'] + this.last_selection['line_nb'] - 1 );
		}
			
		var max_height_visible= zone.clientHeight + zone.scrollTop;
		var miss_top	= cursor_pos_top + this.lineHeight - max_height_visible;
		if(miss_top>0){
			//alert(miss_top);
			zone.scrollTop=  zone.scrollTop + miss_top;
		}else if( zone.scrollTop > cursor_pos_top){
			// when erase all the content -> does'nt scroll back to the top
			//alert("else: "+cursor_pos_top);
			zone.scrollTop= cursor_pos_top;	 
		}
		
		// manage left scroll
		//var cursor_pos_left= parseInt(_$("cursor_pos").style.left.replace("px",""));
		var cursor_pos_left= _$("cursor_pos").cursor_left;
		var max_width_visible= zone.clientWidth + zone.scrollLeft;
		var miss_left= cursor_pos_left + 10 - max_width_visible;
		if(miss_left>0){			
			zone.scrollLeft= zone.scrollLeft + miss_left + 50;
		}else if( zone.scrollLeft > cursor_pos_left){
			zone.scrollLeft= cursor_pos_left ;
		}else if( zone.scrollLeft == 45){
			// show the line numbers if textarea align to it's left
			zone.scrollLeft=0;
		}
	};
	
	EditArea.prototype.check_undo= function(only_once){
		if(!editAreas[this.id])
			return false;
		if(this.textareaFocused && editAreas[this.id]["displayed"]==true){
			var text=this.textarea.value;
			if(this.previous.length<=1)
				this.switchClassSticky(_$("undo"), 'editAreaButtonDisabled', true);
		
			if(!this.previous[this.previous.length-1] || this.previous[this.previous.length-1]["text"] != text){
				this.previous.push({"text": text, "selStart": this.textarea.selectionStart, "selEnd": this.textarea.selectionEnd});
				if(this.previous.length > this.settings["max_undo"]+1)
					this.previous.shift();
				
			}
			if(this.previous.length >= 2)
				this.switchClassSticky(_$("undo"), 'editAreaButtonNormal', false);		
		}

		if(!only_once)
			setTimeout("editArea.check_undo()", 3000);
	};
	
	EditArea.prototype.undo= function(){
		//alert("undo"+this.previous.length);
		if(this.previous.length > 0)
		{
			this.getIESelection();
		//	var pos_cursor=this.textarea.selectionStart;
			this.next.push( { "text": this.textarea.value, "selStart": this.textarea.selectionStart, "selEnd": this.textarea.selectionEnd } );
			var prev= this.previous.pop();
			if( prev["text"] == this.textarea.value && this.previous.length > 0 )
				prev	=this.previous.pop();						
			this.textarea.value	= prev["text"];
			this.last_undo		= prev["text"];
			this.area_select(prev["selStart"], prev["selEnd"]-prev["selStart"]);
			this.switchClassSticky(_$("redo"), 'editAreaButtonNormal', false);
			this.resync_highlight(true);
			//alert("undo"+this.previous.length);
			this.check_file_changes();
		}
	};
	
	EditArea.prototype.redo= function(){
		if(this.next.length > 0)
		{
			/*this.getIESelection();*/
			//var pos_cursor=this.textarea.selectionStart;
			var next= this.next.pop();
			this.previous.push(next);
			this.textarea.value= next["text"];
			this.last_undo= next["text"];
			this.area_select(next["selStart"], next["selEnd"]-next["selStart"]);
			this.switchClassSticky(_$("undo"), 'editAreaButtonNormal', false);
			this.resync_highlight(true);
			this.check_file_changes();
		}
		if(	this.next.length == 0)
			this.switchClassSticky(_$("redo"), 'editAreaButtonDisabled', true);
	};
	
	EditArea.prototype.check_redo= function(){
		if(editArea.next.length == 0 || editArea.textarea.value!=editArea.last_undo){
			editArea.next= [];	// undo the ability to use "redo" button
			editArea.switchClassSticky(_$("redo"), 'editAreaButtonDisabled', true);
		}
		else
		{
			this.switchClassSticky(_$("redo"), 'editAreaButtonNormal', false);
		}
	};
	
	
	// functions that manage icons roll over, disabled, etc...
	EditArea.prototype.switchClass = function(element, class_name, lock_state) {
		var lockChanged = false;
	
		if (typeof(lock_state) != "undefined" && element != null) {
			element.classLock = lock_state;
			lockChanged = true;
		}
	
		if (element != null && (lockChanged || !element.classLock)) {
			element.oldClassName = element.className;
			element.className = class_name;
		}
	};
	
	EditArea.prototype.restoreAndSwitchClass = function(element, class_name) {
		if (element != null && !element.classLock) {
			this.restoreClass(element);
			this.switchClass(element, class_name);
		}
	};
	
	EditArea.prototype.restoreClass = function(element) {
		if (element != null && element.oldClassName && !element.classLock) {
			element.className = element.oldClassName;
			element.oldClassName = null;
		}
	};
	
	EditArea.prototype.setClassLock = function(element, lock_state) {
		if (element != null)
			element.classLock = lock_state;
	};
	
	EditArea.prototype.switchClassSticky = function(element, class_name, lock_state) {
		var lockChanged = false;
		if (typeof(lock_state) != "undefined" && element != null) {
			element.classLock = lock_state;
			lockChanged = true;
		}
	
		if (element != null && (lockChanged || !element.classLock)) {
			element.className = class_name;
			element.oldClassName = class_name;
		}
	};
	
	//make the "page up" and "page down" buttons works correctly
	EditArea.prototype.scroll_page= function(params){
		var dir= params["dir"], shift_pressed= params["shift"];
		var lines= this.textarea.value.split("\n");		
		var new_pos=0, length=0, char_left=0, line_nb=0, curLine=0;
		var toScrollAmount	= _$("result").clientHeight -30;
		var nbLineToScroll	= 0, diff= 0;
		
		if(dir=="up"){
			nbLineToScroll	= Math.ceil( toScrollAmount / this.lineHeight );
			
			// fix number of line to scroll
			for( i = this.last_selection["line_start"]; i - diff > this.last_selection["line_start"] - nbLineToScroll ; i-- )
			{
				if( elem = _$('line_'+ i) )
				{
					diff +=  Math.floor( ( elem.offsetHeight - 1 ) / this.lineHeight );
				}
			}
			nbLineToScroll	-= diff;
			
			if(this.last_selection["selec_direction"]=="up"){
				for(line_nb=0; line_nb< Math.min(this.last_selection["line_start"]-nbLineToScroll, lines.length); line_nb++){
					new_pos+= lines[line_nb].length + 1;
				}
				char_left=Math.min(lines[Math.min(lines.length-1, line_nb)].length, this.last_selection["curr_pos"]-1);
				if(shift_pressed)
					length=this.last_selection["selectionEnd"]-new_pos-char_left;	
				this.area_select(new_pos+char_left, length);
				view="top";
			}else{			
				view="bottom";
				for(line_nb=0; line_nb< Math.min(this.last_selection["line_start"]+this.last_selection["line_nb"]-1-nbLineToScroll, lines.length); line_nb++){
					new_pos+= lines[line_nb].length + 1;
				}
				char_left=Math.min(lines[Math.min(lines.length-1, line_nb)].length, this.last_selection["curr_pos"]-1);
				if(shift_pressed){
					//length=this.last_selection["selectionEnd"]-new_pos-char_left;	
					start= Math.min(this.last_selection["selectionStart"], new_pos+char_left);
					length= Math.max(new_pos+char_left, this.last_selection["selectionStart"] )- start ;
					if(new_pos+char_left < this.last_selection["selectionStart"])
						view="top";
				}else
					start=new_pos+char_left;
				this.area_select(start, length);
				
			}
		}
		else
		{
			var nbLineToScroll= Math.floor( toScrollAmount / this.lineHeight );
			// fix number of line to scroll
			for( i = this.last_selection["line_start"]; i + diff < this.last_selection["line_start"] + nbLineToScroll ; i++ )
			{
				if( elem = _$('line_'+ i) )
				{
					diff +=  Math.floor( ( elem.offsetHeight - 1 ) / this.lineHeight );
				}
			}
			nbLineToScroll	-= diff;
				
			if(this.last_selection["selec_direction"]=="down"){
				view="bottom";
				for(line_nb=0; line_nb< Math.min(this.last_selection["line_start"]+this.last_selection["line_nb"]-2+nbLineToScroll, lines.length); line_nb++){
					if(line_nb==this.last_selection["line_start"]-1)
						char_left= this.last_selection["selectionStart"] -new_pos;
					new_pos+= lines[line_nb].length + 1;
									
				}
				if(shift_pressed){
					length=Math.abs(this.last_selection["selectionStart"]-new_pos);	
					length+=Math.min(lines[Math.min(lines.length-1, line_nb)].length, this.last_selection["curr_pos"]);
					//length+=Math.min(lines[Math.min(lines.length-1, line_nb)].length, char_left);
					this.area_select(Math.min(this.last_selection["selectionStart"], new_pos), length);
				}else{
					this.area_select(new_pos+char_left, 0);
				}
				
			}else{
				view="top";
				for(line_nb=0; line_nb< Math.min(this.last_selection["line_start"]+nbLineToScroll-1, lines.length, lines.length); line_nb++){
					if(line_nb==this.last_selection["line_start"]-1)
						char_left= this.last_selection["selectionStart"] -new_pos;
					new_pos+= lines[line_nb].length + 1;									
				}
				if(shift_pressed){
					length=Math.abs(this.last_selection["selectionEnd"]-new_pos-char_left);	
					length+=Math.min(lines[Math.min(lines.length-1, line_nb)].length, this.last_selection["curr_pos"])- char_left-1;
					//length+=Math.min(lines[Math.min(lines.length-1, line_nb)].length, char_left);
					this.area_select(Math.min(this.last_selection["selectionEnd"], new_pos+char_left), length);
					if(new_pos+char_left > this.last_selection["selectionEnd"])
						view="bottom";
				}else{
					this.area_select(new_pos+char_left, 0);
				}
				
			}
		}
		//console.log( new_pos, char_left, length, nbLineToScroll, toScrollAmount, _$("result").clientHeigh );
		this.check_line_selection();
		this.scroll_to_view(view);
	};
	
	EditArea.prototype.start_resize= function(e){
		parent.editAreaLoader.resize["id"]		= editArea.id;		
		parent.editAreaLoader.resize["start_x"]	= (e)? e.pageX : event.x + document.body.scrollLeft;		
		parent.editAreaLoader.resize["start_y"]	= (e)? e.pageY : event.y + document.body.scrollTop;
		if(editArea.isIE)
		{
			editArea.textarea.focus();
			editArea.getIESelection();
		}
		parent.editAreaLoader.resize["selectionStart"]	= editArea.textarea.selectionStart;
		parent.editAreaLoader.resize["selectionEnd"]	= editArea.textarea.selectionEnd;
		parent.editAreaLoader.start_resize_area();
	};
	
	EditArea.prototype.toggle_full_screen= function(to){
		var t=this, p=parent, a=t.textarea, html, frame, selStart, selEnd, old, icon;
		if(typeof(to)=="undefined")
			to= !t.fullscreen['isFull'];
		old			= t.fullscreen['isFull'];
		t.fullscreen['isFull']= to;
		icon		= _$("fullscreen");
		selStart	= t.textarea.selectionStart;
		selEnd		= t.textarea.selectionEnd;
		html		= p.document.getElementsByTagName("html")[0];
		frame		= p.document.getElementById("frame_"+t.id);
		
		if(to && to!=old)
		{	// toogle on fullscreen		
			
			t.fullscreen['old_overflow']	= p.get_css_property(html, "overflow");
			t.fullscreen['old_height']		= p.get_css_property(html, "height");
			t.fullscreen['old_width']		= p.get_css_property(html, "width");
			t.fullscreen['old_scrollTop']	= html.scrollTop;
			t.fullscreen['old_scrollLeft']	= html.scrollLeft;
			t.fullscreen['old_zIndex']		= p.get_css_property(frame, "z-index");
			if(t.isOpera){
				html.style.height	= "100%";
				html.style.width	= "100%";	
			}
			html.style.overflow	= "hidden";
			html.scrollTop		= 0;
			html.scrollLeft		= 0;
			
			frame.style.position	= "absolute";
			frame.style.width		= html.clientWidth+"px";
			frame.style.height		= html.clientHeight+"px";
			frame.style.display		= "block";
			frame.style.zIndex		= "999999";
			frame.style.top			= "0px";
			frame.style.left		= "0px";
			
			// if the iframe was in a div with position absolute, the top and left are the one of the div, 
			// so I fix it by seeing at witch position the iframe start and correcting it
			//cds add frame.style.top			= "-"+p.calculeOffsetTop(frame)+"px";
			//cds add frame.style.left		= "-"+p.calculeOffsetLeft(frame)+"px";
			
		//	parent.editAreaLoader.execCommand(t.id, "update_size();");
		//	var body=parent.document.getElementsByTagName("body")[0];
		//	body.appendChild(frame);
			
			t.switchClassSticky(icon, 'editAreaButtonSelected', false);
			t.fullscreen['allow_resize']= t.resize_allowed;
			t.allow_resize(false);
	
			//t.area_select(selStart, selEnd-selStart);
			
		
			// opera can't manage to do a direct size update
			if(t.isFirefox){
				p.editAreaLoader.execCommand(t.id, "update_size();");
				t.area_select(selStart, selEnd-selStart);
				t.scroll_to_view();
				t.focus();
			}else{
				setTimeout("parent.editAreaLoader.execCommand('"+ t.id +"', 'update_size();');editArea.focus();", 10);
			}	
			
			/////// cds add 全屏功能//////////
			var el = frame;
			while(el = p.getParent(el)){
				var oldStyle = el.style.cssText||"";
				var className = el.className || "";
				p.setAttribute(el,"_style",oldStyle);
				p.setAttribute(el,"_class",className);
				if(el.tagName=="HTML" || el.tagName=="BODY"){
					el.style.cssText = 'position: static; overflow: hidden; z-index: 9999; width: 0px; height: 0px;';
				}else{
					el.style.cssText = 'position: static; overflow: visible; z-index: 9999;';
				}
				el.className="";
			}
			//////// cds add end //////////
		}
		else if(to!=old)
		{	// toogle off fullscreen
			frame.style.position="static";
			frame.style.zIndex= t.fullscreen['old_zIndex'];
		
			if(t.isOpera)
			{
				html.style.height	= "auto"; 
				html.style.width	= "auto";
				html.style.overflow	= "auto";
			}
			else if(t.isIE && p!=top)
			{	// IE doesn't manage html overflow in frames like in normal page... 
				html.style.overflow	= "auto";
			}
			else
			{
				html.style.overflow	= t.fullscreen['old_overflow'];
			}
			html.scrollTop	= t.fullscreen['old_scrollTop'];
			html.scrollLeft	= t.fullscreen['old_scrollLeft'];
		
			p.editAreaLoader.hide(t.id);
			p.editAreaLoader.show(t.id);
			
			t.switchClassSticky(icon, 'editAreaButtonNormal', false);
			if(t.fullscreen['allow_resize'])
				t.allow_resize(t.fullscreen['allow_resize']);
			if(t.isFirefox){
				t.area_select(selStart, selEnd-selStart);
				setTimeout("editArea.scroll_to_view();", 10);
			}			
			
			//p.editAreaLoader.remove_event(p.window, "resize", editArea.update_size);
			
			/////// cds add 退出全屏功能//////////
			var el = frame;
			while(el = p.getParent(el)){
				p.setAttribute(el,"style",p.getAttribute(el,"_style"));
				p.setAttribute(el,"class",p.getAttribute(el,"_class"));
				el.removeAttribute('_style');
				el.removeAttribute('_class');
			}
			//////// cds add end //////////
		}
		
	};
	
	EditArea.prototype.allow_resize= function(allow){
		var resize= _$("resize_area");
		if(allow){
			
			resize.style.visibility="visible";
			parent.editAreaLoader.add_event(resize, "mouseup", editArea.start_resize);
		}else{
			resize.style.visibility="hidden";
			parent.editAreaLoader.remove_event(resize, "mouseup", editArea.start_resize);
		}
		this.resize_allowed= allow;
	};
	
	
	EditArea.prototype.change_syntax= function(new_syntax, is_waiting){
	//	alert("cahnge to "+new_syntax);
		// the syntax is the same
		if(new_syntax==this.settings['syntax'])
			return true;
		
		// check that the syntax is one allowed
		var founded= false;
		for(var i=0; i<this.syntax_list.length; i++)
		{
			if(this.syntax_list[i]==new_syntax)
				founded= true;
		}
		
		if(founded==true)
		{
			// the reg syntax file is not loaded
			if(!parent.editAreaLoader.load_syntax[new_syntax])
			{
				// load the syntax file and wait for file loading
				if(!is_waiting)
					parent.editAreaLoader.load_script(parent.editAreaLoader.baseURL + "reg_syntax/" + new_syntax + ".js");
				setTimeout("editArea.change_syntax('"+ new_syntax +"', true);", 100);
				this.show_waiting_screen();
			}
			else
			{
				if(!this.allready_used_syntax[new_syntax])
				{	// the syntax has still not been used
					// rebuild syntax definition for new languages
					parent.editAreaLoader.init_syntax_regexp();
					// add style to the new list
					this.add_style(parent.editAreaLoader.syntax[new_syntax]["styles"]);
					this.allready_used_syntax[new_syntax]=true;
				}
				// be sure that the select option is correctly updated
				var sel= _$("syntax_selection");
				if(sel && sel.value!=new_syntax)
				{
					for(var i=0; i<sel.length; i++){
						if(sel.options[i].value && sel.options[i].value == new_syntax)
							sel.options[i].selected=true;
					}
				}
				
			/*	if(this.settings['syntax'].length==0)
				{
					this.switchClassSticky(_$("highlight"), 'editAreaButtonNormal', false);
					this.switchClassSticky(_$("reset_highlight"), 'editAreaButtonNormal', false);
					this.change_highlight(true);
				}
				*/
				this.settings['syntax']= new_syntax;
				this.resync_highlight(true);
				this.hide_waiting_screen();
				return true;
			}
		}
		return false;
	};
	
	
	// check if the file has changed
	EditArea.prototype.set_editable= function(is_editable){
		if(is_editable)
		{
			document.body.className= "";
			this.textarea.readOnly= false;
			this.is_editable= true;
		}
		else
		{
			document.body.className= "non_editable";
			this.textarea.readOnly= true;
			this.is_editable= false;
		}
		
		if(editAreas[this.id]["displayed"]==true)
			this.update_size();
	};
	
	/***** Wrap mode *****/
	
	// toggling function for set_wrap_mode
	EditArea.prototype.toggle_word_wrap= function(){
		this.set_word_wrap( !this.settings['word_wrap'] );
	};
	
	
	// open a new tab for the given file
	EditArea.prototype.set_word_wrap= function(to){
		var t=this, a= t.textarea;
		if( t.isOpera && t.isOpera < 9.8 )
		{
			this.settings['word_wrap']= false;
			t.switchClassSticky( _$("word_wrap"), 'editAreaButtonDisabled', true );
			return false;
		}
		
		if( to )
		{
			wrap_mode = 'soft';
			this.container.className+= ' word_wrap';
			this.container.style.width="";
			this.content_highlight.style.width="";
			a.style.width="100%";
			if( t.isIE && t.isIE < 7 )	// IE 6 count 50 px too much
			{
				a.style.width	= ( a.offsetWidth-5 )+"px";
			}
			
			t.switchClassSticky( _$("word_wrap"), 'editAreaButtonSelected', false );
		}
		else
		{
			wrap_mode = 'off';
			this.container.className	= this.container.className.replace(/word_wrap/g, '');
			t.switchClassSticky( _$("word_wrap"), 'editAreaButtonNormal', true );
		}
		this.textarea.previous_scrollWidth = '';
		this.textarea.previous_scrollHeight = '';
		
		a.wrap= wrap_mode;
		//a.setAttribute('wrap', wrap_mode);
		// only IE can change wrap mode on the fly without element reloading//roney:seems not necessary;
		/*
		if(!this.isIE)
		{
			var start=a.selectionStart, end= a.selectionEnd;
			var parNod = a.parentNode, nxtSib = a.nextSibling;
			parNod.removeChild(a);
			parNod.insertBefore(a, nxtSib);
			this.area_select(start, end-start);
		}
		*/
		// reset some optimisation
		this.settings['word_wrap']	= to;
		this.focus();
		this.update_size();
		this.check_line_selection();
	};	
	/***** tabbed files managing functions *****/
	
	// open a new tab for the given file
	EditArea.prototype.open_file= function(settings){
		
		if(settings['id']!="undefined")
		{
			var id= settings['id'];
			// create a new file object with defautl values
			var new_file= {};
			new_file['id']			= id;
			new_file['title']		= id;
			new_file['text']		= "";
			new_file['last_selection']	= "";		
			new_file['last_text_to_highlight']	= "";
			new_file['last_hightlighted_text']	= "";
			new_file['previous']	= [];
			new_file['next']		= [];
			new_file['last_undo']	= "";
			new_file['smooth_selection']	= this.settings['smooth_selection'];
			new_file['do_highlight']= this.settings['start_highlight'];
			new_file['syntax']		= this.settings['syntax'];
			new_file['scroll_top']	= 0;
			new_file['scroll_left']	= 0;
			new_file['selection_start']= 0;
			new_file['selection_end']= 0;
			new_file['edited']		= false;
			new_file['font_size']	= this.settings["font_size"];
			new_file['font_family']	= this.settings["font_family"];
			new_file['word_wrap']	= this.settings["word_wrap"];
			new_file['toolbar']		= {'links':{}, 'selects': {}};
			new_file['compare_edited_text']= new_file['text'];
			
			
			this.files[id]= new_file;
			this.update_file(id, settings);
			this.files[id]['compare_edited_text']= this.files[id]['text'];
			
			
			var html_id= 'tab_file_'+encodeURIComponent(id);
			this.filesIdAssoc[html_id]= id;
			this.files[id]['html_id']= html_id;
		
			if(!_$(this.files[id]['html_id']) && id!="")
			{
				// be sure the tab browsing area is displayed
				this.tab_browsing_area.style.display= "block";
				var elem= document.createElement('li');
				elem.id= this.files[id]['html_id'];
				var close= "<img src=\""+ parent.editAreaLoader.baseURL +"images/close.gif\" title=\""+ this.get_translation('close_tab', 'word') +"\" onclick=\"editArea.execCommand('close_file', editArea.filesIdAssoc['"+ html_id +"']);return false;\" class=\"hidden\" onmouseover=\"this.className=''\" onmouseout=\"this.className='hidden'\" />";
				elem.innerHTML= "<a onclick=\"javascript:editArea.execCommand('switch_to_file', editArea.filesIdAssoc['"+ html_id +"']);\" selec=\"none\"><b><span><strong class=\"edited\">*</strong>"+ this.files[id]['title'] + close +"</span></b></a>";
				_$('tab_browsing_list').appendChild(elem);
				var elem= document.createElement('text');
				this.update_size();
			}
			
			// open file callback (for plugin)
			if(id!="")
				this.execCommand('file_open', this.files[id]);
			
			this.switch_to_file(id, true);
			return true;
		}
		else
			return false;
	};
	
	// close the given file
	EditArea.prototype.close_file= function(id){
		if(this.files[id])
		{
			this.save_file(id);
			
			// close file callback
			if(this.execCommand('file_close', this.files[id])!==false)
			{
				// remove the tab in the toolbar
				var li= _$(this.files[id]['html_id']);
				li.parentNode.removeChild(li);
				// select a new file
				if(id== this.curr_file)
				{
					var next_file= "";
					var is_next= false;
					for(var i in this.files)
					{
						if( is_next )
						{
							next_file	= i;
							break;
						}
						else if( i == id )
							is_next		= true;
						else
							next_file	= i;
					}
					// display the next file
					this.switch_to_file(next_file);
				}
				// clear datas
				delete (this.files[id]);
				this.update_size();
			}	
		}
	};
	
	// backup current file datas
	EditArea.prototype.save_file= function(id){
		var t= this, save, a_links, a_selects, save_butt, img, i;
		if(t.files[id])
		{
			var save= t.files[id];
			save['last_selection']			= t.last_selection;		
			save['last_text_to_highlight']	= t.last_text_to_highlight;
			save['last_hightlighted_text']	= t.last_hightlighted_text;
			save['previous']				= t.previous;
			save['next']					= t.next;
			save['last_undo']				= t.last_undo;
			save['smooth_selection']		= t.smooth_selection;
			save['do_highlight']			= t.do_highlight;
			save['syntax']					= t.settings['syntax'];
			save['text']					= t.textarea.value;
			save['scroll_top']				= t.result.scrollTop;
			save['scroll_left']				= t.result.scrollLeft;
			save['selection_start']			= t.last_selection["selectionStart"];
			save['selection_end']			= t.last_selection["selectionEnd"];
			save['font_size']				= t.settings["font_size"];
			save['font_family']				= t.settings["font_family"];
			save['word_wrap']				= t.settings["word_wrap"];
			save['toolbar']					= {'links':{}, 'selects': {}};
			
			// save toolbar buttons state for fileSpecific buttons
			a_links= _$("toolbar_1").getElementsByTagName("a");
			for( i=0; i<a_links.length; i++ )
			{
				if( a_links[i].getAttribute('fileSpecific') == 'yes' )
				{
					save_butt	= {};
					img			= a_links[i].getElementsByTagName('img')[0];
					save_butt['classLock']		= img.classLock;
					save_butt['className']		= img.className;
					save_butt['oldClassName']	= img.oldClassName;
					
					save['toolbar']['links'][a_links[i].id]= save_butt;
				}
			}
			
			// save toolbar select state for fileSpecific buttons
			a_selects= _$("toolbar_1").getElementsByTagName("select");
			for( i=0; i<a_selects.length; i++)
			{
				if(a_selects[i].getAttribute('fileSpecific')=='yes')
				{
					save['toolbar']['selects'][a_selects[i].id]= a_selects[i].value;
				}
			}
				
			t.files[id]= save;
			
			return save;
		}
		
		return false;
	};
	
	// update file_datas
	EditArea.prototype.update_file= function(id, new_values){
		for(var i in new_values)
		{
			this.files[id][i]= new_values[i];
		}
	};
	
	// display file datas
	EditArea.prototype.display_file= function(id){
		var t = this, a= t.textarea, new_file, a_lis, a_selects, a_links, a_options, i, j;
		
		// we're showing the empty file
		if(id=='')
		{
			a.readOnly= true;
			t.tab_browsing_area.style.display= "none";
			_$("no_file_selected").style.display= "block";
			t.result.className= "empty";
			// clear current datas
			if(!t.files[''])
			{
				t.open_file({id: ''});
			}
		}
		// we try to show a non existent file, so we left
		else if( typeof( t.files[id] ) == 'undefined' )
		{
			return false;
		}
		// display a normal file
		else
		{
			t.result.className= "";
			a.readOnly= !t.is_editable;
			_$("no_file_selected").style.display= "none";
			t.tab_browsing_area.style.display= "block";
		}
		
		// ensure to have last state for undo/redo actions
		t.check_redo(true);
		t.check_undo(true);
		t.curr_file= id;
		
		// replace selected tab file
		a_lis= t.tab_browsing_area.getElementsByTagName('li');
		for( i=0; i<a_lis.length; i++)
		{
			if(a_lis[i].id == t.files[id]['html_id'])
				a_lis[i].className='selected';
			else
				a_lis[i].className='';
		}
		
		// replace next files datas
		new_file= t.files[id];
	
		// restore text content
		a.value= new_file['text'];
		
		// restore font-size
		t.set_font(new_file['font_family'], new_file['font_size']);
		
		// restore selection and scroll
		t.area_select(new_file['selection_start'], new_file['selection_end'] - new_file['selection_start']);
		t.manage_size(true);
		t.result.scrollTop= new_file['scroll_top'];
		t.result.scrollLeft= new_file['scroll_left'];
		
		// restore undo, redo
		t.previous=	new_file['previous'];
		t.next=	new_file['next'];
		t.last_undo=	new_file['last_undo'];
		t.check_redo(true);
		t.check_undo(true);
		
		// restore highlight
		t.execCommand("change_highlight", new_file['do_highlight']);
		t.execCommand("change_syntax", new_file['syntax']);
		
		// smooth mode
		t.execCommand("change_smooth_selection_mode", new_file['smooth_selection']);
		
		// word_wrap
		t.execCommand("set_word_wrap", new_file['word_wrap']);
			
		// restore links state in toolbar
		a_links= new_file['toolbar']['links'];
		for( i in a_links)
		{
			if( img =  _$(i).getElementsByTagName('img')[0] )
			{
				img.classLock	= a_links[i]['classLock'];
				img.className	= a_links[i]['className'];
				img.oldClassName= a_links[i]['oldClassName'];
			}
		}
		
		// restore select state in toolbar
		a_selects = new_file['toolbar']['selects'];
		for( i in a_selects)
		{
			a_options	= _$(i).options;
			for( j=0; j<a_options.length; j++)
			{
				if( a_options[j].value == a_selects[i] )
					_$(i).options[j].selected=true;
			}
		}
	
	};

	// change tab for displaying a new one
	EditArea.prototype.switch_to_file= function(file_to_show, force_refresh){
		if(file_to_show!=this.curr_file || force_refresh)
		{
			this.save_file(this.curr_file);
			if(this.curr_file!='')
				this.execCommand('file_switch_off', this.files[this.curr_file]);
			this.display_file(file_to_show);
			if(file_to_show!='')
				this.execCommand('file_switch_on', this.files[file_to_show]);
		}
	};

	// get all infos for the given file
	EditArea.prototype.get_file= function(id){
		if(id==this.curr_file)
			this.save_file(id);
		return this.files[id];
	};
	
	// get all available files infos
	EditArea.prototype.get_all_files= function(){
		tmp_files= this.files;
		this.save_file(this.curr_file);
		if(tmp_files[''])
			delete(this.files['']);
		return tmp_files;
	};
	
	
	// check if the file has changed
	EditArea.prototype.check_file_changes= function(){
	
		var id= this.curr_file;
		if(this.files[id] && this.files[id]['compare_edited_text']!=undefined)
		{
			if(this.files[id]['compare_edited_text'].length==this.textarea.value.length && this.files[id]['compare_edited_text']==this.textarea.value)
			{
				if(this.files[id]['edited']!= false)
					this.set_file_edited_mode(id, false);
			}
			else
			{
				if(this.files[id]['edited']!= true)
					this.set_file_edited_mode(id, true);
			}
		}
	};
	
	// set if the file is edited or not
	EditArea.prototype.set_file_edited_mode= function(id, to){
		// change CSS for edited tab
		if(this.files[id] && _$(this.files[id]['html_id']))
		{
			var link= _$(this.files[id]['html_id']).getElementsByTagName('a')[0];
			if(to==true)
			{
				link.className= 'edited';
			}
			else
			{
				link.className= '';
				if(id==this.curr_file)
					text= this.textarea.value;
				else
					text= this.files[id]['text'];
				this.files[id]['compare_edited_text']= text;
			}
				
			this.files[id]['edited']= to;
		}
	};

	EditArea.prototype.set_show_line_colors = function(new_value){
		this.show_line_colors = new_value;
		
		if( new_value )
			this.selection_field.className	+= ' show_colors';
		else
			this.selection_field.className	= this.selection_field.className.replace( / show_colors/g, '' );
	};
	/*代码格式化开始*/
	EditArea.prototype.format_code= function(){
		var source = this.textarea.value;
		source = source.replace(/^\s+/, '');
		if (source && source.charAt(0) === '<'){
			this.textarea.value = this.style_html(source, 1, "	", 300);
		} else{
			this.textarea.value = this.js_beautify(source, 1,"	");
		} 
		this.resync_highlight(true);
		
	};
	EditArea.prototype.style_html= function(html_source, indent_size, indent_character, max_char) {
		//Wrapper function to invoke all the necessary constructors and deal with the output.
		var Parser, multi_parser;

		function Parser() {

			this.pos = 0; //Parser position
			this.token = '';
			this.current_mode = 'CONTENT'; //reflects the current Parser mode: TAG/CONTENT
			this.tags = { //An object to hold tags, their position, and their parent-tags, initiated with default values
				parent: 'parent1',
				parentcount: 1,
				parent1: ''
			};
			this.tag_type = '';
			this.token_text = this.last_token = this.last_text = this.token_type = '';

			this.Utils = { //Uilities made available to the various functions
				whitespace: "\n\r\t ".split(''),
				single_token: 'br,input,link,meta,!doctype,basefont,base,area,hr,wbr,param,img,isindex,?xml,embed'.split(','),
				//all the single tags for HTML
				extra_liners: 'head,body,/html'.split(','),
				//for tags that need a line of whitespace before them
				in_array: function(what, arr) {
					for (var i = 0; i < arr.length; i++) {
						if (what === arr[i]) {
							return true;
						}
					}
					return false;
				}
			}

			this.get_content = function() { //function to capture regular content between tags
				var char = '';
				var content = [];
				var space = false; //if a space is needed
				while (this.input.charAt(this.pos) !== '<') {
					if (this.pos >= this.input.length) {
						return content.length ? content.join('') : ['', 'TK_EOF'];
					}

					char = this.input.charAt(this.pos);
					this.pos++;
					this.line_char_count++;

					if (this.Utils.in_array(char, this.Utils.whitespace)) {
						if (content.length) {
							space = true;
						}
						this.line_char_count--;
						continue; //don't want to insert unnecessary space
					} else if (space) {
						if (this.line_char_count >= this.max_char) { //insert a line when the max_char is reached
							content.push('\n');
							for (var i = 0; i < this.indent_level; i++) {
								content.push(this.indent_string);
							}
							this.line_char_count = 0;
						} else {
							content.push(' ');
							this.line_char_count++;
						}
						space = false;
					}
					content.push(char); //letter at-a-time (or string) inserted to an array
				}
				return content.length ? content.join('') : '';
			}

			this.get_script = function() { //get the full content of a script to pass to js_beautify
				var char = '';
				var content = [];
				var reg_match = new RegExp('\<\/script' + '\>', 'igm');
				reg_match.lastIndex = this.pos;
				var reg_array = reg_match.exec(this.input);
				var end_script = reg_array ? reg_array.index: this.input.length; //absolute end of script
				while (this.pos < end_script) { //get everything in between the script tags
					if (this.pos >= this.input.length) {
						return content.length ? content.join('') : ['', 'TK_EOF'];
					}

					char = this.input.charAt(this.pos);
					this.pos++;

					content.push(char);
				}
				return content.length ? content.join('') : ''; //we might not have any content at all
			}

			this.record_tag = function(tag) { //function to record a tag and its parent in this.tags Object
				if (this.tags[tag + 'count']) { //check for the existence of this tag type
					this.tags[tag + 'count']++;
					this.tags[tag + this.tags[tag + 'count']] = this.indent_level; //and record the present indent level
				} else { //otherwise initialize this tag type
					this.tags[tag + 'count'] = 1;
					this.tags[tag + this.tags[tag + 'count']] = this.indent_level; //and record the present indent level
				}
				this.tags[tag + this.tags[tag + 'count'] + 'parent'] = this.tags.parent; //set the parent (i.e. in the case of a div this.tags.div1parent)
				this.tags.parent = tag + this.tags[tag + 'count']; //and make this the current parent (i.e. in the case of a div 'div1')
			}

			this.retrieve_tag = function(tag) { //function to retrieve the opening tag to the corresponding closer
				if (this.tags[tag + 'count']) { //if the openener is not in the Object we ignore it
					var temp_parent = this.tags.parent; //check to see if it's a closable tag.
					while (temp_parent) { //till we reach '' (the initial value);
						if (tag + this.tags[tag + 'count'] === temp_parent) { //if this is it use it
							break;
						}
						temp_parent = this.tags[temp_parent + 'parent']; //otherwise keep on climbing up the DOM Tree
					}
					if (temp_parent) { //if we caught something
						this.indent_level = this.tags[tag + this.tags[tag + 'count']]; //set the indent_level accordingly
						this.tags.parent = this.tags[temp_parent + 'parent']; //and set the current parent
					}
					delete this.tags[tag + this.tags[tag + 'count'] + 'parent']; //delete the closed tags parent reference...
					delete this.tags[tag + this.tags[tag + 'count']]; //...and the tag itself
					if (this.tags[tag + 'count'] == 1) {
						delete this.tags[tag + 'count'];
					} else {
						this.tags[tag + 'count']--;
					}
				}
			}

			this.get_tag = function() { //function to get a full tag and parse its type
				var char = '';
				var content = [];
				var space = false;

				do {
					if (this.pos >= this.input.length) {
						return content.length ? content.join('') : ['', 'TK_EOF'];
					}

					char = this.input.charAt(this.pos);
					this.pos++;
					this.line_char_count++;

					if (this.Utils.in_array(char, this.Utils.whitespace)) { //don't want to insert unnecessary space
						space = true;
						this.line_char_count--;
						continue;
					}

					if (char === "'" || char === '"') {
						if (!content[1] || content[1] !== '!') { //if we're in a comment strings don't get treated specially
							char += this.get_unformatted(char);
							space = true;
						}
					}

					if (char === '=') { //no space before =
						space = false;
					}

					if (content.length && content[content.length - 1] !== '=' && char !== '>' && space) { //no space after = or before >
						if (this.line_char_count >= this.max_char) {
							this.print_newline(false, content);
							this.line_char_count = 0;
						} else {
							content.push(' ');
							this.line_char_count++;
						}
						space = false;
					}
					content.push(char); //inserts character at-a-time (or string)
				} while ( char !== '>');

				var tag_complete = content.join('');
				var tag_index;
				if (tag_complete.indexOf(' ') != -1) { //if there's whitespace, thats where the tag name ends
					tag_index = tag_complete.indexOf(' ');
				} else { //otherwise go with the tag ending
					tag_index = tag_complete.indexOf('>');
				}
				var tag_check = tag_complete.substring(1, tag_index).toLowerCase();
				if (tag_complete.charAt(tag_complete.length - 2) === '/' || this.Utils.in_array(tag_check, this.Utils.single_token)) { //if this tag name is a single tag type (either in the list or has a closing /)
					this.tag_type = 'SINGLE';
				} else if (tag_check === 'script') { //for later script handling
					this.record_tag(tag_check);
					this.tag_type = 'SCRIPT';
				} else if (tag_check === 'style') { //for future style handling (for now it justs uses get_content)
					this.record_tag(tag_check);
					this.tag_type = 'STYLE';
				} else if (tag_check.charAt(0) === '!') { //peek for <!-- comment
					if (tag_check.indexOf('[if') != -1) { //peek for <!--[if conditional comment
						if (tag_complete.indexOf('!IE') != -1) { //this type needs a closing --> so...
							var comment = this.get_unformatted('-->', tag_complete); //...delegate to get_unformatted
							content.push(comment);
						}
						this.tag_type = 'START';
					} else if (tag_check.indexOf('[endif') != -1) { //peek for <!--[endif end conditional comment
						this.tag_type = 'END';
						this.unindent();
					} else if (tag_check.indexOf('[cdata[') != -1) { //if it's a <[cdata[ comment...
						var comment = this.get_unformatted(']]>', tag_complete); //...delegate to get_unformatted function
						content.push(comment);
						this.tag_type = 'SINGLE'; //<![CDATA[ comments are treated like single tags
					} else {
						var comment = this.get_unformatted('-->', tag_complete);
						content.push(comment);
						this.tag_type = 'SINGLE';
					}
				} else {
					if (tag_check.charAt(0) === '/') { //this tag is a double tag so check for tag-ending
						this.retrieve_tag(tag_check.substring(1)); //remove it and all ancestors
						this.tag_type = 'END';
					} else { //otherwise it's a start-tag
						this.record_tag(tag_check); //push it on the tag stack
						this.tag_type = 'START';
					}
					if (this.Utils.in_array(tag_check, this.Utils.extra_liners)) { //check if this double needs an extra line
						this.print_newline(true, this.output);
					}
				}
				return content.join(''); //returns fully formatted tag
			}

			this.get_unformatted = function(delimiter, orig_tag) { //function to return unformatted content in its entirety
				if (orig_tag && orig_tag.indexOf(delimiter) != -1) {
					return '';
				}
				var char = '';
				var content = '';
				var space = true;
				do {

					char = this.input.charAt(this.pos);
					this.pos++

					if (this.Utils.in_array(char, this.Utils.whitespace)) {
						if (!space) {
							this.line_char_count--;
							continue;
						}
						if (char === '\n' || char === '\r') {
							content += '\n';
							for (var i = 0; i < this.indent_level; i++) {
								content += this.indent_string;
							}
							space = false; //...and make sure other indentation is erased
							this.line_char_count = 0;
							continue;
						}
					}
					content += char;
					this.line_char_count++;
					space = true;

				} while ( content . indexOf ( delimiter ) == -1);
				return content;
			}

			this.get_token = function() { //initial handler for token-retrieval
				var token;

				if (this.last_token === 'TK_TAG_SCRIPT') { //check if we need to format javascript
					var temp_token = this.get_script();
					if (typeof temp_token !== 'string') {
						return temp_token;
					}
					token = js_beautify(temp_token, this.indent_size, this.indent_character, this.indent_level); //call the JS Beautifier
					return [token, 'TK_CONTENT'];
				}
				if (this.current_mode === 'CONTENT') {
					token = this.get_content();
					if (typeof token !== 'string') {
						return token;
					} else {
						return [token, 'TK_CONTENT'];
					}
				}

				if (this.current_mode === 'TAG') {
					token = this.get_tag();
					if (typeof token !== 'string') {
						return token;
					} else {
						var tag_name_type = 'TK_TAG_' + this.tag_type;
						return [token, tag_name_type];
					}
				}
			}

			this.printer = function(js_source, indent_character, indent_size, max_char) { //handles input/output and some other printing functions
				this.input = js_source || ''; //gets the input for the Parser
				this.output = [];
				this.indent_character = indent_character || ' ';
				this.indent_string = '';
				this.indent_size = indent_size || 2;
				this.indent_level = 0;
				this.max_char = max_char || 70; //maximum amount of characters per line
				this.line_char_count = 0; //count to see if max_char was exceeded
				for (var i = 0; i < this.indent_size; i++) {
					this.indent_string += this.indent_character;
				}

				this.print_newline = function(ignore, arr) {
					this.line_char_count = 0;
					if (!arr || !arr.length) {
						return;
					}
					if (!ignore) { //we might want the extra line
						while (this.Utils.in_array(arr[arr.length - 1], this.Utils.whitespace)) {
							arr.pop();
						}
					}
					arr.push('\n');
					for (var i = 0; i < this.indent_level; i++) {
						arr.push(this.indent_string);
					}
				}

				this.print_token = function(text) {
					this.output.push(text);
				}

				this.indent = function() {
					this.indent_level++;
				}

				this.unindent = function() {
					if (this.indent_level > 0) {
						this.indent_level--;
					}
				}
			}
			return this;
		}

		/*_____________________--------------------_____________________*/

		multi_parser = new Parser(); //wrapping functions Parser
		multi_parser.printer(html_source, indent_character, indent_size,max_char); //initialize starting values

		while (true) {
			var t = multi_parser.get_token();
			multi_parser.token_text = t[0];
			multi_parser.token_type = t[1];

			if (multi_parser.token_type === 'TK_EOF') {
				break;
			}

			switch (multi_parser.token_type) {
			case 'TK_TAG_START':
			case 'TK_TAG_SCRIPT':
			case 'TK_TAG_STYLE':
				if(multi_parser.last_token!=='TK_CONTENT' || (multi_parser.last_token==='TK_CONTENT' && multi_parser.last_text ==='')){
					multi_parser.print_newline(true, multi_parser.output);
				}//cds add
				multi_parser.print_token(multi_parser.token_text);
				multi_parser.indent();
				multi_parser.current_mode = 'CONTENT';
				break;
			case 'TK_TAG_END':
				if(multi_parser.last_token!=='TK_CONTENT' || (multi_parser.last_token==='TK_CONTENT' && multi_parser.last_text ==='')){
					multi_parser.print_newline(true, multi_parser.output);
				}
				multi_parser.print_token(multi_parser.token_text);
				multi_parser.current_mode = 'CONTENT';
				break;
			case 'TK_TAG_SINGLE':
				multi_parser.print_newline(false, multi_parser.output);
				multi_parser.print_token(multi_parser.token_text);
				multi_parser.current_mode = 'CONTENT';
				break;
			case 'TK_CONTENT':
				if (multi_parser.token_text !== '') {
					//multi_parser.print_newline(false, multi_parser.output);
					multi_parser.print_token(multi_parser.token_text);
				}
				multi_parser.current_mode = 'TAG';
				break;
			}
			multi_parser.last_token = multi_parser.token_type;
			multi_parser.last_text = multi_parser.token_text;
		}
		return multi_parser.output.join('');
	};
	
	EditArea.prototype.js_beautify= function(js_source_text, indent_size, indent_character, indent_level) {
		var input, output, token_text, last_type, last_text, last_word, current_mode, modes, indent_string;
		var whitespace, wordchar, punct, parser_pos, line_starters, in_case;
		var prefix, token_type, do_block_just_closed, var_line, var_line_tainted;



		function trim_output()
		{
			while (output.length && (output[output.length - 1] === ' ' || output[output.length - 1] === indent_string)) {
				output.pop();
			}
		}

		function print_newline(ignore_repeated)
		{
			ignore_repeated = typeof ignore_repeated === 'undefined' ? true: ignore_repeated;
			
			trim_output();

			if (!output.length) {
				return; // no newline on start of file
			}

			if (output[output.length - 1] !== "\n" || !ignore_repeated) {
				output.push("\n");
			}
			for (var i = 0; i < indent_level; i++) {
				output.push(indent_string);
			}
		}



		function print_space()
		{
			var last_output = output.length ? output[output.length - 1] : ' ';
			if (last_output !== ' ' && last_output !== '\n' && last_output !== indent_string) { // prevent occassional duplicate space
				output.push(' ');
			}
		}


		function print_token()
		{
			output.push(token_text);
		}

		function indent()
		{
			indent_level++;
		}


		function unindent()
		{
			if (indent_level) {
				indent_level--;
			}
		}


		function remove_indent()
		{
			if (output.length && output[output.length - 1] === indent_string) {
				output.pop();
			}
		}


		function set_mode(mode)
		{
			modes.push(current_mode);
			current_mode = mode;
		}


		function restore_mode()
		{
			do_block_just_closed = current_mode === 'DO_BLOCK';
			current_mode = modes.pop();
		}


		function in_array(what, arr)
		{
			for (var i = 0; i < arr.length; i++)
			{
				if (arr[i] === what) {
					return true;
				}
			}
			return false;
		}



		function get_next_token()
		{
			var n_newlines = 0;
			var c = '';

			do {
				if (parser_pos >= input.length) {
					return ['', 'TK_EOF'];
				}
				c = input.charAt(parser_pos);

				parser_pos += 1;
				if (c === "\n") {
					n_newlines += 1;
				}
			}
			while (in_array(c, whitespace));

			if (n_newlines > 1) {
				for (var i = 0; i < 2; i++) {
					print_newline(i === 0);
				}
			}
			var wanted_newline = (n_newlines === 1);


			if (in_array(c, wordchar)) {
				if (parser_pos < input.length) {
					while (in_array(input.charAt(parser_pos), wordchar)) {
						c += input.charAt(parser_pos);
						parser_pos += 1;
						if (parser_pos === input.length) {
							break;
						}
					}
				}

				// small and surprisingly unugly hack for 1E-10 representation
				if (parser_pos !== input.length && c.match(/^[0-9]+[Ee]$/) && input.charAt(parser_pos) === '-') {
					parser_pos += 1;

					var t = get_next_token(parser_pos);
					c += '-' + t[0];
					return [c, 'TK_WORD'];
				}

				if (c === 'in') { // hack for 'in' operator
					return [c, 'TK_OPERATOR'];
				}
				return [c, 'TK_WORD'];
			}
			
			if (c === '(' || c === '[') {
				return [c, 'TK_START_EXPR'];
			}

			if (c === ')' || c === ']') {
				return [c, 'TK_END_EXPR'];
			}

			if (c === '{') {
				return [c, 'TK_START_BLOCK'];
			}

			if (c === '}') {
				return [c, 'TK_END_BLOCK'];
			}

			if (c === ';') {
				return [c, 'TK_END_COMMAND'];
			}

			if (c === '/') {
				var comment = '';
				// peek for comment /* ... */
				if (input.charAt(parser_pos) === '*') {
					parser_pos += 1;
					if (parser_pos < input.length) {
						while (! (input.charAt(parser_pos) === '*' && input.charAt(parser_pos + 1) && input.charAt(parser_pos + 1) === '/') && parser_pos < input.length) {
							comment += input.charAt(parser_pos);
							parser_pos += 1;
							if (parser_pos >= input.length) {
								break;
							}
						}
					}
					parser_pos += 2;
					return ['/*' + comment + '*/', 'TK_BLOCK_COMMENT'];
				}
				// peek for comment // ...
				if (input.charAt(parser_pos) === '/') {
					comment = c;
					while (input.charAt(parser_pos) !== "\x0d" && input.charAt(parser_pos) !== "\x0a") {
						comment += input.charAt(parser_pos);
						parser_pos += 1;
						if (parser_pos >= input.length) {
							break;
						}
					}
					parser_pos += 1;
					if (wanted_newline) {
						print_newline();
					}
					return [comment, 'TK_COMMENT'];
				}

			}

			if (c === "'" || // string
			c === '"' || // string
			(c === '/' &&
			((last_type === 'TK_WORD' && last_text === 'return') || (last_type === 'TK_START_EXPR' || last_type === 'TK_END_BLOCK' || last_type === 'TK_OPERATOR' || last_type === 'TK_EOF' || last_type === 'TK_END_COMMAND')))) { // regexp
				var sep = c;
				var esc = false;
				c = '';

				if (parser_pos < input.length) {

					while (esc || input.charAt(parser_pos) !== sep) {
						c += input.charAt(parser_pos);
						if (!esc) {
							esc = input.charAt(parser_pos) === '\\';
						} else {
							esc = false;
						}
						parser_pos += 1;
						if (parser_pos >= input.length) {
							break;
						}
					}

				}

				parser_pos += 1;
				if (last_type === 'TK_END_COMMAND') {
					print_newline();
				}
				return [sep + c + sep, 'TK_STRING'];
			}

			if (in_array(c, punct)) {
				while (parser_pos < input.length && in_array(c + input.charAt(parser_pos), punct)) {
					c += input.charAt(parser_pos);
					parser_pos += 1;
					if (parser_pos >= input.length) {
						break;
					}
				}
				return [c, 'TK_OPERATOR'];
			}

			return [c, 'TK_UNKNOWN'];
		}


		//----------------------------------

		indent_character = indent_character || ' ';
		indent_size = indent_size || 4;

		indent_string = '';
		while (indent_size--) {
			indent_string += indent_character;
		}

		input = js_source_text;

		last_word = ''; // last 'TK_WORD' passed
		last_type = 'TK_START_EXPR'; // last token type
		last_text = ''; // last token text
		output = [];

		do_block_just_closed = false;
		var_line = false;
		var_line_tainted = false;

		whitespace = "\n\r\t ".split('');
		wordchar = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_$'.split('');
		punct = '+ - * / % & ++ -- = += -= *= /= %= == === != !== > < >= <= >> << >>> >>>= >>= <<= && &= | || ! !! , : ? ^ ^= |='.split(' ');

		// words which should always start on new line.
		line_starters = 'continue,try,throw,return,var,if,switch,case,default,for,while,break,function'.split(',');

		// states showing if we are currently in expression (i.e. "if" case) - 'EXPRESSION', or in usual block (like, procedure), 'BLOCK'.
		// some formatting depends on that.
		current_mode = 'BLOCK';
		modes = [current_mode];

		indent_level = indent_level || 0;
		parser_pos = 0; // parser position
		in_case = false; // flag for parser that case/default has been processed, and next colon needs special attention
		while (true) {
			var t = get_next_token(parser_pos);
			token_text = t[0];
			token_type = t[1];
			if (token_type === 'TK_EOF') {
				break;
			}

			switch (token_type) {

			case 'TK_START_EXPR':
				var_line = false;
				set_mode('EXPRESSION');
				if (last_type === 'TK_END_EXPR' || last_type === 'TK_START_EXPR') {
					// do nothing on (( and )( and ][ and ]( ..
				} else if (last_type !== 'TK_WORD' && last_type !== 'TK_OPERATOR') {
					print_space();
				} else if (in_array(last_word, line_starters) && last_word !== 'function') {
					print_space();
				}
				print_token();
				break;

			case 'TK_END_EXPR':
				print_token();
				restore_mode();
				break;

			case 'TK_START_BLOCK':
				
				if (last_word === 'do') {
					set_mode('DO_BLOCK');
				} else {
					set_mode('BLOCK');
				}
				if (last_type !== 'TK_OPERATOR' && last_type !== 'TK_START_EXPR') {
					if (last_type === 'TK_START_BLOCK') {
						print_newline();
					} else {
						print_space();
					}
				}
				print_token();
				indent();
				break;

			case 'TK_END_BLOCK':
				if (last_type === 'TK_START_BLOCK') {
					// nothing
					trim_output();
					unindent();
				} else {
					unindent();
					print_newline();
				}
				print_token();
				restore_mode();
				break;

			case 'TK_WORD':

				if (do_block_just_closed) {
					print_space();
					print_token();
					print_space();
					break;
				}

				if (token_text === 'case' || token_text === 'default') {
					if (last_text === ':') {
						// switch cases following one another
						remove_indent();
					} else {
						// case statement starts in the same line where switch
						unindent();
						print_newline();
						indent();
					}
					print_token();
					in_case = true;
					break;
				}


				prefix = 'NONE';
				if (last_type === 'TK_END_BLOCK') {
					if (!in_array(token_text.toLowerCase(), ['else', 'catch', 'finally'])) {
						prefix = 'NEWLINE';
					} else {
						prefix = 'SPACE';
						print_space();
					}
				} else if (last_type === 'TK_END_COMMAND' && (current_mode === 'BLOCK' || current_mode === 'DO_BLOCK')) {
					prefix = 'NEWLINE';
				} else if (last_type === 'TK_END_COMMAND' && current_mode === 'EXPRESSION') {
					prefix = 'SPACE';
				} else if (last_type === 'TK_WORD') {
					prefix = 'SPACE';
				} else if (last_type === 'TK_START_BLOCK') {
					prefix = 'NEWLINE';
				} else if (last_type === 'TK_END_EXPR') {
					print_space();
					prefix = 'NEWLINE';
				}

				if (last_type !== 'TK_END_BLOCK' && in_array(token_text.toLowerCase(), ['else', 'catch', 'finally'])) {
					print_newline();
				} else if (in_array(token_text, line_starters) || prefix === 'NEWLINE') {
					if (last_text === 'else') {
						// no need to force newline on else break
						print_space();
					} else if ((last_type === 'TK_START_EXPR' || last_text === '=') && token_text === 'function') {
						// no need to force newline on 'function': (function
						// DONOTHING
					} else if (last_type === 'TK_WORD' && (last_text === 'return' || last_text === 'throw')) {
						// no newline between 'return nnn'
						print_space();
					} else if (last_type !== 'TK_END_EXPR') {
						if ((last_type !== 'TK_START_EXPR' || token_text !== 'var') && last_text !== ':') {
							// no need to force newline on 'var': for (var x = 0...)
							if (token_text === 'if' && last_type === 'TK_WORD' && last_word === 'else') {
								// no newline for } else if {
								print_space();
							} else {
								print_newline();
							}
						}
					} else {
						if (in_array(token_text, line_starters) && last_text !== ')') {
							print_newline();
						}
					}
				} else if (prefix === 'SPACE') {
					print_space();
				}
				print_token();
				last_word = token_text;

				if (token_text === 'var') {
					var_line = true;
					var_line_tainted = false;
				}

				break;

			case 'TK_END_COMMAND':

				print_token();
				var_line = false;
				break;

			case 'TK_STRING':

				if (last_type === 'TK_START_BLOCK' || last_type === 'TK_END_BLOCK') {
					print_newline();
				} else if (last_type === 'TK_WORD') {
					print_space();
				}
				print_token();
				break;

			case 'TK_OPERATOR':

				var start_delim = true;
				var end_delim = true;
				if (var_line && token_text !== ',') {
					var_line_tainted = true;
					if (token_text === ':') {
						var_line = false;
					}
				}

				if (token_text === ':' && in_case) {
					print_token(); // colon really asks for separate treatment
					print_newline();
					break;
				}

				in_case = false;

				if (token_text === ',') {
					if (var_line) {
						if (var_line_tainted) {
							print_token();
							print_newline();
							var_line_tainted = false;
						} else {
							print_token();
							print_space();
						}
					} else if (last_type === 'TK_END_BLOCK') {
						print_token();
						print_newline();
					} else {
						if (current_mode === 'BLOCK') {
							print_token();
							print_newline();
						} else {
							// EXPR od DO_BLOCK
							print_token();
							print_space();
						}
					}
					break;
				} else if (token_text === '--' || token_text === '++') { // unary operators special case
					if (last_text === ';') {
						// space for (;; ++i)
						start_delim = true;
						end_delim = false;
					} else {
						start_delim = false;
						end_delim = false;
					}
				} else if (token_text === '!' && last_type === 'TK_START_EXPR') {
					// special case handling: if (!a)
					start_delim = false;
					end_delim = false;
				} else if (last_type === 'TK_OPERATOR') {
					start_delim = false;
					end_delim = false;
				} else if (last_type === 'TK_END_EXPR') {
					start_delim = true;
					end_delim = true;
				} else if (token_text === '.') {
					// decimal digits or object.property
					start_delim = false;
					end_delim = false;

				} else if (token_text === ':') {
					// zz: xx
					// can't differentiate ternary op, so for now it's a ? b: c; without space before colon
					if (last_text.match(/^\d+$/)) {
						// a little help for ternary a ? 1 : 0;
						start_delim = true;
					} else {
						start_delim = false;
					}
				}
				if (start_delim) {
					print_space();
				}

				print_token();

				if (end_delim) {
					print_space();
				}
				break;

			case 'TK_BLOCK_COMMENT':

				print_newline();
				print_token();
				print_newline();
				break;

			case 'TK_COMMENT':

				// print_newline();
				print_space();
				print_token();
				print_newline();
				break;

			case 'TK_UNKNOWN':
				print_token();
				break;
			}

			last_type = token_type;
			last_text = token_text;
		}

		return output.join('');

	};
	/*代码格式化 结束*/
	