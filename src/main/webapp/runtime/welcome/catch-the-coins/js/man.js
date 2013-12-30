var Man = new function(){
    var that = this;
    var score = 0;
    var speed = 10;
    var ins = null;
    var moveTID = 0;
    var flag = false;
    
    
    this.wudi = false;
    this.init = function(){ 
		  ins = G("man");
          document.addEventListener("keydown", dokeydown, false);
	      
    };
    var dokeydown = function(e){ 
        
        if(e.which==37){
			if(flag) return;
			flag = true;
			 document.addEventListener("keyup", dokeyup, false);
			 moveTID = window.setInterval(function(){
				 that.moveLeft(speed);
			 },10);
		}else if(e.which==39){
			if(flag) return;
			flag = true;
			 document.addEventListener("keyup", dokeyup, false);
			 moveTID = window.setInterval(function(){ 
				 that.moveRight(speed);
			 },10);
		}
	
    };
    
    
    var dokeyup = function(e){
        flag = false;
        document.removeEventListener("keyup", dokeyup,false);
	    window.clearInterval(moveTID);
    };
    this.moveLeft = function(speed){
        if(ins.offsetLeft<=0) ins.offsetLeft = "0px";
        else ins.style.left = ins.offsetLeft - speed + 'px';
    };
    this.moveRight = function(){
        if(ins.offsetLeft>=900) ins.offsetLeft = "900px";
        else ins.style.left = ins.offsetLeft + speed+ 'px';
    };
    this.CalScore = function(s){
        return score += s;
    };
    
};