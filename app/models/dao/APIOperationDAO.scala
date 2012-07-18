package models.dao
import util.ConfigUtils
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import play.api.libs.json.JsObject
import models.APIOperation
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.commons.Imports.DBObject
import com.mongodb.casbah.commons.MongoDBList
import models.BaseKey


class APIOperationDAO extends MongoSalatDAO[APIOperation, String](APIOperation.getTableName) {
  override def findById(id: String) = {
	 var key = new BaseKey(id,ConfigUtils.CURRENT_VERSION)
	 this.findById(key)
  }
  
  def searchByKeyword(version:String,keyword:String) = {
    val regex = ".*" + keyword + ".*"
    val operationQuery = MongoDBObject("_id.version" -> version,"_id.path" -> regex.r)
    val parameterQuery = MongoDBObject("_id.version" -> version,"apiParameterIds.path" -> regex.r)
    var query = List[DBObject]()
    query ::= operationQuery
    query ::= parameterQuery
    val result = findByMultiCondition("$or",query)
    result.toList
  }
}
