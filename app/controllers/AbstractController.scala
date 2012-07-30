package controllers

import play.api.modules.spring.Spring
import play.api.mvc.Action
import play.api.mvc.Controller
import service.MyService
import service.TestCaseService
import play.api.mvc.Result
import play.api.mvc.PlainResult
import play.api.mvc.SimpleResult
import service.ParameterService
import service.APIConfigService
import service.APIResourceService
import service.VersionTrackingService
import util.ConfigUtils
import service.UserService

class AbstractController extends Controller {
  val userService = Spring.getBeanOfType(classOf[UserService])
  val myService = Spring.getBeanOfType(classOf[MyService])
  val testCaseService = Spring.getBeanOfType(classOf[TestCaseService])
  val parameterService = Spring.getBeanOfType(classOf[ParameterService])
  val apiConfigService = Spring.getBeanOfType(classOf[APIConfigService])
  val apiResourceService = Spring.getBeanOfType(classOf[APIResourceService])
  val versionTrackingService = Spring.getBeanOfType(classOf[VersionTrackingService])
  
  ConfigUtils.CURRENT_VERSION = versionTrackingService.getLastedVersion()
  
  def filterResponse[T](ret: SimpleResult[T]): Result = {
    ret.withHeaders("Access-Control-Allow-Origin" -> "*")
  }
  
  def filterResponse(ret: PlainResult): Result = {
    ret.withHeaders("Access-Control-Allow-Origin" -> "*")
  }
}
