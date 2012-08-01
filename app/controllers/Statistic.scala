package controllers

import dispatch.json.Js
import util.HiveUtils
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
import views.html.statistic_index$
import java.util.Calendar




object Statistic extends AbstractController {

  def index(date : String) = Action{
    val time = Calendar.getInstance().getTime().toString()
    val list = HiveUtils.getapiReport(date)
    println("SIZE============="+list.size())
    Ok(views.html.statistic_index(list))
  }
  
  
  def chart= Action{
    Ok(views.html.chart())
  }
}
