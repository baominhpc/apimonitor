var ApiVersionMain = Spine.Controller.sub({
	
	
	
	init : function(){
		loadConfig();
		this.loadListVersion();
	}, 
	
	elements : {
		"#framecontent .innercontent" : "version_content",
		"#framecontent" 			  : "frame_content"
	},
	
	events : {
		"click #compare" : "compareVersion",
		"click #build_version_bt" : "buildVersion"
		
	},
	
	buildVersion : function(){
		$(".img_loading").css({"display":"block","text-align": "center"});
		$.get("/build_database_for_testcase	", function(){
			window.location = "/version";
		});
			
	},
	
	loadListVersion : function(){
	

		this.version_content.load("/get_list_version", function(){
			$('#framecontent li:last').addClass("active_version");
		});
	},
	

	
	compareVersion : function(){
		if(this.frame_content.find('.ckbox:checked').size() <2){
			alert("Please choose 2 versions to compare !");
			return;
		}
		var ver1 = this.frame_content.find('.ckbox:checked:eq(0)').parent().attr("id");
		var ver2 = this.frame_content.find('.ckbox:checked:eq(1)').parent().attr("id");

		var link = "/compare?ver1=" + ver1 + "&ver2=" + ver2;
		window.open(link);
		
	}
	
	
	
	
})



var ApiVersion = Spine.Controller.sub({
	
	events : {
		"click  a.version" : "getApiByVersion",
		"click .ckbox" : "checkLimit",
		"click .delVersion" : "delVersion"
	},
	
	delVersion : function(e){
		this.el.remove();
		$.post(
			"/delete_version/" + this.id
			
		)
		if($('#framecontent').find('li').size()!=0){
			$('#framecontent').find('li:first a.version').trigger("click");
		}else{
			$('#resources_list').empty();
		}
	},
	
	checkLimit : function(e){
		if($('#framecontent').find('.ckbox:checked').size() > 2 ){
			$(e.target).prop("checked", false);
		}
	},
	
	getApiByVersion : function(){
		Main.getAPI(this.id);
		$('#framecontent li').removeClass();
		$('#' + this.id).addClass("active_version");
		
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
		//compare resource
		self.find(".resource").each(function(){
			var resourceId = $(this).attr("id");
			var resourceCompareId = "";
			if(resourceId.indexOf("list2")!=-1){
				resourceCompareId = resourceId.replace("list2", "list1");	
			}else{
				var resourceCompareId = resourceId.replace("list1", "list2");
			}
			
			if($('#' + resourceCompareId).length == 0){
				$(this).find("div.heading:first").addClass("diff_item");
			}else{
				$(this).find(".endpoints .endpoint .operation").each(function(){
					var id = $(this).attr("id");
					var idCompare = id;
					if(id.indexOf("list2")!=-1){
						idCompare.replace("list2", "list1");	
					}else{
						idCompare.replace("list1", "list2");
					}
					
					if($("#" + idCompare).length == 0){
						$(this).parents(".resource").find("div.heading:first").addClass("diff_item");
						
						$("#" + id).find('div.heading:first').addClass("diff_item");
					}else{
						//compare params
						controller.compareParams(id, idCompare);
							
					}
				});
			}
		});
		//compare operation
		
			
			
		
	},
	
	compareParams : function(id, idCompare){
		var idContent = id + "_content";
		var idCompareContent = idCompare + "_content";
		$('#' + idContent + " tbody input").each(function(){
			name = $(this).attr("name");
			if($("#" + idCompareContent + " tbody input[name=" + name + "]").length == 0){
				$(this).parents(".resource").find("div.heading:first").addClass("diff_item");
				$(this).parents(".operation").find('div.heading:first').addClass("diff_item");
				$(this).parents("tr").addClass("diff_item");
			}
			
		});
	}


})