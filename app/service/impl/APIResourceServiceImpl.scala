package service.impl

import service.APIResourceService
import service.AbstractService
import util.StringUtil
import models.APIResource
import sjson.json.Serializer.SJSON
import models.APISpec
import models.APIOperation
import models.APIParameter
import models.BaseKey
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
class APIResourceServiceImpl extends APIResourceService with AbstractService {

  def getAPIResources(start: Int, end: Int, path: String,currentVersion:String): List[APIResource] = {
    val id = StringUtil.basePath + path
    var resources = List[APIResource]()
    var lastestVersion =  currentVersion
    if(StringUtil.isBlank(currentVersion)){
      val listVersion = apiVersionTrackingDAO.findAndOrder(StringUtil.Order.DESC, 0, StringUtil.MAXINT)
      print(listVersion)
      if (listVersion == null) {
        resources
      }
      lastestVersion = listVersion(0).id
    }

    val result = apiResourceDAO.findbyProperty("_id.version",lastestVersion)
    if (result != null) {
      result.foreach(item => {
        var resource = getAPIResource(item,"") 
        if (resource != null) {
          resources ::= resource
        }
      })
    }
    return resources
  }

  private def getAPIResource(apiResource: APIResource,keyword: String):APIResource = {
    var listSpec = List[APISpec]()
    if (apiResource == null || apiResource.specIds.isEmpty) {
      return null
    }
    apiResource.specIds.foreach(specId => {
      val spec = apiSpecDAO.findById(specId)
      var listOpertation = List[APIOperation]()
      if (spec == null || spec.operationsId.isEmpty) {
        null
      }
      spec.operationsId.foreach(operationId => {
        val operation = apiOperationDAO.findById(operationId)
        if (operation == null || operation.apiParameterIds.isEmpty) {
          null
        }
        if(StringUtil.isBlank(keyword) || (StringUtil.isNotBlank(operation.nickname) && operation.nickname.contains(keyword))){
          var listParameter = List[APIParameter]()
          operation.apiParameterIds.foreach(parameterId => {
            val parameter = apiParameterDAO.findById(parameterId)
            listParameter ::= parameter
          })
          operation.parameters = listParameter
          listOpertation ::= operation
        }        
      })
      spec.operations = listOpertation
      if(!spec.operations.isEmpty){
          listSpec ::= spec
      }
    })
    apiResource.apis = listSpec
    return apiResource
  }
  
  private def getSpecFromOperationId(operationId:String,version:String):APISpec={
    val specPath = operationId.split("__")(0)
    var searchKey = new BaseKey
    searchKey.path = specPath
    searchKey.version = version
    val spec = apiSpecDAO.findById(searchKey)
    return spec
  }

  def getAPIResource(id: String,keyword: String, version:String): APIResource = {
    var lastestVersion = version
    if (StringUtil.isBlank(lastestVersion)) {
      val listVersion = apiVersionTrackingDAO.findAndOrder(StringUtil.Order.DESC, 0, StringUtil.MAXINT)
      if (listVersion == null) {
        null
      }
      lastestVersion = listVersion(0).id
    }
    val key = new BaseKey(id, lastestVersion)
    val apiRes = apiResourceDAO.findById(key)
    if (apiRes != null) {
      return getAPIResource(apiRes, keyword)
    } else {
      return null
    }
  }
  
    
  def getParameterList(operation : APIOperation) = {
     var listParameter = List[APIParameter]()
          operation.apiParameterIds.foreach(parameterId => {
            val parameter = apiParameterDAO.findById(parameterId)
            listParameter ::= parameter
          })
     listParameter
  }
  
  def getListOperation(spec: APISpec) = {
    var listOpertation = List[APIOperation]()
    if (spec == null || spec.operationsId.isEmpty) {
      null
    }
    spec.operationsId.foreach(operationId => {
      val operation = apiOperationDAO.findById(operationId)
      if (operation == null || operation.apiParameterIds.isEmpty) {
        null
      }
      var listParameter = List[APIParameter]()
      operation.parameters = getParameterList(operation)
      listOpertation ::= operation
    })
    listOpertation
  }
  
  def getListSpec(apiResource: APIResource) = {
    var listSpec = List[APISpec]()
    if (apiResource == null || apiResource.specIds.isEmpty) {
      null
    }
    apiResource.specIds.foreach(specId => {
      val spec = apiSpecDAO.findById(specId)
      var listOpertation = List[APIOperation]()
      if (spec == null || spec.operationsId.isEmpty) {
        null
      }
      listOpertation = getListOperation(spec)
      spec.operations = listOpertation
      if (!spec.operations.isEmpty) {
        listSpec ::= spec
      }
    })
    listSpec
  }
  
  def getNameAPIResources(start: Int, end: Int, path: String,currentVersion:String): List[String] = {
    val id = StringUtil.basePath + path
    var resources = List[String]()
    var lastestVersion =  currentVersion
    if(StringUtil.isBlank(currentVersion)){
      val listVersion = apiVersionTrackingDAO.findAndOrder(StringUtil.Order.DESC, 0, StringUtil.MAXINT)
      print(listVersion)
      if (listVersion == null) {
        resources
      }
      lastestVersion = listVersion(0).id
    }

    val result = apiResourceDAO.findbyProperty("_id.version",lastestVersion)
    if (result != null) {
      result.foreach(item => {
        var resource = getAPIResource(item,"") 
        if (resource != null) {
          resources ::= resource.resourcePath
        }
      })
    }
    return resources
  }
  
  def searchOperation(version:String,keyword:String):List[APIOperation] = {
    var lastestVersion = version
    if(StringUtil.isBlank(lastestVersion)){
      val listVersion = apiVersionTrackingDAO.findAndOrder(StringUtil.Order.DESC, 0, StringUtil.MAXINT)
      if (listVersion == null) {
        null
      }
      lastestVersion = listVersion(0).id
    }
    return apiOperationDAO.searchByKeyword(lastestVersion,keyword)
  }
  
  def getAPIResourceByKeyword(keyword:String,version:String):List[APIResource] = {
    if (StringUtil.isNotBlank(keyword)) {
      val listResource = new ListBuffer[APIResource]()
      val listResourceId = new ListBuffer[String]()
      var mapSpec = new HashMap[String, APISpec]()
      val operationList = apiOperationDAO.searchByKeyword(version, keyword)
      if (operationList != null) {
        var apis = List[APISpec]()
        operationList.foreach(operation => {
          val specId = operation.id.split("__")(0)
          if (!mapSpec.isEmpty && mapSpec.keySet.contains(specId)) {
            var spec = mapSpec.get(specId).get
            spec.operations ::= operation
          } else {
            var spec = getSpecFromOperationId(operation.id, version)
            if (spec != null) {
              if (listResourceId.contains(spec.resPath)) {
                for (i <- 0 to listResourceId.length -1) {
                  if (listResourceId(i).equals(spec.resPath)) {
                    listResource(i).apis ::= spec
                  }
                }
              } else {
                var searchKey = new BaseKey
                searchKey.path = spec.resPath
                searchKey.version = version
                val resource = apiResourceDAO.findById(searchKey)
                if (resource != null) {
                  spec.operations ::= operation
                  resource.apis ::= spec
                  mapSpec.put(resource.resourcePath, spec)
                  listResource += resource
                  listResourceId += resource.id.path
                }
              }
            }
          }
        })
      }
      return listResource.toList
    }else{
      return null
    }
  }

}
