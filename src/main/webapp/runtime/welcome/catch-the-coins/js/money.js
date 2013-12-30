var MoneyFactory = new function(){
  
    var Money = function(){

    	this.speed = Math.random() * 5000 + 1000;
		this.left = Math.random() * (1000 - 40);
		this.score = 0;
		this.type = 'common';
		
		var that = this;
		var ins = document.createElement('div');
		ins.className = 'money';
		ins.style.left = that.left + 'px';
		
		var initType = function(){
			var r = Math.random();
			if (r < 0.1){ //wudi
				ins.style.background = 'green';
				ins.style.color = '#fff';
				that.type = 'wudi';
				ins.innerHTML = '无敌';
			}
			else if (r < 0.8){
				that.score = Math.round((Math.random() * 290 + 10) * (Math.random() > 0.5  ?  -1: 1));
				ins.style.background = that.score > 0 ? '#f60' : 'blue';
				ins.style.color = that.score > 0 ? '#000' : '#fff';
				ins.innerHTML = that.score;
			}
			else { //boom
				ins.style.background = 'red';
				ins.style.color = '#fff';
				that.type = 'boom';
				ins.innerHTML = '炸弹';
			}
		}
		
		initType();

		
		G('money_box').appendChild(ins);
		ins.style.webkitTransition = 'all '+ that.speed +'ms linear';
		
		setTimeout(function(){
			ins.style.top = '460px';
			//ins.style.opacity = 0;
			
			setTimeout(function(){
				Stage.check(that);
				that.destory();
			}, that.speed)
		}, 10);
		
		
		
		
    	this.destory  = function(){
    		try {
    			ins.style.webkitTransition = null;
    			G('money_box').removeChild(ins);
    			
    		}
    		
    		catch(e){}
			
		};
		
		
    };
    this.create = function(){
        return new Money();
    };
};
