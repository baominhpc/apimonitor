package service.impl
import service.UserService
import service.AbstractService
import models.User
import com.mongodb.WriteConcern

class UserServiceImpl extends UserService with AbstractService {
  
  def createAccount(user : User) : Boolean ={
    if(checkExistAccount(user))
      return false;
    userDAO.save(user);
    return true;
  }
  
  
  def checkExistAccount(user : User) : Boolean ={
    val res = userDAO.findbyProperty("username", user.username)
    if(res.size == 0) return false;
    return true;
  }
  
  def deleteAccount(id : String){
    userDAO.removeById(id, WriteConcern.SAFE)
  }
  
  def login(username : String, pwd : String): User = {
    val res = userDAO.findbyProperty("username", username)
    res.foreach(item => {
      if(pwd.equals(item.password)){
        return item
      }
    })
    return null
  }
  
  
}