package models
import util.StringUtil
import scala.reflect.BeanInfo
import models.testcase.BaseBean
import com.novus.salat.annotations._


@BeanInfo case class User(
  @Key("_id") var userId: String,
  var username : String,
  var password : String
  ) extends BaseBean(userId) {
  
  def this()={
    this(StringUtil.generateStringTimeStamp(),"","")
  }
  
  def setUsername(username : String){
    this.username = username
  }
  
  def setPassword(password : String){
    this.password = password
  }
  
//  def this(username : String, pwd : String)= {
//    this(StringUtil.generateStringTimeStamp(),username, pwd)
//    
//  }
}

object User {
  def getTableName = "User"
}