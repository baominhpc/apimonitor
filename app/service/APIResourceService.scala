package service
import models.APIResource
import models.APIOperation
trait APIResourceService {	
  def getAPIResource(id : String,keyword: String, version:String): APIResource
  def getAPIResources(start: Int, end: Int, path: String,currentVersion:String): List[APIResource]
  def getNameAPIResources(start: Int, end: Int, path: String,currentVersion:String): List[String]
  def searchOperation(version:String,keyword:String):List[APIOperation] 
  def getAPIResourceByKeyword(keyword:String,version:String):List[APIResource]
}