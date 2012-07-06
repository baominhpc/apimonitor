var ApiVersionMain = Spine.Controller.sub({
	
	
	
	init : function(){
		this.loadListVersion();
	}, 
	
	elements : {
		"#framecontent .innercontent" : "version_content",
	},
	
	events : {
		"click #compare" : "compareVersion",
	},
	
	compareVersion : function(){
		
	},
	
	loadListVersion : function(){
		this.version_content.load("/get_list_version");
	},
	
	
	
	
	
})



var ApiVersion = Spine.Controller.sub({
	
	events : {
		"click  a" : "getApiByVersion",
		"click .ckbox" : "checkLimit"
	},
	
	checkLimit : function(e){
		if($('#framecontent').find('.ckbox:checked').size() > 2 ){
			$(e.target).prop("checked", false);
		}
	},
	
	getApiByVersion : function(){
		Main.getAPI(this.id);
	},
	
})


var VersionCompare = Spine.Controller.sub({

	elements : {
		"#left_frame" : "left_frame",
		"#right_frame" : "right_frame",
	},
	init : function(){
		this.compare();
	},
	compare : function(){
		
		this.compare_in_left();
		this.compare_in_right();
	
	},
	
	compare_in_left : function(){
		var controller = this;
		this.compareInSide(this.left_frame, controller);
	},
	compare_in_right : function(){
		var controller = this;
		this.compareInSide(this.right_frame, controller);
	},
	
	compareInSide : function(self, controller){
		var controller = this;
		self.find(".resource .endpoints .endpoint .operation").each(function(){
			var id = $(this).attr("id");
			var idCompare = id;
			idCompare.replace("list2", "list1");
			if($("#" + idCompare).length == 0){
				$(this).parents(".resource").find("div.heading:first").css("background", "#093");
				
				$("#" + id).find('div.heading:first').css("background", "green");
			}else{
				//compare params
				controller.compareParams(id, idCompare);
					
			}
			
			
		});
	},
	
	compareParams : function(id, idCompare){
		var idContent = id + "_content";
		var idCompareContent = idCompare + "_content";
		$('#' + idContent + " table tbody input").each(function(){
			name = $(this).attr("name");
			if($("#" + idCompareContent + " table tbody input[name=" + name + "]").length == 0){
				$(this).parents(".resource").find(":first").css("background", "orange");
				$(this).parents(".operation").find('div.heading:first').css("background", "green");
				$(this).parents("tr").css("background", "green");
			}
			
		});
	}


})