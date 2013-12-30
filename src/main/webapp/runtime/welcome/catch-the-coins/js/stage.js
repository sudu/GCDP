var Stage = new function(){
    var that = this; 
    
    var ran = function(a, b){
    	return Math.random() * (b - a) + a;
    };
    
    var timeID;
    var doStart = function(){
    	var t = ran(1, 3);
         	for (var i = 0; i < t; i++){
         		MoneyFactory.create();
         }
    }
    this.init = function(){ 
         G('btn').onclick = function(){
         	this.style.display = 'none';
			G('gameover').style.display = 'none';
         	that.start();
         	
         	return false;
         }
         
    };
    this.start = function(){
    	Man.init();
    	G('money_box').innerHTML = '';
    	doStart(); 
        timeID = setInterval(function(){
         	doStart(); 
         }, ran(1000, 3000));
    };
    
    
    
    this.stop = function(){
    	clearInterval(timeID);
    	G('money_box').innerHTML = '';
    	G('gameover').style.display = 'block';
    	G('btn').style.display = 'block';
    	
    };

	this.showScore =function(s){
		if (s < 0){
			that.stop();
			G('pn').innerHTML = 0;
			return;
		}
	    G("pn").innerHTML = s;
	    
	};
	this.showTime = function(){
	
	};
	this.check = function(mn){
		
	    if(mn.left + 40 >= G("man").offsetLeft  && mn.left <= G("man").offsetLeft+100){ //是否接到

	    	if (mn.type == 'wudi'){
				Man.wudi = true;
				G('man').style.borderTop = '5px solid #33ff00';
				setTimeout(function(){
					G('man').style.borderTop = 'none';
					Man.wudi = false;
				}, 5000)
	    	}
	    	else{
	    		if (Man.wudi){
	    			
	    			if (mn.type == 'boom' || mn.score < 0) return;
	    		}
	    		else {
	    			if (mn.type == 'boom') { that.stop(); return; }
	    		}
	    		that.showScore(Man.CalScore(mn.score));	
	    	}
		    
		     
		}
	};
};
