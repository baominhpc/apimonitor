package controllers

import dispatch.json.Js
import models.APIResource
import models.Res
import models.ResList
import play.api.mvc.Action
import sjson.json.Serializer.SJSON
import util.APIRequestUtils
import models.User
import play.mvc.Http.Response
import play.api.mvc.Cookie
import play.api.mvc.Cookie
import play.api.mvc.Cookie
import play.mvc.Http




object Application extends AbstractController {

  
  
  def login() = Action(parse.urlFormEncoded){ request =>

    val password = request.body.get("password").head.head
    val username = request.body.get("username").head.head
    val user = userService.login(username, password)
    if(user != null){
      Redirect(routes.TestCaseApplication.index())
      	.withCookies(new Cookie("token", user.userId))
    }else{
      
    	Ok(views.html.login_index(false))  
    }
    
  }
  
  def logout = Action  {request =>
    
    Redirect(routes.Application.login()).discardingCookies("token")
  }
  
  def register  = Action(parse.urlFormEncoded){ request =>
    val password = request.body.get("password").head.head
    val username = request.body.get("username").head.head
    var user = new User()
    user.setUsername(username)
    user.setPassword(password)
    val status = userService.createAccount(user)
    if(status){
	    Redirect(routes.TestCaseApplication.index()).withCookies(new Cookie("token", user.userId))  
    }else{
    	Ok(views.html.register_index(status))  
    }
    
    
  }
  
  
  def login_page = Action{ request =>
    if(request.cookies.get("token") != None){
      Redirect(routes.TestCaseApplication.index())
    }else{
    	Ok(views.html.login_index())  
    }
    
  }
  
  def register_page = Action{request =>
      if(request.cookies.get("token") != None){
      Redirect(routes.TestCaseApplication.index())
    }else{
    	Ok(views.html.register_index())  
    }
  }
  
  def index = Action { 
    Ok(views.html.index("api"))
    
  }
  
/*
   def getapi(url: String) = Action {
    var newUrl = url

    if (!newUrl.startsWith("http://")) {
      newUrl += "http://"
    }

    println(newUrl)

    val res = APIRequestUtils.getWS(newUrl + "/resources.json", Map())
    val apis: List[Res] = SJSON.in[ResList](Js(res)).apis

    var list = List[APIResource]()
    apis.foreach(api => {
      val path = api.path
      val res2 = APIRequestUtils.getWS(newUrl + path + "/list_api?api_key=a3633f30bb4a11e18887005056a70023", Map())

      list ::= SJSON.in[APIResource](Js(res2))
    })

    
    Ok(views.html.resources_list(list))
  }
*/
  def getListAPI = Action {

    filterResponse(Ok(myService.getListApi).as("text/plain"))
  }

  def getListAPIInRest(rest: String) = Action {
    filterResponse(Ok(myService.getListAPIInRes(rest)).as("text/plain"))
  }

  def getListTestCaseDetail(id: String) = Action { request =>
    var value = myService.getListTestCaseDetail(id)
    filterResponse(Ok(value))
  }
  
}
