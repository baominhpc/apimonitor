package models.dao
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.query.FluidQueryBarewordOps
import com.mongodb.casbah.MongoConnection
import com.novus.salat.dao.SalatDAO
import com.novus.salat.Context

class MongoSalatDAO[ObjectType <: AnyRef, ID <: AnyRef](collectionName: String)(implicit mot: Manifest[ObjectType], mid: Manifest[ID], ctx: Context) extends 
	SalatDAO[ObjectType, ID](collection = MongoConnection("mongo01b",27017)("mobion")(collectionName))(mot, mid, ctx) with FluidQueryBarewordOps{
  collection.getDB().authenticate("mobion","mobion!life")
  
  def findAll() = {
	 find(MongoDBObject())
  }
  
  def findbyProperty(key : String, value : AnyRef) = {
    var c = find(MongoDBObject(key -> value));
    c.toList
  }
  def findById(id:String) = {
    var result = findOne(MongoDBObject("_id" -> id))
    if(result == None){
      null
    }
    result.get
  }
  def findLimit(start:Int,size:Int) : List[ObjectType] = {
    var c = find(MongoDBObject()).skip(start).limit(size)
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
}


