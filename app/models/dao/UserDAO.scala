package models.dao
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import models.User
class UserDAO extends MongoSalatDAO[User,String](User.getTableName){
  
  def getUser(username :String, password : String){
    
  }
}