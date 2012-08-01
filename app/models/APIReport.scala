package models
import scala.reflect.BeanInfo
import sjson.json.JSONTypeHint
import scala.annotation.target.field
import models.testcase.BaseBean
import com.novus.salat.annotations._

@BeanInfo case class APIReport(
  @Key("_id") var id:BaseKey,
  hit : String,
  totalTime : String,
  avg : String, 
  lastHit : String, 
  avgRate : String,
  currentReate : String){
  
 
  def this() = {
    this(null, "","", "", "", "", "")
  }
}

object APIReport {
  def getTableName = "APIReport"
}