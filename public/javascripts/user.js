

var User = Spine.Controller.sub({
	elements : {
		"input[name=username]" : "username",
		"input[name=password]"	   : "pwd",
		"input[name=pwdConfirm]" : "pwdConfirm",
		"span.inform" 			: "inform",
		
	},

	events : {
	
		"submit form#register" : "register",
	},
	
	

	register : function(e){
		if(this.pwd.val().length < 6){
			this.inform.html("Password isn't less than 6 character !!");
			e.preventDefault();
		}else if(this.pwd.val() != this.pwdConfirm.val()){
			this.inform.html("Confirm Password is incorrect !");
			e.preventDefault();
		}
		
	},

});