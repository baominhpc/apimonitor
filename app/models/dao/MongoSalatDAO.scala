package models.dao
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.FluidQueryBarewordOps
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import com.novus.salat.Context
import models.BaseKey
import com.mongodb.casbah.WriteConcern
import com.mongodb.casbah.commons.MongoDBList


class MongoSalatDAO[ObjectType >: Null <: AnyRef, ID <: AnyRef](collectionName: String)(implicit mot: Manifest[ObjectType], mid: Manifest[ID], ctx: Context) extends 
	SalatDAO[ObjectType, ID](collection = MongoConnection("mongo01b",27017)("mobion")(collectionName))(mot, mid, ctx) with FluidQueryBarewordOps{
  collection.getDB().authenticate("mobion","mobion!life")
  
  def findAll() = {
    val result = find(MongoDBObject())
    if (result == None) {
      null
    }else{
      result.toList      
    }
  }
  
  def findbyProperty(key : String, value : String) = {
	  find(MongoDBObject(key -> value)).toList;
  }
  
  def findbyProperty(key : String, id:BaseKey) = {
    val tmp = MongoDBObject(key + ".path" -> id.path , key + ".version" -> id.version)
	find(tmp).toList
  }
  
  def findById(id:String) : ObjectType = {
    val result = findOne(MongoDBObject("_id" -> id))
    if(result == None){
      return null
    }else{
      return result.get
    }
    
  }
  
  def deleteById(id :ID){
    removeById(id, WriteConcern.Safe)
  }
  def findLimit(start:Int,size:Int) : List[ObjectType] = {
    val c = find(MongoDBObject()).skip(start).limit(size)
    if (c == None) {
      return null
    }
    return c.toList
  }
  
  def pushToField(id:String,field:String,value:String){
    val push = $push(field -> value)
    update(MongoDBObject("_id" -> id),push)
  }
  
  def pullFromField(id:String,field:String,value:String){
    val pull = $pull(field -> value)
    update(MongoDBObject("_id" -> id),pull)
  }
  
  def findById(id:BaseKey):ObjectType = {
	val result = findOne(MongoDBObject("_id.path" -> id.path , "_id.version" -> id.version))
    if(result == None){
      return null
    }else{
      return result.get
    }	
  }
  
  def findAndOrder(order:Int,start:Int,size:Int) = {
    val result = find(MongoDBObject()).sort(orderBy = MongoDBObject("_id" -> -1)).skip(start).limit(size)
    if (result == None) {
      null
    }
    result.toList
  }
  
  def findById(id:String,start:Int,size:Int):List[ObjectType] = {
    var result = find(MongoDBObject("_id" -> id)).skip(start).limit(size)
    if(result == None){
      return null
    }
    return result.toList
  }
  
  def countLike(field:String, value:String) = {
    var regex = ".*" + value + ".*"
    count(MongoDBObject(field -> regex.r))
  }
  
  def findLike(field:String, value:String):List[ObjectType] = {
    var regex = ".*" + value + ".*"
    var result = find(MongoDBObject(field -> regex.r))
    if(result == None){
      return null
    }else{
      return result.toList      
    }
  }
  
  def findByMultiCondition(connector:String, conditions:List[MongoDBObject] = List[MongoDBObject]()){
    val builder = MongoDBList.newBuilder
    conditions.foreach(condition =>{
      builder += condition
    }
    )
    val query = MongoDBObject("$or" -> builder)
    val result = find(query)
  }
  
  def removeByCondition(field:String, value:String){
    this.remove(MongoDBObject(field->value))
  }
}


