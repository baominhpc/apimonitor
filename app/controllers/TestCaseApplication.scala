package controllers

import scala.reflect.BeanInfo
import dispatch.json.Js
import models.testcase.TestCase
import models.APIResource
import models.Res
import models.ResList
import play.api.libs.json.Json
import play.api.mvc.Action
import sjson.json.Serializer.SJSON
import util.APIRequestUtils
import util.ConfigUtils
import util.StringUtil
import play.mvc.Http
import play.api.mvc.Response

object TestCaseApplication extends AbstractController {

  def index = Action {request => 
    
    
    
   
    if(request.cookies.get("token") != None){
      val token = request.cookies.get("token").get.value
       Ok(views.html.index("testcase", token))
    }else{
    	Redirect(routes.Application.login_page())  
    }
    
    
    
  }
  
  
  //return list test case with id and name
  def getTestcases(start: Int, size: Int, userId : String) = Action {
    var list = testCaseService.getTestCasesByUserId(userId)
     
    Ok(views.html.testcase_list(list))
  }


  def getAPIsinTestCase(id: String) = Action { request =>
    Ok(views.html.apis_list_in_testcase("testcase_" + id, id, testCaseService.getAPIsinTestCase(id)))
  }

  def addTestCase = Action(parse.json) { request =>
    var testCase = SJSON.in[TestCase](Js(request.body.toString()))
    println(testCase)
    testCase = testCaseService.addTestCase(testCase)

    Ok(views.html.testcase(testCase.id, testCase.name))

  }

  def addAPI2TestCase = Action(parse.json) { request =>

    var testCase = SJSON.in[TestCase](Js(request.body.toString()))
    testCaseService.addAPI2TestCase(testCase)

    Ok(views.html.apis_list_in_testcase("testcase_" + testCase.id, testCase.id,   testCaseService.getAPIsinTestCase(testCase.id)))

  }
  
   def removeAPIfromTestCase = Action(parse.json) { request =>

    println("remove from test case : " + request.body.toString())
    var testCase = SJSON.in[TestCase](Js(request.body.toString()))

    testCaseService.removeAPIfromTestCase(testCase.id, testCase.apiConfigIds)
    
    Ok("OK")
  }


  def deleteTestCase(id: String) = Action {
    testCaseService.deleteTestCase(id);
    Ok("OK");

  }

/*
  def removeFunctionInTestCase = Action(parse.text) { request =>
    var testCase = SJSON.in[TestCase](Js(request.body.toString()))
    testCaseService.removeFunctionInTestCase(testCase)
    filterResponse(Ok(Json.toJson(
      Map("status" -> "success"))))
  }
  
  def addFunctionInTestCase = Action(parse.text) { request =>
    var testCase = SJSON.in[TestCase](Js(request.body.toString()))
    testCaseService.addFunctionInTestCase(testCase)
    filterResponse(Ok(Json.toJson(
      Map("status" -> "success"))))
  }
  
  def editFunctionInTestCase = Action(parse.text) { request =>
    var testCase = SJSON.in[TestCase](Js(request.body.toString()))
    testCaseService.editFunctionInTestCase(testCase)
    filterResponse(Ok(Json.toJson(
      Map("status" -> "success"))))
  }
*/
    
  def buildAPIDatabase = Action {
    versionTrackingService.buildAPIVersion()
    filterResponse(Ok(Json.toJson(
      Map("status" -> "success"))))
  }
  
  def getListMobionTestCase(start: String, size: String) = Action { request =>
    var value = testCaseService.getListMobionTestCase(start, size)
    filterResponse(Ok("OK"))
  }
}

