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
import views.html.statistic_index$




object Statistic extends AbstractController {

  def index = Action{
    apiReportService.getListAPIReport()
    Ok(views.html.statistic_index())
  }
  
  
  def chart= Action{
    Ok(views.html.chart())
  }
}
