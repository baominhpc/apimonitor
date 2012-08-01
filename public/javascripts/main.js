$(function(){
	$("#input_apiKey").change(function(){
		save_token_again(this);
	});
	
	$("#input_baseUrl").change(function(){
		save_url_again(this);
	});

	
	
});

	
	function save_token_again(e){ 
		var token = $(e).val().trim();
		Main.token = token;
		if (supportsLocalStorage()) {
			localStorage.setItem("com.mobion.token", token);
		}
	}
	
	function save_url_again(e) {
		var url = $(e).val().trim();
		Main.base_url = url;
		if(supportsLocalStorage()){
			localStorage.setItem("com.mobion.url", url);
		}
	}
	



function postJson(url, data, success) {
	$.ajax({
		url : url,
		type : "POST",
		data : data,
		headers : {
			"Content-Type" : "application/json",
			"Accept" : "application/json, text/plain"
		},
		dataType : "json",
		complete : success
	})
};

function supportsLocalStorage() {
	try {
		return 'localStorage' in window && window['localStorage'] !== null;
	} catch (e) {
		return false;
	}
}

function loadConfig(){
	if (supportsLocalStorage()) {
		var url = localStorage.getItem("com.mobion.url", url);
		$("#input_baseUrl").val(url);
		var token = localStorage.getItem("com.mobion.token", url);
		$("#input_apiKey").val(token);
		
		Main.base_url = url;
		Main.token = token;
	}
}


var Main = Spine.Controller.sub({
	init : function() {
		
		loadConfig();
		Main.getAPI();
		
		$('#explore').click(function(){
			Main.getAPI();
		});
	},

	elements : {
		
		"#api_tab" : "api_tab",
		"#api_tab #resources_list" : "resources_list",
	},



	
});

Main.extend({

	form2Json : function(form) {

		var o = {};
		var a = form.serializeArray();
		$.each(a, function() {
			if (o[this.name] !== undefined) {
				if (!o[this.name].push) {
					o[this.name] = [ o[this.name] ];
				}
				o[this.name].push(this.value || '');
			} else {
				o[this.name] = this.value || '';
			}
		});
		return o;
	},
	
	getAPI : function(version) {
		if(version == null){
			version= "";
		}
		var url = $("#input_baseUrl").val().trim();
		var token = $("#input_apiKey").val().trim();
		var keyword = $('#filter').val();
		$("#content_message").html("Loading...");
		$("#resources_list").slideUp();
		$("#content_message").slideDown();
		
		var urlCall = "";
		if(keyword != ""){
			urlCall = '/getapi?url=' + encodeURIComponent(url) + "&version=" + version + "&keyword=" + keyword;
		}else{
			urlCall = '/get_list_name_resource?url=' + encodeURIComponent(url) + "&version=" + version;
		}
		var controller = this;
		$('#resources_list').load(urlCall,null, function() {
					$("#content_message").slideUp();
					$("#resources_list").slideDown();
					if (supportsLocalStorage()) {
						localStorage.setItem("com.mobion.url", url);
						localStorage.setItem("com.mobion.token", token);
					}
				
				});
	}
});

var Resource = Spine.Controller.sub({

	events : {
		"click h2" : "toggleEndpoint",
		"click .show_hide" : "toggleEndpoint",
		"click .expand_ops" : "expandOperations",
		"click .list_ops" : "collapseOperations"

	},
	
	
		
	
	toggleEndpoint : function() {
		
		if($("#" + this.id + "_endpoint_list .endpoint").size() < 1){
			var id = this.id;
			$('.img_loading').css("display", "block");
			$("#" + this.id + "_endpoint_list").load("/get_resource?rest=" + this.path + "&id=" + id, null, function(){
				Docs.toggleEndpointListForResource(id);
				$('.img_loading').css("display", "none");
			});	
		}else{
			Docs.toggleEndpointListForResource(this.id)		
		}
		
		
		
	},

	collapseOperations : function() {
		Docs.collapseOperationsForResource(this.id)
	},

	expandOperations : function() {
		Docs.expandOperationsForResource(this.id)
	}
});

var Operation = Spine.Controller.sub({
	tag : "li",
	target : "",
	
	
	init : function(){
		if(this.testcase_id == ""){
			this.target = "#" + this.id;
		}else{
			this.target = "#testcase_" + this.testcase_id + " #" + this.id;
		}
		
	},
	events : {
		"click .add_expert" : "add_expert_input",
		"click h3" : "click",
		"click .sandbox_header input.submit" : "call_api",
		"click .save_operation" : "update_operation"
	},
	
	elements : {
		".expert_params" : "expert_container",
		".params_table"  : "params_table"
	},
	
	update_operation : function(e){

		var countApiConfigs = $('#update_api_2_testcase_form  .new_added_apis')
				.size();

		var idform = $(e.target).parents(".endpoint").find(".content form").attr("id");
		var json = new Object();
		$("#" + idform + " tbody tr").each(function(){
			var name = $(this).find("input.input").attr("name");
			var value = $(this).find("input.input").val();
			var needed_name = $(this).find("input[name=needed_name]").val();
			var needed_api = $(this).find("select").val();
			if(needed_name != ""){
				value += "___" + needed_name + "___" + needed_api;
			}
			json[name]= value;
		});
		var exp_params = new Object();
		$(e.target).parents(".endpoint").find("div.expert_frm tbody tr").each(function(){
			var name = $(this).find("input[name=name]").val();
			var value = $(this).find("input[name=value]").val();
			exp_params[name]= value;
			
		});

		var obj = new Object();
		obj.index = countApiConfigs;
		obj.exp_params = JSON.stringify(exp_params);
		obj.apiConfigId = this.apiConfigId;
		obj.params = JSON.stringify(json);
		obj.apiId = e.target.id.split("id_")[1];
		var id = $(e.target).parents('.resource').attr("id").split("testcase_")[1];

		
		$('#update_api_2_testcase_form .testcase_id').val(id);
		$("#apiConfigs_template").tmpl(obj).appendTo(
				"#update_api_2_testcase_form dl");
		

		var formData = form2js("update_api_2_testcase_form", '.', true);
		var json2 = JSON.stringify(formData, null, '\t');
		alert(json2);
		postJson("/add_api_to_testcase", json2, function(res) {
			$("#testcase_list #resources #testcase_" + id + "_endpoint_list")
					.html(res.responseText);
		});
		$('#update_api_2_testcase_form dl').empty();
	},

	add_expert_input : function(){
		this.expert_container.append($('#expert_param_tmpl').tmpl()).slideDown("slow");
	},
	
	click : function() {
		if(this.testcase_id == ""){
			Docs.toggleOperationContent(this.id + '_content');	
		}else{
			Docs.toggleOperationContent("testcase_" + this.testcase_id + " #" + this.id + '_content');
		}
		
	},

	call_api : function(e) {
		var form = $("#" + this.id + "_form");
		var error_free = true;
		var missing_input = null;
		var controller = this;
		this.params_table.find("tbody tr").each(function(){
			var needed_name = $(this).find("input[name=needed_name]").val();
			var needed_api = $(this).find("select").val();
			
			if(needed_name != null && needed_api != null && needed_name != "" && needed_api != ""){
				$(this).find("input.input").val("");
				var response = $(this).parents(".resource")
								.find("." + needed_api + " .response_body pre").html()
								.replace(/<br>/g,"");
				
				var jsonData = JSON.parse(response);
				var arrName = needed_name.split(".");
				var jsonKey = jsonData;
				for(var i in arrName){
					jsonKey = jsonKey[arrName[i]];
					if(jsonKey == null){
						break;
					}
				}
				if(jsonKey != null){
					$(this).find("input.input").val(jsonKey);
				}
			}
		});
		
		form.find("input.required").each(function() {

			$(this).removeClass('error');

			if ($(this).val() == '') {
				if (missing_input == null)
					missing_input = $(this);
				$(this).addClass('error');
				 $(controller.target + " .options .run_status").html("Require").css("color", "red");
				error_free = false;
			}

		});

		if (error_free) {
			var invocationUrl = this.invocationUrl(form.serializeArray(), this.http_method);
			$(".request_url", this.target + "_content_sandbox_response")
					.html("<pre>" + invocationUrl + "</pre>");

			if (this.http_method == "get") {
				$.getJSON(invocationUrl, this.proxy(this.showResponse))
						.complete(this.proxy(this.showCompleteStatus)).error(
								this.proxy(this.showErrorStatus));
			} else {
				
				var postParams = this.invocationPostParam(form.serializeArray());
				var data;
				if(this.version == "1.0"){
					data = "params=" + postParams ;
				}else{
					data = $.parseJSON(postParams);
				}
				$.post(invocationUrl , (data),
						this.proxy(this.showResponse)).complete(this.proxy(this.showCompleteStatus))
						.error(this.proxy(this.showErrorStatus));
			}

		}
		
	
		
	
	},

	showResponse : function(response, elementScope) {
		var prettyJson = JSON.stringify(response, null, "\t").replace(/\n/g,
				"<br>");
		$(".response_body",this.target + "_content_sandbox_response").html(
				prettyJson);

		$("#testcase_" + this.target + "_content_sandbox_response").slideDown();
	},

	showErrorStatus : function(data, elementScope) {
		this.showStatus(data, elementScope);
		$(this.target + "_content_sandbox_response").slideDown();
	},

	showCompleteStatus : function(data, elementScope) {
		this.showStatus(data, elementScope);
	},

	showStatus : function(data, elementScope) {
		var res = (JSON.stringify(data.responseText, null, "\t"));
		var jsonData = JSON.parse(data.responseText);
		var response_body = "<pre>"
				+ JSON.stringify(jsonData, null, 2).replace(/\n/g, "<br>")
				+ "</pre>";
		
	
		this.expert_container.find("tr").each(function(){
			
			var name = $(this).find("input[name=name]").val();
			var value = $(this).find("input[name=value]").val();
			if(value == "" && name == ""){
				$(this).removeClass();
			}else{
				var nameArr = name.split(".");
				var jsonKey = jsonData;
				var find = true;
				for(var i in nameArr){
					jsonKey = jsonKey[nameArr[i]]
					if(jsonKey == null){
						find = false;
						break;
					}
				}
				
				if(value != ""){
					find = value == jsonKey ? true : false;
				}else{
					find = typeof(jsonKey) != "undefined" ? true : false;
				}
				find == false ? $(this).addClass("not_found") : $(this).removeClass(); 
			}
	
		});
		
		if (jsonData.status == "success") {
			 $(this.target + " .options .run_status").html("Success").css("color", "blue");
			$(".response_code", this.target + "_content_sandbox_response")
					.html("<pre>" + "OK" + "</pre>");
			
		
		} else {
			$(this.target + " .options .run_status").html("Fail").css("color","red");
			$(".response_code", this.target + "_content_sandbox_response")
					.html("<pre>" + jsonData.error_code + "</pre>");
		}
		
		$(".response_body", this.target + "_content_sandbox_response").html(
				response_body);
		$(".response_headers", this.target + "_content_sandbox_response")
				.html("<pre>" + data.getAllResponseHeaders() + "</pre>");
		$(this.target + "_content_sandbox_response").slideDown();
		
		//runner
		var runner = $(this.target).parents(".resource").find(".run").html();
		
		if(runner != null && runner.length > 0){
			$(this.target).parents(".endpoint").next().find('input.submit').trigger("click");
			if($(this.target).parents(".endpoint").next().html()== null){
				$(this.target).parents(".resource").find(".run").removeClass("run");
			}
		}
		
	},

	invocationUrl : function(formValues, method) {
		var path = this.path;
		var formValuesMap = new Object();
		for ( var i = 0; i < formValues.length; i++) {
			var formValue = formValues[i];
			if (formValue.value && jQuery.trim(formValue.value).length > 0)
				formValuesMap[formValue.name] = formValue.value;
		}
		
		if(this.version == "1.0"){
		
			if(this.path.indexOf("{token}") != -1){
				if(formValuesMap["token"] == null){
					path = this.path.replace("{token}", Main.token);
				}
			}
		}
		
		var urlTemplateText = path.split("{").join("${");
//		var urlTemplateText = this.path.split("{").join("").split("}").join("");
		 
		var urlTemplate = $.template(null, urlTemplateText);
		
		var url = $.tmpl(urlTemplate, formValuesMap)[0].data;
		var queryParams = "";
		var apiKey = Main.token;
		if (apiKey) {
			apiKey = jQuery.trim(apiKey);
			if (apiKey.length > 0)
				queryParams = "?api_key=" + apiKey;
		}

		if(method=="post"){
			url = Main.base_url + url + queryParams;
			return url;
		}
		for ( var name in formValuesMap) {
			var value = formValuesMap[name];
			var valArr = new Array();
			valArr[0] = value;
			var type = this.params_table.find('input[name=' + name + "]").parents("tr").find(".type").html(); 
			if(type == "String[]" || type == "Array"){
				valArr = value.split(",");
			}
			
			for(var i in valArr){
				val = jQuery.trim(valArr[i]);
				if (val.length > 0) {
					queryParams += queryParams.length > 0 ? "&" : "?";
					
					queryParams += name;
					queryParams += "=";
					queryParams += val;
				}
			}
			
		}
		;

		url = Main.base_url + url + queryParams;
		return url;
	},

	invocationPostParam : function(formValues) {
		var formValuesMap = new Object();
		for ( var i = 0; i < formValues.length; i++) {
			var formValue = formValues[i];
			if (formValue.value && jQuery.trim(formValue.value).length > 0)
				formValuesMap[formValue.name] = formValue.value;
		}
		
		var postParam = "";
		var version = this.version;
		for(var name in formValuesMap){
			
			var value = jQuery.trim(formValuesMap[name]);
			var valArr = new Array();
			valArr[0] = value;
			var dataType = this.params_table.find('input[name=' + name + "]").parents("tr").find(".type").html(); 
			if(dataType == "String[]" ||  dataType == "Array"){
				valArr = value.split(",");
				
			}
			if(version == "1.0" && (dataType == "String[]" || dataType == "Array")){
				postParam = postParam.length > 0 ? postParam : "{";
				var listFormatParam = "\"" + name + "\":[";
				for(var i in valArr){
					var paramValue = jQuery.trim(valArr[i]);
					if(paramValue.length > 0){
						listFormatParam += "\"" + paramValue + "\",";
					}
				}
				listFormatParam = listFormatParam.substring(0, listFormatParam.length-1) + "],";
				postParam += listFormatParam;
			}else{
				for(var i  in valArr){
					
					var paramValue = jQuery.trim(valArr[i]);
					
					if (paramValue.length > 0) {
						postParam = postParam.length > 0 ? postParam : "{";
						postParam += "\"" + name + "\"";
						postParam += ":";
						postParam += "\"" + paramValue + "\",";
					}
				}
			}
		}

		if (postParam.length > 0) {
			postParam = postParam.substring(0, postParam.length - 1) + "}";
		}
		return postParam;
	},
	
	

});

// GUi Effect utils
var Docs = {

	shebang : function() {

		// If shebang has an operation nickname in it..
		// e.g. /docs/#!/words/get_search
		var fragments = $.param.fragment().split('/');
		fragments.shift(); // get rid of the bang

		switch (fragments.length) {
		case 1:
			// Expand all operations for the resource and scroll to it
			log('shebang resource:' + fragments[0]);
			var dom_id = 'resource_' + fragments[0];

			Docs.expandEndpointListForResource(fragments[0]);
			$("#" + dom_id).slideto({
				highlight : false
			});
			break;
		case 2:
			// Refer to the endpoint DOM element, e.g. #words_get_search
			log('shebang endpoint: ' + fragments.join('_'));

			// Expand Resource
			Docs.expandEndpointListForResource(fragments[0]);
			$("#" + dom_id).slideto({
				highlight : false
			});

			// Expand operation
			var li_dom_id = fragments.join('_');
			var li_content_dom_id = li_dom_id + "_content";

			log("li_dom_id " + li_dom_id);
			log("li_content_dom_id " + li_content_dom_id);

			Docs.expandOperation($('#' + li_content_dom_id));
			$('#' + li_dom_id).slideto({
				highlight : false
			});
			break;
		}

	},

	toggleEndpointListForResource : function(resource) {
		var elem = $('li#resource_' + resource + ' ul.endpoints');
		if (elem.is(':visible')) {
			Docs.collapseEndpointListForResource(resource);
		} else {
			Docs.expandEndpointListForResource(resource);
		}
	},

	// Expand resource
	expandEndpointListForResource : function(resource) {
		$('#resource_' + resource).addClass('active');

		var elem = $('li#resource_' + resource + ' ul.endpoints');
		elem.slideDown();
	},

	// Collapse resource and mark as explicitly closed
	collapseEndpointListForResource : function(resource) {
		$('#resource_' + resource).removeClass('active');

		var elem = $('li#resource_' + resource + ' ul.endpoints');
		elem.slideUp();
	},

	expandOperationsForResource : function(resource) {
		// Make sure the resource container is open..
		Docs.expandEndpointListForResource(resource);
		$('li#resource_' + resource + ' li.operation div.content').each(
				function() {
					Docs.expandOperation($(this));
				});
	},

	collapseOperationsForResource : function(resource) {
		// Make sure the resource container is open..
		Docs.expandEndpointListForResource(resource);
		$('li#resource_' + resource + ' li.operation div.content').each(
				function() {
					Docs.collapseOperation($(this));
				});
	},

	expandOperation : function(elem) {
		elem.slideDown();
	},

	collapseOperation : function(elem) {
		elem.slideUp();
	},

	toggleOperationContent : function(dom_id) {
		var elem = $('#' + dom_id);
		(elem.is(':visible')) ? Docs.collapseOperation(elem) : Docs
				.expandOperation(elem);
	}

};
