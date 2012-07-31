package service
import models.User

trait UserService {
	def createAccount(user : User): Boolean
	
	def login(user : String, pwd : String): User
	
	def deleteAccount(id : String) 
}
